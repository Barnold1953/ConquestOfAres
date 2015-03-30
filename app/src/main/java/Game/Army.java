package Game;

import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Army {

    public Army() {}

    public Army(Army b) {
        units = new ArrayList<Unit>(b.units);
    }

    public ArrayList<Unit> units = new ArrayList<Unit>();
    // TODO: Bonuses?
}
