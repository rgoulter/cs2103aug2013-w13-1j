//@author A0097081B
package jim.journal;
import jim.Configuration;

import org.joda.time.DateTimeComparator;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.ISOChronology;

public class TimedTask extends Task implements Comparable<TimedTask>{

    private MutableDateTime startTime;
    private MutableDateTime endTime;
    
    private static Configuration configManager = Configuration.getConfiguration();
    private static final String DATE_SEPARATOR = configManager.getDateSeparator();
    private static final String TIME_SEPARATOR = configManager.getTimeSeparator();
    private static final String DATE_TIME_OF_TASKS_WITH_START_END_TIME = "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d]" +
            															 " [%02d" + TIME_SEPARATOR + "%02d]"+  " - " +
            															 "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d]" +
            															 " [%02d" + TIME_SEPARATOR + "%02d]" + " %s";
    private static final String DATE_TIME_OF_TASKS_WITHOUT_START_TIME = "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d]" +
            														  " [%02d" + TIME_SEPARATOR + "%02d] %s";
    
    private static final int CURRENT_MIILLENIUM = 2000;
    public TimedTask(MutableDateTime startTime, MutableDateTime endTime, String desc) {
        // Tasks with both start and end date and time.
        this.startTime = startTime;
        this.endTime = endTime;
        description = desc;
    }
    
    //For taking task from storage.
    //@author A0105572L
    public TimedTask(String startTime, String endTime, String desc) {
        this.startTime = MutableDateTime.parse(startTime);
        this.endTime = MutableDateTime.parse(endTime);
        description = desc;
    }
    
    
    public TimedTask(MutableDateTime date, String desc) {
        // It's unclear how to handle the difference between
        // only one datetime, and having two datetimes.
        
        this.startTime = null;
        this.endTime = date;
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
    	
    	if (this.startTime == null) {
        	return String.format(DATE_TIME_OF_TASKS_WITHOUT_START_TIME, 
        						 endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear() - CURRENT_MIILLENIUM,
        						 endTime.getHourOfDay(), endTime.getMinuteOfHour(), getDescription());
    	} else {
        	return String.format(DATE_TIME_OF_TASKS_WITH_START_END_TIME, 
        						startTime.getDayOfMonth(), startTime.getMonthOfYear(), startTime.getYear(),
			        			startTime.getHourOfDay(), startTime.getMinuteOfHour(),
			        			endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear() - CURRENT_MIILLENIUM,	
								endTime.getHourOfDay(), endTime.getMinuteOfHour(),
								getDescription());
    	}
    }
    
    //@author A0105572L
    public String toStringForEditCommand(){
        String taskName = "%02d/%02d/%02d %02d:%02d to %02d:%02d %s";
        return String.format(taskName, endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear() - CURRENT_MIILLENIUM,
                    startTime.getHourOfDay(), startTime.getMinuteOfHour(),
                    endTime.getHourOfDay(), endTime.getMinuteOfHour(),
                    getDescription());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TimedTask) {
            TimedTask helper = (TimedTask) o;
            
            this.startTime.setChronology(ISOChronology.getInstance());
            this.endTime.setChronology(ISOChronology.getInstance());
            helper.getStartTime().setChronology(ISOChronology.getInstance());
            helper.getEndTime().setChronology(ISOChronology.getInstance());
            
            if ((this.startTime.equals(helper.getStartTime())) &&
                (this.endTime.equals(helper.getEndTime())) &&
                (this.description.equalsIgnoreCase(helper.getDescription()))) {
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
