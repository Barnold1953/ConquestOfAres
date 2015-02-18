package Generation;

/**
 * Created by brb55_000 on 2/18/2015.
 */

import java.util.*;

public class Vertex extends Coord {

    public List<VoronoiSegment> edges = null;
    public int number;

    public Vertex() {
        edges = new LinkedList<VoronoiSegment>();
    }
}
