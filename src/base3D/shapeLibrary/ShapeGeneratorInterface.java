package base3D.shapeLibrary;

import base3D.definitions.ResourceUser;
import processing.core.PShape;

public interface ShapeGeneratorInterface {
    void setPaintWithTexture(boolean b);
    boolean getPaintWithTexture();
    PShape generateShape();
    void setParent(ResourceUser parent);
    void set(ShapeGenerator sg);
    void setShapeHolderOffset(ShapeHolder sh);
}
