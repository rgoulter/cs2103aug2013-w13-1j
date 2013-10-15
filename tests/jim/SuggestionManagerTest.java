
package jim;

import static org.junit.Assert.*;
import jim.journal.Command;
import jim.journal.DeadlineTask;
import jim.journal.FloatingTask;
import jim.journal.RemoveCommand;
import jim.journal.Task;
import jim.journal.TimedTask;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
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
    
    @Test
    public void testCanGrammarParseTask_TimedTask_DateDescription () {
        // For grammar definition: <date> <description>
        String inputString = "31/12/13 Celebrate New Years Eve";
        MutableDateTime expectedDate = new MutableDateTime(2013, 12, 31, 0, 0, 0, 0);
        TimedTask expectedTask = new TimedTask(expectedDate,
                                               "Celebrate New Years Eve");
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Task parsedTask = suggestionManager.parseTask(inputString.split(" "));
        
        assertTrue("Parsed task should be a DeadlineTask.", parsedTask instanceof DeadlineTask);
        
        DeadlineTask parsedFloatingTask = (DeadlineTask) parsedTask;
        assertEquals("Parsed description should be the same.",
                     expectedTask.getDescription(),
                     parsedFloatingTask.getDescription());
        assertEquals("Parsed time should be the same.",
                     expectedTask.getEndTime(),
                     parsedFloatingTask.getEndDate());
    }
    
    @Test
    public void testCanGrammarParseTask_TimedTask_DateTimeTimeDescription () {
        // For grammar definition: <date> <time> <time> <description>
        
        String inputString = "31/12/13 2300 2359 Party until the new year";
        MutableDateTime expectedStartDateTime = new MutableDateTime(2013, 12, 31, 23, 0, 0, 0);
        MutableDateTime expectedEndDateTime = new MutableDateTime(2013, 12, 31, 23, 59, 0, 0);
        TimedTask expectedTask = new TimedTask(expectedStartDateTime,
                                               expectedEndDateTime,
                                               "Party until the new year");
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Task parsedTask = suggestionManager.parseTask(inputString.split(" "));
        
        assertTrue("Parsed task should be a TimedTask.", parsedTask instanceof TimedTask);
        
        TimedTask parsedFloatingTask = (TimedTask) parsedTask;
        assertEquals("Parsed description should be the same.",
                     expectedTask.getDescription(),
                     parsedFloatingTask.getDescription());
        assertEquals("Parsed start datetime should be the same.",
                     expectedTask.getStartTime(),
                     parsedFloatingTask.getStartTime());
        assertEquals("Parsed end datetime should be the same.",
                     expectedTask.getEndTime(),
                     parsedFloatingTask.getEndTime());
    }
    
    @Test
    public void testCanGrammarParseTask_TimedTask_DateTimeToTimeDescription () {
        // For grammar definition: <date> <time> 'to' <time> <description>
        
        String inputString = "31/12/13 2300 to 2359 Party until the new year";
        MutableDateTime expectedStartDateTime = new MutableDateTime(2013, 12, 31, 23, 0, 0, 0);
        MutableDateTime expectedEndDateTime = new MutableDateTime(2013, 12, 31, 23, 59, 0, 0);
        TimedTask expectedTask = new TimedTask(expectedStartDateTime,
                                               expectedEndDateTime,
                                               "Party until the new year");
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Task parsedTask = suggestionManager.parseTask(inputString.split(" "));
        
        assertTrue("Parsed task should be a TimedTask.", parsedTask instanceof TimedTask);
        
        TimedTask parsedFloatingTask = (TimedTask) parsedTask;
        assertEquals("Parsed description should be the same.",
                     expectedTask.getDescription(),
                     parsedFloatingTask.getDescription());
        assertEquals("Parsed start datetime should be the same.",
                     expectedTask.getStartTime(),
                     parsedFloatingTask.getStartTime());
        assertEquals("Parsed end datetime should be the same.",
                     expectedTask.getEndTime(),
                     parsedFloatingTask.getEndTime());
    }
    
    @Test
    public void testCanGrammarParseTask_TimedTask_DateTimeDateTimeDescription () {
        // For grammar definition: <date> <time> <date> <time> <description>
        
        String inputString = "01/01/14 0000 31/12/14 2359 Party all year round";
        MutableDateTime expectedStartDateTime = new MutableDateTime(2014, 1, 1, 0, 0, 0, 0);
        MutableDateTime expectedEndDateTime = new MutableDateTime(2014, 12, 31, 23, 59, 0, 0);
        TimedTask expectedTask = new TimedTask(expectedStartDateTime,
                                               expectedEndDateTime,
                                               "Party all year round");
        
        SuggestionManager suggestionManager = new SuggestionManager();
        Task parsedTask = suggestionManager.parseTask(inputString.split(" "));
        
        assertTrue("Parsed task should be a TimedTask.", parsedTask instanceof TimedTask);
        
        TimedTask parsedFloatingTask = (TimedTask) parsedTask;
        assertEquals("Parsed description should be the same.",
                     expectedTask.getDescription(),
                     parsedFloatingTask.getDescription());
        assertEquals("Parsed start datetime should be the same.",
                     expectedTask.getStartTime(),
                     parsedFloatingTask.getStartTime());
        assertEquals("Parsed end datetime should be the same.",
                     expectedTask.getEndTime(),
                     parsedFloatingTask.getEndTime());
    }
}
