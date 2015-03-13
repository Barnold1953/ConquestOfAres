package Game;
import android.os.Parcel;
import android.os.Parcelable;

import Generation.MapGenerationParams;
/**
 * Created by brb55_000 on 2/6/2015.
 */

enum TerritoryDistributionMode {
    RANDOM,
    ROUND_ROBIN
}

public class GameSettings implements Parcelable {
    public enum GameSettingErrors {
        NUM_PLAYERS_UNSET,
        MAP_WRAP_UNSET,
        MAP_SYMMETRY_UNSET,
        MAP_SIZE_UNSET,
        TURN_LENGTH_UNSET,
        VICTORY_CONDITION_UNSET,
        NO_ERROR
    }

    boolean m_isMultiplayer = false; ///< True when multiplayer mode is active
    boolean m_isHorizontalWrap = false; ///< True when the map wraps horizontally
    int m_numPlayers = 0; ///< Number of players in the game
    int m_numAI = 1; ///< Number of AI players in the game
    double m_territoriesForVictory = -1; ///< Percent of territories needed for victory. -1 means all
    int m_maxTurnLength = -1; ///< Max turn length in minutes. -1 means no limit
    TerritoryDistributionMode m_territoryDistMode = TerritoryDistributionMode.RANDOM; ///< How to distribute territory
    MapGenerationParams m_mapGenParams = new MapGenerationParams(); ///< Map generation specific data
    // TODO: Other settings

    public GameSettings() {
        // stub constructor, because there is a private constructor
    }


    /* ----------------- GETTER METHODS --------------------*/

    public int getNumPlayers() {
        return m_numPlayers;
    }

    public int getNumAI() {
        return m_numAI;
    }

    public double getTerritoriesForVictory() {
        return m_territoriesForVictory;
    }

    public boolean isHorizontalWrap() {
        return m_isMultiplayer;
    }

    public int getTurnLength() {
        return m_maxTurnLength;
    }

    public TerritoryDistributionMode getTerritoryDistMode() {
        return m_territoryDistMode;
    }

    public MapGenerationParams getMapGenParams() {
        return m_mapGenParams;
    }

    /*
        Below is the functions for parsing and creating Game Settings as a parcelable
        object that can be sent from one object to another. These can be largely ignored, unless you need to add
        more data to the class. To do so, just add the variable to the writeToParcel function, as well
        as the private constructor following the fashion of the variables already present.
     */

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) (m_isMultiplayer ? 1 : 0));
        out.writeByte((byte) (m_isHorizontalWrap ? 1 : 0));
        out.writeInt(m_numPlayers);
        out.writeDouble(m_territoriesForVictory);
        out.writeInt(m_maxTurnLength);
        out.writeString(m_territoryDistMode.toString());
        out.writeInt(m_mapGenParams.seed);
        out.writeString(m_mapGenParams.mapSize.toString());
        out.writeString(m_mapGenParams.mapSymmetry.toString());
    }

    public static final Creator<GameSettings> CREATOR = new Creator<GameSettings>() {
        @Override
        public GameSettings createFromParcel(Parcel source) {
            return new GameSettings(source);
        }

        @Override
        public GameSettings[] newArray(int size) {
            return new GameSettings[0];
        }
    };

    private GameSettings(Parcel source) {
        if(m_mapGenParams ==  null) {
            m_mapGenParams = new MapGenerationParams();
        }

        m_isMultiplayer = source.readByte() != 0;
        m_isHorizontalWrap = source.readByte() != 0;
        m_numPlayers = source.readInt();
        m_territoriesForVictory = source.readDouble();
        m_maxTurnLength = source.readInt();
        m_territoryDistMode = TerritoryDistributionMode.valueOf(source.readString());
        m_mapGenParams.seed = source.readInt();
        m_mapGenParams.mapSize = MapGenerationParams.MapSize.valueOf(source.readString());
        m_mapGenParams.mapSymmetry = MapGenerationParams.MapSymmetry.valueOf(source.readString());
    }

    public GameSettingErrors parseSettings (
            String numPlayers,
            String horizontalWrap,
            String territoriesForVictory,
            String turnLength,
            String mapSize,
            String mapSymmetry) {

        // parse numPlayers, unless it hasn't been chosen, in which case return 1
        if(numPlayers != null) {
            m_numPlayers = Integer.parseInt(numPlayers);
        }
        else return GameSettingErrors.NUM_PLAYERS_UNSET;

        // parse horizontalWrap unless it hasn't been chosen, in which case return 2
        if(!horizontalWrap.equals("Map Wrap Settings")) {
            m_isHorizontalWrap = horizontalWrap.equals("Horizontal Wrap");
        }
        else return GameSettingErrors.MAP_WRAP_UNSET;

        if(!territoriesForVictory.equals("Victory Condition")) {
            m_territoriesForVictory = Integer.parseInt(territoriesForVictory.substring(0,2))/100.0;
        }
        else return GameSettingErrors.VICTORY_CONDITION_UNSET;

        if(m_mapGenParams == null) {
            m_mapGenParams = new MapGenerationParams();
        }

        if(!mapSize.equals("Map Size")) {
            m_mapGenParams.mapSize = MapGenerationParams.MapSize.valueOf(mapSize.toUpperCase());
        }
        else return GameSettingErrors.MAP_SIZE_UNSET;

        if(!mapSize.equals("Map Symmetry Settings")) {
            m_mapGenParams.mapSymmetry = MapGenerationParams.MapSymmetry.valueOf(mapSymmetry.toUpperCase());
        }
        else return GameSettingErrors.MAP_SYMMETRY_UNSET;

        switch(turnLength) {
            case "Half Hour":
                m_maxTurnLength = 30;
                break;
            case "One Hour":
                m_maxTurnLength = 60;
                break;
            case "Two Hours":
                m_maxTurnLength = 120;
                break;
            case "Six Hours":
                m_maxTurnLength = 360;
                break;
            case "Twelve Hours":
                m_maxTurnLength = 60*12;
                break;
            case "One Day":
                m_maxTurnLength = 24*60;
                break;
            case "Two Days":
                m_maxTurnLength = 48*60;
                break;
            default:
                return GameSettingErrors.TURN_LENGTH_UNSET;
        }
        return GameSettingErrors.NO_ERROR;
    }
}
