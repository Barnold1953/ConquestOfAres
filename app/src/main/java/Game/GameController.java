package Game;

import android.content.Context;
import android.util.Log;

import java.util.*;

import Generation.MapData;
import Generation.MapGenerator;
import Generation.MapGenerationParams;

/**
 * Created by brb55_000 on 2/6/2015.
 */

public class GameController {
    private MapGenerator mapGenerator = new MapGenerator(); ///< Generates the map

    /// Initializes a game by setting up game state and map
    /// @param gameState: The game state
    /// @param gameSettings: Settings for the game
    public void initGame(GameState gameState, GameSettings gameSettings) {
        // Generate the map
        MapData mapData = mapGenerator.generateMap(gameSettings.mapGenParams);
        gameState.territories = mapData.territories;
        gameState.mapData = mapData;
        // Assign territories
        assignTerritories(gameState, gameSettings);
        // Place units
        initUnits(gameState);
        Log.d("Init: ", "INITIALIZING****************************");
    }

    /// Assignes territories to players based on the TerritoryDistMode
    /// @param gameState: The game state
    /// @param gameSettings: The game settings
    private void assignTerritories(GameState gameState, GameSettings gameSettings) {
        // Set gamestate
        //gameState.territories = gameSettings.mapGenParams.territories;

        // Assign territories to players
        switch (gameSettings.territoryDistMode) {
            case RANDOM:
                // TODO: Implement
                break;
            case ROUND_ROBIN:
                // TODO: This needs MP stuff or AI
                break;
        }
    }

    /// Sets up all the initial armies
    /// @param gameState: The game state
    private void initUnits(GameState gameState) {
        // TODO: Implement
    }
}
