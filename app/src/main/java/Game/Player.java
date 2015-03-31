package Game;
import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Player {

    public Player() {}

    public Player(Player b) {
        name = new String(b.name);
        units = new Vector<Unit>(b.units);
        extraUnits = b.extraUnits;
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

    public String name; ///< Player name for display
    public ArrayList<Territory> territories = new ArrayList<Territory>(); ///< List of all armies.
    public Vector<Unit> units = new Vector<Unit>();
    public Vector<Unit> unitsInFlight = new Vector<>();
    public int extraUnits;
    public boolean isAI = true; ///< True when controlled by AI
    public byte[] color = new byte[3]; ///< Players army color TODO: Use and set this in GameController
    // TODO: Statistics?
    // TODO: Networking stuff
}
