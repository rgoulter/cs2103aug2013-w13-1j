package jim.journal;

import java.util.ArrayList;

public class FloatingTask extends Task {
	private static final String MESSAGE_PRINT_FLOATING_TASKS = "%d. %s";
	
    private ArrayList <String> listOfFloatingTask = new ArrayList <String> ();
 
    public FloatingTask(String desc){
        description = desc;
    }
    
    public void addFloatingTask (String task) {
    	listOfFloatingTask.add(task);
    }
    
    public String getDescription(int index) {
        return listOfFloatingTask.get(index);
    }

    @Override
    public boolean equals(Object o) {
        // TODO: An actual equals method
    	for (int i = 0; i < listOfFloatingTask.size(); i++) {
    		if (o.equals(listOfFloatingTask.get(i))) {
    			return true;
    		}
    	}
		return false; 
    }
    
    public void printFloatingTasks() {
    	for (int i = 0; i < listOfFloatingTask.size(); i++) {
    		System.out.println(String.format(MESSAGE_PRINT_FLOATING_TASKS,i,listOfFloatingTask.get(i)));
    	}
    }
}
