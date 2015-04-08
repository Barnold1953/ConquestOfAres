package AI;

import Game.GameState;
import Game.Territory;

import java.util.*;

/**
 * Created by jeff on 1/16/15.
 */
public class AI {
    GameState s = null;
    //sorted list of priority actions
    List<WeightedAction> reactions;

    public AI(GameState s){
        this.s = s;
    }

    public ArrayList<WeightedAction> generateAssignments(){
        Vector<Territory> neighbors = s.selectedTerritory.neighbors;
        ArrayList<WeightedAction> possibleActions = new ArrayList<WeightedAction>();

        return possibleActions;
    }

    protected void generateAttackAssignments(Vector<Territory> neighbors){
        for(Territory n : neighbors){
            if(n.owner != s.players.get(s.currentPlayerIndex)){

            }
        }
    }

    protected void generateMovementAssignments(Vector<Territory> t){

    }

    protected void generatePlacementAssignments(Vector<Territory> t) {

    }

    public void assignWeights(){
        ArrayList<WeightedAction> reactions = generateAssignments();
    }

    public void sortReactions(){
        Collections.sort(reactions, new GameActionComparator());
    }

    public void reset(){
        this.s = null;
        reactions.clear();
        reactions = null;
    }
}