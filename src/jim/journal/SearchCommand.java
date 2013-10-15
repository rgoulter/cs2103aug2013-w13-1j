
package jim.journal;

import java.util.ArrayList;
import java.util.List;



public class SearchCommand extends Command {

    private String searchTerm;



    public SearchCommand(String operand) {
        searchTerm = operand;
    }



    @Override
    public void execute(JournalManager journalManager) {
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
    }

}
