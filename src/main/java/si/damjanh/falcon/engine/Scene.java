package si.damjanh.falcon.engine;

import si.damjanh.falcon.engine.graph.Mesh;
import si.damjanh.falcon.engine.graph.weather.Fog;
import si.damjanh.falcon.engine.objects.GameObject;
import si.damjanh.falcon.engine.objects.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private final Map<Mesh, List<GameObject>> meshMap;

    private SkyBox skyBox;

    private SceneLight sceneLight;

    private Fog fog = Fog.NOFOG;

    public Scene() {
        meshMap = new HashMap<>();
    }

    public Map<Mesh, List<GameObject>> getGameObjectsMeshes() {
        return meshMap;
    }

    public void setGameObjects(GameObject[] gameObjects) {
        int numGameItems = gameObjects != null ? gameObjects.length : 0;
        for (int i=0; i<numGameItems; i++) {
            GameObject gameItem = gameObjects[i];
            Mesh mesh = gameItem.getMesh();
            List<GameObject> list = meshMap.get(mesh);
            if ( list == null ) {
                list = new ArrayList<>();
                meshMap.put(mesh, list);
            }
            list.add(gameItem);
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }
}
