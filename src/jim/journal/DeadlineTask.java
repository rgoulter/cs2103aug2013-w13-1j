package jim.journal;
import org.joda.time.MutableDateTime;


public class DeadlineTask extends Task {

    private MutableDateTime endDate;


    public DeadlineTask(MutableDateTime endDate, String desc) {
        // Tasks with both start and end date and time.
        this.endDate = endDate;
        this.description = desc;
    }
    public DeadlineTask(String endDate, String desc){
        if (endDate.isEmpty()){
            System.out.println("deadline task must have time! error exists in storage.");
        } else{
            this.endDate = MutableDateTime.parse(endDate);
        }
        this.description = desc;
    }
    
    public MutableDateTime getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
    	
    	String taskNameDeadline = "%s %d/%d/%d";
    	
    	return String.format(taskNameDeadline, getDescription(), endDate.getDayOfMonth(), endDate.getMonthOfYear() , endDate.getYear());
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
}
