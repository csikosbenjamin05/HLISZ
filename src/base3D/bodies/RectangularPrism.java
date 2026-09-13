package base3D.bodies;

import base3D.Useful;
import base3D.definitions.GeneratedShape;
import base3D.definitions.ResourceUser;
import base3D.shapeLibrary.RectangularPrismShapeGenerator;
import processing.core.PVector;

public class RectangularPrism extends SimpleBody implements ResourceUser, GeneratedShape {


    private RectangularPrismShapeGenerator rectGen;

    //-----------------------------------------
    // Constructors

    public RectangularPrism(PVector pos) {
        super(pos);
    }
    public RectangularPrism(PVector pos, RectangularPrismShapeGenerator rectGen) {
        super(pos);
        setRectGen(rectGen);
        generateOnAnotherThread();
    }

    public RectangularPrism(PVector pos, PVector dimensions) {
        this(pos, new RectangularPrismShapeGenerator(null, dimensions));
    }

    public RectangularPrism(PVector pos, String texture, PVector dimensions) {
        this(pos, new RectangularPrismShapeGenerator(null, dimensions, texture));
    }

    // copy constructor
    public RectangularPrism(RectangularPrism c) {
        super();
        set(c);
    }

    @Override
    public void setToSimpleBody(SpatialBody o) {set((RectangularPrism) o);}
    public void set(RectangularPrism c) {
        this.set((SimpleBody) c);
        this.rectGen = (this.rectGen == null) ? new RectangularPrismShapeGenerator(this) : this.rectGen;
        this.rectGen.set(c.getRectGen());
    }

    public RectangularPrism copy() {
        return new RectangularPrism(this);
    }

    //-----------------------------------------
    // Methods
    @Override
    public String toString() {
        return "RectangularPrism<" + Useful.objectToHex(this) + ">(Shape:" + getShapeHolder().toString() +
                "\n Movement:" + getPosition().toString() + "\n Rotation:" + getRotation().toString() + ")\n";
    }

    public void reGenerateElement() {
        generateOnAnotherThread();
    }

    //-----------------------------------------
    // Getters & Setters

    public void setRectGen(RectangularPrismShapeGenerator rectGen) {
        rectGen.setShapeHolderOffset(getShapeHolder());

        rectGen.setParent(this);
        this.rectGen = rectGen;
    }

    public void setDimension(PVector s) {
        rectGen.dimensions.set(s);}
    public PVector getDimension() {return rectGen.dimensions;}

    public RectangularPrismShapeGenerator getRectGen() {return rectGen;}


    @Override
    public void generateOnAnotherThread() {
        getShapeHolder().setShape(rectGen);
    }

    @Override
    public void generateOnMainThread() {
        getShapeHolder().setShape(rectGen.generateShape());
    }
}
