package Game;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.*;

import Generation.MapData;
import Generation.MapGenerator;
import Generation.MapGenerationParams;
import Graphics.CoARenderer;
import Sound.SoundManager;
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
    private boolean m_isAttacking = false;
    private Territory m_defender;
    private Territory m_attacker;
    public Boolean stateHasChanged = false;
    private GameActivity m_gameActivity = null;

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings, GameActivity gameActivity) {
        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameState.currentState = GameState.State.GAME_START;
        m_gameSettings = gameSettings;
        // Initialize the game
        m_gameEngine.initGame(m_gameState, m_gameSettings, this);
        m_gameState.currentPlayerIndex = 0;
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex);
        m_gameActivity = gameActivity;
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
            m_gameState.currentState = GameState.State.PLACING_UNITS;
        }
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex % m_gameState.players.size());
        if(m_gameState.currentState == GameState.State.PLACING_UNITS) {
            //m_currentPlayer.placeableUnits = 0;
            m_currentPlayer.placeableUnits = m_currentPlayer.territories.size();
        }
        // Check if we should do AI
        if (m_currentPlayer.isAI) {
            // TODO: Do AI stuff
            nextTurn(); // Recursively go to next turn
            return;
        }
        // Current player is human, he is now placing units
        Debug.logState(m_gameState);
    }

    public void stepState(){
        stateHasChanged = true;
        switch(m_gameState.currentState){
            case PLACING_UNITS:
                m_gameState.currentState = GameState.State.ATTACKING;
                break;
            case ATTACKING:
                m_gameState.currentState = GameState.State.FORTIFYING;
                break;
            case INITIAL_UNIT_PLACEMENT:
                nextTurn();
                break;
            default:
                m_gameState.currentState = GameState.State.PLACING_UNITS;
                nextTurn();
                break;
        }
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
        return MapGenerator.getClosestTerritory(x, y, m_gameState.mapData);
    }

    public boolean attack(Territory attacker, Territory defender, CoARenderer renderer){
        if (attacker.selectedUnits.size() == attacker.units.size()) return false;

        Action action = new Action(m_currentPlayer, Action.Category.attack, attacker, defender);

        // Move attackers into range and start combat animations
        synchronized (attacker.units) {
            for (Unit u : attacker.selectedUnits) {
                u.destination.x = u.location.x + (defender.x - attacker.x) * 0.5f;
                u.destination.y = u.location.y + (defender.y - attacker.y) * 0.5f;
            }
        }
        synchronized (defender.units) {
            for (Unit u : defender.units) {
                u.isDefending = true;
                u.destination.x = u.location.x + (attacker.x - defender.x) * 0.5f;
                u.destination.y = u.location.y + (attacker.y - defender.y) * 0.5f;
            }
        }
        SystemClock.sleep(500);
        // Start combat animations
        for (Unit u : attacker.selectedUnits) u.inCombat = true;
        for (Unit u : defender.units) u.inCombat = true;

        while(defender.units.size() > 0 && attacker.selectedUnits.size() > 0) {
            final float accuracy = 5.0f;
            // Random firing animation
            for (int i = 0; i < attacker.units.size() / 2 + 1; i++) {
                // Attacker fire
                int randomSrc = m_gameState.random.nextInt(attacker.selectedUnits.size());
                int randomDst = m_gameState.random.nextInt(defender.units.size());
                Unit src1 = attacker.selectedUnits.get(randomSrc);
                Unit dst1 = defender.units.get(randomDst);
                renderer.addLaser(src1.location.x, src1.location.y,
                        dst1.location.x + (m_gameState.random.nextFloat() * 2.0f - 1.0f) * accuracy,
                        dst1.location.y + (m_gameState.random.nextFloat() * 2.0f - 1.0f) * accuracy,
                        attacker.owner.fColor[0], attacker.owner.fColor[1], attacker.owner.fColor[2]);
                SoundManager.getLaser();
                SystemClock.sleep(25);

                // Defender fire
                randomSrc = m_gameState.random.nextInt(defender.units.size());
                randomDst = m_gameState.random.nextInt(attacker.selectedUnits.size());
                Unit src2 = defender.units.get(randomSrc);
                Unit dst2 = attacker.selectedUnits.get(randomDst);
                renderer.addLaser(src2.location.x, src2.location.y,
                        dst2.location.x + (m_gameState.random.nextFloat() * 2.0f - 1.0f) * accuracy,
                        dst2.location.y + (m_gameState.random.nextFloat() * 2.0f - 1.0f) * accuracy,
                        defender.owner.fColor[0], defender.owner.fColor[1], defender.owner.fColor[2]);
                SoundManager.getLaser();
                SystemClock.sleep(25);
            }

            if(m_gameState.random.nextInt(2) == 0){
                Unit u = attacker.selectedUnits.get(attacker.selectedUnits.size()-1);
                action.sUnitsLost.add(u);
                SoundManager.getScream();
                attacker.selectedUnits.remove(u);
                synchronized (attacker.units) { attacker.units.remove(u); }
            }
            else{
                Unit u = defender.units.get(defender.units.size()-1);
                action.dUnitsLost.add(u);
                SoundManager.getScream();
                synchronized (defender.units) { defender.units.remove(u); }
            }
        }

        // End attack animations
        for (Unit u : defender.units) {
            u.destination.x = u.location.x;
            u.destination.y = u.location.y;
            u.isDefending = false;
            u.inCombat = false;
        }
        for (Unit u : attacker.selectedUnits) u.inCombat = false;

        if(defender.units.isEmpty() && !attacker.selectedUnits.isEmpty()){
            Player p = defender.owner;
            defender.owner.removeTerritory(defender);
            attacker.owner.addTerritory(defender);
            //SoundManager.march();
            moveUnits(attacker, defender);
            if(p.territories.isEmpty()){
                m_gameState.players.remove(p);
                if(m_gameState.players.size() == 1){
                    Log.d("WINNER", "Player " + m_gameState.players.get(0).name + " has won!");
                }
            }
        }

        return !attacker.units.isEmpty();
    }

    public void moveUnits(Territory source, Territory destination){
        if (source.selectedUnits.size() == source.units.size()) return;

        Action action = new Action(m_currentPlayer, Action.Category.moveUnit, source, destination);

        synchronized (destination.units) {
            synchronized (source.units) {
                for (Unit unit : source.selectedUnits) {
                    action.sUnitsLost.add(unit);
                    action.dUnitsGained.add(unit);

                    unit.destination = destination.getUnitPlace();

                    source.units.remove(unit);
                    destination.units.add(unit);
                }
            }
        }
        source.selectedUnits.clear();

        m_gameState.actions.add(action);
    }
}
