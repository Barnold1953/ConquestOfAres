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

/// Contains the game logic core
public class GameController {

    private MapGenerator mapGenerator = new MapGenerator(); ///< Generates the map
    private GameState m_gameState = null; ///< Handle to game
    private GameSettings m_gameSettings = null; ///< Settings TODO(Aaron): pass in good settings in initGame
    private GameEngine m_gameEngine = new GameEngine; ///< For separating init logic

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings) {
        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameSettings = gameSettings;
        // Generate the map
        MapData mapData = mapGenerator.generateMap(gameSettings.mapGenParams);
        m_gameState.territories = mapData.territories;
        m_gameState.mapData = mapData;
        // Initialize players
        initPlayers(m_gameSettings.numPlayers, m_gameSettings.numAI);
        // Assign territories
        assignTerritories();
        // Place units
        initUnits();
        Log.d("Init: ", "initGame finished.");
    }

    private void initPlayers(int numPlayers, int numAI) {
        // TODO: Set up all the players and assign random colors
    }

    /// Assignes territories to players based on the TerritoryDistMode
    private void assignTerritories() {
        // Set gamestate
        //gameState.territories = gameSettings.mapGenParams.territories;

        // Assign territories to players
        switch (m_gameSettings.territoryDistMode) {
            case RANDOM:
                // TODO: Implement. Randomly loop through each territories and assign to players
                break;
            case ROUND_ROBIN:
                // TODO: This needs MP stuff or AI probably.
                break;
        }
    }

    /// Call this method when the world is clicked on
    void onClick(float x, float y) {
        // TODO: Implement
        // TODO: Get territory that was clicked
        // TODO: Handle unit transfer
    }

    /// Returns the territory at a specific point
    Territory getTerritoryAtPoint(float x, float y) {
        return MapGenerator.getClosestTerritory(x, y, m_gameState.territories);
    }

    /// Sets up all the initial armies
    private void initUnits() {
        // TODO: Implement
    }
}
