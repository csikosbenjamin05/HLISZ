package base3D.shapeLibrary;

import base3D.definitions.ResourceUser;
import processing.core.PVector;

public class CubeShapeGenerator extends RectangularPrismShapeGenerator {

    public int size;

    ////////////////////////////
    // Constructors ->
    public CubeShapeGenerator(ResourceUser parent) {
        super(parent);
        size = 100;
    }

    public CubeShapeGenerator() {
        this((ResourceUser) null);
    }

    public CubeShapeGenerator(ResourceUser parent, int size) {
        this(parent);
        this.size = size;
    }

    public CubeShapeGenerator(int size) {
        this(null, size);
    }

    public CubeShapeGenerator(ResourceUser parent, int size, String texture) {
        this(parent, size);

        texture_name = texture;

        paintWithTexture = true;
    }

    public CubeShapeGenerator(int size, String texture) {
        this(null, size, texture);
    }

    // copy constructor
    public CubeShapeGenerator(CubeShapeGenerator cubeGen) {
        this(cubeGen.getParent());
        set(cubeGen);
    }

    public void set(CubeShapeGenerator cubeGen) {
        set((RectangularPrismShapeGenerator) cubeGen);
        this.size = cubeGen.size;
    }

    ////////////////////////////
    // Generators :: defined in RectangularPrism
    @Override
    protected PVector[] getCorners() {
        PVector[] corners = new PVector[8];
        corners[0] = new PVector(0,0,0);
        corners[1] = new PVector(0,size,0);
        corners[2] = new PVector(size,size,0);
        corners[3] = new PVector(size,0,0);

        corners[4] = new PVector(0,0,size);
        corners[5] = new PVector(0,size,size);
        corners[6] = new PVector(size,size,size);
        corners[7] = new PVector(size,0,size);

        return corners;
    }

    public void setShapeHolderOffset(ShapeHolder sh) {
        sh.setOffSet(size/2f);
    }
}
