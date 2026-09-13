package base3D.ui;

import base3D.resources.ResourceManager;
import base3D.definitions.ResourceUser;
import base3D.resources.Texture;
import base3D.ui.core.*;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class CheckBox extends UIElement implements Clickable, ResourceUser {

    //-----------------------------------------
    // Attributes:
    private Texture state1Image; // state  :  true
    private Texture state2Image; // state  :  false
    private boolean state;


    //-----------------------------------------
    // Constructors:

    public CheckBox(Area area, String state1Image, String state2Image) {
        super(area);
        setState1Image(state1Image);
        setState2Image(state2Image);
        this.state = true;
    }

    //copy
    //------------------------
    public CheckBox(CheckBox other) {super(other.getAreaCp()); set(other);}

    public void set(CheckBox other) {
        this.set((UIElement) other);
        this.state1Image = other.getState1ImageRef();
        this.state2Image = other.getState2ImageRef();
        this.state = other.getState();
        this.clickAction = other.getClickAction();
    }
    //------------------------



    //-----------------------------------------
    // Methods:

    @Override
    public void render(PGraphics canvas) {
        UIVector position = getAreaRef().cachedPosition;
        UIVector size = getAreaRef().size;

        Texture img = (state) ? state1Image : state2Image;

        canvas.image(img.get(), position.cacheX, position.cacheY, size.cacheX, size.cacheY);
    }


    public void changeState() {
        state = ! state;
        matchImageSize();
    }

    public void matchImageSize() {
        PImage activeImage = ((state) ? state1Image : state2Image).get();

        UIVector size = getAreaRef().size;

        size.ensureXYAreAbsolute();

        size.xCoord.set(activeImage.width);
        size.yCoord.set(activeImage.height);
    }


    //-----------------------------------------
    // Getters & Setters:

    public boolean getState() {return state;}
    public Texture getState1ImageRef() {return state1Image;}
    public Texture getState2ImageRef() {return state2Image;}

    public void setState1Image(String str) {this.state1Image = ResourceManager.getTexture(str, this::reGenerateElement);}
    public void setState2Image(String str) {this.state2Image = ResourceManager.getTexture(str, this::reGenerateElement);}


    @Override
    public void reGenerateElement() {
        matchImageSize();
    }




    private Runnable clickAction = (PVector p) -> {};
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
