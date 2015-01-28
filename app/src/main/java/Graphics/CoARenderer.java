package Graphics;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Created by Nathan on 1/16/2015.
 */
public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
    MatrixHelper mHelper = new MatrixHelper();
    ShaderHelper sHelper = new ShaderHelper();
    GeometryHelper gHelper = new GeometryHelper();
    DrawHelper dHelper = new DrawHelper(mHelper);

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        Log.d("Setup", "Surface created.");
        Log.d("Setup", "Shader handle created.");
        try {
            programHandle = sHelper.compileShader("simple");
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }
        Log.d("Setup", "Shader successfully initialized.");
        mHelper.matrixSetup(programHandle);
        Log.d("Setup", "Matrix successfully initialized.");
        gHelper.createBuffers();
        Log.d("Setup", "Geometry buffers initialized and filled.");
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mHelper.updateMatrix();
        dHelper.DrawCube(programHandle, gHelper.mVertexBuffer, gHelper.mColorBuffer, gHelper.mTextCoordBuffer);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        mHelper.surfaceChanged(width, height);
    }
}
