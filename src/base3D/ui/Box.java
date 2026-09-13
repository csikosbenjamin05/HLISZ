package base3D.ui;

import base3D.ENV;
import base3D.shapeLibrary.brushes.ShapeBrush;
import base3D.ui.core.Area;
import base3D.ui.core.UIElement;
import base3D.ui.core.UIVector;
import processing.core.PGraphics;

public class Box extends UIElement {

    //-----------------------------------------
    // Attributes:
    private ShapeBrush brush;

    //-----------------------------------------
    // Constructors:

    public Box(Area area) {super(area); this.brush = new ShapeBrush();}
    public Box(Area area, ShapeBrush brush) {super(area); this.brush = brush;}
    public Box(Area area, ShapeBrush brush, boolean active, boolean visible) {
        super(area, active, visible);
        this.brush = brush;
    }
    //------------------------
    public Box(Box other) {super(other); set(other);}
    public Box copy() {return new Box(this);}
    public void set(Box other) {
        this.set((UIElement) other);
        this.brush = other.getBrushRef();
    }
    //------------------------


    //-----------------------------------------
    // Methods:

    public void render(PGraphics canvas) {
        UIVector position = getAreaRef().cachedPosition;
        UIVector size     = getAreaRef().size;

        brush.setPGraphics(canvas);
        canvas.rect(position.cacheX, position.cacheY, size.cacheX, size.cacheY);
    }

    @Override
    public String toString() {
        return "Box(" + getUIElementInfo() + " Area:" + getAreaRef() + "\twrapper:" + brush + ")";
    }

    //-----------------------------------------
    // Getters & Setters:

    public ShapeBrush getBrushRef() {return brush;}
    public void setBrush(ShapeBrush brush) {this.brush = brush;}

}
