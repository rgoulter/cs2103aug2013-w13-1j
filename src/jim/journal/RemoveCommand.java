//@author A0105572L
package jim.journal;

import java.util.ArrayList;

import org.joda.time.MutableDateTime;

public class RemoveCommand extends Command {
    //For system purpose
    private static final String COMMANDTYPE = "remove";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_PENDING = "Pending";
    private static final String EXECUTION_STATUS_FAILURE = "Failure";
    
    //For display to user purpose
    private static final String INFO_CHOOSE_ONE_TASK = "Type in just the index of tasks you wish to process. Please seperate them by '%s'";
    private static final String INFO_NOMATCH = "Description was not matched.";
    private static final String INFO_SEPERATER = ",";
    private static final String INFO_SPACE = " ";
    private static final String INFO_FILE_ERROR = "FILE_ERROR: Please add any tasks in the box above to create the storage file. Enjoy JIM!";
    private static final String INFO_REMOVE_TASK = "Removed task: %s";
    
    //For taking tasks from the array
    private static final int NUMOFTASK_NOMATCH = 0;
    private static final int NUMOFTASK_ONEMATCH = 1;
    private static final int FIRST_TASK_INDEX = 0;
    private static final String REMOVE_NOT_SUCCESSFUL = "Removing task was not successful.";
    String description = null;
    JournalManager MyJournalManager;
    MutableDateTime date;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    ArrayList<Task> taskToRemove = new ArrayList<Task>();

    //Constructor for input with only description
    public RemoveCommand(String des) {
        description = des;
    }
    //Constructor for input with only date
    public RemoveCommand(MutableDateTime d){
        date = d;
    }

    @Override
    public String execute(JournalManager journalManager) {
        MyJournalManager = journalManager;
        
        SearchTool searchTool;
        try {
            searchTool = new SearchTool(journalManager);
        } catch (Exception e) {
            outputln(INFO_FILE_ERROR);
            return EXECUTION_STATUS_FAILURE;
        }
        if (description != null){
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        }else if (date != null){
            matchingTasks = searchTool.searchByDate(date);
        }
        if (matchingTasks.size() == NUMOFTASK_NOMATCH){
            outputln(INFO_NOMATCH);
            return EXECUTION_STATUS_SUCCESS;
        }else if (matchingTasks.size() == NUMOFTASK_ONEMATCH){
            taskToRemove.add(matchingTasks.get(FIRST_TASK_INDEX));
            executeHelper();
            return EXECUTION_STATUS_SUCCESS;
        }else{
            outputln(String.format(INFO_CHOOSE_ONE_TASK, INFO_SEPERATER));
            for (int i = FIRST_TASK_INDEX; i < matchingTasks.size(); i++){
                Task task = matchingTasks.get(i);
                outputln(i + INFO_SEPERATER + INFO_SPACE + task.toString());
            }
            return EXECUTION_STATUS_PENDING;
        }
    }

    @Override
    public String secondExecute(String secondInput) {
        String[] IndexesOfTasks = secondInput.split(INFO_SEPERATER);
        for (int i = FIRST_TASK_INDEX; i < IndexesOfTasks.length; i++){
            try{
               int indexOfTask = Integer.parseInt(IndexesOfTasks[i]);
               taskToRemove.add(matchingTasks.get(indexOfTask));
            }catch(Exception e){
               return EXECUTION_STATUS_PENDING;
            }
        }
        return executeHelper();
    }

    @Override
    public String thirdExecute(Task task) {
        return null;
    }
    
    private String executeHelper(){
        clearOutput();
        for (Task task : taskToRemove) {
            try {
                if (MyJournalManager.removeTask(task)) {
                MyJournalManager.addCommandHistory(COMMANDTYPE, task);
                    outputln(String.format(INFO_REMOVE_TASK, task.toString()));
                } else {
                    outputln(REMOVE_NOT_SUCCESSFUL);
                }
            } catch (Exception e) {
                clearOutput();
                outputln(INFO_FILE_ERROR);
                return EXECUTION_STATUS_FAILURE;
            }
        }
        return EXECUTION_STATUS_SUCCESS;
    }
    
    public String toString() {
        return COMMANDTYPE;
    }

}
