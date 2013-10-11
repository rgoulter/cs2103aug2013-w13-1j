
package jim.journal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jim.JimInputter;



public abstract class Command {

    private StringBuilder outputStringBuilder;
    protected JimInputter inputSource;



    public Command() {
        outputStringBuilder = new StringBuilder();
    }

    public void setInputSource(JimInputter source) {
        inputSource = source;
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
