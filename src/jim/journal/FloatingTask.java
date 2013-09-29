package jim.journal;

public class FloatingTask extends Task {
 
    public FloatingTask(String desc){
        description = desc;
    }
    
    public String getDescription() {
        return description;
    }
    public boolean equals(Object o) {
        // TODO: An actual equals method
    	if (o instanceof FloatingTask){
    		FloatingTask helper = (FloatingTask) o;
    		if (this.description.equalsIgnoreCase(helper.getDescription())){
    			return true;
    		}
    	}
    	return false;
    }
}
