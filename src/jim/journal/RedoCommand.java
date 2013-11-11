//@author A0097081B
package jim.journal;

public class RedoCommand extends Command{
    private static final String FILE_ERROR = "FILE_ERROR: Please add any tasks in the box above to create the storage file. Enjoy JIM!";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_FAIL = "Failure";
    private static final String MESSAGE_SUCCESS_REDO = "Redo Successful";
    private static final String MESSAGE_FAIL_REDO = "Redo Unsuccessful";
    private static final String COMMAND_REDO = "Redo";
    
    public RedoCommand() {
    }
    @Override
    public String execute(JournalManager journalManager) {
        // TODO Auto-generated method stub
        try {
            if (journalManager.redoUndoCommand()){
            	outputln(MESSAGE_SUCCESS_REDO);
            	return EXECUTION_STATUS_SUCCESS;
            } else {
            	outputln(MESSAGE_FAIL_REDO);
            	return EXECUTION_STATUS_FAIL;
            }
        } catch (Exception e) {
            outputln(FILE_ERROR);
            return EXECUTION_STATUS_FAIL;
        }
    }
    @Override
    public String secondExecute(String secondInput) {
        return null;
    }
    @Override
    public String thirdExecute(Task task) {
        return null;
    }
    
    public String toString() {
        return COMMAND_REDO;
    }

}