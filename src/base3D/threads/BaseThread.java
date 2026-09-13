package base3D.threads;

public abstract class BaseThread extends Thread implements ThreadInterface {
    private final ThreadTypes type;
    protected boolean started;

    public boolean hasThreadBeenStarted() {
        return started;
    }

    public BaseThread(ThreadTypes type) {
        this.type = type;
    }
    public BaseThread(ThreadTypes type, String name) {
        this.type = type;
        this.setName(name);
    }

    public ThreadTypes getType() {return type;}

    public abstract void run();
    public abstract void startThread();

    public abstract void stopThread();


}