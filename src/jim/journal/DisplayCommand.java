//@author A0097081B, QW
package jim.journal;

import org.joda.time.MutableDateTime;
import java.util.ArrayList;

public class DisplayCommand extends Command {
	private static final String MESSAGE_SUCCESS = "Success";
	private static final String MESSAGE_DISPLAY = "Display";
	private static final String DIVIDER_TASKS = "-------------------- Tasks ----------------------";
	private static final String DIVIDER_COMPLETED_TASKS = "--------------- Completed Tasks -----------------";
	private static final String CATEGORY_TIMED_TASKS = "Timed Tasks: ";
	private static final String CATEGORY_DEADLINE_TASKS = "\nDeadline Tasks: ";
	private static final String CATEGORY_FLOATING_TASKS = "\nFloating Tasks: ";
	
    MutableDateTime date;
    JournalManager JM;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    SearchTool searchTool;

    public DisplayCommand(MutableDateTime d) {
        date = d;
    }
    
    public DisplayCommand() {
        this(null);
    }
    
    @Override
    public String execute(JournalManager journalManager) {
        JM = journalManager;
        boolean uncompletedOnly = false ;
        boolean completedOnly = false ;
        searchTool = new SearchTool(JM);
        if (date != null){
            matchingTasks = searchTool.searchByDate(date);
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
        
        return MESSAGE_SUCCESS;        
    }
    
    @Override
    public String secondExecute(String secondInput) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString() {
        return MESSAGE_DISPLAY;
    }
    
    private void generateUnCompletedTaskOutput(){
        ArrayList<Task> unCompletedTasks = searchTool.getuncompletedTasks(matchingTasks);
        outputln(DIVIDER_TASKS);
        this.generateTaskOutput(unCompletedTasks);
    }
    private void generateCompletedTaskOutput(){
        ArrayList<Task> completedTasks = searchTool.getcompletedTasks(matchingTasks);
        System.out.println();
        outputln(DIVIDER_COMPLETED_TASKS);
        this.generateTaskOutput(completedTasks);
    }
    private void generateTaskOutput(ArrayList<Task> Tasks){
        
        ArrayList<TimedTask> TimedTasksToDisplay = searchTool.getAllTimedTasks(Tasks);
        ArrayList<DeadlineTask> DeadlineTasksToDisplay = searchTool.getAllDeadlineTasks(Tasks);
        ArrayList<FloatingTask> FloatingTasksToDisplay = searchTool.getAllFloatingTasks(Tasks);
        
        //sort
        TimedTasksToDisplay = searchTool.sortTimedTasks(TimedTasksToDisplay);
        DeadlineTasksToDisplay = searchTool.sortDeadlineTasks(DeadlineTasksToDisplay);
        FloatingTasksToDisplay = searchTool.sortFloatingTasks(FloatingTasksToDisplay);
        
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
