// All members have edited this class
package jim.journal;

public abstract class Command {
	private static final String GENERIC_COMMAND = "Generic Command";

    private StringBuilder outputStringBuilder;
    String ExecutionState = "Pending";


    public Command() {
        outputStringBuilder = new StringBuilder();
    }
    
    public abstract String execute(JournalManager journalManager);
    
    public abstract String secondExecute(String secondInput);
    
    public abstract String thirdExecute(Task task);
    
    public void output(String outputStr) {
        outputStringBuilder.append(outputStr);
    }
    
    protected void outputln(String line) {
        output(line);
        outputStringBuilder.append('\n');
    }
    
    protected void clearOutput() {
        int stringLength = outputStringBuilder.length();
        outputStringBuilder.delete(0, stringLength);
    }
    
    public String getOutput() {
        return outputStringBuilder.toString();
    }
    
    @Override
    public String toString() {
        return GENERIC_COMMAND;
    }
}