package base3D.ui;

import base3D.ui.core.*;
import processing.core.PVector;

public class ImageButton extends Image implements Clickable {


    private Runnable clickAction = (PVector p) -> {};

    public ImageButton(Area area) {
        super(area);
    }

    public ImageButton(Area area, String imgName) {
        super(area, imgName);
    }

    public ImageButton(Image imgObj) {
        super(imgObj);
    }



    @Override
    public void clicked(PVector clickLocation) {
        if(isActive() && getAreaRef().isInside(clickLocation)) clickAction.run(clickLocation);
    }

    @Override
    public Runnable getClickAction() {
        return clickAction;
    }

    @Override
    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }
}
