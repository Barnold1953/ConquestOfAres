package AI;

import Game.Action;
import Game.GameState;
import Game.Territory;
import Game.Player;
import Game.Unit;

import java.util.*;

/**
 * Created by jeff on 1/16/15.
 */
public class AI {
    GameState s = null;
    protected List<WeightedAction> weightedReactions = new ArrayList<WeightedAction>();
    //list of priority actions
    protected List<Action> attackReactions = new ArrayList<Action>();
    protected List<Action> movementReactions = new ArrayList<Action>();
    protected List<Action> placementReactions = new ArrayList<Action>();

    protected List<Territory> weakSelfTerritories = new ArrayList<Territory>();
    protected List<Territory> weakEnemyTerritories = new ArrayList<Territory>();

    Player AIPlayer = s.players.get(s.currentPlayerIndex);
    List<Territory> AITerritories = AIPlayer.territories;

    public AI(GameState s){
        this.s = s;
    }

    protected void generateAttackAssignments(){
        for(Territory t : AITerritories){
            evaluateWeakness(t);
            if(isWeakEnemyTerritory(t))
                attackReactions.add(new Action(AIPlayer, Action.Category.ATTACK, t, n));
        }
    }

    protected void generateMovementAssignments(){
        for(Territory t : AITerritories){
            for(Territory n : t.neighbors){
                movementReactions.add(new Action(AIPlayer, Action.Category.MOVE_UNIT, t, n));
            }
        }
    }

    protected void generatePlacementAssignments() {
        for(Territory t : AITerritories){
            if(isWeakOwnedTerritory(t)){
                placementReactions.add(new Action(AIPlayer, Action.Category.ADD_UNIT, null, t));
            }
        }
    }

    public void assignWeights(){
        //assign weights to movementReactions
        for(Action a : attackReactions) {
            for (Action m : movementReactions) {
                for (Action p : placementReactions) {
                    int anticipatedGameStateWeight = rateGameState(generateAnticipatedGameState(a, m, p));
                    weightedReactions.add(new WeightedAction(a, m, p, anticipatedGameStateWeight));
                }
            }
        }
    }

    protected void evaluateWeakness(Territory t){
        int selfUnits = t.units.size();
        int enemyUnits = 0;

        for(Territory n : t.neighbors){
            if(n.owner != s.players.get(s.currentPlayerIndex))
                enemyUnits += n.units.size();
        }

        double weaknessRatio = enemyUnits / selfUnits;
        t.weakness = weaknessRatio;
    }

    protected Boolean isWeakEnemyTerritory(Territory t) {
        return (t.weakness < 1.0);
    }

    protected Boolean isWeakOwnedTerritory(Territory t){
        return (t.weakness > 1.0);
    }

    public void reset(){
        this.s = null;
        attackReactions.clear();
        movementReactions.clear();
        movementReactions = null;
        attackReactions = null;
    }

    protected GameState generateAnticipatedGameState(Action a, Action m, Action p){
        GameState anticipatedState = s;

        //modify state with possible movement actions

        //modify state with possible attack actions

        //modify state with possible placement action
        

        return anticipatedState;
    }

    protected int rateGameState(GameState s){
       int gameStateRating = 0;

       return gameStateRating;
    }

    public void distributePlayerResources(Player p){
        //distribute player resources after one quits
    }
}