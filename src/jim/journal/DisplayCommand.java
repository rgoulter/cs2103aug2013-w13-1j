package jim.journal;



import org.joda.time.MutableDateTime;
import java.util.ArrayList;



public class DisplayCommand extends Command {

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
        boolean uncompletedOnly = false;
        boolean completedOnly = false;
        searchTool = new SearchTool(JM);
        if (date != null){
            matchingTasks = searchTool.searchByDate(date);
        }else{
            matchingTasks = searchTool.getAllTasks();
        }
        if (uncompletedOnly){
            this.generateUnCompletedTaskOutput();
        }else if (completedOnly){
            this.generateCompletedTaskOutput();
        }else{
            this.generateUnCompletedTaskOutput();
            this.generateCompletedTaskOutput();
        }
        return "Success";        
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
        return "Display";
    }
    
    private void generateUnCompletedTaskOutput(){
        ArrayList<Task> unCompletedTasks = searchTool.getuncompletedTasks(matchingTasks);
        outputln("-------------------- Tasks ----------------------");
        this.generateTaskOutput(unCompletedTasks);
    }
    private void generateCompletedTaskOutput(){
        ArrayList<Task> completedTasks = searchTool.getcompletedTasks(matchingTasks);
        outputln("\n--------------- Completed Tasks -----------------");
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
        
        for (TimedTask current : TimedTasksToDisplay){
            outputln(current.toString());
        }
        for (DeadlineTask current : DeadlineTasksToDisplay){
            outputln(current.toString());
        }
        for (FloatingTask current : FloatingTasksToDisplay){
            outputln(current.toString());
        }
    }

}
