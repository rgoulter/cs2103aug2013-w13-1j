
package jim.suggestions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.CompleteCommand;
import jim.journal.DeadlineTask;
import jim.journal.DisplayCommand;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.RemoveCommand;
import jim.journal.SearchCommand;
import jim.journal.TimedTask;
import jim.journal.UndoCommand;

import org.joda.time.MutableDateTime;

import java.util.logging.Level;
import java.util.logging.Logger;

import static jim.util.StringUtils.isStringSurroundedBy;
import static jim.util.StringUtils.join;
import static jim.util.StringUtils.stripStringPrefixSuffix;



public class SuggestionManager {
    private int highlightedLine = -1;
    private String filteringSubsequence = "";
    private ArrayList<SuggestionHint> generatedSuggestionHints;
    private int numberOfSuggestionsToKeep = 8;
    
    private Parser inputParser;
    

    //assumes the current class is called logger
    private final static Logger LOGGER = Logger.getLogger(SuggestionManager.class .getName()); 



    public SuggestionManager() {
    	generatedSuggestionHints = new ArrayList<SuggestionHint>();
    	
    	inputParser = new Parser();
    }
    
    // Pre-Condition: Requires getSuggestionsToDisplay() to be called first
    public SuggestionHints getSuggestionHints() {
        return new SuggestionHints(generatedSuggestionHints);
    }



    public String getCurrentSuggestion() {
        String output = "";
        if (getCurrentSuggestionIndex() != -1) {
            output = generatedSuggestionHints.get(getCurrentSuggestionIndex()).toString();
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
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) %
        		                  generatedSuggestionHints.size());
    }



    public void prevSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() - 1));
        if (getCurrentSuggestionIndex() < 0) {
            setCurrentSuggestionIndex(generatedSuggestionHints.size() - 1);
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
    	filteringSubsequence = text;
    	
    	filterThroughGeneratedSuggestions();
    	generateMoreSuggestionsIfNecessary();
    }
    
    
    
    private void filterThroughGeneratedSuggestions() {
    	for (int i = generatedSuggestionHints.size() - 1; i >= 0; i--) {
    		boolean matchesSubseq = isSubsequenceOfSuggestionHint(filteringSubsequence,
    		                                                      generatedSuggestionHints.get(i));
    		
    		if (!matchesSubseq) {
    			LOGGER.info("Filtering out: " + generatedSuggestionHints.get(i).toString());
    			generatedSuggestionHints.remove(i);
    		}
    	}
    }
    
    
    
    private static boolean isSubsequenceOfSuggestionHint(String subseq, SuggestionHint hint) {
    	int i = 0;
    	int lastIndex = 0;
    	
    	String hintPhrase = hint.toString();
    	
    	while (i < subseq.length() && lastIndex >= 0) {
    		char charToLookFor = subseq.charAt(i);
    		lastIndex = hintPhrase.indexOf(charToLookFor, lastIndex);
    		lastIndex += 1;
    		i++;
    	}
    	
    	return i == subseq.length();
    }
    
    
    
    private void generateMoreSuggestionsIfNecessary() {
    	// Don't try too hard to generate unique suggestions
    	// at this stage.
    	
    	for (int i = generatedSuggestionHints.size(); i <= numberOfSuggestionsToKeep; i++) {
    		SuggestionHint hint;
    		boolean added = false;
    		
    		for (int attempts = 0; !added && attempts < 3; attempts++) { // MAGIC
    			hint = generateRandomSuggestion();
    			
	    		if (isSubsequenceOfSuggestionHint(filteringSubsequence, hint)) {
	    			LOGGER.info("Adding Hint to Queue: " + hint);
	    			generatedSuggestionHints.add(hint);
	    			added = true;
	    		}
    		}
    	}
    }
    
    
    
    private SuggestionHint generateRandomSuggestion() {
    	// TODO: Get rid of this dependency on inputParser.
    	List<SyntaxFormat> syntaxFormats = inputParser.getDisplayableSyntaxTreeLeafNodes();
    	
    	int i = (int) Math.floor(Math.random() * syntaxFormats.size());
    	double rnd = Math.random();
    	
    	SuggestionHint hint = syntaxFormats.get(i).generate(null, rnd);
		LOGGER.info("Generating Hint: " + hint);
    	return hint;
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
    
    
    
    public MutableDateTime parseDate(String input) {
    	return (MutableDateTime) inputParser.doParse("<date>", input);
    }
    
    
    
    public MutableDateTime parseTime(String input) {
    	return (MutableDateTime) inputParser.doParse("<time>", input);
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
