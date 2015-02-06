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
    boolean isMultiplayer = false; ///< True when multiplayer mode is active
    boolean isHorizontalWrap = false; ///< True when the map wraps horizontally
    int numPlayers = 0; ///< Number of players in the game
    int territoriesForVictory = -1; ///< Number of territories needed for victory. -1 means all
    int maxTurnLength = -1; ///< Max turn length in minutes. -1 means no limit
    int mapSeed = -1; ///< Random seed for map generation. -1 = random
    TerritoryDistributionMode territoryDistMode = TerritoryDistributionMode.RANDOM; ///< How to distribute territory
    MapGenerationParams mapGenParams; ///< Map generation specific data
    // TODO: Other settings
}
