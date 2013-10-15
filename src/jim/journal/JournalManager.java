
package jim.journal;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;


public class JournalManager {
	private static final int NO_COMMAND_EXECUTED_YET = -1;
	
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    TaskStorage taskStorage = new TaskStorage("taskstorage.txt");
    private int historyIndex = NO_COMMAND_EXECUTED_YET; 
    
    private List<Command_Task> historyOfCommand = new ArrayList<Command_Task>();

    /**
     * Returns a String representation of the current Journal state.
     * 
     * @return
     */
    /*  This method is to reduce the time of read through the storage file every time when want to change the content.
     *  However, if we choose to use this method, the storeAllTasks will only be written to file when exit.
     */
   
    public void initializeJournalManager(){
        try {
            storeAllTasks = taskStorage.getAllTasks();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public boolean compareDate(MutableDateTime taskTime, MutableDateTime current) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, current) == 0) {
    		return true;
    	} else return false;
    }
    
    public String getDisplayString() {
        //List<Task> upcomingTasks = storeAllTasks;
        List<Task> upcomingTasks = this.getAllTasks();
        String timedTasks = "", floatingTasks = "", deadlineTasks = "";
        String output = "Upcoming Events:\n";

        MutableDateTime today = new MutableDateTime();

        for (Task current : upcomingTasks) {
            if (current instanceof TimedTask) {
            	MutableDateTime taskTime = ((TimedTask) current).getStartTime();    
                if (compareDate(taskTime, today)) {
     				   timedTasks = timedTasks + current.toString() + "\n";
     			   }
            } else if (current instanceof FloatingTask) {
                floatingTasks = floatingTasks + current.toString() + "\n";
            } else if (current instanceof DeadlineTask) {
            	MutableDateTime taskTime = ((DeadlineTask) current).getEndDate();    
                if (compareDate(taskTime, today)) {
                	deadlineTasks = deadlineTasks + current.toString() + "\n";
     			   }
            }

        }

        output = output + deadlineTasks + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }



    public List<Task> getAllTasks() {
        try {
            storeAllTasks = taskStorage.getAllTasks();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return storeAllTasks; 
    }

    public void saveToStorage(){
        try {
            this.taskStorage.writeToFile(storeAllTasks);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<Task> getuncompletedTasks() {
        
        List<Task> uncompletedTasks = new ArrayList<Task>();
        getAllTasks();
        for (Task t : storeAllTasks) {
            if (!t.isCompleted()) {
                uncompletedTasks.add(t);
            }
        }
        saveToStorage();
        return uncompletedTasks;
    }



    public List<Task> getcompletedTasks() {
        List<Task> completedTasks = new ArrayList<Task>();
        getAllTasks();
        for (Task t : storeAllTasks) {
            if (t.isCompleted()) {
                completedTasks.add(t);
            }
        }
        saveToStorage();
        return completedTasks;
    }



    /*
     * Following methods update the storeAllTasks, uncompletedTasks,
     * completedTasks.
     */
    public void addTask(Task task) {

        storeAllTasks.add(task);
        try {
            taskStorage.recordNewTask(task);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public boolean removeTask(Task task) {
        boolean result = false;
        getAllTasks();
        if (storeAllTasks.remove(task)){
            result = true;
            saveToStorage();
        }
        return result;
    }



    public String completeTask(Task task) {
        if (task.isCompleted()) {
            return "Task " +
                   task.toString() +
                   " has already been marked as completed.";
        } else {
            
            task.markAsCompleted();
            saveToStorage();
            return "Completed Task: " + task.toString();
        }
    }
    
    public String incompleteTask(Task task) {
        if (!task.isCompleted()) {
            return "Task " +
                   task.toString() +
                   " is currently incomplete.";
        } else {
            task.markAsIncompleted();
            saveToStorage();
            return "Incompleted Task: " + task.toString();
        }
    }

    public void editTask(Task old_task, Task new_task) {
        getAllTasks();
        storeAllTasks.remove(old_task);
        storeAllTasks.add(new_task);
        saveToStorage();
    }
    
    public String getPreviousCommand(){
        return historyOfCommand.get(historyOfCommand.size()-1).getCommand();
    }
    //only certain command need to push.
    //add, edit, complete, remove.
    public void addCommandHistory(String cmd, Task someTask){
    	Command_Task command = new Command_Task(cmd, someTask);
    	historyIndex++;
    	historyOfCommand.add(historyIndex, command);
    }
    public void undoLastCommand(){
    	// get the last command in historyOfCommand
    	if (historyIndex == NO_COMMAND_EXECUTED_YET) {
    		// do nothing
    	} else {
			Command_Task LastCommand = historyOfCommand.get(historyIndex--);
		    //add, edit, remove, complete
		    if (LastCommand.getCommand().equals("add")){
		    	removeTask(LastCommand.getTask());
		    } else if (LastCommand.getCommand().equals("edit")){
		        
		    } else if (LastCommand.getCommand().equals("remove")){
		    	addTask(LastCommand.getTask());
		    } else if (LastCommand.getCommand().equals("complete")){
		    	incompleteTask(LastCommand.getTask());
		    } else {
		        //error
		    }
    	}
    }
    
	class Command_Task {
		String cmd;
		Task someTask;
	
		public Command_Task(String cmd, Task temp) {
			this.cmd = cmd;
			someTask = temp;
		}
	
		String getCommand() { return cmd; }
		Task getTask() { return someTask; }
	}


}
