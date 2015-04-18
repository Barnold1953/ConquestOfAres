package Graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by brb55_000 on 4/11/2015.
 */
public class GLRenderTarget {

    int[] fb, depthRb, colorRb, renderTex; // the framebuffer, the renderbuffer and the texture to render
    IntBuffer texBuffer;          //  Buffer to store the texture
    int m_width, m_height;

    public void init(int width, int height) {
        m_width = width;
        m_height = height;
        // Create the ints for the framebuffer, depth render buffer and texture
        fb = new int[1];
        depthRb = new int[1];
        colorRb = new int[1];
        renderTex = new int[1];

        // Generate
        GLES20.glGenFramebuffers(1, fb, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);

        // Color Buffer
        GLES20.glGenRenderbuffers(1, colorRb, 0); // the depth buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, colorRb[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_RGBA, width, height);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, colorRb[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        // Depth Buffer
        GLES20.glGenRenderbuffers(1, depthRb, 0); // the depth buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        // Check status
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.d("FrameBuffer ", "Bad framebuffer with status " + status);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.d("GLRenderTarget", "Error " + Integer.toString(error));
        }

    }

    public void bindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
    }

    public boolean bind() {
        // Bind the framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
        // Viewport should match texture size
        GLES20.glViewport(0, 0, m_width, m_height);

        // Check status
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
            return false;

        // Clear the texture (buffer) and then render as usual...
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        return true;
    }

    public void unBind() {
        // Bind the default framebuffer (to render to the screen) - indicated by '0'
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }
}
