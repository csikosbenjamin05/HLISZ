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


public class Throw extends Scene {

    private enum SliderNames {
        BallHeightSlider("BallHeightSlider", 0, 100, " m"),
        AngleSlider("AngleSlider", -90, 90, " °"),
        VelocitySlider("VelocitySlider", 0, 100, " m/s"),
        GravitationalAccelerationSlider("GravitationalAccelerationSlider", 0, 20, " m/s^2"),
        DragSlider("DragSlider", 0, 0.05f, "");

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


    private static final String[] itemNames = {"Hajítási idő: ", "Hajítás magassága: ", "Hajítás távolsága: "};

    private static class NBallThrow extends NBallSimulationSystem {


        public NBallThrow(int maxBallCount, Field fallArea, String[] forceList, Area UIArea, String[] sliderNames) {
            super(maxBallCount, fallArea, forceList, UIArea, sliderNames);
        }

        @Override
        public PVector initialisePosition(int i) {
            return new PVector(i * 400 - 800, -1000, -10000);
        }

        @Override
        public PVector initialiseVelocity(int i) {
            return new PVector(0, 0, 0);
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
            return new LinearGravity(
                    fallArea,
                    new PVector(0, 100, 0), // gravitational acceleration
                    true
            );
        }

        @Override
        public float initialiseSliderValues(String sliderName, int i) {
            switch (sliderName) {
                case "BallHeightSlider" -> {
                    return 0.1f;
                }
                case "AngleSlider" -> {
                    return 30f;
                }
                case "GravitationalAccelerationSlider" -> {
                    return 9.81f;
                }
                case "VelocitySlider" -> {
                    float[] v = {35, 35, 50, 50, 0};
                    return v[i];
                }
                case "DragSlider" -> {
                    float[] v = {0, 0.0261f, 0, 0.04f, 0};
                    return v[i];
                }

                default -> {
                    return 0;
                }
            }
        }
    }


    private class ThrowUI extends Window {

        public ThrowUI(Scene parent) {
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

            nBallThrow.initialiseUIElements(sliderLowerBounds, sliderUpperBounds, sliderUnits, itemNames, 100);
            nBallThrow.addUIElementsToRenderList(this);

            for (SliderNames sn : SliderNames.values())
                sn.slider = nBallThrow.getSlider(sn.name);


        }
    }


    ThrowUI throwUI;

    NBallThrow nBallThrow;

    LinearGravity[] ballsGravityComponent;
    Drag[] ballsDragComponent;

    NBallSimulationSystem.SimulationState previousSimState;

    long startTime;
    long endTime;
    float maxHeight;

    public Throw(SceneManager sceneManager) {
        super(sceneManager, "Throw");
    }

    @Override
    public void setup() {
        // Camera
        cam.camPos.set( -14117.973f, -2090.1726f, -2.4542847f );
        cam.setLookAt(0,3.0589194f);
        cam.mouseEnabled = true;


        // Objects
        ObjectUpdateThread objMan = ThreadController.getInstance().objMan;

        Mesh ground = new Mesh(
                new PVector(0,0,15000),
                "tiles/grass.png",
                new LayeredNoise(), 100,500,100
                );


        Field fallArea = new Field(
          new PVector(0,-15000,-5000),
          new PVector(10000,30000,50000)
        );

        objMan.addObject(ground);
        //objMan.addObject(fallArea);

        throwUI = new ThrowUI(this);
        cam.HUDRenderMethod = throwUI::renderElement;

        nBallThrow = new NBallThrow(
                5,
                fallArea, forceNameList,
                throwUI.getAreaRef(), sliderNameList
        );

        throwUI.setup();

        nBallThrow.initialiseObjects();
        nBallThrow.addObjectsToRenderList();

        ballsGravityComponent = (LinearGravity[]) nBallThrow.getForceArray(ForceNames.Gravity.name);
        ballsDragComponent = (Drag[])  nBallThrow.getForceArray(ForceNames.Drag.name);

        for (int i = 0; i < nBallThrow.maxBallCount; i++) initialiseBalls(i);


        nBallThrow.updateSliders();

        previousSimState = NBallSimulationSystem.SimulationState.EDITING;

    }

    void initialiseBalls(int ballIndex) {

        nBallThrow.ballsInitialPosition[ballIndex].y =  nBallThrow.getSliderValues(SliderNames.BallHeightSlider.name)[ballIndex] * -100;

        ballsGravityComponent[ballIndex].gravitationalAcceleration.y = nBallThrow.getSliderValues(SliderNames.GravitationalAccelerationSlider.name)[ballIndex] * 100;
        ballsDragComponent[ballIndex].k = nBallThrow.getSliderValues(SliderNames.DragSlider.name)[ballIndex];

        float angleSliderValue = nBallThrow.getSliderValues(SliderNames.AngleSlider.name)[ballIndex];
        float velocitySliderValue = nBallThrow.getSliderValues(SliderNames.VelocitySlider.name)[ballIndex];
        nBallThrow.ballsInitialVelocity[ballIndex].z = velocitySliderValue * PApplet.cos((float) Math.toRadians(angleSliderValue)) * 100;
        nBallThrow.ballsInitialVelocity[ballIndex].y = velocitySliderValue * PApplet.sin((float) Math.toRadians(angleSliderValue)) * 100;


        for (int i = 0; i < nBallThrow.activeBallCount; i++) {
            nBallThrow.balls[i].getPosition().velocity.set(nBallThrow.ballsInitialVelocity[i]);
            nBallThrow.balls[i].getPosition().value.set(nBallThrow.ballsInitialPosition[i]);
        }
    }

    void updateBalls(int ballIndex) {

        nBallThrow.ballsInitialPosition[ballIndex].y =  SliderNames.BallHeightSlider.slider.value * -100;

        ballsGravityComponent[ballIndex].gravitationalAcceleration.y = SliderNames.GravitationalAccelerationSlider.slider.value * 100;
        ballsDragComponent[ballIndex].k = SliderNames.DragSlider.slider.value;

        float angleSliderValue = -SliderNames.AngleSlider.slider.value;
        nBallThrow.ballsInitialVelocity[ballIndex].z = SliderNames.VelocitySlider.slider.value * PApplet.cos((float) Math.toRadians(angleSliderValue)) * 100;
        nBallThrow.ballsInitialVelocity[ballIndex].y = SliderNames.VelocitySlider.slider.value * PApplet.sin((float) Math.toRadians(angleSliderValue)) * 100;


        for (int i = 0; i < nBallThrow.activeBallCount; i++) {
            nBallThrow.balls[i].getPosition().velocity.set(nBallThrow.ballsInitialVelocity[i]);
            nBallThrow.balls[i].getPosition().value.set(nBallThrow.ballsInitialPosition[i]);
        }


    }

    @Override
    public void displayScene() {


        if (nBallThrow.simulationState != previousSimState) {
            previousSimState = nBallThrow.simulationState;

            if (nBallThrow.simulationState == NBallSimulationSystem.SimulationState.SIMULATING) {
                maxHeight = 0;
                startTime = System.nanoTime();
                endTime = startTime;
            }

        }

        if (nBallThrow.simulationState == NBallSimulationSystem.SimulationState.EDITING) {

            updateBalls(nBallThrow.ballIndex);

            //System.out.println(ballsInitialVelocity[ballIndex]);
        } else {
            //System.out.println(balls[ballIndex].getPosition().acceleration + " " + balls[ballIndex].getPosition().velocity);

            if (nBallThrow.balls[nBallThrow.ballIndex].getPosition().value.y < maxHeight) {
                maxHeight = nBallThrow.balls[nBallThrow.ballIndex].getPosition().value.y;
            }

            if (nBallThrow.balls[nBallThrow.ballIndex].getPosition().value.y < 0)
                endTime = System.nanoTime();

            nBallThrow.setItemValue(itemNames[0], (endTime - startTime) / 1000000 + " ms");
            nBallThrow.setItemValue(itemNames[1], -maxHeight / 100 + " m");
            nBallThrow.setItemValue(itemNames[2], nBallThrow.balls[nBallThrow.ballIndex].getPosition().value.z / 100 + 100 + " m");

            for (int i = 0; i < nBallThrow.activeBallCount; i++) {
                nBallThrow.pathRecorders[i].getLineShapeGenerator().render(ENV.p.g, cam.camPos.y);
            }

        }
    }

}
