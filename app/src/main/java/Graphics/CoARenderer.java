package Graphics;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import Game.GameState;
import Game.Player;
import Game.Territory;
import Game.Unit;
import Generation.GpuGenerator;
import Generation.MapData;
import Generation.MapGenerator;
import Utils.PreciseTimer;
import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/16/2015.
 */

public class CoARenderer implements GLSurfaceView.Renderer {
    int programHandle;
    Context context;
    Camera camera;
    GameState gameState = null;
    int m_viewportW = 0;
    int m_viewportH = 0;
    int frame;
    int previousAttackCount = 0, previousIdleCount = 0, previousMoveCount = 0;
    double[] fTime = new double[100];
    int width, height;
    LinkedList<Laser> lasers = new LinkedList<Laser>();

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

    public void addLaser(float sx, float sy, float dx, float dy, float r, float g, float b) {
        synchronized (lasers) {
            Laser l = new Laser();
            l.mesh.addVertex(sx / gameState.mapData.width * 2.0f - 1.0f, sy / gameState.mapData.height * 2.0f - 1.0f, 0.0f, r, g, b);
            l.mesh.addVertex(dx / gameState.mapData.width * 2.0f - 1.0f, dy / gameState.mapData.height * 2.0f - 1.0f, 0.0f, r, g, b);
            lasers.add(l);
        }
    }

    public CoARenderer(Context c, int w, int h) {
        context = c;

        camera = new Camera();
        dHelper = new DrawHelper(camera);
        width = w;
        height = h;

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
        //TextureHelper.imageToTexture(context, R.drawable.character1walk, "soldier");
        TextureHelper.imageToTexture(context, R.drawable.man_run, "soldier_move");
        TextureHelper.imageToTexture(context, R.drawable.man_idle, "soldier_idle");
        TextureHelper.imageToTexture(context, R.drawable.man_shoot, "soldier_attack");

        Log.d("Setup", "Geometry buffers initialized and filled.");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
    }

    float[] getSlope(Unit u){
        if(u.frame == u.speed){
            u.destinationStep();
        }

        float[] slope = new float[2];

        if(Math.abs(u.destination.x - u.location.x) > width / 3){
            slope[0] = -(u.destination.x - u.location.x) / u.speed;
            if(u.wrapFrame.x == -1){
                for(int i = 0; i < u.speed; i++){
                    if(u.location.x + slope[0] * i > width){
                        u.wrapFrame.x = i;
                        break;
                    }
                }
            }
            if(u.wrapFrame.x <= u.frame){
                slope[0] = width - (u.location.x + slope[0] * u.wrapFrame.x) + u.location.x + slope[0] * u.frame;
            }
            else{
                slope[0] = u.location.x + slope[0] * u.frame;
            }
        }
        else{
            slope[0] = (u.destination.x - u.location.x) / u.speed;
            slope[0] = u.location.x + slope[0] * u.frame;
        }

        if(Math.abs(u.destination.y - u.location.y) > height / 3){
            Log.d("slope", "vertical wrap");
            slope[1] = -(u.destination.y - u.location.y) / u.speed;
            if(u.wrapFrame.y == -1){
                for(int i = 0; i < u.speed; i++){
                    if(u.location.y + slope[1] * i > height){
                        u.wrapFrame.y = i;
                        break;
                    }
                }
            }
            if(u.wrapFrame.y <= u.frame){
                slope[1] = height - (u.location.y + slope[1] * u.wrapFrame.y) + u.location.y + slope[1] * u.frame;
            }
            else{
                slope[1] = u.location.y + slope[1] * u.frame;
            }
        }
        else{
            slope[1] = (u.destination.y - u.location.y) / u.speed;
            slope[1] = u.location.y + slope[1] * u.frame;
        }

        u.frame++;
        return slope;
    }

    void renderUnits(Territory territory){
        Player currentPlayer = gameState.players.get(gameState.currentPlayerIndex%gameState.players.size());
        if(gameState.currentState == GameState.State.INITIAL_UNIT_PLACEMENT){
            if(territory.owner == currentPlayer){
                for (Unit u : territory.units) {
                    float[] slope = getSlope(u);
                    SpriteBatchSystem.addUnit(u, (slope[0]/gameState.mapData.width) * 2 - 1 - (.1f / 2), (slope[1]/gameState.mapData.height) * 2 - 1 - (.1f / 2), territory.owner.color);
                }
            }
        }
        else{
            for (Unit u : territory.units) {
                float[] slope = getSlope(u);
                SpriteBatchSystem.addUnit(u, (slope[0]/gameState.mapData.width) * 2 - 1 - (.1f / 2), (slope[1]/gameState.mapData.height) * 2 - 1 - (.1f / 2), territory.owner.color);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
       // Upload mapData texture
        PreciseTimer timer = new PreciseTimer();
        if (gameState != null && gameState.mapData.isDoneGenerating) {
            for (Territory t : gameState.territories) {
                t.texture = TextureHelper.dataToTexture(t.pixelBuffer, "t" + t.index, t.textureWidth, t.textureHeight);
                t.pixelBuffer = null;
                t.mesh = new TerritoryMesh();
                t.mesh.init(((float)t.textureX / gameState.mapData.width) * 2.0f - 1.0f,
                        ((float)t.textureY / gameState.mapData.height) * 2.0f - 1.0f,
                        ((float)t.textureWidth / gameState.mapData.width) * 2.0f,
                        ((float)t.textureHeight / gameState.mapData.height) * 2.0f,
                        context);
            }

            Log.d("Line", "Got here");
            gameState.mapData.isDoneGenerating = false;
        }

        // Make sprites
        SpriteBatchSystem.clear();

        int idleCount = 0, moveCount = 0, attackCount = 0;
        for(Territory t : gameState.territories){
            for(Unit u : t.units) {
                switch (u.type){
                    case soldier_attack:
                        attackCount++;
                        break;
                    case soldier_idle:
                        if(u.destination != u.location){
                            moveCount++;
                            u.type = Unit.Type.soldier_move;
                        }
                        else {
                            idleCount++;
                        }
                        break;
                    case soldier_move:
                        if(u.destination == u.location){
                            u.type = Unit.Type.soldier_idle;
                            idleCount++;
                        }
                        else {
                            moveCount++;
                        }
                        break;
                }
            }
        }

        SpriteBatchSystem.Initialize(attackCount, idleCount, moveCount);

        for (Territory t : gameState.territories) {
            renderUnits(t);
        }

        GeometryHelper.allocateBuffs(previousAttackCount, previousIdleCount, previousMoveCount);

        previousAttackCount = attackCount;
        previousIdleCount = idleCount;
        previousMoveCount = moveCount;
        // Redraw background color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // Update generation
        if (GpuGenerator.hasGenRequest) {
            GpuGenerator.updateGen(context);
            MapGenerator.finishGeneration((int)gameState.mapData.width, (int)gameState.mapData.height);
            GLES20.glViewport(0, 0, m_viewportW, m_viewportH);
        }

        //programHandle = ShaderHelper.getShader("simple");
        for (Territory t: gameState.mapData.territories) {
            t.updateAnimation();
            if (t.mesh != null) t.mesh.render(t, t.texture, camera.getVPMatrix());
        }

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

        // Render the lasers
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        synchronized (lasers) {
            for (Iterator<Laser> iterator = lasers.iterator(); iterator.hasNext();) {
                Laser l = iterator.next();
                if (l.needsFinish) {
                    l.needsFinish = false;
                    l.mesh.finish(context);
                }
                if (l.render(camera.getVPMatrix())) {
                    iterator.remove();
                }

            }
        }
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        fTime[frame % 100] = timer.stop();
        frame++;
        if(frame >= 100000000){
            frame = 0;
        }
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
        m_viewportW = width;
        m_viewportH = height;
        // Set the projection matrix
        if (IS_3D) {
            camera.setSurface(width, height, 0.1f, 10000.0f);
        } else {
            camera.ortho(1, 1);
        }
    }
}
