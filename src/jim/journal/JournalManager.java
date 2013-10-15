
package jim.journal;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;


public class JournalManager {
    private ArrayList<Task> storeAllTasks = new ArrayList<Task>();
    TaskStorage taskStorage = new TaskStorage("taskstorage.txt");
    private Stack<Command> ExecutedCommand = new Stack<Command>();

    /**
     * Returns a String representation of the current Journal state.
     * 
     * @return
     */
    /*  This method is to reduce the time of read through the storage file every time when want to change the content.
     *  However, if we choose to use this method, the storeAllTasks will only be written to file when exit.
     */
   
    public void initializeJournalManager(){
        try {
            storeAllTasks = taskStorage.getAllTasks();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public boolean compareDate(MutableDateTime taskTime, MutableDateTime current) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, current) == 0) {
    		return true;
    	} else return false;
    }
    
    public String getDisplayString() {
        //List<Task> upcomingTasks = storeAllTasks;
        List<Task> upcomingTasks = this.getAllTasks();
        String timedTasks = "", floatingTasks = "", deadlineTasks = "";
        String output = "Upcoming Events:\n";

        MutableDateTime today = new MutableDateTime();

        for (Task current : upcomingTasks) {
            if (current instanceof TimedTask) {
            	MutableDateTime taskTime = ((TimedTask) current).getStartTime();    
                if (compareDate(taskTime, today)) {
     				   timedTasks = timedTasks + current.toString() + "\n";
     			   }
            } else if (current instanceof FloatingTask) {
                floatingTasks = floatingTasks + current.toString() + "\n";
            } else if (current instanceof DeadlineTask) {
            	MutableDateTime taskTime = ((DeadlineTask) current).getEndDate();    
                if (compareDate(taskTime, today)) {
                	deadlineTasks = deadlineTasks + current.toString() + "\n";
     			   }
            }

        }

        output = output + deadlineTasks + timedTasks + "\n\nTodo:\n" + floatingTasks;
        return output;
    }


    public void saveToStorage(){
        try {
            this.taskStorage.writeToFile(storeAllTasks);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<Task> getAllTasks() {
        try {
            storeAllTasks = taskStorage.getAllTasks();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return storeAllTasks; 
    }
    
    /*
     * Following methods update the storeAllTasks, uncompletedTasks,
     * completedTasks.
     */
    public void addTask(Task task) {

        storeAllTasks.add(task);
        try {
            taskStorage.recordNewTask(task);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public boolean removeTask(Task task) {
        boolean result = false;
        getAllTasks();
        if (storeAllTasks.remove(task)){
            result = true;
            saveToStorage();
        }
        return result;
    }



    public String completeTask(Task task) {
        if (task.isCompleted()) {
            return "Task " +
                   task.toString() +
                   " has already been marked as completed.";
        } else {
            
            task.markAsCompleted();
            saveToStorage();
            return "Completed Task: " + task.toString();
        }
    }



    public void editTask(Task old_task, Task new_task) {
        getAllTasks();
        storeAllTasks.remove(old_task);
        storeAllTasks.add(new_task);
        saveToStorage();
    }
    
    public Stack<Command> getPreviousCommand(){
        return ExecutedCommand;
    }
    //only certain command need to push.
    //add, edit, complete, remove.
    public void pushCommand(Command command){
        ExecutedCommand.push(command);
    }
    public void undoLastCommand(){
        Command LastCommand = ExecutedCommand.pop();
        //add, edit, remove, complete
        if (LastCommand instanceof AddCommand){
            
        }else if (LastCommand instanceof EditCommand){
            
        }else if (LastCommand instanceof RemoveCommand){
            
        }else if (LastCommand instanceof CompleteCommand){
            
        }else{
            //error
        }
        
        
        
    }



}
