
package jim.journal;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;

/*
 * This TemporaryJournalManager is just a rough copy of the JournalManager to aid the Unit Test
 * Because we don't want to involve the storage file when doing the unit test.
 */
public class TemporaryJournalManager extends JournalManager {
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    private List<Command_Task> historyOfCommand = new ArrayList<Command_Task>();
    private int historyIndex = -1; 

    /**
     * Returns a String representation of the current Journal state.
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
                if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, today) == 0) {
     				   timedTasks = timedTasks + current.toString() + "\n";
     			   }
            } else if (current instanceof FloatingTask) {
                floatingTasks = floatingTasks + current.toString() + "\n";
            }

        }

        output = output + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }


    @Override
    public ArrayList<Task> getAllTasks() {
        return storeAllTasks;
    }
    public ArrayList<Task> getuncompletedTasks() {
        ArrayList<Task> uncompletedTasks = new ArrayList<Task>();
        for (Task t : storeAllTasks) {
            if (!t.isCompleted()) {
                uncompletedTasks.add(t);
            }
        }
        return uncompletedTasks;
    }
    public ArrayList<Task> getcompletedTasks() {
        ArrayList<Task> completedTasks = new ArrayList<Task>();
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
        addCommandHistory("add", task);
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
    
    public void addCommandHistory(String cmd, Task someTask){
    	Command_Task command = new Command_Task(cmd, someTask);
    	historyIndex++;
    	historyOfCommand.add(historyIndex, command);
    }
    public boolean undoLastCommand(){
    	// get the last command in historyOfCommand
    	if (historyIndex > -1) {
    		Command_Task LastCommand = historyOfCommand.get(historyIndex--);
		    //add, edit, remove, complete
		    if (LastCommand.getCommand().equals("add")){
		    	removeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("edit")){
		        editTask(LastCommand.getSomeTask(), LastCommand.getEditTask());
		    } else if (LastCommand.getCommand().equals("remove")){
		    	addTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("complete")){
		    	try {
                    incompleteTask(LastCommand.getSomeTask());
                } catch (IOException e) {
                    e.printStackTrace();
                }
		    } else {
		        //error
		    }
    		return true;
    	} else {
			return false;
    	}
    }

}
