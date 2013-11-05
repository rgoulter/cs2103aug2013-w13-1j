package jim.journal;

import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTimeComparator;
import org.joda.time.MutableDateTime;


public class SearchTool {
    ArrayList<Task> AllTasks = new ArrayList<Task>();
    JournalManager JManager;
    SearchTool theOne;
    public SearchTool(JournalManager journal){
        JManager = journal;
        
        AllTasks = JManager.getAllTasks();
        
    }
   public void setJournalManager(JournalManager JM){
       this.JManager = JM;
   }
    
    /*
     * SearchTool methods which are supposed to be used by Command class and SuggestionManager.
     * 
     * TODO: Welcome to add new search method when needed.
     * 
     */
 //return all the tasks that contains the key word.
   public ArrayList<Task> searchByNonStrictDescription(String KeyWord){
       ArrayList<Task> matchingTasks = new ArrayList<Task>();
       for (Task task : AllTasks) {  
           if (task.getDescription().toLowerCase().contains(KeyWord)) {
               matchingTasks.add(task);
           }
       }
       return matchingTasks;
   }
   public ArrayList<Task> searchByNonStrictDescription(String KeyWord, ArrayList<Task> tasks){
       ArrayList<Task> matchingTasks = new ArrayList<Task>();
       for (Task task : tasks) {  
           if (task.getDescription().contains(KeyWord)) {
               matchingTasks.add(task);
           }
       }
       return matchingTasks;
   }
   
   
    public ArrayList<Task> searchByDescription(String des){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        for (Task task : AllTasks) {
            if (task.getDescription().equals(des)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    public ArrayList<Task> searchByDescription(String des, ArrayList<Task> tasks){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        for (Task task : tasks) {
            if (task.getDescription().equals(des)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
    //return task whoever has a date that matches the given date.
    public Task  compareDate(MutableDateTime taskTime, Task current, MutableDateTime dateLimit) {
        if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, dateLimit) == 0) {
            return current;
        }else{
            return null;
        }
    }
    
    public ArrayList<Task> searchByDate(MutableDateTime dateLimit){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        for (Task current : AllTasks) {
                if (current instanceof TimedTask) {
                    MutableDateTime taskTime =((TimedTask) current).getStartTime();
                    Task t = compareDate(taskTime, current, dateLimit);
                    if (t != null){
                        matchingTasks.add(t);
                    }
                } else if (current instanceof DeadlineTask){ 
                    MutableDateTime taskTime =((DeadlineTask) current).getEndDate();
                    Task t = compareDate(taskTime, current, dateLimit);
                    if (t != null){
                        matchingTasks.add(t);
                    }
                }
        }
        return matchingTasks;
    }
    public ArrayList<Task> searchByDate(MutableDateTime dateLimit, ArrayList<Task> tasks){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        for (Task current : tasks) {
                if (current instanceof TimedTask) {
                    MutableDateTime taskTime =((TimedTask) current).getStartTime();
                    Task t = compareDate(taskTime, current, dateLimit);
                    if (t != null){
                        matchingTasks.add(t);
                    }
                } else if (current instanceof DeadlineTask){ 
                    MutableDateTime taskTime =((DeadlineTask) current).getEndDate();
                    Task t = compareDate(taskTime, current, dateLimit);
                    if (t != null){
                        matchingTasks.add(t);
                    }
                }
        }
        return matchingTasks;
    }
    
    //return task whose start date match the given date
    public ArrayList<Task> searchByStartDate(MutableDateTime date){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks(AllTasks);
        for (TimedTask current : AllTimedTasks){
            MutableDateTime taskTime =((TimedTask) current).getStartTime();
            Task t = compareDate(taskTime, current, date);
            if (t != null){
                matchingTasks.add(t);
            }
        }
        return matchingTasks;
    }
    
    public ArrayList<Task> searchByStartDate(MutableDateTime date, ArrayList<Task> tasks){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks(tasks);
        for (TimedTask current : AllTimedTasks){
            MutableDateTime taskTime =((TimedTask) current).getStartTime();
            Task t = compareDate(taskTime, current, date);
            if (t != null){
                matchingTasks.add(t);
            }
        }
        return matchingTasks;
    }
    
    //return these tasks whose end date match the given date.
    public ArrayList<Task> searchByEndDate(MutableDateTime date){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks(AllTasks);
        ArrayList<DeadlineTask> AllDeadlineTasks = this.getAllDeadlineTasks(AllTasks);

        for (TimedTask current : AllTimedTasks){
            MutableDateTime taskTime =((TimedTask) current).getEndTime();
            Task t = compareDate(taskTime, current, date);
            if (t != null){
                matchingTasks.add(t);
            }
        }
        for (DeadlineTask current : AllDeadlineTasks){
            MutableDateTime taskTime =((DeadlineTask) current).getEndDate();
            Task t = compareDate(taskTime, current, date);
            if (t != null){
                matchingTasks.add(t);
            }
        }
        return matchingTasks;
    }
    public ArrayList<Task> searchByEndDate(MutableDateTime date, ArrayList<Task> tasks){
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        ArrayList<TimedTask> AllTimedTasks = this.getAllTimedTasks(tasks);
        ArrayList<DeadlineTask> AllDeadlineTasks = this.getAllDeadlineTasks(tasks);

        for (TimedTask current : AllTimedTasks){
            MutableDateTime taskTime =((TimedTask) current).getEndTime();
            Task t = compareDate(taskTime, current, date);
            if (t != null){
                matchingTasks.add(t);
            }
        }
        for (DeadlineTask current : AllDeadlineTasks){
            MutableDateTime taskTime =((DeadlineTask) current).getEndDate();
            Task t = compareDate(taskTime, current, date);
            if (t != null){
                matchingTasks.add(t);
            }
        }
        return matchingTasks;
        
    }
    
    public ArrayList<Task> getAllTasks(){
        return AllTasks;
    }
    
    
    public ArrayList<TimedTask> getAllTimedTasks(ArrayList<Task> tasks){
        ArrayList<TimedTask> AllTimedTask = new ArrayList<TimedTask>();
        for (Task task : tasks){
            if (task instanceof TimedTask){
                AllTimedTask.add((TimedTask)task);
            }
        }
        return AllTimedTask;
    }
    public ArrayList<FloatingTask> getAllFloatingTasks(ArrayList<Task> tasks){
        ArrayList<FloatingTask> AllFloatingTask = new ArrayList<FloatingTask>();
        for (Task task : tasks){
            if (task instanceof FloatingTask){
                AllFloatingTask.add((FloatingTask)task);
            }
        }
        return AllFloatingTask;
    }
    public ArrayList<DeadlineTask> getAllDeadlineTasks(ArrayList<Task> tasks){
        ArrayList<DeadlineTask> AllDeadlineTask = new ArrayList<DeadlineTask>();
        for (Task task : tasks){
            if (task instanceof DeadlineTask){
                AllDeadlineTask.add((DeadlineTask)task);
            }
        }
        return AllDeadlineTask;
        
    }
    public ArrayList<Task> getuncompletedTasks(ArrayList<Task> tasks) {
        ArrayList<Task> uncompletedTasks = new ArrayList<Task>();
        for (Task t : tasks) {
            if (!t.isCompleted()) {
                uncompletedTasks.add(t);
            }
        }
        return uncompletedTasks;
    }
    public ArrayList<Task> getcompletedTasks(ArrayList<Task> tasks) {
        ArrayList<Task> completedTasks = new ArrayList<Task>();
        for (Task t : tasks) {
            if (t.isCompleted()) {
                completedTasks.add(t);
            }
        }
        return completedTasks;
    }
    public ArrayList<TimedTask> sortTimedTasks(ArrayList<TimedTask> tasks){
        Collections.sort(tasks);
        return tasks;
    }
    public ArrayList<DeadlineTask> sortDeadlineTasks(ArrayList<DeadlineTask> tasks){
        Collections.sort(tasks);
        return tasks;
    }
    public ArrayList<FloatingTask> sortFloatingTasks(ArrayList<FloatingTask> tasks){
        Collections.sort(tasks);
        return tasks;
    }
}
