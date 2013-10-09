
package jim.journal;

import java.util.ArrayList;
import java.util.List;



public class RemoveCommand extends Command {

    String description;



    public RemoveCommand(String des) {
        description = des;
    }


    
    @Override
    public void execute(JournalManager journalManager) {
        List<Task> taskToremove = new ArrayList<Task>();
        List<Task> allTasks = journalManager.getAllTasks();
        for (Task task : allTasks) {
            if (task.getDescription().equals(description)) {
                taskToremove.add(task);
            }
        }
        if (taskToremove.isEmpty()) {
            outputln("Description was not matched.");
        }

        for (Task t : taskToremove) {
            if (journalManager.removeTask(t)) {
                outputln("Removed task: " + t.toString());
            } else {
                outputln("Removing task was not successful.");
            }
        }

    }

}
