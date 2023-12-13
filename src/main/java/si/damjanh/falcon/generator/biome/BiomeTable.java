package si.damjanh.falcon.generator.biome;

public class BiomeTable {

    public static final BiomeType[][] BIOME_TABLE = new BiomeType[][] {
            //COLDEST        //COLDER          //COLD                  //HOT                    //HOTTER                       //HOTTEST
            { BiomeType.ICE, BiomeType.TUNDRA, BiomeType.GRASSLAND,    BiomeType.DESERT,        BiomeType.DESERT,        BiomeType.DESERT },      //DRYEST
            { BiomeType.ICE, BiomeType.TUNDRA, BiomeType.GRASSLAND,    BiomeType.DESERT,        BiomeType.DESERT,        BiomeType.DESERT },      //DRYER
            { BiomeType.ICE, BiomeType.TUNDRA, BiomeType.WOODLAND,     BiomeType.WOODLAND,      BiomeType.SAVANNA,       BiomeType.SAVANNA },     //DRY
            { BiomeType.ICE, BiomeType.TUNDRA, BiomeType.FOREST,       BiomeType.WOODLAND,      BiomeType.SAVANNA,       BiomeType.SAVANNA },     //WET
            { BiomeType.ICE, BiomeType.TAJGA,  BiomeType.FOREST,       BiomeType.FOREST,        BiomeType.RAINFOREST,    BiomeType.RAINFOREST },  //WETTER
            { BiomeType.ICE, BiomeType.TAJGA,  BiomeType.FOREST,       BiomeType.FOREST,        BiomeType.RAINFOREST,    BiomeType.RAINFOREST }   //WETTEST
    };
}
