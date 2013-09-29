package jim.journal;

import java.util.List;

public class EditCommand extends Command {
	
	List<Task> tasksToEdit;
    Task tasktoEdit;
    Task taskChangedTo;
    
    //TODO maybe editcommand should only accept one taskToEdit not a list of task. 
    //This will depend on how the searchbydescription function works.
    //changedToTask is the new task which user want to replace with the older one.
    
	public EditCommand(List<Task> tasksWhichMatchDescription, Task changedToTask) {
		// TODO Auto-generated constructor stub
    	tasksToEdit = tasksWhichMatchDescription;
    	taskChangedTo = changedToTask;
	}

	@Override
    public void execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
		
		journalManager.editTask(tasktoEdit, taskChangedTo);
    	
    }



}
