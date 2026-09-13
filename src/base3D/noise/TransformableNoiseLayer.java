package base3D.noise;

import base3D.definitions.NoiseLayer;
import processing.core.PVector;

public abstract class TransformableNoiseLayer implements NoiseLayer {
    //---------------------------
    // Attributes
    private final PVector coordinateOffset;
    private final PVector frequency;
    private float amplitude;
    private float offset;

    //---------------------------
    // Constructors
    public TransformableNoiseLayer() {
        this.coordinateOffset = new PVector();
        this.frequency = new PVector(1,1,1);
        this.amplitude = 0f;
        this.offset = 0f;
    }

    public TransformableNoiseLayer(PVector coordinateOffset, PVector frequency, float amplitude, float offset) {
        this();
        this.coordinateOffset.set(coordinateOffset);
        this.frequency.set(frequency);
        this.amplitude = amplitude;
        this.offset = offset;
    }

    //----------------
    // copy
    public TransformableNoiseLayer(TransformableNoiseLayer other) {this(); set(other);}
    public TransformableNoiseLayer copy() {return null;}
    public void set(TransformableNoiseLayer other) {
        this.coordinateOffset.set(other.getCoordinateOffset());
        this.frequency.set(other.getFrequency());
        this.amplitude = other.getAmplitude();
        this.offset = other.getOffset();
    }
    // ----------------

    //---------------------------
    // Methods

    // <<override this>>
    abstract float calculate3D(float x, float y, float z);
    abstract float calculate2D(float x, float z);
    abstract float calculate1D(float x);

    @Override
    public float get(PVector v) {
        // g(x) =  k * f((x+ox)*sx,(y+oy)*sy,(z+oz)*sz) + c
        return offset + amplitude * calculate3D(
                                  (v.x+coordinateOffset.x)*frequency.x,
                                  (v.y+coordinateOffset.y)*frequency.y,
                                  (v.z+coordinateOffset.z)*frequency.z
        );
    }

    @Override
    public float get(float x, float y, float z) {
        return offset + amplitude * calculate3D(
                (x+coordinateOffset.x)*frequency.x,
                (y+coordinateOffset.y)*frequency.y,
                (z+coordinateOffset.z)*frequency.z
        );
    }

    @Override
    public float get(float x, float z) {
        return offset + amplitude * calculate2D(
                (x+coordinateOffset.x)*frequency.x,
                (z+coordinateOffset.z)*frequency.z
        );
    }

    @Override
    public float get(float x) {
        return offset + amplitude * calculate1D(
                (x+coordinateOffset.x)*frequency.x
        );
    }
//---------------------------
    // Getters & Setters

    public PVector getCoordinateOffset() {return coordinateOffset;}
    public PVector getFrequency() {return frequency;}
    public float getAmplitude() {return amplitude;}
    public float getOffset() {return offset;}

    public void setOffset(float offset) {this.offset = offset;}
    public void setCoordinateOffset(PVector v) {coordinateOffset.set(v);}
    public void setFrequency(PVector v) {frequency.set(v);}
    public void setFrequencyXYZ(float v) {frequency.set(v,v,v);}
    public void setAmplitude(float amplitude) {this.amplitude = amplitude;}
}
