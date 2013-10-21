
package jim.journal;




public abstract class Command {

    private StringBuilder outputStringBuilder;
    String ExecutionState = "Pending";


    public Command() {
        outputStringBuilder = new StringBuilder();
    }

    public String getCommandState(){
        return ExecutionState;
    }
    
    //Can only be "Pending", "Failure" or "Success". 
    public void changeCommandState(String d){
        ExecutionState = d;
    }
    
    
    
    public abstract String execute(JournalManager journalManager);
    
    public abstract String secondExecute(String secondInput);
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
