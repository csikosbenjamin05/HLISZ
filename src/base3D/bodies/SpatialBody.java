package base3D.bodies;

import base3D.Useful;
import base3D.definitions.Renderable;
import base3D.definitions.Updatable;
import base3D.shapeLibrary.ShapeHolder;
import processing.core.PGraphics;
import processing.core.PShape;

public abstract class SpatialBody implements Renderable, Updatable {
    //******************************************
    // Constructor

    public SpatialBody() { // initialise the internal values
        getNewObjectId();
        this.position = new PhysicsComponent();
        this.rotation = new PhysicsComponent();

        this.visible = true;
        this.active = true;

        this.shapeHolder = new ShapeHolder();
    }

    //******************************************
    // Set
    public void set(SpatialBody other) {
        this.position.set(other.getPosition());
        this.rotation.set(other.getRotation());

        this.visible = other.isVisible();
        this.active = other.isActive();

        this.shapeHolder.set(other.shapeHolder);

        this.markPosition = other.markPosition;
    }

    public void setToSimpleBody(SpatialBody other) {set(other);}

    public SpatialBody(SpatialBody other) {
        this();
        getNewObjectId();
        set(other);
    }

    public SpatialBody copy() {return null;}

    //******************************************
    // id system for easier object identification
    private static int objectCountID = 0;
    private int id = -1;
    public void getNewObjectId() {this.id = objectCountID++;}
    public int getId() {return id;}


    //******************************************
    // Position and rotation
    private final PhysicsComponent position;
    private final PhysicsComponent rotation;

    public PhysicsComponent getPosition() {return position;}
    public PhysicsComponent getRotation() {return rotation;}

    public void updatePositionAndRotation() {
        position.updateElement();
        rotation.updateElement();

        Useful.keepInBound(true, rotation.value, 0, 360);
    }


    //******************************************
    // Shape
    private final ShapeHolder shapeHolder;

    public void setShape(PShape sp) {shapeHolder.setShape(sp);}

    public ShapeHolder getShapeHolder() {return shapeHolder;}
    public PShape getShape() {return shapeHolder.getShape();}

    public void renderShape(PGraphics canvas, float y0) {
        shapeHolder.renderPosRot(canvas, y0, position.value, rotation.value);
    }

    //******************************************
    // Visible and active flags for the render() and the update() methods
    private boolean visible;
    private boolean active;

    public boolean isActive() {return active;}
    public boolean isVisible() {return visible;}

    public void setActive(boolean b) {active = b;}
    public void setVisible(boolean b) {visible = b;}

    public void setVisibleAndActive(boolean b) {active = b; visible = b;}


    //******************************************
    // Render & Update

    private boolean markPosition = false;
    private void markPosition(PGraphics canvas, float y0) {
        canvas.pushMatrix();
        canvas.translate(position.value.x, position.value.y + y0, position.value.z);
        canvas.sphere(100);
        canvas.popMatrix();
    }

    public void setMarkPosition(boolean b) {markPosition = b;}
    @Override
    public void renderElement(PGraphics canvas, float y0) {
        if (visible) render(canvas, y0);
        if (markPosition) markPosition(canvas, y0);
    }
    @Override
    public void updateElement() {if (active) update();}

    public abstract void render(PGraphics canvas, float y0);
    public abstract void update();


}