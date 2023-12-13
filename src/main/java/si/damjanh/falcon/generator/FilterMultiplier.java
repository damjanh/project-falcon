package si.damjanh.falcon.generator;

import java.awt.image.BufferedImage;

public class FilterMultiplier {

    private final int width;
    private final int height;

    private final float[][] result;

    public FilterMultiplier(float[][] map, float[][] filter) throws Exception {
        if (map.length == 0 || filter.length == 0 || map.length != filter.length) {
            throw new Exception("Invalid map or filter size");
        }
        if (map[0].length == 0 || filter[0].length == 0 || map[0].length != filter[0].length) {
            throw new Exception("Invalid map or filter size");
        }

        width = map.length;
        height = map[0].length;
        result = new float[height][width];

        for (int i = 0; i< height; i++) {
            for (int j = 0; j< width; j++) {
                result[i][j] = map[i][j] * filter[i][j];
            }
        }
    }

    public float[][] getResult() {
        return result;
    }
}
