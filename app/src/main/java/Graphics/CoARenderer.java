package Graphics;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

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
import Generation.MapGenerator;
import Sound.SoundManager;
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
    int width, height;
    int m_soldierMoveTexture = 0;
    int m_soldierAttackTexture = 0;
    int m_soldierIdleTexture = 0;
    SpriteBatch m_unitSpriteBatch = new SpriteBatch();
    LinkedList<Laser> lasers = new LinkedList<Laser>();

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
    boolean m_needsLoad = true;
    boolean m_hasLoadedTerritories = false;

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

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
    }

    public void loadResources() {
        try {
            programHandle = ShaderHelper.compileShader(context, R.string.simple_vert, R.string.texture_frag, "simple");
        }
        catch (IOException e){
            Log.d("Shader", "Error occurred during compilation");
        }

        m_soldierMoveTexture = TextureHelper.imageToTexture(context, R.drawable.man_run, "soldier_move");
        m_soldierIdleTexture = TextureHelper.imageToTexture(context, R.drawable.man_idle, "soldier_idle");
        m_soldierAttackTexture = TextureHelper.imageToTexture(context, R.drawable.man_shoot, "soldier_attack");
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        if (m_needsLoad) {
            reload();
        }

        // Upload mapData texture (Happens once)
        if (gameState != null && gameState.mapData.isDoneGenerating && !m_hasLoadedTerritories) {
            finishTerritories();
        }

        // Clear color and depth buffers
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Update GPU generation
        if (GpuGenerator.hasGenRequest) {
            GpuGenerator.updateGen(context);
            MapGenerator.finishGeneration((int)gameState.mapData.width, (int)gameState.mapData.height);
            GLES20.glViewport(0, 0, m_viewportW, m_viewportH);
        }

        // Render territories
        for (Territory t: gameState.mapData.territories) {
            t.updateAnimation();
            if (t.mesh != null) t.mesh.render(t, t.texture, camera.getVPMatrix());
        }

        // Draw sprites
        drawUnits();

        // Render the lasers
        drawLasers();

        // Count frames for animation purposes
        frame++;
    }

    // Needs to reload everything
    public void onResume() {
        m_needsLoad = true;
    }

    public void reload() {
        ShaderHelper.clearAll();
        loadResources();
        lasers.clear();
        m_unitSpriteBatch.m_vbo = null;
        ColorMesh.m_programHandle = 0;
        TerritoryMesh.m_programHandle = 0;
        if (gameState != null && gameState.mapData.isDoneGenerating) {
            finishTerritories();
        }
        m_needsLoad = false;
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

    // Only used by drawUnits for randomness in animation
    private int fastHash(int x) {
        x = (x ^ 61) ^ (x >> 16);
        x = x + (x << 3);
        x = x ^ (x >> 4);
        x = x * 0x27d4eb2d;
        x = x ^ (x >> 15);
        return x;
    }

    private void drawUnits() {
        Player currentPlayer = gameState.players.get(gameState.currentPlayerIndex%gameState.players.size());

        m_unitSpriteBatch.begin();
        for (Territory t : gameState.territories) {
            // If we are placing units, only show current player units
            if (gameState.currentState == GameState.State.INITIAL_UNIT_PLACEMENT && t.owner != currentPlayer)
                continue;

            synchronized (t.units) {
                for (Unit u : t.units) {
                    // Update movement
                    updateUnitPosition(u);
                    // Get texture coordinates. We add id * 7 + id to get some randomness
                    SpriteSheetDimensions dims = new SpriteSheetDimensions("soldier_move", frame / 5 + fastHash(u.id));
                    // Get texture
                    int texture;
                    switch (u.type) {
                        case soldier_attack:
                            texture = m_soldierAttackTexture;
                            break;
                        case soldier_move:
                            texture = m_soldierMoveTexture;
                            break;
                        default:
                            texture = m_soldierIdleTexture;
                            break;
                    }

                    // Render the texture
                    m_unitSpriteBatch.draw(texture,
                            (u.location.x / gameState.mapData.width) * 2.0f - 1.0f,
                            (u.location.y / gameState.mapData.height) * 2.0f - 1.0f,
                            0.1f, 0.1f, dims.u, dims.v, dims.uw, dims.vw, u.angle, t.owner.color);
                }
            }
        }
        // Actually render to the screen
        m_unitSpriteBatch.end();
        m_unitSpriteBatch.render(camera);
    }

    private void drawLasers(){
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
    }

    // Updates position of unit
    private void updateUnitPosition(Unit u){
        final float SPEED = 2.0f;
        if (u.destination == u.location) {
            u.type = u.inCombat ? Unit.Type.soldier_attack : Unit.Type.soldier_idle;
            return;
        }

        // Calculate normal vector towards destination
        float dx = u.destination.x - u.location.x;
        float dy = u.destination.y - u.location.y;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        // Don't want to divide by 0
        if (length == 0.0f) {
            u.type = u.inCombat ? Unit.Type.soldier_attack : Unit.Type.soldier_idle;
            u.location.x = u.destination.x;
            u.location.y = u.destination.y;
            return;
        }
        u.type = u.inCombat ? Unit.Type.soldier_attack : Unit.Type.soldier_move;

        // Normalize
        dx = dx / length;
        dy = dy / length;

        // Update position if we aren't defending
        if (!u.isDefending) {
            u.location.x += dx * SPEED;
            if (dx < 0 && u.location.x < u.destination.x) {
                u.location.x = u.destination.x;
            } else if (dx > 0 && u.location.x > u.destination.x) {
                u.location.x = u.destination.x;
            }
            u.location.y += dy * SPEED;
            if (dy < 0 && u.location.y < u.destination.y) {
                u.location.y = u.destination.y;
            } else if (dy > 0 && u.location.y > u.destination.y) {
                u.location.y = u.destination.y;
            }
        }

        // Calculate desired angle
        float angle = (float)Math.acos(dy);
        if (dx > 0.0f) angle = -angle;
        // Adjust actual angle
        if (u.angle < angle) {
            u.angle += 0.1f;
            if (u.angle > angle) u.angle = angle;
        } else if (u.angle > angle) {
            u.angle -= 0.1f;
            if (u.angle < angle) u.angle = angle;
        }
    }

    private void finishTerritories() {
        Log.d("hello", "LOL");
        for (Territory t : gameState.territories) {
            t.texture = TextureHelper.dataToTexture(t.pixelBuffer, "t" + t.index, t.textureWidth, t.textureHeight);
            t.mesh = new TerritoryMesh();
            t.mesh.init(((float)t.textureX / gameState.mapData.width) * 2.0f - 1.0f,
                    ((float)t.textureY / gameState.mapData.height) * 2.0f - 1.0f,
                    ((float)t.textureWidth / gameState.mapData.width) * 2.0f,
                    ((float)t.textureHeight / gameState.mapData.height) * 2.0f,
                    context);
        }

        m_hasLoadedTerritories = true;
    }
}
