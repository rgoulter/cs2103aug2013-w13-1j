
package jim.journal;

import java.util.List;
import java.util.ArrayList;
import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;


public class JournalManager {
    private List<Task> storeAllTasks = new ArrayList<Task>();



    /**
     * Returns a String representation of the current Journal state.
     * 
     * @return
     */
    public String getDisplayString() {
        List<Task> upcomingTasks = this.getAllTasks();
        String timedTasks = "";
        String floatingTasks = "";
        String output = "Upcoming Events:\n";

        MutableDateTime today = new MutableDateTime();

        for (Task current : upcomingTasks) {
            if (current instanceof TimedTask) {
            	MutableDateTime taskTime = ((TimedTask) current).getStartTime();                
                if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, today) == 0
        				/*taskTime.year() == today.year() &&
     		           taskTime.dayOfYear()  == today.dayOfYear() */) {
     				   timedTasks = timedTasks + current.toString() + "\n";
     			   }
            } else if (current instanceof FloatingTask) {
                floatingTasks = floatingTasks + current.toString() + "\n";
            }

        }

        output = output + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }



    public List<Task> getAllTasks() {
        // TODO: Not cheat on this.
        /*
         * Calendar startTime = new GregorianCalendar(2013, 10, 10, 14, 0);
         * Calendar endTime = new GregorianCalendar(2013, 10, 10, 15, 0); String
         * description = "CS2103 Lecture";
         * 
         * Task expectedTask = new TimedTask(startTime, endTime, description);
         * List<Task> allTasks = new ArrayList<Task>();
         * allTasks.add(expectedTask);
         */

        return storeAllTasks; // Added this change here! 1.
    }



    public List<Task> getuncompletedTasks() {
        List<Task> uncompletedTasks = new ArrayList<Task>();
        for (Task t : storeAllTasks) {
            if (!t.isCompleted()) {
                uncompletedTasks.add(t);
            }
        }
        return uncompletedTasks;
    }



    public List<Task> getcompletedTasks() {
        List<Task> completedTasks = new ArrayList<Task>();
        for (Task t : storeAllTasks) {
            if (t.isCompleted()) {
                completedTasks.add(t);
            }
        }
        return completedTasks;
    }



    /*
     * Following methods update the storeAllTasks, uncompletedTasks,
     * completedTasks.
     */
    public void addTask(Task task) {

        storeAllTasks.add(task);
    }



    public boolean removeTask(Task task) {
        return storeAllTasks.remove(task);
    }



    public String completeTask(Task task) {
        if (task.isCompleted()) {
            return "Task " +
                   task.toString() +
                   " has already been marked as completed.";
        } else {
            task.markAsCompleted();
            return "Completed Task: " + task.toString();
        }
    }



    public void editTask(Task old_task, Task new_task) {
        storeAllTasks.remove(old_task);
        storeAllTasks.add(new_task);
    }

}
