package Game;
import Generation.MapGenerationParams;
/**
 * Created by brb55_000 on 2/6/2015.
 */

enum TerritoryDistributionMode {
    RANDOM,
    ROUND_ROBIN
}

public class GameSettings {
    public boolean isMultiplayer = false; ///< True when multiplayer mode is active
    public boolean isHorizontalWrap = false; ///< True when the map wraps horizontally
    public int numPlayers = 0; ///< Number of players in the game
    public int territoriesForVictory = -1; ///< Number of territories needed for victory. -1 means all
    public int maxTurnLength = -1; ///< Max turn length in minutes. -1 means no limit
    public int mapSeed = -1; ///< Random seed for map generation. -1 = random
    public TerritoryDistributionMode territoryDistMode = TerritoryDistributionMode.RANDOM; ///< How to distribute territory
    public MapGenerationParams mapGenParams; ///< Map generation specific data
    // TODO: Other settings
}
