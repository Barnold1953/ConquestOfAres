package Graphics;

import java.io.IOException;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/16/2015.
 */
public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
    Context context;
    HashMap<String,String> shaders;
    HashMap<String, int[]> textures;
    Camera mHelper;
    ShaderHelper sHelper;
    GeometryHelper gHelper;
    DrawHelper dHelper;
    TextureHelper tHelper;


    public CoARenderer(Context c, HashMap<String,String> s, HashMap<String, int[]> t) {
        context = c;
        shaders = s;
        textures = t;

        mHelper = new Camera();
        sHelper = new ShaderHelper(shaders);
        gHelper = new GeometryHelper();
        tHelper = new TextureHelper(context);
        dHelper = new DrawHelper(mHelper, gHelper, tHelper);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        Log.d("Setup", "Surface created.");
        try {
            programHandle = sHelper.compileShader("simple");
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }
        dHelper.setProgHandles(programHandle);
        Log.d("Setup", "Shader successfully initialized.");
        mHelper.matrixSetup(programHandle);
        Log.d("Setup", "Matrix successfully initialized.");
        tHelper.ImageToTexture(context, R.drawable.texture1);
        Log.d("Setup", "After texture get");
        gHelper.createBuffers();
        //gHelper.createQuad(0, 0, 0, 1, 1);
        //gHelper.createQuad(-1, -1, 0, 1, 1);
        //gHelper.createQuad(-1, 0, 0, 1, 1);
        //gHelper.createQuad(0,-1,0,1,1);
        Log.d("Setup", "Geometry buffers initialized and filled.");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        mHelper.updateMatrix();
        dHelper.Draw(programHandle, gHelper.mVertexBuffer, gHelper.mColorBuffer, gHelper.mTextCoordBuffer, gHelper.mIndicesBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        mHelper.surfaceChanged(width, height);
    }
}
