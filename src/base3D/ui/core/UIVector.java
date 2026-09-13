package base3D.ui.core;

import java.lang.ref.Reference;

public class UIVector {
    //-----------------------------------------
    // Attributes:
    public Coordinate xCoord;
    public Coordinate yCoord;

    public float cacheX;
    public float cacheY;

    //-----------------------------------------
    // Constructors:

    public UIVector(UIVector reference, float relativeX, float relativeY) {set(reference, relativeX, relativeY);}

    public UIVector(float x, float y) {
        this.cacheX = 0;
        this.cacheY = 0;
        this.xCoord = new AbsoluteCoordinate(x);
        this.yCoord = new AbsoluteCoordinate(y);
    }

    public void set(UIVector reference, float relativeX, float relativeY) {
        this.cacheX = 0;
        this.cacheY = 0;

        if (reference == null)
            throw new RuntimeException("[UIVector] Can't make a reference to null");

        this.xCoord = new RelativeCoordinate(reference.xCoord, relativeX);
        this.yCoord = new RelativeCoordinate(reference.yCoord, relativeY);
    }

    public UIVector(Coordinate ref1, float v1, Coordinate ref2, float v2) {
        if (ref1 == null)
            this.xCoord = new AbsoluteCoordinate(v1);
        else
            this.xCoord = new RelativeCoordinate(ref1, v1);

        if (ref2 == null)
            this.yCoord = new AbsoluteCoordinate(v2);
        else
            this.yCoord = new RelativeCoordinate(ref2, v2);

    }

    //------------------------
    public UIVector(UIVector other) {set(other);}
    public UIVector copy() {return new UIVector(this);}

    public void set(UIVector other) {
        this.cacheX = 0;
        this.cacheY = 0;

        this.xCoord = other.xCoord.copy();
        this.yCoord = other.yCoord.copy();
    }
    //------------------------

    //-----------------------------------------
    // Methods:

    public void update() {
        xCoord.update();
        yCoord.update();
        cacheX = xCoord.get();
        cacheY = yCoord.get();
    }

    @Override
    public String toString() {
        return "UIVector(  x:" + cacheX + " y:" + cacheY +  ")";
    }

    //-----------------------------------------
    // Getters & Setters:

    public void setReference(UIVector ref) {

        if (ref == null)
            throw new RuntimeException("[UIVector] Can't make a reference to null.");

        if (xCoord.isRelative()) ((RelativeCoordinate) xCoord).reference = ref.xCoord;
        if (yCoord.isRelative()) ((RelativeCoordinate) yCoord).reference = ref.yCoord;

    }

    public void ensureXIsAbsolute() {
        if (xCoord.isRelative())
            throw new RuntimeException("[UIVector] Coordinate X must be absolute");
    }

    public void ensureYIsAbsolute() {
        if (yCoord.isRelative())
            throw new RuntimeException("[UIVector] Coordinate Y must be absolute");

    }

    public void ensureXYAreAbsolute() {
        ensureXIsAbsolute();
        ensureYIsAbsolute();
    }
}
