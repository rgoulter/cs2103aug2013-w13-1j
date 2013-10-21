
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

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture"){

            protected String inputLine () {
                return "0";
            }
        };
        completeCmd1.execute(jManager);

        String output = completeCmd1.getOutput();

        assertEquals("Type in just the index of tasks you wish to process. Please seperate them by ','" +
				     "0, CS2103 Lecture\n", output);
    }



    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("Lecture");
        addCmd2.execute(jManager);

        CompleteCommand completeCmd = new CompleteCommand("Lecture") {

            protected String inputLine () {
                return "0";
            }
        };
        completeCmd.execute(jManager);

        String output = completeCmd.getOutput();

        assertEquals("Give the index of the task you wish to remove.\n" +
				     "0, CS2103 Lecture\n" +
				     "1, Lecture\n",
                     output);

    }



    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesButCompletedCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture") {

            protected String inputLine () {
                return "0";
            }
        };
        completeCmd1.execute(jManager);
        CompleteCommand completeCmd2 = new CompleteCommand("Lecture") {

            protected String inputLine () {
                return "0";
            }
        };
        completeCmd2.execute(jManager);

        String output = completeCmd2.getOutput();

        assertEquals("Give the index of the task you wish to remove.\n" +
				     "0, CS2103 Lecture\n" +
				     "Task CS2103 Lecture has already been marked as completed.\n",
                     output);

    }

}
