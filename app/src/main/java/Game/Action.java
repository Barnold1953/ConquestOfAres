package Game;

import java.util.List;

/**
 * Created by Nathan on 3/27/2015.
 */
public class Action {
    public enum Category{
        moveUnit, attack, addUnit
    }
    Player player;
    Category category;
    Territory source, destination;
    List<Unit> sUnitsLost, sUnitsGained, dUnitsLost, dUnitsGained;

    public Action(Player p, Category c, Territory s, Territory d){
        player = p;
        category = c;
        source = s;
        destination = d;
    }
}
