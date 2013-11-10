// All members have edited this class
package jim.journal;

public abstract class Task {

    protected String description;
    protected boolean completed = false;

    public boolean isCompleted() {
        return completed;
    }
    public void markAsCompleted() {
        completed = true;
    }
    public void markAsIncompleted() {
        completed = false;
    }
    public String getDescription() {
        return description;
    }
    public String toString() {
        return description;
    }
    public String toStringForEditCommand(){
        return description;
    }
    public boolean equals(Object o) {
        if (o instanceof Task) {
            Task helper = (Task) o;
            if (this.toString().equalsIgnoreCase(helper.toString())) {
                return true;
            }
        }
        return false;
    }
}
