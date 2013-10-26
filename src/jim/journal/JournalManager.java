
package jim.journal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;


public class JournalManager {
	private static final int NO_COMMAND_EXECUTED_YET = -1;
	
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    TaskStorage taskStorage = new TaskStorage("taskstorage.txt");
    private int historyIndex = NO_COMMAND_EXECUTED_YET; 
    
    private List<Command_Task> historyOfCommand = new ArrayList<Command_Task>();
    private static Logger Logger = java.util.logging.Logger.getLogger("JournalManager");
     
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
    
    /*public String getDisplayString() {

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
                if (current.isCompleted()){
                    floatingTasks = floatingTasks + "[DONE] "+ current.toString() +"\n";
                }else{
                    floatingTasks = floatingTasks + current.toString() + "\n";
                }
            } else if (current instanceof DeadlineTask) {
            	MutableDateTime taskTime = ((DeadlineTask) current).getEndDate();    
                if (compareDate(taskTime, today)) {
                	deadlineTasks = deadlineTasks + current.toString() + "\n";
     			   }
            }

        }

        output = output + deadlineTasks + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }*/
    public String getDisplayString() {

        ArrayList<Task> upcomingTasks = this.getAllTasks();
        ArrayList<TimedTask> upcomingTimedTask = new ArrayList<TimedTask>();
        ArrayList<DeadlineTask> upcomingDeadlineTask = new ArrayList<DeadlineTask>();
        ArrayList<FloatingTask> upcomingFloatingTask = new ArrayList<FloatingTask>();
        
        String timedTasks = "", floatingTasks = "", deadlineTasks = "";
        String output = "Upcoming Events:\n";

        MutableDateTime today = new MutableDateTime();

        for (Task current : upcomingTasks) {
            if (current instanceof TimedTask) {
                MutableDateTime taskTime = ((TimedTask) current).getStartTime();    
                if (compareDate(taskTime, today)) {
                       upcomingTimedTask.add((TimedTask)current);
                   }
            } else if (current instanceof FloatingTask) {
                upcomingFloatingTask.add((FloatingTask)current);
            } else if (current instanceof DeadlineTask) {
                MutableDateTime taskTime = ((DeadlineTask) current).getEndDate();    
                if (compareDate(taskTime, today)) {
                    upcomingDeadlineTask.add((DeadlineTask)current);
                   }
            }

        }
        sortTimedTask(upcomingTimedTask);
        sortDeadlineTask(upcomingDeadlineTask);
        sortFloatingTask(upcomingFloatingTask);
        for (TimedTask task : upcomingTimedTask){
            timedTasks = timedTasks + task.toString() + "\n";
        }
        for (DeadlineTask task : upcomingDeadlineTask){
            deadlineTasks = deadlineTasks + task.toString() + "\n";
        }
        for (FloatingTask task : upcomingFloatingTask){
            if (task.isCompleted()){
                floatingTasks = floatingTasks + "[DONE] "+ task.toString() +"\n";
            }else{
                floatingTasks = task.toString() + "\n" + floatingTasks;
            }
        }
        output = output + deadlineTasks + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }

    public void saveToStorage(){
        Logger.log(Level.INFO, "going to save to storage");
        try {
            this.taskStorage.writeToFile(storeAllTasks);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.log(Level.WARNING, "file error");
        }
        Logger.log(Level.INFO, "end of processing");
    }

    public ArrayList<Task> getAllTasks() {
        try {
            storeAllTasks = taskStorage.getAllTasks();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return storeAllTasks; 
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
            storeAllTasks.remove(task);
            task.markAsCompleted();
            storeAllTasks.add(task);
            saveToStorage();
            return "Completed Task: " + task.toString();
        }
    }
    
    public void incompleteTask(Task task) {
    	for (Task current: storeAllTasks) {
    		if (task.equals(current)){
		        if (!task.isCompleted()) {
		            System.out.println( "Task " +
		                   task.toString() +
		                   " is currently incomplete.");
		        } else {
		            storeAllTasks.remove(task);
		            task.markAsIncompleted();
		            storeAllTasks.add(task);
		            saveToStorage();
		            System.out.println(  "Incompleted Task: " + task.toString());
		        }
	    	}
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
    public void sortTimedTask(ArrayList<TimedTask> timedTasks){
        Collections.sort(timedTasks);
    }
    public void sortDeadlineTask(ArrayList<DeadlineTask> deadlineTasks){
        Collections.sort(deadlineTasks);
    }
    public void sortFloatingTask(ArrayList<FloatingTask> floatingTasks){
        
    }

}
