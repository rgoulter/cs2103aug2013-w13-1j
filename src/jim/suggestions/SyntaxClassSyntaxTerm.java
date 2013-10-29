package jim.suggestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static jim.util.StringUtils.filterMatchBySubseq;

class SyntaxClassSyntaxTerm extends SyntaxTerm {
    private final static Logger LOGGER = Logger.getLogger(SyntaxClassSyntaxTerm.class .getName()); 
    
	private static Map<String, List<SyntaxFormat>> syntaxClassesMap = null;
	
	private String syntaxClassName;
	
    public SyntaxClassSyntaxTerm(String className) {
    	syntaxClassName = className;
    }

    @Override
    public boolean matches(String inputTerm) {
        throw new UnsupportedOperationException("Don't match against syntax class.");
    }

    @Override
    public boolean isDisplayable() {
        return "date time description".contains(syntaxClassName);
    }

    @Override
    public SuggestionHint generate(GenerationContext context, double t) {
    	assert context != null;
    	
    	if ("description".equals(syntaxClassName)) {
    		return generateDescriptionSuggestionHint(context, t);
    	} else if ("date".equals(syntaxClassName)) {
    		return generateDateSuggestionHint(context, t);
    	} else if ("time".equals(syntaxClassName)) {
    		return generateTimeSuggestionHint(context, t);
    	} else {
    		return generateSuggesionHintFromRandomFormat(context, t);
    	}
    }

	private SuggestionHint generateDateSuggestionHint(GenerationContext context, double t) {
    	List<String> wordList = Arrays.asList(new String[]{"05/11/13", "31/12/13"}); // Temporary MAGIC
    	String suggestedWord = wordList.get((int) Math.floor(t * wordList.size()));
    	
    	return new SuggestionHint(new String[]{suggestedWord},
    			                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    private SuggestionHint generateTimeSuggestionHint(GenerationContext context, double t) {
    	List<String> wordList = Arrays.asList(new String[]{"0800", "1200", "2359"}); // Temporary MAGIC
    	String suggestedWord = wordList.get((int) Math.floor(t * wordList.size()));
    	
    	return new SuggestionHint(new String[]{suggestedWord},
    			                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    private SuggestionHint generateDescriptionSuggestionHint(GenerationContext context, double t) {
    	// Delegate to other methods, if we can.
    	if (isSearchCmd(context, t)) {
    		return generateSearchDescriptionSuggestionHint(context, t);
    	}
    	
    	List<String> wordList = Arrays.asList(new String[]{"monkey", "banana"}); // Temporary MAGIC
    	String suggestedWord = wordList.get((int) Math.floor(t * wordList.size()));
    	
    	return new SuggestionHint(new String[]{suggestedWord},
    			                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    private SuggestionHint generateSearchDescriptionSuggestionHint(GenerationContext context, double t) {
    	// Here we generate as little or as much of a description as we need to.
    	// This may be one word, or it may be many.
    	
    	SuggestionHint currentHint = context.getCurrentGeneratedHint();
    	int numWordsSoFar = currentHint.getWords().length;
    	
    	// LIMITATION: We must assume that the input subsequence has spaces between things matched.
    	String[] subseqParts = context.getInputSubsequence().split(" ");
    	String subseqForGenWord = (numWordsSoFar < subseqParts.length) ? subseqParts[numWordsSoFar] : "";
    	
    	Set<String> wordsFromCurrentTasks = context.getAllWordsFromCurrentTasks();
    	Set<String> matchingWordSet = filterMatchBySubseq(wordsFromCurrentTasks,
    	                                                  subseqForGenWord);
    	List<String> wordList = new ArrayList<String>(matchingWordSet);
    	
    	LOGGER.info("# words: " + wordsFromCurrentTasks.size() + ", # matched: " + matchingWordSet.size());
    	
    	
    	String suggestedWord = wordList.get((int) Math.floor(t * wordList.size()));
    	
    	return new SuggestionHint(new String[]{suggestedWord},
    			                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    private boolean isSearchCmd(GenerationContext context, double t) {
    	if (context.getCurrentGeneratedHint() == null) {
    		return false;
    	}
    	
    	String firstWord = context.getCurrentGeneratedHint().getWords()[0];
    	
    	return "search".contains(firstWord); // MAGIC
    }
    
    private SuggestionHint generateSuggesionHintFromRandomFormat(GenerationContext context,
                                                                 double t) {
    	List<SyntaxFormat> definitions = getDefinitions();
		
		// Try and improve the distribution of t
		// to be more uniform. (t not assumed to be uniform).
		int N = definitions.size();
		int indexToChoose = (int) (t * N * N) % N;
		
		SyntaxFormat chosenFormat = definitions.get(indexToChoose);
		
		return chosenFormat.generate(context, t);
	}

    @Override
    public String toString() {
    	return "<" + syntaxClassName + ">";
    }
    
    public String getSyntaxClassName() {
    	return syntaxClassName;
    }
    
    private List<SyntaxFormat> getDefinitions() {
    	return getDefinitionsForSyntaxClassName(syntaxClassName);
    }
    
    private static List<SyntaxFormat> getDefinitionsForSyntaxClassName(String name) {
    	if(syntaxClassesMap == null){
    		syntaxClassesMap = new HashMap<String, List<SyntaxFormat>>();
    		SyntaxGrammar.initSyntax(syntaxClassesMap);
    	}
    	
    	return syntaxClassesMap.get(name);
    }
}
