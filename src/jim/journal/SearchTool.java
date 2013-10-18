package jim.journal;

import java.util.ArrayList;

import org.joda.time.MutableDateTime;


public class SearchTool {
    ArrayList<Task> AllTasks = new ArrayList<Task>();
    JournalManager JManager;
    
    public SearchTool(JournalManager journal){
        JManager = journal;
        
            AllTasks = JManager.getAllTasks();
        
    }
   
    
    /*
     * SearchTool methods which are supposed to be used by Command class and SuggestionManager.
     * 
     * TODO: Welcome to add new search method when needed.
     * 
     */
    public ArrayList<Task> searchByDescription(String des){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        for (Task task : AllTasks) {
            if (task.getDescription().equals(des)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    
    //return task whoever has a date that matches the given date.
    public ArrayList<Task> searchByDate(MutableDateTime date){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks();
        ArrayList<DeadlineTask> AllDeadlineTasks = this.getAllDeadlineTasks();
        
        for (TimedTask task : AllTimedTasks){
            if (task.getStartTime().equals(date)||task.getEndTime().equals(date)){
                matchingTasks.add(task);
            }
        }
        for (DeadlineTask task : AllDeadlineTasks){
            if (task.getEndDate().equals(date)){
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    
    //return task whose start date match the given date
    public ArrayList<Task> searchByStartDate(MutableDateTime date){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks();
        

        for (TimedTask task : AllTimedTasks){
            if (task.getStartTime().equals(date)){
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    
    //return these tasks whose end date match the given date.
    public ArrayList<Task> searchByEndDate(MutableDateTime date){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks();
        ArrayList<DeadlineTask> AllDeadlineTasks = this.getAllDeadlineTasks();

        for (TimedTask task : AllTimedTasks){
            if (task.getEndTime().equals(date)){
                matchingTasks.add(task);
            }
        }
        for (DeadlineTask task : AllDeadlineTasks){
            if (task.getEndDate().equals(date)){
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    
    //return all the tasks that contains the key word.
    public ArrayList<Task> searchByNonStrictDescription(String KeyWord){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        for (Task task : AllTasks) {
            if (task.getDescription().contains(KeyWord)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    
    public ArrayList<Task> searchCompletedTask(){
        return this.getcompletedTasks();
    }
    public ArrayList<Task> searchUncompletedTask(){
        return this.getuncompletedTasks();
    }
    public ArrayList<FloatingTask> searchFloatingTask(){
        return this.getAllFloatingTasks();
    }
    
    
    
    
    
    
    
    /*
     * Second level methods all private
     */
    private ArrayList<TimedTask> getAllTimedTasks(){
        ArrayList<TimedTask> AllTimedTask = new ArrayList<TimedTask>();
        for (Task task : AllTasks){
            if (task instanceof TimedTask){
                AllTimedTask.add((TimedTask)task);
            }
        }
        return AllTimedTask;
    }
    private ArrayList<FloatingTask> getAllFloatingTasks(){
        ArrayList<FloatingTask> AllFloatingTask = new ArrayList<FloatingTask>();
        for (Task task : AllTasks){
            if (task instanceof FloatingTask){
                AllFloatingTask.add((FloatingTask)task);
            }
        }
        return AllFloatingTask;
    }
    private ArrayList<DeadlineTask> getAllDeadlineTasks(){
        ArrayList<DeadlineTask> AllDeadlineTask = new ArrayList<DeadlineTask>();
        for (Task task : AllTasks){
            if (task instanceof TimedTask){
                AllDeadlineTask.add((DeadlineTask)task);
            }
        }
        return AllDeadlineTask;
        
    }
    private ArrayList<Task> getuncompletedTasks() {
        ArrayList<Task> uncompletedTasks = new ArrayList<Task>();
        for (Task t : AllTasks) {
            if (!t.isCompleted()) {
                uncompletedTasks.add(t);
            }
        }
        return uncompletedTasks;
    }
    private ArrayList<Task> getcompletedTasks() {
        ArrayList<Task> completedTasks = new ArrayList<Task>();
        for (Task t : AllTasks) {
            if (t.isCompleted()) {
                completedTasks.add(t);
            }
        }
        return completedTasks;
    }

    
}