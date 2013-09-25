package jim;

import static org.junit.Assert.*;
import jim.suggestions.SuggestionManager;
import jim.journal.AddCommand;
import jim.journal.Command;

import org.junit.Test;

public class JimTests {
    
    /*
     * Strict Syntax Tests:
     * These tests will test that add works when a strict command syntax is adhered to.
     */

    @Test
    public void testStrictSyntaxAddCommandCanParse() {
        // Strict syntax for "add" command:
        // add <start-date> <start-time> <end-date> <end-time> <words describing event>
        String testCommand = "add 10/10/13 1400 10/10/13 1500 CS2103 Lecture";
        String[] testCommandWords = testCommand.split(" "); 
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Command parsedAddCmd = suggestionManager.parseCommand(testCommandWords);
        
        assertNotNull(parsedAddCmd);
        assertTrue(parsedAddCmd instanceof AddCommand);
    }

}
