package AI;

import Game.Action;
import Game.Territory;
import Game.Player;

/**
 * Created by jeff on 4/4/15.
 */
public class WeightedAction extends Action {
    int weight;

    public WeightedAction(Player player, Category category, Territory s,
                            Territory d, int weight){
        super(player, category, s, d);
        this.weight = weight;
    }
}