package jim.journal;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

public class JournalManager {
	private final GregorianCalendar cutoff = new GregorianCalendar();
	
    /**
     * Returns a String representation of the current Journal state.
     * @return
     */
    public String getDisplayString() {
        List<Task> upcomingTasks = this.getAllTasks();        
        String timedTasks = "";
        String floatingTasks = "";
        String output = "Upcoming Events:\n";
        
        Calendar today = Calendar.getInstance();
        
        for(Task current : upcomingTasks) {
        	if (current instanceof TimedTask) {
        		Calendar taskTime = ((TimedTask) current).getStartTime();
        		if (taskTime.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
     		           taskTime.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
     				   timedTasks = timedTasks + current.toString() + "\n";
     			   }
        	}
        	else if (current instanceof FloatingTask) {
        		floatingTasks = floatingTasks + current.toString() + "\n";
        	}
        	
        }
        
        output = output + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }
    
    public void addTask(Task task) {
        
    }
    
    public List<Task> getAllTasks() {
        // TODO: Not cheat on this.
        Calendar startTime = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar endTime =   new GregorianCalendar(2013, 10, 10, 15, 0);
        String description = "CS2103 Lecture";

        Task expectedTask = new TimedTask(startTime, endTime, description);
        List<Task> allTasks = new ArrayList<Task>();
        allTasks.add(expectedTask);
        
        return allTasks;
    }
}
