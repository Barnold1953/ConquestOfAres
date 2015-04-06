package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Game.GameController;
import Game.GameState;
import Game.Territory;
import Game.GameSettings;
import Graphics.CoARenderer;
import UI.GamePlayBanner;
import UI.TerritoryPanel;
import Utils.Device;
import Utils.Utils;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class GameActivity extends Activity {
    TerritoryPanel territoryPanel = null;
    GamePlayBanner gamePlayBanner = null;
    ImageView checkMark = null;
    RelativeLayout mainView = null;

    private GLSurfaceView mGLSurfaceView;
    private GameController gameController;
    private CoARenderer coaRenderer;
    private GameSettings gameSettings;

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gamescreen );
        mainView = (RelativeLayout) findViewById(R.id.gameScreen);
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
        //Device.screenHeight = (int)dm.heightPixels;
        //Device.screenWidth = (int)dm.widthPixels;
        Point point = Utils.getScreenDimensions(this);
        Device.screenHeight = point.y;
        Device.screenWidth = point.x;
        //Device.screenHeight = mGLSurfaceView.getMeasuredHeight();
        //Device.screenWidth = mGLSurfaceView.getMeasuredWidth();


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
        checkMark = (ImageView) findViewById(R.id.checkMark);
        setListeners();
    }

    public void setListeners() {
        final GameState gameState = gameController.getGameState();
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
                    Territory oldTerritory = gameState.selectedTerritory;
                    Territory newTerritory = gameController.onClick(coordx, coordy);
                    // Check if selected territory changed
                    if (oldTerritory == newTerritory) {
                        setShowTerritoryPanel(false);
                    } else if (oldTerritory == null) {
                        updateTerritoryMenu(newTerritory);
                        setShowTerritoryPanel(true);
                    } else {
                        updateTerritoryMenu(newTerritory);
                    }
                }
                return true;
            }
        });

        checkMark.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                }
                return true;
            }
        });
    }

    public void setShowTerritoryPanel(Boolean show) {
        if(show) {
            // if the panel is in fully hidden or fully shown position, good, otherwise the user jumped the gun, rapid clicking, ignore it
            territoryPanel.animating = YoYo.with(Techniques.SlideInUp).duration(500).playOn(territoryPanel);
        } else {
            territoryPanel.animating = YoYo.with(Techniques.SlideOutDown).duration(500).playOn(territoryPanel);
        }
    }

    public void setCheckMark(Boolean show) {
        if(show) {
            if(checkMark.getVisibility()==View.GONE) checkMark.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.RollIn).duration(500).playOn(checkMark);
        } else {
            YoYo.with(Techniques.RollOut).duration(500).playOn(checkMark);
        }
    }

    public void updateTerritoryMenu(Territory territory) {
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
