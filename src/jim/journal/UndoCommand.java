package jim.journal;

public class UndoCommand extends Command{
	
    public UndoCommand() {
    }
    @Override
    public void execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
        journalManager.undoLastCommand();
    }

}
