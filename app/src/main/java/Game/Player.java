package Game;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */

@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Player {

    public ArrayList<Territory> territories = new ArrayList<Territory>(); ///< List of all armies.
    public Vector<Unit> units = new Vector<Unit>();
    public Vector<Unit> unitsInFlight = new Vector<>();
    public int placeableUnits;
    public boolean isAI = true; ///< True when controlled by AI
    public byte[] color = new byte[3]; ///< Players army color
    // TODO: Statistics?
    // TODO: Networking stuff

    public Player(int initialUnits) {
        placeableUnits = initialUnits;
        isAI = false;
    }

    public Player(Player b) {
        units = new Vector<Unit>(b.units);
        placeableUnits = b.placeableUnits;
        isAI = b.isAI;
        color[0] = b.color[0];
        color[1] = b.color[1];
        color[2] = b.color[2];
    }

    public void addTerritory(Territory t) {
        t.owner = this;
        territories.add(t);
    }

    public void removeTerritory(Territory t) {
        territories.remove(t);
        t.owner = null;
    }
}
