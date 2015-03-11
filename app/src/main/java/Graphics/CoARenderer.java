package Graphics;

import java.io.IOException;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import Game.GameState;
import Generation.MapData;
import Generation.MapGenerationParams;
import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/16/2015.
 */

public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
    Context context;
    HashMap<String, int[]> textures;
    Camera camera;
    MapData mapData;
    GameState gameState = null;

    GeometryHelper gHelper;
    DrawHelper dHelper;
    final boolean IS_3D = false; ///< Temporary: determines if we are rendering in 3D or 2D

    // Camera lookAt
    final float eyeX = 0.0f;
    final float eyeY = 0.0f;
    final float eyeZ = 1.0f;
    final float lookX = 0.0f;
    final float lookY = 0.0f;
    final float lookZ = 0.0f;
    final float upX = 0.0f;
    final float upY = 1.0f;
    final float upZ = 0.0f;

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public CoARenderer(Context c, HashMap<String, int[]> t) {
        context = c;
        textures = t;

        camera = new Camera();
        gHelper = new GeometryHelper();
        dHelper = new DrawHelper(camera, gHelper);

        // Set the view matrix
        if (IS_3D) {
            camera.lookAt(eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        Log.d("Setup", "Surface created.");
        try {
            programHandle = ShaderHelper.compileShader(context, R.string.simple_vert, R.string.simple_frag, "simple");
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }

        dHelper.setProgHandles(programHandle);
        TextureHelper.imageToTexture(context, R.drawable.texture1, "texture1");

        Log.d("Setup", "After texture get");

        Log.d("Setup", "Shader successfully initialized.");
        gHelper.createBuffers();
        gHelper.createQuad(-1, -1, 0, 2, 2);
        Log.d("Setup", "Geometry buffers initialized and filled.");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
    }

    @Override
    public void onDrawFrame(GL10 unused) {
       if (gameState != null && gameState.mapData.texture == 0) {
           gameState.mapData.texture = TextureHelper.dataToTexture(gameState.mapData.pixelBuffer,
                    "vortest",
                    gameState.mapData.width,
                    gameState.mapData.height);
           gameState.mapData.territoryGraphMesh.finish(context);
        }
        // Redraw background color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        GLES20.glUseProgram(programHandle);
        dHelper.draw(camera, gHelper.mVertexBuffer, gHelper.mColorBuffer, gHelper.mTextCoordBuffer, gHelper.mIndicesBuffer, TextureHelper.getTexture("vortest"));
        gameState.mapData.territoryGraphMesh.renderLines(camera.getVPMatrix());
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // Set the projection matrix
        if (IS_3D) {
            camera.setSurface(width, height, 0.1f, 10000.0f);
        } else {
            camera.ortho(width, height);
        }
    }
}
