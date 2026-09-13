package base3D.bodies;


import base3D.definitions.GeneratedShape;
import base3D.definitions.ResourceUser;
import base3D.shapeLibrary.SphereShapeGenerator;
import base3D.Useful;
import processing.core.PVector;

public class Sphere extends SimpleBody implements ResourceUser, GeneratedShape {
    private SphereShapeGenerator sphereGen;

    //-----------------------------------------
    // Constructors
    public Sphere(PVector pos, SphereShapeGenerator sphereGen) {
        super(pos);
        sphereGen.setShapeHolderOffset(getShapeHolder());

        sphereGen.setParent(this);
        this.sphereGen = sphereGen;

        generateOnAnotherThread();
    }

    public Sphere(PVector pos, int size) {
        this(pos, new SphereShapeGenerator(null, size));
    }

    public Sphere(PVector pos, String texture, int size) {
        this(pos, new SphereShapeGenerator(null, size, texture));
    }

    // copy constructor
    public Sphere(Sphere c) {
        super();
        set(c);
    }

    @Override
    public void setToSimpleBody(SpatialBody o) {set((Sphere) o);}
    public void set(Sphere c) {
        this.set((SimpleBody) c);
        this.sphereGen = (this.sphereGen == null) ? new SphereShapeGenerator(this) : this.sphereGen;
        this.sphereGen.set(c.getSphereGen());
    }

    @Override
    public Sphere copy() {
        return new Sphere(this);
    }

    //-----------------------------------------
    // Methods
    @Override
    public String toString() {
        return "Cube<" + Useful.objectToHex(this) + ">(Shape:" + getShapeHolder().toString() +
                "\n Movement:" + getPosition().toString() + "\n Rotation:" + getRotation().toString() + ")\n";
    }

    public void reGenerateElement() {
        generateOnAnotherThread();}

    //-----------------------------------------
    // Getters & Setters

    public void setSize(int s) {sphereGen.radius = s;}
    public int getSize() {return sphereGen.radius;}

    public SphereShapeGenerator getSphereGen() {return sphereGen;}


    @Override
    public void generateOnAnotherThread() {
        getShapeHolder().setShape(sphereGen);
    }

    public void generateOnMainThread() {
        getShapeHolder().setShape(sphereGen.generateShape());
    }
}
