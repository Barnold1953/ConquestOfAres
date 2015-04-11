package Game;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.*;

import Generation.MapData;
import Generation.MapGenerator;
import Generation.MapGenerationParams;
import Graphics.Quadrilateral;
import Graphics.SpriteBatchSystem;
import Utils.Device;
import utkseniordesign.conquestofares.GameActivity;

/**
 * Created by brb55_000 on 2/6/2015.
 */

/// Contains the game logic core
public class GameController {
    private GameState m_gameState = null; ///< Handle to game
    private GameSettings m_gameSettings = null; ///< Settings
    private GameEngine m_gameEngine = new GameEngine(); ///< Initializes the game
    private Player m_currentPlayer = null;
    public Boolean stateHasChanged = false;

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings) {
        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameState.currentState = GameState.State.GAME_START;
        m_gameSettings = gameSettings;
        // Initialize the game
        m_gameEngine.initGame(m_gameState, m_gameSettings, this);
        m_gameState.currentPlayerIndex = 0;
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex);
    }

    public GameState getGameState(){
        return m_gameState;
    }

    public Player getCurrentPlayer() { return m_currentPlayer; }

    /// Call this to transition to the next turn
    public void nextTurn() {
        Debug.logState(m_gameState);
        // Go to next player (starts at -1)
        m_gameState.currentPlayerIndex++;
        Debug.logRound(m_gameState);
        if(m_gameState.currentPlayerIndex / m_gameState.players.size() > 0){
            m_gameState.currentState = GameState.State.PLAYING;
            stateHasChanged = true;
        }
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex % m_gameState.players.size());
        // Check if we should do AI
        if (m_currentPlayer.isAI) {
            // TODO: Do AI stuff
            nextTurn(); // Recursively go to next turn
            return;
        }
        // Current player is human, he is now placing units
        Debug.logState(m_gameState);
    }

    /// Call this method when the world is clicked on
    public Territory onClick(float x, float y) {
        Territory territory = getTerritoryAtPoint(x, y);

        if (m_gameState.selectedTerritory == territory) {
            territory.unselect();
            m_gameState.selectedTerritory = null;
        } else if (m_gameState.selectedTerritory == null) {
            territory.select();
            m_gameState.selectedTerritory = territory;
        } else {
            m_gameState.selectedTerritory.unselect();
            territory.select();
            m_gameState.selectedTerritory = territory;
        }
        return territory;
    }

    /// Returns the territory at a specific point
    public Territory getTerritoryAtPoint(float x, float y) {
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

    public void moveUnits(Territory source, Territory destination){
        Action action = new Action(m_currentPlayer, Action.Category.moveUnit, source, destination);

        for(Unit unit : source.selectedUnits ) {
            action.sUnitsLost.add(unit);
            action.dUnitsGained.add(unit);
            m_gameState.actions.add(action);
            source.units.remove(unit);

            unit.path = new PathFinding().getPath(source, destination);
            unit.frame = 0;
            unit.location = new PointF(source.x, source.y);
            unit.destination = unit.path.get(unit.path.size()-1).getUnitPlace();
            destination.units.add(unit);
        }

        //addUnit(destination, destination.x, destination.y, unit.type);

        //source.owner.unitsInFlight.add(unit);
    }
}
