package jim.journal;



import java.util.ArrayList;
import java.util.List;

import org.joda.time.MutableDateTime;

public class EditCommand extends Command {

    String description;
    JournalManager JM;
    MutableDateTime date;

    List<Task> tasksToEdit = null;
    Task taskToEdit = null;
    Task taskChangedTo = null;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();



    public EditCommand(String des) {
        description = des;
    }

    public EditCommand(String des, Task newTask) {
        description = des;
        taskChangedTo = newTask;
    }
    public EditCommand(MutableDateTime d){
        date = d;
    }


    @Override
    public String execute(JournalManager journalManager) {
        JM = journalManager;
        if (tasksToEdit == null) {
            SearchTool searchTool = new SearchTool(JM);
            if (description != null){
                matchingTasks = searchTool.searchByNonStrictDescription(description);
            } else if (date != null){
                matchingTasks = searchTool.searchByDate(date);
            }
            if (matchingTasks.size() == 0){
                outputln("No Matching Tasks with the description provided.");
                return "Failure";
            } else if (matchingTasks.size() == 1 && taskChangedTo != null) {
                taskToEdit = matchingTasks.get(0);
                executeHelper();
                return "Success";
            } else if (matchingTasks.size() == 1 && taskChangedTo == null){
                outputln("The following Task will be edited.");
                outputln(matchingTasks.get(0).toString());
                taskToEdit = matchingTasks.get(0);
                outputln("Please enter a new task.");
                return "NeedNewTask";
            } else {
                outputln("Type in just the index of tasks you wish to process.");
                for (int i = 0; i < matchingTasks.size(); i++){
                    Task task = matchingTasks.get(i);
                    outputln(i + ", " + task.toString());
                }
                return "Pending";
            }
        }
        return "NeedNewTask";
        
    }
    
    @Override
    public String secondExecute(String secondInput){
            try{
               int j = Integer.parseInt(secondInput);
               taskToEdit = matchingTasks.get(j);
            }catch(NumberFormatException e){
                return "Pending";
            }
            clearOutput();
            outputln("The following Task will be edited.");
            outputln(taskToEdit.toString());
            outputln("Please enter a new task.");
        return "NeedNewTask";
    }

    // Pre-Cond: Command must be in third phase of execution ("NeedNewTask")
    public String getSelectedTaskDescription() {
        return taskToEdit.getDescription();
    }

    @Override
    public String thirdExecute(Task task) {
        taskChangedTo = task;
        executeHelper();
        return "Success";
    }
    
    private void executeHelper() {
        clearOutput();
        outputln("The following task is edited");
        outputln(taskToEdit.toString());
        outputln("To");
        outputln(taskChangedTo.toString());
        JM.editTask(taskToEdit, taskChangedTo);
        JM.addCommandHistory("edit", taskChangedTo, taskToEdit);
    }
    
    public String toString() {
        return "Edit";
    }
       
}
