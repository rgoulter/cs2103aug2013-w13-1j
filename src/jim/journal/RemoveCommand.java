
package jim.journal;

import java.util.ArrayList;
import java.util.List;



public class RemoveCommand extends Command {

    String description;



    public RemoveCommand(String des) {
        description = des;
    }


    
    @Override
    public void execute(JournalManager journalManager) {
        ArrayList<Task> taskToRemove = new ArrayList<Task>();
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        SearchTool searchTool = new SearchTool(journalManager);
        matchingTasks = searchTool.searchByNonStrictDescription(description);
        
       
        /*
            if (matchingTasks.size() == 0){
                outputln("Description was not matched.");
            }else{
                outputln("Give the number of which task you wish to remove.");
                for (int i = 0; i < matchingTasks.size(); i++){
                    Task task = matchingTasks.get(i);
                    outputln(i + ", " + task.toString());
                }
               
                String IndexOfTasks = inputLine();
                String[] Indexes = IndexOfTasks.split(",");
                
                for (int i = 0; i < Indexes.length; i++){
                    int j = Integer.parseInt(Indexes[i]);
                    assert((j <= matchingTasks.size()) && (j >= 0) );
                    taskToRemove.add(matchingTasks.get(j));
                }
                
                
            }
        */
        taskToRemove = searchTool.searchByDescription(description);
        if (taskToRemove.isEmpty()){
            outputln("Description was not matched.");
        }else{
        
            for (Task t : taskToRemove) {
                if (journalManager.removeTask(t)) {
                journalManager.addCommandHistory("remove", t);
                    outputln("Removed task: " + t.toString());
                } else {
                    outputln("Removing task was not successful.");
                }
            }
        }

    }

}
