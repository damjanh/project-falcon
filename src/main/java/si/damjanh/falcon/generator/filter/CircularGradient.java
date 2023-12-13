package si.damjanh.falcon.generator.filter;

import java.awt.image.BufferedImage;

public class CircularGradient {

    private final int width;
    private final int height;

    private final float[][] map;

    public CircularGradient(int width, int height) {
        this.width = width;
        this.height = height;

        map = new float[height][width];

        int centerX = width / 2;
        int centerY = height / 2;

        float maxDistance = 1;

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y++) {
                int distanceX = Math.abs(x - centerX);
                int distanceY = Math.abs(y - centerY);

                float distance = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                if (x == 0 && y == 0) {
                    maxDistance = distance;
                }

                map[x][y] = (float) ((((distance) / maxDistance) - 0.8) * 2.0f) * -1;
                //map[x][y] = (float) (distance / maxDistance) * 2f;

                if (map[x][y] < 0.025) {
                    map[x][y] = 0.025f; // Dont set to 0 since it will get flattend
                }
                if (map[x][y] >= 1) {
                    map[x][y] = 1 - (map[x][y] -1 );
                }

            }
        }
    }

    public float[][] getMap() {
        return map;
    }
}
