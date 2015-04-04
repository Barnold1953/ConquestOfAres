package Graphics;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import Game.GameController;
import Game.GameState;
import Game.Player;
import Game.Territory;
import Game.Unit;
import Generation.MapData;
import Utils.PreciseTimer;
import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/16/2015.
 */

public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
    int mapShader;
    Context context;
    Camera camera;
    MapData mapData;
    GameState gameState = null;
    int frame, previousUnitCount = 0;
    double[] fTime = new double[100];

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
    boolean showTerrain = false;
    boolean showLines = false;

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void toggleLines() {
        showLines = !showLines;
    }

    public void toggleTerrain() {

    }

    public CoARenderer(Context c) {
        context = c;

        camera = new Camera();
        dHelper = new DrawHelper(camera);

        // Set the view matrix
        if (IS_3D) {
            camera.lookAt(eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        frame = 0;
        // Set the background frame color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        Log.d("Setup", "Surface created.");
        try {
            //programHandle = ShaderHelper.compileShader(context, R.string.simple_vert, R.string.animate_frag, "animate");
            //dHelper.setProgHandles(programHandle, "animate");
            programHandle = ShaderHelper.compileShader(context, R.string.simple_vert, R.string.texture_frag, "simple");
            dHelper.setProgHandles(programHandle, "simple");
            mapShader = ShaderHelper.compileShader(context, R.string.simple_vert, R.string.map_frag, "map");
            dHelper.setProgHandles(programHandle, "map");
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }

        TextureHelper.imageToTexture(context, R.drawable.texture1, "test1");
        //TextureHelper.imageToTexture(context, R.drawable.character1walk, "soldier");
        TextureHelper.imageToTexture(context, R.drawable.man_frames_top, "soldier");

        //gHelper.addToBatch(quad, "master");
        /*quad = Quadrilateral.getQuad(quad, 0,0,0,1,1,ftmp);
        SpriteBatchSystem.addSprite("soldier", quad, TextureHelper.getTexture("soldier"));
        quad = Quadrilateral.getQuad(quad, -1,0,0,1,1,ftmp);
        SpriteBatchSystem.addSprite("soldier", quad, TextureHelper.getTexture("soldier"));
        quad = Quadrilateral.getQuad(quad, -1,-1,0,1,1,ftmp);
        SpriteBatchSystem.addSprite("soldier", quad, TextureHelper.getTexture("soldier"));
        quad = Quadrilateral.getQuad(quad, 0,-1,0,1,1,ftmp);
        SpriteBatchSystem.addSprite("soldier", quad, TextureHelper.getTexture("soldier"));*/
        Log.d("Setup", "Geometry buffers initialized and filled.");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
    }

    float[] getSlope(Unit u){
        if(u.frame == 10){
            u.destinationStep();
        }

        float[] slope = new float[2];

        slope[0] = (u.destination[0] - u.location[0]) / 10;
        slope[1] = (u.destination[1] - u.location[1]) / 10;
        slope[0] = u.location[0] + slope[0] * u.frame;
        slope[1] = u.location[1] + slope[1] * u.frame;
        u.frame++;
        return slope;
    }

    void renderUnits(Territory territory){
        Player currentPlayer = gameState.players.get(gameState.currentPlayerIndex%gameState.players.size());
        if(gameState.currentState == GameState.State.PLACING_UNITS_RR){
            if(territory.owner == currentPlayer){
                for (Unit u : territory.units) {
                    float[] slope = getSlope(u);
                    SpriteBatchSystem.addUnit(u.type, (slope[0]/gameState.mapData.width) * 2 - 1 - (.1f / 2), (slope[1]/gameState.mapData.height) * 2 - 1 - (.1f / 2), territory.owner.color);
                }
            }
        }
        else{
            for (Unit u : territory.units) {
                float[] slope = getSlope(u);
                SpriteBatchSystem.addUnit(u.type, (slope[0]/gameState.mapData.width) * 2 - 1 - (.1f / 2), (slope[1]/gameState.mapData.height) * 2 - 1 - (.1f / 2), territory.owner.color);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
       // Upload mapData texture
        PreciseTimer timer = new PreciseTimer();
        if (gameState != null && gameState.mapData.texture == 0) {
           gameState.mapData.texture = TextureHelper.dataToTexture(gameState.mapData.pixelBuffer,
                    "vortest",
                   Math.round(gameState.mapData.width),
                   Math.round(gameState.mapData.height));
           gameState.mapData.territoryGraphMesh.finish(context);
            Log.d("Line", "Got here");
        }
        // Make sprites
        SpriteBatchSystem.clear();

        int unitCount = 0;
        for(Territory t : gameState.territories){
            unitCount += t.units.size();
        }

        SpriteBatchSystem.Initialize(unitCount);

        for (Territory t : gameState.territories) {
            renderUnits(t);
        }

        GeometryHelper.allocateBuffs(previousUnitCount);

        previousUnitCount = unitCount;
        // Redraw background color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //programHandle = ShaderHelper.getShader("simple");

        dHelper.draw(camera, GeometryHelper.getVertBuff("master"), GeometryHelper.getColorBuff("master"), GeometryHelper.getTextBuff("master"), TextureHelper.getTexture("vortest"), GeometryHelper.getVerticesCount("master"), "map");

        if (showLines) gameState.mapData.territoryGraphMesh.renderLines(camera.getVPMatrix());

        Enumeration vEnum = SpriteBatchSystem.sprites.elements();
        //programHandle = ShaderHelper.getShader("animate");
        //GLES20.glUseProgram(programHandle);
        while(vEnum.hasMoreElements()){
            String name = vEnum.nextElement().toString();
            SpriteBatchSystem.sprite s = SpriteBatchSystem.getSprite(name);
            SpriteSheetDimensions ssd = new SpriteSheetDimensions(name);
            GeometryHelper.setFrameTexture(name, ssd.width, ssd.height, ssd.frameWidth, ssd.frameHeight, frame/5);

            dHelper.draw(camera, s.vBuf, s.cBuf, s.tBuf, s.texture, GeometryHelper.getVerticesCount(name), "simple");
        }

        fTime[frame % 100] = timer.stop();
        frame++;
        /*GLES20.glFinish();
        if(frame % 100 == 0 && frame / 100 > 0) {
            Double avgTime = 0.0;
            for (int i = 0; i < 100; i++) {
                avgTime += fTime[i];
            }
            avgTime /= 100.0;
            Log.d("*TIME render:", avgTime.toString() + " Units: " + unitCount);
        }*/
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // Set the projection matrix
        if (IS_3D) {
            camera.setSurface(width, height, 0.1f, 10000.0f);
        } else {
            camera.ortho(1, 1);
        }
    }
}
