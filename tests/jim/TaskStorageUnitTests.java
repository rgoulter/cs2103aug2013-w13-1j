package jim;

import static org.junit.Assert.*;

import java.util.ArrayList;

import jim.journal.DeadlineTask;
import jim.journal.FloatingTask;
import jim.journal.Task;
import jim.journal.TaskStorage;
import jim.journal.TimedTask;

import org.junit.Test;


public class TaskStorageUnitTests {

    @Test
    public void TaskStorageCanGetFloatingTask() {
        TaskStorage TS = new TaskStorage("test1.txt");
        FloatingTask FT = new FloatingTask("do a floatingTask");
        try {
            TS.recordNewTask(FT);
            ArrayList<Task> allTasks = TS.getAllTasks();
            assertNotNull("TaskStorage cannot store well",allTasks);
            assertNotNull("FloatingTask cannot be null", allTasks.get(allTasks.size()-1));
            boolean result = false;
            if (allTasks.get(allTasks.size()-1) instanceof FloatingTask){
                result = true;
            }
            assertTrue("The Task is a floatingTask", result);
            assertEquals("do a floatingTask", allTasks.get(allTasks.size()-1).getDescription());
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    @Test
    public void TaskStorageCanGetTimedTask(){
        TaskStorage TS = new TaskStorage("test2.txt");
        String startTime = "2013-10-12T12:00:00.000+08:00";
        String endTime = "2013-10-12T13:00:00.000+08:00";
        String description = "do a TimedTask";
        TimedTask TT = new TimedTask(startTime,endTime,description);
        TimedTask TimedTaskFromStorage = null;
        assertNotNull("the new created task cannot be null", TT);
        try {
            TS.recordNewTask(TT);
            ArrayList<Task> allTasks = TS.getAllTasks();
            assertNotNull("TaskStorage cannot store well",allTasks);
            assertNotNull("TimedTask cannot be null", allTasks.get(0));
            boolean result = false;
            if (allTasks.get(allTasks.size()-1) instanceof TimedTask){
                result = true;
                TimedTaskFromStorage = (TimedTask)allTasks.get(allTasks.size()-1);
            }
            assertTrue("The Task is a TimedTask", result);
            assertEquals(description, allTasks.get(allTasks.size()-1).getDescription());
            assertEquals(TT,TimedTaskFromStorage);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void TaskStorageCanGetDeadlineTask(){
        TaskStorage TS = new TaskStorage("test3.txt");
        String endTime = "2013-10-12T13:00:00.000+08:00";
        String description = "do a DeadlineTask";
        DeadlineTask DT = new DeadlineTask(endTime,description);
        DeadlineTask DeadlineTaskFromStorage = null;
        assertNotNull("the new created task cannot be null", DT);
        assertEquals("the DT description",description, DT.getDescription());
        try {
            TS.recordNewTask(DT);
            ArrayList<Task> allTasks = TS.getAllTasks();
            assertNotNull("TaskStorage cannot store well",allTasks);
            assertNotNull("DeadlineTask cannot be null", allTasks.get(allTasks.size()-1));
            boolean result = false;
            if (allTasks.get(allTasks.size()-1) instanceof DeadlineTask){
                result = true;
                DeadlineTaskFromStorage = (DeadlineTask)allTasks.get(allTasks.size()-1);
            }
            assertTrue("The Task is a DeadlineTask", result);
            assertEquals(DT,DeadlineTaskFromStorage);
            assertEquals(description, DeadlineTaskFromStorage.getDescription());
            assertEquals(DT,DeadlineTaskFromStorage);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
