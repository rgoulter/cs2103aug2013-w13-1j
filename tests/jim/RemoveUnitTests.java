
package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.JournalManager;
import jim.journal.RemoveCommand;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.junit.Test;






public class RemoveUnitTests {

    @Test
    public void testStrictSyntaxRemoveCommandCanParse () {
        String[] arguments = {"remove", "testing"};

        SuggestionManager sManager = new SuggestionManager();
        Command parsedRemoveCommand = sManager.parseCommand(arguments);

        assertNotNull(parsedRemoveCommand);
        assertTrue(parsedRemoveCommand instanceof RemoveCommand);
    }



    @Test
    public void testStrictSyntaxRemoveCommandNoMatchCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2101 Sectional");
        addCmd2.execute(jManager);

        RemoveCommand removeCmd = new RemoveCommand("CS2103 Tutorial");
        removeCmd.execute(jManager);

        String output = removeCmd.getOutput();

        assertEquals("Description was not matched.\n",output);
    }



    @Test
    public void testStrictSyntaxRemoveCommandHasMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        RemoveCommand removeCmd1 = new RemoveCommand("CS2103 Lecture");
        String executionStatus = removeCmd1.execute(jManager);
        String output = removeCmd1.getOutput();
        
        assertEquals("Execution status not correctly updated to Pending (Phase 1)",
                     "Pending", executionStatus);

        assertEquals("Output generated does not match expected output (Phase 1)",
                     "Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
				     "0, CS2103 Lecture\n", output);
        
        executionStatus = removeCmd1.secondExecute("0");
        output = removeCmd1.getOutput();
        
        assertEquals("Execution status not correctly updated to Success (Phase 2)",
                     "Success", executionStatus);

        assertEquals("Output generated does not match expected output (Phase 2)",
                     "Removed task: CS2103 Lecture\n", output);
        
        
    }



    @Test
    public void testStrictSyntaxRemoveCommandHasMultipleMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        RemoveCommand removeCmd2 = new RemoveCommand("Lecture");
        String executionStatus = removeCmd2.execute(jManager);
        String output = removeCmd2.getOutput();
        
        assertEquals("Execution status not correctly updated to Pending (Phase 1)",
                     "Pending", executionStatus);

        assertEquals("Output generated does not match expected output (Phase 1)",
                     "Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
                     "0, CS2103 Lecture\n" + 
                     "1, CS2010 Lecture\n", output);
        
        executionStatus = removeCmd2.secondExecute("1");
        output = removeCmd2.getOutput();
        
        assertEquals("Execution status not correctly updated to Success (Phase 2)",
                     "Success", executionStatus);

        assertEquals("Output generated does not match expected output (Phase 2)",
                     "Removed task: CS2010 Lecture\n", output);


    }
    @Test
    public void testStrictSyntaxRemoveCommandHasMultipleMatchesSecondInputNotMatchCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        RemoveCommand removeCmd2 = new RemoveCommand("Lecture");
        String executionStatus = removeCmd2.execute(jManager);
        String output = removeCmd2.getOutput();
        
        assertEquals("Execution status not correctly updated to Pending (Phase 1)",
                     "Pending", executionStatus);

        assertEquals("Output generated does not match expected output (Phase 1)",
                     "Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
                     "0, CS2103 Lecture\n" + 
                     "1, CS2010 Lecture\n", output);
        
        executionStatus = removeCmd2.secondExecute("2");
        output = removeCmd2.getOutput();
        
        assertEquals("Execution status not correctly updated to Success (Phase 2)",
                     "Pending", executionStatus);

    }
    @Test
    public void testStrictSyntaxRemoveCommandHasMultipleMatchesSecondInputNottMatchCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        RemoveCommand removeCmd2 = new RemoveCommand("Lecture");
        String executionStatus = removeCmd2.execute(jManager);
        String output = removeCmd2.getOutput();
        
        assertEquals("Execution status not correctly updated to Pending (Phase 1)",
                     "Pending", executionStatus);

        assertEquals("Output generated does not match expected output (Phase 1)",
                     "Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
                     "0, CS2103 Lecture\n" + 
                     "1, CS2010 Lecture\n", output);
        
        executionStatus = removeCmd2.secondExecute("ajsdlfj");
        output = removeCmd2.getOutput();
        
        assertEquals("Execution status not correctly updated to Success (Phase 2)",
                     "Pending", executionStatus);

    }
    @Test
    public void testDateRemoveCommandHasOneMatchesCanExecute () {
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
        
        RemoveCommand removeCmd = new RemoveCommand(testTask.getStartTime());
        removeCmd.execute(jManager);

        String output = removeCmd.getOutput();

        assertEquals("Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
                     "0, [12" + dSeparator + "10" + dSeparator + "2013] " + 
                     "[12" + tSeparator + "00 - 13" + tSeparator + "00] do a TimedTask\n",
                     output);

    }

}
