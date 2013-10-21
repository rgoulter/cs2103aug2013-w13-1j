package jim.journal;



import java.util.ArrayList;

import org.joda.time.MutableDateTime;



public class CompleteCommand extends Command {

    String description;
    MutableDateTime EndDate;
    JournalManager JM;
    ArrayList<Task> taskToComplete = new ArrayList<Task>();
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    public CompleteCommand(String des) {
        description = des;
    }
    public CompleteCommand(MutableDateTime ED){
        EndDate = ED;
    }

    @Override
    public String execute(JournalManager journalManager) {
        JM = journalManager;
        SearchTool searchTool = new SearchTool(JM);
        
        
        if (description != null){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        }
          
        if (matchingTasks.size() == 0){
            outputln("Description was not matched.");
            return "Success";
        }else{
            outputln( "Type in just the index of tasks you wish to process. Please seperate them by ',''");
            for (int i = 0; i < matchingTasks.size(); i++){
                Task task = matchingTasks.get(i);
                outputln(i + ", " + task.toString());
            }
            return "Pending";
        }
    }
            
    @Override
    public String secondExecute(String secondInput) {
        String[] IndexesOfTasks = secondInput.split(",");
        for (int i = 0; i < IndexesOfTasks.length; i++){
            try{
               int j = Integer.parseInt(IndexesOfTasks[i]);
               taskToComplete.add(matchingTasks.get(j));
            }catch(NumberFormatException e){
                return "Pending";
            }
        }
        executeHelper();
        return "Success";
    }
    
    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    private void executeHelper(){
        for (Task t : matchingTasks) {
            String feedback = JM.completeTask(t);
            JM.addCommandHistory("complete", t);
            outputln(feedback);
    
        }
    }
    
    public String toString() {
        return "complete";
    }

}
