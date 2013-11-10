//@author A0105572L
package jim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.DisplayCommand;
import jim.journal.JournalManager;
import jim.journal.TemporaryJournalManager;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;
public class DisplayUnitTests {

    @Test
    public void testStrictSyntaxDisplayCommandZeroArgsCanParse () {
        String testCommand = "display";
        String[] testCommandWords = testCommand.split(" ");

        SuggestionManager sManager = new SuggestionManager();
        Command parsedCmd = sManager.parseCommand(testCommandWords);

        assertNotNull(parsedCmd);
        assertTrue(parsedCmd instanceof DisplayCommand);
    }



    @Test
    public void testStrictSyntaxDisplayCommandUntimedTasksCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();

        AddCommand addCmd = new AddCommand("CS2103 Lecture");
        addCmd.execute(jManager);

        DisplayCommand dispCmd = new DisplayCommand();
        dispCmd.execute(jManager);

        String output = dispCmd.getOutput();

       

        assertEquals("-------------------- Tasks ----------------------\n" + 
                "Timed Tasks: \n" +
                "\n" +
                "Deadline Tasks: \n" +
                "\n" + 
                "Todo: \n" +
                "CS2103 Lecture\n" +
                "\n--------------- Completed Tasks -----------------\n"+
                "Timed Tasks: \n\n" +
                "Deadline Tasks: \n\n" +
                "Todo: \n",
                output);
    }

    @Test
    public void testStrictSyntaxDisplayCommandTimedTasksCanExecute() {
        Configuration cManager = Configuration.getConfiguration();
        String dSeparator = cManager.getDateSeparator();
        String tSeparator = cManager.getTimeSeparator();
        
    	JournalManager jManager = new TemporaryJournalManager();
        Calendar startCal = new GregorianCalendar(2013, 10, 7);
        Calendar endCal = new GregorianCalendar(2013, 10, 8);
        MutableDateTime start = new MutableDateTime(startCal);
        MutableDateTime end = new MutableDateTime(endCal);
    	
    	AddCommand addCmd = new AddCommand(start, end, "Birthday Party");
    	addCmd.execute(jManager);
    	
    	DisplayCommand dispCmd = new DisplayCommand();
    	dispCmd.execute(jManager);
    	String output = dispCmd.getOutput();
    	
        assertEquals("Display of Timed Task failed",
                     "-------------------- Tasks ----------------------\n" + 
                     "Timed Tasks: \n" +
                     "[08" + dSeparator + "11" + dSeparator + "2013] " +
                     "[00" + tSeparator + "00 - 00" + tSeparator + "00] Birthday Party\n"+
                     "\n" +
                     "Deadline Tasks: \n" +
                     "\n" + 
                     "Todo: \n" +
                     "\n--------------- Completed Tasks -----------------\n"+
                     "Timed Tasks: \n\n" +
                     "Deadline Tasks: \n" +
                     "\n" + 
                     "Todo: \n",
                     output);
    }

    @Test
    public void testStrictSyntaxDisplayCommandOneArgsCanExecute () {
        Configuration cManager = Configuration.getConfiguration();
        String dSeparator = cManager.getDateSeparator();
        String tSeparator = cManager.getTimeSeparator();
        
        JournalManager jManager = new TemporaryJournalManager();

        // Note that "January" is Month 0. October is month 9..
        Calendar testDateCal = new GregorianCalendar(2013, 9, 10);
        MutableDateTime testDate = new MutableDateTime(testDateCal);
        AddCommand addCmd = new AddCommand(testDate, testDate, "CS2103 Lecture");
        addCmd.execute(jManager);

        DisplayCommand dispCmd = new DisplayCommand(testDate);
        dispCmd.execute(jManager);

        String output = dispCmd.getOutput();
        assertEquals("-------------------- Tasks ----------------------\n" + 
                "Timed Tasks: \n" +
                "[10" + dSeparator + "10" + dSeparator + "2013] " + 
                "[00" + tSeparator + "00 - 00" + tSeparator + "00] CS2103 Lecture\n" +
                "\n" +
                "Deadline Tasks: \n" +
                "\n" + 
                "Todo: \n" +
                "\n--------------- Completed Tasks -----------------\n"+
                "Timed Tasks: \n\n" +
                "Deadline Tasks: \n" +
                "\n" + 
                "Todo: \n",
                output);
                     
    }
    
    @Test
    public void testStrictSyntaxDisplayCommandMultipleItemsCanExecute() {
        Configuration cManager = Configuration.getConfiguration();
        String dSeparator = cManager.getDateSeparator();
        String tSeparator = cManager.getTimeSeparator();
        
        JournalManager jManager = new TemporaryJournalManager();
        
        Calendar testDateCal = new GregorianCalendar(2013, 9, 10);
        MutableDateTime testDate = new MutableDateTime(testDateCal);
        AddCommand addCmd = new AddCommand(testDate, testDate, "CS2103 Lecture");
        addCmd.execute(jManager);
        
        testDateCal = new GregorianCalendar(2013, 9, 14);
        testDate = new MutableDateTime(testDateCal);
        addCmd = new AddCommand(testDate, testDate, "CS2101 Lesson");
        addCmd.execute(jManager);
        
        testDateCal = new GregorianCalendar(2013, 9, 10);
        testDate = new MutableDateTime(testDateCal);
        DisplayCommand dispCmd = new DisplayCommand(testDate);
        dispCmd.execute(jManager);
        
        String output = dispCmd.getOutput();
        assertEquals("Display command test on multiple items failed",
                             "-------------------- Tasks ----------------------\n" +
                             "Timed Tasks: \n" +
                             "[10" + dSeparator + "10" + dSeparator + "2013] " + 
                             "[00" + tSeparator + "00 - 00" + tSeparator + "00] CS2103 Lecture\n" +
                             "\n" +
                             "Deadline Tasks: \n" +
                             "\n" + 
                             "Todo: \n" +
                             "\n--------------- Completed Tasks -----------------\n"+
                             "Timed Tasks: \n\n" +
                             "Deadline Tasks: \n\n" +
                             "Todo: \n",
                             output);
                     
    }

}
