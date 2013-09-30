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
		for (Task task : allTasks){
			if (task.getDescription().contains(description)) {
				taskToremove.add(task);
			}
		}
    	for (Task t : taskToremove){
    		journalManager.removeTask(t);
    	}
    }

}
