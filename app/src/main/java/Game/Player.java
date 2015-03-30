package Game;
import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Player {

    public Player() {}

    public Player(Player b) {
        name = new String(b.name);
        armies = new Vector<Army>(b.armies);
        extraUnits = b.extraUnits;
        isAI = b.isAI;
        color[0] = b.color[0];
        color[1] = b.color[1];
        color[2] = b.color[2];
    }

    public String name; ///< Player name for display
    public Vector<Army> armies = new Vector<Army>(); ///< List of all armies. TODO: Is this even needed?
    public int extraUnits;
    public boolean isAI = true; ///< True when controlled by AI
    public byte[] color = new byte[3]; ///< Players army color TODO: Use and set this in GameController
    // TODO: Statistics?
    // TODO: Networking stuff
}
