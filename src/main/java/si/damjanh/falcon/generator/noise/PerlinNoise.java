package si.damjanh.falcon.generator.noise;

import java.awt.image.BufferedImage;
import java.util.Random;

public class PerlinNoise {

    private final int width;
    private final int height;
    private final int octaveCount;
    private final float persistence;
    private final long seed;

    private final float[][] perlinNoise;

    public PerlinNoise(int height, int width, int octaveCount, float persistence, long seed) {
        this.height = height;
        this.width = width;
        this.octaveCount = octaveCount;
        this.persistence = persistence;
        this.seed = seed;

        this.perlinNoise = new float[width][height];

        final float[][] base = new float[width][height];
        final float[][][] noiseLayers = new float[octaveCount][][];

        Random random = new Random(seed);
        //fill base array with random values as base for noise
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                base[x][y] = random.nextFloat();
            }
        }

        //calculate octaves with different roughness
        for (int octave = 0; octave < octaveCount; octave++) {
            noiseLayers[octave] = generatePerlinNoiseLayer(base, width, height, octave);
        }

        float amplitude = 1f;
        float totalAmplitude = 0f;

        //calculate perlin noise by blending each layer together with specific persistence
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    //adding each value of the noise layer to the noise
                    //by increasing amplitude the rougher noises will have more impact
                    perlinNoise[x][y] += noiseLayers[octave][x][y] * amplitude;
                }
            }
        }

        //normalize values so that they stay between 0..1
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                perlinNoise[x][y] /= totalAmplitude;
            }
        }
    }

    private float[][] generatePerlinNoiseLayer(float[][] base, int width, int height, int octave) {
        float[][] perlinNoiseLayer = new float[width][height];

        //calculate period (wavelength) for different shapes
        int period = 1 << octave; //2^k
        float frequency = 1f / period; // 1/2^k

        for (int x = 0; x < width; x++) {
            //calculates the horizontal sampling indices
            int x0 = (x / period) * period;
            int x1 = (x0 + period) % width;
            float horizintalBlend = (x - x0) * frequency;

            for (int y = 0; y < height; y++) {
                //calculates the vertical sampling indices
                int y0 = (y / period) * period;
                int y1 = (y0 + period) % height;
                float verticalBlend = (y - y0) * frequency;

                //blend top corners
                float top = interpolate(base[x0][y0], base[x1][y0], horizintalBlend);

                //blend bottom corners
                float bottom = interpolate(base[x0][y1], base[x1][y1], horizintalBlend);

                //blend top and bottom interpolation to get the final blend value for this cell
                perlinNoiseLayer[x][y] = interpolate(top, bottom, verticalBlend);
            }
        }

        return perlinNoiseLayer;
    }

    private float interpolate(float a, float b, float alpha) {
        return a * (1 - alpha) + alpha * b;
    }

    public float[][] getPerlinNoise() {
        return perlinNoise;
    }
}
