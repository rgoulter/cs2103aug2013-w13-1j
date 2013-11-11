//@author A0105572L, A0097081B
package jim.journal;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import jim.journal.JournalManager.CommandTaskPair;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;

/*
 * This TemporaryJournalManager is just a rough copy of the JournalManager to aid the Unit Test
 * Because we don't want to involve the storage file when doing the unit test.
 */
public class TemporaryJournalManager extends JournalManager {
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
    private static final String FILE_ERROR = "FILE ERROR";
    private static final String TASK_ALREADY_COMPLETED = "Task %s has already been completed.";
    private static final String TASK_ALREADY_UNCOMPLETED = "Task %s has not been completed.";
    
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    private List<CommandTaskPair> historyOfCommand = new ArrayList<CommandTaskPair>();
	private static final int NO_COMMAND_EXECUTED_YET = -1;
    private int historyIndex = NO_COMMAND_EXECUTED_YET; 
    private boolean newTrueCommand = true;
    
    private ArrayList<TimedTask> upcomingTimedTask;
    private ArrayList<DeadlineTask> upcomingDeadlineTask;
    private ArrayList<FloatingTask> upcomingFloatingTask;

    /**
     * Returns a String representation of the current Journal state.
     */
    public void sortAllTasks() {
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
        sortAllTasks();
        String output = DESCRIPTION_UPCOMING_TASKS + getTimedTaskString() + getDeadlineTaskString() + DESCRIPTION_TODO + getFloatingString();
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
        	 return String.format(TASK_ALREADY_COMPLETED, task.toString());
        } else {
            task.markAsCompleted();
            return String.format(MESSAGE_COMPLETED_TASK, task.toString());
        }
    }
    
    public String uncompleteTask(Task task) throws IOException {
        if (!task.isCompleted()) {
            return String.format(TASK_ALREADY_UNCOMPLETED, task.toString());
        } else {
            task.markAsIncompleted();
            return String.format(MESSAGE_UNCOMPLETED_TASK, task.toString());
        }
    }

    public void editTask(Task old_task, Task new_task) {
        storeAllTasks.remove(old_task);
        storeAllTasks.add(new_task);
    }
    public void addCommandHistory(String cmd, Task someTask, Task editTask){
    	CommandTaskPair command = new CommandTaskPair(cmd, someTask, editTask);
    	historyIndex++;
    	historyOfCommand.add(historyIndex, command);
    }
    
    public void addCommandHistory(String cmd, Task someTask){
    	addCommandHistory(cmd, someTask, null);
    }
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
}
