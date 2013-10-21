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
    
    public void compareDate(MutableDateTime taskTime, Task current) {
    	if (DateTimeComparator.getDateOnlyInstance().compare(taskTime, dateLimit) == 0) {
    		outputln(current.toString());
    	}
    }
    

    @Override
    public String execute(JournalManager journalManager) {
        List<Task> allTasks = journalManager.getAllTasks();

        for (Task current : allTasks) {
            if (dateLimit == null) {
                outputln(current.toString());
            } else {
                if (current instanceof TimedTask) {
                	MutableDateTime taskTime =((TimedTask) current).getStartTime();

                    // Workaround to check if two events are on the same day,
                    // ignoring time
                	compareDate(taskTime, current);
                } else if (current instanceof DeadlineTask){ 
                	MutableDateTime taskTime =((DeadlineTask) current).getEndDate();
                	compareDate(taskTime, current);
                }
            }

        }
        return "Success";
    }



    @Override
    public String secondExecute(String secondInput) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString() {
        return "Display";
    }

}
