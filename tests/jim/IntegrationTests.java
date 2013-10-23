package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.TemporaryJournalManager;
import jim.suggestions.SuggestionManager;

import org.junit.Test;


public class IntegrationTests {

    @Test
    // Tests add and display in a full running session of JIM
    public void basicIntegrationTest() {
        SuggestionManager sManager = new SuggestionManager();
        TemporaryJournalManager jManager = new TemporaryJournalManager();
        
        AddCommand addCmd1 = new AddCommand("hello");
        addCmd1.execute(jManager);
        AddCommand addCmd2 = new AddCommand("world");
        addCmd2.execute(jManager);
        
        String input = "display";
        String inputTokens[] = input.split(" ");
        jim.journal.Command command = sManager.parseCommand(inputTokens);
        
        command.execute(jManager);
        String feedback = command.getOutput();
        
        assertEquals(feedback, "hello\nworld\n");
    }
    
    
    @Test
    // Tests command execution status in a full running session of JIM
    public void basicStatusTest() {
        SuggestionManager sManager = new SuggestionManager();
        TemporaryJournalManager jManager = new TemporaryJournalManager();
        
        String input = "add hello";
        String inputTokens[] = input.split(" ");
        jim.journal.Command command = sManager.parseCommand(inputTokens);
        String executionStatus = command.execute(jManager);
        assertEquals(executionStatus, "Success");
        
        input = "display";
        String inputTokens2[] = input.split(" ");
        command = sManager.parseCommand(inputTokens2);
        
        command.execute(jManager);
        String feedback = command.getOutput();
        
        assertEquals(feedback, "hello\n");
    }
    
    

}
