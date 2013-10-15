
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

        assertEquals("No tasks was not matched.\n", output);
    }



    @Test
    public void testStrictSyntaxCompleteCommandHasMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
        completeCmd1.execute(jManager);

        String output = completeCmd1.getOutput();

        assertEquals("Completed Task: CS2103 Lecture\n", output);
    }



    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("Lecture");
        addCmd2.execute(jManager);

        CompleteCommand completeCmd = new CompleteCommand("Lecture");
        completeCmd.execute(jManager);

        String output = completeCmd.getOutput();

        assertEquals("Completed Task: CS2103 Lecture\nCompleted Task: Lecture\n",
                     output);

    }



    @Test
    public void testStrictSyntaxCompleteCommandHasMultipleMatchesButCompletedCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
        completeCmd1.execute(jManager);
        CompleteCommand completeCmd2 = new CompleteCommand("Lecture");
        completeCmd2.execute(jManager);

        String output = completeCmd2.getOutput();

        assertEquals("Task CS2103 Lecture has already been marked as completed.\n",
                     output);

    }

}
