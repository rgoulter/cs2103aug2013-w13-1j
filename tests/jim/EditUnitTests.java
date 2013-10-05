package jim;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import jim.journal.Command;
import jim.journal.EditCommand;
import jim.journal.JournalManager;
import jim.journal.Task;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.junit.Test;

public class EditUnitTests {

    @Test
    public void testStrictSyntaxEditCommandCanParse() {
        // Strict syntax for "edit" command:
        // This edits a timed task
        // edit <description of old task> to <new description> <new start-date> <new start-time> <new end-date> <new end-time>

        Calendar startTime = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar endTime =   new GregorianCalendar(2013, 10, 10, 15, 0);
        TimedTask myOldTimedTask = new TimedTask(startTime, endTime, "MyOldTask");
        
        String testCommand = "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";
        String[] testCommandWords = testCommand.split(" "); 
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Command parsedEditCmd = suggestionManager.parseCommand(testCommandWords);
        
        assertNotNull(parsedEditCmd);
        assertTrue(parsedEditCmd instanceof EditCommand);
    }

    @Test
    public void testStrictSyntaxEditCommandCanExecute() {
        // Edit Command with the given change-to-task.

        // e.g.  "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";

        Calendar oldStartTime = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar oldEndTime =   new GregorianCalendar(2013, 10, 10, 15, 0);
        TimedTask myOldTimedTask = new TimedTask(oldStartTime, oldEndTime, "MyOldTask");
        
        // n.b. JAN = 0, ..., DEC = 11
        Calendar newStartTime = new GregorianCalendar(2013, 11, 31, 14, 0);
        Calendar newEndTime =   new GregorianCalendar(2013, 11, 31, 15, 0);
        TimedTask expectedNewTimedTask = new TimedTask(newStartTime, newEndTime, "MyNewTask");
        
        JournalManager journalManager = new JournalManager(); // Empty; NO TASKS.
        journalManager.addTask(myOldTimedTask);

        EditCommand editCmd = new EditCommand(Arrays.asList(new Task[]{myOldTimedTask}),
                                              expectedNewTimedTask);
        editCmd.execute(journalManager);
        
        // This works since, if we have only one Task, then this will be at 0.
        // And if the EditCommand worked, it will replace the task..
        assertEquals("MyNewTask", journalManager.getAllTasks().get(0).getDescription());
    }

    @Test
    public void testStrictSyntaxEditCommandWithInputCanExecute() {
        // Edit command, getting input from inputLine()

        // e.g.  "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";

        Calendar oldStartTime = new GregorianCalendar(2013, 10, 10, 14, 0);
        Calendar oldEndTime =   new GregorianCalendar(2013, 10, 10, 15, 0);
        TimedTask myOldTimedTask = new TimedTask(oldStartTime, oldEndTime, "MyOldTask");
        
        JournalManager journalManager = new JournalManager(); // Empty; NO TASKS.
        journalManager.addTask(myOldTimedTask);

        EditCommand editCmd = new EditCommand(Arrays.asList(new Task[]{myOldTimedTask})){
            protected String inputLine() {
                return "31/12/13 0000 31/12/13 2359 MyNewTask";
            }
        };
        editCmd.execute(journalManager);
        
        // This works since, if we have only one Task, then this will be at 0.
        // And if the EditCommand worked, it will replace the task..
        assertNotNull(journalManager.getAllTasks().get(0));
        assertEquals("MyNewTask", journalManager.getAllTasks().get(0).getDescription());
    }
    

}
