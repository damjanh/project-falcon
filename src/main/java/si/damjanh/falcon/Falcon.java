package si.damjanh.falcon;

import si.damjanh.falcon.engine.GameEngine;
import si.damjanh.falcon.engine.IGameLogic;
import si.damjanh.falcon.game.DummyGame;
import si.damjanh.falcon.generator.FilterMultiplier;
import si.damjanh.falcon.generator.Util;
import si.damjanh.falcon.generator.biome.BiomeMapGenerator;
import si.damjanh.falcon.generator.filter.CircularGradient;
import si.damjanh.falcon.generator.noise.PerlinNoise;
import si.damjanh.falcon.generator.noise.RandomNoise;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Falcon {

    public static void main(String[] args) throws Exception {

//        RandomNoise randomNoise = new RandomNoise(512, 256);
//        float[][] randomNoiseHeightmap = randomNoise.getHeightMap();
//
//        File outputfile = new File("random.jpg");
//        ImageIO.write(Util.convertHeightmapToImage(randomNoiseHeightmap), "jpg", outputfile);

        PerlinNoise perlin = new PerlinNoise(2048, 2048, 8, 0.4f, 1234592);
        float[][] perlinNoiseHeightmap = perlin.getPerlinNoise();

        File outputfile = new File("perlin.jpg");
        ImageIO.write(Util.convertHeightmapToImage(perlinNoiseHeightmap), "jpg", outputfile);

        CircularGradient circularGradient = new CircularGradient(2048, 2048);
        float[][] circularGradientFilter = circularGradient.getMap();
        outputfile = new File("circular.jpg");
        ImageIO.write(Util.convertHeightmapToImage(circularGradientFilter), "jpg", outputfile);

        FilterMultiplier filterMultiplier = new FilterMultiplier(perlinNoiseHeightmap, circularGradientFilter);
        perlinNoiseHeightmap = filterMultiplier.getResult();

        perlinNoiseHeightmap = Util.normalize(perlinNoiseHeightmap);

        outputfile = new File("archipelago.png");
        ImageIO.write(Util.convertHeightmapToImage(perlinNoiseHeightmap), "png", outputfile);

        //perlin = new PerlinNoise(2048, 2048, 10, 0.6f, 235643);
        perlin = new PerlinNoise(2048, 2048, 6, 0.2f, 235643);
        float[][] rainfall = Util.normalize(perlin.getPerlinNoise());
        outputfile = new File("moisture.jpg");
        ImageIO.write(Util.convertHeightmapToImage(rainfall), "jpg", outputfile);

        BiomeMapGenerator biomeMapGenerator = new BiomeMapGenerator(perlinNoiseHeightmap, rainfall);
        outputfile = new File("biome.png");
        ImageIO.write(biomeMapGenerator.getNoiseBufferedImage(), "png", outputfile);

        // Make bigger texture

        BufferedImage scaledImage = new BufferedImage((2048 * 4),(2048 * 4), BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = AffineTransform.getScaleInstance(4.0, 4.0);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(biomeMapGenerator.getNoiseBufferedImage(), scaledImage);

        outputfile = new File("biome4x.png");
        ImageIO.write(scaledImage, "png", outputfile);

        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEng = new GameEngine("GAME", 600, 480, vSync, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}
