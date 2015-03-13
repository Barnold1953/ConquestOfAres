package Game;

import android.util.Log;

import java.util.Random;

import Generation.MapData;
import Generation.MapGenerator;

/**
 * Created by brb55_000 on 3/6/2015.
 */

/// Handles initialization logic
public class GameEngine {
    public MapGenerator mapGenerator = new MapGenerator(); ///< Generates the map
    private GameState m_gameState = null; ///< Handle to game
    private GameSettings m_gameSettings = null; ///< Settings TODO(Aaron): pass in good settings in initGame

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings) {
        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameSettings = gameSettings;
        // Generate the map
        MapData mapData = mapGenerator.generateMap(gameSettings.getMapGenParams());
        m_gameState.territories = mapData.territories;
        m_gameState.mapData = mapData;
        // Initialize players
        initPlayers(m_gameSettings.getNumPlayers(), m_gameSettings.getNumAI());
        // Assign territories
        assignTerritories();
        // Place units
        initUnits();
        Log.d("Init: ", "initGame finished.");
    }

    private int generateByteColor(int num)
    {
        int color = num*10; //unique color to each player
        return color;
    }

    private void initPlayers(int numPlayers, int numAI) {
        for (int i=1; i <= numPlayers; i++) {
            Player p = new Player();
            m_gameState.players.add(new Player());
            p.color[0] = (byte)(generateByteColor(i)); //R
            p.color[1] = (byte) (generateByteColor(i*2)); // G
            p.color[2] = (byte) (generateByteColor(i*3)); //B
       }
    }

    /// Assignes territories to players based on the TerritoryDistMode
    private void assignTerritories() {
        // Set gamestate
        //gameState.territories = gameSettings.mapGenParams.territories;

        // Assign territories to players
        switch (m_gameSettings.getTerritoryDistMode()) {
            case RANDOM:
                // TODO: Implement. Randomly loop through each territories and assign to players
                break;
            case ROUND_ROBIN:
                // TODO: This needs MP stuff or AI probably.
                break;
        }
    }

    /// Sets up all the initial armies
    private void initUnits() {
        for (int i=0; i < m_gameState.territories.size(); i++) {
            for (int j=0; j <3; j++) {
                m_gameState.territories.get(i).army.units.add(new Unit());
                //double for loop necessary?
            }
        }
        //3 or 4 units per unit per territory
        // TODO: Implement
    }
}
