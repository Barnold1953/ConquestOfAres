package Graphics;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import Game.GameState;
import Generation.MapData;
import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/16/2015.
 */

public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
    Context context;
    HashMap<String, int[]> textures = new HashMap<String, int[]>();
    Camera camera;
    MapData mapData;
    GameState gameState = null;

    DrawHelper dHelper;
    SpriteBatchSystem sbsHelper;

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
    boolean showTerrain = true;
    boolean showLines = false;

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void toggleLines() {
        showLines = !showLines;
    }

    public void toggleTerrain() {
        showTerrain = !showTerrain;
    }

    public CoARenderer(Context c) {
        context = c;

        camera = new Camera();
        dHelper = new DrawHelper(camera);
        sbsHelper = new SpriteBatchSystem();

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
            programHandle = ShaderHelper.compileShader(context, R.string.simple_vert, R.string.texture_frag, "simple");
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }

        dHelper.setProgHandles(programHandle);
        TextureHelper.imageToTexture(context, R.drawable.texture1, "test1");

        Log.d("Setup", "After texture get");

        Log.d("Setup", "Shader successfully initialized.");
        float[] ftmp = {255f, 255f, 255f, 255f};
        Quadrilateral quad = new Quadrilateral();
        quad = Quadrilateral.getQuad(quad, -1, -1, 0, 2, 2, ftmp);
        //gHelper.addToBatch(quad, "master");
        GeometryHelper.addToBatch(quad, "master");
        //quad = Quadrilateral.getQuad(quad, 0,0,0,1,1,ftmp);
        //SpriteBatchSystem.addSprite("test1", quad, TextureHelper.getTexture("test1"));
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
           gameState.mapData.terrainTexture = TextureHelper.dataToTexture(gameState.mapData.terrainPixelBuffer,
                   "tertest",
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
        if (showTerrain) {
            dHelper.draw(camera, GeometryHelper.getVertBuff("master"), GeometryHelper.getColorBuff("master"), GeometryHelper.getTextBuff("master"), TextureHelper.getTexture("tertest"));
        } else {
            dHelper.draw(camera, GeometryHelper.getVertBuff("master"), GeometryHelper.getColorBuff("master"), GeometryHelper.getTextBuff("master"), TextureHelper.getTexture("vortest"));
        }
        if (showLines) gameState.mapData.territoryGraphMesh.renderLines(camera.getVPMatrix());

        Enumeration vEnum = SpriteBatchSystem.sprites.elements();
        while(vEnum.hasMoreElements()){
            String name = vEnum.nextElement().toString();
            SpriteBatchSystem.sprite s = SpriteBatchSystem.getSprite(name);
            dHelper.draw(camera, s.vBuf, s.tBuf, s.cBuf, TextureHelper.getTexture(name));
        }
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
