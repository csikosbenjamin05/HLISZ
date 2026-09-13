package HLISZ;

import base3D.ENV;
import base3D.EventHandler;
import base3D.bodies.Cube;
import base3D.bodies.Mesh;
import base3D.bodies.SimpleBody;
import base3D.bodies.Sphere;
import base3D.bodies.forces.Field;
import base3D.noise.LayeredNoise;
import base3D.noise.PerlinNoise;
import base3D.project.scene.Camera3D;
import base3D.project.scene.Scene;
import base3D.project.scene.SceneManager;
import base3D.shapeLibrary.CubeShapeGenerator;
import base3D.shapeLibrary.MeshShapeGenerator;
import base3D.shapeLibrary.SphereShapeGenerator;
import base3D.threads.ThreadController;
import base3D.threads.updateThreads.ObjectUpdateThread;
import base3D.ui.*;
import base3D.ui.core.Area;
import base3D.ui.core.UIVector;
import processing.core.PGraphics;
import processing.core.PVector;

public class MyScene extends Scene {

    public static class MyWindow extends Window {

        public MyWindow(Scene parent) {
            super(new Area( new UIVector(0,0), new UIVector(ENV.p.width, ENV.p.height)), parent);

            EventHandler.subscribe(EventHandler.events.WINDOW_RESIZED, 0, this::windowSizeChanged);
            ThreadController.getInstance().uiMan.addWindow(this);
        }

        private void windowSizeChanged() {
            getAreaRef().setSizeAbsolute(ENV.p.width, ENV.p.height);
        }

        @Override
        public void setup() {
            Box b1 = new Box(new Area(
                            getAreaRef(),
                            getAreaRef().position,
                            new UIVector(getAreaRef().size, 0.2f, 0.2f),
                            new UIVector(getAreaRef().size, 0.2f, 0.2f)
            ));
            addElement(b1);

            Label l1 = new Label(
                    new Area(
                            getAreaRef(),
                            getAreaRef().position,
                            new UIVector(0,0), // set by the label
                            new UIVector(getAreaRef().size, 0.5f, 0.5f)
                    ),
                    "HelloFromHell! "
            );
            addElement(l1);

            LabelButton bb1 = new LabelButton(
                            new Area(
                                    getAreaRef(),
                                    getAreaRef().position,
                                    new UIVector(0,0), // set by the label
                                    new UIVector(getAreaRef().size, 0.7f, 0.7f)
                            ),
                            " Click me! "
            );
            bb1.setClickAction(p -> {
                if (bb1.getBoxRef().getBrushRef().faceColor == ENV.p.color(100,100,100))
                    bb1.getBoxRef().getBrushRef().faceColor = ENV.p.color(100,255,100);
                else
                    bb1.getBoxRef().getBrushRef().faceColor = ENV.p.color(100,100,100);
                System.out.println("GOOD JOB");
            });
            addElement(bb1);

            ImageButton imgButton = new ImageButton(
                    new Area(
                            getAreaRef(),
                            getAreaRef().position,
                            new UIVector(0,0),
                            new UIVector(getAreaRef().size, 0.1f, 0.1f)
                    ),
                    "UI/unchecked.png"
            );
            imgButton.setClickAction(p -> System.out.println("Img Button pressed!"));
            imgButton.matchImageSize();
            addElement(imgButton);

            CheckBox cb = new CheckBox(
                    new Area(
                            getAreaRef(),
                            getAreaRef().position,
                                new UIVector(0,0),
                            new UIVector(getAreaRef().size, 0.1f, 0.2f)
                    ),
                    "UI/checked.png",
                    "UI/unchecked.png"
            );
            cb.matchImageSize();
            cb.setClickAction(p -> {
                cb.changeState();
                System.out.println("CheckBox " + ((cb.getState()) ? "CHECKED" : "unchecked") + " !");
            });
            addElement(cb);


            Slider sl1 = new Slider(
                    new Area(
                            getAreaRef(),
                            getAreaRef().position,
                            new UIVector(getAreaRef().size, 0.2f, 0.1f),
                            new UIVector(getAreaRef().size, 0.6f, 0.1f)
                    ),
                    -5,
                    5
            );

            addElement(sl1);

        }
    }


    MyWindow camHudUI;

    Cube cubeGenTest;
    Cube c1, rotatingCube;
    Sphere s;

    Mesh mesh1, mesh2;
    LayeredNoise layeredNoise1, layeredNoise2;

    Field testField;

    public MyScene(SceneManager sceneManager) {
        super(sceneManager, "testScene1");
    }

    public void setup() {

        camHudUI = new MyWindow(this);
        cam.HUDRenderMethod = camHudUI::renderElement;
        //cam.HUDRenderMethod = this::displayHUD;

        EventHandler.subscribe(EventHandler.events.KEY_PRESSED, 'r', this::rotatingCubeReset);

        ObjectUpdateThread objMan = ThreadController.getInstance().objMan;

        CubeShapeGenerator cubeGen = new CubeShapeGenerator(100);
        cubeGen.brush.borderColor = ENV.p.color(255, 0, 0);
        cubeGen.brush.drawFace = false;
        c1 = new Cube(new PVector(500, 500, 500), cubeGen);
        c1.getPosition().velocity.set(0,0,0);
        objMan.addObject(c1);
        rotatingCube = new Cube(new PVector(500, 500, 0), "skybox/skybox.png", 20);
        rotatingCubeReset();
        rotatingCube.getRotation().velocity.set(30, 20, 0);
        objMan.addObject(rotatingCube);

        s = new Sphere(new PVector(0,0,0), "sphere/earth_like.png", 10);
        objMan.addObject(s);

        objMan.addObject(new SimpleBody(new PVector(2000, 0, 0)));


        cubeGenTest = new Cube(new PVector(-1000, 0, -1000), "skybox/skybox5.png", 100);
        cubeGenTest.getRotation().velocity.set(2, 1, 0);
        objMan.addObject(new Sphere(new PVector(-1000, 0, -1000), 10));
        objMan.addObject(cubeGenTest);

        layeredNoise1 = new LayeredNoise();
        PerlinNoise perlin = new PerlinNoise();
        perlin.setAmplitude(500);
        perlin.setOctaveCount(30);
        perlin.setFrequencyXYZ(.05f);
        layeredNoise1.addLayer(perlin);


        // MESH1
        MeshShapeGenerator m1 = new MeshShapeGenerator(layeredNoise1, 100,100, 10);
        m1.startHeightFromZero = true;
        m1.wrapper.borderColor = ENV.p.color(99,99,99);
        mesh1 = new Mesh(new PVector(0,1000,0), m1);

        objMan.addObject(mesh1);


        layeredNoise2 = new LayeredNoise();
        PerlinNoise perlin2 = new PerlinNoise();
        perlin2.setAmplitude(500);
        perlin2.setOctaveCount(30);
        perlin2.setFrequencyXYZ(.05f);
        layeredNoise2.addLayer(perlin2);


        // MESH2
        MeshShapeGenerator m2 = new MeshShapeGenerator(null, layeredNoise2,100, 100, 50, "tile/grass.png");
        m2.wrapper.borderColor = ENV.p.color(99,99,99);
        m2.startHeightFromZero = true;
        mesh2 = new Mesh(new PVector(0,500,0), m2);

        objMan.addObject(mesh2);


        SphereShapeGenerator shapeGen1 = new SphereShapeGenerator(200);
        shapeGen1.drawFace = false;
        shapeGen1.sphereDetail = 5;
        Sphere sphere1 = new Sphere(new PVector(-900, 900, -900), shapeGen1);
        sphere1.setActive(false);
        objMan.addObject(sphere1);

        SphereShapeGenerator shapeGen2 = new SphereShapeGenerator(200, "sphere/earth_like.png");
        shapeGen2.drawEdge = false;
        shapeGen2.faceColor = ENV.p.color(255);
        shapeGen2.sphereDetail = 30;
        Sphere sphere2 = new Sphere(new PVector(-900, 1500, -900), shapeGen2);
        sphere2.setActive(false);

        objMan.addObject(sphere2);



        Sphere s1 = new Sphere(new PVector(-900, 2000, -900), "sphere/mars.png", 200);
        objMan.addObject(s1);


        testField = new Field(new PVector(2000, 0, 2000), new PVector(200,100,200));
        Sphere t1 = new Sphere(testField.getCorner1(), 10);
        Sphere t2 = new Sphere(testField.getCorner2(), 10);

        objMan.addObject(testField);
        objMan.addObject(t1);
        objMan.addObject(t2);

    }

    public void displayScene() {
        PVector vRadius = PVector.sub(s.getPosition().value, rotatingCube.getPosition().value);

        float r = vRadius.mag();
        float r3 = r*r*r;
        float f = 1e8f /r3;


        // G * (m*m)/r^3 * rv
        PVector acc = new PVector(f,f,f);
        acc.x *= vRadius.x;
        acc.y *= vRadius.y;
        acc.z *= vRadius.z;

        rotatingCube.getPosition().acceleration.set(acc);

        if (testField.isObjectInside(cam.camPos)) {
            System.out.println("The camera is inside the testField!  " + ENV.p.frameRate);
        }

    }

    public void displayHUD(PGraphics canvas, float y0) {
        ENV.p.fill(0, 255, 0);
        Camera3D.HUD_drawText.render(ENV.p.g, "Default scene \nFPS: " + ENV.p.frameRate + "\n  movementSpeed: " + cam.getMovementSpeed() +
                "\n  camLookAt " + cam.getLookAtRef().toString() +
                "\n  camPos " + cam.camPos.toString()
        );
    }


    public void rotatingCubeReset() {
        rotatingCube.getPosition().value.set(200, 200, 200);
        rotatingCube.getPosition().velocity.set(0, 20 ,-500);
        rotatingCube.getPosition().acceleration.set(0,0,0);
    }
}
