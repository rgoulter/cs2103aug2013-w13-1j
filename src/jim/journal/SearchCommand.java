package jim.journal;



import java.util.ArrayList;



public class SearchCommand extends Command {

    private String searchTerm;



    public SearchCommand(String operand) {
        searchTerm = operand;
    }



    @Override
    public String execute(JournalManager journalManager) {
        String output = "";
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        SearchTool searchTool = new SearchTool(journalManager);
        matchingTasks = searchTool.searchByNonStrictDescription(searchTerm);
        for (Task task : matchingTasks){
            output = output + task.toString() + "\n";
        }
        if (output.equals("")) {
            outputln("Search term '" + searchTerm + "' was not found.");
        } else {
            outputln("Matches for '" + searchTerm + "':\n" + output);
        }
        return "Success";
    }



    @Override
    public String secondExecute(String secondInput) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString() {
        return "Search";
    }

}
