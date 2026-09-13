package HLISZ.scenes;

import base3D.ENV;
import base3D.bodies.PathRecorder;
import base3D.bodies.Sphere;
import base3D.bodies.forces.Drag;
import base3D.bodies.forces.Field;
import base3D.bodies.forces.LinearGravity;
import base3D.bodies.forces.SphericalGravity;
import base3D.definitions.Force;
import base3D.shapeLibrary.SphereShapeGenerator;
import base3D.shapeLibrary.brushes.BrushCollection;
import base3D.shapeLibrary.brushes.ShapeBrush;
import base3D.threads.ThreadController;
import base3D.threads.updateThreads.ObjectUpdateThread;
import base3D.ui.*;
import base3D.ui.core.Area;
import base3D.ui.core.Text;
import base3D.ui.core.UIVector;
import processing.core.PVector;

import java.util.HashMap;

public abstract class NBallSimulationSystem {

    enum SimulationState {
        EDITING,
        SIMULATING
    }

    static final int[] ballColours = {
            ENV.p.color(237, 104, 95), // red
            ENV.p.color(53, 150, 62), // green
            ENV.p.color(61, 122, 186), // blue
            ENV.p.color(159, 184, 51), // yellow
            ENV.p.color(100), // gray
    };
    static final float sliderWidth = 0.175f;
    static final float sliderHeight = 0.2f;



    public final int maxBallCount;
    public int activeBallCount;
    public int ballIndex;

    Sphere[] balls;
    PathRecorder[] pathRecorders;
    PVector[] ballsInitialPosition;
    PVector[] ballsInitialVelocity;

    private final HashMap<String, Force[]> forceMap;
    private final String[] forceList;

    private final HashMap<String, Slider> sliders;
    private final String[] sliderNames;
    private final HashMap<String, float[]> sliderValues;
    Label ballCountLabel;
    LabelButton increaseBallCount;
    LabelButton decreaseBallCount;
    LabelButton[] ballControllers;
    LabelButton startSimulation;
    Field fallArea;

    private final Area UIArea;

    SimulationState simulationState;




    Box measurementBox;
    HashMap<String, Label> items;

    public NBallSimulationSystem(int maxBallCount, Field fallArea, String[] forceList, Area UIArea, String[] sliderNames) {
        this.maxBallCount = maxBallCount;
        this.activeBallCount = 1;
        this.ballIndex = 0;

        this.balls = new Sphere[maxBallCount];
        this.ballsInitialPosition = new PVector[maxBallCount];
        this.ballsInitialVelocity = new PVector[maxBallCount];
        this.pathRecorders = new PathRecorder[maxBallCount];

        this.forceMap = new HashMap<>();
        this.forceList = forceList;

        this.fallArea = fallArea;

        this.UIArea = UIArea;
        this.sliders = new HashMap<>();
        this.sliderValues = new HashMap<>();
        this.sliderNames = sliderNames;

        this.simulationState = SimulationState.EDITING;



    }

    // ------------------------------------------------
    public abstract PVector initialisePosition(int i);
    public abstract PVector initialiseVelocity(int i);
    public abstract Force initialiseForce(String forceType, int i);

    // ------------------------------------------------

    public Force[] getForceArray(String name) {
        return forceMap.get(name);
    }

    public Slider getSlider(String name) {return sliders.get(name);}

    public void initialiseObjects() {

        // Initialise the forceMap
        for (String forceStr : forceList) {
            switch (forceStr) {
                case "Drag" -> forceMap.put(forceStr, new Drag[maxBallCount]);
                case "LinearGravity" -> forceMap.put(forceStr, new LinearGravity[maxBallCount]);
                case "SphericalGravity" -> forceMap.put(forceStr, new SphericalGravity[maxBallCount]);
                // expand here
            }
        }


        SphereShapeGenerator ballShapeGenerator = new SphereShapeGenerator(100);
        ballShapeGenerator.drawEdge = false;

        Sphere currentBall;
        PathRecorder currentPathRecorder;
        balls = new Sphere[maxBallCount];

        for (int i = 0; i < maxBallCount; i++) {
            ballShapeGenerator.faceColor = ballColours[i];

            ballsInitialPosition[i] = initialisePosition(i);
            ballsInitialVelocity[i] = initialiseVelocity(i);

            balls[i] = new Sphere(ballsInitialPosition[i], new SphereShapeGenerator(ballShapeGenerator));
            currentBall = balls[i];
            currentBall.getPosition().velocity.set(ballsInitialVelocity[i]);
            currentBall.setActive(false);

            if (i >= activeBallCount) currentBall.setVisible(false);


            for (String f : forceList) {

                forceMap.get(f)[i] = initialiseForce(f, i);

                currentBall.getPosition().forces.add(forceMap.get(f)[i]);

            }


            pathRecorders[i] = new PathRecorder(currentBall, 100, 0.02f);
            currentPathRecorder = pathRecorders[i];
            currentPathRecorder.setActive(false);
            currentPathRecorder.getLineShapeGenerator().meshBrush.borderColor = ballColours[i];
        }
    }

    public void addObjectsToRenderList() {
        ObjectUpdateThread objMan = ThreadController.getInstance().objMan;

        for (int i = 0; i < maxBallCount; i++) {
            objMan.addObject(balls[i]);
            objMan.addObject(pathRecorders[i]);
        }
    }


    private void setActivateForBallsAndRecorders(boolean b) {
        for (int i = 0; i < activeBallCount; i++) {
            balls[i].setActive(b);
            pathRecorders[i].setActive(b);
        }
    }

    private void clearPathRecorderBuffers() {
        for (int i = 0; i < activeBallCount; i++) {
            pathRecorders[i].clearPoints();
        }
    }

    private void resetActiveBallVelocities() {
        for (int i = 0; i < activeBallCount; i++) {
            balls[i].getPosition().velocity.set(0,0,0);
            balls[i].getPosition().value.set(ballsInitialPosition[i]);
        }
    }


    private LabelButton generateLabelButton(String text, float xRel, float yRel, int textSize, boolean centered) {
        LabelButton labelButton1 = new LabelButton(
                new Area(
                        UIArea,
                        new UIVector(UIArea.size, xRel, yRel),
                        new UIVector(0,0)
                ),
                "-"
        );
        labelButton1.getBoxRef().getBrushRef().drawBorder = false;
        labelButton1.getBoxRef().getBrushRef().faceColor = ENV.p.color(0,200,50);
        labelButton1.getTextRef().textSize = textSize;
        labelButton1.setText(text);
        if (centered) {
            labelButton1.getAreaRef().offSet.xCoord.set(-labelButton1.getAreaRef().size.xCoord.get() / 2);
            labelButton1.getAreaRef().offSet.yCoord.set(-labelButton1.getAreaRef().size.yCoord.get() / 2);
        }
        labelButton1.update();

        return labelButton1;
    }

    private Label generateLabel(String text, float xRel, float yRel, int textSize, boolean centered) {
        Label label1 = new LabelButton(
                new Area(
                        UIArea,
                        new UIVector(UIArea.size, xRel, yRel),
                        new UIVector(0,0)
                ),
                "-"
        );
        label1.getBoxRef().getBrushRef().drawBorder = false;
        label1.getBoxRef().getBrushRef().faceColor = ENV.p.color(0,200,50);
        label1.getTextRef().textSize = textSize;
        label1.setText(text);
        if (centered) {
            label1.getAreaRef().offSet.xCoord.set(-label1.getAreaRef().size.xCoord.get() / 2);
            label1.getAreaRef().offSet.yCoord.set(-label1.getAreaRef().size.yCoord.get() / 2);
        }
        label1.update();

        return label1;
    }

    private void setSliderColours(Slider slider, int colour) {
        slider.getLabelValue().getBoxRef().getBrushRef().faceColor = colour;
        slider.getLabelLowerBound().getBoxRef().getBrushRef().faceColor = colour;
        slider.getLabelUpperBound().getBoxRef().getBrushRef().faceColor = colour;
    }



    // ------------------------------------------------
    public abstract float initialiseSliderValues(String sliderName, int i);

    // ------------------------------------------------

    public float[] getSliderValues(String slider) {
        return sliderValues.get(slider);
    }

    public void initialiseUIElements(float[] sliderLowerBounds, float[] sliderUpperBounds, String[] sliderUnits, String[] itemNames, int measurementBoxHeight) {
        int spacing = 10;

        this.measurementBox = new Box(
                new Area(
                        UIArea,
                        new UIVector(UIArea.size, 0.0f,0.01f),
                        new UIVector(400, measurementBoxHeight)
                )
        );
        this.items = new HashMap<>();

        ShapeBrush itemBoxBrush = new ShapeBrush();
        itemBoxBrush.drawFace = false;
        itemBoxBrush.drawBorder = false;


        for (int i = 0; i < itemNames.length; i++) {
            String s = itemNames[i];
            items.put(s, new Label(
                    new Area(
                            measurementBox.getAreaRef(),
                            new UIVector(measurementBox.getAreaRef().position, 1, 1),
                            new UIVector(0,0),
                            new UIVector(measurementBox.getAreaRef().size,0.05f, 1.0f*i/itemNames.length)
                    )
            ));
            items.get(s).setText(s);
            items.get(s).getBoxRef().getBrushRef().set(itemBoxBrush);
        }

        // Display ball count
        ballCountLabel = generateLabel("Labdák : " + activeBallCount, 0.8f, 0f, 30, false);
        //ballCountLabel.getAreaRef().offSet.yCoord.set(ballCountLabel.getAreaRef().size.yCoord.get() * 0.5f);

        increaseBallCount = generateLabelButton("X", 0.8f, 0f, 30, false);
        decreaseBallCount = generateLabelButton("X", 0.8f, 0f, 30, false);

        increaseBallCount.getAreaRef().offSet.xCoord.set(ballCountLabel.getAreaRef().size.xCoord.get() + spacing);
        decreaseBallCount.getAreaRef().offSet.xCoord.set(
                increaseBallCount.getAreaRef().offSet.xCoord.get() + increaseBallCount.getAreaRef().size.xCoord.get() + spacing
        );

        increaseBallCount.getTextRef().setTextWithoutUpdate("^");
        decreaseBallCount.getTextRef().setTextWithoutUpdate("v");


        ballControllers = new LabelButton[maxBallCount];

        LabelButton currentLabelButton;
        for (int i = 0; i < maxBallCount; i++) {
            currentLabelButton = generateLabelButton("0", 0.8f, 0.1f, 30, false);

            currentLabelButton.getBoxRef().getBrushRef().faceColor = ballColours[i];

            if (i >= activeBallCount) currentLabelButton.setActiveAndVisible(false);

            ballControllers[i] = currentLabelButton;

        }

        float ballControllersWidth = ballControllers[0].getWidth();
        for (int i = 0; i < maxBallCount; i++) {
            ballControllers[i].getAreaRef().offSet.xCoord.set(
                    (ballControllersWidth + spacing) * i
            );
            ballControllers[i].getTextRef().setTextWithoutUpdate(String.valueOf(i+1));
        }


        // Start simulation button
        startSimulation = generateLabelButton("Indítás", 1f, 0.9f, 30, true);
        startSimulation.getAreaRef().offSet.xCoord.set(-startSimulation.getAreaRef().size.xCoord.get() * 1);
        startSimulation.getAreaRef().offSet.yCoord.set(-startSimulation.getAreaRef().size.yCoord.get() / 2);





        // Sliders

        float[] currentSliderFloatArr;
        String slider;

        for (int i = 0; i < sliderNames.length; i++) {

            slider = sliderNames[i];

            sliderValues.put(slider, new float[maxBallCount]);

            currentSliderFloatArr = sliderValues.get(slider);
            for (int j = 0; j < maxBallCount; j++) currentSliderFloatArr[j] = initialiseSliderValues(slider, j);


            sliders.put(slider, new Slider(
                    new Area(
                        UIArea,
                        new UIVector(UIArea.size, 0.8f, 0.4f + (0.1f * i)),
                        new UIVector(UIArea.size, sliderWidth, sliderHeight)
                    ),
                    sliderLowerBounds[i], sliderUpperBounds[i], sliderUnits[i]
            ));
        }

        // set the slider's colours to the first ball colour
        for (String s : sliderNames)
            setSliderColours(sliders.get(s), ballColours[0]);


        // Click action for the ballControllers
        for (int i = 0; i < maxBallCount; i++) {
            int colour = ballColours[i];
            int index = i;
            ballControllers[i].setClickAction((PVector c) -> {

                for (String s : sliderNames) {
                    setSliderColours(sliders.get(s), colour);
                    sliderValues.get(s)[ballIndex] = sliders.get(s).value;
                }

                ballIndex = index;

                for (String s : sliderNames)
                    sliders.get(s).setHandlePositionFromValue(sliderValues.get(s)[ballIndex]);

            });
        }


        for (String s : sliderNames)
            sliders.get(s).setHandlePositionFromValue(sliderValues.get(s)[0]);


        // Click action for increaseBallCount
        increaseBallCount.setClickAction((PVector c) -> {
            if (activeBallCount < maxBallCount) {
                balls[activeBallCount].setVisible(true);
                ballControllers[activeBallCount].setActiveAndVisible(true);
                activeBallCount++;
                ballCountLabel.setText("Labdák : " + activeBallCount);
            }
        });

        // Click action for decreaseBallCount
        decreaseBallCount.setClickAction((PVector c) -> {
            if (activeBallCount > 1) {
                activeBallCount--;

                if (ballIndex >= activeBallCount-1) {

                    int colour = ballColours[activeBallCount - 1];

                    for (String s : sliderNames) {
                        setSliderColours(sliders.get(s), colour);
                        sliderValues.get(s)[ballIndex] = sliders.get(s).value;
                    }

                    ballIndex = activeBallCount - 1;

                    for (String s : sliderNames)
                        sliders.get(s).setHandlePositionFromValue(sliderValues.get(s)[ballIndex]);

                }

                balls[activeBallCount].setVisible(false);
                ballControllers[activeBallCount].setActiveAndVisible(false);
                ballCountLabel.setText("Labdák : " + activeBallCount);
            }
        });

        // Click action for startSimulation
        startSimulation.setClickAction((PVector c) -> {
            if (startSimulation.getTextRef().getText().equals("Indítás")) {
                startSimulation.setText("Szerkesztés");
                simulationState = SimulationState.SIMULATING;
                setActivateForBallsAndRecorders(true);
                clearPathRecorderBuffers();


                ballCountLabel.setActiveAndVisible(false);
                increaseBallCount.setActiveAndVisible(false);
                decreaseBallCount.setActiveAndVisible(false);

                for (LabelButton lb : ballControllers) lb.setActiveAndVisible(false);

                for (String s : sliderNames)
                    sliders.get(s).setActiveAndVisible(false);


            } else {
                startSimulation.setText("Indítás");
                simulationState = SimulationState.EDITING;
                setActivateForBallsAndRecorders(false);
                resetActiveBallVelocities();

                ballCountLabel.setActiveAndVisible(true);
                increaseBallCount.setActiveAndVisible(true);
                decreaseBallCount.setActiveAndVisible(true);

                for (int i = 0; i < activeBallCount; i++) ballControllers[i].setActiveAndVisible(true);

                for (String s : sliderNames)
                    sliders.get(s).setActiveAndVisible(true);

            }
            startSimulation.getAreaRef().offSet.xCoord.set(-startSimulation.getAreaRef().size.xCoord.get());
            startSimulation.getAreaRef().offSet.yCoord.set(-startSimulation.getAreaRef().size.yCoord.get() / 2);
        });
    }

    public void addUIElementsToRenderList(Window window) {

        window.addElement(ballCountLabel);
        window.addElement(startSimulation);
        window.addElement(increaseBallCount);
        window.addElement(decreaseBallCount);

        for (LabelButton lb : ballControllers) window.addElement(lb);
        for (String s : sliderNames) window.addElement(sliders.get(s));

        window.addElement(measurementBox);

        for (String s : items.keySet()) {
            window.addElement(items.get(s));
        }

    }


    public void updateSliders() {
        try {
            Thread.sleep(100);
            for (String s : sliderNames)
                sliders.get(s).setHandlePositionFromValue(sliderValues.get(s)[ballIndex]);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setItemValue(String itemName, String value) {
        assert(items.containsKey(itemName));
        items.get(itemName).setText(itemName + value);
    }


}
