//@author A0105572L
package jim.journal;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;

public class DisplayCommand extends Command {
    //For system purpose
    private static final String COMMANDTYPE = "Display";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_FAILURE = "Failure";
    
    //For display to user
    private static final String INFO_NO_TASKS = "There are no tasks to display.";
    private static final String INFO_TASK_TITLE = "-------------------- Tasks ----------------------";
    private static final String INFO_COMPLETED_TASK_TITLE = "\n--------------- Completed Tasks -----------------";
    private static final String CATEGORY_TIMED_TASKS = "Timed Tasks: ";
    private static final String CATEGORY_DEADLINE_TASKS = "\nDeadline Tasks: ";
    private static final String CATEGORY_FLOATING_TASKS = "\nTo-Do Tasks: ";
    
    MutableDateTime date;
    JournalManager MyJournalManager;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    SearchTool searchTool;
    boolean uncompletedOnly = false;
    boolean completedOnly = false;
    
    //Constructor for display with a specific date
    public DisplayCommand(MutableDateTime d) {
        date = d;
    }
    
    //Constructor for display all the tasks
    public DisplayCommand() {
        this(null);
    }
    
    //Constructor for display Only completed or Only uncompleted tasks
    public DisplayCommand(MutableDateTime d, boolean uncomOnly){
        date = d;
        uncompletedOnly = uncomOnly;
        completedOnly = true && uncomOnly;
    }
    
    @Override
    public String execute(JournalManager journalManager) {
        MyJournalManager = journalManager;
        
        try {
            searchTool = new SearchTool(MyJournalManager);
        } catch (Exception e) {
            outputln(INFO_NO_TASKS);
            return EXECUTION_STATUS_FAILURE;
        }
        if (date != null){
            matchingTasks = searchTool.searchByDate(date);
            matchingTasks.addAll(searchTool.searchByDateWithinTimeFrame(date));
        } else {
            matchingTasks = searchTool.getAllTasks();
        }
        if (uncompletedOnly){
            this.generateUnCompletedTaskOutput();
        } else if (completedOnly){
            this.generateCompletedTaskOutput();
        } else {
            this.generateUnCompletedTaskOutput();
            this.generateCompletedTaskOutput();
        }
        return EXECUTION_STATUS_SUCCESS;        
    }
    
    @Override
    public String secondExecute(String secondInput) {
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        return null;
    }
    //@author A0097081B
    public String toString() {
        return COMMANDTYPE;
    }
    
    /*
     * Private functions for generating the out put of display command.
     */
    private void generateUnCompletedTaskOutput(){
        ArrayList<Task> unCompletedTasks = searchTool.getuncompletedTasks(matchingTasks);
        outputln(INFO_TASK_TITLE);
        this.generateTaskOutput(unCompletedTasks);
    }
    private void generateCompletedTaskOutput(){
        ArrayList<Task> completedTasks = searchTool.getcompletedTasks(matchingTasks);
        outputln(INFO_COMPLETED_TASK_TITLE);
        this.generateTaskOutput(completedTasks);
    }
    private void generateTaskOutput(ArrayList<Task> Tasks){
        
        ArrayList<TimedTask> TimedTasksToDisplay = searchTool.getAllTimedTasks(Tasks);
        ArrayList<DeadlineTask> DeadlineTasksToDisplay = searchTool.getAllDeadlineTasks(Tasks);
        ArrayList<FloatingTask> FloatingTasksToDisplay = searchTool.getAllFloatingTasks(Tasks);
        
        //sort the tasks in order of timed tasks, deadline tasks and Floating Tasks.
        TimedTasksToDisplay = searchTool.sortTimedTasks(TimedTasksToDisplay);
        DeadlineTasksToDisplay = searchTool.sortDeadlineTasks(DeadlineTasksToDisplay);
        FloatingTasksToDisplay = searchTool.sortFloatingTasks(FloatingTasksToDisplay);
        
        if (TimedTasksToDisplay.isEmpty() && DeadlineTasksToDisplay.isEmpty() &&
            FloatingTasksToDisplay.isEmpty()) {
            outputln(INFO_NO_TASKS);
        }
        else {
            outputln(CATEGORY_TIMED_TASKS);
            for (TimedTask current : TimedTasksToDisplay){
                outputln(current.toString());
            }
            outputln(CATEGORY_DEADLINE_TASKS);
            for (DeadlineTask current : DeadlineTasksToDisplay){
                outputln(current.toString());
            }
            outputln(CATEGORY_FLOATING_TASKS);
            for (FloatingTask current : FloatingTasksToDisplay){
                outputln(current.toString());
            }
        }
    }

}
