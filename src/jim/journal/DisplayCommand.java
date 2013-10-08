
package jim.journal;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeComparator;
import java.util.List;



public class DisplayCommand extends Command {

    MutableDateTime dateLimit;



    public DisplayCommand(MutableDateTime date) {
        dateLimit = date;
    }



    public DisplayCommand() {
        this(null);
    }



    @Override
    public void execute(JournalManager journalManager) {
        List<Task> allTasks = journalManager.getAllTasks();

        for (Task current : allTasks) {
            if (dateLimit == null) {
                outputln(current.toString());
            } else {
                if (current instanceof TimedTask) {
                	MutableDateTime taskTime =((TimedTask) current).getStartTime();

                    // Workaround to check if two events are on the same day,
                    // ignoring time
                	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, dateLimit) == 0
            				/*	   taskTime.year() == dateLimit.year() &&
            		           taskTime.dayOfYear() == dateLimit.dayOfYear()*/){
            				   outputln(current.toString());
                    }
                }
            }

        }
    }

}
