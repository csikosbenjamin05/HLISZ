package base3D.definitions;

public interface Updatable {

    void updateElement(); // runs at every update,  bVisible? -> True: update()

    void setActive(boolean b);
    boolean isActive();
}
