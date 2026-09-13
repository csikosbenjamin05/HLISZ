package base3D.shapeLibrary;

import base3D.ENV;
import base3D.noise.LayeredNoise;
import base3D.resources.ResourceManager;
import base3D.definitions.ResourceUser;
import base3D.shapeLibrary.brushes.ShapeBrush;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;


// TODO :: Implement SquareMesh!
// TYPE is assumed to be TRIANGLE_STRIP;

public class MeshShapeGenerator extends ShapeGenerator {

    // BROKEN
    static public void setMeshHeight(PShape shape, LayeredNoise layeredNoise) {
        if (shape.getKind() == PConstants.GROUP) {
            PShape child;
            PVector v;
            for (int i = 0; i < shape.getChildCount(); i++) {
                child = shape.getChild(i);
                for (int j = 0; j < child.getVertexCount(); j++) {
                    v = child.getVertex(j);
                    v.y = layeredNoise.get(v);
                    child.setVertex(j, v);
                }
            }
        } else {
            PVector v;
            for (int i = 0; i < shape.getVertexCount(); i++) {
                v = shape.getVertex(i);
                v.y = layeredNoise.get(v);
                shape.setVertex(i, v);
            }
        }
    }

    public final ShapeBrush wrapper;
    public int width;
    public int height;
    public int scale;
    public final LayeredNoise layeredNoise;
    public boolean startHeightFromZero = false;

    float[][] noiseValues;

    //---------------------------
    // Constructors

    public MeshShapeGenerator(ResourceUser parent, LayeredNoise layeredNoise) {
        super(parent, false);
        this.wrapper = new ShapeBrush();
        this.width = 0;
        this.height = 0;
        this.scale = 1;
        this.layeredNoise = layeredNoise;
    }

    public MeshShapeGenerator(LayeredNoise layeredNoise) {
        this(null,layeredNoise);
    }

    public MeshShapeGenerator(ResourceUser parent, LayeredNoise layeredNoise, int width, int height, int scale) {
        this(parent,layeredNoise);
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    public MeshShapeGenerator(LayeredNoise layeredNoise, int width, int height, int scale) {
        this(null, layeredNoise, width, height, scale);
    }


    public MeshShapeGenerator(ResourceUser parent, LayeredNoise layeredNoise, int width, int height, int scale, String texture) {
        this(parent, layeredNoise,width, height, scale);

        texture_name = texture;
        paintWithTexture = true;
    }

    public MeshShapeGenerator(LayeredNoise layeredNoise, int width, int height, int scale, String texture) {
        this(null, layeredNoise,width, height, scale, texture);
    }

    //----------------
    // copy
        public MeshShapeGenerator(MeshShapeGenerator m) {this(m.getParent(), m.layeredNoise); set(m);}
        public MeshShapeGenerator copy() {return new MeshShapeGenerator(this);}
        public void set(MeshShapeGenerator m) {
            set((ShapeGenerator) m);

            this.wrapper.set(m.wrapper);
            this.width  = m.width;
            this.height = m.height;
            this.scale  = m.scale;

            this.startHeightFromZero = m.startHeightFromZero;
            this.noiseValues = null;

        }
    // ----------------


    private PShape generateMesh() {
        PShape shape = ENV.p.createShape();


        shape.beginShape(PConstants.QUAD_STRIP);
        shape.normal(0,1,0);
        wrapper.setPShape(shape);

        float hScale = height * scale;
        float wScale = width  * scale;

        for (float x = 0; x < wScale; x += scale) {
            for (float z = 0; z < hScale; z += scale) {
                shape.vertex(x, 0, z);
                shape.vertex(x + scale, 0, z);
            }

            x+=scale;
            for (float z = hScale-scale; z > -1; z-=scale) {
                shape.vertex(x + scale, 0, z);
                shape.vertex(x, 0, z);
            }
        }

        shape.endShape();

        return shape;
    }

    private float[][] calculateNoiseValues() {
        noiseValues = new float[width+1][height+1];

        for (int x = 0; x < width+1; x++)
            for (int z = 0; z < height+1; z++)
                noiseValues[x][z] = layeredNoise.get(x,z);

        return noiseValues;
    }

    private float getMinHeight() {
        float minHeight = Integer.MAX_VALUE;
        for (float[] hArr : noiseValues)
            for (float h : hArr)
                minHeight = Float.min(minHeight, h);

        return minHeight;
    }

    private void startHeightFromZero() {
        float minHeight = getMinHeight();
        for (int x = 0; x < width+1; x++)
            for (int z = 0; z < height+1; z++)
                noiseValues[x][z] -= minHeight;
    }

    @Override
    PShape generateTexturelessShape() {
        PShape shape = ENV.p.createShape();


        shape.beginShape(PConstants.TRIANGLE_STRIP);
        shape.normal(0,-1,0);
        wrapper.setPShape(shape);

        if (noiseValues == null) noiseValues = calculateNoiseValues();
        if(startHeightFromZero) startHeightFromZero();

        int hScale = height * scale;
        int wScale = width  * scale;

        for (int x = 0; x < wScale; x += scale) {
            for (int z = 0; z < hScale; z += scale) {
                shape.vertex(x, noiseValues[x/scale][z/scale], z);
                shape.vertex(x + scale, noiseValues[x/scale + 1][z/scale], z);
            }

            x+=scale;
            for (int z = hScale-scale; z > -1; z-=scale) {
                shape.vertex(x + scale, noiseValues[x/scale + 1][z/scale], z);
                shape.vertex(x, noiseValues[x/scale][z/scale], z);
            }
        }

        shape.endShape();

        return shape;
    }



    @Override
    PShape generateTexturedShape() {
        //PShape shape = ENV.p.createShape(PConstants.GROUP);

        PShape shape = ENV.p.createShape();
        shape.beginShape(PConstants.TRIANGLE_STRIP);
        shape.normal(0,-1,0);
        wrapper.setPShape(shape);

        PImage texture = ResourceManager.getTexture(texture_name, parent::reGenerateElement).get();

        if (noiseValues == null) noiseValues = calculateNoiseValues();
        if(startHeightFromZero) startHeightFromZero();

        int hScale = height * scale;
        int wScale = width  * scale;

        shape.texture(texture);

        shape.textureMode(PConstants.NORMAL);

        shape.noFill();
        shape.noStroke();


        for (int x = 0; x < wScale; x += scale) {
            for (int z = 0; z < hScale; z += scale) {
                shape.vertex(x, noiseValues[x/scale][z/scale], z, 0, 0);
                shape.vertex(x + scale, noiseValues[x/scale + 1][z/scale], z, 0, 1);

                z += scale;
                shape.vertex(x, noiseValues[x/scale][z/scale], z, 1, 1);
                shape.vertex(x + scale, noiseValues[x/scale + 1][z/scale], z, 1, 0);
            }

            x+=scale;
            for (int z = hScale-scale; z > -1; z-=scale) {
                shape.vertex(x + scale, noiseValues[x/scale + 1][z/scale], z, 0, 1);
                shape.vertex(x, noiseValues[x/scale][z/scale], z, 0, 0);

                z-=scale;

                shape.vertex(x + scale, noiseValues[x/scale + 1][z/scale], z, 1, 0);
                shape.vertex(x, noiseValues[x/scale][z/scale], z, 1, 1);
            }
        }



        shape.endShape();

        return shape;
    }

    public void setShapeHolderOffset(ShapeHolder sh) {
        sh.setOffSet(width/2f * scale, 0, height/2f * scale);
    }

    //---------------------------
    // Getters & Setters


}
