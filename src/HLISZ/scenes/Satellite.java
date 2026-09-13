package HLISZ.scenes;


import base3D.ENV;
import base3D.EventHandler;
import base3D.bodies.Sphere;
import base3D.bodies.forces.Drag;
import base3D.bodies.forces.Field;
import base3D.bodies.forces.SphericalGravity;
import base3D.definitions.Force;
import base3D.project.scene.Scene;
import base3D.project.scene.SceneManager;
import base3D.shapeLibrary.SphereShapeGenerator;
import base3D.threads.ThreadController;
import base3D.threads.updateThreads.ObjectUpdateThread;
import base3D.ui.LabelButton;
import base3D.ui.Slider;
import base3D.ui.Window;
import base3D.ui.core.Area;
import base3D.ui.core.UIVector;
import processing.core.PVector;


public class Satellite extends Scene {

    private enum SliderNames {
        BallHeightSlider("BallHeightSlider", 20, 100,  "*10^4 m"),
        VelocitySlider("VelocitySlider", 0, 100, "*10^2 m/s"),
        DragSlider("DragSlider", 0, 0.02f, "");

        final String name;
        Slider slider;
        final float lower;
        final float upper;
        final String unit;

        SliderNames(String name, float lower, float upper, String unit) {
            this.name = name;
            this.lower = lower;
            this.upper = upper;
            this.unit = unit;
        }
    }

    private enum ForceNames {
        Drag("Drag"),
        Gravity("SphericalGravity");

        final String name;

        ForceNames(String name) {this.name = name;}
    }

    static final String[] forceNameList;
    static final String[] sliderNameList;
    static final float[] sliderLowerBounds;
    static final float[] sliderUpperBounds;
    static final String[] sliderUnits;

    static {

        // Get the data of the sliders
        SliderNames[] sn = SliderNames.values();

        sliderNameList = new String[sn.length];
        sliderLowerBounds = new float[sn.length];
        sliderUpperBounds = new float[sn.length];
        sliderUnits = new String[sn.length];

        for (int i = 0; i < sn.length; i++) {
            sliderNameList[i] = sn[i].name;
            sliderLowerBounds[i] = sn[i].lower;
            sliderUpperBounds[i] = sn[i].upper;
            sliderUnits[i] = sn[i].unit;
        }




        // Get the names of the forces
        ForceNames[] fn = ForceNames.values();
        forceNameList = new String[fn.length];
        for (int i = 0; i < fn.length; i++)
            forceNameList[i] = fn[i].name;

    }

    private static final String[] itemNames = {""};

    private class NBallSatellite extends NBallSimulationSystem {


        public NBallSatellite(int maxBallCount, Field fallArea, String[] forceList, Area UIArea, String[] sliderNames) {
            super(maxBallCount, fallArea, forceList, UIArea, sliderNames);
        }

        @Override
        public PVector initialisePosition(int i) {
            return new PVector(0,0,2000);
        }

        @Override
        public PVector initialiseVelocity(int i) {
            return new PVector(0,0,0);
        }

        @Override
        public Force initialiseForce(String forceType, int i) {
            if (forceType.equals(ForceNames.Drag.name)) {
                return new Drag(
                        fallArea,
                        0f
                );
            }
            // gravity
            return new SphericalGravity(
                    planet,
                    fallArea,
                    true
            );
        }

        @Override
        public float initialiseSliderValues(String sliderName, int i) {
            if (sliderName.equals("BallHeightSlider")) {
                float[] v = {50, 70, 80, 90, 20};
                return v[i];
            } else if (sliderName.equals(SliderNames.VelocitySlider.name)) {
                float[] v = {26, 30, 20, 30, 40};
                return v[i];
            }
            // velocity, drag
            return 0;
        }
    }


    private class SatelliteUI extends Window {


        public SatelliteUI(Scene parent) {
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
            LabelButton backToMainMenu = generateLabelButton("Vissza a főmenübe", 0f, 0.9f, 30, false);
            backToMainMenu.setClickAction((PVector c) -> {
                System.out.println(getParentScene().sceneName);
                switchScene("MainMenu");
            });

            nBallSatellite.initialiseUIElements(sliderLowerBounds, sliderUpperBounds, sliderUnits, itemNames, 0);
            nBallSatellite.addUIElementsToRenderList(this);

            for (SliderNames sn : SliderNames.values())
                sn.slider = nBallSatellite.getSlider(sn.name);

        }
    }


    SatelliteUI satelliteUI;

    NBallSatellite nBallSatellite;

    SphericalGravity[] ballsGravityComponent;
    Drag[] ballsDragComponent;

    Sphere planet;

    public Satellite(SceneManager sceneManager) {
        super(sceneManager, "Satellite");
        skyBoxName = "skybox/stars.png";
    }

    @Override
    public void setup() {
        // Camera
        cam.camPos.set( -11282.18f, -3243.8696f, 2712.0698f );
        cam.setLookAt(-0.19634955f,3.773343f);
        cam.mouseEnabled = true;


        // Objects
        ObjectUpdateThread objMan = ThreadController.getInstance().objMan;

        SphereShapeGenerator planetSSG = new SphereShapeGenerator(200);
        planetSSG.drawEdge = false;
        planetSSG.faceColor = ENV.p.color(100,200,100);

        planet = new Sphere(new PVector(0,0,0), planetSSG);

        planet.getPosition().mass = 1000;

        Field fallArea = new Field(
          new PVector(0,0,0),
          new PVector(50000,10000,50000)
        );

        objMan.addObject(planet);
        //objMan.addObject(fallArea);

        satelliteUI = new SatelliteUI(this);
        cam.HUDRenderMethod = satelliteUI::renderElement;

        nBallSatellite = new NBallSatellite(
                5,
                fallArea, forceNameList,
                satelliteUI.getAreaRef(), sliderNameList
        );

        satelliteUI.setup();

        nBallSatellite.initialiseObjects();
        nBallSatellite.addObjectsToRenderList();


        ballsGravityComponent = (SphericalGravity[]) nBallSatellite.getForceArray(ForceNames.Gravity.name);
        ballsDragComponent = (Drag[])  nBallSatellite.getForceArray(ForceNames.Drag.name);

        nBallSatellite.updateSliders();
    }

    @Override
    public void displayScene() {

        if (nBallSatellite.simulationState == NBallSimulationSystem.SimulationState.EDITING) {


            nBallSatellite.ballsInitialPosition[nBallSatellite.ballIndex].z =  SliderNames.BallHeightSlider.slider.value * 100;
            nBallSatellite.ballsInitialVelocity[nBallSatellite.ballIndex].x =  SliderNames.VelocitySlider.slider.value * 100;
            ballsDragComponent[nBallSatellite.ballIndex].k = SliderNames.DragSlider.slider.value;

            for (int i = 0; i < nBallSatellite.activeBallCount; i++) {
                nBallSatellite.balls[i].getPosition().value.set(nBallSatellite.ballsInitialPosition[i]);
                nBallSatellite.balls[i].getPosition().velocity.set(nBallSatellite.ballsInitialVelocity[i]);
            }
        } else {

            for (int i = 0; i < nBallSatellite.activeBallCount; i++) {
                nBallSatellite.pathRecorders[i].getLineShapeGenerator().render(ENV.p.g, cam.camPos.y);
            }

        }
    }


}
