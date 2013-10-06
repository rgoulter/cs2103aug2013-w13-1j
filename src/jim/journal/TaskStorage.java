package jim.journal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
	private String TaskFormat = " Type: %1$s \n SDate&Time: %2$s \n EDate&Time: %3$s \n Description: %4$s \n Status: %5$s \n";
	
	//TODO link with the configuration to set the fileDirectory through journal manager.
	public TaskStorage(String fileDirectory){
		StorageFileDirectory = fileDirectory;
		StorageFile = new File(StorageFileDirectory);
	}
	
	
	/*
	 * Methods communicate with the journal manager.
	 * The changes to the file will be saved in each command.
	 */
	
	//So far, new task will append to the end of the file.
	public void recordNewTask(Task task) throws IOException{
		String TaskType;
		String StartTime = "";
		String EndTime = "";
		String Description;
		String Status;
		
		if (task instanceof TimedTask){
			TaskType = "TimedTask";
			StartTime = ((TimedTask) task).getStartTime().toString();
			EndTime = ((TimedTask) task).getEndTime().toString();
			
		}else{
			TaskType = "FloatingTask";
		}
		
		Description = task.getDescription();
		
		if (task.isCompleted()){
			Status = "Completed";
		}else{
			Status = "Not Completed";
		}
		
		filewriter = new FileWriter(StorageFile,true);
		bufferedwriter = new BufferedWriter(filewriter);
		
		bufferedwriter.write(String.format(TaskFormat,TaskType,StartTime,EndTime,Description,Status));
		
		filewriter.close();
		bufferedwriter.close();
	}
	
	public void removeTask(){
		//TODO 
	}
	
	//Both edit and markAscompleted method from journal manager.
	public void modifyTask(){
		//TODO
		
	}
	public ArrayList<Task> getAllTasks() throws Exception{
		ArrayList<Task> AllTasks = new ArrayList<Task>();
		String TaskType;
		String StartTime = "";
		String EndTime = "";
		String Description;
		String Status;
		Task currentTask = null;
		
		String crtLine;
		while ((crtLine = bufferedreader.readLine()) != null){
			
			TaskType = crtLine.substring(6);
			StartTime = bufferedreader.readLine().substring(12);  
			EndTime = bufferedreader.readLine().substring(12);
			Description = bufferedreader.readLine().substring(13);
			Status = bufferedreader.readLine().substring(8);
			
			//create Task object
			if (TaskType.equals("TimedTask")){
			//TODO modify to use the joda time.
				//currentTask = new TimedTask(StartTime, EndTime, Description);
			}else if (TaskType.equals("FloatingTask")){
				currentTask = new FloatingTask(Description);
			}else{
				throw new Exception ("Wrong format in the storage.");
			}
			
			//Completed or not
			if (currentTask!=null && Status.equals("Completed")){
				currentTask.markAsCompleted();
			}
			
			AllTasks.add(currentTask);
		}
		
		bufferedreader.close();
		return AllTasks;
	}
	
}
