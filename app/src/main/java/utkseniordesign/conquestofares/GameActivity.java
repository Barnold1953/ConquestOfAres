package utkseniordesign.conquestofares;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gamescreen );
        mainView = (RelativeLayout) findViewById(R.id.gameScreen);
        territoryPanel = (TerritoryPanel)findViewById(R.id.territoryLayout);
        gamePlayBanner = (GamePlayBanner)findViewById(R.id.gameplayBanner);

        // Get Game Settings, everything except MapGenParams
        Intent intent = getIntent();
        if( intent != null ) {
            gameSettings = (GameSettings) intent.getParcelableExtra("Settings");
        } else {
            gameSettings = new GameSettings();
        }

        // Initlialize device
        Utils.getScreenDimensions(this);

        // Initialize the glSurfaceView
        mGLSurfaceView = ( GLSurfaceView ) findViewById( R.id.glRenderArea );
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        coaRenderer = new CoARenderer(this, size.x, size.y);
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
        gamePlayBanner.setUpPanel(this);
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
                    float coordx = event.getRawX();
                    float coordy = event.getRawY();

                    // translate touch event to OpenGL coordinates, and scale to mapSize
                    PointF coords = Utils.translateCoordinatePair(coordx,coordy,gameSettings.getMapGenParams().mapSize);

                    if(
                        gameState.selectedTerritory == null ||
                        gameState.currentState == GameState.State.GAME_START ||
                        gameState.currentState == GameState.State.PLACING_UNITS  ||
                        gameState.currentState == GameState.State.INITIAL_UNIT_PLACEMENT ||
                        gameState.selectedTerritory.selectedUnits.size() == 0
                    ) handleTerritorySelect(coords.x, coords.y);
                    else if(
                        gameState.selectedTerritory.selectedUnits.size() > 0 &&
                        gameState.currentState == GameState.State.FORTIFYING
                    ) handleUnitMove(coords.x, coords.y);
                    else if(gameState.selectedTerritory.selectedUnits.size() > 0 &&
                            gameState.currentState == GameState.State.ATTACKING
                    ) handleUnitAttack(coords.x, coords.y);
                }
                return true;
            }
        });

        checkMark.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (gameState.selectedTerritory != null) {
                        gameState.selectedTerritory.unselect();
                        gameState.selectedTerritory = null;
                    }
                    if (territoryPanel.isVisible()) territoryPanel.toggle();
                    gameController.stepState();
                    if (gameController.getGameState().currentState == GameState.State.PLACING_UNITS ||
                        gameController.getGameState().currentState == GameState.State.INITIAL_UNIT_PLACEMENT )
                        setCheckMark(false);
                    if (gameController.stateHasChanged) {
                        gamePlayBanner.refresh(); // gets new banner
                        gameController.stateHasChanged = false;
                    } else gamePlayBanner.changeContent(); // updates current banner
                }
                return true;
            }
        });
    }

    public void handleTerritorySelect(float x, float y) {
        Territory oldTerritory = gameController.getGameState().selectedTerritory;
        Territory newTerritory = gameController.onClick(x, y);
        // Check if selected territory changed
        if (oldTerritory == newTerritory) {
            oldTerritory.selectedUnits.removeAllElements();
            territoryPanel.toggle();
        } else if (oldTerritory == null) {
            territoryPanel.update(newTerritory);
            territoryPanel.toggle();
        } else {
            territoryPanel.update(newTerritory);
            oldTerritory.selectedUnits.removeAllElements();
        }
    }

    public void handleUnitMove(float x, float y) {
        Log.d("GameActivity", "HandleUnitMove called");
        Territory moveTo = gameController.getTerritoryAtPoint(x, y);
        Territory selectedTerritory = gameController.getGameState().selectedTerritory;
        if(selectedTerritory.neighbors.contains(moveTo) && moveTo.owner == selectedTerritory.owner) {
            gameController.moveUnits(gameController.getGameState().selectedTerritory, moveTo);
            selectedTerritory.selectedUnits.removeAllElements();
            selectedTerritory.unselectNeighbors();
            selectedTerritory.select();
            territoryPanel.update(selectedTerritory);
        }
    }

    public void handleUnitAttack(float x, float y){
        Territory attack = gameController.getTerritoryAtPoint(x,y);
        Territory selectedTerritory = gameController.getGameState().selectedTerritory;
        if(selectedTerritory.neighbors.contains(attack) && attack.owner != selectedTerritory.owner && attack.owner != null) {
            gameController.attack(selectedTerritory, attack, coaRenderer);
            selectedTerritory.selectedUnits.removeAllElements();
            selectedTerritory.unselectNeighbors();
            selectedTerritory.select();
            territoryPanel.update(selectedTerritory);
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
