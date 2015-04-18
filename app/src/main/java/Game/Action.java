package Game;

import java.util.List;
import java.util.Vector;

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
    Vector<Unit> sUnitsLost = new Vector<Unit>();
    Vector<Unit> sUnitsGained = new Vector<Unit>();
    Vector<Unit> dUnitsLost = new Vector<Unit>();
    Vector<Unit> dUnitsGained = new Vector<Unit>();

    public Action(Player p, Category c, Territory s, Territory d){
        player = p;
        category = c;
        source = s;
        destination = d;
    }
}
