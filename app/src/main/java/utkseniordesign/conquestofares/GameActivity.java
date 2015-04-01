package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.HashMap;

import Game.GameController;
import Game.GameState;
import Game.Territory;
import Generation.MapGenerationParams;
import Game.GameSettings;
import Graphics.CoARenderer;
import UI.TerritoryPanel;
import UI.UserInterfaceHelper;

public class GameActivity extends Activity {

    int screenHeight = 0;
    int screenWidth = 0;
    TextView [] toggleButtons;
    TerritoryPanel territoryPanel = null;
    View.OnTouchListener touchListener = null;
    Boolean devPanelShown = false;
    Boolean territoryPanelShown = false;

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
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;

        Log.d("Display width: ", Integer.toString(size.x));
        Log.d("Display height: ", Integer.toString(size.y));

        Intent intent = getIntent();
        if( intent != null ) {
            gameSettings = (GameSettings) intent.getParcelableExtra("Settings");
        } else {
            gameSettings = new GameSettings();
        }

        // Initialize the glSurfaceView
        mGLSurfaceView = ( GLSurfaceView ) findViewById( R.id.glRenderArea );
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        coaRenderer = new CoARenderer(this);
        gameController = new GameController();

        mGLSurfaceView.setRenderer(coaRenderer);

        // Init the game
        final GameState gameState = new GameState();
        gameController.initGame(gameState, gameSettings);
        coaRenderer.setGameState(gameState);

        // Get game screen touch listener
        findViewById(R.id.glRenderArea).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout parent = (FrameLayout)v.getParent();
                Territory territory = gameController.onClick(event.getX(),event.getY());
                if( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    if (gameState.selectedTerritory == territory) {
                        gameState.selectedTerritory = null;
                        territoryPanelShown = false;
                        toggleTerritoryPanel(false);
                    } else if (gameState.selectedTerritory == null) {
                        getTerritoryMenu(parent, territory);
                        territoryPanelShown = true;
                        toggleTerritoryPanel(true);
                    } else {
                        getTerritoryMenu(parent, territory);
                    }
                    gameState.selectedTerritory = territory;
                }
                return true;
            }
        } );
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

    public void toggleTerritoryPanel(Boolean show) {
        if(show) {
            // if the panel is in fully hidden or fully shown position, good, otherwise the user jumped the gun, rapid clicking, ignore it
            territoryPanel.animate().y(screenHeight - territoryPanel.getHeight());

        } else {
            territoryPanel.animate().y(screenHeight);
        }
    }

    public void getTerritoryMenu(FrameLayout parent, Territory territory) {
        if(territoryPanel == null) {
            territoryPanel = new TerritoryPanel(getBaseContext(), territory, screenHeight);
            parent.addView(territoryPanel);
        } else territoryPanel.update(territory);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
