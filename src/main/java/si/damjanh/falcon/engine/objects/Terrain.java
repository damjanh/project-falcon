package si.damjanh.falcon.engine.objects;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import si.damjanh.falcon.engine.graph.HeightMapMesh;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Terrain {

    private GameObject gameObject;
    private Box2D boundingBox;

    private final HeightMapMesh heightMapMesh;

    private int verticesPerCol;
    private int verticesPerRow;

    public Terrain(float scale, float minY, float maxY, float[][] heightMap, String textureFile) throws Exception {
        heightMapMesh = new HeightMapMesh(minY, maxY, heightMap, textureFile);

        // The number of vertices per column and row

        verticesPerCol = heightMap.length;
        verticesPerRow = 0;
        if (heightMap.length > 0) {
            verticesPerRow = heightMap[0].length;
        }

        createGameObjects(scale);
    }

    public Terrain(float scale, float minY, float maxY, String heightMapFile, String textureFile) throws Exception {

        ByteBuffer buf = null;
        int width;
        int height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(heightMapFile, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + heightMapFile + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        // The number of vertices per column and row
        verticesPerCol = width - 1;
        verticesPerRow = height - 1;

        heightMapMesh = new HeightMapMesh(minY, maxY, buf, width, height, textureFile);

        stbi_image_free(buf);

        createGameObjects(scale);
    }

    private void createGameObjects(float scale) {
                GameObject terrainBlock = new GameObject(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(0, 0 ,0);
                gameObject = terrainBlock;

                boundingBox = getBoundingBox(terrainBlock);
    }

    public float getHeight(Vector3f position) {
        float result = Float.MIN_VALUE;
        // For each terrain block we get the bounding box, translate it to view coodinates
        // and check if the position is contained in that bounding box
        Box2D boundingBox = null;
        boolean found = boundingBox.contains(position.x, position.z);
        // If we have found a terrain block that contains the position we need
        // to calculate the height of the terrain on that position
        if (found) {
            Vector3f[] triangle = getTriangle(position, boundingBox, gameObject);
            result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);
        }
        return result;
    }

    public GameObject getGameObjects() {
        return gameObject;
    }

    private Box2D getBoundingBox(GameObject terrainBlock) {
        float scale = terrainBlock.getScale();
        Vector3f position = terrainBlock.getPosition();

        float topLeftX = HeightMapMesh.STARTX * scale + position.x;
        float topLeftZ = HeightMapMesh.STARTZ * scale + position.z;
        float width = Math.abs(HeightMapMesh.STARTX * 2) * scale;
        float height = Math.abs(HeightMapMesh.STARTZ * 2) * scale;
        return new Box2D(topLeftX, topLeftZ, width, height);
    }

    protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox, GameObject terrainBlock) {
        // Get the column and row of the heightmap associated to the current position
        float cellWidth = boundingBox.width / (float) verticesPerCol;
        float cellHeight = boundingBox.height / (float) verticesPerRow;
        int col = (int) ((position.x - boundingBox.x) / cellWidth);
        int row = (int) ((position.z - boundingBox.y) / cellHeight);

        Vector3f[] triangle = new Vector3f[3];
        triangle[1] = new Vector3f(
                boundingBox.x + col * cellWidth,
                getWorldHeight(row + 1, col, terrainBlock),
                boundingBox.y + (row + 1) * cellHeight);
        triangle[2] = new Vector3f(
                boundingBox.x + (col + 1) * cellWidth,
                getWorldHeight(row, col + 1, terrainBlock),
                boundingBox.y + row * cellHeight);
        if (position.z < getDiagonalZCoord(triangle[1].x, triangle[1].z, triangle[2].x, triangle[2].z, position.x)) {
            triangle[0] = new Vector3f(
                    boundingBox.x + col * cellWidth,
                    getWorldHeight(row, col, terrainBlock),
                    boundingBox.y + row * cellHeight);
        } else {
            triangle[0] = new Vector3f(
                    boundingBox.x + (col + 1) * cellWidth,
                    getWorldHeight(row + 2, col + 1, terrainBlock),
                    boundingBox.y + (row + 1) * cellHeight);
        }

        return triangle;
    }

    protected float getDiagonalZCoord(float x1, float z1, float x2, float z2, float x) {
        float z = ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
        return z;
    }

    protected float getWorldHeight(int row, int col, GameObject gameObject) {
        float y = heightMapMesh.getHeight(row, col);
        return y * gameObject.getScale() + gameObject.getPosition().y;
    }

    protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z) {
        // Plane equation ax+by+cz+d=0
        float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
        float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
        float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
        float d = -(a * pA.x + b * pA.y + c * pA.z);
        // y = (-d -ax -cz) / b
        float y = (-d - a * x - c * z) / b;
        return y;
    }

    static class Box2D {
        public float x;
        public float y;

        public float width;
        public float height;

        public Box2D(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean contains(float x2, float y2) {
            return x2 >= x
                    && y2 >= y
                    && x2 < x + width
                    && y2 < y + height;
        }
    }
}
