
package jim.journal;

import java.util.ArrayList;

import org.joda.time.MutableDateTime;



public class CompleteCommand extends Command {

    String description;
    MutableDateTime EndDate;
    ArrayList<Task> taskToComplete = new ArrayList<Task>();
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
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        
        if (description != null){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        }
        if (matchingTasks.size() == 0){
            outputln("Description was not matched.");
            this.changeCommandState("Failure");
        }else{
            outputln("Give the index of the task you wish to remove.");
            for (int i = 0; i < matchingTasks.size(); i++){
                Task task = matchingTasks.get(i);
                outputln(i + ", " + task.toString());
            }
            this.changeCommandState("Pending");
            String IndexOfTasks = inputLine();
            String[] Indexes = IndexOfTasks.split(",");
            
            for (int i = 0; i < Indexes.length; i++){
                int j = Integer.parseInt(Indexes[i]);
                assert((j <= matchingTasks.size()) && (j >= 0) );
                taskToComplete.add(matchingTasks.get(j));
            }
            this.changeCommandState("Success");
        }
        
            for (Task t : matchingTasks) {
            
                String feedback = journalManager.completeTask(t);
                journalManager.addCommandHistory("complete", t);
                outputln(feedback);
        
            }
         
        
    }

}
