package base3D.definitions;

import processing.core.PVector;

public interface NoiseLayer {
    float get(PVector v);
    float get(float x, float y, float z);
    float get(float x, float z);
    float get(float x);
}
