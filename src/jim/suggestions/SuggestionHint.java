package jim.suggestions;

import static jim.util.StringUtils.join;
import static jim.util.StringUtils.isLowercase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Suggestion Hint, which describes a suggested input's words, syntax,
 *  and where the current input matches. (This is for the driver/GUI to then
 *  render as it sees fit).
 * 
 * API VOLATILE as the suggestion system hasn't been implemented yet.
 */
public class SuggestionHint implements Comparable<SuggestionHint> {
	private String[] words;
	private boolean[][] matchingMask;
	private String matchingSubseq;
	private SyntaxTerm[] terms;

	public SuggestionHint(String[] words, String matchingSubsequence, SyntaxTerm[] terms) {
		this.words = words;
		this.matchingSubseq = matchingSubsequence;
		matchingMask = getMatchMaskForWords(matchingSubseq, words);
		this.terms = terms;
	}
	
	protected String[] getWords() {
		return words;
	}
	
	protected SyntaxTerm[] getSyntaxTerms() {
		return terms;
	}
	
	protected void setMatchingSubsequence(String subseq) {
		this.matchingSubseq = subseq;
		matchingMask = getMatchMaskForWords(matchingSubseq, words);
	}
	
	@Override
	public String toString() {
        if (words.length < 1) {
            return "";
        }

        // The "String" of this Hint is the largest prefix sequence
        // which has non-empty words
        
        StringBuilder result = new StringBuilder();

        result.append(words[0]);

        for (int i = 1; i < words.length && !words[i].isEmpty(); i++) {
            result.append(' ');
            result.append(words[i]);
        }

		return result.toString();
	}
	
	protected boolean[][] getMatchingMask() {
		return matchingMask;
	}
	
	private static boolean[][] getMatchMaskForWords(String subseq, String[] words) {
		// Smart case logic
		boolean ignoreCase = isLowercase(subseq);
		
		int i = 0;
    	int lastIndex = 0;
    	
    	String hintPhrase = join(words, ' ');
    	List<Integer> matchingIndices = new ArrayList<Integer>(subseq.length());
    	
    	if (ignoreCase) {
    		hintPhrase = hintPhrase.toLowerCase();
    	}
    	
    	while (i < subseq.length() && lastIndex >= 0) {
    		char charToLookFor = subseq.charAt(i);
    		lastIndex = hintPhrase.indexOf(charToLookFor, lastIndex);
    		
    		if (lastIndex >= 0) {
    			matchingIndices.add(lastIndex);
    			lastIndex += 1;
			}
    		i++;
    	}
    	
    	boolean matches = (i == subseq.length());
    	boolean[][] matchingMask = new boolean[words.length][];
    	
    	for (i = 0; i < words.length; i++) {
    		matchingMask[i] = new boolean[words[i].length()];
    		Arrays.fill(matchingMask[i], false);
    	}
    	
    	if (!matches) {
    		return matchingMask;
    	}
    	
    	int currentWord = 0;
    	int skip = 0; // accumulated length of the words already matched..
    	for (Integer index : matchingIndices) {
    		while(index - skip >= words[currentWord].length()) {
    			skip += 1 + words[currentWord].length();
    			currentWord += 1;
    		}
    		
    		if(index - skip < 0){ continue; }
    		
    		matchingMask[currentWord][index - skip] = true;
    	}
    	
		return matchingMask;
	}
	
	public boolean matchesSubsequence(String subsequence) {
		// Smart case logic
		boolean ignoreCase = isLowercase(subsequence);
		
		int i = 0;
    	
    	String hintPhrase = join(words, ' ');
    	
    	for (int j = 0;
             j < hintPhrase.length() && i < subsequence.length();
             j++) {
    		char hintChar = hintPhrase.charAt(j);
    		char subseqChar = subsequence.charAt(i);
    		
    		if ((ignoreCase && Character.toLowerCase(hintChar) == Character.toLowerCase(subseqChar)) ||
    			hintChar == subseqChar) {
    			i++;
    		}
    	}
    	
		return i == subsequence.length();
	}
	
	public static SuggestionHint combine(SuggestionHint hint1, SuggestionHint hint2) {
		String[] newWords = new String[hint1.words.length + hint2.words.length];
		String newMatchingSubseq;
		SyntaxTerm[] newTerms = new SyntaxTerm[hint1.terms.length + hint2.terms.length];

		System.arraycopy(hint1.words, 0, newWords, 0, hint1.words.length);
		System.arraycopy(hint2.words, 0, newWords, hint1.words.length, hint2.words.length);
		
		// See: generating in SyntaxFormat.generate(..),
		// Since we do not split up the subseq there, we shouldn't
		// join it here.
		newMatchingSubseq = hint1.matchingSubseq; // + hint2.matchingSubseq;
		
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
	
	public int hashCode() {
		return Arrays.asList(words).hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof SuggestionHint) {
			SuggestionHint other = (SuggestionHint) o;
			
			return Arrays.equals(words, other.words);
		}
		
		return false;
	}

	@Override
	public int compareTo(SuggestionHint otherHint) {
		// Show first the suggestions which...?
		
		// ...has fewer words.
		// n.b. INCONSISTENT WITH EQUALS
		int lenDiff = words.length - otherHint.words.length;
		
		if (lenDiff != 0) {
			return lenDiff;
		} else {
			// If the same length, sort by the strings..
			return join(words, ' ').compareTo(join(otherHint.words, ' '));
		}
	}
}
