package AI;
/**
 * Created by jeff on 1/16/15.
 */
public class AI {

    AI(){
        //initialize AI class
    }

    public void resetAIClass(){
        //reset attributes of AI class
        //so that new priorities and attributes
        //can be assigned
    }
    class GameInterpreter {
        //code to read game state
    }

    public class PrioritySystem{
        //sort priorities based on game state
        PrioritySystem(/*GameInterpreter g*/) {
        }

        public void gatherTasks(){
            //gather basic tasks to be performed
        }

        public void generateAssignments(){
            //generate list of all possible
            //assignments that could be made
            //in the context of the situation
        }

        public void sortAssignments(){
            //sort possible actions based on
            //priority system
        }
    }

    class Reactor {
        //react to current game state based on
        //priority system
        //change NPCs accordingly
        public void assignTask(){
            //assign task based on which task gets priority
        }
    }
}
