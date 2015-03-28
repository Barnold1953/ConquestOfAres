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

    private GameState m_gameState = null; ///< Handle to game
    private GameSettings m_gameSettings = null; ///< Settings TODO(Aaron): pass in good settings in initGame
    private GameEngine m_gameEngine = new GameEngine(); ///< Initializes the game
    private Player m_currentPlayer = null;

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings) {
        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameSettings = gameSettings;
       // Initialize the game
        m_gameEngine.initGame(m_gameState, m_gameSettings);
        m_gameState.currentPlayerIndex = -1; // Start at -1 so nextTurn goes to 0
    }

    /// Call this to transition to the next turn
    void nextTurn() {
        // Go to next player (starts at -1)
        m_gameState.currentPlayerIndex++;
        if (m_gameState.currentPlayerIndex >= m_gameState.players.size()) m_gameState.currentPlayerIndex = 0;
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex);
        // Check if we should do AI
        if (m_currentPlayer.isAI) {
            // TODO: Do AI stuff
            nextTurn(); // Recursively go to next turn
            return;
        }
        // Current player is human, he is now placing units
        m_gameState.currentState = GameState.State.PLACING_UNITS;
    }

    /// Call this method when the world is clicked on
    public Territory onClick(float x, float y) {
        // TODO: Implement
        Territory t = getTerritoryAtPoint(x,y);
        // TODO: Handle unit transfer
        /*switch (m_gameState.currentState) {
            case PLACING_UNITS:
                // TODO: Implement
                break;
            case PLAYING:
                // TODO: Implement
                break;
        }*/
        return t;
    }

    /// Returns the territory at a specific point
    Territory getTerritoryAtPoint(float x, float y) {
        return MapGenerator.getClosestTerritory(x, y, m_gameState.territories);
    }
}
