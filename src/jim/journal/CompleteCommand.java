
package jim.journal;

import java.util.ArrayList;
import java.util.List;



public class CompleteCommand extends Command {

    String description;



    public CompleteCommand(String des) {
        description = des;
    }



    @Override
    public void execute (JournalManager journalManager) {
        // TODO Auto-generated method stub
        List<Task> tasksCompleted = new ArrayList<Task>();
        List<Task> allTasks = journalManager.getAllTasks();

        for (Task task : allTasks) {
            if (task.getDescription().contains(description)) {
                tasksCompleted.add(task);
            }
        }
        if (tasksCompleted.isEmpty()) {
            outputln("Description was not matched.");
        }
        for (Task t : tasksCompleted) {
            String feedback = journalManager.completeTask(t);
            outputln(feedback);
        }

    }

}
