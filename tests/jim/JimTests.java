package jim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import jim.suggestions.SuggestionManager;
import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.DisplayCommand;
import jim.journal.JournalManager;
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
        
    	// TODO: This is cheating as well ~CC
    	
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
    	// NB: This is kinda cheating, since it depends on the currently hard-coded "getAllTasks"
    	
    	JournalManager jManager = new JournalManager();
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
    	// NB: This is kinda cheating, since it depends on the currently hard-coded "getAllTasks"
    	
    	JournalManager jManager = new JournalManager();
    	DisplayCommand dispCmd = new DisplayCommand(new GregorianCalendar(2013,10,10));
    	dispCmd.execute(jManager);
    	
    	String output = dispCmd.getOutput();
    	
    	boolean stringsMatch = false;
    	if (output.equals("CS2103 Lecture\n")) {
    		stringsMatch = true;
    	}
    	
    	System.out.println(output.length());
    	assertTrue("Display command test (on one argument) has failed", stringsMatch);
    }

}
