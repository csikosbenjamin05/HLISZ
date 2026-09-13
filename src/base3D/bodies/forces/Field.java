package base3D.bodies.forces;

import base3D.bodies.RectangularPrism;
import base3D.shapeLibrary.RectangularPrismShapeGenerator;
import base3D.shapeLibrary.brushes.BrushCollection;
import processing.core.PVector;

public class Field extends RectangularPrism {
    private final PVector dimensions;
    private final PVector corner1;
    private final PVector corner2;
    public Field(PVector pos, PVector dimensions) {
        super(pos);

        this.dimensions = new PVector();
        this.corner1 = new PVector();
        this.corner2 = new PVector();

        RectangularPrismShapeGenerator rectGen = new RectangularPrismShapeGenerator();
        rectGen.brush.set(BrushCollection.defaultMeshBrush);
        rectGen.dimensions.set(dimensions);

        setRectGen(rectGen);
        setDimension(dimensions);
    }


    public PVector getDimensions() {
        return dimensions;
    }

    public PVector getCorner1() {
        return corner1;
    }

    public PVector getCorner2() {
        return corner2;
    }

    public void setDimension(PVector dim2) {
        dimensions.set(dim2);
        corner1.set(dim2).mult(-0.5f).add(getPosition().value);
        corner2.set(dim2).mult(0.5f).add(getPosition().value);

        reGenerateElement();
    }

    public boolean isObjectInside(PVector coordinate) {
        return  coordinate.x > corner1.x && coordinate.y > corner1.y && coordinate.z > corner1.z
                && coordinate.x < corner2.x && coordinate.y < corner2.y && coordinate.z < corner2.z;
    }
}
