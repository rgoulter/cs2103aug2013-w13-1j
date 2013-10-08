
package jim.journal;
import org.joda.time.MutableDateTime;


public class TimedTask extends Task {

    private MutableDateTime startTime;
    private MutableDateTime endTime;



    // private String description;

    public TimedTask(MutableDateTime startTime, MutableDateTime endTime, String desc) {
        // Tasks with both start and end date and time.
        this.startTime = startTime;
        this.endTime = endTime;
        description = desc;
    }

    public TimedTask(MutableDateTime date, String description) {
        // TODO Auto-generated constructor stub
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
    	String taskName = "%s %d/%d/%d %02d:%02d to %02d:%02d";
    	return String.format(taskName, getDescription(), endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear(),
    						startTime.getHourOfDay(), startTime.getMinuteOfHour(),
    						endTime.getHourOfDay(), endTime.getMinuteOfHour());
    	//return getDescription() +"  "+ startTime.toString() +" to " + endTime.toString();
    }



    @Override
    public boolean equals(Object o) {
        // TODO: An actual equals method
        if (o instanceof TimedTask) {
            TimedTask helper = (TimedTask) o;
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
}
