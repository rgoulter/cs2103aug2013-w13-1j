
package jim.suggestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
import jim.journal.JournalManager;
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
import static jim.util.StringUtils.isSubsequenceMatch;



public class SuggestionManager {
    private int highlightedLine = 0;
    private String filteringSubsequence = "";
    private ArrayList<SuggestionHint> generatedSuggestionHintsList;
    private Set<SuggestionHint> generatedSuggestionHintsSet;
    private SuggestionHints hintSet;
    private int numberOfSuggestionsToKeep = 8; // MAGIC
    
    private Parser inputParser;
    
    private JournalManager currentJournalManager;
    

    //assumes the current class is called logger
    private final static Logger LOGGER = Logger.getLogger(SuggestionManager.class .getName()); 



    public SuggestionManager() {
    	generatedSuggestionHintsList = new ArrayList<SuggestionHint>();
    	generatedSuggestionHintsSet = new HashSet<SuggestionHint>();
    	
    	inputParser = new Parser();
    }
	
	public void setJournalManager(JournalManager jm) {
		// We need this dependency for GenerationContext, unfortunately.
		currentJournalManager = jm;
	}
    
    public SuggestionHints getSuggestionHints() {
        hintSet = new SuggestionHints(generatedSuggestionHintsList, filteringSubsequence);
        hintSet.setSelectedHint(highlightedLine);
        return hintSet;
    }



    public String getCurrentSuggestion() {
        String output = "";
        int idx = getCurrentSuggestionIndex();
        
    	// "Index 0" is the current input subsequence.
        output = idx == 0 ?
        		 filteringSubsequence :
        		 generatedSuggestionHintsList.get(getCurrentSuggestionIndex() - 1).toString();

        return output;
    }



    public void setCurrentSuggestionIndex(int i) {
        assert i >= 0;
        
        highlightedLine = i;
    }



    public int getCurrentSuggestionIndex() {
        return highlightedLine;
    }



    public void nextSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) %
        		                  (generatedSuggestionHintsList.size() + 1));
    }



    public void prevSuggestion() {
    	int prevIndex = (getCurrentSuggestionIndex() - 1);
    	if (prevIndex < 0) {
    		prevIndex += generatedSuggestionHintsList.size() + 1;
    	}
        setCurrentSuggestionIndex(prevIndex);
        if (getCurrentSuggestionIndex() < 0) {
            setCurrentSuggestionIndex(generatedSuggestionHintsList.size());
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
    	
    	// This is very clunky at the moment, since,
    	//  as we add more input to the buffer, we "delete" the earlier,
    	//  "good" suggestions...
    	// To be more natural, we could perhaps try "deriving" suggestions
    	//  from current suggestions. (GenerationContext would allow this?).
    	// or other alternatives; but the above may be alright..
    	
    	if (filteringSubsequence != null) {
	    	filterThroughGeneratedSuggestions();
	    	generateMoreSuggestionsIfNecessary();
    	} else {
    		highlightedLine = 0;
    		hintSet = null;
    		generatedSuggestionHintsList.clear();
    		generatedSuggestionHintsSet.clear();
    	}
    }
    
    
    
    private void filterThroughGeneratedSuggestions() {
    	for (int i = generatedSuggestionHintsList.size() - 1; i >= 0; i--) {
    		SuggestionHint hint = generatedSuggestionHintsList.get(i);
    		boolean matchesSubseq = hint.matchesSubsequence(filteringSubsequence);
    		
    		if (!matchesSubseq) {
    			LOGGER.finer("Filtering out: " + generatedSuggestionHintsList.get(i).toString());
    			SuggestionHint hintToRemove = generatedSuggestionHintsList.get(i);
    			generatedSuggestionHintsList.remove(hintToRemove);
    			generatedSuggestionHintsSet.remove(hintToRemove);
    			
    			//TODO: Preserve selected index here...
    		} else {
    			hint.setMatchingSubsequence(filteringSubsequence);
    		}
    	}
    }
    
    
    
    private void generateMoreSuggestionsIfNecessary() {
    	// Don't try too hard to generate unique suggestions
    	// at this stage.
    	
    	//TODO: Preserve selected index here...
    	
    	for (int i = generatedSuggestionHintsList.size(); i <= numberOfSuggestionsToKeep; i++) {
    		SuggestionHint hint;
    		boolean added = false;
    		
    		for (int attempts = 0; !added && attempts < 6; attempts++) { // MAGIC
    			hint = generateRandomSuggestion();
    			
	    		if (hint.matchesSubsequence(filteringSubsequence) &&
	    			!generatedSuggestionHintsSet.contains(hint)) {
	    			LOGGER.finer("Adding Hint to Queue: " + hint);
	    			generatedSuggestionHintsList.add(hint);
	    			generatedSuggestionHintsSet.add(hint);
	    			added = true;
	    		}
    		}
    	}
    	
    	Collections.sort(generatedSuggestionHintsList);
    }
    
    
    
    private SuggestionHint generateRandomSuggestion() {
    	// TODO: Get rid of this dependency on inputParser.
    	List<SyntaxFormat> syntaxFormats = inputParser.getDisplayableSyntaxTreeLeafNodes();
    	
    	// Assumption: First word is the operative command word.
    	// Assumption: Subsequence Filtering String separates its matching words with space.
    	
    	String[] subseqFilterParts = filteringSubsequence.split(" ");
        String subseqToMatch = (subseqFilterParts.length > 0) ?
                               subseqFilterParts[0] :
                               "";

        // Filter out the syntax formats
        // whose firstWord
        for (int i = syntaxFormats.size() - 1; i >= 0; i--) {
        	String syntaxFirstWord = syntaxFormats.get(i).getSyntaxTerms()[0].toString();
        	boolean matches = isSubsequenceMatch(syntaxFirstWord, subseqToMatch);
        	
        	if (!matches) {
        		syntaxFormats.remove(i);
        	}
        }
    	
    	int i = (int) Math.floor(Math.random() * syntaxFormats.size());
    	double rnd = Math.random();

    	GenerationContext genCtx = getGenerationContext();
    	SuggestionHint hint = syntaxFormats.get(i).generate(genCtx, rnd);
		LOGGER.finer("Generating Hint: " + hint);
    	return hint;
    }
    
    
    
    private GenerationContext getGenerationContext() {
    	// Generating suggestions, we need a Journal Manager.
    	assert currentJournalManager != null;

    	GenerationContext genCtx = new GenerationContext(null, filteringSubsequence);
    	genCtx.setJournalManager(currentJournalManager);
    	
    	return genCtx;
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
