package base3D.bodies;

import base3D.Useful;
import base3D.definitions.GeneratedShape;
import base3D.definitions.ResourceUser;
import base3D.noise.LayeredNoise;
import base3D.shapeLibrary.MeshShapeGenerator;
import processing.core.PVector;

public class Mesh extends SimpleBody implements ResourceUser, GeneratedShape {
    private MeshShapeGenerator meshShapeGenerator;

    //-----------------------------------------
    // Constructors
    public Mesh(PVector pos, MeshShapeGenerator meshShapeGenerator) {
        super(pos);


        meshShapeGenerator.setShapeHolderOffset(getShapeHolder());

        meshShapeGenerator.setParent(this);
        this.meshShapeGenerator = meshShapeGenerator;

        generateOnAnotherThread();
    }

    public Mesh(PVector pos, LayeredNoise layeredNoise, int width, int height, int scale) {
        this(pos, new MeshShapeGenerator(layeredNoise, width, height, scale));
    }

    public Mesh(PVector pos, String texture, LayeredNoise layeredNoise, int width, int height, int scale) {
        this(pos, new MeshShapeGenerator(layeredNoise, width, height, scale, texture));
    }

    // copy constructor
    public Mesh(Mesh c) {
        super();
        set(c);
    }

    @Override
    public void setToSimpleBody(SpatialBody o) {set((Mesh) o);}
    public void set(Mesh c) {
        this.set((SimpleBody) c);

        this.meshShapeGenerator =
                (this.meshShapeGenerator == null) ?
                        new MeshShapeGenerator(this, c.meshShapeGenerator.layeredNoise) : this.meshShapeGenerator;

        this.meshShapeGenerator.set(c.getMeshShapeGenerator());
    }

    public Mesh copy() {
        return new Mesh(this);
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

    public MeshShapeGenerator getMeshShapeGenerator() {return meshShapeGenerator;}


    @Override
    public void generateOnAnotherThread() {
        getShapeHolder().setShape(meshShapeGenerator);
    }

    public void generateOnMainThread() {
        getShapeHolder().setShape(meshShapeGenerator.generateShape());
    }

}
