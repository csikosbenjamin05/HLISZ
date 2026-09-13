package base3D.threads.taskThreads;

import base3D.shapeLibrary.ShapeGenerator;
import base3D.shapeLibrary.ShapeHolder;
import base3D.threads.ThreadController;

public class ShapeGeneratorThread {

    public static void generate(ShapeHolder shapeHolder, ShapeGenerator shapeGen) {

        if (shapeGen == null) throw new RuntimeException("[ShapeGeneratorThread] Parameter shapeGen can't be null.");

        ThreadController.getInstance().startNewThread(
                new TaskThread(
                        () -> shapeHolder.setShape(shapeGen.generateShape()),
                        "ShapeGeneratorThread"
                )
        );
    }
    private ShapeGeneratorThread() {}

}