package Game;

import java.util.*;
import Generation.MapGenerator;
import Generation.MapGenerationParams;

/**
 * Created by brb55_000 on 2/6/2015.
 */

public class GameController {
    private MapGenerator mapGenerator = new MapGenerator(); ///< Generates the map

    /// Initializes a game by setting up game state and map
    /// @param gameState: The game state
    /// @param mapGenParams: Parameters for map generation
    public void initGame(GameState gameState, MapGenerationParams mapGenParams) {
        // Generate the map
        mapGenerator.generateMap(mapGenParams);
        // Assign territories
    }

    private void assignTerritories(GameState gameState, Vector<Territory> territories) {

    }
}
