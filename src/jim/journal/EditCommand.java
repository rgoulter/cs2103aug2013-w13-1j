package jim.journal;



import java.util.ArrayList;
import java.util.List;

public class EditCommand extends Command {

    String description;
    JournalManager JM;

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



    @Override
    public String execute(JournalManager journalManager) {
        JM = journalManager;
        if (tasksToEdit == null) {
            SearchTool searchTool = new SearchTool(JM);
            matchingTasks = searchTool.searchByNonStrictDescription(description);
            if (matchingTasks.size() == 0){
                outputln("No Matching Tasks with the description provided.");
                return "Failure";
            }else if (matchingTasks.size() == 1 && taskChangedTo != null) {
                taskToEdit = matchingTasks.get(0);
                executeHelper();
                return "Success";
            }else if (matchingTasks.size() == 1 && taskChangedTo == null){
                outputln("The following Task will be edited.");
                outputln(matchingTasks.get(0).toString());
                taskToEdit = matchingTasks.get(0);
                return "NeedNewTask";
            }else{
                outputln("Type in just the index of tasks you wish to process. Please seperate them by ',''");
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
        return "NeedNewTask";
    }



    @Override
    public String thirdExecute(Task task) {
        taskChangedTo = task;
        executeHelper();
        return "Success";
    }
    
    private void executeHelper() {
        clearOutput();
        outputln("The task " + taskToEdit.getDescription() + " will be modified.");
        JM.editTask(taskToEdit, taskChangedTo);
    }
    
    public String toString() {
        return "Edit";
    }
       
}
