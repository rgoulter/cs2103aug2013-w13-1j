package jim.journal;

import java.util.List;

public class CompleteCommand extends Command {
	List<Task> tasksCompleted;
    
	public CompleteCommand(List<Task> tasksWhichMatchDescription) {
		tasksCompleted = tasksWhichMatchDescription;
	}

	@Override
    public void execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
		for (Task t : tasksCompleted){
    		journalManager.completeTask(t);
    	}
    }

}
