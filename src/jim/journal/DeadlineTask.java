package jim.journal;
import jim.Configuration;

import org.joda.time.DateTimeComparator;
import org.joda.time.MutableDateTime;


public class DeadlineTask extends Task implements Comparable<DeadlineTask>{

    private MutableDateTime endDate;

    private static Configuration configManager = Configuration.getConfiguration();
    private static final String DATE_SEPARATOR = configManager.getDateSeparator();
    private static final String TIME_SEPARATOR = configManager.getTimeSeparator();
    private static final String TASK_NAME_DEADLINE = "[%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d] " + "[%02d" + TIME_SEPARATOR + "%02d] %s";
    private static final String EDIT_TASK_NAME_DEADLINE = "%02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d" + " %02d" + TIME_SEPARATOR + "%02d %s";
    private static final int CURRENT_MIILLENIUM = 2000;

    private static final int DEFAULT_HOUR = 23;

    private static final int DEFAULT_MIN = 59;
    
    //@author A0105572L
    public DeadlineTask(MutableDateTime endDate, String desc) {
        MutableDateTime currentDateTime = new MutableDateTime();
        if (DateTimeComparator.getInstance().compare(currentDateTime, endDate) > 0){
            endDate = currentDateTime;
            endDate.setHourOfDay(DEFAULT_HOUR);
            endDate.setMinuteOfHour(DEFAULT_MIN);
        }
        this.endDate = endDate;
        if ((endDate.getHourOfDay() == 0) && (endDate.getMinuteOfHour() == 0)){
            endDate.setHourOfDay(DEFAULT_HOUR);
            endDate.setMinuteOfHour(DEFAULT_MIN);
        }
        this.description = desc;
    }
    //@author A0105572L
    public DeadlineTask(String date, String desc){
        endDate = MutableDateTime.parse(date);
        if ((endDate.getHourOfDay() == 0) && (endDate.getMinuteOfHour() == 0)){
            endDate.setHourOfDay(DEFAULT_HOUR);
            endDate.setMinuteOfHour(DEFAULT_MIN);
        }
        this.description = desc;
    }
    //@author A0097081B
    public MutableDateTime getEndDate() {
        return endDate;
    }
    //@author A0097081B
    public String getDescription() {
        return this.description;
    }
    //@author A0097081B
    public String toString() {
    	return String.format(TASK_NAME_DEADLINE, endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear() - CURRENT_MIILLENIUM,
    						endDate.getHourOfDay(), endDate.getMinuteOfHour(), getDescription());
    }
    //@author A0105572L
    public String toStringForEditCommand() {
        return String.format(EDIT_TASK_NAME_DEADLINE, endDate.getDayOfMonth(), endDate.getMonthOfYear() , endDate.getYear() - CURRENT_MIILLENIUM, 
        					 endDate.getHourOfDay(), endDate.getMinuteOfHour(), getDescription());
    }
    //@author A0097081B
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
    //@author A0097081B
    @Override
    public int hashCode() {
        return endDate.hashCode() *
               31 + description.hashCode();
    }
    //@author A0105572L
    @Override
    public int compareTo(DeadlineTask arg0) {
        return DateTimeComparator.getInstance().compare(this.getEndDate(), arg0.getEndDate());
    }
}
