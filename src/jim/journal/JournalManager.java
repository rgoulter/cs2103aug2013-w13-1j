package jim.journal;

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
}
