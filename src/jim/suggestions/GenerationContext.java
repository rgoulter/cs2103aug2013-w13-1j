package jim.suggestions;

/**
 * Placeholder helper class for when algorithm is more complicated.
 */
class GenerationContext {
	private SuggestionHint currentGeneratedHint;
	private String inputSubsequence;
	
	// Do not instantiate outside of package.
	protected GenerationContext(SuggestionHint currentGenerated, String inputSubseq) {
		currentGeneratedHint = currentGenerated;
		inputSubsequence = inputSubseq;
	}
	
	public SuggestionHint getCurrentGeneratedHint() {
		return currentGeneratedHint;
	}
	
	public String getInputSubsequence() {
		return inputSubsequence;
	}
}
