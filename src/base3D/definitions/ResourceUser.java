package base3D.definitions;

public interface ResourceUser {
    void reGenerateElement(); // Reload the object if any of the used resources has been changed.
}
