package base3D.project.scene;

import base3D.ENV;
import base3D.bodies.Cube;
import processing.core.PGraphics;
import processing.core.PVector;


public class SkyBox extends Cube {

    int renderDistance;

    public SkyBox(PVector camPosRef, String texture, int renderDistance) {
        super(camPosRef, texture, renderDistance);
        this.renderDistance = renderDistance;
        getCubeGen().size = renderDistance;
        reGenerateElement();
    }

    public void setRenderDistance(int renderDistance) {
        this.renderDistance = renderDistance;
        getCubeGen().size = renderDistance;
        reGenerateElement();
    }

    public void drawSkyBox(PGraphics g, PVector camPos) {
        getPosition().value.set(camPos.x, 0, camPos.z);
        render(ENV.p.g, camPos.y);
    }
}