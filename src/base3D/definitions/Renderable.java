package base3D.definitions;

import processing.core.PGraphics;

public interface Renderable {

    void renderElement(PGraphics canvas, float y0); // runs at every update,  bVisible? -> True: render()

    void setVisible(boolean b);
    boolean isVisible();

}
