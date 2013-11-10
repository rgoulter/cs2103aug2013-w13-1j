//@author A0097081B
package jim.journal;

import java.io.IOException;

import org.joda.time.MutableDateTime;

public class AddCommand extends Command {

    private static final String FILE_ERROR = "FILE ERROR";
    private Task taskToAdd;
    /**
     * Adds a Task with specified start date+time, end date+time, and
     * description.
     * 
     * We represent date+time with Joda Time MutableDateTime Class
     */
    public AddCommand(MutableDateTime startTime, MutableDateTime endTime, String description) {
        taskToAdd = new TimedTask(startTime, endTime, description);
    }

    public AddCommand(MutableDateTime endDate, String description) {
        taskToAdd = new DeadlineTask(endDate, description);
    }

    public AddCommand(String description) {
        taskToAdd = new FloatingTask(description);
    }

    public AddCommand(Task task) {
        taskToAdd = task;
    }
    
    // TODO: If we have more constructors, may be easier to process other
    // command formats.
    // e.g. add <description> (for a floating task kindof thing).

    @Override
    public String execute(JournalManager journalManager) {
        try {
            journalManager.addTask(taskToAdd);
        } catch (IOException e) {
            outputln(FILE_ERROR);
            return "Failure";
        }
        journalManager.addCommandHistory("add", taskToAdd);
        return "Success";     
    }

    @Override
    public String secondExecute(String secondInput) {
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        return null;
    }
    
    @Override
    public String toString() {
        return "add";
    }

}
