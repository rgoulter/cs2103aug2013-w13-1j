package jim.journal;

public abstract class Command {

    private StringBuilder outputStringBuilder;
    String ExecutionState = "Pending";


    public Command() {
        outputStringBuilder = new StringBuilder();
    }
    //First Execution for all command
    public abstract String execute(JournalManager journalManager);
    
    //Used when the command needs confirmation from user. e.g remove, complete, uncomplete, edit command.
    //@author A0105572L
    public abstract String secondExecute(String secondInput);
    
    //Used when the command needs a new task from user. e.g edit command.
    //@author A0105572L
    public abstract String thirdExecute(Task task);

    protected void output(String outputStr) {
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
        return "Generic Command";
    }
}
