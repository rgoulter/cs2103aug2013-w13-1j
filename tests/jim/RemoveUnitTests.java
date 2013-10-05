package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.JournalManager;
import jim.journal.RemoveCommand;
import jim.suggestions.SuggestionManager;

import org.junit.Test;

public class RemoveUnitTests {

    @Test
    public void testStrictSyntaxRemoveCommandCanParse() {
    	String[] arguments = {"remove", "testing"};
    	
    	SuggestionManager sManager = new SuggestionManager();
    	Command parsedRemoveCommand = sManager.parseCommand(arguments);
    	
    	assertNotNull(parsedRemoveCommand);
    	assertTrue(parsedRemoveCommand instanceof RemoveCommand);
    }

    @Test
    public void testStrictSyntaxRemoveCommandNoMatchCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	AddCommand addCmd2 = new AddCommand("CS2101 Sectional");
    	addCmd2.execute(jManager);
    	
    	RemoveCommand removeCmd = new RemoveCommand("Tutorial");
    	removeCmd.execute(jManager);
    	
    	String output = removeCmd.getOutput();
    	boolean result = false;
    	
    	if (output.equals("Description was not matched.\n")) { result = true; }
    	assertTrue("Search for absent item has failed", result);
    }

    @Test
    public void testStrictSyntaxRemoveCommandHasMatchesCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	
    	RemoveCommand removeCmd1 = new RemoveCommand("Lecture");
    	removeCmd1.execute(jManager);
    	
    	String output = removeCmd1.getOutput();
    
    	assertEquals("Removed task: CS2103 Lecture\n", output);
    }
    
    @Test
    public void testStrictSyntaxRemoveCommandHasMultipleMatchesCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	AddCommand addCmd2 = new AddCommand("Lecture");
    	addCmd2.execute(jManager);
    	
    	RemoveCommand removeCmd2 = new RemoveCommand("Lecture");
    	removeCmd2.execute(jManager);
    	
    	String output = removeCmd2.getOutput();
    	
    	assertEquals("Removed task: CS2103 Lecture\nRemoved task: Lecture\n", output);
    	
    }

}
