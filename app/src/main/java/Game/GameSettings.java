package Game;

/**
 * Created by brb55_000 on 2/6/2015.
 */

enum TerritoryDistributionMode {
    RANDOM,
    ROUND_ROBIN
}

enum MapSize {
    CRAMPED,
    SMALL,
    AVERAGE,
    LARGE
}

enum MapSymmetry {
    NONE,
    HORIZONTAL,
    VERTICAL,
    RADIAL
}

public class GameSettings {
    boolean isMultiplayer = false; ///< True when multiplayer mode is active
    boolean isHorizontalWrap = false; ///< True when the map wraps horizontally
    int numPlayers = 0; ///< Number of players in the game
    int territoriesForVictory = -1; ///< Number of territories needed for victory. -1 means all
    int maxTurnLength = -1; ///< Max turn length in minutes. -1 means no limit
    TerritoryDistributionMode territoryDistMode = TerritoryDistributionMode.RANDOM; ///< How to distribute territory
    MapSize mapSize = MapSize.AVERAGE; ///< Size of map
    MapSymmetry mapSymmetry = MapSymmetry.NONE; ///< Symmetry of generated map

    // TODO: Other settings
}
