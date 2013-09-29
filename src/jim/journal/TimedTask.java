package jim.journal;

import java.util.Calendar;

public class TimedTask extends Task {
    private Calendar startTime;
    private Calendar endTime;
    // private String description;
    
    public TimedTask(Calendar startTime, Calendar endTime, String desc) {
    	// Tasks with both start and end date and time.
        this.startTime = startTime;
        this.endTime = endTime;
        description = desc;
    }
    
    public TimedTask(Calendar endTime, String desc) {
    	// Tasks with ONLY end date. (AKA deadline tasks)
        this.startTime = null;
        this.endTime = endTime;
        description = desc;
    }
    
    public Calendar getStartTime() {
        return startTime;
    }
    
    public Calendar getEndTime() {
        return endTime;
    }
    
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        // TODO: An actual equals method
        return o instanceof TimedTask;
    }
    
    @Override
    public int hashCode() {
        return startTime.hashCode() * 31 + endTime.hashCode() * 13 + description.hashCode();
    }
}
