
package jim.journal;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.MutableDateTime;



public class CompleteCommand extends Command {

    String description;
    MutableDateTime EndDate;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    public CompleteCommand(String des) {
        description = des;
    }
    public CompleteCommand(MutableDateTime ED){
        EndDate = ED;
    }

    @Override
    public void execute(JournalManager journalManager) {
        // TODO Auto-generated method stub

        SearchTool searchTool = new SearchTool(journalManager);
        
        if (description != null){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        }
        
        if (matchingTasks.isEmpty()) {
            outputln("No tasks was not matched.");
        }
        
        //if (ExecutionState == "CanExecute"){
            for (Task t : matchingTasks) {
            
                String feedback = journalManager.completeTask(t);
                outputln(feedback);
        
            }
          //  ExecutionState = "Success";
        //}
    }

}
