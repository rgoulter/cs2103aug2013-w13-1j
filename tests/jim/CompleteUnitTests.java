
package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.CompleteCommand;
import jim.journal.JournalManager;
import jim.journal.TemporaryJournalManager;

import org.junit.Test;






public class CompleteUnitTests {

    @Test
    public void testStrictSyntaxCompleteCommandNoMatchCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
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
    public void testStrictSyntaxCompleteCommandHasMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
        completeCmd1.execute(jManager);

        String output = completeCmd1.getOutput();

        assertEquals("Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
				     "0, CS2103 Lecture\n", output);
    }


    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2010 Lecture");
        addCmd2.execute(jManager);

        CompleteCommand completeCmd = new CompleteCommand("Lecture");
        completeCmd.execute(jManager);

        String output = completeCmd.getOutput();

        assertEquals("Type in just the index of tasks you wish to process. Please seperate them by ','\n" +
				     "0, CS2103 Lecture\n" +
				     "1, CS2010 Lecture\n",
                     output);

    }



    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesButCompletedCanExecute () {
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

    }

}
