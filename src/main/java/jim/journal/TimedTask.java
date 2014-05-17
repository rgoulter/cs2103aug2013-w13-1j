//@author A0097081B
package jim.journal;
import jim.Configuration;

import org.joda.time.DateTimeComparator;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.ISOChronology;

public class TimedTask extends Task implements Comparable<TimedTask>{

    private static Configuration configManager = Configuration.getConfiguration();
    private static final int DEFAULT_TIME_DURATION = 2;
    private static final String DATE_SEPARATOR = configManager.getDateSeparator();
    private static final String TIME_SEPARATOR = configManager.getTimeSeparator();
    private static final String DATE_TIME_OF_TASKS = "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d]" +
            										 " [%02d" + TIME_SEPARATOR + "%02d]"+  " - " +
            										 "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d]" +
            										 " [%02d" + TIME_SEPARATOR + "%02d]" + " %s"; 
    private static final String DATE_TIME_OF_TASKS_FOR_EDIT_COMMAND = "%02d/%02d/%02d %02d:%02d %02d/%02d/%02d %02d:%02d %s";
    private static final int ZERO_FOR_COMPARE = 0;
    private static final int CURRENT_MIILLENIUM = 2000;
    private MutableDateTime startTime;
    private MutableDateTime endTime;
    
    //@author A0105572L
    public TimedTask(MutableDateTime startTime, MutableDateTime endTime, String desc) {
        //To make sure that the start time is earlier than the end time. 
        //If they are equal, then there is default duration 2 hours.
        if (DateTimeComparator.getInstance().compare(startTime, endTime) > ZERO_FOR_COMPARE){
            this.startTime = endTime;
            this.endTime = startTime;
        }else if (DateTimeComparator.getInstance().compare(startTime, endTime) == ZERO_FOR_COMPARE){
            this.startTime = startTime;
            endTime.addHours(DEFAULT_TIME_DURATION);
            this.endTime = endTime;
        }else{
            this.startTime = startTime;
            this.endTime = endTime;
        }
        description = desc;
    }
    
    //For taking task from storage.
    //@author A0105572L
    public TimedTask(String startTime, String endTime, String desc) {
        //we don't have to compare the startTime and endTime here, 
        //since it should be in the correct order when we add in the task.
        this.startTime = MutableDateTime.parse(startTime);
        this.endTime = MutableDateTime.parse(endTime);
        description = desc;
    }
    
    public MutableDateTime getStartTime() {
        return startTime;
    }

    public MutableDateTime getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        	return String.format(DATE_TIME_OF_TASKS, 
        						startTime.getDayOfMonth(), startTime.getMonthOfYear(), startTime.getYear() - CURRENT_MIILLENIUM,
			        			startTime.getHourOfDay(), startTime.getMinuteOfHour(),
			        			endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear() - CURRENT_MIILLENIUM,	
								endTime.getHourOfDay(), endTime.getMinuteOfHour(),
								getDescription());
    }
    //@author A0105572L
    public String toStringForEditCommand(){
        return String.format(DATE_TIME_OF_TASKS_FOR_EDIT_COMMAND, startTime.getDayOfMonth(), startTime.getMonthOfYear() , startTime.getYear() - CURRENT_MIILLENIUM,
                    startTime.getHourOfDay(), startTime.getMinuteOfHour(),
                    endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear() - CURRENT_MIILLENIUM,
                    endTime.getHourOfDay(), endTime.getMinuteOfHour(),
                    getDescription());
    }

    @Override
    public boolean equals(Object oneObject) {
        if (oneObject instanceof TimedTask) {
            TimedTask taskToBeComparedWith = (TimedTask) oneObject;
            this.startTime.setChronology(ISOChronology.getInstance());
            this.endTime.setChronology(ISOChronology.getInstance());
            taskToBeComparedWith.getStartTime().setChronology(ISOChronology.getInstance());
            taskToBeComparedWith.getEndTime().setChronology(ISOChronology.getInstance());
            
            if ((this.startTime.equals(taskToBeComparedWith.getStartTime())) &&
                (this.endTime.equals(taskToBeComparedWith.getEndTime())) &&
                (this.description.equalsIgnoreCase(taskToBeComparedWith.getDescription()))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return startTime.hashCode() *
               31 +
               endTime.hashCode() *
               13 +
               description.hashCode();
    }

    @Override
    //@author A0105572L
    public int compareTo(TimedTask arg0) {
        return DateTimeComparator.getInstance().compare(this.getStartTime(), arg0.getStartTime());
    }
    
    
}
