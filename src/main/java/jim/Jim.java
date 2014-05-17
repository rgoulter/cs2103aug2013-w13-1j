//@author A0096790N
package jim;

/* import java.util.Scanner;

import jim.journal.Command;
import jim.journal.JournalManager;
import jim.suggestions.SuggestionManager; */


// Deprecated: Formerly the entry point of JIM! via CLI
//             Old code has been commented out in case of future need for CLI integration
//             Working code on this page launches the GUI
public class Jim {
    
    public static void main(String[] args) {
        new JimMainPanel();
    }
    
    /*
    public static void main(String[] args) {
        
        // Load Journal logic.
        SuggestionManager suggestionManager = new SuggestionManager();
        JournalManager journalManager = new JournalManager();

        // Parse the command, try to execute it.
        Command cmd = suggestionManager.parseCommand(args);
        
        if (cmd != null) {
            cmd.execute(journalManager);
            System.out.print(cmd.getOutput());
        } else {
            System.out.println("Unable to parse arguments.");
        }
        
    } */

}
