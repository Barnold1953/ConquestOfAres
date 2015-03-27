package Game;

import java.util.List;

/**
 * Created by Nathan on 3/27/2015.
 */
public class Action {
    public enum type{
        moveUnit, attack, addUnit
    }
    Player player;

    Territory source, destination;
    List<Unit> sUnitsLost, sUnitsGained, dUnitsLost, dUnitsGained;
}
