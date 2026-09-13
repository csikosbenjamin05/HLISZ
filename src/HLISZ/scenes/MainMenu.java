package HLISZ.scenes;

import base3D.ENV;
import base3D.EventHandler;
import base3D.bodies.PathRecorder;
import base3D.bodies.Sphere;
import base3D.bodies.forces.Field;
import base3D.bodies.forces.SphericalGravity;
import base3D.project.scene.Camera3D;
import base3D.project.scene.Scene;
import base3D.project.scene.SceneManager;
import base3D.shapeLibrary.SphereShapeGenerator;
import base3D.threads.ThreadController;
import base3D.threads.updateThreads.ObjectUpdateThread;
import base3D.ui.LabelButton;
import base3D.ui.Window;
import base3D.ui.core.Area;
import base3D.ui.core.UIVector;
import processing.core.PGraphics;
import processing.core.PVector;

public class MainMenu extends Scene {

    private static class MainMenuUI extends Window {

        public MainMenuUI(Scene parent) {
            super(  new Area(
                        new UIVector(0,0),
                        new UIVector(ENV.p.width, ENV.p.height)
                    ),
                    parent
            );

            EventHandler.subscribe(EventHandler.events.WINDOW_RESIZED, 0, this::windowSizeChanged);
            ThreadController.getInstance().uiMan.addWindow(this);
        }

        private void windowSizeChanged() {
            getAreaRef().setSizeAbsolute(ENV.p.width, ENV.p.height);
        }



        @Override
        public void setup() {
            LabelButton sim1FreeFall = generateCenteredLabelButton("Szabadesés", 0.4f);
            LabelButton sim2FerdeHajitas = generateCenteredLabelButton("Ferde Hajítás", 0.55f);
            LabelButton sim3Muhold = generateCenteredLabelButton("Műholdak", 0.7f);
            //LabelButton sim4KetTest = generateCenteredLabelButton("Két égitest", 0.85f);


            sim1FreeFall.setClickAction((PVector c) -> {
                System.out.println(getParentScene().sceneName);
                switchScene("FreeFall");
            });

            sim2FerdeHajitas.setClickAction((PVector c) -> {
                System.out.println(getParentScene().sceneName);
                switchScene("Throw");
            });

            sim3Muhold.setClickAction((PVector c) -> {
                System.out.println(getParentScene().sceneName);
                switchScene("Satellite");
            });

        }
    }


    MainMenuUI camUI;
    Sphere satellite;
    PathRecorder pathRecorder1;

    public MainMenu(SceneManager sceneManager) {
        super(sceneManager, "MainMenu");
    }

    @Override
    public void setup() {
        // Camera
        cam.camPos.set( -679.00964f, -88.75577f, 895.3229f );
        cam.setLookAt(4.5454917f, 3.3334575f);
        cam.mouseEnabled = false;
        camUI = new MainMenuUI(this);
        camUI.setup();
        cam.HUDRenderMethod = camUI::renderElement;


        // Objects
        ObjectUpdateThread objMan = ThreadController.getInstance().objMan;

        SphereShapeGenerator sphereShapeGeneratorPlanet = new SphereShapeGenerator(200);
        sphereShapeGeneratorPlanet.drawEdge = false;
        Sphere planet = new Sphere(new PVector(-1000,0,0), sphereShapeGeneratorPlanet);

        Field fallArea = new Field(
                new PVector(0,0,0),
                new PVector(50000,10000,50000)
        );


        SphereShapeGenerator sphereShapeGeneratorSatellite = new SphereShapeGenerator(10);
        sphereShapeGeneratorSatellite.drawEdge = false;
        satellite = new Sphere(new PVector(-1000,0,500), sphereShapeGeneratorSatellite);
        satellite.getPosition().velocity.set(-400,0,0);
        satellite.getPosition().forces.add(new SphericalGravity(planet, fallArea));


        pathRecorder1 = new PathRecorder(satellite, 20, 0.05f);

        objMan.addObject(planet);
        objMan.addObject(satellite);
        objMan.addObject(pathRecorder1);
    }

    @Override
    public void displayScene() {
        pathRecorder1.getLineShapeGenerator().render(ENV.p.g, cam.camPos.y);
    }

    public void displayHUD(PGraphics canvas, float y0) {
        ENV.p.fill(0, 255, 0);
        Camera3D.HUD_drawText.render(ENV.p.g, "Default scene \nFPS: " + ENV.p.frameRate + "\n  movementSpeed: " + cam.getMovementSpeed() +
                "\n  camLookAt " + cam.getLookAtRef().toString() +
                "\n  camPos " + cam.camPos.toString()
        );
    }
}
