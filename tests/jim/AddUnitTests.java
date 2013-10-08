
package jim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.JournalManager;
import jim.journal.Task;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;






public class AddUnitTests {

    @Test
    public void testStrictSyntaxAddCommandCanParse () {
        // Strict syntax for "add" command:
        // add <start-date> <start-time> <end-date> <end-time> <words describing
        // event>
        String testCommand = "add 10/10/13 1400 10/10/13 1500 CS2103 Lecture";
        String[] testCommandWords = testCommand.split(" ");

        SuggestionManager suggestionManager = new SuggestionManager();
        Command parsedAddCmd = suggestionManager.parseCommand(testCommandWords);

        assertNotNull(parsedAddCmd);
        assertTrue(parsedAddCmd instanceof AddCommand);
    }



    @Test
    public void testStrictSyntaxAddCommandCanExecute () {
        // Strict syntax for "add" command:
        // add <start-date> <start-time> <end-date> <end-time> <words describing
        // event>

        Calendar startTimeCal = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar endTimeCal = new GregorianCalendar(2013, 10, 10, 15, 0);
        MutableDateTime startTime = new MutableDateTime(startTimeCal);
        MutableDateTime endTime = new MutableDateTime(endTimeCal);
        String description = "CS2103 Lecture";

        AddCommand addCmd = new AddCommand(startTime, endTime, description);
        JournalManager journalManager = new TemporaryJournalManager(); // Empty; NO
                                                              // TASKS.
        addCmd.execute(journalManager);

        Task expectedTask = new TimedTask(startTime, endTime, description);
        List<Task> expectedList = new ArrayList<Task>();
        expectedList.add(expectedTask);

        assertEquals(expectedList, journalManager.getAllTasks());
    }

}
