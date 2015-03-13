package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import Game.GameController;
import Game.GameState;
import Generation.MapGenerationParams;
import Game.GameSettings;
import Graphics.CoARenderer;
import UI.UserInterfaceHelper;

public class GameActivity extends Activity {

    TextView [] toggleButtons;
    Boolean devPanelShown = false;

    private GLSurfaceView mGLSurfaceView;
    private GameState gameState;
    private GameController gameController;
    private CoARenderer coaRenderer;
    private GameSettings gameSettings;

    protected void onCreate( Bundle savedInstanceState ) {
        toggleButtons = new TextView[3];
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gamescreen );

        // Get Game Settings, everything except MapGenParams

        /* COMMENT THIS OUT IF GAME ACTIVITY IS YOUR STARTUP ACTIVITY */
        Intent intent = getIntent();
        if( intent != null ) {
            GameSettings settings = (GameSettings) intent.getParcelableExtra("Settings");
        }

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

    public void toggleDevPanel(View v) {
        LinearLayout parent = (LinearLayout)v.getParent();
        if(parent != null) {
            if (!devPanelShown) {
                devPanelShown = true;
                parent.setBackgroundColor(getResources().getColor(R.color.transparentBlack));
                createPanelTextViews();
                for (int i = 0; i < 3; i++) parent.addView(toggleButtons[i]);
            } else {
                devPanelShown = false;
                parent.setBackgroundColor(getResources().getColor(R.color.transparent));
                for (int i = 0; i < 3; i++) parent.removeView(toggleButtons[i]);
            }
        }
    }

    private void createPanelTextViews() {
        toggleButtons = new TextView[3];
        for( int i = 0; i < 3; i++ ) {
            toggleButtons[i] = new TextView(this);
            toggleButtons[i].setPadding(40,10,40,0);
            toggleButtons[i].setTextSize(20);
            toggleButtons[i].setTextColor(getResources().getColor(R.color.white));
        }

        toggleButtons[0].setText("Graph");
        toggleButtons[0].setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coaRenderer.toggleLines();
            }
        });
        toggleButtons[1].setText("Terrain");
        toggleButtons[1].setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coaRenderer.toggleTerrain();
            }
        });
        toggleButtons[2].setText("Owners");
        toggleButtons[2].setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
