package jim.suggestions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jim.journal.AddCommand;

public class SuggestionManager {

	private static final String BLACK_COLOR = "</font>"; // Only use this to return to black from other color
	private static final String RED_COLOR = "<font color='red'>";
	private static final String BLUE_COLOR = "<font color='blue'>";
	private static final String PURPLE_COLOR = "<font color='purple'>";
	private static final String GREEN_COLOR = "<font color='green'>";
	private static final String GREY_COLOR = "<font color='gray'>";
	
	private int highlightedLine = 0;
	
    public List<String> getSuggestionsToDisplay() {
        List<String> displayedSuggestions = new ArrayList<String>();

        displayedSuggestions.add(BLUE_COLOR + "add" + BLACK_COLOR + " (name) (date) (time)");
        displayedSuggestions.add(RED_COLOR + "remove" + BLACK_COLOR + " (name)");
        displayedSuggestions.add("display");
        displayedSuggestions.add("exit");
        
        return displayedSuggestions;
    }
    
    public String getCurrentSuggestion() {
        throw new UnsupportedOperationException("Not yet implemented");
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
        
        // Add commands
        if (args[0].equals("add")) {
            // Accepted add syntaxes:
            // add <start-date> <start-time> <end-date> <end-time> <words describing event>
            // TODO: Add more syntaxes
            
            Calendar startDateTime = null;
            Calendar endDateTime = null;
            String description = null;
            
            // TODO: Process values in a sensible way.
            
            return new jim.journal.AddCommand(startDateTime, endDateTime, description);
        }
        
        return null;
    }
}
