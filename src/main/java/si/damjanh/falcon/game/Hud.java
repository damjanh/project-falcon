package si.damjanh.falcon.game;

import org.joml.Vector4f;
import si.damjanh.falcon.engine.objects.GameObject;
import si.damjanh.falcon.engine.IHud;
import si.damjanh.falcon.engine.objects.TextItem;
import si.damjanh.falcon.engine.Window;
import si.damjanh.falcon.engine.graph.FontTexture;
import si.damjanh.falcon.engine.graph.Material;
import si.damjanh.falcon.engine.graph.Mesh;
import si.damjanh.falcon.engine.graph.OBJLoader;

import java.awt.*;

public class Hud implements IHud {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final GameObject[] gameObjects;

    private final TextItem statusTextItem;
    private final GameObject compassObject;

    public Hud(String statusText) throws Exception {
        // Text
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        // Create compass
        Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        compassObject = new GameObject(mesh);
        compassObject.setScale(40.0f);
        compassObject.setRotation(0f, 0f, 180f);

        gameObjects = new GameObject[] { statusTextItem, compassObject };
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    public void rotateCompass(float angle) {
        this.compassObject.setRotation(0, 0, 180 + angle);
    }

    @Override
    public GameObject[] getGameObjects() {
        return gameObjects;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
        this.compassObject.setPosition(window.getWidth() - 50f, 50f, 0);
    }
}
