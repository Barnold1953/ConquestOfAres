package AI;

import Game.Action;
import Game.Territory;
import Game.Player;

/**
 * Created by jeff on 4/4/15.
 */
public class WeightedAction {
    int weight;
    Action movementAction;
    Action attackAction;
    Action placementAction;

    public WeightedAction(Action a, Action m, Action p, int w){
        this.movementAction = m;
        this.attackAction = a;
        this.placementAction = p;
        this.weight = w;
    }
}