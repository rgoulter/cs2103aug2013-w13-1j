
package jim;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.DisplayCommand;
import jim.journal.JournalManager;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;






public class DisplayUnitTests {

    @Test
    public void testStrictSyntaxDisplayCommandZeroArgsCanParse () {
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
    public void testStrictSyntaxDisplayCommandUntimedTasksCanExecute () {
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

        assertTrue("Display command test (on zero arguments) has failed",
                   stringsMatch);
    }

    @Test
    public void testStrictSyntaxDisplayCommandTimedTasksCanExecute() {
    	JournalManager jManager = new JournalManager();
        Calendar startCal = new GregorianCalendar(2013, 10, 7);
        Calendar endCal = new GregorianCalendar(2013, 10, 8);
        MutableDateTime start = new MutableDateTime(startCal);
        MutableDateTime end = new MutableDateTime(endCal);
    	
    	AddCommand addCmd = new AddCommand(start, end, "Birthday Party");
    	addCmd.execute(jManager);
    	
    	DisplayCommand dispCmd = new DisplayCommand();
    	dispCmd.execute(jManager);
    	String output = dispCmd.getOutput();
    	
    	assertTrue("Display of Timed Task failed",
    	           output.equals("Birthday Party 8/11/2013 00 00\n"));
    }

    @Test
    public void testStrictSyntaxDisplayCommandOneArgsCanExecute () {
        JournalManager jManager = new JournalManager();

        Calendar testDateCal = new GregorianCalendar(2013, 10, 10);
        MutableDateTime testDate = new MutableDateTime(testDateCal);
        AddCommand addCmd = new AddCommand(testDate, testDate, "CS2103 Lecture");
        addCmd.execute(jManager);

        DisplayCommand dispCmd = new DisplayCommand(testDate);
        dispCmd.execute(jManager);

        String output = dispCmd.getOutput();

        boolean stringsMatch = false;
        if (output.equals("CS2103 Lecture 10/10/2013 00 00\n")) {
            stringsMatch = true;
        }

        assertTrue("Display command test (on one argument) has failed",
                   stringsMatch);
    }
    
    @Test
    public void testStrictSyntaxDisplayCommandMultipleItemsCanExecute() {
        JournalManager jManager = new JournalManager();
        
        Calendar testDateCal = new GregorianCalendar(2013, 10, 10);
        MutableDateTime testDate = new MutableDateTime(testDateCal);
        AddCommand addCmd = new AddCommand(testDate, testDate, "CS2103 Lecture");
        addCmd.execute(jManager);
        
        testDateCal = new GregorianCalendar(2013, 10, 14);
        testDate = new MutableDateTime(testDateCal);
        addCmd = new AddCommand(testDate, testDate, "CS2101 Lesson");
        addCmd.execute(jManager);
        
        testDateCal = new GregorianCalendar(2013, 10, 10);
        testDate = new MutableDateTime(testDateCal);
        DisplayCommand dispCmd = new DisplayCommand(testDate);
        dispCmd.execute(jManager);
        
        String output = dispCmd.getOutput();
        assertTrue("Display command test on multiple items failed",
                   output.equals("CS2103 Lecture 10/10/2013 00 00\n"));
    }

}
