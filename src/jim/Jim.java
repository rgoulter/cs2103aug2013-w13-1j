
package jim;

import java.util.Scanner;

import jim.journal.Command;
import jim.journal.JournalManager;
import jim.suggestions.SuggestionManager;



/**
 * The entry-point for command-line executions of Jim!. Should $java -jar
 * Jim.jar jim.Jim be too ugly to type, we could probably put this in a .bat or
 * .sh script.
 */
public class Jim {

    static class CLIInputter extends JimInputter {
        private static Scanner sc;
        
        public CLIInputter() {
            sc = new Scanner(System.in);
        }
        
        public String getInput() {
            return sc.nextLine();
        }
    }
    
    public static void main(String[] args) {
        
        // Load Journal logic.
        SuggestionManager suggestionManager = new SuggestionManager();
        JournalManager journalManager = new JournalManager();
        
        JimInputter inputSource = new CLIInputter();

        // Parse the command, try to execute it.
        Command cmd = suggestionManager.parseCommand(args);
        
        if (cmd != null) {
        	cmd.setInputSource(inputSource);
            cmd.execute(journalManager);
            System.out.print(cmd.getOutput());
        } else {
            System.out.println("Unable to parse arguments.");
        }
        
    }

}
