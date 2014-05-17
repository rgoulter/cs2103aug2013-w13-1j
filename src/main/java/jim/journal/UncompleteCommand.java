//@author A0097081B
package jim.journal;

import java.util.ArrayList;

import org.joda.time.MutableDateTime;

public class UncompleteCommand extends Command {
    private static final String FILE_ERROR = "FILE ERROR: Please add any tasks in the box above to create the storage file. Enjoy JIM!";
    private static final String COMMAND_UNCOMPLETE = "Uncomplete";
    private static final String MESSAGE_DESCRIPTION_MISMATCH = "Description was not matched.";
    private static final String MESSAGE_INDEX_SELECTION = "Type in just the index of tasks you wish to process. Please seperate them by ','";
    private static final String MESSAGE_INDEX_FOUND_ITEM = "%d, %s";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_PENDING = "Pending";
    private static final String EXECUTION_STATUS_FAILURE = "Failure";
    
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
            return EXECUTION_STATUS_FAILURE;
        }
        
        if (description != null ){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        } else if (EndDate != null ){
            matchingTasks = searchTool.searchByDate(EndDate);
        }
          
        if (matchingTasks.size() == 0){
            outputln(MESSAGE_DESCRIPTION_MISMATCH);
            return EXECUTION_STATUS_SUCCESS;
        } else if (matchingTasks.size() == 1){
        	taskToUncomplete.add(matchingTasks.get(0));
            executeHelper();
            return EXECUTION_STATUS_SUCCESS;
        } else {
            outputln(MESSAGE_INDEX_SELECTION);
            for (int i = 0; i < matchingTasks.size(); i++){
                Task task = matchingTasks.get(i);
                outputln(String.format(MESSAGE_INDEX_FOUND_ITEM, i, task.toString()));
            }
            return EXECUTION_STATUS_PENDING;
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
                return EXECUTION_STATUS_PENDING;
            }
        }
        executeHelper();
        return EXECUTION_STATUS_SUCCESS;
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
            } catch (Exception e) {
                outputln(FILE_ERROR);
                return ;
            }
            JM.addCommandHistory(COMMAND_UNCOMPLETE, t);
            outputln(feedback);
        }
    }
    
    public String toString() {
        return COMMAND_UNCOMPLETE;
    }

}
