package base3D.noise;

import base3D.definitions.NoiseLayer;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;

public class LayeredNoise implements NoiseLayer {
    //---------------------------
    // Attributes
    private final List<NoiseLayer> noiseLayers;

    //---------------------------
    // Constructors
    public LayeredNoise() {
        noiseLayers = new LinkedList<>();
    }

    //----------------
    // copy
    public LayeredNoise(LayeredNoise other) {this(); set(other);}
    public LayeredNoise copy() {return new LayeredNoise(this);}
    public void set(LayeredNoise other) {
        this.noiseLayers.clear();
        this.noiseLayers.addAll(other.getLayers());
    }
    // ----------------

    //---------------------------
    // Methods

    public void addLayer(NoiseLayer ly) {
        noiseLayers.add(ly);}

    @Override
    public float get(PVector v) {
        float value = 0;
        for (NoiseLayer ly : noiseLayers) value += ly.get(v);
        return value;
    }

    @Override
    public float get(float x, float y, float z) {
        float value = 0;
        for (NoiseLayer ly : noiseLayers) value += ly.get(x,y,z);
        return value;
    }

    @Override
    public float get(float x, float z) {
        float value = 0;
        for (NoiseLayer ly : noiseLayers) value += ly.get(x,z);
        return value;
    }

    @Override
    public float get(float x) {
        float value = 0;
        for (NoiseLayer ly : noiseLayers) value += ly.get(x);
        return value;
    }

    //---------------------------
    // Getters & Setters
    public List<NoiseLayer> getLayers() {return noiseLayers;}


}
