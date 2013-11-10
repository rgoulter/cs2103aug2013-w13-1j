package jim.journal;
import jim.Configuration;

import org.joda.time.DateTimeComparator;
import org.joda.time.MutableDateTime;


public class DeadlineTask extends Task implements Comparable<DeadlineTask>{

    private MutableDateTime endDate;

    private static Configuration configManager = Configuration.getConfiguration();
    private static final String DATE_SEPARATOR = configManager.getDateSeparator();
    private static final String TIME_SEPARATOR = configManager.getTimeSeparator();

    public DeadlineTask(MutableDateTime endDate, String desc) {
        this.endDate = endDate;
        this.description = desc;
    }
    
    //For taking task from the storage file
    //@author A0105572L
    public DeadlineTask(String endDate, String desc){
        this.endDate = MutableDateTime.parse(endDate);
        this.description = desc;
    }
    
    public MutableDateTime getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
       
        String taskNameDeadline = "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d] " + 
                                  "[%02d" + TIME_SEPARATOR + "%02d] %s";
    	return String.format(taskNameDeadline, endDate.getDayOfMonth(), endDate.getMonthOfYear() , endDate.getYear(),endDate.getHourOfDay(), endDate.getMinuteOfHour(),getDescription());
    }
    
    //@author A0105572L
    public String toStringForEditCommand() {
        String taskNameDeadline = "%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d %s";
        return String.format(taskNameDeadline, endDate.getDayOfMonth(), endDate.getMonthOfYear() , endDate.getYear()-2000,getDescription());
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof DeadlineTask) {
            DeadlineTask helper = (DeadlineTask) o;
            if ((this.endDate.equals(helper.getEndDate())) &&
                (this.description.equalsIgnoreCase(helper.getDescription()))) {
                return true;
            }
        }
        return false;
    }



    @Override
    public int hashCode() {
        return endDate.hashCode() *
               31 + description.hashCode();
    }
    @Override
    //@author A0105572L
    public int compareTo(DeadlineTask arg0) {
        return DateTimeComparator.getInstance().compare(this.getEndDate(), arg0.getEndDate());
    }
}
