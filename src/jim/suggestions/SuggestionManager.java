package jim.suggestions;

import java.util.ArrayList;
import java.util.List;

public class SuggestionManager {

    public List<String> getSuggestionsToDisplay() {
        List<String> displayedSuggestions = new ArrayList<String>();

        displayedSuggestions.add("add <name> <date> <time>");
        displayedSuggestions.add("remove <name>");
        displayedSuggestions.add("display");
        displayedSuggestions.add("exit");
        
        return displayedSuggestions;
    }
    
    public String getCurrentSuggestion() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public void setCurrentSuggestionIndex(int i) {
        
    }
    
    public int getCurrentSuggestionIndex() {
        return -1;
    }
    
    public void nextSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) % getSuggestionsToDisplay().size());
    }
    
    public void prevSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() - 1) % getSuggestionsToDisplay().size());
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
        return null;
    }
}
