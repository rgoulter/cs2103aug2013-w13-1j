
package jim.journal;

import java.util.List;

import jim.suggestions.SuggestionManager;



public class EditCommand extends Command {

    String description;

    List<Task> tasksToEdit = null;
    Task taskToEdit = null;
    Task taskChangedTo = null;



    public EditCommand(String des) {
        description = des;
    }



    public EditCommand(String des, Task newTask) {
        description = des;
        taskChangedTo = newTask;
    }



    @Override
    public void execute(JournalManager journalManager) {
        if (tasksToEdit == null) {
            // TODO: Probably we will need to get access to a/the Suggestion
            // Manager somehow. How??
            SuggestionManager suggMan = new SuggestionManager();

            tasksToEdit = suggMan.searchForTasksByDescription(journalManager,
                                                              description);
        }

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
            // This is the lazy way to resolve the way searchbydescription
            // method works..
            taskToEdit = tasksToEdit.get(0);
        }

        journalManager.editTask(taskToEdit, taskChangedTo);

    }

}
