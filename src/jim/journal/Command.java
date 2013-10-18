
package jim.journal;

import jim.JimInputter;



public abstract class Command {

    private StringBuilder outputStringBuilder;
    protected JimInputter inputSource;
    String ExecutionState = "Pending";


    public Command() {
        outputStringBuilder = new StringBuilder();
    }

    public void setInputSource(JimInputter source) {
        inputSource = source;
    }
    public String getCommandState(){
        return ExecutionState;
    }
    
    //Can only be "Pending", "Failure" or "Success". 
    public void changeCommandState(String d){
        ExecutionState = d;
    }
    
    
    
    public abstract void execute(JournalManager journalManager);

    // Abstract reading input from command line (should we need it)
    // so that in UnitTests or in the GUI this can be overridden.
    protected String inputLine() {
        return inputSource.getInput();
    }



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
