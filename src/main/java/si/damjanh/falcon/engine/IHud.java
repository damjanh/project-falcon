package si.damjanh.falcon.engine;

import si.damjanh.falcon.engine.objects.GameObject;

public interface IHud {
    GameObject[] getGameObjects();

    default void cleanup() {
        GameObject[] gameObjects = getGameObjects();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }
}
