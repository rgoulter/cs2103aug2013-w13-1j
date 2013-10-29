package jim.suggestions;

import jim.journal.JournalManager;

/**
 * Placeholder helper class for when algorithm is more complicated.
 */
class GenerationContext {
	private SuggestionHint currentGeneratedHint;
	private String inputSubsequence;
	
	private JournalManager journalManager;
	
	// Do not instantiate outside of package.
	protected GenerationContext(SuggestionHint currentGenerated, String inputSubseq) {
		currentGeneratedHint = currentGenerated;
		inputSubsequence = inputSubseq;
	}
	
	public void setJournalManager(JournalManager jm) {
		journalManager = jm;
	}
	
	public SuggestionHint getCurrentGeneratedHint() {
		return currentGeneratedHint;
	}
	
	public String getInputSubsequence() {
		return inputSubsequence;
	}
}
