package si.damjanh.falcon.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import si.damjanh.falcon.Constants;
import si.damjanh.falcon.engine.graph.weather.Fog;
import si.damjanh.falcon.engine.objects.GameObject;
import si.damjanh.falcon.engine.IGameLogic;
import si.damjanh.falcon.engine.Scene;
import si.damjanh.falcon.engine.SceneLight;
import si.damjanh.falcon.engine.objects.SkyBox;
import si.damjanh.falcon.engine.Window;
import si.damjanh.falcon.engine.graph.Camera;
import si.damjanh.falcon.engine.graph.lights.DirectionalLight;
import si.damjanh.falcon.engine.graph.Material;
import si.damjanh.falcon.engine.graph.Mesh;
import si.damjanh.falcon.engine.graph.MouseInput;
import si.damjanh.falcon.engine.graph.OBJLoader;
import si.damjanh.falcon.engine.graph.Renderer;
import si.damjanh.falcon.engine.graph.Texture;
import si.damjanh.falcon.engine.objects.Terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private final Scene scene;

    private Terrain terrain;

    private Hud hud;

    private float lightAngle;
    private float angleInc;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        scene = new Scene();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = 45;
        angleInc = 1.1f;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        window.setClearColor(Constants.CLOLOR_BLUE_CLEAR_SKY);

        // Setup fog
        scene.setFog(new Fog(false, Constants.CLOLOR_BLUE_FOG, 0.06f));
        //scene.setFog(Fog.NOFOG);


        // Setup Game Objects
        float reflectance = 1f;


        float terrainScale = 6;
        float minY = 0.0f;
        float maxY = 1.0f;
        //terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "textures/heightmap.png", "textures/terrain.png", textInc);
        terrain = new Terrain(terrainScale, minY, maxY, "archipelago.png", "biome4x.png");
        //terrain = Terrain.createFromArray(terrainSize, terrainScale, minY, maxY, world.getHeightMap(), "textures/terrain.png", textInc);
        List<GameObject> gameObjectsList = new ArrayList<>();
        gameObjectsList.addAll(Arrays.asList(terrain.getGameObjects()));

        // Blocks for normal map test
        //float reflectance = 0.65f;
        Texture normalMap = new Texture("textures/rock_normals.png");

        Mesh quadMesh1 = OBJLoader.loadMesh("/models/quad.obj");
        Texture texture = new Texture("textures/rock.png");
        Material quadMaterial1 = new Material(texture, reflectance);
        quadMesh1.setMaterial(quadMaterial1);
        GameObject quadGameItem1 = new GameObject(quadMesh1);
        quadGameItem1.setPosition(-3f, 0, 0);
        quadGameItem1.setScale(2.0f);
        quadGameItem1.setRotation(90, 0, 0);

        Mesh quadMesh2 = OBJLoader.loadMesh("/models/quad.obj");
        Material quadMaterial2 = new Material(texture, reflectance);
        quadMaterial2.setNormalMap(normalMap);
        quadMesh2.setMaterial(quadMaterial2);
        GameObject quadGameItem2 = new GameObject(quadMesh2);
        quadGameItem2.setPosition(3f, 0, 0);
        quadGameItem2.setScale(2.0f);
        quadGameItem2.setRotation(90, 0, 0);

//        gameObjectsList.add(quadGameItem1);
//        gameObjectsList.add(quadGameItem2);

//        Texture texture = new Texture("textures/grassblock.png");
//        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
//        Material material = new Material(texture, reflectance);
//        mesh.setMaterial(material);

//        GameObject gameObject = new GameObject(mesh);
//        gameObject.setPosition(0, 0, -3);

        Mesh meshKatana = OBJLoader.loadMesh("/models/katana_final.obj");
        Material materialKatana = new Material(new Vector4f(0.2f, 0.5f, 0.5f, 1.0f), reflectance);
        meshKatana.setMaterial(materialKatana);

        GameObject katana = new GameObject(meshKatana);
        katana.setPosition(0.0f, -2f, -1);
        katana.setScale(0.1f);

        //gameObjectsList.add(katana);
//
//        GameObject[] gameObjects = new GameObject[2];
//        gameObjects[0] = gameObject;
//        gameObjects[1] = gameObject2;

//        float blockScale = 0.5f;
//        float skyBoxScale = 10.0f;
//        float extension = 2.0f;
//
//        setUpBlocks(mesh, blockScale, skyBoxScale, extension);

        Mesh quadMesh = OBJLoader.loadMesh("/models/plane.obj");
        Material quadMaterial = new Material(new Vector4f(0.0f, 0.0f, 1.0f, 10.0f), reflectance);
        quadMesh.setMaterial(quadMaterial);
        GameObject quadGameItem = new GameObject(quadMesh);
        quadGameItem.setPosition(0, -0.8f, 0);
        quadGameItem.setScale(50f);

        //gameObjectsList.add(quadGameItem);


        // Shading
        Mesh cubeMesh = OBJLoader.loadMesh("/models/bunny.obj");
        Material cubeMaterial = new Material(new Vector4f(0, 1, 0, 1), reflectance);
        cubeMesh.setMaterial(cubeMaterial);
        GameObject cubeGameItem = new GameObject(cubeMesh);
        cubeGameItem.setPosition(0, 0, 0);
        cubeGameItem.setScale(0.2f);

        //gameObjectsList.add(cubeGameItem);



        GameObject[] gameObjects = new GameObject[gameObjectsList.size()];
        int i = 0;
        for (GameObject object : gameObjectsList) {
            gameObjects[i] = object;
            i++;
        }
        scene.setGameObjects(gameObjects);
        //scene.setGameObjects(new GameObject[]{cubeGameItem, quadGameItem});


        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "textures/skybox.png");
        skyBox.setScale(10.0f);
        //scene.setSkyBox(skyBox);


        // Setup Lights
        setupLights();

        // Create HUD
        hud = new Hud("Project Falcon");
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(.5f, .5f, .5f));
        sceneLight.setSkyBoxLight(new Vector3f(0.8f, 0.8f, 0.8f));

        /** Not using Point Lights and SpotLigts now...

        // Point Light
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        sceneLight.setPointLightList(new PointLight[] { pointLight });

        // Spot Light
        lightPosition = new Vector3f(0, 0.0f, 10f);
        PointLight sl_pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        sl_pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(sl_pointLight, coneDir, cutoff);
        sceneLight.setSpotLightList(new SpotLight[] { spotLight });

        **/

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(.8f, .8f, .8f), lightDirection, lightIntensity);
        directionalLight.setShadowPosMult(5);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        sceneLight.setDirectionalLight(directionalLight);

        scene.setSceneLight(sceneLight);
    }

    @Override
    public void input(Window window) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }

        // Point light movement
        //        float lightPos = pointLight.getPosition().z;
        //        if (window.isKeyPressed(GLFW_KEY_N)) {
        //            this.pointLight.getPosition().z = lightPos + 0.1f;
        //        } else if (window.isKeyPressed(GLFW_KEY_M)) {
        //            this.pointLight.getPosition().z = lightPos - 0.1f;
        //        }

        // Spot light movement
//        SpotLight[] spotLightList = scene.getSceneLight().getSpotLightList();
//        float lightPos = spotLightList[0].getPointLight().getPosition().z;
//        if (window.isKeyPressed(GLFW_KEY_N)) {
//            spotLightList[0].getPointLight().getPosition().z = lightPos + 0.1f;
//        } else if (window.isKeyPressed(GLFW_KEY_M)) {
//            spotLightList[0].getPointLight().getPosition().z = lightPos - 0.1f;
//        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

        // Save camera position pre-move
        Vector3f prevPos = new Vector3f(camera.getPosition());

        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP);

        // Check if there has been a collision. If true, set the y position to the maximum height
//        float height = terrain.getHeight(camera.getPosition());
//        if (camera.getPosition().y <= height) {
//            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
//        }


        // Update spot light direction
//        spotAngle += spotInc * 0.05f;
//        if (spotAngle > 2) {
//            spotInc = -1;
//        } else if (spotAngle < -2) {
//            spotInc = 1;
//        }
//        double spotAngleRad = Math.toRadians(spotAngle);
//        SpotLight[] spotLightList = scene.getSceneLight().getSpotLightList();
//        Vector3f coneDir = spotLightList[0].getConeDirection();
//        coneDir.y = (float) Math.sin(spotAngleRad);

        // Update directional light direction, intensity and colour
        lightAngle += angleInc;
//        if (lightAngle < 0) {
//            lightAngle = 0;
//        } else if (lightAngle > 180) {
//            lightAngle = 180;
//        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
    }

    void setUpBlocks(Mesh blockMesh, float blockScale, float skyBoxScale, float extension) {
        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;
        int NUM_ROWS = (int)(extension * skyBoxScale * 2 / inc);
        int NUM_COLS = (int)(extension * skyBoxScale * 2/ inc);
        GameObject[] gameItems  = new GameObject[NUM_ROWS * NUM_COLS];
        for(int i=0; i<NUM_ROWS; i++) {
            for(int j=0; j<NUM_COLS; j++) {
                GameObject gameItem = new GameObject(blockMesh);
                gameItem.setScale(blockScale);
                incy = Math.random() > 0.9f ? blockScale * 2 : 0f;
                gameItem.setPosition(posx, starty + incy, posz);
                gameItems[i*NUM_COLS + j] = gameItem;

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }
        scene.setGameObjects(gameItems);
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }
}