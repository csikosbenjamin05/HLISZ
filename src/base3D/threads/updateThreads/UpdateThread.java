package base3D.threads.updateThreads;


import base3D.threads.BaseThread;
import base3D.threads.ThreadTypes;

public abstract class UpdateThread extends BaseThread implements UpdateThreadInterface {

    // Attributes:
    //if a value is written to push_updates,
    //then all variables visible to the thread are also written to main memory
    private boolean push_updates;
    private volatile boolean pause; // when adding new objects

    private final int targetFrequency;
    private int ups;
    private boolean running;

    private UpdateThreadStates state;


    // Constructors
    public UpdateThread(int targetFrequency, String uiUpdateThread) {
        super(ThreadTypes.UPDATE, uiUpdateThread);
        this.targetFrequency = targetFrequency;

        this.push_updates = false;
        this.pause = false;

        this.running = true;
        setThreadState(UpdateThreadStates.STOPPED);
    }

    public UpdateThread(int targetFrequency) {
        this(targetFrequency, "UnnamedUpdateThread");
    }



    // Methods:

    public abstract void startThread();

    public void stopThread() {running = false; pause = false;}

    public void run() {
        long updateTime = 1000000000 / targetFrequency; // ns cycle time
        long endOfCurrentCycle;
        int updateCounter = 0;
        long prev_second = System.nanoTime() / 1000000000;
        long second;
        long sleepTime;

        try {
            while(running) {
                setThreadState(UpdateThreadStates.RUNNING);


                while(!pause && running) {
                    updateCounter++;
                    endOfCurrentCycle = System.nanoTime() + updateTime;

                    //Code\\
                    targetFunction();
                    //    \\

                    sleepTime =(endOfCurrentCycle - System.nanoTime()) / 1000000;
                    if (sleepTime > -1) Thread.sleep(sleepTime);

                    push_updates = ! push_updates;

                    second = System.nanoTime() / 1000000000;
                    if (prev_second < second) {
                        ups = updateCounter;
                        updateCounter = 0;
                        prev_second = second;
                    }
                }


                setThreadState(UpdateThreadStates.PAUSED);
                while(pause) {Thread.sleep(10);} // wait while the thread is paused
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setThreadState(UpdateThreadStates.STOPPED);
    }

    public void waitForPause() {
        try {
            while (getThreadState() != UpdateThreadStates.PAUSED) {Thread.sleep(10);}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public abstract void targetFunction();

    // Getters & Setters
    public void pause() {pause = true;}
    public void unpause() {pause = false;}

    public int getUPS() {return ups;}

    public void setThreadState(UpdateThreadStates s) {state = s;}
    public  UpdateThreadStates getThreadState() {return state;}
}

