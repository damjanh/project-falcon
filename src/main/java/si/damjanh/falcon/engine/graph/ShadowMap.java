package si.damjanh.falcon.engine.graph;

import static org.lwjgl.opengl.ARBFramebufferObject.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.ARBFramebufferObject.glBindFramebuffer;
import static org.lwjgl.opengl.ARBFramebufferObject.glCheckFramebufferStatus;
import static org.lwjgl.opengl.ARBFramebufferObject.glDeleteFramebuffers;
import static org.lwjgl.opengl.ARBFramebufferObject.glFramebufferTexture2D;
import static org.lwjgl.opengl.ARBFramebufferObject.glGenFramebuffers;
import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;

public class ShadowMap {

    public static final int SHADOW_MAP_WIDTH = 1024;
    public static final int SHADOW_MAP_HEIGHT = 1024;

    private final int depthMapFBO;
    private final Texture depthMap;

    public ShadowMap() throws Exception {
        // Create a FBO to render the depth map
        depthMapFBO = glGenFramebuffers();
        // Create the depth map texture
        depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);
        // Attach the the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
        // Set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }
        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    public Texture getDepthMapTexture() {
        return depthMap;
    }
    public int getDepthMapFBO() {
        return depthMapFBO;
    }
    public void cleanup() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }
}
