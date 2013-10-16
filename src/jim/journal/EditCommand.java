
package jim.journal;

import java.util.ArrayList;
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
            SearchTool searchTool = new SearchTool(journalManager);
            ArrayList<Task> matchingTasks = searchTool.searchByNonStrictDescription(description);
            if (matchingTasks.size() == 0){
                outputln("No Matching Tasks with the description provided.");
            }else if (matchingTasks.size() == 1){
                outputln("The following Task will be edited.");
                outputln(matchingTasks.get(0).toString());
                taskToEdit = matchingTasks.get(0);
            }else{
                outputln("Give the number of which task you wish to edit.");
                for (int i = 0; i < matchingTasks.size(); i++){
                    Task task = matchingTasks.get(i);
                    outputln(i + ", " + task.toString());
                }
               
                String IndexOfTasks = inputLine();
                String[] Indexes = IndexOfTasks.split(",");
                Integer[] IndexesInInteger = new Integer[Indexes.length];
                for (int i = 0; i < Indexes.length; i++){
                    IndexesInInteger[i] = Integer.parseInt(Indexes[i]);
                }
                //For now,
                assert(IndexesInInteger.length == 1);
                assert((IndexesInInteger[0] <= matchingTasks.size()) && (IndexesInInteger[0] >= 0) );
                assert(false);
                outputln("This Task is the choosen task by you: ");
                outputln(matchingTasks.get(IndexesInInteger[0]).toString());
                taskToEdit = matchingTasks.get(IndexesInInteger[0]);
            }
        }

        if (taskChangedTo == null) {
            // We weren't given a task to change to.
            // Get one from the cmd line:
            outputln("Please enter in a new Task to change this to:");

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
