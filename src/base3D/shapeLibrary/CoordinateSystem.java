package base3D.shapeLibrary;

import base3D.ENV;
import base3D.definitions.ResourceUser;
import processing.core.PGraphics;
import processing.core.PShape;

import static processing.core.PApplet.*;

public class CoordinateSystem extends ShapeGenerator{
    // dimension == 2 -> x,y
    // dimension == 3 -> complex numbers,  realOfZ ? -> x,y,z + color

    private float f(float x) {
        try {
            return x - (int)x;
        } catch (Exception ignored) {}
        return 0f;
    }

    private float f(float x, float y) {
        try {
            return distance(x + (float)Math.sin(y) * 5, y);
            //return -(float)Math.sqrt(256 - x*x - y*y);
            //return (float)(Math.sin(distance(x,y)) * Math.cos(distance(x,y)*2));
            // return sin((x * y) * 0.1) * 5 + tan(pow(distance(x,y),1.1)) * 10; // gpu killer
            // return x + y;

        } catch (Exception e) {}
        return 0f;
    }


    private double distance(double a, double b) {
        return Math.sqrt(a*a + b*b);
    }

    private float distance(float a, float b) {
        return (float)Math.sqrt(a*a + b*b);
    }


    private int dimension;
    private boolean showAxes;
    private float unit;
    private int scale; // unit -> x pixels
    private int[] bounds; // x lower, x higher, y lower, y higher bound

    private boolean enableText = true;
    private int precision = 1;

    public int textColor = ENV.p.color(50,0,50);
    public int axesColor = ENV.p.color(255,0,0);
    public int mapColor = ENV.p.color(0,200,0);

    public int textSize = 100;
    public int spacing = 50;

    public CoordinateSystem(ResourceUser parent) {
        super(parent, true);
        this.dimension = 2;
        this.unit = 1f;
        this.scale = 500;
        this.bounds = new int[] {-10, 10, -10, 10};
    }


    public CoordinateSystem(ResourceUser parent, int dimension, float unit) {
        this(parent);
        this.dimension = dimension;
        this.unit = unit;
        this.scale = 500;
        if (dimension == 2) {
            this.bounds = new int[] {-10, 10, -10, 10};
        } else {
            this.bounds = new int[] {-10, 10, -10, 10, -10, 10};
        }

        
    }

    public CoordinateSystem(int dimension, float unit) {
        this(null, dimension, unit);
    }

    public void setBounds(int x0, int x1, int y0, int y1, int z0, int z1) {
        bounds = new int[] {x0, x1, y0, y1, z0, z1};
    }

    public void setPrecision(int p) {
        precision = p;
    }
    public int getDimension() {return dimension;}

    public void setShowAxes(boolean b) {showAxes = b;}
    public boolean getShowAxes() {return showAxes;}

    // Render text for the titles 
    private PGraphics textToImage(String s, int tSize, int tColor) {
        PGraphics pg = ENV.p.createGraphics((int)(s.length() * tSize*0.6), tSize, P2D);
        pg.beginDraw();
            pg.fill(tColor);
            pg.textSize(tSize);
            pg.text(s, 0, tSize/1.1f);
        pg.endDraw();

        return pg;
    }

    // Generate an axel on the xy plane
    private PShape generateAxeXY(int b0, int b1, String id) {
        PShape axe = ENV.p.createShape(GROUP);
        PShape lines = ENV.p.createShape();

        int start = b0 * scale;
        int end   = b1 * scale;

        lines.beginShape(LINES);
            lines.stroke(axesColor);

            lines.vertex(start,0,0);
            lines.vertex(end,0,0);

            for (int i = start; i <= end; i+=scale) {
                lines.vertex(i,-spacing/2f, 0);
                lines.vertex(i, spacing/2f, 0);
            }
        lines.endShape();
        axe.addChild(lines);
        if (enableText) {
            PShape number;
            PGraphics n;
            int nw, nh;
            for (int i = start; i <= end; i+=scale) {
                number = ENV.p.createShape();
                number.beginShape(QUAD);
                    number.noStroke();
                    n = textToImage((unit * (i / scale)) + id, textSize, textColor);
                    nw = n.width/2;
                    nh = n.height/2;
                    number.texture(n);


                    number.vertex(i-nw, 0, 0, 0, 0);
                    number.vertex(i + nw , 0, 0, n.width, 0);
                    number.vertex(i + nw, nh, 0, n.width, n.height);
                    number.vertex(i -nw, nh, 0, 0, n.height);

                number.endShape(CLOSE);
                axe.addChild(number);
            }
        }

        return axe;
    }

    // Generate an axel on the yz plane
    private PShape generateAxeYZ(int b0, int b1, String id) {
        PShape axe = ENV.p.createShape(GROUP);
        PShape lines = ENV.p.createShape();

        int start = b0 * scale;
        int end   = b1 * scale;

        lines.beginShape(LINES);
            lines.stroke(axesColor);

            lines.vertex(0, start, 0);
            lines.vertex(0, end,   0);

            for (int i = start; i <= end; i+=scale) {
                lines.vertex(0, i,-spacing/2f);
                lines.vertex(0, i, spacing/2f);
            }
        lines.endShape();
        axe.addChild(lines);

        if (enableText) {
            PShape number;
            PGraphics n;
            int nw, nh;
            for (int i = start; i <= end; i+=scale) {
                number = ENV.p.createShape();
                number.beginShape(QUAD);
                    number.noStroke();
                    n = textToImage((-unit * (i / scale)) + id, textSize, textColor);
                    nw = n.width/2;
                    nh = n.height/2;
                    number.texture(n);


                    number.vertex(0, i-nw, 0, 0, 0);
                    number.vertex(0, i + nw, 0, n.width, 0);
                    number.vertex(0, i + nw, nh, n.width, n.height);
                    number.vertex(0, i -nw, nh, 0, n.height);

                number.endShape(CLOSE);
                axe.addChild(number);
            }
        }
        return axe;
    }
    
    // generate the axes based on the dimension:
    private PShape generateAxes() {
        PShape axes = ENV.p.createShape(GROUP);
        PShape a1, a2, a3;
        
        a1 = generateAxeXY(bounds[0], bounds[1], "X");
        axes.addChild(a1);

        a3 = generateAxeYZ(bounds[2], bounds[3], "Y");
        axes.addChild(a3);
        
        if( dimension > 2) {
            a2 = generateAxeXY(bounds[4], bounds[5], (dimension == 3) ? "y" : "i");
            a2.rotateY(HALF_PI);
            axes.addChild(a2);

        }
    
        return axes;
    }


    // get the function's value at each point: 
    private PShape graphFunction() {
        PShape shape = ENV.p.createShape(GROUP);
        if (showAxes) shape.addChild(generateAxes());

        float b0 = bounds[2] * unit;
        float b1 = bounds[3] * unit;

        PShape map = ENV.p.createShape();

        long timer;

        int startLoop1 = bounds[0] * precision;
        int startLoop2 = bounds[4] * precision;

        int endLoop1 = bounds[1] * precision;
        int endLoop2 = bounds[5] * precision;

        float bVal,  aVal;
        float c = unit / precision;

        float boundZUnit0 = bounds[4] * unit;
        float boundZUnit1 = bounds[5] * unit;
        float scaleUnit = scale * unit;
        float precisionFloat = precision;

        int aOff, bOff;

        switch (dimension) {
            case 2 -> { // x -> y
                float y;
                float value;
                System.out.println("Graphing started");
                timer = System.nanoTime();
                map.beginShape();
                map.strokeWeight(3);
                map.noFill();
                map.stroke(mapColor);
                for (int n = startLoop1; n < endLoop1; n++) {
                    value = ((float) n) / precision;
                    y = max(min(-f(value), b1), b0);
                    map.vertex(value * scale, y * scale);
                }
                map.endShape();
                System.out.println("Graphing ended (" + (System.nanoTime() - timer) / 1000000 + " ms)\n");
            }
            case 3 -> { // x,y -> n
                float[][] n = new float[(bounds[1] - bounds[0]) * precision + 1][(bounds[5] - bounds[4]) * precision + 1];
                System.out.println("Value calc started");
                timer = System.nanoTime();
                for (int a = startLoop1; a <= endLoop1; a++) {
                    for (int b = startLoop2; b <= endLoop2; b++) {
                        aVal = (float) a * c;
                        bVal = (float) b * c;

                        n[a - startLoop1][b - startLoop2] = max(min(f(aVal, bVal), b1), b0);
                    }
                }
                System.out.println("Value calc finished (" + (System.nanoTime() - timer) / 1000000 + " ms)\n");
                System.out.println("Shape calc started");
                timer = System.nanoTime();
                map.beginShape(QUADS);
                //map.strokeWeight(1);
                //map.stroke(200);
                map.noStroke();
                for (int a = 1; a < n.length; a++) {
                    for (int b = 1; b < n[0].length; b++) {
                        aOff = a - 1;
                        bOff = b - 1;
                        map.fill(map(n[a][b], boundZUnit0, boundZUnit1, 255, 0), 0, 0);

                        map.vertex(
                                (a / precisionFloat + bounds[0]) * scaleUnit,
                                n[a][b] * scale,
                                (b / precisionFloat + bounds[4]) * scaleUnit
                        );
                        map.vertex(
                                (aOff / precisionFloat + bounds[0]) * scaleUnit,
                                n[aOff][b] * scale,
                                (b / precisionFloat + bounds[4]) * scaleUnit
                        );
                        map.vertex(
                                (aOff / precisionFloat + bounds[0]) * scaleUnit,
                                n[aOff][bOff] * scale,
                                (bOff / precisionFloat + bounds[4]) * scaleUnit
                        );
                        map.vertex(
                                (a / precisionFloat + bounds[0]) * scaleUnit,
                                n[a][bOff] * scale,
                                (bOff / precisionFloat + bounds[4]) * scaleUnit
                        );

                    }
                }
                map.endShape(CLOSE);
                println("Shape calc finished (" + (System.nanoTime() - timer) / 1000000 + " ms)\n\n");
            }
        }
        shape.addChild(map);

        return shape;
    }

    @Override
    PShape generateTexturedShape() {
        return graphFunction();
    }

    @Override
    PShape generateTexturelessShape() {
        return null;
    }

    @Override
    public void setShapeHolderOffset(ShapeHolder sh) {

    }
}