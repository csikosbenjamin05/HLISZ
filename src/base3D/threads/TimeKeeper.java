package base3D.threads;

public class TimeKeeper {
    public long currentUpdateTime;
    public long lastUpdateTime;
    public float dTimeSec;

    public void startNewUpdate() {
        lastUpdateTime = currentUpdateTime;
        currentUpdateTime = System.nanoTime();

        dTimeSec = (currentUpdateTime - lastUpdateTime) / 1e9f;
    }

    public void advanceTime() {
        dTimeSec = (System.nanoTime() - currentUpdateTime) / 1e9f;
    }
}