package jim.journal;



import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.MutableDateTime;



public class UncompleteCommand extends Command {
    private static final String FILE_ERROR = "FILE_ERROR";
    
    String description;
    MutableDateTime EndDate;
    JournalManager JM;
    ArrayList<Task> taskToUncomplete = new ArrayList<Task>();
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    public UncompleteCommand(String des) {
        description = des;
    }
    public UncompleteCommand(MutableDateTime ED){
        EndDate = ED;
    }

    @Override
    public String execute(JournalManager journalManager) {
        JM = journalManager;
        SearchTool searchTool;
        try {
            searchTool = new SearchTool(JM);
        } catch (Exception e) {
            outputln(FILE_ERROR);
            return "Failure";
        }
        
        if (description != null){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        } else if (EndDate != null){
            matchingTasks = searchTool.searchByDate(EndDate);
        }
          
        if (matchingTasks.size() == 0){
            outputln("Description was not matched.");
            return "Success";
        } else if (matchingTasks.size() == 1){
        	taskToUncomplete.add(matchingTasks.get(0));
            executeHelper();
            return "Success";
        } else {
            outputln( "Type in just the index of tasks you wish to process. Please seperate them by ','");
            for (int i = 0; i < matchingTasks.size(); i++){
                Task task = matchingTasks.get(i);
                outputln(i + ", " + task.toString());
            }
            return "Pending";
        }
    }
            
    @Override
    public String secondExecute(String secondInput) {
        clearOutput();
        
        String[] IndexesOfTasks = secondInput.split(",");
        for (int i = 0; i < IndexesOfTasks.length; i++){
            try{
               int j = Integer.parseInt(IndexesOfTasks[i]);
               taskToUncomplete.add(matchingTasks.get(j));
            } catch (NumberFormatException e){
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
        for (Task t : taskToUncomplete) {
            String feedback;
            try {
                feedback = JM.uncompleteTask(t);
            } catch (IOException e) {
                outputln(FILE_ERROR);
                return ;
            }
            JM.addCommandHistory("uncomplete", t);
            outputln(feedback);
        }
    }
    
    public String toString() {
        return "uncomplete";
    }

}
