package base3D.shapeLibrary;

import base3D.definitions.ResourceUser;
import processing.core.PShape;

public abstract class ShapeGenerator implements ShapeGeneratorInterface {
    protected boolean paintWithTexture;

    protected ResourceUser parent;

    protected String texture_name;

    ShapeGenerator(ResourceUser parent) {
        this.parent = parent;
        this.texture_name = "";
    }

    @SuppressWarnings("SameParameterValue")
    ShapeGenerator(ResourceUser parent, boolean paintWithTexture) {
        this(parent);
        this.paintWithTexture = paintWithTexture;
    }

    public void setPaintWithTexture(boolean b) {paintWithTexture = b;}
    public boolean getPaintWithTexture() {return paintWithTexture;}

    public PShape generateShape() {

        if (paintWithTexture) {
            if (parent == null)
                throw new NullPointerException("[ShapeGenerator] Field 'parent' can't be null. (" + texture_name + ")");

            if (texture_name == null)
                throw new NullPointerException("[ShapeGenerator] Field 'texture_name' can't be null for textured shape generation.");

            return generateTexturedShape();
        }

        return generateTexturelessShape();
    }

    abstract PShape generateTexturedShape();
    abstract PShape generateTexturelessShape();

    protected ResourceUser getParent() {
        return parent;
    }

    public void setParent(ResourceUser parent) {
        this.parent = parent;
    }

    public void set(ShapeGenerator sg) {
        this.parent = sg.parent;
        this.paintWithTexture = sg.paintWithTexture;
        this.texture_name = sg.texture_name;
    }

    public abstract void setShapeHolderOffset(ShapeHolder sh);
}