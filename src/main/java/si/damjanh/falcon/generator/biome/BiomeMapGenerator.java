package si.damjanh.falcon.generator.biome;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BiomeMapGenerator {

    public static final float SEA_LEVEL = 0.4f;
    public static final float OCEAN_START_AFTER = 0.1f;

    private final BiomeType[][] biomeMap;
    private final int width;
    private final int height;

    private static BiomeMapGenerator instance = null;

    public static BiomeMapGenerator getInstance() {
        return instance;
    }

    public BiomeMapGenerator(float[][] heightMap, float[][] rainfallMap) throws Exception {
        BiomeMapGenerator.instance = this;
        if (heightMap.length == 0 || rainfallMap.length == 0 || heightMap.length != rainfallMap.length) {
            throw new Exception("Invalid height map or rainfal map size");
        }
        if (heightMap[0].length == 0 || rainfallMap[0].length == 0 || heightMap[0].length != rainfallMap[0].length) {
            throw new Exception("Invalid height map or rainfal map size");
        }

        width = heightMap.length;
        height = heightMap[0].length;
        biomeMap = new BiomeType[height][width];

        for (int i = 0; i< height; i ++) {
            for (int j = 0; j< width; j++) {
                final float heightValue = heightMap[i][j];
                if (heightValue < SEA_LEVEL - OCEAN_START_AFTER) {
                    biomeMap[i][j] = BiomeType.OCEAN;
                } else if (heightValue < SEA_LEVEL) {
                    biomeMap[i][j] = BiomeType.SHALLOWS;
                } else {
                    biomeMap[i][j] = BiomeTable.BIOME_TABLE[Moisture.rainfallToMoisture(rainfallMap[i][j])
                            .ordinal()][Temperature.heightToTemperature(heightValue).ordinal()];
                }
            }
        }
    }

    public BufferedImage getNoiseBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = Color.RED;
                switch (biomeMap[y][x]) {
                    case DESERT:
                        color = desert;
                        break;
                    case SAVANNA:
                        color = savanna;
                        break;
                    case RAINFOREST:
                        color = rainforest;
                        break;
                    case GRASSLAND:
                        color = grassland;
                        break;
                    case WOODLAND:
                        color = woodland;
                        break;
                    case FOREST:
                        color = forest;
                        break;
                    case TAJGA:
                        color = tajga;
                        break;
                    case TUNDRA:
                        color = tundra;
                        break;
                    case ICE:
                        color = ice;
                        break;
                    case SHALLOWS:
                        color = shallows;
                        break;
                    case OCEAN:
                        color = ocean;
                        break;
                }
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

    public BiomeType getBiomeForPos(int y, int x) {
        return biomeMap[y][x];
    }

    public Vector4f lookupColorForBiome(int y, int x) {
        Color color;
        switch (biomeMap[y][x]) {
            case DESERT:
                color = desert;
                break;
            case SAVANNA:
                color = savanna;
                break;
            case RAINFOREST:
                color = rainforest;
                break;
            case GRASSLAND:
                color = grassland;
                break;
            case WOODLAND:
                color = woodland;
                break;
            case FOREST:
                color = forest;
                break;
            case TAJGA:
                color = tajga;
                break;
            case TUNDRA:
                color = tundra;
                break;
            case ICE:
                color = ice;
                break;
            case SHALLOWS:
                color = shallows;
                break;
            case OCEAN:
                color = ocean;
                break;
            default:
                color = Color.red;
                break;
        }

        return new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
    }

    private static Color shallows = Color.cyan;
    private static Color ocean = Color.blue;


    private static Color ice = Color.white;
    private static Color desert = new Color(238/255f, 218/255f, 130/255f, 1);
    private static Color savanna = new Color(177/255f, 209/255f, 110/255f, 1);
    private static Color rainforest = new Color(66/255f, 123/255f, 25/255f, 1);
    private static Color tundra = new Color(96/255f, 131/255f, 112/255f, 1);
    private static Color grassland = new Color(164/255f, 225/255f, 99/255f, 1);
    private static Color forest = new Color(73/255f, 100/255f, 35/255f, 1);
    private static Color tajga = new Color(95/255f, 115/255f, 62/255f, 1);
    private static Color woodland = new Color(139/255f, 175/255f, 90/255f, 1);

}
