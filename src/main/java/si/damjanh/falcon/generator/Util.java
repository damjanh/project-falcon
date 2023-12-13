package si.damjanh.falcon.generator;

import java.awt.image.BufferedImage;

public class Util {

    public static float[][] normalize(float[][] heightMap) {
        int height = heightMap.length;
        int width = 0;
        if (heightMap[0] != null && heightMap[0].length > 0) {
            width = heightMap[0].length;
        }

        float maxValue = 0;
        // Find maximum
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                float value = heightMap[i][j];
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        float[][] normalizedMap = new float[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                normalizedMap[i][j] = heightMap[i][j] / maxValue;
                if (normalizedMap[i][j] == 1) {
                    System.out.println(normalizedMap[i][j]);
                }
            }
        }

        return normalizedMap;
    }

    public static BufferedImage convertHeightmapToImage(float[][] heightMap) {
        int height = heightMap.length;
        int width = 0;
        if (heightMap.length > 0) {
            width = heightMap[0].length;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float value = heightMap[y][x];
                image.setRGB(x, y, getGrayscaleInt(value));
            }
        }
        return image;
    }

    private static int getGrayscaleInt(float value) {
        int valueInt = (int) (value * 255.0f);
        int rgb = valueInt;
        rgb = (rgb << 8) + valueInt;
        rgb = (rgb << 8) + valueInt;
        return rgb;
    }
}
