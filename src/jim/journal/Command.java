package jim.journal;

public abstract class Command {
    private StringBuilder outputStringBuilder;
    
    public Command() {
        outputStringBuilder = new StringBuilder();
    }
    
    public abstract void execute(JournalManager journalManager);
    
    
    protected void output(String outputStr) {
        outputStringBuilder.append(outputStr);
    }
    
    protected void outputln(String line) {
        output(line);
        outputStringBuilder.append('\n');
    }
    
    public String getOutput() {
        return outputStringBuilder.toString();
    }
}
