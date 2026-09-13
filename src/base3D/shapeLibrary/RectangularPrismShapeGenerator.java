package base3D.shapeLibrary;

import base3D.ENV;
import base3D.definitions.ResourceUser;
import base3D.resources.ResourceManager;
import base3D.shapeLibrary.brushes.ShapeBrush;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;

public class RectangularPrismShapeGenerator extends ShapeGenerator {

    public enum Side {
        TOP,
        BOTTOM,
        FRONT,
        BACK,
        LEFT,
        RIGHT
    }

    public Map<Side, Boolean> renderSides;

    public final ShapeBrush brush;

    public PVector dimensions;

    ////////////////////////////
    // Constructors ->
    public RectangularPrismShapeGenerator(ResourceUser parent) {
        super(parent, false);

        this.brush = new ShapeBrush();
        brush.faceColor = ENV.p.color(100, 0, 0);
        brush.borderColor = ENV.p.color(0, 255, 0);
        brush.drawBorder = true;
        brush.drawFace = true;



        dimensions = new PVector(100,100,100);

        renderSides = new HashMap<>();
        for (Side s : Side.values()) renderSides.put(s, true);
    }

    public RectangularPrismShapeGenerator() {
        this((ResourceUser) null);
    }

    public RectangularPrismShapeGenerator(ResourceUser parent, PVector dimensions) {
        this(parent);
        this.dimensions.set(dimensions);
    }

    public RectangularPrismShapeGenerator(PVector dimensions) {
        this(null, dimensions);
    }

    public RectangularPrismShapeGenerator(ResourceUser parent, PVector dimensions, String texture) {
        this(parent, dimensions);

        texture_name = texture;

        paintWithTexture = true;
    }

    public RectangularPrismShapeGenerator(PVector dimensions, String texture) {
        this(null, dimensions, texture);
    }

    // copy constructor
    public RectangularPrismShapeGenerator(RectangularPrismShapeGenerator rectPGen) {
        this(rectPGen.getParent());
        set(rectPGen);
    }

    public void set(RectangularPrismShapeGenerator rectPGen) {
        set((ShapeGenerator) rectPGen);

        this.brush.set(rectPGen.brush);
        this.dimensions = rectPGen.dimensions;

        this.renderSides = new HashMap<>();
        for (Side s : Side.values()) renderSides.put(s, rectPGen.renderSides.get(s));
    }

    ////////////////////////////
    // Generators ->
    private PShape generateTexturedSide(PImage texture, PVector v1, PVector v2, PVector v3, PVector v4, int[] uvs) {
        PShape side = ENV.p.createShape();
        side.beginShape(ENV.p.QUADS);
        side.normal(0, 1, 0);
        side.noStroke();
        side.texture(texture);
        side.vertex(v1.x, v1.y, v1.z, uvs[0], uvs[1]);
        side.vertex(v2.x, v2.y, v2.z, uvs[2], uvs[3]);
        side.vertex(v3.x, v3.y, v3.z, uvs[4], uvs[5]);
        side.vertex(v4.x, v4.y, v4.z, uvs[6], uvs[7]);
        side.endShape();

        return side;
    }

    private PShape generateTextureLessSide(PVector v1, PVector v2, PVector v3, PVector v4) {
        PShape side = ENV.p.createShape();
        side.beginShape(ENV.p.QUADS);
        side.normal(0,1,0);

        brush.setPShape(side);

        side.vertex(v1.x, v1.y, v1.z);
        side.vertex(v2.x, v2.y, v2.z);
        side.vertex(v3.x, v3.y, v3.z);
        side.vertex(v4.x, v4.y, v4.z);
        side.endShape();

        return side;
    }

    protected PVector[] getCorners() {
        PVector[] corners = new PVector[8];
        corners[0] = new PVector(0,0,0);
        corners[1] = new PVector(0, dimensions.y,0);
        corners[2] = new PVector(dimensions.x, dimensions.y,0);
        corners[3] = new PVector(dimensions.x,0,0);

        corners[4] = new PVector(0,0, dimensions.z);
        corners[5] = new PVector(0, dimensions.y, dimensions.z);
        corners[6] = new PVector(dimensions.x, dimensions.y, dimensions.z);
        corners[7] = new PVector(dimensions.x,0, dimensions.z);

        return corners;
    }

    PShape generateTexturedShape() {
        // the corners:

        PImage texture = ResourceManager.getTexture(texture_name, parent::reGenerateElement).get();

        PVector[] corners = getCorners();

        PVector p1 = corners[0];
        PVector p2 = corners[1];
        PVector p3 = corners[2];
        PVector p4 = corners[3];
        PVector p5 = corners[4];
        PVector p6 = corners[5];
        PVector p7 = corners[6];
        PVector p8 = corners[7];

        int sideLength = texture.height;
        PImage[] sideImages = new PImage[6];
        for (int i = 0; i < 6; i++) {
            sideImages[i] = texture.get(sideLength * i, 0, sideLength * (i+1) - 1, sideLength - 1);
        }

        PShape sBox = ENV.p.createShape(ENV.p.GROUP);
        // ------------ SIDES ------------
        // 1,2,3,4
        sBox.addChild(generateTexturedSide(
                sideImages[3], p1, p2, p3, p4,
                new int[] {0, 0,   0, sideLength,   sideLength, sideLength,   sideLength, 0}
        ));

        //1,2,6,5
        sBox.addChild(generateTexturedSide(
                sideImages[2], p1, p2, p6, p5,
                new int[] {sideLength, 0,   sideLength, sideLength,   0, sideLength,   0, 0}
        ));

        //6,5,8,7
        sBox.addChild(generateTexturedSide(
                sideImages[1], p6, p5, p8, p7,
                new int[] {sideLength, sideLength,   sideLength, 0,   0, 0,   0, sideLength}
        ));

        //8,7,3,4
        sBox.addChild(generateTexturedSide(
                sideImages[0], p8, p7, p3, p4,
                new int[] {sideLength, 0,   sideLength, sideLength,   0, sideLength,   0, 0}
        ));

        //1,4,8,5
        sBox.addChild(generateTexturedSide(
                sideImages[4], p1, p4, p8, p5,
                new int[] {sideLength, 0,   0, 0,   0, sideLength,   sideLength, sideLength}
        ));

        //2,3,7,6
        sBox.addChild(generateTexturedSide(
                sideImages[5], p2, p3, p7, p6,
                new int[] {sideLength, sideLength,   0, sideLength,   0, 0,   sideLength, 0}
        ));

        return sBox;
    }

    PShape generateTexturelessShape() {
        // the corners:

        PVector[] corners = getCorners();

        PVector p1 = corners[0];
        PVector p2 = corners[1];
        PVector p3 = corners[2];
        PVector p4 = corners[3];
        PVector p5 = corners[4];
        PVector p6 = corners[5];
        PVector p7 = corners[6];
        PVector p8 = corners[7];

        PShape sBox = ENV.p.createShape(ENV.p.GROUP);
        // ------------ SIDES ------------
        // 1,2,3,4
        sBox.addChild(generateTextureLessSide(p1, p2, p3, p4));

        //1,2,6,5
        sBox.addChild(generateTextureLessSide(p1, p2, p6, p5));

        //6,5,8,7
        sBox.addChild(generateTextureLessSide(p6, p5, p8, p7));

        //8,7,3,4
        sBox.addChild(generateTextureLessSide(p8, p7, p3, p4));

        //1,4,8,5
        sBox.addChild(generateTextureLessSide(p1, p4, p8, p5));

        //2,3,7,6
        sBox.addChild(generateTextureLessSide(p2, p3, p7, p6));

        return sBox;
    }

    @Override
    public void setShapeHolderOffset(ShapeHolder sh) {
        sh.setOffSet(dimensions.x/2, dimensions.y/2, dimensions.x/2);
    }
}
