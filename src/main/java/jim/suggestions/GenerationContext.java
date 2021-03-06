package jim.suggestions;

import java.util.HashSet;
import java.util.Set;

import jim.journal.JournalManager;
import jim.journal.Task;

/**
 * Placeholder helper class for when algorithm is more complicated.
 */
//@author A0088816N
class GenerationContext {
	private SuggestionHint currentGeneratedHint;
	private String inputSubsequence;
	
	private JournalManager journalManager;
	
	private Set<String> allWordsFromCurrentTasks;
	
	// Do not instantiate outside of package.
	protected GenerationContext(SuggestionHint currentGenerated, String inputSubseq) {
		currentGeneratedHint = currentGenerated;
		inputSubsequence = inputSubseq;
	}
	
	public void setJournalManager(JournalManager jm) {
		journalManager = jm;
	}
	
	public void setCurrentGeneratedHint(SuggestionHint h) {
		currentGeneratedHint = h;
	}
	
	public SuggestionHint getCurrentGeneratedHint() {
		return currentGeneratedHint;
	}
	
	public String getInputSubsequence() {
		return inputSubsequence;
	}
	
	public Set<String> getAllWordsFromCurrentTasks() {
		if (allWordsFromCurrentTasks != null) {
			return allWordsFromCurrentTasks;
		}
		
		allWordsFromCurrentTasks = new HashSet<String>();
		
		try {
            for (Task t : journalManager.getAllTasks()) {
            	String description = t.getDescription();
            	
            	String words[] = description.split(" ");
            	for (String word : words) {
            		allWordsFromCurrentTasks.add(word);
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return allWordsFromCurrentTasks;
	}
}
