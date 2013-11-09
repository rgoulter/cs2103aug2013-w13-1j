//@author A0096790N
package jim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.JournalManager;
import jim.journal.Task;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;


/* GLOSSARY
 * Editing happens in up to three phases: 1) Search Phase, 2) Select Phase, 3) Edit Phase
 * In Phase 1, the user enters the name of a task, and a search is performed
 * In Phase 2, if the input is ambiguous, allow the user to make a selection
 * In Phase 3, the user enters new text to replace that of the selected task
 */




public class EditUnitTests {

    // Tests Parsing
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

        String testCommand = "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";
        String[] testCommandWords = testCommand.split(" ");

        SuggestionManager suggestionManager = new SuggestionManager();
        Command parsedEditCmd = suggestionManager.parseCommand(testCommandWords);

        assertNotNull(parsedEditCmd);
        assertTrue(parsedEditCmd instanceof EditCommand);
    }


    // Tests Only Phase 3
    @Test
    public void testStrictSyntaxEditCommandOnePartCanExecute () {
        Configuration cManager = Configuration.getConfiguration();
        String dSeparator = cManager.getDateSeparator();
        String tSeparator = cManager.getTimeSeparator();
        
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
                     "[10" + dSeparator + "11" + dSeparator + "2013] " + 
                     "[14" + tSeparator + "00 - 15" + tSeparator + "00] MyOldTask\n" +
                     "To\n"+
                     "[31" + dSeparator + "12" + dSeparator + "2013] " + 
                     "[14" + tSeparator + "00 - 15" + tSeparator + "00] MyNewTask\n",
                     editCmd.getOutput());
    }


    // Tests Only Phase 1 and Phase 3
    @Test
    public void testStrictSyntaxEditCommandTwoPartsCanExecute () {
        // Edit command, getting input from inputLine()
        // e.g. "edit MyOldTask to MyNewTask 31/12/13 0000 31/12/13 2359";

        Configuration cManager = Configuration.getConfiguration();
        String dSeparator = cManager.getDateSeparator();
        String tSeparator = cManager.getTimeSeparator();
        
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
        
        assertEquals("Execution status not correctly updated to NeedNewTask (Part 1)",
                     "NeedNewTask", executionStatus);
        
        assertEquals("Returned feedback does not match expected feedback (Part 1)",
                     "The following Task will be edited.\n" +
                     "[10" + dSeparator + "11" + dSeparator + "2013] " + 
                     "[14" + tSeparator + "00 - 15" + tSeparator + "00] MyOldTask\n"+
                     "Please enter a new task.\n",
                     feedback);
        
        SuggestionManager sManager = new SuggestionManager();
        String[] tokens = "MyNewTask 31/12/13 0000 31/12/13 2359".split(" ");
        Task newTask = sManager.parseTask(tokens);
        
        executionStatus = editCmd.thirdExecute(newTask);
        feedback = editCmd.getOutput();
        
        assertEquals("Execution status not correctly updated to Success (Part 2)",
                     "Success", executionStatus);
        
        assertEquals("Returned feedback does not match expected feedback (Part 2)",
                     "The following task is edited\n"+
                     "[10" + dSeparator + "11" + dSeparator + "2013] " + 
                     "[14" + tSeparator + "00 - 15" + tSeparator + "00] MyOldTask\n"+
                     "To\n"+
                     "[31" + dSeparator + "12" + dSeparator + "2013] " + 
                     "[00" + tSeparator + "00 - 23" + tSeparator + "59] MyNewTask\n",
                     feedback);
        
        assertEquals("The task was not changed correctly in the JournalManager",
                     "MyNewTask", journalManager.getAllTasks().get(0).getDescription());
        
    }

    
    // Tests Phases 1, 2 and 3
    @Test
    public void testStrictSyntaxEditCommandThreePartsCanExecute() {
        JournalManager jManager = new TemporaryJournalManager();
        jManager.addTask(new FloatingTask("Testing Item"));
        jManager.addTask(new FloatingTask("Placeholder Item"));
        
        EditCommand editCmd = new EditCommand("Item");
        String executionStatus = editCmd.execute(jManager);
        String commandOutput = editCmd.getOutput();
        
        assertEquals("Execution Status not set to 'Pending'",
                     executionStatus, "Pending");
        assertEquals("Edit Command did not return expected output",
                     "Type in just the index of tasks you wish to process.\n" +
                     "0, Testing Item\n" + 
                     "1, Placeholder Item\n",
                     commandOutput);
        
        executionStatus = editCmd.secondExecute("0");
        commandOutput = editCmd.getOutput();
        
        assertEquals("Execution Status not set to 'NeedNewTask'",
                     executionStatus, "NeedNewTask");
        assertEquals("Edit Command did not return expected output",
                     "The following Task will be edited.\n" +
                     "Testing Item\n" +
                     "Please enter a new task.\n",
                     commandOutput);

        executionStatus = editCmd.thirdExecute(new FloatingTask("Testing Edit"));
        commandOutput = editCmd.getOutput();
        
        assertEquals("Execution Status not set to 'Success'",
                     executionStatus, "Success");
        assertEquals("Edit Command did not return expected output",
                     "The following task is edited\n" + 
                     "Testing Item\nTo\nTesting Edit\n", commandOutput);
        assertEquals("The task was not changed correctly in the JournalManager",
                     "Testing Edit", jManager.getAllTasks().get(1).getDescription());
        
    }
    @Test
    public void testDateEditCommandHasOneMatchesCanExecute () {
        Configuration cManager = Configuration.getConfiguration();
        String dSeparator = cManager.getDateSeparator();
        String tSeparator = cManager.getTimeSeparator();
        
        JournalManager jManager = new TemporaryJournalManager();
        String startTime = "2013-10-12T12:00:00.000+08:00";
        String endTime = "2013-10-12T13:00:00.000+08:00";
        String description = "do a TimedTask";
        TimedTask testTask = new TimedTask(startTime,endTime,description);
        AddCommand addCmd1 = new AddCommand(testTask);
        addCmd1.execute(jManager);
        
        EditCommand editCmd = new EditCommand(testTask.getStartTime());
        editCmd.execute(jManager);

        String output = editCmd.getOutput();

        assertEquals("The following Task will be edited.\n" + 
                     "[12" + dSeparator + "10" + dSeparator + "2013] " + 
                     "[12" + tSeparator + "00 - 13" + tSeparator + "00] do a TimedTask\n" + 
                     "Please enter a new task.\n",
                     output);

    }
    
}
