package si.damjanh.falcon.generator.biome;

public enum Moisture {
    DRYEST,
    DRYER,
    DRY,
    WET,
    WETTER,
    WETTEST;

    public static Moisture rainfallToMoisture(float rainfall) {
        if (rainfall > 0.85) {
            return WETTEST;
        } else if (rainfall > 0.7) {
            return WETTER;
        } else if (rainfall > 0.55) {
            return WET;
        } else if (rainfall > 0.30) {
            return DRY;
        } else if (rainfall > 0.15) {
            return DRYER;
        } else {
            return DRYEST;
        }
    }
}
