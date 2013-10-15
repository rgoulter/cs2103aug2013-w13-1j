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
	private String TypeFormat = "T:%1$s"; 
	private String StartDateFormat = "S:%1$s";
	private String EndDateFormat = "E:%1$s";
	private String DescriptionFormat = "D:%1$s";
	private String StatusFormat = "C:%1$s";
	
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
		}else if(task instanceof DeadlineTask){
		    TaskType = "DeadlineTask";
		    EndTime = ((DeadlineTask) task).getEndDate().toString();
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
	/*
	public void removeTask(){
		//TODO 
	}
	
	//Both edit and markAscompleted method from journal manager.
	public void modifyTask(){
		//TODO
		
	}*/
	
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
		String StartTime = "";
		String EndTime = "";
		String Description;
		String Status;
		Task currentTask = null;
		filereader = new FileReader(StorageFile);
		bufferedreader = new BufferedReader(filereader);
		String crtLine;
		while ((crtLine = bufferedreader.readLine()) != null){
			
			TaskType = crtLine.substring(2);
			StartTime = bufferedreader.readLine().substring(2);  
			EndTime = bufferedreader.readLine().substring(2);
			Description = bufferedreader.readLine().substring(2);
			Status = bufferedreader.readLine().substring(2);
			
			//create Task object
			if (TaskType.equals("TimedTask")){
			
				currentTask = new TimedTask(StartTime, EndTime, Description);
				
			}else if (TaskType.equals("DeadlineTask")){
			    
			    currentTask = new DeadlineTask(EndTime, Description);
			    
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
		filereader.close();
		return AllTasks;
	}
	
}
