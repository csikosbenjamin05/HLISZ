package base3D.ui.core;

import base3D.ENV;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Area {
    //-----------------------------------------
    // Attributes:
    public UIVector position;
    public UIVector size;
    public Area container;
    public UIVector offSet;

    public UIVector cachedPosition; // Absolute

    //-----------------------------------------
    // Constructors:

    public Area(UIVector position, UIVector size, UIVector offSet) {
        this.container = null;
        this.cachedPosition = new UIVector(0,0);

        this.position = position;
        this.size = size;
        this.offSet = offSet;
    }

    public Area(UIVector position, UIVector size) {this(position,size,new UIVector(0,0));}

    public Area(Area container, UIVector position, UIVector size, UIVector offSet) {
        this(position, size, offSet);
        this.container = container;
    }

    public Area(Area container, UIVector position, UIVector size) {this(container,position,size,new UIVector(0,0));}

    //------------------------
    public Area(Area other) {set(other);}
    public Area copy() {return new Area(this);}

    public void set(Area other) {
        this.position  = other.position.copy();
        this.size      = other.size.copy();
        this.offSet    = other.offSet.copy();
        this.container = other.container; // ref
    }
    //------------------------


    //-----------------------------------------
    // Methods:

    public void update() {
        position.update();
        size.update();
        offSet.update();

        cachedPosition.xCoord.set(position.cacheX + offSet.cacheX); // absolute
        cachedPosition.yCoord.set(position.cacheY + offSet.cacheY); // absolute

        cachedPosition.update();
    }

    public boolean isInside(PVector coord) {
        return  coord.x >= cachedPosition.cacheX &&  coord.x <= cachedPosition.cacheX + size.cacheX &&
                coord.y >= cachedPosition.cacheY &&  coord.y <= cachedPosition.cacheY + size.cacheY;
    }


    public void drawOutline(PGraphics canvas) {
        canvas.fill(ENV.p.color(200));
        canvas.stroke(0xFF0000);
        canvas.strokeWeight(16);
        canvas.rect(cachedPosition.cacheX, cachedPosition.cacheY, size.cacheX, size.cacheY);
    }


    @Override
    public String toString() {
        return "Area(container:"+ container +"\tpos:" + position + "\tsize:" + size +  ")";
    }


    //-----------------------------------------
    // Getters & Setters:

    public void setContainer(Area aa) {
        this.container = aa.getContainerRef();
        this.position.setReference(aa.position);
        this.size.setReference(aa.size);
    }

    public Area getContainerRef() {return container;}

    public void setSizeAbsolute(float x, float y) {
        size.xCoord.set(x);
        size.yCoord.set(y);
    }
}
