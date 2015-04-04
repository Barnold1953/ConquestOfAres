package Game;

import android.content.Context;
import android.util.Log;

import java.util.*;

import Generation.MapData;
import Generation.MapGenerator;
import Generation.MapGenerationParams;
import Graphics.Quadrilateral;
import Graphics.SpriteBatchSystem;

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
        m_gameState.currentState = GameState.State.GAME_START;
        m_gameSettings = gameSettings;
        // Initialize the game
        m_gameEngine.initGame(m_gameState, m_gameSettings, this);
        m_gameState.currentPlayerIndex = 0; // Start at -1 so nextTurn goes to 0
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex);
    }

    public GameState getGameState(){
        return m_gameState;
    }

    public Player getCurrentPlayer() { return m_currentPlayer; }

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
        Territory territory = getTerritoryAtPoint(x, y);
        return territory;
    }

    /// Returns the territory at a specific point
    Territory getTerritoryAtPoint(float x, float y) {
        return MapGenerator.getClosestTerritory(x, y, m_gameState.territories);
    }

    boolean attack(Territory attacker, Territory defender){
        Action action = new Action(m_currentPlayer, Action.Category.attack, attacker, defender);

        while(attacker.units.size() > 0 && defender.units.size() > 0){
            // I figure we can change the chance of winning based on the type of unit it is, like tanks are weak to airplanes, airplanes are weak to soldiers, and soldiers are weak to tanks
            // kind of like a rock-paper-scissors dynamic

            if(m_gameState.random.nextInt() % 2 == 0){
                action.sUnitsLost.add(attacker.units.get(attacker.units.size()-1));
                attacker.units.remove(attacker.units.size()-1);
            }
            else{
                action.dUnitsLost.add(defender.units.get(defender.units.size()-1));
                defender.units.remove(defender.units.size()-1);
            }
        }
        return !attacker.units.isEmpty();
    }

    void moveUnit(Territory source, Territory destination){
        Action action = new Action(m_currentPlayer, Action.Category.moveUnit, source, destination);

        Unit unit = source.units.remove(source.units.size()-1);
        action.sUnitsLost.add(unit);
        action.dUnitsGained.add(unit);
        m_gameState.actions.add(action);

        //addUnit(destination, destination.x, destination.y, unit.type);

        unit.path = new PathFinding().getPath(source, destination);
        unit.frame = 0;
        unit.location = new float[] {source.x, source.y};
        unit.destination = new float[] {unit.path.get(unit.path.capacity()-1).x, unit.path.get(unit.path.capacity()-1).y};
        source.units.add(unit);
        //source.owner.unitsInFlight.add(unit);
    }
}
