package base3D.shapeLibrary.brushes;

import base3D.ENV;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

public class ShapeBrush implements ShapeBrushInterface {

    //-----------------------------------------
    // Attributes:
    public int     faceColor;
    public int     borderColor;
    public int     borderSize;
    public int     borderCap;
    public int     borderJoin;
    public boolean drawBorder;
    public boolean drawFace;


    //-----------------------------------------
    // Constructors:
    public ShapeBrush() {
        this.faceColor = ENV.p.color(100,100,100);
        this.borderColor = ENV.p.color(0,0,0);
        this.borderSize = 1;
        this.borderCap  = ENV.p.ROUND;
        this.borderJoin = ENV.p.MITER;
        this.drawBorder = true;
        this.drawFace = true;
    }

    public ShapeBrush(int faceColor, int borderColor) {
        this();
        this.faceColor = faceColor;
        this.borderColor = borderColor;
    }

    public ShapeBrush(int faceColor, int borderColor, int borderSize) {
        this();
        this.faceColor = faceColor;
        this.borderColor = borderColor;
        this.borderSize = borderSize;
    }

    public ShapeBrush(int faceColor, int borderColor, int borderSize, int borderCap, int borderJoin) {
        this.faceColor = faceColor;
        this.borderColor = borderColor;
        this.borderSize = borderSize;
        this.borderCap  = borderCap;
        this.borderJoin = borderJoin;
    }

    //------------------------
    public ShapeBrush(ShapeBrush other) {set(other);}
    public ShapeBrush copy() {return new ShapeBrush(this);}

    public void set(ShapeBrush other) {
        this.faceColor = other.faceColor;
        this.borderColor = other.borderColor;
        this.borderSize  = other.borderSize;
        this.borderCap   = other.borderCap;
        this.borderJoin  = other.borderJoin;
        this.drawFace = other.drawFace;
        this.drawBorder  = other.drawBorder;
    }
    //------------------------

    //-----------------------------------------
    // Methods:

    // There is no interface that groups the graphical settings together so ... I have 3 methods with the same code :)
    public void setPApplet(PApplet e) {
        if (drawBorder) {
            e.stroke(borderColor);
            e.strokeWeight(borderSize);
            e.strokeCap(borderCap);
            e.strokeJoin(borderJoin);
        } else e.noStroke();
        if (drawFace) e.fill(faceColor);
        else e.noFill();
    }
    public void setPShape(PShape e) {
        if (drawBorder) {
            e.stroke(borderColor);
            e.strokeWeight(borderSize);
            e.strokeCap(borderCap);
            e.strokeJoin(borderJoin);
        } else e.noStroke();
        if (drawFace) e.fill(faceColor);
        else e.noFill();
    }
    public void setPGraphics(PGraphics e) {
        if (drawBorder) {
            e.stroke(borderColor);
            e.strokeWeight(borderSize);
            e.strokeCap(borderCap);
            e.strokeJoin(borderJoin);
        } else e.noStroke();
        if (drawFace) e.fill(faceColor);
        else e.noFill();
    }
    public void setLocalEnvironment() {setPApplet(ENV.p);}

    @Override
    public String toString() {
        return "ShapeBrush["+ ((drawBorder) ? "B" : "") + ((drawFace)? "F" : "") +"](bgc:" + faceColor + "  bc:" + borderColor + "  bs:" + borderSize + " bCap:" + borderCap + "  bj:" + borderJoin + ")";
    }

    //-----------------------------------------
    // Getters & Setters:
}
