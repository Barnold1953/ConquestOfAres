package Game;
import java.sql.Time;
import java.util.*;

import Generation.MapData;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class GameState {

    public enum State {
        GAME_START,
        SELECTING_TERRITORIES,
        PLACING_UNITS,
        ATTACKING,
        FORTIFYING,
        PLAYING
    }

    public int currentPlayerIndex = -1; ///< Index of the current player into players TODO: Use this
    public int assignedTerritories = 0;
    public State currentState = null;
    public Vector<Player> players = new Vector<Player>(); ///< List of all players TODO: Use this
    public Vector<Territory> territories = new Vector<Territory>(); ///< List of all territories
    public MapData mapData; ///< Map specific data
    public Territory selectedTerritory = null; //< Currently selected territory TODO: Use this
    public Random random = new Random(System.currentTimeMillis());
    // TODO: Statistics? Scores?
    public Vector<Action> actions = new Vector<Action>();
}
