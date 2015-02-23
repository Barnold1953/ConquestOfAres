package Game;
import java.util.*;

import Generation.MapData;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class GameState {
    public int currentPlayerIndex = 0; ///< Index of the current player into players
    public Vector<Player> players = new Vector<Player>();
    public MapData mapData; ///< Contains map information
    // TODO: Statistics? Scores?
    // TODO: Previous Moves?
    //
}
