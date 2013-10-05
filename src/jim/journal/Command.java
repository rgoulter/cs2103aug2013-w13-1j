
package jim.journal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public abstract class Command {

    private StringBuilder outputStringBuilder;



    public Command() {
        outputStringBuilder = new StringBuilder();
    }



    public abstract void execute (JournalManager journalManager);



    // Abstract reading input from command line (should we need it)
    // so that in UnitTests or in the GUI this can be overridden.
    protected String inputLine () {
        try {
            // Read a line of input from STDIN.
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();

            return line;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

        return "";
    }



    protected void output (String outputStr) {
        outputStringBuilder.append(outputStr);
    }



    protected void outputln (String line) {
        output(line);
        outputStringBuilder.append('\n');
    }



    public String getOutput () {
        return outputStringBuilder.toString();
    }
}
