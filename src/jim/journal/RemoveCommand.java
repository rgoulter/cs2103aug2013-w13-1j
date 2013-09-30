package jim.journal;

import java.util.Calendar;
import java.util.List;

public class RemoveCommand extends Command {
	List<Task> taskToremove;
	
	public RemoveCommand(Calendar startTime, Calendar endTime, String description) {
        taskToremove.add(new TimedTask(startTime, endTime, description));
    }
    
    public RemoveCommand(String description) {
    	taskToremove.add(new FloatingTask(description));
    }
    
    public RemoveCommand(List<Task> tasks){
    	taskToremove = tasks;
    }
    
    @Override
    public void execute(JournalManager journalManager) {
    	for (Task t : taskToremove){
    		journalManager.removeTask(t);
    	}
    }

}
