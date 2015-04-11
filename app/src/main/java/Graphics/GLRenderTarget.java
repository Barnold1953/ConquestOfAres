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

    int[] fb, depthRb, renderTex; // the framebuffer, the renderbuffer and the texture to render
    IntBuffer texBuffer;          //  Buffer to store the texture
    int m_width, m_height;

    public void init(int width, int height) {
        m_height = width;
        m_height = height;
        // Create the ints for the framebuffer, depth render buffer and texture
        fb = new int[1];
        depthRb = new int[1];
        renderTex = new int[1];

        // Generate
        GLES20.glGenFramebuffers(1, fb, 0);
        GLES20.glGenRenderbuffers(1, depthRb, 0); // the depth buffer
        GLES20.glGenTextures(1, renderTex, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);

        // Generate texture
        bindTexture();
        // Parameters - we have to make sure we clamp the textures to the edges
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);

        // Create an empty intbuffer first
        int[] buf = new int[width * height];
        texBuffer = IntBuffer.wrap(buf);

        // Generate the textures
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
                renderTex[0], 0);

        // Create render buffer and bind 16-bit depth buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.d("GLRenderTarget", "Error " + Integer.toString(error));
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void bindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
    }

    public boolean bind() {
        // Viewport should match texture size
        GLES20.glViewport(0, 0, m_width, m_height);

        // Bind the framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);

        // Attach render buffer as depth buffer
       // GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);

        // Check status
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
            return false;

        // Clear the texture (buffer) and then render as usual...
        GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        return true;
    }

    public void unBind() {
        // Bind the default framebuffer (to render to the screen) - indicated by '0'
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }
}
