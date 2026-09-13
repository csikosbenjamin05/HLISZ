package base3D.threads.taskThreads;

import base3D.threads.BaseThread;
import base3D.threads.ThreadController;
import base3D.threads.ThreadTypes;

public class TaskThread extends BaseThread {
    private final Task myTask;


    public interface Task {
        void run();
    }

    public TaskThread(Task task, String name) {
        super(ThreadTypes.TASK, name);
        myTask = task;
    }

    public void run() {
        started = true;

        myTask.run();

        ThreadController.getInstance().stopThread(this);
    }

    @Override
    public void startThread() {
        started = true;
        this.start();
    }

    @Override
    public void stopThread() {}


}
