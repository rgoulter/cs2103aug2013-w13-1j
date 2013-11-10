//@author A0105572L
package jim.journal;

import java.util.ArrayList;
import org.joda.time.MutableDateTime;

public class CompleteCommand extends Command {
    //For system purpose
    private static final String COMMANDTYPE = "complete";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_PENDING = "Pending";
    private static final String EXECUTION_STATUS_FAILURE = "Failure";
    
    //For display to user purpose
    private static final String INFO_CHOOSE_ONE_TASK = "Type in just the index of tasks you wish to process. Please seperate them by '%s'";
    private static final String INFO_NOMATCH = "Description was not matched.";
    private static final String INFO_SEPERATER = ",";
    private static final String INFO_SPACE = " ";
    private static final String INFO_FILE_ERROR = "FILE_ERROR";
    
    //For taking tasks from the array
    private static final int NUMOFTASK_NOMATCH = 0;
    private static final int NUMOFTASK_ONEMATCH = 1;
    private static final int FIRST_TASK_INDEX = 0;
    
    String description;
    MutableDateTime EndDate;
    JournalManager MyJournalManager;
    ArrayList<Task> taskToComplete = new ArrayList<Task>();
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    
    //Constructor for complete by description
    public CompleteCommand(String des) {
        description = des;
    }
    //Constructor for complete by date and time
    public CompleteCommand(MutableDateTime ED){
        EndDate = ED;
    }

    @Override
    public String execute(JournalManager journalManager) {
        MyJournalManager = journalManager;
        SearchTool searchTool;
        try {
            searchTool = new SearchTool(MyJournalManager);
        } catch (Exception e) {
            outputln(INFO_FILE_ERROR);
            return EXECUTION_STATUS_FAILURE;
        }
        if (description != null){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        } else if (EndDate != null){
            matchingTasks = searchTool.searchByDate(EndDate);
        }
        if (matchingTasks.size() == NUMOFTASK_NOMATCH){
            outputln(INFO_NOMATCH);
            return EXECUTION_STATUS_SUCCESS;
        } else if (matchingTasks.size() == NUMOFTASK_ONEMATCH){
            taskToComplete.add(matchingTasks.get(FIRST_TASK_INDEX));
            executeHelper();
            return EXECUTION_STATUS_SUCCESS;
        } else {
            outputln( String.format(INFO_CHOOSE_ONE_TASK, INFO_SEPERATER));
            for (int i = 0; i < matchingTasks.size(); i++){
                Task task = matchingTasks.get(i);
                outputln(i + INFO_SEPERATER + INFO_SPACE + task.toString());
            }
            return EXECUTION_STATUS_PENDING;
        }
    }
            
    @Override
    public String secondExecute(String secondInput) {
        clearOutput();
        
        String[] IndexesOfTasks = secondInput.split(INFO_SEPERATER);
        for (int i = 0; i < IndexesOfTasks.length; i++){
            try{
               int choosenIndex = Integer.parseInt(IndexesOfTasks[i]);
               taskToComplete.add(matchingTasks.get(choosenIndex));
            }catch(NumberFormatException e){
                return EXECUTION_STATUS_PENDING;
            }
        }
        executeHelper();
        return EXECUTION_STATUS_SUCCESS;
    }
    
    @Override
    public String thirdExecute(Task task) {
        return null;
    }
    private void executeHelper(){
        for (Task task : taskToComplete) {
            String feedback;
            try {
                feedback = MyJournalManager.completeTask(task);
            } catch (Exception e) {
                feedback = INFO_FILE_ERROR;
                outputln(feedback);
                return;
            }
            MyJournalManager.addCommandHistory(COMMANDTYPE, task);
            outputln(feedback);
        }
    }
    
    public String toString() {
        return COMMANDTYPE;
    }

}
