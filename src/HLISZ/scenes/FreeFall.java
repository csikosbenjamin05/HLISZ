package HLISZ.scenes;


import base3D.ENV;
import base3D.EventHandler;
import base3D.bodies.Mesh;
import base3D.bodies.forces.Drag;
import base3D.bodies.forces.Field;
import base3D.bodies.forces.LinearGravity;
import base3D.definitions.Force;
import base3D.noise.LayeredNoise;
import base3D.project.scene.Scene;
import base3D.project.scene.SceneManager;
import base3D.threads.ThreadController;
import base3D.threads.updateThreads.ObjectUpdateThread;
import base3D.ui.LabelButton;
import base3D.ui.Slider;
import base3D.ui.Window;
import base3D.ui.core.Area;
import base3D.ui.core.UIVector;
import processing.core.PApplet;
import processing.core.PVector;


public class FreeFall extends Scene {

    private enum SliderNames {
        BallHeightSlider("BallHeightSlider", 1, 100, " m"),
        VelocitySlider("VelocitySlider", -50, 50, " m/s"),
        GravitationalAccelerationSlider("GravitationalAccelerationSlider", 0, 20, " m/s^2"),
        DragSlider("DragSlider", 0, 0.1f, "");

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
        Gravity("LinearGravity");

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

    private static final String[] itemNames = {"Esés ideje: ", "Emelkedési magasság: "};

    private static class NBallFreeFall extends NBallSimulationSystem {


        public NBallFreeFall(int maxBallCount, Field fallArea, String[] forceList, Area UIArea, String[] sliderNames) {
            super(maxBallCount, fallArea, forceList, UIArea, sliderNames);
        }

        @Override
        public PVector initialisePosition(int i) {
            return new PVector(0,-1000,i*400 - 800);
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
                        0.2f
                );
            }
            // gravity
                return new LinearGravity(
                        fallArea,
                        new PVector(0,100,0), // gravitational acceleration
                        true
                );
        }

        @Override
        public float initialiseSliderValues(String sliderName, int i) {
            switch (sliderName) {
                case "BallHeightSlider" -> {
                    float[] v = {10, 33f, 33f, -9.39f, -9.38f, -8.88f};
                    return  v[i];
                }

                case "GravitationalAccelerationSlider" -> {
                    return 9.81f;
                }
                case "VelocitySlider" -> {
                    float[] v = {0, 0, 0, -9.39f, -9.38f, -8.88f};
                    return v[i];
                }

                case "DragSlider" -> {
                    float[] v = {0, 0f, 0.021f, 0.0210f, 0.0261f, 0.0384f};
                    return v[i];
                }

                default -> {
                    return 0;
                }
            }
        }
    }

    private class FreeFallUI extends Window {


        public FreeFallUI(Scene parent) {
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

            nBallFreeFall.initialiseUIElements(sliderLowerBounds, sliderUpperBounds, sliderUnits, itemNames, 66);
            nBallFreeFall.addUIElementsToRenderList(this);

            for (SliderNames sn : SliderNames.values())
                sn.slider = nBallFreeFall.getSlider(sn.name);

        }
    }


    FreeFallUI freeFallUI;

    NBallFreeFall nBallFreeFall;

    LinearGravity[] ballsGravityComponent;
    Drag[] ballsDragComponent;

    long startTime;
    long endTime;
    float maxHeight;
    NBallSimulationSystem.SimulationState previousSimState;

    public FreeFall(SceneManager sceneManager) {
        super(sceneManager, "FreeFall");
    }

    @Override
    public void setup() {
        // Camera
        cam.camPos.set( -5958.9736f, -1334.1602f, -356.1045f );
        cam.setLookAt(0,3.0589194f);
        cam.mouseEnabled = true;




        // Objects
        ObjectUpdateThread objMan = ThreadController.getInstance().objMan;

        Mesh ground = new Mesh(
                new PVector(0,0,0),
                "tiles/grass.png",
                new LayeredNoise(), 100,100,100
                );


        Field fallArea = new Field(
          new PVector(0,-7500,0),
          new PVector(10000,15000,10000)
        );

        objMan.addObject(ground);
        //objMan.addObject(fallArea);

        freeFallUI = new FreeFallUI(this);
        cam.HUDRenderMethod = freeFallUI::renderElement;

        nBallFreeFall = new NBallFreeFall(
                5,
                fallArea, forceNameList,
                freeFallUI.getAreaRef(), sliderNameList
        );

        freeFallUI.setup();

        nBallFreeFall.initialiseObjects();
        nBallFreeFall.addObjectsToRenderList();


        ballsGravityComponent = (LinearGravity[]) nBallFreeFall.getForceArray(ForceNames.Gravity.name);
        ballsDragComponent = (Drag[])  nBallFreeFall.getForceArray(ForceNames.Drag.name);

        nBallFreeFall.updateSliders();

        previousSimState = NBallSimulationSystem.SimulationState.EDITING;
    }

    @Override
    public void displayScene() {


        if (nBallFreeFall.simulationState != previousSimState) {
            previousSimState = nBallFreeFall.simulationState;

            if (nBallFreeFall.simulationState == NBallSimulationSystem.SimulationState.SIMULATING) {
                startTime = System.nanoTime();
                endTime = startTime;
                maxHeight = 0;
            }

        }

        if (nBallFreeFall.simulationState == NBallSimulationSystem.SimulationState.EDITING) {

            nBallFreeFall.ballsInitialPosition[nBallFreeFall.ballIndex].y =  SliderNames.BallHeightSlider.slider.value * -100;
            nBallFreeFall.ballsInitialVelocity[nBallFreeFall.ballIndex].y = SliderNames.VelocitySlider.slider.value * -100;
            ballsGravityComponent[nBallFreeFall.ballIndex].gravitationalAcceleration.y = SliderNames.GravitationalAccelerationSlider.slider.value * 100;
            ballsDragComponent[nBallFreeFall.ballIndex].k = SliderNames.DragSlider.slider.value;

            for (int i = 0; i < nBallFreeFall.activeBallCount; i++) {
                nBallFreeFall.balls[i].getPosition().value.set(nBallFreeFall.ballsInitialPosition[i]);
                nBallFreeFall.balls[i].getPosition().velocity.set(nBallFreeFall.ballsInitialVelocity[i]);
            }

        } else {

            if (nBallFreeFall.balls[nBallFreeFall.ballIndex].getPosition().value.y < maxHeight) {
                maxHeight = nBallFreeFall.balls[nBallFreeFall.ballIndex].getPosition().value.y;
            }

            if (nBallFreeFall.balls[nBallFreeFall.ballIndex].getPosition().value.y < 0)
                endTime = System.nanoTime();

            nBallFreeFall.setItemValue(itemNames[0], (endTime - startTime) / 1000000 + " ms");
            nBallFreeFall.setItemValue(itemNames[1], -maxHeight / 100 + " m");

            for (int i = 0; i < nBallFreeFall.activeBallCount; i++) {
                nBallFreeFall.pathRecorders[i].getLineShapeGenerator().render(ENV.p.g, cam.camPos.y);
            }

        }

        //System.out.println(nBallFreeFall.balls[nBallFreeFall.ballIndex].getPosition().acceleration + " " + nBallFreeFall.balls[nBallFreeFall.ballIndex].getPosition().velocity);
    }


}
