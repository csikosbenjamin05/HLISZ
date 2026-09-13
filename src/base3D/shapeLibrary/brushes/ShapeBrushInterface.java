package base3D.shapeLibrary.brushes;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

interface ShapeBrushInterface {

    // There is no interface that groups the graphical settings together so ... I have 3 methods with the same code :)
    void setPApplet(PApplet pApplet);
    void setPShape(PShape pShape);
    void setPGraphics(PGraphics pGraphics);

    void setLocalEnvironment();
}
