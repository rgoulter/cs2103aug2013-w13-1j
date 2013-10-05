
package jim.journal;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class TimedTask extends Task {

    private GregorianCalendar startTime;
    private GregorianCalendar endTime;



    // private String description;

    public TimedTask(Calendar startTime, Calendar endTime, String desc) {
        // Tasks with both start and end date and time.
        this.startTime = (GregorianCalendar) startTime;
        this.endTime = (GregorianCalendar) endTime;
        description = desc;
    }



    /*
     * public TimedTask(Calendar endTime, String desc) { // Tasks with ONLY end
     * date. (AKA deadline tasks) this.startTime = null; this.endTime =
     * (GregorianCalendar) endTime; description = desc; }
     */

    public Calendar getStartTime() {
        return startTime;
    }



    public Calendar getEndTime() {
        return endTime;
    }



    public String getDescription() {
        return description;
    }



    public String toString() {
        String taskName = "%s %d/%d/%d %d%d %d%d";
        return String.format(taskName,
                             getDescription(),
                             endTime.get(Calendar.DAY_OF_MONTH),
                             endTime.get(Calendar.MONTH) + 1,
                             endTime.get(Calendar.YEAR),
                             startTime.get(Calendar.HOUR_OF_DAY),
                             startTime.get(Calendar.MINUTE),
                             endTime.get(Calendar.HOUR_OF_DAY),
                             endTime.get(Calendar.MINUTE));
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
