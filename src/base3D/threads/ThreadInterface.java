package base3D.threads;

public interface ThreadInterface {
    void startThread();
    void stopThread();

    ThreadTypes getType();
}
