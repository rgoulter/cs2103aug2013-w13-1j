package jim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.FloatingTask;
import jim.journal.JournalManager;
import jim.journal.Task;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.journal.UndoCommand;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;

public class UndoUnitTests {

	   @Test
	    public void testFloatingTaskUndoCommand () {
	        // Strict syntax for "add" command:
	        // add <words describing event>

	        String description = "CS2103 Lecture";

	        AddCommand addCmd = new AddCommand(description);
	        JournalManager journalManager = new TemporaryJournalManager(); 
	                                           
	        addCmd.execute(journalManager);

	        Task expectedTask = new FloatingTask(description);
	        List<Task> expectedList = new ArrayList<Task>();
	        expectedList.add(expectedTask);
	        
	        UndoCommand undoCmd = new UndoCommand();
	        undoCmd.execute(journalManager);
	        expectedList.remove(expectedTask);
	        
	        assertEquals(expectedList,journalManager.getAllTasks());
	    }

}
