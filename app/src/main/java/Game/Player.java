package Game;
import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Player {
    public String name; ///< Player name for display
    public ArrayList<Territory> territories = new ArrayList<Territory>(); ///< List of all armies.
    public boolean isAI = true; ///< True when controlled by AI
    public byte[] color = new byte[3]; ///< Players army color TODO: Use and set this in GameController
    // TODO: Statistics?
    // TODO: Networking stuff
}
