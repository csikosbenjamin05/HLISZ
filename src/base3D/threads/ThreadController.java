package base3D.threads;

import base3D.ENV;
import base3D.threads.updateThreads.ObjectUpdateThread;
import base3D.threads.updateThreads.UIUpdateThread;
import base3D.threads.updateThreads.UpdateThread;


import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadController {
    // Singleton

    //////////////////////////
    // Attributes
    public final UIUpdateThread uiMan;
    public final ObjectUpdateThread objMan;

    private static ArrayList<BaseThread> runningThreads;
    private static ThreadController instance = null;
    //////////////////////////
    // Constructors

    public static void init(int uiFreq, int objFreq) {
        if (ThreadController.instance == null) {
            ThreadController.instance = new ThreadController(uiFreq, objFreq);
        }
    }

    public static ThreadController getInstance() {
        if (ThreadController.instance != null) {
            return ThreadController.instance;
        }
        else throw new NullPointerException("[ThreadController] No instance found. Have you called the init(freq) method?");
    }

    private ThreadController(int uiFreq, int objFreq) {
        runningThreads = new ArrayList<>();
        uiMan = new UIUpdateThread(uiFreq);
        objMan = new ObjectUpdateThread(objFreq);
        runningThreads.add(uiMan);
        runningThreads.add(objMan);
    }



    //////////////////////////
    //Methods


    public static float getObjectManagerTimeKeeperDeltaTime() { // assumes objMan has been initialised
        return instance.objMan.timeKeeper.dTimeSec;
    }

    public void printState() {
        if(ENV.p.frameCount % 600 == 0) {
            System.out.println("\n[ThreadController] List of running threads: ");
            for (BaseThread t : runningThreads) {

                if (t != null) {
                    if (t.getType() == ThreadTypes.UPDATE) {

                        System.out.println("[ThreadController]  (UPDATE) " + t.getName() + ": {" + ((UpdateThread) t).getUPS() + "}");

                    } else if (t.getType() == ThreadTypes.TASK) {

                        System.out.println("[ThreadController]  (TASK) " + t.getName());

                    }
                }

            }
            System.out.println();
        }

    }

    public void startNewThread(BaseThread b) {
        runningThreads.add(b);
        b.startThread();
    }


    public ArrayList<BaseThread> getRunningThreads() {
        return runningThreads;
    }

    public BaseThread getThread(String name) {
        for (BaseThread b: runningThreads)
            if (b.getName().equals(name)) return b;
        return null;
    }

    public boolean isThreadRunning(String name) {
        return getThread(name) == null;
    }
    public void startAllThreads() {

        LinkedList<BaseThread> runningThreadsCopy = new LinkedList<>(runningThreads);

        for (BaseThread t : runningThreadsCopy) {
            if (t != null && !t.hasThreadBeenStarted())
                t.startThread();
        }
    }

    public void stopAllThreads() {

        LinkedList<BaseThread> runningThreadsCopy = new LinkedList<>(runningThreads);

        System.out.println("[ThreadController] Stopping all running threads.");
        for(BaseThread t : runningThreadsCopy) {
            if (t != null)
                t.stopThread();
        }

    }

    public void stopThread(BaseThread t) {
        t.stopThread();
        runningThreads.remove(t);
    }
}
