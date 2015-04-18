package Game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.*;

import Generation.MapData;
import Generation.MapGenerator;
import Generation.MapGenerationParams;
import Graphics.Quadrilateral;
import Graphics.SpriteBatchSystem;
import Utils.Device;
import utkseniordesign.conquestofares.GameActivity;
import utkseniordesign.conquestofares.googleClientApiActivity;

/**
 * Created by brb55_000 on 2/6/2015.
 */

/// Contains the game logic core
public class GameController {
    private GameState m_gameState = null; ///< Handle to game
    private GameEngine m_gameEngine = new GameEngine(); ///< Initializes the game
    private Player m_currentPlayer = null;
    private TurnBasedMatch match;
    private Boolean hasCurrentState = false;
    private googleClientApiActivity gameActivity = null;

    public GameController(googleClientApiActivity parentActivity, TurnBasedMatch turnBasedMatch,GameSettings gameSettings) {
        gameActivity = parentActivity;
        match = turnBasedMatch;
        if(match.getData() == null) {
            // if this is the creator of the game, create it
            initGame(new GameState(),gameSettings);
            hasCurrentState = true;
            Log.d("Multiplayer info","Created!");
        }
        else {
            // otherwise, get the game state already created
            readState();
            Log.d("Multiplayer info","Joined");
            Log.d("Multiplayer info",match.getCreatorId());
        }
    }

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings) {
        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameState.gameSettings = gameSettings;

        // Initialize the game
        m_gameEngine.initGame(m_gameState);
        m_gameState.currentPlayerIndex = 0;
        m_currentPlayer = m_gameState.players.get(m_gameState.currentPlayerIndex);
    }

    public void readState() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = new String(match.getData(),"UTF-8");
            m_gameState = objectMapper.readValue(json, GameState.class);
        } catch(Exception e) {
            e.getMessage();
        }

        // get map data
        FileInputStream fs;
        try {
            fs = gameActivity.openFileInput("MapData" + match.getMatchId());
            m_gameState.mapData = objectMapper.readValue(fs, MapData.class);
        } catch (FileNotFoundException fnfound) {
            m_gameState.mapData = (new MapGenerator()).generateMap(m_gameState.gameSettings.getMapGenParams());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void writeState(Boolean newPlayer) {
        ObjectMapper objectMapper = new ObjectMapper();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytesToWrite = null;

        // write map to system file
        FileOutputStream fs;
        try {
            String json = objectMapper.writeValueAsString(m_gameState.mapData);
            fs = gameActivity.openFileOutput("MapData" + match.getMatchId(), Context.MODE_PRIVATE);
            fs.write(json.getBytes());
        } catch(Exception e) {
            e.getMessage();
        }

        // write game state to cloud
        String json = "";
        try {
            json = objectMapper.writeValueAsString(m_gameState);
        } catch(Exception e) {
            Log.d("State Writing",e.getMessage());
        }

        Log.d("State Writing",json);

        // if this is actually the end of a turn
        if(newPlayer) {
            // get new player
            m_gameState.currentPlayerIndex++;
            if (m_gameState.currentPlayerIndex == m_gameState.players.size()) {
                if (!m_gameState.isSetup) m_gameState.isSetup = true;
                m_gameState.currentPlayerIndex = 0;
            }
        } // otherwise, it's just a player pausing, but write the state either way

        m_gameState.currentState = GameState.State.NOT_MY_TURN;

        hasCurrentState = false;
        String participantID = null;
        if(newPlayer) {
            // if the match isn't full, look for the next player
            if(match.getParticipantIds().size() > m_gameState.currentPlayerIndex) {
                participantID = match.getParticipantIds().get(m_gameState.currentPlayerIndex);
            }
        }
        else participantID = match.getParticipantId(Games.Players.getCurrentPlayer(gameActivity.getClient()).getPlayerId());

        Games.TurnBasedMultiplayer.takeTurn(
                gameActivity.getClient(),
                match.getMatchId(),
                json.getBytes(),
                participantID
        );
        Log.d("Google Play Services","State Written by Player" + participantID + "!");
    }

    public Boolean hasState() {
        return hasCurrentState;
    }

    public TurnBasedMatch getMatch() { return match; }

    public GameState getGameState(){
        return m_gameState;
    }

    public Player getCurrentPlayer() { return m_currentPlayer; }

    /// Call this to transition to the next turn
    public void nextTurn() {
        writeState(true);
    }

    public void stepState(){
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
        return MapGenerator.getClosestTerritory(x, y, m_gameState.territories);
    }

    public boolean attack(Territory attacker, Territory defender, int numAttackers){
        Action action = new Action(m_currentPlayer, Action.Category.attack, attacker, defender);

        while(defender.units.size() > 0 && numAttackers > 0){
            // I figure we can change the chance of winning based on the type of unit it is, like tanks are weak to airplanes, airplanes are weak to soldiers, and soldiers are weak to tanks
            // kind of like a rock-paper-scissors dynamic

            if(m_gameState.random.nextInt() % 2 == 0){
                action.sUnitsLost.add(attacker.units.get(attacker.units.size()-1));
                numAttackers--;
                attacker.selectedUnits.remove(attacker.selectedUnits.size()-1);
                attacker.units.remove(attacker.units.size()-1);
            }
            else{
                action.dUnitsLost.add(defender.units.get(defender.units.size()-1));
                defender.units.remove(defender.units.size()-1);
            }
        }
        if(defender.units.isEmpty() && numAttackers > 0){
            Player p = defender.owner;
            defender.owner.removeTerritory(defender);
            attacker.owner.addTerritory(defender);
            moveUnits(attacker, defender);
            if(p.territories.isEmpty()){
                m_gameState.players.remove(p);
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

            if(source.neighbors.contains(destination)){
                unit.destination = new PointF(destination.x,destination.y);
            }
            else {
                unit.path = new PathFinding().getPath(source, destination);
                unit.destination = new PointF(unit.path.get(unit.path.size()-1).x,unit.path.get(unit.path.size()-1).y);
            }
            unit.frame = 0;
            unit.location = new PointF(source.x, source.y);

            destination.units.add(unit);
        }

        source.selectedUnits.clear();

        //addUnit(destination, destination.x, destination.y, unit.type);

        //source.owner.unitsInFlight.add(unit);
    }
}
