package jim.journal;

import java.util.List;

import jim.suggestions.SuggestionManager;

public class EditCommand extends Command {
	
	List<Task> tasksToEdit;
    Task taskToEdit;
    Task taskChangedTo;
    
    //TODO maybe editcommand should only accept one taskToEdit not a list of task. 
    //This will depend on how the searchbydescription function works.
    //changedToTask is the new task which user want to replace with the older one.
    
    public EditCommand(List<Task> tasksWhichMatchDescription) {
        // Expected behavior: ask for which one to replace.
        tasksToEdit = tasksWhichMatchDescription;
        taskToEdit = null;
        taskChangedTo = null;
    }
    
	public EditCommand(List<Task> tasksWhichMatchDescription, Task changedToTask) {
    	tasksToEdit = tasksWhichMatchDescription;
        taskToEdit = null;
    	taskChangedTo = changedToTask;
	}

	@Override
    public void execute(JournalManager journalManager) {
        if (taskChangedTo == null) {
            // We weren't given a task to change to.
            // Get one from the cmd line:
            outputln("Please enter in a Task to change this to:");
            
            // Assume format here is in the format of a task.
            String newTaskCommandString = inputLine();
            String[] newTaskStringWords = newTaskCommandString.split(" ");
            
            // This is a bit unelegant.
            SuggestionManager suggestionManager = new SuggestionManager();
            taskChangedTo = suggestionManager.parseTask(newTaskStringWords);
        }
        
        if (taskToEdit == null && tasksToEdit != null) {
            // This is the lazy way to resolve the way searchbydescription method works..
            taskToEdit = tasksToEdit.get(0);
        }
	    
		journalManager.editTask(taskToEdit, taskChangedTo);
    	
    }



}
