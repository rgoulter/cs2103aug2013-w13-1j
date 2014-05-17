//@author A0097081B
package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.CompleteCommand;
import jim.journal.JournalManager;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.journal.UncompleteCommand;

import org.junit.Test;

public class UncompleteUnitTests {

    @Test
    public void testStrictSyntaxUncompleteCommandNoMatchCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2101 Sectional");
        addCmd2.execute(jManager);

        UncompleteCommand uncompleteCmd = new UncompleteCommand("Tutorial");
        uncompleteCmd.execute(jManager);

        String output = uncompleteCmd.getOutput();

        assertEquals("Description was not matched.\n", output);
    }


    @Test
    public void testStrictSyntaxUncompleteCommandHasMatchNotExecuted () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        UncompleteCommand uncompleteCmd1 = new UncompleteCommand("Lecture");
        uncompleteCmd1.execute(jManager);

        String output = uncompleteCmd1.getOutput();

        assertEquals("Task " +
				     "CS2103 Lecture has not been completed.\n", output);
    }

    @Test
    public void testStrictSyntaxUncompleteCommandHasMatchCanExecuteUncomplete () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
        completeCmd1.execute(jManager);
        
        UncompleteCommand uncompleteCmd1 = new UncompleteCommand("Lecture");
        uncompleteCmd1.execute(jManager);

        String output = uncompleteCmd1.getOutput();

        assertEquals("Uncompleted Task: " +
				     "CS2103 Lecture\n", output);
    }

    @Test
    public void testStrictSyntaxUncompleteCommandHasMultipleMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        UncompleteCommand uncompleteCmd = new UncompleteCommand("Lecture");
        uncompleteCmd.execute(jManager);

        String output = uncompleteCmd.getOutput();

        assertEquals("Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
				     "0, CS2103 Lecture\n" +
				     "1, CS2010 Lecture\n",
                     output);

    }

    @Test
    public void testStrictSyntaxUncompleteCommandHasMultipleMatchesAndUncompletedCannotExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        UncompleteCommand uncompleteCmd1 = new UncompleteCommand("Lecture");
        String executionStatus = uncompleteCmd1.execute(jManager);
        String output = uncompleteCmd1.getOutput();
        
        assertEquals("Output generated does not match expected output (Phase 1)",
                     "Type in just the index of tasks you wish to process. " +
                     "Please seperate them by ','\n" +
                     "0, CS2103 Lecture\n" +
                     "1, CS2010 Lecture\n",
                     output);
        
        assertEquals("Execution status does not match expected status (Phase 1)",
                     "Pending", executionStatus);
        
        executionStatus = uncompleteCmd1.secondExecute("0");
        output = uncompleteCmd1.getOutput();
        
        assertEquals("Output generated does not match expected output (Phase 2)",
                     "Task CS2103 Lecture has not been completed.\n",
                     output);
        
        assertEquals("Execution status does not match expected status (Phase 2)",
                     "Success", executionStatus);
    }
    
    @Test
    public void testStrictSyntaxUncompleteCommandHasMultipleMatchesAndUncompletedCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
        String executionStatus = completeCmd1.execute(jManager);
        String output = completeCmd1.getOutput();
        
        assertEquals("Output generated does not match expected output (Phase 1)",
                     "Type in just the index of tasks you wish to process. " +
                     "Please seperate them by ','\n" +
                     "0, CS2103 Lecture\n" +
                     "1, CS2010 Lecture\n",
                     output);
        
        assertEquals("Execution status does not match expected status (Phase 1)",
                     "Pending", executionStatus);
        
        executionStatus = completeCmd1.secondExecute("0");
        output = completeCmd1.getOutput();
        
        assertEquals("Output generated does not match expected output (Phase 2)",
                     "Completed Task: CS2103 Lecture\n",
                     output);
        
        assertEquals("Execution status does not match expected status (Phase 2)",
                     "Success", executionStatus);

        UncompleteCommand uncompleteCmd1 = new UncompleteCommand("Lecture");
        executionStatus = uncompleteCmd1.execute(jManager);
        output = uncompleteCmd1.getOutput();

        assertEquals("Output generated does not match expected output (Phase 1)",
        			 "Type in just the index of tasks you wish to process. " +
    			   	 "Please seperate them by ','\n" +
    				 "0, CS2103 Lecture\n" +
    				 "1, CS2010 Lecture\n",
    				 output);

        assertEquals("Execution status does not match expected status (Phase 1)",
        			 "Pending", executionStatus);

        executionStatus = uncompleteCmd1.secondExecute("0");
        output = uncompleteCmd1.getOutput();

        assertEquals("Output generated does not match expected output (Phase 2)",
        			 "Uncompleted Task: CS2103 Lecture\n",
        			  output);

        assertEquals("Execution status does not match expected status (Phase 2)",
        			 "Success", executionStatus);
    }
    
    @Test
    public void testDateUncompleteCommandHasOneMatchCanExecute () {
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
        
        CompleteCommand completeCmd = new CompleteCommand(testTask.getStartTime());
        completeCmd.execute(jManager);
        
        UncompleteCommand uncompleteCmd = new UncompleteCommand(testTask.getStartTime());
        uncompleteCmd.execute(jManager);
        
        String output = uncompleteCmd.getOutput();

        assertEquals("Uncompleted Task: " +
                     "[12" + dSeparator + "10" + dSeparator + "13] " + 
                     "[12" + tSeparator + "00] - " +
                     "[12" + dSeparator + "10" + dSeparator + "13] " +
                     "[13" + tSeparator + "00] do a TimedTask\n",
                     output);
    }

}
