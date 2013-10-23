package jim.journal;



import java.util.ArrayList;



public class RemoveCommand extends Command {

    String description;
    JournalManager JM;
    ArrayList<Task> matchingTasks = new ArrayList<Task>();
    ArrayList<Task> taskToRemove = new ArrayList<Task>();

    public RemoveCommand(String des) {
        description = des;
    }


    
    @Override
    public String execute(JournalManager journalManager) {
        JM = journalManager;
        
        SearchTool searchTool = new SearchTool(journalManager);
        matchingTasks = searchTool.searchByNonStrictDescription(description);
        
       
       
            if (matchingTasks.size() == 0){
                outputln("Description was not matched.");
                return "Success";
            }else{
                outputln("Type in just the index of tasks you wish to process. Please seperate them by ','");
                for (int i = 0; i < matchingTasks.size(); i++){
                    Task task = matchingTasks.get(i);
                    outputln(i + ", " + task.toString());
                }
                return "Pending";
                
            }
    }



    @Override
    public String secondExecute(String secondInput) {
        String[] IndexesOfTasks = secondInput.split(",");
        for (int i = 0; i < IndexesOfTasks.length; i++){
            try{
               int j = Integer.parseInt(IndexesOfTasks[i]);
               //if ((0<j)||(j>matchingTasks.size())){
                 //  return "Fail";
               //}
               taskToRemove.add(matchingTasks.get(j));
            }catch(Exception e){
               return "Pending";
            }
        }
        executeHelper();
        return "Success";
    }



    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    private void executeHelper(){
        clearOutput();
        
        for (Task t : taskToRemove) {
            if (JM.removeTask(t)) {
            JM.addCommandHistory("remove", t);
                outputln("Removed task: " + t.toString());
            } else {
                outputln("Removing task was not successful.");
            }
        }
    }
    
    public String toString() {
        return "Remove";
    }

}
