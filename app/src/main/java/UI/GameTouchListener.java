package UI;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import Game.GameController;
import Game.GameState;
import Generation.MapGenerator;
import Utils.Utils;
import utkseniordesign.conquestofares.GameActivity;

/**
 * Created by lasth_000 on 4/18/2015.
 */
public class GameTouchListener implements View.OnTouchListener {
    enum TouchState {
        IDLE,
        ZOOM,
        DRAG,
        SELECT
    }
    TouchState mState = TouchState.IDLE;
    GameController mGameController = null;
    GameActivity parentActivity = null;
    PointF touch1 = new PointF(); // touch coordinates of first finger (pointer)
    PointF touch2 = new PointF(); // touch coordinates of second finger (pointer)
    PointF touchDist = new PointF(); // coordinate distance between the touch coordinates
    double distance; // actual distance between the two touch points

    public GameTouchListener(GameActivity gameActivity, GameController controller) {
        // Feel free to add parameters you may need as class arguments
        mGameController = controller;
        parentActivity = gameActivity;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch(e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mState = TouchState.SELECT;
                touch1 = getRawCoords(v,e.getX(0),e.getY(0));;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mState = TouchState.ZOOM;
                touch2 = getRawCoords(v,e.getX(1),e.getY(1));
                touchDist.x = touch1.x - touch2.x;
                touchDist.y = touch1.y - touch2.y;
                distance = Math.sqrt(touchDist.x*touchDist.x + touchDist.y*touchDist.y);
                break;
            case MotionEvent.ACTION_MOVE:
                PointF newTouch1 = getRawCoords(v,e.getX(),e.getY());
                if(mState == TouchState.SELECT) {
                    // allow some tolerance for movement before considering it a "drag"
                    float touch1dx = touch1.x - newTouch1.x;
                    float touch1dy = touch1.y - newTouch1.y;
                    float distanceFromInitial = (touch1dx*touch1dx + touch1dy*touch1dy);
                    if(distanceFromInitial > 20.0) mState = TouchState.DRAG;
                }
                if(mState == TouchState.DRAG) {
                    PointF newTouchLocation = getRawCoords(v, e.getX(), e.getY());

                    // DO DRAG COMMANDS HERE
                    Log.i("GameTouchListener","Dragging");
                } else if (mState == TouchState.ZOOM) {
                    touch1 = getRawCoords(v,e.getX(0),e.getY(0));
                    touch2 = getRawCoords(v,e.getX(1),e.getY(1));
                    touchDist.x = touch1.x - touch2.x;
                    touchDist.y = touch1.y - touch2.y;
                    distance = Math.sqrt(touchDist.x*touchDist.x + touchDist.y*touchDist.y);

                    // DO ZOOM COMMANDS HERE
                    Log.i("GameTouchListener","Zooming");
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mState = TouchState.DRAG;
                break;
            case MotionEvent.ACTION_UP:
                if(mState == TouchState.SELECT) {
                    handleTouchEvents();
                }
        }
        return true;
    }

    public void handleTouchEvents() {
        if(mGameController.getGameState().selectedTerritory == null ||
                mGameController.getGameState().currentState == GameState.State.GAME_START ||
                mGameController.getGameState().currentState == GameState.State.PLACING_UNITS  ||
                mGameController.getGameState().currentState == GameState.State.INITIAL_UNIT_PLACEMENT ||
                mGameController.getGameState().selectedTerritory.selectedUnits.size() == 0
        ) parentActivity.handleTerritorySelect(touch1.x, touch1.y);
        else if(mGameController.getGameState().selectedTerritory.selectedUnits.size() > 0 &&
                mGameController.getGameState().currentState == GameState.State.FORTIFYING
        ) parentActivity.handleUnitMove(touch1.x, touch1.y);
        else if(mGameController.getGameState().selectedTerritory.selectedUnits.size() > 0 &&
                mGameController.getGameState().currentState == GameState.State.ATTACKING
        ) parentActivity.handleUnitAttack(touch1.x, touch1.y);
    }

    public PointF getRawCoords(View v, float x, float y) {
        PointF p = new PointF();
        int locationOfView[] = {0,0};
        v.getLocationOnScreen(locationOfView);
        Log.i("GameTouchListener", "Relative: " + Float.toString(x) + " " + Float.toString(y));
        p.x = x + locationOfView[0];
        p.y = y + locationOfView[1];
        Log.i("GameTouchListener","Raw: " + Float.toString(p.x) + " " + Float.toString(p.y));
        p = Utils.translateCoordinatePair(p.x, p.y, MapGenerator.mapData.params.mapSize);

        Log.i("GameTouchListener","Converted: " + Float.toString(p.x) + " " + Float.toString(p.y));
        return p;
    }
}
