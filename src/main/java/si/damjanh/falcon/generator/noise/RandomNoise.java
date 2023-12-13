package si.damjanh.falcon.generator.noise;

import java.util.Random;

public class RandomNoise {

    private final int width;
    private final int height;

    private final float[][] values;

    public RandomNoise(int height, int width) {
        this.width = width;
        this.height = height;

        values = new float[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Random random = new Random();
                values[y][x] = random.nextFloat();
            }
        }
    }

    public float[][] getHeightMap() {
        return values;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
