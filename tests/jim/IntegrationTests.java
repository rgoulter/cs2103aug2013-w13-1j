package jim;

import static org.junit.Assert.*;
import jim.journal.AddCommand;
import jim.journal.CompleteCommand;
import jim.journal.DisplayCommand;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.RemoveCommand;
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
    
    
    // Runs ALL five major commands, but only tests the final output
    @Test
    public void basicAllEncompassingTest() {
        SuggestionManager sManager = new SuggestionManager();
        TemporaryJournalManager jManager = new TemporaryJournalManager();
        
        AddCommand addCmd1 = new AddCommand(new FloatingTask("Item the First!"));
        AddCommand addCmd2 = new AddCommand(new FloatingTask("Item the Second!"));
        addCmd1.execute(jManager);
        addCmd2.execute(jManager);
        
        EditCommand editCmd = new EditCommand("Item");
        editCmd.execute(jManager);
        editCmd.secondExecute("1");
        editCmd.thirdExecute(new FloatingTask("The Second!"));
        
        RemoveCommand remCmd = new RemoveCommand("First!");
        remCmd.execute(jManager);
        remCmd.secondExecute("0");
        
        CompleteCommand completeCmd = new CompleteCommand("The");
        completeCmd.execute(jManager);
        completeCmd.secondExecute("0");
        
        DisplayCommand dispCmd = new DisplayCommand();
        dispCmd.execute(jManager);
        
        String displayOutput = dispCmd.getOutput();
        assertEquals(displayOutput, "[DONE] The Second!\n");
    }

}
