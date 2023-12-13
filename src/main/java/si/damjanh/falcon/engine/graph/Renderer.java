package si.damjanh.falcon.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import si.damjanh.falcon.engine.IHud;
import si.damjanh.falcon.engine.Scene;
import si.damjanh.falcon.engine.SceneLight;
import si.damjanh.falcon.engine.Utils;
import si.damjanh.falcon.engine.Window;
import si.damjanh.falcon.engine.graph.lights.DirectionalLight;
import si.damjanh.falcon.engine.graph.lights.PointLight;
import si.damjanh.falcon.engine.graph.lights.SpotLight;
import si.damjanh.falcon.engine.objects.GameObject;
import si.damjanh.falcon.engine.objects.SkyBox;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.ARBFramebufferObject.glBindFramebuffer;
import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;
    private final float specularPower;
    private ShadowMap shadowMap;
    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram depthShaderProgram;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        shadowMap = new ShadowMap();

        setupDepthShaderProgram();
        setupSkyBoxShader();
        setupSceneShader();
        setupHudShader();
    }

    public void setupSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        sceneShaderProgram.link();

        // Create uniforms for world and projection matrices
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        // Create uniform for texture sampler
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createMaterialUniform("material");
        sceneShaderProgram.createUniform("normalMap");
        // Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");
        // Create fog related uniforms
        sceneShaderProgram.createFogUniform("fog");
        // Create uniforms for shadow mapping
        sceneShaderProgram.createUniform("shadowMap");
        sceneShaderProgram.createUniform("orthoProjectionMatrix");
        sceneShaderProgram.createUniform("modelLightViewMatrix");
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vs"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.fs"));
        hudShaderProgram.link();
        // Create uniforms for Ortographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
        hudShaderProgram.createUniform("hasTexture");
    }

    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sb_vertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.fs"));
        skyBoxShaderProgram.link();
        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }

    private void setupDepthShaderProgram() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
        depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));
        depthShaderProgram.link();
        depthShaderProgram.createUniform("orthoProjectionMatrix");
        depthShaderProgram.createUniform("modelLightViewMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Scene scene, IHud hud) {
        clear();

        // Render depth map before view ports have been set up
        renderDepthMap(window, camera, scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderScene(scene);
        renderSkyBox(scene);
        renderHud(window, hud);
    }

    public void renderScene(Scene scene) {
        sceneShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix();

        // Update Light Uniforms
        renderLights(viewMatrix, scene.getSceneLight());

        // Set uniform for texture sampler
        sceneShaderProgram.setUniform("texture_sampler", 0);

        // Set uniform for fog
        sceneShaderProgram.setUniform("fog", scene.getFog());

        // Set normal mapping uniform
        sceneShaderProgram.setUniform("normalMap", 1);
        sceneShaderProgram.setUniform("shadowMap", 2);

        // Render each game object
        Map<Mesh, List<GameObject>> mapMeshes = scene.getGameObjectsMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            sceneShaderProgram.setUniform("material", mesh.getMaterial());
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            mesh.renderList(mapMeshes.get(mesh), (GameObject gameObject) -> {
                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameObject, viewMatrix);
                        sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                        Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(gameObject, lightViewMatrix);
                        sceneShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                    }
            );
        }

        sceneShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            sceneShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.setUniform("directionalLight", currDirLight);
    }

    private void renderHud(Window window, IHud hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrtho2DProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (GameObject gameItem : hud.getGameObjects()) {
            Mesh mesh = gameItem.getMesh();
            // Set ortohtaphic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.buildOrthoProjModelMatrix(gameItem, ortho);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("colour", gameItem.getMesh().getMaterial().getAmbientColour());
            hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

            // Render the mesh for this HUD item
            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    private void renderSkyBox(Scene scene) {
        SkyBox skyBox = scene.getSkyBox();
        if (skyBox != null) {
            skyBoxShaderProgram.bind();
            skyBoxShaderProgram.setUniform("texture_sampler", 0);
            // Update projection Matrix
            Matrix4f projectionMatrix = transformation.getProjectionMatrix();
            skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
            Matrix4f viewMatrix = transformation.getViewMatrix();
            viewMatrix.m30 = 0;
            viewMatrix.m31 = 0;
            viewMatrix.m32 = 0;
            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
            skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getSkyBoxLight());
            scene.getSkyBox().getMesh().render();
            skyBoxShaderProgram.unbind();
        }
    }

    private void renderDepthMap(Window window, Camera camera, Scene scene) {
        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getSceneLight().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Map<Mesh, List<GameObject>> mapMeshes = scene.getGameObjectsMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GameObject gameItem) -> {
                        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gameItem, lightViewMatrix);
                        depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                    }
            );
        }

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void cleanup() {
        if (shadowMap != null) {
            shadowMap.cleanup();
        }
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanup();
        }
        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanup();
        }
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }
}
