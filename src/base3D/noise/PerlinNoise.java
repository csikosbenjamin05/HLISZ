package base3D.noise;

import base3D.ENV;

import java.util.Random;

// Wrapper class for the noise function
public class PerlinNoise extends TransformableNoiseLayer {
    //---------------------------
    // Attributes
    private int noiseSeed;
    private int octaveCount;
    private float octaveContribution;


    //---------------------------
    // Constructors
    public PerlinNoise() {
        this.noiseSeed = (int)ENV.p.random(1e9f);
        this.octaveCount = 4;
        this.octaveContribution = .5f;
    }

    //----------------
    // copy
    public PerlinNoise(PerlinNoise other) {this(); set(other);}
    public PerlinNoise copy() {return new PerlinNoise(this);}
    public void set(PerlinNoise other) {
        this.noiseSeed = other.getNoiseSeed();
        this.octaveCount = other.getOctaveCount();
        this.octaveContribution = other.getOctaveContribution();
    }
    // ----------------

    //---------------------------
    // Methods

    @Override
    float calculate3D(float x, float y, float z) {
        noiseSeed(noiseSeed);
        noiseDetail(octaveCount, octaveContribution);
        return noise(x,y,z);
    }

    @Override
    float calculate2D(float x, float z) {
        noiseSeed(noiseSeed);
        noiseDetail(octaveCount, octaveContribution);
        return noise(x,z);
    }

    @Override
    float calculate1D(float x) {
        noiseSeed(noiseSeed);
        noiseDetail(octaveCount, octaveContribution);
        return noise(x);
    }

    //---------------------------
    // Getters & Setters


    public int getNoiseSeed() {return noiseSeed;}

    public void setNoiseSeed(int noiseSeed) {this.noiseSeed = noiseSeed;}

    public int getOctaveCount() {return octaveCount;}

    public void setOctaveCount(int octaveCount) {this.octaveCount = octaveCount;}

    public float getOctaveContribution() {return octaveContribution;}

    public void setOctaveContribution(float octaveContribution) {this.octaveContribution = octaveContribution;}



    // Copied code form PApplet and PGraphics, because PApplet doesn't support multithreading :(
    protected static final float[] cosLUT = new float[720];

    static {
        for(int i = 0; i < 720; ++i) {
            cosLUT[i] = (float)Math.cos((float)i * 0.017453292F * 0.5F);
        }

    }
    int perlin_octaves = 4;
    float perlin_amp_falloff = 0.5F;
    int perlin_TWOPI;
    int perlin_PI;
    float[] perlin_cosTable;
    float[] perlin;
    Random perlinRandom;

    public float noise(float x) {
        return this.noise(x, 0.0F, 0.0F);
    }

    public float noise(float x, float y) {
        return this.noise(x, y, 0.0F);
    }

    public float noise(float x, float y, float z) {
        int xi;
        if (this.perlin == null) {
            if (this.perlinRandom == null) {
                this.perlinRandom = new Random();
            }

            this.perlin = new float[4096];

            for(xi = 0; xi < 4096; ++xi) {
                this.perlin[xi] = this.perlinRandom.nextFloat();
            }

            this.perlin_cosTable = cosLUT;
            this.perlin_TWOPI = this.perlin_PI = 720;
            this.perlin_PI >>= 1;
        }

        if (x < 0.0F) {
            x = -x;
        }

        if (y < 0.0F) {
            y = -y;
        }

        if (z < 0.0F) {
            z = -z;
        }

        xi = (int)x;
        int yi = (int)y;
        int zi = (int)z;
        float xf = x - (float)xi;
        float yf = y - (float)yi;
        float zf = z - (float)zi;
        float r = 0.0F;
        float ampl = 0.5F;

        for(int i = 0; i < this.perlin_octaves; ++i) {
            int of = xi + (yi << 4) + (zi << 8);
            float rxf = this.noise_fsc(xf);
            float ryf = this.noise_fsc(yf);
            float n1 = this.perlin[of & 4095];
            n1 += rxf * (this.perlin[of + 1 & 4095] - n1);
            float n2 = this.perlin[of + 16 & 4095];
            n2 += rxf * (this.perlin[of + 16 + 1 & 4095] - n2);
            n1 += ryf * (n2 - n1);
            of += 256;
            n2 = this.perlin[of & 4095];
            n2 += rxf * (this.perlin[of + 1 & 4095] - n2);
            float n3 = this.perlin[of + 16 & 4095];
            n3 += rxf * (this.perlin[of + 16 + 1 & 4095] - n3);
            n2 += ryf * (n3 - n2);
            n1 += this.noise_fsc(zf) * (n2 - n1);
            r += n1 * ampl;
            ampl *= this.perlin_amp_falloff;
            xi <<= 1;
            xf *= 2.0F;
            yi <<= 1;
            yf *= 2.0F;
            zi <<= 1;
            zf *= 2.0F;
            if (xf >= 1.0F) {
                ++xi;
                --xf;
            }

            if (yf >= 1.0F) {
                ++yi;
                --yf;
            }

            if (zf >= 1.0F) {
                ++zi;
                --zf;
            }
        }

        return r;
    }

    private float noise_fsc(float i) {
        return 0.5F * (1.0F - this.perlin_cosTable[(int)(i * (float)this.perlin_PI) % this.perlin_TWOPI]);
    }

    public void noiseDetail(int lod) {
        if (lod > 0) {
            this.perlin_octaves = lod;
        }

    }

    public void noiseDetail(int lod, float falloff) {
        if (lod > 0) {
            this.perlin_octaves = lod;
        }

        if (falloff > 0.0F) {
            this.perlin_amp_falloff = falloff;
        }

    }

    public void noiseSeed(long seed) {
        if (this.perlinRandom == null) {
            this.perlinRandom = new Random();
        }

        this.perlinRandom.setSeed(seed);
        this.perlin = null;
    }
}
