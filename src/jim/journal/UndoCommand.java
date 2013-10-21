package jim.journal;



public class UndoCommand extends Command{
	
    public UndoCommand() {
    }
    @Override
    public String execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
        journalManager.undoLastCommand();
        outputln("Undo Successful");
        return "Success";
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

}
