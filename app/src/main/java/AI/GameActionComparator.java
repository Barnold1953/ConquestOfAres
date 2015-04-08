package AI;

import java.util.Comparator;

/**
 * Created by jeff on 4/4/15.
 */
public class GameActionComparator implements Comparator<WeightedAction> {
    @Override
    public int compare(WeightedAction g1, WeightedAction g2) {
        int w1 = g1.weight;
        int w2 = g2.weight;

        return (w1 < w2 ? -1 : (w1 == w2 ? 0 : 1));
    }
}
