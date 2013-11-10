package jim.journal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TaskStorage {
    private String StorageFileDirectory;
	private File StorageFile;
	private FileReader filereader;
	private BufferedReader bufferedreader;
	private FileWriter filewriter;
	private BufferedWriter bufferedwriter;
	private int READ_FROM_INDEX = 2;
	private String TypeFormat = "T:%1$s"; 
	private String StartDateFormat = "S:%1$s";
	private String EndDateFormat = "E:%1$s";
	private String DescriptionFormat = "D:%1$s";
	private String StatusFormat = "C:%1$s";
	private String STRING_INITIAL = "";
	private String TimedTaskSign = "TimedTask";
	private String DeadlineTaskSign = "DeadlineTask";
	private String FloatingTaskSign = "FloatingTask";
	private String CompletedSign = "Completed";
	private String NotCompletedSign = "Not Completed";
	private String ERRORINFILE = "Wrong format in the storage.";

	public TaskStorage(String fileDirectory){
		StorageFileDirectory = fileDirectory;
		StorageFile = new File(StorageFileDirectory);
	}
	
	
	/*
	 * Methods communicate with the journal manager.
	 * The changes to the file will be saved in each command.
	 */
	
	
	//New task will be recorded at the end of the storage file.
	public void recordNewTask(Task task) throws IOException{
	    String TaskType;
		String StartTime = STRING_INITIAL;
		String EndTime = STRING_INITIAL;
		String Description;
		String Status;
		if (task instanceof TimedTask){
		    TaskType = TimedTaskSign;
			StartTime = ((TimedTask) task).getStartTime().toString();
			EndTime = ((TimedTask) task).getEndTime().toString();
		}else if(task instanceof DeadlineTask){
		    TaskType = DeadlineTaskSign;
		    EndTime = ((DeadlineTask) task).getEndDate().toString();
		}else{
		    TaskType = FloatingTaskSign;
		}
		Description = task.getDescription();
		if (task.isCompleted()){
			Status = CompletedSign;
		}else{
			Status = NotCompletedSign;
		}
		filewriter = new FileWriter(StorageFile,true);
		bufferedwriter = new BufferedWriter(filewriter);
		bufferedwriter.write(String.format(TypeFormat,TaskType));
		bufferedwriter.newLine();
		bufferedwriter.write(String.format(StartDateFormat,StartTime));
        bufferedwriter.newLine();
        bufferedwriter.write(String.format(EndDateFormat,EndTime));
        bufferedwriter.newLine();
        bufferedwriter.write(String.format(DescriptionFormat,Description));
        bufferedwriter.newLine();
        bufferedwriter.write(String.format(StatusFormat, Status));
        bufferedwriter.newLine();
		bufferedwriter.close();
		filewriter.close();
	}
	
	public void writeToFile(ArrayList<Task> tasks) throws IOException{
	    filewriter = new FileWriter(StorageFile);
	    bufferedwriter = new BufferedWriter(filewriter);
	    for (Task t : tasks){
	        recordNewTask(t);
	    }
	}
	
	public ArrayList<Task> getAllTasks() throws Exception{
		ArrayList<Task> AllTasks = new ArrayList<Task>();
		String TaskType;
		String StartTime = STRING_INITIAL;
		String EndTime = STRING_INITIAL;
		String Description;
		String Status;
		String crtLine;
		Task currentTask = null;
		filereader = new FileReader(StorageFile);
		bufferedreader = new BufferedReader(filereader);
		
		while ((crtLine = bufferedreader.readLine()) != null){
			TaskType = crtLine.substring(READ_FROM_INDEX);
			StartTime = bufferedreader.readLine().substring(READ_FROM_INDEX);  
			EndTime = bufferedreader.readLine().substring(READ_FROM_INDEX);
			Description = bufferedreader.readLine().substring(READ_FROM_INDEX);
			Status = bufferedreader.readLine().substring(READ_FROM_INDEX);
			//create Task object
			if (TaskType.equals(TimedTaskSign)){
				currentTask = new TimedTask(StartTime, EndTime, Description);
			}else if (TaskType.equals(DeadlineTaskSign)){
			    currentTask = new DeadlineTask(EndTime, Description);
			}else if (TaskType.equals(FloatingTaskSign)){
			    currentTask = new FloatingTask(Description);
			}else{
				throw new Exception (ERRORINFILE);
			}
			//Completed or not
			if (currentTask!=null && Status.equals(CompletedSign)){
				currentTask.markAsCompleted();
			}
			AllTasks.add(currentTask);
		}
		bufferedreader.close();
		filereader.close();
		return AllTasks;
	}
	
}
