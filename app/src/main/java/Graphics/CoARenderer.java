package Graphics;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import org.w3c.dom.Text;

import Game.GameState;
import Game.Player;
import Game.Unit;
import Generation.MapData;
import Utils.PreciseTimer;
import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/16/2015.
 */

public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
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
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }

        TextureHelper.imageToTexture(context, R.drawable.texture1, "test1");
        TextureHelper.imageToTexture(context, R.drawable.character1walk, "soldier");

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

    private void moveUnits(){
        float[] ftmp = {255f, 255f, 255f, 255f};
        Quadrilateral quad;
        for(int i = 0; i < gameState.players.capacity(); i++){
            Player p = gameState.players.get(i);
            for(int j = 0; j < p.unitsInFlight.capacity(); j++){
                Unit unit = p.unitsInFlight.get(j);
                quad = unit.getUnit();
                SpriteBatchSystem.removeSprite("soldier", quad);
                unit.frame++;
                if(unit.frame == 10){
                    System.arraycopy(unit.destination, 0, unit.location, 0, unit.destination.length);
                    unit.path.remove(unit.path.capacity()-1);
                    unit.frame = 0;
                    if(unit.path.capacity() != 0){
                        unit.destination[0] = unit.path.get(unit.path.capacity()-1).x;
                        unit.destination[1] = unit.path.get(unit.path.capacity()-1).y;
                    }
                    else{
                        p.unitsInFlight.remove(unit);
                    }
                }
                quad = unit.getUnit();
                SpriteBatchSystem.addSprite("soldier", quad, TextureHelper.getTexture("soldier"));
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
                    gameState.mapData.width,
                    gameState.mapData.height);
           gameState.mapData.territoryGraphMesh.finish(context);
            Log.d("Line", "Got here");
        }
        // Make sprites
        SpriteBatchSystem.clear();

        int unitCount = 0;
        for(Player p : gameState.players){
            unitCount += p.units.capacity();
        }

        SpriteBatchSystem.Initialize(unitCount);

        for (Player p : gameState.players) {
            for (Unit u : p.units) {
                SpriteBatchSystem.addUnit(u.type, (u.location[0]/gameState.mapData.width) * 2 - 1 - (.25f / 2), (u.location[1]/gameState.mapData.height) * 2 - 1 - (.25f / 2));
            }
        }

        GeometryHelper.allocateBuffs(previousUnitCount);

        previousUnitCount = unitCount;
        // Redraw background color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //programHandle = ShaderHelper.getShader("simple");

        dHelper.draw(camera, GeometryHelper.getVertBuff("master"), GeometryHelper.getColorBuff("master"), GeometryHelper.getTextBuff("master"), TextureHelper.getTexture("vortest"), GeometryHelper.getVerticesCount("master"));

        if (showLines) gameState.mapData.territoryGraphMesh.renderLines(camera.getVPMatrix());

        Enumeration vEnum = SpriteBatchSystem.sprites.elements();
        //programHandle = ShaderHelper.getShader("animate");
        //GLES20.glUseProgram(programHandle);
        while(vEnum.hasMoreElements()){
            String name = vEnum.nextElement().toString();
            SpriteBatchSystem.sprite s = SpriteBatchSystem.getSprite(name);
            SpriteSheetDimensions ssd = new SpriteSheetDimensions();
            GeometryHelper.getFrameTexture(name, ssd.soldierWidth, ssd.soldierHeight, ssd.soldierFrameWidth, ssd.soldierFrameHeight, frame % ssd.soldierFrames, (frame / ssd.soldierFrames) % (int)(ssd.soldierHeight / ssd.soldierFrameHeight));
            dHelper.draw(camera, s.vBuf, s.tBuf, s.tBuf, s.texture, GeometryHelper.getVerticesCount(name));
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
