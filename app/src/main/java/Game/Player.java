package Game;
import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Player {
    public String name; ///< Player name for display
    public Vector<Army> armies = new Vector<Army>(); ///< List of all armies. TODO: Is this even needed?
    public boolean isAI = true; ///< True when controlled by AI
    public byte[] color = new byte[3]; ///< Players army color TODO: Use and set this in GameController
    // TODO: Statistics?
    // TODO: Networking stuff
}
