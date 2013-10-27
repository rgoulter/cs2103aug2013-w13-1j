
package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.JournalManager;
import jim.journal.SearchCommand;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.junit.Test;






public class SearchUnitTests {

    @Test
    public void testStrictSyntaxSearchCommandCanParse () {
        String[] arguments = {"search", "testing"};

        SuggestionManager sManager = new SuggestionManager();
        Command parsedSearchCommand = sManager.parseCommand(arguments);

        assertNotNull(parsedSearchCommand);
        assertTrue(parsedSearchCommand instanceof SearchCommand);
    }



    @Test
    public void testStrictSyntaxSearchCommandNoMatchCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("CS2101 Sectional");
        addCmd2.execute(jManager);

        SearchCommand searchCmd = new SearchCommand("Tutorial");
        searchCmd.execute(jManager);

        String output = searchCmd.getOutput();
        boolean result = false;

        if (output.equals("Search term 'Tutorial' was not found.\n")) {
            result = true;
        }
        assertTrue("Search for absent item has failed", result);
    }



    @Test
    public void testStrictSyntaxSearchCommandHasMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
        addCmd1.execute(jManager);

        SearchCommand searchCmd1 = new SearchCommand("Lecture");
        searchCmd1.execute(jManager);

        String output = searchCmd1.getOutput();


        assertEquals("Matches for 'Lecture':\nCS2103 Lecture\n\n", output); 
            


        AddCommand addCmd2 = new AddCommand("CS2103 Tutorial");
        addCmd2.execute(jManager);

        SearchCommand searchCmd2 = new SearchCommand("Lecture");
        searchCmd2.execute(jManager);

        output = searchCmd2.getOutput();
        assertEquals("Matches for 'Lecture':\nCS2103 Lecture\n\n", output);


        AddCommand addCmd3 = new AddCommand("CS2101 Lecture");
        addCmd3.execute(jManager);

        SearchCommand searchCmd3 = new SearchCommand("Lecture");
        searchCmd3.execute(jManager);

        output = searchCmd3.getOutput();
        assertEquals("Matches for 'Lecture':\nCS2103 Lecture\nCS2101 Lecture\n\n", output);

    }
    @Test
    public void testDateSearchCommandHasOneMatchesCanExecute () {
        JournalManager jManager = new TemporaryJournalManager();
        String startTime = "2013-10-12T12:00:00.000+08:00";
        String endTime = "2013-10-12T13:00:00.000+08:00";
        String description = "do a TimedTask";
        TimedTask testTask = new TimedTask(startTime,endTime,description);
        AddCommand addCmd1 = new AddCommand(testTask);
        addCmd1.execute(jManager);
        
        SearchCommand searchCmd = new SearchCommand(testTask.getStartTime());
        searchCmd.execute(jManager);

        String output = searchCmd.getOutput();

        assertEquals("Matches for 'null':\n" +
                     "[12/10/2013] [12:00 - 13:00] do a TimedTask\n" + "\n",
                     output);

    }

}
