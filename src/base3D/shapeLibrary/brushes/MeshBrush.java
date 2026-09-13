package base3D.shapeLibrary.brushes;

import base3D.ENV;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

public class MeshBrush extends ShapeBrush {

    //-----------------------------------------
    // Attributes: (inherited)


    //-----------------------------------------
    // Constructors:
    public MeshBrush() {
        this.borderColor = ENV.p.color(255,0,0);
        this.borderSize = 3;
        this.borderCap  = ENV.p.ROUND;
        this.borderJoin = ENV.p.MITER;

        this.drawFace = false;
        this.drawBorder = true;
    }

    public MeshBrush(int borderColor) {
        this();
        this.borderColor = borderColor;
    }

    public MeshBrush(int borderColor, int borderSize) {
        this();
        this.borderColor = borderColor;
        this.borderSize = borderSize;
    }

    public MeshBrush(int borderColor, int borderSize, int borderCap, int borderJoin) {
        this.borderColor = borderColor;
        this.borderSize = borderSize;
        this.borderCap  = borderCap;
        this.borderJoin = borderJoin;
    }

    //------------------------
    public MeshBrush(MeshBrush other) {set(other);}
    public MeshBrush copy() {return new MeshBrush(this);}

    public void set(MeshBrush other) {
        this.borderColor = other.borderColor;
        this.borderSize  = other.borderSize;
        this.borderCap   = other.borderCap;
        this.borderJoin  = other.borderJoin;
    }

    //-----------------------------------------
    // Methods:

    // There is no interface that groups the graphical settings together so ... I have 3 methods with the same code :)
    @Override
    public void setPApplet(PApplet e) {
        e.noFill();
        e.stroke(borderColor);
        e.strokeWeight(borderSize);
        e.strokeCap(borderCap);
        e.strokeJoin(borderJoin);
    }

    @Override
    public void setPShape(PShape e) {
        e.noFill();
        e.stroke(borderColor);
        e.strokeWeight(borderSize);
        e.strokeCap(borderCap);
        e.strokeJoin(borderJoin);
    }

    @Override
    public void setPGraphics(PGraphics e) {
        e.noFill();
        e.stroke(borderColor);
        e.strokeWeight(borderSize);
        e.strokeCap(borderCap);
        e.strokeJoin(borderJoin);
    }
    public void setLocalEnvironment() {setPApplet(ENV.p);}



    @Override
    public String toString() {
        return "MeshBrush["+  "bc:" + borderColor + "  bs:" + borderSize + " bCap:" + borderCap + "  bj:" + borderJoin + ")";
    }
}
