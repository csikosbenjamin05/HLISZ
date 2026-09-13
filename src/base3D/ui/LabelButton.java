package base3D.ui;

import base3D.shapeLibrary.brushes.ShapeBrush;
import base3D.ui.core.*;
import processing.core.PVector;

public class LabelButton extends Label implements Clickable {

    // Attributes
    private Runnable clickAction = (PVector p) -> {};


    // Constructors
    public LabelButton(Area area) {
        super(area);
    }

    public LabelButton(Area area, String strText) {
        super(area, strText);
    }

    public LabelButton(Area area, ShapeBrush brush) {
        super(area, brush);
    }

    public LabelButton(Label lb) {
        super(lb);
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
