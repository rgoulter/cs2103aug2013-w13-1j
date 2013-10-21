
package jim.suggestions;

import java.util.ArrayList;
import java.util.List;

import jim.journal.Command;
import java.util.logging.Logger;



public class SuggestionManager {
    private int highlightedLine = -1;
    private SuggestionHints hints;
    private Parser inputParser;
    

    //assumes the current class is called logger
    private final static Logger LOGGER = Logger.getLogger(SuggestionManager.class .getName()); 



    public SuggestionManager() {
    	inputParser = new Parser();
    }



    public List<String> getSuggestionsToDisplay() {
        // TODO: Stop cheating on this, as well =P
        List<String> displayedSuggestions = new ArrayList<String>();
        displayedSuggestions.add("Please ignore suggestions for now! ~CC");
        displayedSuggestions.add("add (name) (date) (time)");
        displayedSuggestions.add("remove (name)");
        displayedSuggestions.add("display");
        displayedSuggestions.add("exit");
        
        List<String> hintList = new ArrayList<String>();
        hintList.add("edit");
        hintList.add("add");
        hintList.add("remove");
        hintList.add("nil");
        hintList.add("nil");
        hints = new SuggestionHints(displayedSuggestions, hintList);
        
        return displayedSuggestions;
    }
    
    // Pre-Condition: Requires getSuggestionsToDisplay() to be called first
    public SuggestionHints getSuggestionHints() {
        return hints;
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
        hints.setSelected(i);
    }



    public int getCurrentSuggestionIndex() {
        return highlightedLine;
    }



    public void nextSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) %
                                  getSuggestionsToDisplay().size());
    }



    public void prevSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() - 1));
        if (getCurrentSuggestionIndex() < 0) {
            setCurrentSuggestionIndex(getSuggestionsToDisplay().size() - 1);
        }
    }



    /**
     * Update the current 'buffer' of content for the suggestion manager to
     * process.
     * 
     * @param text
     *            The input currently in the textfield.
     */
    public void updateBuffer(String text) {

    }



    /**
     * Search the given Journal for a list of tasks which match the given
     * description. At present, "matching" is defined strictly as
     * "has the same description".
     * 
     * @param journal
     * @param description
     * @return
     */
    public List<jim.journal.Task> searchForTasksByDescription(jim.journal.JournalManager journal,
                                                              String description) {
    	// As changed in commit 80154c9c7110
        return null;
    }



    public jim.journal.Task parseTask(String[] input) {
        return inputParser.parseTask(input);
    }



    /**
     * Takes an array of strings, e.g. {"add", "rom this.
     * 
     * TODO: Potentially throw some kind of "Poor format exception" as a better
     * way of giving feedback than return-null.
     */
    public Command parseCommand(String args[]) {
        return (Command) inputParser.doParse("<cmd>", args);
    }
    
}
