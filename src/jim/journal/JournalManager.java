
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
    private ArrayList<Task> allTasksInStorage = new ArrayList<Task>();
    private static final int NO_COMMAND_EXECUTED_YET = -1;
    private static final String FILE_ERROR = "FILE ERROR";
    private int historyIndex = NO_COMMAND_EXECUTED_YET;
    private ArrayList<Command_Task> historyOfCommand = new ArrayList<Command_Task>();
    private boolean newTrueCommand = true;
    private int ZERO_FOR_COMPARE = 0;
    
    //For display to user
    private String TASKTITILE_ONE = "Upcoming Events:\n";
    private String TASKTITILE_TWO = "\n\nTodo:\n";
    private String STRING_INITIAL = "";
    private String END_OF_LINE = "\n";
    private String SIGN_FOR_COMPLETED_TASK = "[DONE] ";
    private String TASK_ALREADY_COMPLETED = "Task %s has already been marked as completed.";
    private String TASK_COMPLETED = "Completed Task: %s";
    private String TASK_NOT_COMPLETED_YET = "Task %S has not been completed.";
    private String TASK_UNCOMPLETED = "Uncompleted Task: %s";


     
    /**
     * Returns a String representation of the current Journal state.
     */

    public boolean compareDate(MutableDateTime taskTime, MutableDateTime currentTime) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, currentTime) == ZERO_FOR_COMPARE) {
    		return true;
    	} else {
    	    return false;
    	}
    }
    //@author A0105572L
    public String getDisplayString() {

        ArrayList<Task> upcomingTasks;
        try {
            upcomingTasks = this.getAllTasks();
        } catch (Exception e) {
            return FILE_ERROR;
        }
        ArrayList<TimedTask> upcomingTimedTask = new ArrayList<TimedTask>();
        ArrayList<DeadlineTask> upcomingDeadlineTask = new ArrayList<DeadlineTask>();
        ArrayList<FloatingTask> upcomingFloatingTask = new ArrayList<FloatingTask>();
        String timedTasks = STRING_INITIAL;
        String floatingTasks = STRING_INITIAL;
        String deadlineTasks = STRING_INITIAL;
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
    //@author A0105572L
    public void saveToStorage() throws IOException{
            this.taskStorage.writeToFile(allTasksInStorage);
    }
    //@author A0105572L
    public ArrayList<Task> getAllTasks() throws Exception {
        allTasksInStorage = taskStorage.getAllTasks();
        return allTasksInStorage; 
    }
    
    /*
     * Following methods update the storeAllTasks and save it to the storage file each time it is executed.
     */
    public void addTask(Task task) throws IOException {
    	clearPastCmds();
        allTasksInStorage.add(task);
        taskStorage.recordNewTask(task);
     // System.out.println("history Index = " + historyIndex);
    }
    
    //@author A0105572L
    public boolean removeTask(Task task) throws Exception {
    	clearPastCmds();
        boolean result = false;
        getAllTasks();
        if (allTasksInStorage.remove(task)){
            result = true;
            saveToStorage();
        }
        return result;
    }
    //@author A0105572L
    public String completeTask(Task task) throws IOException {
    	clearPastCmds();
        if (task.isCompleted()) {
            return String.format(TASK_ALREADY_COMPLETED,task.toString());
        } else {
            allTasksInStorage.remove(task);
            task.markAsCompleted();
            allTasksInStorage.add(task);
            saveToStorage();
            return String.format(TASK_COMPLETED, task.toString());
        }
    }
    
    public String uncompleteTask(Task task) throws IOException {
    	clearPastCmds();
        if (!task.isCompleted()) {
            return String.format(TASK_NOT_COMPLETED_YET, task.toString());
        } else {
            allTasksInStorage.remove(task);
            task.markAsIncompleted();
            allTasksInStorage.add(task);
            saveToStorage();
            return String.format(TASK_UNCOMPLETED,task.toString());
        }
    }
    
    public void incompleteTask(Task task) throws IOException {
    	clearPastCmds();
    	for (Task current: allTasksInStorage) {
    		if (task.equals(current)){
		        if (!task.isCompleted()) {
		            System.out.println( "Task " +
		                   task.toString() +
		                   " is currently incomplete.");
		        } else {
		            allTasksInStorage.remove(task);
		            task.markAsIncompleted();
		            allTasksInStorage.add(task);
		            saveToStorage();
		            System.out.println(  "Incompleted Task: " + task.toString());
		        }
	    	}
    	}
    }
    
    //@author A0105572L
    public void editTask(Task old_task, Task new_task) throws Exception {
    	if (old_task.toString().equals(new_task.toString())){
    	    return;
    	}else{
    	    clearPastCmds();
    	    getAllTasks();
    	    allTasksInStorage.remove(old_task);
    	    allTasksInStorage.add(new_task);
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

    public boolean undoLastCommand() throws Exception{
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
    
    public boolean redoUndoCommand() throws Exception{
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
	
	//@author A0105572L
    public void sortTimedTask(ArrayList<TimedTask> timedTasks){
        Collections.sort(timedTasks);
    }
    
    //@author A0105572L
    public void sortDeadlineTask(ArrayList<DeadlineTask> deadlineTasks){
        Collections.sort(deadlineTasks);
    }
    
}
