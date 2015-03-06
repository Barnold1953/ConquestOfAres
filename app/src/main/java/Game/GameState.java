package Game;
import java.util.*;

import Generation.MapData;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class GameState {

    enum State {
        PLACING_UNITS,
        PLAYING
    }

    public int currentPlayerIndex = 0; ///< Index of the current player into players TODO: Use this
    State currentState;
    public Vector<Player> players = new Vector<Player>(); ///< List of all players TODO: Use this
    public Vector<Territory> territories = new Vector<Territory>(); ///< List of all territories
    public MapData mapData; ///< Map specific data
    public Territory selectedTerritory = null; //< Currently selected territory TODO: Use this
    // TODO: Statistics? Scores?
    // TODO: Previous Moves?
}
