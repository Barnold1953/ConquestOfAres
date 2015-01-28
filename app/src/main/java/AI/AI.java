package AI;
/**
 * Created by jeff on 1/16/15.
 */
public class AI {

    AI(){
        //initialize AI class
    }

    public void ResetAIClass(){
        //reset attributes of AI class
        //so that new priorities and attributes
        //can be assigned
    }
    public class GameInterpreter {
        //code to read game state
    }

    public class PrioritySystem{
        //sort priorities based on game state
        PrioritySystem(/*GameInterpreter g*/) {
        }

        public void GatherTasks(){
            //gather basic tasks to be performed
        }

        public void GenerateAssignments(){
            //generate list of all possible
            //assignments that could be made
            //in the context of the situation
        }

        public void SortAssignments(){
            //sort possible actions based on
            //priority system
        }
    }

    public class Reactor {
        //react to current game state based on
        //priority system
        //change NPCs accordingly
        public void AssignTask(){
            //assign task based on which task gets priority
        }
    }
}
