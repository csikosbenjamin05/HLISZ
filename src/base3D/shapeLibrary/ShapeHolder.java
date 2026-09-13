package base3D.shapeLibrary;

import base3D.threads.taskThreads.ShapeGeneratorThread;
import base3D.Useful;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class ShapeHolder {
    private PShape shape;
    private final PVector offset;
    private final PVector scale;


    //-----------------------------------------
    //constructors
    public ShapeHolder() {
        this.shape = null;
        this.offset = new PVector(0,0,0);
        this.scale = new PVector(1,1,1);
    }

    public ShapeHolder(PVector offset) {
        this();
        this.offset.set(offset);
    }

    public ShapeHolder(PShape shape, PVector offset) {
        this(offset);
        this.shape = shape;
    }

    public ShapeHolder(PShape shape, PVector offset, PVector scale) {
        this(shape, offset);
        this.scale.set(scale);
    }

    // copy constructor
    public ShapeHolder(ShapeHolder sh) {
        this();
        this.set(sh);
    }

    public void set(ShapeHolder sh) {
        this.shape = sh.getShape();
        this.offset.set(sh.getOffsetRef());
        this.scale.set(sh.getScaleRef());
    }

    public ShapeHolder copy() {
        return new ShapeHolder(this);
    }

    //-----------------------------------------
    // Methods
    @Override
    public String toString() {
        return "ShapeHolder<" + Useful.objectToHex(this) + ">(Shape:" + Useful.getClassOfObject(shape) +
                " offset:" + Useful.pvectoString(offset) + " Scale:" + Useful.pvectoString(scale) + ")";
    }


    public void setScale(float x, float y, float z) {
        shape.scale(1/scale.x, 1/scale.y, 1/scale.z);
        shape.scale(x, y, z);
        scale.set(x, y, z);
    }
    public void setScale(PVector vec) {setScale(vec.x, vec.y, vec.z);}
    public void setScale(float value) {setScale(value, value, value);}

    private void render(PGraphics canvas) {
        if (shape != null) canvas.shape(shape); else {
            canvas.fill(255, 0, 255);
            canvas.stroke(0, 255, 0);
            canvas.box(100);
        }
    }

    public void renderPosRot(PGraphics canvas, float y0, PVector pos, PVector rot) {
        canvas.pushMatrix();
            canvas.translate(pos.x, y0 + pos.y, pos.z);
            canvas.rotateX(PApplet.radians(rot.x));
            canvas.rotateY(PApplet.radians(rot.y));
            canvas.translate(-offset.x, -offset.y, -offset.z);
            render(canvas);
        canvas.popMatrix();
    }


    //-----------------------------------------
    // Getters and setters

    public void setOffSet(PVector offset) {this.offset.set(offset);}
    public void setOffSet(float v) {this.offset.set(v, v, v);}
    public void setOffSet(float x, float y, float z) {this.offset.set(x, y, z);}

    public void setShape(PShape shape) {this.shape = shape;}

    public void setShape(ShapeGenerator shapeGen) {
        ShapeGeneratorThread.generate(this, shapeGen);
    }

    public PVector getOffsetRef() {return this.offset;}
    public PVector getOffsetCp() {return this.offset.copy();}
    public PVector getScaleRef() {return this.scale;}
    public PVector getScaleCp() {return this.scale.copy();}
    public PShape getShape() {return this.shape;}


}