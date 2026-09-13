package base3D.threads.taskThreads;

import base3D.resources.Resource;
import base3D.threads.ThreadController;
import processing.core.PApplet;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class ResourceLoaderThread {

    final Queue<Resource> resourceQueue;

    public ResourceLoaderThread() {
        resourceQueue = new LinkedList<>();
    }

    public void addItem(Resource r) {
        resourceQueue.add(r);
    }

    private void msg(String msg) {
        PApplet.println("[ResourceManager] : " + msg);
    }

    public void start(File root) {
        ThreadController.getInstance().startNewThread(
                new TaskThread(
                        () -> {
                            while (!resourceQueue.isEmpty()) {
                                resourceQueue.poll().LoadResource(root);

                            }
                        },
                        "ResourceLoaderThread"
                )
        );
    }

}
