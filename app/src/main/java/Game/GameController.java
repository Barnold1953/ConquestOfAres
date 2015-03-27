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
    void onClick(float x, float y) {
        Territory territory = getTerritoryAtPoint(x, y);
        if(territory == m_gameState.selectedTerritory){
            m_gameState.selectedTerritory = null;
            return;
        }
        if(m_gameState.selectedTerritory == null){
            m_gameState.selectedTerritory = territory;
            return;
        }
        switch (m_gameState.currentState) {
            case PLACING_UNITS:
                addUnit(territory, x, y, Unit.Type.soldier);
                break;
            case PLAYING:
                if(m_gameState.selectedTerritory.owner != territory.owner){
                    attack(m_gameState.selectedTerritory, territory);
                }
                else{
                    moveUnit(m_gameState.selectedTerritory, territory);
                }
                break;
        }
    }

    /// Returns the territory at a specific point
    Territory getTerritoryAtPoint(float x, float y) {
        return MapGenerator.getClosestTerritory(x, y, m_gameState.territories);
    }

    boolean addUnit(Territory territory, float x, float y, Unit.Type type){
        if(territory.owner.extraUnits > 0){
            Unit unit = new Unit(x,y, Unit.Type.soldier);

            SpriteBatchSystem.addUnit(unit.type, x, y);

            territory.army.units.add(unit);
            territory.owner.extraUnits--;
            return true;
        }
        return false;
    }

    boolean attack(Territory attacker, Territory defender){
        while(attacker.army.units.size() > 0 && defender.army.units.size() > 0){
            // I figure we can change the chance of winning based on the type of unit it is, like tanks are weak to airplanes, airplanes are weak to soldiers, and soldiers are weak to tanks
            // kind of like a rock-paper-scissors dynamic

            if(m_gameState.random.nextInt() % 2 == 0){
                attacker.army.units.remove(attacker.army.units.size()-1);
            }
            else{
                defender.army.units.remove(defender.army.units.size()-1);
            }
        }
        return !attacker.army.units.isEmpty();
    }

    void moveUnit(Territory source, Territory destination){
        Unit unit = source.army.units.get(source.army.units.size()-1);
        destination.owner.extraUnits++;
        addUnit(destination, destination.x, destination.y, unit.type);
        source.army.units.remove(source.army.units.size()-1);
    }
}
