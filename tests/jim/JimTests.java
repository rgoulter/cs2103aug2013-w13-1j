package jim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import jim.suggestions.SuggestionManager;
import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.CompleteCommand;
import jim.journal.DisplayCommand;
import jim.journal.JournalManager;
import jim.journal.RemoveCommand;
import jim.journal.SearchCommand;
import jim.journal.Task;
import jim.journal.TimedTask;

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

    @Test
    public void testStrictSyntaxAddCommandCanExecute() {
        // Strict syntax for "add" command:
        // add <start-date> <start-time> <end-date> <end-time> <words describing event>
    	
        Calendar startTime = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar endTime =   new GregorianCalendar(2013, 10, 10, 15, 0);
        String description = "CS2103 Lecture";
        
        AddCommand addCmd = new AddCommand(startTime, endTime, description);
        JournalManager journalManager = new JournalManager(); // Empty; NO TASKS.
        addCmd.execute(journalManager);

        Task expectedTask = new TimedTask(startTime, endTime, description);
        List<Task> expectedList = new ArrayList<Task>();
        expectedList.add(expectedTask);
        
        assertEquals(expectedList, journalManager.getAllTasks());
    }
    
    @Test
    public void testStrictSyntaxDisplayCommandZeroArgsCanParse() {
    	// Strict syntax for "display" command (0 parameters edition):
    	// display
    	
    	String testCommand = "display";
    	String[] testCommandWords = testCommand.split(" ");
    	
    	SuggestionManager sManager = new SuggestionManager();
    	Command parsedCmd = sManager.parseCommand(testCommandWords);
    	
    	assertNotNull(parsedCmd);
    	assertTrue(parsedCmd instanceof DisplayCommand);
    }
    
    @Test
    public void testStrictSyntaxDisplayCommandZeroArgsCanExecute() {
    	JournalManager jManager = new JournalManager();
    	
    	AddCommand addCmd = new AddCommand("CS2103 Lecture");
    	addCmd.execute(jManager);
    	
    	DisplayCommand dispCmd = new DisplayCommand();
    	dispCmd.execute(jManager);
    	
    	String output = dispCmd.getOutput();
    	
    	boolean stringsMatch = false;
    	if (output.equals("CS2103 Lecture\n")) {
    		stringsMatch = true;
    	}
    	
    	assertTrue("Display command test (on zero arguments) has failed", stringsMatch);
    }
    
    @Test
    public void testStrictSyntaxDisplayCommandOneArgsCanExecute() {    	
    	JournalManager jManager = new JournalManager();
    	
    	Calendar testDate = new GregorianCalendar(2013,10,10);
    	AddCommand addCmd = new AddCommand(testDate, testDate, "CS2103 Lecture");
    	addCmd.execute(jManager);
    	
    	DisplayCommand dispCmd = new DisplayCommand(new GregorianCalendar(2013,10,10));
    	dispCmd.execute(jManager);
    	
    	String output = dispCmd.getOutput();
    	
    	boolean stringsMatch = false;
    	if (output.equals("CS2103 Lecture\n")) {
    		stringsMatch = true;
    	}
    	
    	assertTrue("Display command test (on one argument) has failed", stringsMatch);
    }

    @Test
    public void testStrictSyntaxSearchCommandCanParse() {
    	String[] arguments = {"search", "testing"};
    	
    	SuggestionManager sManager = new SuggestionManager();
    	Command parsedSearchCommand = sManager.parseCommand(arguments);
    	
    	assertNotNull(parsedSearchCommand);
    	assertTrue(parsedSearchCommand instanceof SearchCommand);
    }

    @Test
    public void testStrictSyntaxSearchCommandNoMatchCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	AddCommand addCmd2 = new AddCommand("CS2101 Sectional");
    	addCmd2.execute(jManager);
    	
    	SearchCommand searchCmd = new SearchCommand("Tutorial");
    	searchCmd.execute(jManager);
    	
    	String output = searchCmd.getOutput();
    	boolean result = false;
    	
    	if (output.equals("Search term 'Tutorial' was not found.\n")) { result = true; }
    	assertTrue("Search for absent item has failed", result);
    }

    @Test
    public void testStrictSyntaxSearchCommandHasMatchesCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	
    	SearchCommand searchCmd1 = new SearchCommand("Lecture");
    	searchCmd1.execute(jManager);
    	
    	String output = searchCmd1.getOutput();
    	boolean result = false;
    	
    	if (output.equals("Matches for 'Lecture':\nCS2103 Lecture\n\n")) { result = true; }
    	assertTrue("Basic search has failed", result);
    	
    	result = false;
    	AddCommand addCmd2 = new AddCommand("CS2103 Tutorial");
    	addCmd2.execute(jManager);
    	
    	SearchCommand searchCmd2 = new SearchCommand("Lecture");
    	searchCmd2.execute(jManager);
    	
    	output = searchCmd2.getOutput();
    	if (output.equals("Matches for 'Lecture':\nCS2103 Lecture\n\n")) { result = true; }
    	assertTrue("Search with non-matches failed", result);
    	
    	result = false;
    	AddCommand addCmd3 = new AddCommand("CS2101 Lecture");
    	addCmd3.execute(jManager);

    	SearchCommand searchCmd3 = new SearchCommand("Lecture");
    	searchCmd3.execute(jManager);
    	
    	output = searchCmd3.getOutput();
    	if (output.equals("Matches for 'Lecture':\nCS2103 Lecture\nCS2101 Lecture\n\n")) { result = true; }
    	assertTrue("Search with multiple matches failed", result);
    	
    }
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
    public void testStrictSyntaxCompleteCommandNoMatchCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	AddCommand addCmd2 = new AddCommand("CS2101 Sectional");
    	addCmd2.execute(jManager);
    	
    	CompleteCommand completeCmd = new CompleteCommand("Tutorial");
    	completeCmd.execute(jManager);
    	
    	String output = completeCmd.getOutput();

    	assertEquals("Description was not matched.\n", output);
    }

    @Test
    public void testStrictSyntaxCompleteCommandHasMatchesCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	
    	CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
    	completeCmd1.execute(jManager);
    	
    	String output = completeCmd1.getOutput();
    
    	assertEquals("Completed Task: CS2103 Lecture\n", output);
    }
    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	AddCommand addCmd2 = new AddCommand("Lecture");
    	addCmd2.execute(jManager);
    	
    	CompleteCommand completeCmd = new CompleteCommand("Lecture");
    	completeCmd.execute(jManager);
    	
    	String output = completeCmd.getOutput();
    	
    	assertEquals("Completed Task: CS2103 Lecture\nCompleted Task: Lecture\n", output);
    	
    }
    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesButCompletedCanExecute() {
    	JournalManager jManager = new JournalManager();
    	AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
    	addCmd1.execute(jManager);
    	
    	CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
    	completeCmd1.execute(jManager);
    	CompleteCommand completeCmd2 = new CompleteCommand("Lecture");
    	completeCmd2.execute(jManager);
    	
    	
    	String output = completeCmd2.getOutput();
    	
    	assertEquals("Task CS2103 Lecture has already been marked as completed.\n", output);
    	
    }
}
