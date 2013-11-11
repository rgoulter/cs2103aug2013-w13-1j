//@author A0105572L
package jim.journal;

import java.util.ArrayList;

import org.joda.time.MutableDateTime;

public class SearchCommand extends Command {
    //For system purpose
    private static final String COMMANDTYPE = "Search";
    private static final String EXECUTION_STATUS_SUCCESS = "Success";
    private static final String EXECUTION_STATUS_FAILURE = "Failure";
    private static final String EXECUTION_STATUS_FILE_ERROR = "FILE ERROR: Please add any tasks in the box above to create the storage file. Enjoy JIM!";
    
    //For display to the user
    private static final String INFO_SEARCH_BY_DATE_NOT_FOUND = "Search performed on that date returned no results.";
    private static final String INFO_SEARCH_TERM_NOT_FOUND = "Search term '%s' was not found.";
    private static final String INFO_SEARCH_TERM_FOUND = "Matches for '%s':\n";
    private static final String INFO_STRING_INITIAL = "";
    private static final String INFO_END_OF_LINE = "\n";
    
    private String description;
    private MutableDateTime date;

    //Constructor for input with description
    public SearchCommand(String des) {
        description = des;
    }
    //Constructor for input with date 
    public SearchCommand(MutableDateTime d){
        date = d;
    }
    
    @Override
    public String execute(JournalManager journalManager) {
        String output = INFO_STRING_INITIAL;
        String searchTerm = INFO_STRING_INITIAL;
        ArrayList<Task> matchingTasks = new ArrayList<Task>();
        SearchTool searchTool;
        try {
            searchTool = new SearchTool(journalManager);
        } catch (Exception e) {
            outputln(EXECUTION_STATUS_FILE_ERROR);
            return EXECUTION_STATUS_FAILURE;
        }
        if (description != null ){
            searchTerm = description.toLowerCase();
            matchingTasks = searchTool.searchByNonStrictDescription(searchTerm);
        } else if (date != null ){
            matchingTasks = searchTool.searchByDate(date);
        }
        for (Task task : matchingTasks){
            output = output + task.toString() + INFO_END_OF_LINE;
        }
        
        if (output.equals(INFO_STRING_INITIAL) && date != null) {
            outputln(INFO_SEARCH_BY_DATE_NOT_FOUND);
        } else if (output.equals(INFO_STRING_INITIAL)) {
            outputln(String.format(INFO_SEARCH_TERM_NOT_FOUND,description));
        } else {
            outputln(String.format(INFO_SEARCH_TERM_FOUND, description) + output);
        }
        return EXECUTION_STATUS_SUCCESS;
    }

    @Override
    public String secondExecute(String secondInput) {
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        return null;
    }
    
    public String toString() {
        return COMMANDTYPE;
    }

}
