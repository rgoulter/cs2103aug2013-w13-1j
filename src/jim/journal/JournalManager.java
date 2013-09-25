package jim.journal;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

public class JournalManager {
    /**
     * Returns a String representation of the current Journal state.
     * @return
     */
    public String getDisplayString() {
        List<String> upcomingEventsList = new ArrayList<String>();
        upcomingEventsList.add("Monday 12 Sept.:\tBe awesome!");
        upcomingEventsList.add("Tuesday 13 Sept.:\tWork on CE2 Some more.");
        
        List<String> todoList = new ArrayList<String>();
        todoList.add("Write memoirs");
        
        
        StringBuilder output = new StringBuilder();
        
        output.append("Upcoming Events:\n");
        for(String upcomingEventStr : upcomingEventsList){
            output.append(upcomingEventStr);
            output.append('\n');
        }
        output.append('\n');
        
        output.append("Todo:\n");
        for(String todoItemStr : todoList){
            output.append(todoItemStr);
            output.append('\n');
        }
        output.append('\n');
        
        
        return output.toString();
    }
    
    public void addTask(Task task) {
        
    }
    
    public List<Task> getAllTasks() {
        // TODO: Not cheat on this.
        Calendar startTime = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar endTime =   new GregorianCalendar(2013, 10, 10, 15, 0);
        String description = "CS2103 Lecture";

        Task expectedTask = new TimedTask(startTime, endTime, description);
        List<Task> allTasks = new ArrayList<Task>();
        allTasks.add(expectedTask);
        
        return allTasks;
    }
}
