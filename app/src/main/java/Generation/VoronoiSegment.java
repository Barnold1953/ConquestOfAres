package Generation;

/**
 * Created by brb55_000 on 2/18/2015.
 */
public class VoronoiSegment extends Segment {

    VoronoiSegment(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
        floating = false;
    }

    public final Vertex v1, v2;
    public boolean floating;

}
