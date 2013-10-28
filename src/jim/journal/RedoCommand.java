package jim.journal;



public class RedoCommand extends Command{
	
    public RedoCommand() {
    }
    @Override
    public String execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
        if (journalManager.redoUndoCommand()){
        	outputln("Redo Successful");
        	return "Success";
        } else {
        	outputln("Redo Unsuccessful");
        	return "Failure";
        }
    }
    @Override
    public String secondExecute(String secondInput) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString() {
        return "Redo";
    }

}