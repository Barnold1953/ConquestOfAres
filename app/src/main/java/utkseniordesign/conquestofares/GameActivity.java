package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import Game.GameController;
import Game.GameState;
import Game.Territory;
import Game.GameSettings;
import Graphics.CoARenderer;
import UI.GamePlayBanner;
import UI.TerritoryPanel;
import UI.UserInterfaceHelper;
import Utils.Device;
import Utils.Utils;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class GameActivity extends Activity {
    TerritoryPanel territoryPanel = null;
    GamePlayBanner gamePlayBanner = null;
    FrameLayout mainView = null;

    private GLSurfaceView mGLSurfaceView;
    private GameController gameController;
    private CoARenderer coaRenderer;
    private GameSettings gameSettings;

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gamescreen );
        mainView = (FrameLayout) findViewById(R.id.gameScreen);
        territoryPanel = (TerritoryPanel)findViewById(R.id.territoryLayout);

        // Get Game Settings, everything except MapGenParams
        Intent intent = getIntent();
        if( intent != null ) {
            gameSettings = (GameSettings) intent.getParcelableExtra("Settings");
        } else {
            gameSettings = new GameSettings();
        }

        // Initlialize device
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Device.screenHeight = (int)dm.heightPixels;
        Device.screenWidth = (int)dm.widthPixels;
        //Point point = Utils.getScreenDimensions(this);
        //Device.screenHeight = point.y;
        //Device.screenWidth = point.x;

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
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d("Listener", "(" + event.getX() + ", " + event.getY() + ")");
               // Territory territory = gameController.onClick(event.getX(), event.getY());

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float coordx = event.getX();
                    float coordy = event.getY();
                    float[] coords = Utils.translateCoordinatePair(coordx,coordy,gameSettings.getMapGenParams().mapSize);
                    coordx = coords[0];
                    coordy = coords[1];
                    //Log.d("Coordinates:",Float.toString(coordx) + " " + Float.toString(coordy));
                    Territory territory = gameController.onClick(coordx, coordy);
                    if (gameState.selectedTerritory == territory) {
                        gameState.selectedTerritory = null;
                        toggleTerritoryPanel(false);
                    } else if (gameState.selectedTerritory == null) {
                        gameState.selectedTerritory = territory;
                        getTerritoryMenu(territory);
                        toggleTerritoryPanel(true);
                    } else {
                        gameState.selectedTerritory = territory;
                        getTerritoryMenu(territory);
                    }
                }
                return true;
            }
        });

        createScreen();
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

    public void createScreen() {
        gamePlayBanner = new GamePlayBanner(getBaseContext());
        gamePlayBanner.changeContent(gameController.getGameState());
        mainView.addView(gamePlayBanner);
        territoryPanel.setUpPanel(this);
    }

    public void toggleTerritoryPanel(Boolean show) {
        if(show) {
            // if the panel is in fully hidden or fully shown position, good, otherwise the user jumped the gun, rapid clicking, ignore it
            territoryPanel.animating = YoYo.with(Techniques.SlideInUp).duration(500).playOn(territoryPanel);
        } else {
            territoryPanel.animating = YoYo.with(Techniques.SlideOutDown).duration(500).playOn(territoryPanel);
        }
    }

    public void getTerritoryMenu(Territory territory) {
        if(territoryPanel.getVisibility()==View.GONE) {
            territoryPanel.update(territory);
            territoryPanel.setVisibility(View.VISIBLE);
        } else territoryPanel.update(territory);
    }

    public GamePlayBanner getGamePlayBanner() {
        return gamePlayBanner;
    }

    public GameController getGameController() {
        return gameController;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
