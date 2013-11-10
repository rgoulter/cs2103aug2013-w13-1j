//@author A0097081B, A0105572L
package jim.journal;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;

import jim.Configuration;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;

public class JournalManager {
	private static final int NO_COMMAND_EXECUTED_YET = -1;
	private static final int SAME_TIME = 0;
	
	private static final String DESCRIPTION_UPCOMING_TASKS = "Upcoming Events: \n";
	private static final String MESSAGE_DONE = "[DONE] ";
	private static final String DESCRIPTION_TODO = "\n\nTodo:\n";
	private static final String APPEND_TIMED_DEADLINE_TASK = "%s%s%s";
	private static final String APPEND_FLOATING_TASK_WITH_DONE = "%s%s%s%s";
	private static final String APPEND_FLOATING_TASK_WITHOUT_DONE = "%s%s%s";
    private static final String FILE_ERROR = "FILE ERROR";
	
	private static Configuration configManager = Configuration.getConfiguration();    
    private int historyIndex = NO_COMMAND_EXECUTED_YET; 
    
    private boolean newTrueCommand = true;
    
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    private ArrayList<Command_Task> historyOfCommand = new ArrayList<Command_Task>();
    private ArrayList<TimedTask> upcomingTimedTask;
    private ArrayList<DeadlineTask> upcomingDeadlineTask;
    private ArrayList<FloatingTask> upcomingFloatingTask;
    
    TaskStorage taskStorage = new TaskStorage(configManager.getOutputFileName());
    /**
     * Returns a String representation of the current Journal state.
     * 
     */
    
    public boolean compareDate(MutableDateTime taskTime, MutableDateTime current) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, current) == SAME_TIME) {
    		return true;
    	} else 
    		return false;
    }

    public void sortAllTasks() throws Exception {
    	ArrayList<Task> upcomingTasks = this.getAllTasks();
    	upcomingTimedTask = new ArrayList<TimedTask> ();
    	upcomingDeadlineTask = new ArrayList<DeadlineTask> ();
    	upcomingFloatingTask = new ArrayList<FloatingTask> ();
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
    }
    
    /* Note: Use sortAllTasks() before calling getTimedTaskString()*/
    public String getTimedTaskString() {
    	String timedTasks = "";
    	for (TimedTask task : upcomingTimedTask){
            timedTasks =  String.format(APPEND_TIMED_DEADLINE_TASK, timedTasks, task.toString(),"\n");
        }
    	return timedTasks;
    }
    
    /* Note: Use sortAllTasks() before calling getDeadlineTaskString()*/
    public String getDeadlineTaskString() {
    	String deadlineTasks = "";
    	for (DeadlineTask task : upcomingDeadlineTask){
    		deadlineTasks =  String.format(APPEND_TIMED_DEADLINE_TASK, deadlineTasks, task.toString(),"\n");
        }
    	return deadlineTasks;
    }
    
    /* Note: Use sortAllTasks() before calling getFloatingString()*/
    public String getFloatingString() {
    	String floatingTasks = "";
    	 for (FloatingTask task : upcomingFloatingTask){
             if (task.isCompleted()){
                 floatingTasks = String.format(APPEND_FLOATING_TASK_WITH_DONE, floatingTasks, MESSAGE_DONE, task.toString(),"\n");
             } else {
                 floatingTasks = String.format(APPEND_FLOATING_TASK_WITHOUT_DONE, task.toString(),"\n", floatingTasks);
             }
         }
    	return floatingTasks;
    }
    
    public String getDisplayString() {
    	try {
            sortAllTasks();
        } catch (Exception e) {
            return FILE_ERROR;
        }
        String output = DESCRIPTION_UPCOMING_TASKS + getDeadlineTaskString() + getTimedTaskString() + DESCRIPTION_TODO + getFloatingString();
        return output;
    }

    public void saveToStorage() throws IOException{
            this.taskStorage.writeToFile(storeAllTasks);
    }

    public ArrayList<Task> getAllTasks() throws Exception {
        storeAllTasks = taskStorage.getAllTasks();
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

    public boolean removeTask(Task task) throws Exception {
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

    public String completeTask(Task task) throws IOException {
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
    
    public String uncompleteTask(Task task) throws IOException {
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
    
    public void incompleteTask(Task task) throws IOException {
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

    public void editTask(Task old_task, Task new_task) throws Exception {
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
    public void sortTimedTask(ArrayList<TimedTask> timedTasks){
        Collections.sort(timedTasks);
    }
    public void sortDeadlineTask(ArrayList<DeadlineTask> deadlineTasks){
        Collections.sort(deadlineTasks);
    }
    public void sortFloatingTask(ArrayList<FloatingTask> floatingTasks){
    }
}
