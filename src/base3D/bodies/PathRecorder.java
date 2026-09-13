package base3D.bodies;

import base3D.definitions.ResourceUser;
import base3D.shapeLibrary.JaggedLineShapeGenerator;
import base3D.threads.TimeKeeper;
import processing.core.PGraphics;

public class PathRecorder extends SimpleBody implements ResourceUser {

    //////////////////
    // Attributes

    private final SimpleBody target;
    private float updateTimeSec;
    private final TimeKeeper timeKeeper;
    private final JaggedLineShapeGenerator lineShapeGenerator;

    //////////////////
    // Constructor
    public PathRecorder(SimpleBody target, int pointCount, float updateTimeSec) {
        super();
        this.target = target;
        this.lineShapeGenerator = new JaggedLineShapeGenerator(this, pointCount);
        this.updateTimeSec = updateTimeSec;
        this.timeKeeper = new TimeKeeper();


        // Disable position and rotation updates
        getPosition().setActive(false);
        getRotation().setActive(false);

        setVisible(false);
    }

    //////////////////
    // Methods

    @Override
    public void update() {

        // update the timer
        timeKeeper.advanceTime();

        if(timeKeeper.dTimeSec > updateTimeSec) {

            // add the next point
            lineShapeGenerator.addPoint(target.getPosition().value);

            // reset the timer
            timeKeeper.startNewUpdate();
        }

        updatePositionAndRotation();
    }

    @Override
    public void render(PGraphics canvas, float y0) {
        lineShapeGenerator.render(canvas, y0);
    }


    @Override
    public void reGenerateElement() {} // this shape is a line ... this method can't be called ... it just exists


    //////////////////
    // Getters & Setters

    public float getUpdateTimeSec() {
        return updateTimeSec;
    }

    public void setUpdateTimeSec(float updateTimeSec) {
        this.updateTimeSec = updateTimeSec;
    }
    public JaggedLineShapeGenerator getLineShapeGenerator() {
        return lineShapeGenerator;
    }

    //////////////////
    // Methods




    public void clearPoints() {
        lineShapeGenerator.clearPoints();
    }
}
