package base3D.ui.core;

import base3D.definitions.Renderable;
import base3D.definitions.Updatable;
import processing.core.PGraphics;

public abstract class UIElement implements Renderable, Updatable {
    //-----------------------------------------
    // Attributes:
    private boolean active;
    private boolean visible;
    private Area area;
    private UIElement parent;

    //-----------------------------------------
    // Constructors:
    public UIElement(Area area) {
        this.area = area;
        this.active = true;
        this.visible = true;
        this.parent = null;
    }

    public UIElement(Area area, boolean active, boolean visible) {
        this.area = area;
        this.active = active;
        this.visible = visible;
        this.parent = null;
    }

    //copy
    //------------------------
    public UIElement(UIElement other) {set(other);}

    public void set(UIElement other) {
        this.area    = other.getAreaCp();
        this.active  = other.isActive();
        this.visible = other.isVisible();
        this.parent = other.getParent();
    }
    //------------------------


    //-----------------------------------------
    // Methods:
    public void renderElement(PGraphics canvas, float y0) {if (visible) render(canvas);}
    public abstract void render(PGraphics canvas);

    public void updateElement() {if (active) update();}
    public void update() {
        getAreaRef().update();
    }

    public String getUIElementInfo() {return "[" +(visible ? "V" : "_")+(active? "A" : "_") +  "]";}

    //-----------------------------------------
    // Getters & Setters:

    public float getWidth()  {return area.size.xCoord.get();}
    public float getHeight() {return area.size.xCoord.get();}

    public void setActive(boolean b) {this.active = b;}
    public void setVisible(boolean b) {this.visible = b;}

    public boolean isActive() {return active;}
    public boolean isVisible() {return visible;}

    public Area getAreaRef() {return area;}
    public Area getAreaCp() {return area.copy();}

    public void setAreaRef(Area area) {this.area = area;}
    public void setAreaCp(Area area) {this.area = area.copy();}

    public UIElement getParent() {
        return parent;
    }

    public void setParent(UIElement parent) {
        this.parent = parent;
    }

    public void setActiveAndVisible(boolean b) {
        active = b;
        visible = b;
    }
}