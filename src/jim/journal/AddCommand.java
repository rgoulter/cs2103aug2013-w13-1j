package jim.journal;

import java.util.Calendar;

public class AddCommand extends Command {
    private Task taskToAdd;
    
    /**
     * Adds a Task with specified start date+time, end date+time, and description.
     * 
     * We represent date+time with the java.util.Calendar class.
     */
    public AddCommand(Calendar startTime, Calendar endTime, String description) {
        taskToAdd = new TimedTask(startTime, endTime, description);
    }
    
    public AddCommand(String description) {
    	taskToAdd = new FloatingTask(description);
    }
    
    // TODO: If we have more constructors, may be easier to process other command formats.
    // e.g. add <description> (for a floating task kindof thing).

    @Override
    public void execute(JournalManager journalManager) {
        journalManager.addTask(taskToAdd);
    }

    @Override
    public String addAnEvent(String anEvent) {
        return null;
    }

    @Override
    public String deleteAnEvent() {
        return null;
    }

}
