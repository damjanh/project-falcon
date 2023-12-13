package si.damjanh.falcon.generator.biome;

import static si.damjanh.falcon.generator.biome.BiomeMapGenerator.SEA_LEVEL;

public enum Temperature {
    COLDEST,
    COLDER,
    COLD,
    HOT,
    HOTTER,
    HOTTEST;

    public static Temperature heightToTemperature(float height) {
        if (height > SEA_LEVEL + 0.5) {
            return COLDEST;
        } else if (height > SEA_LEVEL + 0.35) {
            return COLDER;
        } else if (height > SEA_LEVEL + 0.25) {
            return COLD;
        } else if (height > SEA_LEVEL + 0.1) {
            return HOT;
        } else if (height > SEA_LEVEL) {
            return HOTTER;
        } else {
            return HOTTEST;
        }
    }
}
