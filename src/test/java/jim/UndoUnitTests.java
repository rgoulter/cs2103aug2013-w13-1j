//@author A0097081B
package jim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jim.journal.AddCommand;
import jim.journal.CompleteCommand;
import jim.journal.EditCommand;
import jim.journal.Command;
import jim.journal.FloatingTask;
import jim.journal.JournalManager;
import jim.journal.RemoveCommand;
import jim.journal.Task;
import jim.journal.TemporaryJournalManager;
import jim.journal.TimedTask;
import jim.journal.UncompleteCommand;
import jim.journal.UndoCommand;

import org.joda.time.MutableDateTime;
import org.junit.Test;

public class UndoUnitTests {

	   @Test
	    public void testAddSingleFloatingTaskUndoCommand () {
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
	        
	        try {
	            assertEquals(expectedList,journalManager.getAllTasks());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	   
	   @Test
	    public void testAddMultipleFloatingTaskUndoCommand () {
	        // Strict syntax for "add" command:
	        // add <words describing event>
	
	        String description = "CS2103 Lecture";
	
	        AddCommand addCmd = new AddCommand(description);
	        JournalManager journalManager = new TemporaryJournalManager(); 	           
	        for (int i = 0; i < 4; i++) {
	            addCmd.execute(journalManager);
	        } 
	        Task expectedTask = new FloatingTask(description);
	        List<Task> expectedList = new ArrayList<Task>();
	        for (int i = 0; i < 4; i++) {
		        expectedList.add(expectedTask);
	        } 
	        UndoCommand undoCmd = new UndoCommand();
	        for (int i = 0; i < 2; i++) {
	            undoCmd.execute(journalManager);
	            expectedList.remove(expectedTask);
	        }     
	        try {
	           assertEquals(expectedList,journalManager.getAllTasks());
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	    }
	   
	   /* How Edit Command Works:
	    * Edit requires three phases: 1) Search 2) Selection 3) Edit
	    * In Phase 1, the user enters the description of a task, and a search is performed
	    * In Phase 2, user will make a selection when there are multiple search results
	    * In Phase 3, the user enters new text to replace that of the selected task
	    */
	   
	   @Test
	   public void testStrictSyntaxUndoAfterEditCommand () {
	       Configuration cManager = Configuration.getConfiguration();
	       String dSeparator = cManager.getDateSeparator();
	       String tSeparator = cManager.getTimeSeparator();
	
		   String startTime = "2013-10-12T12:00:00.000+08:00";
	       String endTime = "2013-10-12T13:00:00.000+08:00";
	       String oldDes = "This is an old task.";
	       TimedTask oldTestTask = new TimedTask(startTime,endTime,oldDes);
	       
		   startTime = "2013-11-12T12:00:00.000+08:00";
	       endTime = "2013-11-12T15:00:00.000+08:00";
	       String newDes = "This is a new task.";
	       TimedTask newTestTask = new TimedTask(startTime,endTime,newDes);
	
	       JournalManager journalManager = new TemporaryJournalManager();
	                                               
	       try {
	           journalManager.addTask(oldTestTask);
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	
	       EditCommand editCmd = new EditCommand(oldDes, newTestTask);
	       String commandStatus = editCmd.execute(journalManager);
	       
	       // Check to see that the correct output is given to the user
	       assertEquals("The following task is edited\n"+
	                    "[12" + dSeparator + "10" + dSeparator + "13] " + 
	                    "[12" + tSeparator + "00] - " + "[12" + dSeparator + "10" + dSeparator + "13] " + 
	                    "[13" + tSeparator + "00] This is an old task.\n" +
	                    "To\n"+
	                    "[12" + dSeparator + "11" + dSeparator + "13] " + 
	                    "[12" + tSeparator + "00] - " + "[12" + dSeparator + "11" + dSeparator + "13] " + 
	                    "[15" + tSeparator + "00] This is a new task.\n",
	                    editCmd.getOutput());
		   		
	       UndoCommand undoCmd = new UndoCommand();
	       commandStatus = undoCmd.execute(journalManager);
	       assertEquals("Success", commandStatus);
	   }

	   @Test
	   public void testRemoveSingleFloatingTaskAndUndo () {
		   // Strict syntax for "add" command:
		   // add <words describing event>

		   String description = "CS2103 Lecture";
		   AddCommand addCmd = new AddCommand(description);
		   JournalManager journalManager = new TemporaryJournalManager();                                   
		   addCmd.execute(journalManager);
		   Task expectedTask = new FloatingTask(description);
		   List<Task> expectedList = new ArrayList<Task>();
		   expectedList.add(expectedTask);
		   RemoveCommand rmvCmd = new RemoveCommand(description);
		   rmvCmd.execute(journalManager);
		   UndoCommand undoCmd = new UndoCommand();
		   undoCmd.execute(journalManager);
		   try {
			   assertEquals(expectedList,journalManager.getAllTasks());
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	   
	    @Test
	    public void testStrictSyntaxUndoCommandAfterCompleteCommand () {
	        JournalManager journalManager = new TemporaryJournalManager();
	        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
	        addCmd1.execute(journalManager);
	        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
	        completeCmd1.execute(journalManager);
	        UndoCommand undoCmd = new UndoCommand();
	        undoCmd.execute(journalManager);
	        String output = undoCmd.getOutput();
	        assertEquals("Undo Successful\n", output);
	    }
	    
	    @Test
	    public void testStrictSyntaxUndoCommandAfterUncompleteCommand () {
	        JournalManager journalManager = new TemporaryJournalManager();
	        AddCommand addCmd1 = new AddCommand("CS2103 Lecture");
	        addCmd1.execute(journalManager);
	        CompleteCommand completeCmd1 = new CompleteCommand("Lecture");
	        completeCmd1.execute(journalManager);
	        UncompleteCommand uncompleteCmd1 = new UncompleteCommand("Lecture");
	        uncompleteCmd1.execute(journalManager);
	        UndoCommand undoCmd = new UndoCommand();
	        undoCmd.execute(journalManager);
	        String output = undoCmd.getOutput();
	        assertEquals("Undo Successful\n", output);
	    }
}
