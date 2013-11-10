//@author A0097081B
package jim.journal;

import org.joda.time.MutableDateTime;

public class AddCommand extends Command {
    private static final String COMMAND_ADD = "add";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";

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
        journalManager.addTask(taskToAdd);
        journalManager.addCommandHistory(COMMAND_ADD, taskToAdd);
        return EXECUTION_STATUS_SUCCESS;     
    }

    @Override
    public String secondExecute(String secondInput) {
        //assert(false);
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        //assert(false);
        return null;
    }
    
    @Override
    public String toString() {
        return COMMAND_ADD;
    }

}
