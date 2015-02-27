package Graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

/**
 * Created by Nathan on 1/21/2015.
 */
public class Camera {
    private float[] m_viewMatrix = new float[16];
    private float[] m_projectionMatrix = new float[16];
    private float[] m_vpMatrix = new float[16];

    final float eyeX = 0.0f;
    final float eyeY = 0.0f;
    final float eyeZ = 5.0f;

    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = 0.0f;

    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;

    public Camera() {
        // Initialize matrices to identity
        clearViewMatrix();
        clearProjectionMatrix();
    }

    public void clearProjectionMatrix() {
        Matrix.setIdentityM(m_projectionMatrix, 0);
    }

    public void clearViewMatrix() {
        Matrix.setIdentityM(m_viewMatrix, 0);
    }

    /// Sets the view matrix for a camera located at eyeXYZ to point at
    /// lookXYZ, with up vector upXYZ
    public void lookAt(float eyeX, float eyeY, float eyeZ,
                            float lookX, float lookY, float lookZ,
                            float upX, float upY, float upZ) {
        Matrix.setLookAtM(m_viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }
    /// Sets the view matrix to orthographic (2D) rendering
    public void ortho(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        Matrix.orthoM(m_projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, -1, 1);
    }
    /// Sets the viewport and projection matrix
    /// @param width: Width of the viewport
    /// @param height: Height of the viewport
    /// @param zNear: Near clipping planet
    /// @param zFar: Far clipping plane
    public void setSurface(int width, int height, float zNear, float zFar) {
        // Set the viewport
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        // TODO(Ben): maybe should use different values for these
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        // Update projection matrix
        Matrix.frustumM(m_projectionMatrix, 0, left, right, bottom, top, zNear, zFar);
    }
    /// Currently does nothing
    public void update() {
        // Empty for now
    }

    public float[] getVPMatrix() {
        Matrix.multiplyMM(m_vpMatrix, 0, m_projectionMatrix, 0, m_viewMatrix, 0);
        return m_vpMatrix;
    }
}
