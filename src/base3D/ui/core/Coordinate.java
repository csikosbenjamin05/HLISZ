package base3D.ui.core;

public interface Coordinate {

    void update(); // Update screen position

    float get();

    void set(float value); // set absolute or relative value

    boolean isRelative();

    Coordinate getReference();

    float getScale();

    Coordinate copy();

}
