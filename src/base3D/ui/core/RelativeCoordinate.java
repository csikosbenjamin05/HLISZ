package base3D.ui.core;

public class RelativeCoordinate implements Coordinate {
    //-----------------------------------------
    // Attributes:
    public float scale; // real between 0 and 1
    public Coordinate reference;
    public float cache;

    //-----------------------------------------
    // Constructors:
    public RelativeCoordinate(Coordinate ref, float scale) {set(ref, scale);}

    public void set(Coordinate reference, float scale) {
        if(scale < 0 || scale > 1)
            throw new RuntimeException(
                            "[RelativeCoordinate] The scale parameter must have a value between 0 and 1. "
                            + scale + " is not a valid value."
            );

        this.scale = scale;
        this.reference = reference;
    }

    //------------------------
    public RelativeCoordinate(RelativeCoordinate other) {set(other);}
    public RelativeCoordinate copy() {return new RelativeCoordinate(this);}

    public void set(RelativeCoordinate other) {set(other.reference, other.scale);}
    //------------------------

    //-----------------------------------------
    // Methods:

    public void update() {
        cache = reference.get() * scale;
    }

    @Override
    public String toString() {
        return String.valueOf(cache);
    }

    //-----------------------------------------
    // Getters & Setters:

    public float get() {return cache;}

    public void set(float scale) {this.scale = scale;}
    public boolean isRelative() {return true;}

    @Override
    public Coordinate getReference() {
        return reference;
    }

    @Override
    public float getScale() {
        return scale;
    }
}
