package base3D.ui.core;

public class AbsoluteCoordinate implements  Coordinate{
    //-----------------------------------------
    // Attributes:
    private float value;

    //-----------------------------------------
    // Constructors:

    public AbsoluteCoordinate(float value) {
        this.value = value;
    }

    //------------------------
    public AbsoluteCoordinate(AbsoluteCoordinate other) {set(other);}
    public AbsoluteCoordinate copy() {return new AbsoluteCoordinate(this);}

    public void set(AbsoluteCoordinate other) {
        this.value = other.value;
    }
    //------------------------

    //-----------------------------------------
    // Methods:

    public void update() {}

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    //-----------------------------------------
    // Getters & Setters:



    public float get() {return value;}

    public void set(float value) {this.value = value;}

    public boolean isRelative() {return false;}

    @Override
    public Coordinate getReference() {
        throw new RuntimeException("[AbsoluteCoordinate] The coordinate is absolute.");
    }

    @Override
    public float getScale() {
        throw new RuntimeException("[AbsoluteCoordinate] The coordinate is absolute.");
    }
}
