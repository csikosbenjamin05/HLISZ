package base3D.bodies;


import base3D.definitions.GeneratedShape;
import base3D.definitions.ResourceUser;
import base3D.shapeLibrary.CubeShapeGenerator;
import base3D.Useful;
import processing.core.PVector;

public class Cube extends SimpleBody implements ResourceUser, GeneratedShape {
    private CubeShapeGenerator cubeGen;

    //-----------------------------------------
    // Constructors
    public Cube(PVector pos, CubeShapeGenerator cubeGen) {
        super(pos);
        cubeGen.setShapeHolderOffset(getShapeHolder());

        cubeGen.setParent(this);
        this.cubeGen = cubeGen;

        generateOnAnotherThread();
    }

    public Cube(PVector pos, int size) {
        this(pos, new CubeShapeGenerator(null, size));
    }

    public Cube(PVector pos, String texture, int size) {
        this(pos, new CubeShapeGenerator(null, size, texture));
    }

    // copy constructor
    public Cube(Cube c) {
        super();
        set(c);
    }

    @Override
    public void setToSimpleBody(SpatialBody o) {set((Cube) o);}
    public void set(Cube c) {
        this.set((SimpleBody) c);
        this.cubeGen = (this.cubeGen == null) ? new CubeShapeGenerator(this) : this.cubeGen;
        this.cubeGen.set(c.getCubeGen());
    }

    public Cube copy() {
        return new Cube(this);
    }

    //-----------------------------------------
    // Methods
    @Override
    public String toString() {
        return "Cube<" + Useful.objectToHex(this) + ">(Shape:" + getShapeHolder().toString() +
                "\n Movement:" + getPosition().toString() + "\n Rotation:" + getRotation().toString() + ")\n";
    }

    public void reGenerateElement() {
        generateOnAnotherThread();
    }

    //-----------------------------------------
    // Getters & Setters

    public void setSize(int s) {cubeGen.size = s;}
    public int getSize() {return cubeGen.size;}

    public CubeShapeGenerator getCubeGen() {return cubeGen;}


    @Override
    public void generateOnAnotherThread() {
        getShapeHolder().setShape(cubeGen);
    }

    @Override
    public void generateOnMainThread() {
        getShapeHolder().setShape(cubeGen.generateShape());
    }
}
