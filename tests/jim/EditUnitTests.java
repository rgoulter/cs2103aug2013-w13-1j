
package jim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jim.journal.Command;
import jim.journal.EditCommand;
import jim.journal.JournalManager;
import jim.journal.Task;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;






public class EditUnitTests {

    @Test
    public void testStrictSyntaxEditCommandCanParse () {
        // Strict syntax for "edit" command:
        // This edits a timed task
        // edit <description of old task> to <new description> <new start-date>
        // <new start-time> <new end-date> <new end-time>

        Calendar startTimeCal = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar endTimeCal = new GregorianCalendar(2013, 10, 10, 15, 0);
        MutableDateTime startTime = new MutableDateTime(startTimeCal);
        MutableDateTime endTime = new MutableDateTime(endTimeCal);
        TimedTask myOldTimedTask = new TimedTask(startTime,
                                                 endTime,
                                                 "MyOldTask");

        String testCommand = "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";
        String[] testCommandWords = testCommand.split(" ");

        SuggestionManager suggestionManager = new SuggestionManager();
        Command parsedEditCmd = suggestionManager.parseCommand(testCommandWords);

        assertNotNull(parsedEditCmd);
        assertTrue(parsedEditCmd instanceof EditCommand);
    }



    @Test
    public void testStrictSyntaxEditCommandCanExecute () {
        // Edit Command with the given change-to-task.

        // e.g. "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";

        Calendar oldStartTimeCal = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar oldEndTimeCal = new GregorianCalendar(2013, 10, 10, 15, 0);
        MutableDateTime oldStartTime = new MutableDateTime(oldStartTimeCal);
        MutableDateTime oldEndTime = new MutableDateTime(oldEndTimeCal);
        TimedTask myOldTimedTask = new TimedTask(oldStartTime,
                                                 oldEndTime,
                                                 "MyOldTask");

        // n.b. JAN = 0, ..., DEC = 11
        Calendar newStartTimeCal = new GregorianCalendar(2013, 11, 31, 14, 0);
        Calendar newEndTimeCal = new GregorianCalendar(2013, 11, 31, 15, 0);
        MutableDateTime newStartTime = new MutableDateTime(newStartTimeCal);
        MutableDateTime newEndTime = new MutableDateTime(newEndTimeCal);
        TimedTask expectedNewTimedTask = new TimedTask(newStartTime,
                                                       newEndTime,
                                                       "MyNewTask");

        JournalManager journalManager = new TemporaryJournalManager(); // Empty; NO
                                                              // TASKS.
        journalManager.addTask(myOldTimedTask);

        EditCommand editCmd = new EditCommand("MyOldTask", expectedNewTimedTask);
        String commandStatus = editCmd.execute(journalManager);

        // This works since, if we have only one Task, then this will be at 0.
        // And if the EditCommand worked, it will replace the task..
        assertEquals("The task was not changed correctly in the JournalManager",
                     "MyNewTask", journalManager.getAllTasks().get(0).getDescription());
        
        // Check to see that the correct output is given to the user
        assertEquals("Output produced does not match expected output",
                     "The following task is edited\n"+
                     "[10/11/2013] [14:00 - 15:00] MyOldTask\n"+
                     "To\n"+
                     "[31/12/2013] [14:00 - 15:00] MyNewTask\n",
                     editCmd.getOutput());
    }



    @Test
    public void testStrictSyntaxEditCommandWithInputCanExecute () {
        // Edit command, getting input from inputLine()
        // e.g. "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";

        Calendar oldStartTimeCal = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar oldEndTimeCal = new GregorianCalendar(2013, 10, 10, 15, 0);
        MutableDateTime oldStartTime = new MutableDateTime(oldStartTimeCal);
        MutableDateTime oldEndTime = new MutableDateTime(oldEndTimeCal);
        TimedTask myOldTimedTask = new TimedTask(oldStartTime,
                                                 oldEndTime,
                                                 "MyOldTask");

        JournalManager journalManager = new TemporaryJournalManager(); // Empty; NO
                                                                       // TASKS.
        journalManager.addTask(myOldTimedTask);

        EditCommand editCmd = new EditCommand("MyOldTask");
        String executionStatus = editCmd.execute(journalManager);
        String feedback = editCmd.getOutput();
        
        assertEquals("Execution status not correctly updated to NeedNewTask (Phase 1)",
                     "NeedNewTask", executionStatus);
        
        assertEquals("Returned feedback does not match expected feedback (Phase 1)",
                     "The following Task will be edited.\n" +
                     "[10/11/2013] [14:00 - 15:00] MyOldTask\n"+
                     "Please enter a new task.\n",
                     feedback);
        
        SuggestionManager sManager = new SuggestionManager();
        String[] tokens = "MyNewTask 31/12/13 0000 31/12/13 2359".split(" ");
        Task newTask = sManager.parseTask(tokens);
        
        executionStatus = editCmd.thirdExecute(newTask);
        feedback = editCmd.getOutput();
        
        assertEquals("Execution status not correctly updated to Success (Phase 2)",
                     "Success", executionStatus);
        
        assertEquals("Returned feedback does not match expected feedback (Phase 2)",
                     "The following task is edited\n"+
                     "[10/11/2013] [14:00 - 15:00] MyOldTask\n"+
                     "To\n"+
                     "[31/12/2013] [00:00 - 23:59] MyNewTask\n",
                     feedback);
        
        assertEquals("The task was not changed correctly in the JournalManager",
                     "MyNewTask", journalManager.getAllTasks().get(0).getDescription());
        
    }

}
