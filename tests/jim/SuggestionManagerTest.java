
package jim;

import static org.junit.Assert.*;
import jim.journal.Command;
import jim.journal.FloatingTask;
import jim.journal.RemoveCommand;
import jim.journal.Task;
import jim.suggestions.SuggestionManager;

import org.junit.Test;






public class SuggestionManagerTest {

//    @Test
//    public void testCanSearchJournalManagerForTasksByDescription () {
//        fail("not implemented");
//    }
    
    @Test
    public void testCanGrammarParseTask_TaskFloating () {
        String inputString = "A floating task description.";
        FloatingTask expectedTask = new FloatingTask(inputString);
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Task parsedTask = suggestionManager.parseTask(inputString.split(" "));
        
        assertTrue("Parsed task should be a FloatingTask.", parsedTask instanceof FloatingTask);
        
        FloatingTask parsedFloatingTask = (FloatingTask) parsedTask;
        assertEquals("Parsed description should be the same.",
                     inputString,
                     parsedFloatingTask.getDescription());
        assertEquals("Parsed description should be the same.",
                     expectedTask.getDescription(),
                     parsedFloatingTask.getDescription());
    }
}
