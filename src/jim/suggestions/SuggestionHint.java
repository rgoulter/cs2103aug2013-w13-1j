package jim.suggestions;

import static jim.util.StringUtils.join;

/**
 * A Suggestion Hint, which describes a suggested input's words, syntax,
 *  and where the current input matches. (This is for the driver/GUI to then
 *  render as it sees fit).
 * 
 * API VOLATILE as the suggestion system hasn't been implemented yet.
 */
public class SuggestionHint {
	private String[] words;
	private String matchingSubseq;
	private SyntaxTerm[] terms;

	public SuggestionHint(String[] words, String matchingSubsequence, SyntaxTerm[] terms) {
		this.words = words;
		this.matchingSubseq = matchingSubsequence;
		this.terms = terms;
	}
	
	protected String[] getWords() {
		return words;
	}
	
	protected SyntaxTerm[] getSyntaxTerms() {
		return terms;
	}
	
	@Override
	public String toString() {
		return join(words, ' ');
	}
	
	public boolean matchesSubsequence(String subsequence) {
		int i = 0;
    	int lastIndex = 0;
    	
    	String hintPhrase = toString();
    	
    	while (i < subsequence.length() && lastIndex >= 0) {
    		char charToLookFor = subsequence.charAt(i);
    		lastIndex = hintPhrase.indexOf(charToLookFor, lastIndex);
    		
    		if (lastIndex >= 0) {
    			lastIndex += 1;
			}
    		i++;
    	}
    	
    	return i == subsequence.length();
	}
	
	public static SuggestionHint combine(SuggestionHint hint1, SuggestionHint hint2) {
		String[] newWords = new String[hint1.words.length + hint2.words.length];
		String newMatchingSubseq;
		SyntaxTerm[] newTerms = new SyntaxTerm[hint1.terms.length + hint2.terms.length];

		System.arraycopy(hint1.words, 0, newWords, 0, hint1.words.length);
		System.arraycopy(hint2.words, 0, newWords, hint1.words.length, hint2.words.length);
		
		newMatchingSubseq = hint1.matchingSubseq + hint2.matchingSubseq;
		
		System.arraycopy(hint1.terms, 0, newTerms, 0, hint1.terms.length);
		System.arraycopy(hint2.terms, 0, newTerms, hint1.terms.length, hint2.terms.length);
		
		return new SuggestionHint(newWords, newMatchingSubseq, newTerms);
	}
	
	public static SuggestionHint combine(SuggestionHint[] hints) {
		if (hints.length == 0) {
			return null;
		}
		
		SuggestionHint result = hints[0];
		
		for (int i = 1; i < hints.length; i++) {
			result = combine(result, hints[i]);
		}
		
		return result;
	}
}
