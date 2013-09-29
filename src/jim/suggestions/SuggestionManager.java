package jim.suggestions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jim.journal.Task;
import jim.journal.AddCommand;
import jim.journal.CompleteCommand;
import jim.journal.RemoveCommand;
import jim.journal.EditCommand;
import jim.journal.SearchCommand;
import jim.journal.DisplayCommand;

public class SuggestionManager {

	private static final String BLACK_COLOR = "</font>"; // Only use this to return to black from other color
	private static final String RED_COLOR = "<font color='red'>";
	private static final String BLUE_COLOR = "<font color='blue'>";
	private static final String PURPLE_COLOR = "<font color='purple'>";
	private static final String GREEN_COLOR = "<font color='green'>";
	private static final String GREY_COLOR = "<font color='gray'>";
	
	private int highlightedLine = -1;
	
    public List<String> getSuggestionsToDisplay() {
    	// TODO: Stop cheating on this, as well =P
        List<String> displayedSuggestions = new ArrayList<String>();

        displayedSuggestions.add(BLUE_COLOR + "add" + BLACK_COLOR + " (name) (date) (time)");
        displayedSuggestions.add(RED_COLOR + "remove" + BLACK_COLOR + " (name)");
        displayedSuggestions.add("display");
        displayedSuggestions.add("exit");
        
        return displayedSuggestions;
    }
    
    public String getCurrentSuggestion() {
    	String output = "";
    	if (getCurrentSuggestionIndex() != -1) {
    		List<String> allStrings = getSuggestionsToDisplay();
    		output = allStrings.get(getCurrentSuggestionIndex());
    	}
    	
    	return output;
    }
    
    public void setCurrentSuggestionIndex(int i) {
        highlightedLine = i;
    }
    
    public int getCurrentSuggestionIndex() {
        return highlightedLine;
    }
    
    public void nextSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) % getSuggestionsToDisplay().size());
    }
    
    public void prevSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() - 1));
        if (getCurrentSuggestionIndex() < 0) {
        	setCurrentSuggestionIndex(getSuggestionsToDisplay().size()-1);
        }
    }

    /**
     * Update the current 'buffer' of content for the suggestion manager to process.
     * @param text The input currently in the textfield.
     */
    public void updateBuffer(String text) {
        
    }
    
    // Since many of our Commands depend on "searching by description",
    // this helper method makes sense.
    private List<jim.journal.Task> searchForTasksByDescription(String description) {
        //TODO: A rudimentary implementation of this.
        
        return null;
    }
    
    /**
     * Inverse of String.split().
     * Joins an array of Strings to one string.
     * e.g. {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private String join(String arrayOfStrings[], char joinChar) {
        return join(arrayOfStrings, joinChar, 0);
    }
    
    /**
     * Inverse of String.split().
     * Joins an array of Strings to one string.
     * e.g. {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private String join(String arrayOfStrings[], char joinChar, int startIndex) {
        return join(arrayOfStrings, joinChar, startIndex, arrayOfStrings.length);
    }
        
    /**
     * Inverse of String.split().
     * Joins an array of Strings to one string.
     * e.g. {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private String join(String arrayOfStrings[], char joinChar, int startIndex, int endIndex) {
        StringBuilder result = new StringBuilder();
        
        for (int i = startIndex; i < endIndex - 1; i++) {
            result.append(arrayOfStrings[i]);
            result.append(joinChar);
        }
        
        result.append(arrayOfStrings[endIndex - 1]);
        
        return result.toString();
    }
    
    /**
     * Takes an array of strings, e.g. {"add", "lunch", "Monday", "2pm"},
     * and returns a Journal command to be executed from this.
     * 
     * TODO: Potentially throw some kind of "Poor format exception" as a better
     *  way of giving feedback than return-null.
     */
    public jim.journal.Command parseCommand(String args[]){
        /*
         * TODO: Here is where some of our time will be spent in V0.0,
         * getting stuff like "add lunch Monday" (or some sensible command)
         * to work.
         * 
         * Adding logic in TDD fashion is *ideal* for this kind of thing,
         * I would think.
         * 
         * Once basic format rules have been established, (e.g. strict syntax for
         * add, remove, etc.),
         * THEN
         * Any language logic here can infer fairly sensibly how to access Journal API.
         * Journal API can then trust that this will be done sensibly by language logic.
         */
        
        // Parse the words into a Command object.
        // STRICT ASSUMPTION is that the first word is the "operating word". (e.g. add, remove, etc.)
        // Naturally, this assumption will be broken with more flexible inputs.
        if (args[0].equals("add")) {
            return parseAddCommand(args);
        } else if (args[0].equals("complete")) {
            return parseCompleteCommand(args);
        } else if (args[0].equals("remove")) {
            return parseRemoveCommand(args);
        } else if (args[0].equals("edit")) {
            return parseEditCommand(args);
        } else if (args[0].equals("search")) {
            return parseSearchCommand(args);
        } else if (args[0].equals("display")) {
            return parseDisplayCommand(args);
        }
        
        return null;
    }
    
    private AddCommand parseAddCommand(String args[]) {
        // Accepted 'add' syntaxes:
        // add <start-date> <start-time> <end-date> <end-time> <words describing event>
        // TODO: Add more syntaxes/formats for this command
        
        Calendar startDateTime = null;
        Calendar endDateTime = null;
        String description = join(args, ' ', 1 + 2 + 2); // The description follows the other words.
        
        // TODO: Process values in a sensible way.
        
        return new jim.journal.AddCommand(startDateTime, endDateTime, description);
    }
    
    private CompleteCommand parseCompleteCommand(String args[]) { // The "Complete" commands
        // Accepted 'complete' syntaxes:
        // complete <description>
        
        String description = join(args, ' ', 1);
        List<Task> tasksWhichMatchDescription = searchForTasksByDescription(description);
        
        return null;
    }
    
    private RemoveCommand parseRemoveCommand(String args[]) { // The "Remove" commands
        // Accepted 'remove' syntaxes:
        // remove <description>
        // TODO: Add more syntaxes/formats for this command
        
        String description = join(args, ' ', 1);
        
        return null;
    }
    
    private EditCommand parseEditCommand(String args[]) { // The "Edit" commands
        // Accepted 'edit' syntaxes:
        // edit <description>
        // TODO: Add more syntaxes/formats for this command
        
        String description = join(args, ' ', 1);
        
        return null;
    }
    
    private SearchCommand parseSearchCommand(String args[]) { // The "Search" commands
        // Accepted 'search' syntaxes:
        // search <description>
        // TODO: Add more syntaxes/formats for this command

        String description = join(args, ' ', 1);
        
        return null;
    }
    
    private DisplayCommand parseDisplayCommand(String args[]) { // The "Display" commands
        // Accepted 'display' syntaxes:
        // display <date predicate>
        // TODO: Add more syntaxes/formats for this command
        
        return null;
    }
}
