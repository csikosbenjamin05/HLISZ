package base3D.ui.core;

import processing.core.PVector;

public interface Clickable {
    interface Runnable {
        void run(PVector p);
    }

    void clicked(PVector clickLocation);


    Runnable getClickAction();
    void setClickAction(Runnable clickAction);

}
