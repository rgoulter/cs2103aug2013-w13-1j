
package jim.journal;
import org.joda.time.DateTimeComparator;
import org.joda.time.MutableDateTime;


public class TimedTask extends Task implements Comparable<TimedTask>{

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
    	String taskName = "[%d/%d/%d] [%02d:%02d - %02d:%02d] %s";
    	String taskNameNoStartTime = "[%d/%d/%d] [%02d:%02d] %s";
    	//TODO: Do we still need to check startTime != null?? Isn't it DeadlineTask?
    	if (this.startTime == null) {
        	return String.format(taskNameNoStartTime, 
        						 endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear(),
        						 endTime.getHourOfDay(), endTime.getMinuteOfHour(), getDescription());
    	} else {
        	return String.format(taskName, endTime.getDayOfMonth(), endTime.getMonthOfYear() , endTime.getYear(),
					startTime.getHourOfDay(), startTime.getMinuteOfHour(),
					endTime.getHourOfDay(), endTime.getMinuteOfHour(),
					getDescription());
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

    @Override
    public int compareTo(TimedTask arg0) {
        // TODO Auto-generated method stub
        return DateTimeComparator.getInstance().compare(this.getStartTime(), arg0.getStartTime());
    }
    
    
}
