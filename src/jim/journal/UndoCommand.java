//@author A0097081B
package jim.journal;

public class UndoCommand extends Command{
    private static final String FILE_ERROR = "FILE_ERROR: Please add any tasks in the box above to create the storage file. Enjoy JIM!";
    private static final String COMMAND_UNDO = "Undo";
    private static final String MESSAGE_SUCCESSFUL = "Undo Successful";
    private static final String MESSAGE_UNSUCCESSFUL = "Undo Unsuccessful";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_FAIL = "Failure";
    
    public UndoCommand() {
    }
    @Override
    public String execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
        try {
            if (journalManager.undoLastCommand()) {
              outputln(MESSAGE_SUCCESSFUL);
              return EXECUTION_STATUS_SUCCESS;
            } else {
              outputln(MESSAGE_UNSUCCESSFUL);
              return EXECUTION_STATUS_FAIL;
            }
        } catch (Exception e) {
            outputln(FILE_ERROR);
            return EXECUTION_STATUS_FAIL;
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
        return COMMAND_UNDO;
    }

}
