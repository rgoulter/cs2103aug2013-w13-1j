//@author A0105572L
package jim.journal;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.MutableDateTime;

public class EditCommand extends Command {
    //For system purpose
    private static final String COMMANDTYPE = "edit";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_PENDING = "Pending";
    private static final String EXECUTION_STATUS_FAILURE = "Failure";
    private static final String EXECUTION_STATUS_NEEDNEWTASK = "NeedNewTask";
    
    //For display to user purpose
    private static final String INFO_CHOOSE_ONE_TASK = "Type in just the index of tasks you wish to process.";
    private static final String INFO_NOMATCH = "No Matching Tasks with the description provided.";
    private static final String INFO_EDIT_TASK = "The following task is edited";
    private static final String INFO_REQUEST_NEW_TASK = "Please enter a new task.";
    private static final String INFO_WILL_EDIT_TASK = "The following Task will be edited.";
    private static final String INFO_NEW_OLD_TASK_CONNECTOR = "To";
    private static final String INFO_SEPERATER = ",";
    private static final String INFO_SPACE = " ";
    private static final String INFO_FILE_ERROR = "FILE_ERROR: Please add any tasks in the box above to create the storage file. Enjoy JIM!";
    
    //For taking tasks from the array
    private static final int NUMOFTASK_NOMATCH = 0;
    private static final int NUMOFTASK_ONEMATCH = 1;
    private static final int FIRST_TASK_INDEX = 0;
    
    String description;
    JournalManager MyJournalManager;
    MutableDateTime date;
    List<Task> tasksToEdit = null;
    Task taskToEdit = null;
    Task taskChangedTo = null;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();


    //Constructor for input with only description
    public EditCommand(String des) {
        description = des;
    }
    //Constructor for input with a description of the old task and a new task
    public EditCommand(String des, Task newTask) {
        description = des;
        taskChangedTo = newTask;
    }
    //Constructor for input with a date
    public EditCommand(MutableDateTime d){
        date = d;
    }


    @Override
    public String execute(JournalManager journalManager) {
        MyJournalManager = journalManager;
        if (tasksToEdit == null) {
            SearchTool searchTool;
            try {
                searchTool = new SearchTool(MyJournalManager);
            } catch (Exception e) {
                outputln(INFO_FILE_ERROR);
                return EXECUTION_STATUS_FAILURE;
            }
            if (description != null){
                matchingTasks = searchTool.searchByNonStrictDescription(description);
            } else if (date != null){
                matchingTasks = searchTool.searchByDate(date);
            }
            if (matchingTasks.size() == NUMOFTASK_NOMATCH){
                outputln(INFO_NOMATCH);
                return EXECUTION_STATUS_FAILURE;
            } else if (matchingTasks.size() == NUMOFTASK_ONEMATCH && taskChangedTo != null) {
                taskToEdit = matchingTasks.get(FIRST_TASK_INDEX);
                executeHelper();
                return EXECUTION_STATUS_SUCCESS;
            } else if (matchingTasks.size() == NUMOFTASK_ONEMATCH && taskChangedTo == null){                
                outputln(INFO_WILL_EDIT_TASK);
                outputln(matchingTasks.get(FIRST_TASK_INDEX).toString());
                taskToEdit = matchingTasks.get(FIRST_TASK_INDEX);
                outputln(INFO_REQUEST_NEW_TASK);
                return EXECUTION_STATUS_NEEDNEWTASK;
            } else {
                outputln(INFO_CHOOSE_ONE_TASK);
                for (int i = FIRST_TASK_INDEX; i < matchingTasks.size(); i++){
                    Task task = matchingTasks.get(i);
                    outputln(i + INFO_SEPERATER + INFO_SPACE + task.toString());
                }
                return EXECUTION_STATUS_PENDING;
            }
        }
        return EXECUTION_STATUS_NEEDNEWTASK;
        
    }
    
    @Override
    public String secondExecute(String secondInput){
            try{
               int indexForTaskToEdit = Integer.parseInt(secondInput);
               taskToEdit = matchingTasks.get(indexForTaskToEdit);
            }catch(NumberFormatException e){
                return EXECUTION_STATUS_PENDING;
            }
            clearOutput();
            outputln(INFO_WILL_EDIT_TASK);
            outputln(taskToEdit.toString());
            outputln(INFO_REQUEST_NEW_TASK);
        return EXECUTION_STATUS_NEEDNEWTASK;
    }

    // Pre-Condition: Command must be in third phase of execution ("NeedNewTask")
    public String getSelectedTaskDescription(){
        return taskToEdit.toStringForEditCommand();
    }

    @Override
    public String thirdExecute(Task task) {
        taskChangedTo = task;
        return executeHelper();
        
    }
    
    private String executeHelper() {
        clearOutput();
        outputln(INFO_EDIT_TASK);
        outputln(taskToEdit.toString());
        outputln(INFO_NEW_OLD_TASK_CONNECTOR);
        outputln(taskChangedTo.toString());
        try {
            MyJournalManager.editTask(taskToEdit, taskChangedTo);
        } catch (Exception e) {
            clearOutput();
            outputln(INFO_FILE_ERROR);
            return EXECUTION_STATUS_FAILURE;
        }
        MyJournalManager.addCommandHistory(COMMANDTYPE, taskChangedTo, taskToEdit);
        return EXECUTION_STATUS_SUCCESS;
    }
    
    public String toString() {
        return COMMANDTYPE;
    }
       
}
