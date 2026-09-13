package base3D.shader;

import base3D.ENV;
import base3D.project.scene.Camera3D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class Magic {

    public interface SceneDrawer {
        void render(PGraphics g);
    }

    //import peasy.*;

//https://forum.processing.org/two/discussion/12775/simple-shadow-mapping#latest

    final PVector lightDir = new PVector();
    PShader defaultShader;
    PGraphics shadowMap;


    public void draw(SceneDrawer sceneDrawer, Camera3D camera3D) {

        // Calculate the light direction (actually scaled by negative distance)
        float lightAngle = ENV.p.frameCount * 0.002f;
        lightDir.set(
                PApplet.sin(lightAngle) * 200 + camera3D.camPos.x,
                 +camera3D.camPos.y,
                PApplet.cos(lightAngle) * 200 + camera3D.camPos.z);

        // Render shadow pass
        shadowMap.beginDraw();
        shadowMap.camera(lightDir.x, lightDir.y, lightDir.z, 0, 0, 0, 0, 1, 0);
        shadowMap.background(0xffffffff); // Will set the depth to 1.0 (maximum depth)
        sceneDrawer.render(shadowMap);
        shadowMap.endDraw();
        shadowMap.updatePixels();

        // Update the shadow transformation matrix and send it, the light
        // direction normal and the shadow map to the default shader.
        updateDefaultShader();

        // Render default pass
        //ENV.p.background(0xff222222);
        sceneDrawer.render(ENV.p.g);

        // Render light source
        ENV.p.pushMatrix();
            ENV.p.fill(0xffffffff);
            ENV.p.translate(lightDir.x, lightDir.y, lightDir.z);
            ENV.p.box(5);
        ENV.p.popMatrix();

    }

    public void initShadowPass() {
        shadowMap = ENV.p.createGraphics(2048, 2048, ENV.p.P3D);
        String[] vertSource = {
                "uniform mat4 transform;",

                "attribute vec4 vertex;",

                "void main() {",
                "gl_Position = transform * vertex;",
                "}"
        };
        String[] fragSource = {

                // In the default shader we won't be able to access the shadowMap's depth anymore,
                // just the color, so this function will pack the 16bit depth float into the first
                // two 8bit channels of the rgba vector.
                "vec4 packDepth(float depth) {",
                "float depthFrac = fract(depth * 255.0);",
                "return vec4(depth - depthFrac / 255.0, depthFrac, 1.0, 1.0);",
                "}",

                "void main(void) {",
                "gl_FragColor = packDepth(gl_FragCoord.z);",
                "}"
        };
        shadowMap.noSmooth(); // Antialiasing on the shadowMap leads to weird artifacts
        //shadowMap.loadPixels(); // Will interfere with noSmooth() (probably a bug in Processing)
        shadowMap.beginDraw();
            shadowMap.noStroke();
            shadowMap.shader(new PShader(ENV.p, vertSource, fragSource));
            shadowMap.ortho(-200, 200, -200, 200, 10, 1000); // Setup orthogonal view matrix for the directional light
        shadowMap.endDraw();
    }

    public void initDefaultPass() {
        String[] vertSource = {
                "uniform mat4 transform;",
                "uniform mat4 modelview;",
                "uniform mat3 normalMatrix;",
                "uniform mat4 shadowTransform;",
                "uniform vec3 lightDirection;",

                "attribute vec4 vertex;",
                "attribute vec4 color;",
                "attribute vec3 normal;",

                "varying vec4 vertColor;",
                "varying vec4 shadowCoord;",
                "varying float lightIntensity;",

                "void main() {",
                    "vertColor = color;",
                    "vec4 vertPosition = modelview * vertex;", // Get vertex position in model view space
                    "vec3 vertNormal = normalize(normalMatrix * normal);", // Get normal direction in model view space
                    "shadowCoord = shadowTransform * (vertPosition + vec4(vertNormal, 0.0));", // Normal bias removes the shadow acne
                    "lightIntensity = 0.5 + dot(-lightDirection, vertNormal) * 0.5;",
                    "gl_Position = transform * vertex;",
                "}"
        };
        String[] fragSource = {
                "#version 120",

                // Used a bigger poisson disk kernel than in the tutorial to get smoother results
                "const vec2 poissonDisk[9] = vec2[] (",
                "vec2(0.95581, -0.18159), vec2(0.50147, -0.35807), vec2(0.69607, 0.35559),",
                "vec2(-0.0036825, -0.59150), vec2(0.15930, 0.089750), vec2(-0.65031, 0.058189),",
                "vec2(0.11915, 0.78449), vec2(-0.34296, 0.51575), vec2(-0.60380, -0.41527)",
                ");",

                // Unpack the 16bit depth float from the first two 8bit channels of the rgba vector
                "float unpackDepth(vec4 color) {",
                "return color.r + color.g / 255.0;",
                "}",

                "uniform sampler2D shadowMap;",

                "varying vec4 vertColor;",
                "varying vec4 shadowCoord;",
                "varying float lightIntensity;",

                "void main(void) {",

                // Project shadow coords, needed for a perspective light matrix (spotlight)
                "vec3 shadowCoordProj = shadowCoord.xyz / shadowCoord.w;",

                // Only render shadow if fragment is facing the light
                "if(lightIntensity > 0.5) {",
                    "float visibility = 9.0;",

                    // I used step() instead of branching, should be much faster this way
                    "for(int n = 0; n < 9; ++n)",
                    "visibility += step(shadowCoordProj.z, unpackDepth(texture2D(shadowMap, shadowCoordProj.xy + poissonDisk[n] / 512.0)));",
                    "gl_FragColor = vec4(vertColor.rgb * min(visibility * 0.05556, lightIntensity), vertColor.a);",
                "} else",
                    "gl_FragColor = vec4(vertColor.rgb * lightIntensity, vertColor.a);",
                "}"
        };
        ENV.p.shader(defaultShader = new PShader(ENV.p, vertSource, fragSource));
        ENV.p.noStroke();
        ENV.p.perspective(60 * ENV.p.DEG_TO_RAD, (float)ENV.p.width / ENV.p.height, 10, 1000);
    }

    void updateDefaultShader() {

        // Bias matrix to move homogeneous shadowCoords into the UV texture space
        PMatrix3D shadowTransform = new PMatrix3D(
                0.5f, 0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.0f, 0.5f,
                0.0f, 0.0f, 0.5f, 0.5f,
                0.0f, 0.0f, 0.0f, 1.0f
        );

        // Apply project modelview matrix from the shadow pass (light direction)
        shadowTransform.apply(((PGraphicsOpenGL)shadowMap).projmodelview);

        // Apply the inverted modelview matrix from the default pass to get the original vertex
        // positions inside the shader. This is needed because Processing is pre-multiplying
        // the vertices by the modelview matrix (for better performance).
        PMatrix3D modelviewInv = ((PGraphicsOpenGL)ENV.p.g).modelviewInv;
        shadowTransform.apply(modelviewInv);

        // Convert column-minor PMatrix to column-major GLMatrix and send it to the shader.
        // PShader.set(String, PMatrix3D) doesn't convert the matrix for some reason.
        defaultShader.set("shadowTransform", new PMatrix3D(
                shadowTransform.m00, shadowTransform.m10, shadowTransform.m20, shadowTransform.m30,
                shadowTransform.m01, shadowTransform.m11, shadowTransform.m21, shadowTransform.m31,
                shadowTransform.m02, shadowTransform.m12, shadowTransform.m22, shadowTransform.m32,
                shadowTransform.m03, shadowTransform.m13, shadowTransform.m23, shadowTransform.m33
        ));

        // Calculate light direction normal, which is the transpose of the inverse of the
        // modelview matrix and send it to the default shader.
        float lightNormalX = lightDir.x * modelviewInv.m00 + lightDir.y * modelviewInv.m10 + lightDir.z * modelviewInv.m20;
        float lightNormalY = lightDir.x * modelviewInv.m01 + lightDir.y * modelviewInv.m11 + lightDir.z * modelviewInv.m21;
        float lightNormalZ = lightDir.x * modelviewInv.m02 + lightDir.y * modelviewInv.m12 + lightDir.z * modelviewInv.m22;
        float normalLength = PApplet.sqrt(lightNormalX * lightNormalX + lightNormalY * lightNormalY + lightNormalZ * lightNormalZ);
        defaultShader.set("lightDirection", lightNormalX / -normalLength, lightNormalY / -normalLength, lightNormalZ / -normalLength);

        // Send the shadowmap to the default shader
        defaultShader.set("shadowMap", shadowMap);

    }

    void keyPressed() {
        if(ENV.p.key != ENV.p.CODED) {
            if(ENV.p.key == 'd') {
                shadowMap.beginDraw(); shadowMap.ortho(-200, 200, -200, 200, 10, 400); shadowMap.endDraw();
            } else if(ENV.p.key == 's') {
                shadowMap.beginDraw(); shadowMap.perspective(60 * ENV.p.DEG_TO_RAD, 1, 10, 1000); shadowMap.endDraw();
            }
        }
    }

    public void renderLandscape(PGraphics canvas, int landscape) {
        switch (landscape) {
            case 1 -> {
                float offset = -ENV.p.frameCount * 0.01f;
                canvas.fill(0xffff5500);
                for (int z = -5; z < 6; ++z)
                    for (int x = -5; x < 6; ++x) {
                        canvas.pushMatrix();
                        canvas.translate(x * 12, PApplet.sin(offset + x) * 20 + PApplet.cos(offset + z) * 20, z * 12);
                        canvas.box(10, 100, 10);
                        canvas.popMatrix();
                    }
            }
            case 2 -> {
                float angle = -ENV.p.frameCount * 0.0015f, rotation = ENV.p.TWO_PI / 20;
                canvas.fill(0xffff5500);
                for (int n = 0; n < 20; ++n, angle += rotation) {
                    canvas.pushMatrix();
                    canvas.translate(PApplet.sin(angle) * 70, PApplet.cos(angle * 4) * 10, PApplet.cos(angle) * 70);
                    canvas.box(10, 100, 10);
                    canvas.popMatrix();
                }
                canvas.fill(0xff0055ff);
                canvas.sphere(50);
            }
            case 3 -> {
                float angle = -ENV.p.frameCount * 0.0015f, rotation = ENV.p.TWO_PI / 20;
                canvas.fill(0xffff5500);
                for (int n = 0; n < 20; ++n, angle += rotation) {
                    canvas.pushMatrix();
                    canvas.translate(PApplet.sin(angle) * 70, PApplet.cos(angle) * 70, 0);
                    canvas.box(10, 10, 100);
                    canvas.popMatrix();
                }
                canvas.fill(0xff00ff55);
                canvas.sphere(50);
            }
        }
        canvas.fill(0xff222222);
        canvas.box(360, 5, 360);
    }


    //Constructor: :
    /*
    magic = new Magic();
    magic.initShadowPass();
    magic.initDefaultPass();
     */


    //Render ::
    /*
        //setCanvasAndEnvironment();
        cam.updateCamera();
        ENV.p.background(100,100,255);
        displayScene();
        magic.draw((PGraphics pg) -> {
            pg.pushMatrix();
                pg.translate(0,-cam.camPos.y,0);
                pg.noStroke();
                magic.renderLandscape(pg, 2);
            pg.popMatrix();
            //pg.noLights();
            ThreadController.objMan.renderObjects(pg, -cam.camPos.y);
        }, cam);
    */
}
