package si.damjanh.falcon.engine.objects;

import si.damjanh.falcon.engine.graph.Material;
import si.damjanh.falcon.engine.graph.Mesh;
import si.damjanh.falcon.engine.graph.OBJLoader;
import si.damjanh.falcon.engine.graph.Texture;

public class SkyBox extends GameObject {

    public SkyBox(String objectModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objectModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }
}
