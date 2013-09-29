package jim.journal;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DisplayCommand extends Command {
	GregorianCalendar dateLimit;

	public DisplayCommand(GregorianCalendar date) {
		dateLimit = date;
	}
	
	public DisplayCommand() {
		this(null);
	}
	
    @Override
    public void execute(JournalManager journalManager) {
       List<Task> allTasks = journalManager.getAllTasks();
       
       for (Task current : allTasks) {
    	   if (dateLimit == null) { outputln(current.toString()); }
    	   else {
    		   if (current instanceof TimedTask) {
    			   GregorianCalendar taskTime = (GregorianCalendar)((TimedTask) current).getStartTime();
    			   
    			   // Workaround to check if two events are on the same day, ignoring time
    			   if (taskTime.get(Calendar.YEAR) == dateLimit.get(Calendar.YEAR) &&
    		           taskTime.get(Calendar.DAY_OF_YEAR) == dateLimit.get(Calendar.DAY_OF_YEAR)) {
    				   outputln(current.toString());
    			   }
    		   }
    	   }
    	   
       }
    }


}
