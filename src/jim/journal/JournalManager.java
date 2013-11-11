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
	private static final int CURRENT_TIME_WITHIN_TIMEFRAME = 0;
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_REMOVE = "remove";
	private static final String COMMAND_COMPLETE = "complete";
	private static final String COMMAND_UNCOMPLETE = "uncomplete";
	private static final String DESCRIPTION_UPCOMING_TASKS = "Upcoming Events: \n";
	private static final String MESSAGE_DONE = "[DONE] ";
	private static final String MESSAGE_COMPLETED_TASK = "Completed Task: %s";
	private static final String MESSAGE_UNCOMPLETED_TASK = "Uncompleted Task: %s";
	private static final String DESCRIPTION_TODO = "\n\nTodo:\n";
	private static final String APPEND_TIMED_DEADLINE_TASK = "%s%s%s";
	private static final String APPEND_FLOATING_TASK_WITH_DONE = "%s%s%s%s";
	private static final String APPEND_FLOATING_TASK_WITHOUT_DONE = "%s%s%s";
    private static final String FILE_ERROR = "Welcome to use JIM! \n\n Please add any tasks in the box above to create the storage file \n\n and keep this file with your JIM! together all the time. \n\n Enjoy JIM!";
    private static final String TASK_ALREADY_COMPLETED = "Task %s has already been completed.";
    private static final String TASK_ALREADY_UNCOMPLETED = "Task %s has not been completed.";
	
	private static Configuration configManager = Configuration.getConfiguration();    
    private int historyIndex = NO_COMMAND_EXECUTED_YET; 
    
    private boolean newTrueCommand = true;
    
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    private ArrayList<CommandTaskPair> historyOfCommand = new ArrayList<CommandTaskPair>();
    private ArrayList<TimedTask> upcomingTimedTask;
    private ArrayList<DeadlineTask> upcomingDeadlineTask;
    private ArrayList<FloatingTask> upcomingFloatingTask;
    
    TaskStorage taskStorage = new TaskStorage(configManager.getOutputFileName());
    /**
     * Returns a String representation of the current Journal state.
     * 
     */
    //@author A0105572L
    public boolean compareDate(MutableDateTime taskTime, MutableDateTime current) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, current) == SAME_TIME) {
    		return true;
    	} else 
    		return false;
    }
  //@author A0097081B
    public boolean checkWithInTimeFrame(MutableDateTime taskStartTime, MutableDateTime taskEndTime, MutableDateTime current) {
    	int currentTimeAfterStartDate = DateTimeComparator.getDateOnlyInstance().compare(taskStartTime,current);
    	int currentTimeBeforeEndDate = DateTimeComparator.getDateOnlyInstance().compare(current, taskEndTime);
    	if (currentTimeAfterStartDate < CURRENT_TIME_WITHIN_TIMEFRAME && currentTimeBeforeEndDate <= CURRENT_TIME_WITHIN_TIMEFRAME) {
    		return true;
    	} else 
    		return false;
    }
  //@author A0097081B
    public void sortAllTasks() throws Exception {
    	ArrayList<Task> upcomingTasks = this.getAllTasks();
    	upcomingTimedTask = new ArrayList<TimedTask> ();
    	upcomingDeadlineTask = new ArrayList<DeadlineTask> ();
    	upcomingFloatingTask = new ArrayList<FloatingTask> ();
        MutableDateTime today = new MutableDateTime();
        
        for (Task current : upcomingTasks) {
            if (current instanceof TimedTask) {
                MutableDateTime taskStartTime = ((TimedTask) current).getStartTime();  
                MutableDateTime taskEndTime = ((TimedTask) current).getEndTime();  
                if (compareDate(taskStartTime, today)) {
                       upcomingTimedTask.add((TimedTask)current);
                } else if (checkWithInTimeFrame(taskStartTime, taskEndTime, today)) {
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
  //@author A0097081B
    /* Note: Use sortAllTasks() before calling getTimedTaskString()*/
    public String getTimedTaskString() {
    	String timedTasks = "";
    	for (TimedTask task : upcomingTimedTask){
            timedTasks =  String.format(APPEND_TIMED_DEADLINE_TASK, timedTasks, task.toString(),"\n");
        }
    	return timedTasks;
    }
  //@author A0097081B
    /* Note: Use sortAllTasks() before calling getDeadlineTaskString()*/
    public String getDeadlineTaskString() {
    	String deadlineTasks = "";
    	for (DeadlineTask task : upcomingDeadlineTask){
    		deadlineTasks =  String.format(APPEND_TIMED_DEADLINE_TASK, deadlineTasks, task.toString(),"\n");
        }
    	return deadlineTasks;
    }
  //@author A0097081B
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
    
    //@author A0097081B
    public String getDisplayString() {
    	try {
            sortAllTasks();
        } catch (Exception e) {
            return FILE_ERROR;
        }
        String output = DESCRIPTION_UPCOMING_TASKS + getTimedTaskString() + getDeadlineTaskString() + DESCRIPTION_TODO + getFloatingString();
        return output;
    }
  //@author A0105572L
    public void saveToStorage() throws IOException{
            this.taskStorage.writeToFile(storeAllTasks);
    }
  //@author A0105572L
    public ArrayList<Task> getAllTasks() throws Exception {
        storeAllTasks = taskStorage.getAllTasks();
        return storeAllTasks; 
    }
    
    /*
     * Following methods update the storeAllTasks, uncompletedTasks,
     * completedTasks.
     */
    //@author A0105572L
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
    //@author A0105572L
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
    
    //@author A0105572L
    public String completeTask(Task task) throws IOException {
    	clearPastCmds();
        if (task.isCompleted()) {
            return String.format(TASK_ALREADY_COMPLETED, task.toString());
        } else {
            storeAllTasks.remove(task);
            task.markAsCompleted();
            storeAllTasks.add(task);
            saveToStorage();
            return String.format(MESSAGE_COMPLETED_TASK, task.toString());
        }
    }
    
    //@author A0097081B
    public String uncompleteTask(Task task) throws IOException {
    	clearPastCmds();
        if (!task.isCompleted()) {
            return String.format(TASK_ALREADY_UNCOMPLETED, task.toString());
        } else {
            storeAllTasks.remove(task);
            task.markAsIncompleted();
            storeAllTasks.add(task);
            saveToStorage();
            return String.format(MESSAGE_UNCOMPLETED_TASK, task.toString());
        }
    }
    
    //@author A0105572L
    public void editTask(Task oldTask, Task newTask) throws Exception {
    	if (oldTask.toString().equals(newTask.toString())){
    	    return;
    	} else {
    	    clearPastCmds();
    	    getAllTasks();
    	    storeAllTasks.remove(oldTask);
    	    storeAllTasks.add(newTask);
    	    saveToStorage();
    	}
    }
  //@author A0097081B
    public String getPreviousCommand(){
        return historyOfCommand.get(historyOfCommand.size() - 1).getCommand();
    }
    //add, edit, complete, remove.
  //@author A0097081B
    public void addCommandHistory(String cmd, Task someTask, Task editTask){
    	CommandTaskPair command = new CommandTaskPair(cmd, someTask, editTask);
    	historyIndex++;
    	historyOfCommand.add(historyIndex, command);
    }
  //@author A0097081B
    public void addCommandHistory(String cmd, Task someTask){
    	addCommandHistory(cmd, someTask, null);
    }
  //@author A0097081B
    public boolean undoLastCommand() throws Exception{
    	// get the last command in historyOfCommand
    	if (historyIndex > NO_COMMAND_EXECUTED_YET) {
        	newTrueCommand = false;
    		CommandTaskPair LastCommand = historyOfCommand.get(historyIndex--);
		    //add, edit, remove, complete
		    if (LastCommand.getCommand().equals(COMMAND_ADD)){
		    	removeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_EDIT)){
		    	editTask(LastCommand.getSomeTask(), LastCommand.getEditTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_REMOVE)){
		    	addTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_COMPLETE)){
		    	uncompleteTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_UNCOMPLETE)){
		    	completeTask(LastCommand.getSomeTask());
		    }
		    return true;
    	} else {
    		return false;
    	}
    }
  //@author A0097081B
    public boolean redoUndoCommand() throws Exception{
    	if (historyOfCommand.size() >= 0 && historyIndex < historyOfCommand.size() - 1) {
    		newTrueCommand = false;
    		CommandTaskPair LastCommand = historyOfCommand.get(++historyIndex);
		    //add, edit, remove, complete
		    if (LastCommand.getCommand().equals(COMMAND_ADD)){
		    	addTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_EDIT)){
		    	editTask(LastCommand.getEditTask(), LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_REMOVE)){
		    	removeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_COMPLETE)){
		    	completeTask(LastCommand.getSomeTask());
		    } else if (LastCommand.getCommand().equals(COMMAND_UNCOMPLETE)){
		    	uncompleteTask(LastCommand.getSomeTask());
		    }
		    return true;
    	} else {
    		return false;
    	}
    }
  //@author A0097081B
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
  //@author A0097081B
	class CommandTaskPair {
		String cmd;
		Task someTask, editTask;
		
		public CommandTaskPair(String cmd, Task someTask, Task editTask) {
			this.cmd = cmd;
			this.someTask = someTask;
			this.editTask = editTask;
		}
	
		public CommandTaskPair(String cmd, Task someTask) {
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
  //@author A0105572L
    public void sortFloatingTask(ArrayList<FloatingTask> floatingTasks){
    }
}
