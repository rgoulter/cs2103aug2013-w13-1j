
package jim.journal;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import jim.Configuration;
import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;


public class JournalManager {

    private static Configuration configManager = Configuration.getConfiguration();
    private TaskStorage taskStorage = new TaskStorage(configManager.getOutputFileName());
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    private static final int NO_COMMAND_EXECUTED_YET = -1;
    private int historyIndex = NO_COMMAND_EXECUTED_YET;
    private ArrayList<Command_Task> historyOfCommand = new ArrayList<Command_Task>();
    private boolean newTrueCommand = true;
    private String TASKTITILE_ONE = "Upcoming Events:\n";
    private String TASKTITILE_TWO = "\n\nTodo:\n";
    private String STRING_INITIAL = "";
    private String END_OF_LINE = "\n";
    private String SIGN_FOR_COMPLETED_TASK = "[DONE] ";
    private int ZERO_FOR_COMPARE = 0;
     
    /**
     * Returns a String representation of the current Journal state.
     * 
     */

    public boolean compareDate(MutableDateTime taskTime, MutableDateTime currentTime) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, currentTime) == ZERO_FOR_COMPARE) {
    		return true;
    	} else {
    	    return false;
    	}
    }
    
    public String getDisplayString() {

        ArrayList<Task> upcomingTasks = this.getAllTasks();
        ArrayList<TimedTask> upcomingTimedTask = new ArrayList<TimedTask>();
        ArrayList<DeadlineTask> upcomingDeadlineTask = new ArrayList<DeadlineTask>();
        ArrayList<FloatingTask> upcomingFloatingTask = new ArrayList<FloatingTask>();
        
        String timedTasks = STRING_INITIAL, floatingTasks = STRING_INITIAL, deadlineTasks = STRING_INITIAL;
        String output = TASKTITILE_ONE;

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
      
        for (TimedTask task : upcomingTimedTask){
            timedTasks = timedTasks + task.toString() + END_OF_LINE;
        }
        for (DeadlineTask task : upcomingDeadlineTask){
            deadlineTasks = deadlineTasks + task.toString() + END_OF_LINE;
        }
        for (FloatingTask task : upcomingFloatingTask){
            if (task.isCompleted()){
                floatingTasks = floatingTasks + SIGN_FOR_COMPLETED_TASK+ task.toString() +END_OF_LINE;
            }else{
                floatingTasks = task.toString() + END_OF_LINE + floatingTasks;
            }
        }
        output = output + deadlineTasks + timedTasks + TASKTITILE_TWO + floatingTasks;
        return output;
    }

    public void saveToStorage(){
        
        try {
            this.taskStorage.writeToFile(storeAllTasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    	clearPastCmds();
        storeAllTasks.add(task);
        try {
            taskStorage.recordNewTask(task);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     //   System.out.println("history Index = " + historyIndex);
    }

    public boolean removeTask(Task task) {
    	clearPastCmds();
        boolean result = false;
        getAllTasks();
        if (storeAllTasks.remove(task)){
            result = true;
            saveToStorage();
        }
        // System.out.println("DEBUG: history Index = " + historyIndex);
        return result;
    }

    public String completeTask(Task task) {
    	clearPastCmds();
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
    
    public String uncompleteTask(Task task) {
    	clearPastCmds();
        if (!task.isCompleted()) {
            return "Task " +
                   task.toString() +
                  " has not been completed.";
        } else {
            storeAllTasks.remove(task);
            task.markAsIncompleted();
            storeAllTasks.add(task);
            saveToStorage();
            return "Uncompleted Task: " + task.toString();
        }
    }
    
    public void incompleteTask(Task task) {
    	clearPastCmds();
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
    	if (old_task.toString().equals(new_task.toString())){
    	    return;
    	}else{
    	    clearPastCmds();
    	    getAllTasks();
    	    storeAllTasks.remove(old_task);
    	    storeAllTasks.add(new_task);
    	    saveToStorage();
    	}
    }
    
    public String getPreviousCommand(){
        return historyOfCommand.get(historyOfCommand.size()-1).getCommand();
    }
    //only certain command need to push.
    //add, edit, complete, remove.
    public void addCommandHistory(String cmd, Task someTask, Task editTask){
    	Command_Task command = new Command_Task(cmd, someTask, editTask);
    	historyIndex++;
    	historyOfCommand.add(historyIndex, command);
    //	System.out.println("history Index = " + historyIndex);
    }
    
    public void addCommandHistory(String cmd, Task someTask){
    	addCommandHistory(cmd, someTask, null);
    }

    public boolean undoLastCommand(){
    	// get the last command in historyOfCommand
    	if (historyIndex > NO_COMMAND_EXECUTED_YET) {
        	newTrueCommand = false;
    		Command_Task LastCommand = historyOfCommand.get(historyIndex--);
		    //add, edit, remove, complete
		    if (LastCommand.getCommand().equals("add")){
		    	removeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("edit")){
		    	editTask(LastCommand.getSomeTask(), LastCommand.getEditTask());
		    } else if (LastCommand.getCommand().equals("remove")){
		    	addTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("complete")){
		    	incompleteTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("uncomplete")){
		    	completeTask(LastCommand.getSomeTask());
		    } else {
		        //error
		    }
	//	    System.out.println("history Index = " + historyIndex);
		    return true;
    	} else {
   // 		System.out.println("history Index = " + historyIndex);
    		return false;
    	}
    }
    
    public boolean redoUndoCommand(){
    	if (historyOfCommand.size() >= 0 && historyIndex < historyOfCommand.size() - 1) {
    		newTrueCommand = false;
    		Command_Task LastCommand = historyOfCommand.get(++historyIndex);
		    //add, edit, remove, complete
		    if (LastCommand.getCommand().equals("add")){
		    	addTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("edit")){
		    	editTask(LastCommand.getEditTask(), LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("remove")){
		    	removeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("complete")){
		    	completeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals("uncomplete")){
		    	uncompleteTask(LastCommand.getSomeTask());
		    } else {
		        //error
		    }
	//	    System.out.println("history Index = " + historyIndex);
		    return true;
    	} else {
    //		System.out.println("history Index = " + historyIndex);
    		return false;
    	}
    	
    }
    
    private void clearPastCmds(){
    	// if true, it means addTask() (or remove, edit, complete) is not called from undoLastCommand()
    	if (historyOfCommand.size() > 0 && historyIndex != historyOfCommand.size()-1) {
    		if (newTrueCommand) {
   				historyOfCommand.subList(historyIndex+1, historyOfCommand.size()-1).clear();
    		} else {
	    		newTrueCommand = true;
	    	}
    	} else {
    		newTrueCommand = true;
    	}
    }
    
	class Command_Task {
		String cmd;
		Task someTask, editTask;
		
		public Command_Task(String cmd, Task someTask, Task editTask) {
			this.cmd = cmd;
			this.someTask = someTask;
			this.editTask = editTask;
		}
	
		public Command_Task(String cmd, Task someTask) {
			this(cmd, someTask, null);
		}
	
		String getCommand() { return cmd; }
		Task getSomeTask() { return someTask; }
		Task getEditTask() { return editTask; }
	}
    public void sortTimedTask(ArrayList<TimedTask> timedTasks){
        Collections.sort(timedTasks);
    }
    public void sortDeadlineTask(ArrayList<DeadlineTask> deadlineTasks){
        Collections.sort(deadlineTasks);
    }
    
}
