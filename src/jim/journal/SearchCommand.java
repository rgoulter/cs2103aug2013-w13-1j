package jim.journal;



import java.util.ArrayList;

import org.joda.time.MutableDateTime;



public class SearchCommand extends Command {

    private String description;
    private MutableDateTime date;


    public SearchCommand(String operand) {
        description = operand.toLowerCase();
    }
    public SearchCommand(MutableDateTime d){
        date = d;
    }



    @Override
    public String execute(JournalManager journalManager) {
        String output = "";
        String searchTerm = "";
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        SearchTool searchTool = new SearchTool(journalManager);
        if (description != null ){
            searchTerm = description;
            matchingTasks = searchTool.searchByNonStrictDescription(description);
        } else if (date != null ){
            matchingTasks = searchTool.searchByDate(date);
        }
        for (Task task : matchingTasks){
            output = output + task.toString() + "\n";
        }
        if (output.equals("")) {
            outputln("Search term '" + description + "' was not found.");
        } else {
            outputln("Matches for '" + description + "':\n" + output);
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
