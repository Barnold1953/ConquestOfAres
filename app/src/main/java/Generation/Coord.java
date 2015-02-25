package Generation;

/**
 * Created by brb55_000 on 2/18/2015.
 */

import java.lang.*;

public class Coord {

    public double x, y;

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(final Coord p) {
        if(Math.abs(p.x - x) < 1E-5 && Math.abs(p.y - y) < 1E-5)
            return true;
        else
            return false;
    }
}
