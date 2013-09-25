package jim.journal;

import java.util.Calendar;

public class AddCommand implements Command {
    
    /**
     * Adds a Task with specified start date+time, end date+time, and description.
     * 
     * We represent date+time with the java.util.Calendar class.
     */
    public AddCommand(Calendar startTime, Calendar endTime, String description) {
        // TODO: Any kindof logic here.
    }
    
    // TODO: If we have more constructors, may be easier to process other command formats.
    // e.g. add <description> (for a floating task kindof thing).

    @Override
    public void execute(JournalManager journalManager) {
        // Execute the add command, feeding the data to the given journalManager.
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
