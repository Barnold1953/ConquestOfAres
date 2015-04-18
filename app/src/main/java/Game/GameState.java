package Game;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.sql.Time;
import java.util.*;

import Generation.MapData;

/**
 * Created by brb55_000 on 2/6/2015.
 */

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class GameState {
    GameState() {} //stub constructor for jason reconstruction
    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
    public enum State {
        NOT_MY_TURN,
        GAME_START,
        SELECTING_TERRITORIES,
        INITIAL_UNIT_PLACEMENT,
        PLACING_UNITS,
        ATTACKING,
        FORTIFYING
    }

    public int currentPlayerIndex = -1; ///< Index of the current player into players TODO: Use this
    public int assignedTerritories = 0;
    public State currentState = null;
    public int numPlayers = 0;
    public int initialUnits = 10;
    public Vector<Player> players = new Vector<Player>(); ///< List of all players
    public Vector<Territory> territories = new Vector<Territory>(); ///< List of all territories
    transient public MapData mapData; ///< Map specific data
    transient public Territory selectedTerritory = null; //< Currently selected territory TODO: Use this
    transient public Random random = new Random(System.currentTimeMillis());
    transient public Vector<Action> actions = new Vector<Action>();
    public Boolean isSetup = false; ///< Specifies whether initial territory choosing/unti placement is complete.
    public GameSettings gameSettings = null;
}
