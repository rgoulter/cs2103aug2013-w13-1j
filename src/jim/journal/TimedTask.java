
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
    
    //
    public TimedTask(String startTime, String endTime, String desc) {
        if (startTime.isEmpty() || endTime.isEmpty()){
            
               System.out.println("timed task must have time! error exists in storage.");
            
        }
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
    	String taskName = "%s %d/%d/%d %02d:%02d to %02d:%02d";
    	String taskNameNoStartTime = "%s %d/%d/%d %02d:%02d";
    	if (this.startTime == null) {
        	return String.format(taskNameNoStartTime, getDescription(), 
        						 endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear(),
        						 endTime.getHourOfDay(), endTime.getMinuteOfHour());
    	} else {
        	return String.format(taskName, getDescription(), endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear(),
					startTime.getHourOfDay(), startTime.getMinuteOfHour(),
					endTime.getHourOfDay(), endTime.getMinuteOfHour());
    	}

    	//return getDescription() +"  "+ startTime.toString() +" to " + endTime.toString();
    }



    @Override
    public boolean equals(Object o) {
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
