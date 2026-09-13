package base3D.shapeLibrary;

import base3D.ENV;
import base3D.definitions.ResourceUser;
import base3D.shapeLibrary.brushes.MeshBrush;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

import static processing.core.PApplet.min;

public class JaggedLineShapeGenerator extends ShapeGenerator {

    public final MeshBrush meshBrush;
    private final PVector[] points;
    public boolean closed;
    private final int lengthM1;
    private int start;
    private int entries;

    public JaggedLineShapeGenerator(ResourceUser parent, int length) {
        super(parent);
        points = new PVector[length];

        for (int i = 0; i < length; i++)
            points[i] = new PVector();

        this.lengthM1 = length - 1;
        paintWithTexture = false;
        meshBrush = new MeshBrush();
        closed = true;
        start = 0;

        entries = 0;
    }

    public JaggedLineShapeGenerator(ResourceUser parent, MeshBrush meshBrush, int length) {
        this(parent, length);
        this.meshBrush.set(meshBrush);
    }
    public JaggedLineShapeGenerator(ResourceUser parent, PVector[] arr) {
        this(parent, arr.length);
        copyPointData(arr);
    }

    public JaggedLineShapeGenerator(ResourceUser parent, MeshBrush meshBrush, PVector[] arr) {
        this(parent, meshBrush, arr.length);
        copyPointData(arr);
    }

    private void copyPointData(PVector[] source) {
        for (int i = 0; i <= lengthM1; i++) points[i].set(source[i]);
    }

    public void addPoint(float x, float y, float z) {

        // replace the oldest point
        points[start].set(x,y,z);

        // move the start to the second-oldest point
        if (start == lengthM1)
            start = 0;
        else
            start++;


        entries++;
    }

    public void addPoint(PVector p) {
        addPoint(p.x, p.y, p.z);
    }

    @Override
    PShape generateTexturelessShape() {
        PShape shape = ENV.p.createShape();

        shape.beginShape(PConstants.LINE);
        meshBrush.setPShape(shape);
        shape.fill(ENV.p.color(0,255,0));

        PVector t;
            if (lengthM1 > 0) {
                for (int i = start; i < lengthM1; i++) {
                    t = points[i];
                    shape.vertex(t.x, t.y, t.z);
                }

                for (int i = 0; i < start; i++) {
                    t = points[i];
                    shape.vertex(t.x, t.y, t.z);
                }
            }



        if (closed)
            shape.endShape(PConstants.CLOSE);
        else
            shape.endShape();


        return shape;
    }

    public void render(PGraphics canvas, float y0) {

        meshBrush.setPGraphics(canvas);

        //System.out.println("DRAWING");
        PVector t, pt;
        if (lengthM1 > 0) {
            if (entries > lengthM1) {
                for (int i = start + 1; i <= lengthM1; i++) {
                    t = points[i];
                    pt = points[i - 1];
                    canvas.line(pt.x, pt.y - y0, pt.z, t.x, t.y - y0, t.z);
                    //System.out.println(i-1 + " " + i);
                }


                if (start != lengthM1 && start != 0) {
                    t = points[0];
                    pt = points[lengthM1];
                    canvas.line(pt.x, pt.y - y0, pt.z, t.x, t.y - y0, t.z);
                }
            }
            for (int i = 1; i < start; i++) {
                t = points[i];
                pt = points[i - 1];
                canvas.line(pt.x, pt.y - y0, pt.z, t.x, t.y - y0, t.z);
                //System.out.println(i-1 + " " + i);
            }

        }
        //System.out.println();

    }

    public PVector[] getPoints() {
        return points;
    }

    @Override
    public void setShapeHolderOffset(ShapeHolder sh) {
        sh.setOffSet(0); // the start of the line is at the first point
    }

    @Override
    PShape generateTexturedShape() { // never called
        throw new RuntimeException("Something is wrong..");
    }

    public void clearPoints() {
        start = 0;
        entries = 0;
    }
}
