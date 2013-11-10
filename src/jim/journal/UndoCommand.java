package jim.journal;



public class UndoCommand extends Command{
    private static final String FILE_ERROR = "FILE_ERROR";
    public UndoCommand() {
    }
    @Override
    public String execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
        try {
            if (journalManager.undoLastCommand()) {
              outputln("Undo Successful");
              return "Success";
            } else {
              outputln("Undo Unsuccessful");
              return "Failure";
            }
        } catch (Exception e) {
            outputln(FILE_ERROR);
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
        return "Undo";
    }

}
