package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;

import java.util.HashMap;

import Game.GameController;
import Game.GameState;
import Generation.MapGenerationParams;
import Game.GameSettings;
import Graphics.CoARenderer;
import UI.UserInterfaceHelper;

public class GameActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private GameState gameState;
    private GameController gameController;
    private CoARenderer coaRenderer;
    private GameSettings gameSettings;

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gamescreen );

        // Get Game Settings, everything except MapGenParams
        Intent intent = getIntent();
        GameSettings settings = (GameSettings) intent.getParcelableExtra("Settings");
        UserInterfaceHelper.createDialog( this, String.valueOf(settings.getNumPlayers()), "Player Count");
        UserInterfaceHelper.createDialog( this, settings.getMapGenParams().mapSymmetry.toString(), "Map Symmetry");
        UserInterfaceHelper.createDialog( this, settings.getMapGenParams().mapSize.toString(), "Map Size");
        UserInterfaceHelper.createDialog( this, String.valueOf(settings.getTurnLength()), "Turn Length");
        UserInterfaceHelper.createDialog( this, String.valueOf(settings.getTerritoriesForVictory()), "Victory Condition");
        UserInterfaceHelper.createDialog( this, String.valueOf(settings.isHorizontalWrap()), "Horizontal Wrap");

        // Initialize the glSurfaceView
        mGLSurfaceView = ( GLSurfaceView ) findViewById( R.id.glRenderArea );
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        coaRenderer = new CoARenderer(this);
        gameController = new GameController();
        // TODO:Set these with the settings menu parameters
        gameSettings = new GameSettings();
        mGLSurfaceView.setRenderer(coaRenderer);

        // Init the game
        GameState gameState = new GameState();
        gameController.initGame(gameState, gameSettings);
        coaRenderer.setGameState(gameState);
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
