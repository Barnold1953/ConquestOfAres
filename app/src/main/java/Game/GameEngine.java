package Game;

import android.util.Log;

import Generation.MapData;
import Generation.MapGenerator;
import Utils.PreciseTimer;

/**
 * Created by brb55_000 on 3/6/2015.
 */

/// Handles initialization logic
public class GameEngine {
    public MapGenerator mapGenerator = new MapGenerator(); ///< Generates the map
    private GameState m_gameState = null; ///< Handle to game
    private GameSettings m_gameSettings = null; ///< Settings
    private GameController m_gameController = null;

    private byte[][] playerColors = new byte[6][3];

    /// Initializes a game by setting up game state and map
    public void initGame(GameState gameState, GameSettings gameSettings, GameController gameController) {
        initPlayerColors();

        // Set handles so we don't have to pass shit around everywhere
        m_gameState = gameState;
        m_gameSettings = gameSettings;
        m_gameController = gameController;
        // Generate the map
        PreciseTimer timer = new PreciseTimer();
        MapData mapData = mapGenerator.generateMap(gameSettings.getMapGenParams());

        Log.d("*TIME generateMap:", Double.toString(timer.stop()));

        m_gameState.territories = mapData.territories;
        m_gameState.mapData = mapData;
        // Initialize players
        initPlayers(m_gameSettings.getNumPlayers(), m_gameSettings.getNumAI());
        // Assign territories
        assignTerritories();

        Log.d("Init: ", "initGame finished.");
    }

    private void initPlayerColors() {
        playerColors[0][0] = (byte)200;
        playerColors[0][1] = (byte)0;
        playerColors[0][2] = (byte)0;
        playerColors[1][0] = (byte)0;
        playerColors[1][1] = (byte)200;
        playerColors[1][2] = (byte)0;
        playerColors[2][0] = (byte)0;
        playerColors[2][1] = (byte)0;
        playerColors[2][2] = (byte)255;
        playerColors[3][0] = (byte)200;
        playerColors[3][1] = (byte)200;
        playerColors[3][2] = (byte)0;
        playerColors[4][0] = (byte)0;
        playerColors[4][1] = (byte)200;
        playerColors[4][2] = (byte)200;
        playerColors[5][0] = (byte)255;
        playerColors[5][1] = (byte)0;
        playerColors[5][2] = (byte)255;
    }

    private void initPlayers(int numPlayers, int numAI) {
       for (int i = 0; i < numPlayers; i++) {
           Player p = new Player("Player " + (i+1),m_gameSettings);
           if (i < numAI) {
               p.isAI = true;
           }
           p.color[0] = playerColors[i][0];
           p.color[1] = playerColors[i][1];
           p.color[2] = playerColors[i][2];
           p.placeableUnits = 5;
           p.name = "Player " + Integer.toString(i + 1);
           m_gameState.players.add(p);
       }
    }

    /// Assignes territories to players based on the TerritoryDistMode
    private void assignTerritories() {
        m_gameState.currentState = GameState.State.SELECTING_TERRITORIES;
        // Set gamestate
        //gameState.territories = gameSettings.mapGenParams.territories;

        // Assign territories to players
        switch (m_gameSettings.getTerritoryDistMode()) {
            case RANDOM:
                int j = 0;
                for (int i = 0; i < m_gameState.territories.size(); i++) {
                    if(m_gameState.territories.get(i).terrainType != Territory.TerrainType.Ocean) {
                        m_gameState.players.get(j).addTerritory(m_gameState.territories.get(i));
                        j++;
                    }
                    if (j == m_gameState.players.size()) j = 0;
                }
                m_gameState.assignedTerritories = m_gameState.territories.size();
                
                break;
            case ROUND_ROBIN:
                // TODO: This needs MP stuff or AI probably.
                break;
        }
        m_gameState.currentState = GameState.State.PLACING_UNITS;
    }

    /// Sets up all the initial armies
    private void initUnits(int unitsPerTerritory) {
        /* Random r = new Random();
        int totalUnits = unitsPerTerritory * m_gameState.territories.size();
        int unitsPerPlayer = totalUnits / m_gameState.players.size();
        for (Player p : m_gameState.players) {
            int unitsRemaining = unitsPerPlayer;
            for (Territory t : p.territories) {
                for (int i = 0; i < unitsPerTerritory && unitsRemaining != 0; i++) {
                    p.extraUnits++;
                    int direction = r.nextInt();
                    float spread = 30.0f;
                    switch (direction%4){
                        case 0:
                            m_gameController.addUnit(t, t.x + r.nextFloat() * spread, t.y + r.nextFloat() * spread, Unit.Type.soldier);
                            break;
                        case 1:
                            m_gameController.addUnit(t, t.x - r.nextFloat() * spread, t.y + r.nextFloat() * spread, Unit.Type.soldier);
                            break;
                        case 2:
                            m_gameController.addUnit(t, t.x + r.nextFloat() * spread, t.y - r.nextFloat() * spread, Unit.Type.soldier);
                            break;
                        case 3:
                            m_gameController.addUnit(t, t.x - r.nextFloat() * spread, t.y - r.nextFloat() * spread, Unit.Type.soldier);
                            break;
                    }
                    //m_gameController.addUnit(t, t.x + r.nextFloat() * 60.0f, t.y + r.nextFloat() * 60.0f, Unit.Type.soldier);
                    //m_gameController.addUnit(t, t.x, t.y, Unit.Type.soldier);
                }
            }
        }*/
    }
}
