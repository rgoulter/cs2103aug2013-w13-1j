package jim.journal;



import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.List;



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
    
    /*public void compareDate(MutableDateTime taskTime, Task current) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, date) == 0) {
    		outputln(current.toString());
    	}
    }*/
    

    @Override
    public String execute(JournalManager journalManager) {
/*        List<Task> allTasks = journalManager.getAllTasks();

        for (Task current : allTasks) {
            if (date == null) {
                
                if (current.isCompleted()) {
                    outputln("[DONE] " + current.toString());
                } else {
                    outputln(current.toString());
                }
            } else {
                if (current instanceof TimedTask) {
                	MutableDateTime taskTime =((TimedTask) current).getStartTime();

                    // Workaround to check if two events are on the same day,
                    // ignoring time
                	compareDate(taskTime, current);
                } else if (current instanceof DeadlineTask){ 
                	MutableDateTime taskTime =((DeadlineTask) current).getEndDate();
                	compareDate(taskTime, current);
                }
            }
            
        }
        System.out.println(this.getOutput());
        */
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
        System.out.println(this.getOutput());
        return "success";
        
    }
    public void generateUnCompletedTaskOutput(){
        System.out.println("generatingUncompletedTaskOutput");
        matchingTasks = searchTool.getuncompletedTasks(matchingTasks);
        //display floating, deadline, timed not done
        if (matchingTasks == null){
            System.out.println("matchingTasks is null");
        }
        ArrayList<TimedTask> TimedTasksToDisplay = searchTool.getAllTimedTasks(matchingTasks);
        ArrayList<DeadlineTask> DeadlineTasksToDisplay = searchTool.getAllDeadlineTasks(matchingTasks);
        ArrayList<FloatingTask> FloatingTasksToDisplay = searchTool.getAllFloatingTasks(matchingTasks);
        
        
        //sort
        TimedTasksToDisplay = searchTool.sortTimedTasks(TimedTasksToDisplay);
        DeadlineTasksToDisplay = searchTool.sortDeadlineTasks(DeadlineTasksToDisplay);
        FloatingTasksToDisplay = searchTool.sortFloatingTasks(FloatingTasksToDisplay);
        
        outputln("Not Completed Tasks: ");
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
    public void generateCompletedTaskOutput(){
        matchingTasks = searchTool.getcompletedTasks(matchingTasks);
        //display floating, deadline, timed not done
        
        ArrayList<TimedTask> TimedTasksToDisplay = searchTool.getAllTimedTasks(matchingTasks);
        ArrayList<DeadlineTask> DeadlineTasksToDisplay = searchTool.getAllDeadlineTasks(matchingTasks);
        ArrayList<FloatingTask> FloatingTasksToDisplay = searchTool.getAllFloatingTasks(matchingTasks);
        
        //sort
        TimedTasksToDisplay = searchTool.sortTimedTasks(TimedTasksToDisplay);
        DeadlineTasksToDisplay = searchTool.sortDeadlineTasks(DeadlineTasksToDisplay);
        FloatingTasksToDisplay = searchTool.sortFloatingTasks(FloatingTasksToDisplay);
        
        outputln("Completed Tasks: ");
        for (TimedTask current : TimedTasksToDisplay){
            outputln("[DONE] " + current.toString());
        }
        for (DeadlineTask current : DeadlineTasksToDisplay){
            outputln("[DONE] " + current.toString());
        }
        for (FloatingTask current : FloatingTasksToDisplay){
            outputln("[DONE] " + current.toString());
        }
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

}
