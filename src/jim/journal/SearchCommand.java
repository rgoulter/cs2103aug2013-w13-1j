
package jim.journal;

import java.util.List;



public class SearchCommand extends Command {

    private String searchTerm;



    public SearchCommand(String operand) {
        searchTerm = operand;
    }



    @Override
    public void execute(JournalManager journalManager) {
        String output = "";
        List<Task> allTasks = journalManager.getAllTasks();

        for (Task current : allTasks) {
            String currentName = current.getDescription();
            if (currentName.contains(searchTerm)) {
                output = output + currentName + "\n";
            }
        }

        if (output.equals("")) {
            outputln("Search term '" + searchTerm + "' was not found.");
        } else {
            outputln("Matches for '" + searchTerm + "':\n" + output);
        }
    }

}
