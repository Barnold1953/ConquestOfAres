package Game;

import android.util.Log;

/**
 * Created by Nathan on 4/6/2015.
 */
public class Debug {
    public static void logState(GameState state){
        switch(state.currentState){
            case GAME_START:
                Log.d("Debug", "Current State is GAME_START");
                break;
            case SELECTING_TERRITORIES:
                Log.d("Debug", "Current State is SELECTING_TERRITORIES");
                break;
            case PLACING_UNITS:
                Log.d("Debug", "Current State is PLACING_UNITS");
                break;
            case INITIAL_UNIT_PLACEMENT:
                Log.d("Debug", "Current State is INITIAL_UNIT_PLACEMENT");
                break;
            case FORTIFYING:
                Log.d("Debug", "Current State is FORTIFYING");
                break;
            case ATTACKING:
                Log.d("Debug", "Current State is ATTACKING");
                break;
        }
    }

    public static void logRound(GameState state){
        Integer round = state.currentPlayerIndex / state.players.size();
        Log.d("Debug", "PlayerIndex: " + state.currentPlayerIndex + " Players: " + state.players.size() + " Round: " + round.toString());
    }
}