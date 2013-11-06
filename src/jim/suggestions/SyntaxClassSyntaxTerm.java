package jim.suggestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static jim.util.StringUtils.filterMatchBySubseq;
import static jim.util.StringUtils.filterSmartCaseMatchBySubseq;

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
    	String[] monthNames =
        	    new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; // MAGIC
    	String[] daysOfWeekNames =
        	    new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}; // MAGIC
    	String[] relativeWords = new String[]{"next", "this", "last"};

    	Set<String> monthNamesSet = new HashSet<String>(Arrays.asList(monthNames));
    	Set<String> daysOfWeekSet = new HashSet<String>(Arrays.asList(daysOfWeekNames));
    	Set<String> relativeWordsSet = new HashSet<String>(Arrays.asList(relativeWords));
    	
    	Set<String> dateFirstWordsSet = new HashSet<String>();
    	dateFirstWordsSet.addAll(monthNamesSet);
    	dateFirstWordsSet.addAll(relativeWordsSet);


        SuggestionHint currentHint = context.getCurrentGeneratedHint();
        int numWordsSoFar = currentHint.getWords().length;
        
        // LIMITATION: We must assume that the input subsequence has spaces between things matched.
        String[] subseqParts = context.getInputSubsequence().split(" ");
        int numLeftToGenerate = subseqParts.length - numWordsSoFar;

        // This happens 
        if (numLeftToGenerate < 1) {
        	return new SuggestionHint(new String[]{""},
                                      context.getInputSubsequence(),
                                      new SyntaxTerm[]{this});
        }	
        
        // Generate a first-word for the date
        String firstWord = generateSuggestionWord(dateFirstWordsSet, subseqParts[numWordsSoFar], t);
        SuggestionHint generatedHint =  new SuggestionHint(new String[]{firstWord},
                context.getInputSubsequence(),
                new SyntaxTerm[]{this});
        
        
        // I'm unsure of the condition so that we generate days this way.
        if (numLeftToGenerate > 1) {
        	// Now generate the second word
        	String nextWord = "";
    		String nextSubseq = (numLeftToGenerate > 1) ? subseqParts[numWordsSoFar + 1] : "";
        	
        	if (monthNamesSet.contains(firstWord)) {
        		int daysThisMonth = 31; // MAGIC
        		Set<String> dayNumbersSet = new HashSet<String>();
        		for (int i = 1; i <= daysThisMonth; i++) {
        			dayNumbersSet.add(Integer.toString(i));
        		}
        		nextWord = generateSuggestionWord(dayNumbersSet, nextSubseq, t);
        	} else if (relativeWordsSet.contains(firstWord)) {
        		nextWord = generateSuggestionWord(daysOfWeekSet, nextSubseq, t);
        	} else {
        		// Dunno, lol.
        	}
        	
    		SuggestionHint nextGeneratedHint =  new SuggestionHint(new String[]{nextWord},
                    context.getInputSubsequence(),
                    new SyntaxTerm[]{this});
    		generatedHint = SuggestionHint.combine(generatedHint, nextGeneratedHint);
        }
        
        return generatedHint;
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
        if (isSearchCmd(context, t) ||
            isCompleteCmd(context, t) ||
            isUncompleteCmd(context, t) ||
            isEditCmd(context, t) ||
            isRemoveCmd(context, t)) {
            return generateSuggestionHintFromWordsInCurrentTasks(context, t);
        } else if(isAddCmd(context, t)) {
            return generateSuggestionHintFromWordsInCurrentTasks(context, t);
        } else if(isConfigureCmd(context, t)) {
        	return generateSuggestionHintForConfiguration(context, t);
        }
        
        // These magic values are for Config. This is not very elegant.
        List<String> wordList = Arrays.asList(new String[]{"outputfilename",
        		                                           "dateseparator",
        		                                           "timeseparator",
        		                                           "reset"}); // Temporary MAGIC
        String suggestedWord = wordList.get((int) Math.floor(t * wordList.size()));
        
        return new SuggestionHint(new String[]{suggestedWord},
                                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    private SuggestionHint generateSuggestionHintFromWordsInCurrentTasks(GenerationContext context, double t) {
        Set<String> wordsFromCurrentTasks = context.getAllWordsFromCurrentTasks();

        return generateSuggestionHintFromSetOfWords(context, t, wordsFromCurrentTasks);
    }
    
    private SuggestionHint generateSuggestionHintForConfiguration(GenerationContext context, double t) {
    	// TODO: Presumably only the first word should be one of the following. Oh well.
        Set<String> configurationWords = new HashSet<String>();
        configurationWords.addAll(Arrays.asList(new String[]{"outputfilename",
                                                             "dateseparator",
                                                             "timeseparator",
                                                             "reset"}));

        return generateSuggestionHintFromSetOfWords(context, t, configurationWords);
    }

    private SuggestionHint generateSuggestionHintFromSetOfWords(GenerationContext context, double t, Set<String> wordsToGenFrom) {
        // Here we generate as little or as much of a description as we need to.
        // This may be one word, or it may be many.
        
        SuggestionHint currentHint = context.getCurrentGeneratedHint();
        int numWordsSoFar = currentHint.getWords().length;
        
        // LIMITATION: We must assume that the input subsequence has spaces between things matched.
        String[] subseqParts = context.getInputSubsequence().split(" ");

        String subseqForGenWord = (numWordsSoFar < subseqParts.length) ? subseqParts[numWordsSoFar] : "";
        String suggestedWord = generateSuggestionWord(wordsToGenFrom, subseqForGenWord, t);
        SuggestionHint generatedHint =  new SuggestionHint(new String[]{suggestedWord},
                                                           context.getInputSubsequence(),
                                                           new SyntaxTerm[]{this});
        int numWordsGenerated = 1;
        
        while (numWordsSoFar + numWordsGenerated < subseqParts.length) {
            numWordsGenerated++;
            int subseqPartIdx = numWordsSoFar + numWordsGenerated;
            double tt = Math.pow(1 + t, subseqPartIdx) % 1; // need to redistribute t
            subseqForGenWord = (subseqPartIdx < subseqParts.length) ? subseqParts[subseqPartIdx] : "";
            suggestedWord = generateSuggestionWord(wordsToGenFrom, subseqForGenWord, tt);
            SuggestionHint nextGeneratedHint =  new SuggestionHint(new String[]{suggestedWord},
                                                               context.getInputSubsequence(),
                                                               new SyntaxTerm[]{this});
            generatedHint = SuggestionHint.combine(generatedHint, nextGeneratedHint);
        }
        
        // Generate enough words for the given subsequence hint.
        return generatedHint;
    }
    
    private String generateSuggestionWord(Set<String> words, String subseqForGenWord, double t) {
        Set<String> matchingWordSet = filterSmartCaseMatchBySubseq(words,
                                                                   subseqForGenWord);
        List<String> wordList = new ArrayList<String>(matchingWordSet);
        
        // If no words matched, we'll just use the subsequence.
        String suggestedWord = !wordList.isEmpty() ? 
                               wordList.get((int) Math.floor(t * wordList.size())) :
                               subseqForGenWord;
        return suggestedWord;
    }
    
    // There has to be a better way to do the following...
    
    private boolean isAddCmd(GenerationContext context, double t) {
    	String[] addCmdWords = new String[]{"add", "create", "new", "+"};
        return isCurrentHintFirstWordOneOf(context, t, addCmdWords); // MAGIC
    }

    private boolean isCompleteCmd(GenerationContext context, double t) {
    	String[] completeCmdWords = new String[]{"complete", "done", "finish", "*"}; 
        return isCurrentHintFirstWordOneOf(context, t, completeCmdWords); // MAGIC
    }

    private boolean isUncompleteCmd(GenerationContext context, double t) {
    	String[] uncompleteCmdWords = new String[]{"uncomplete", "undone", "unfinish", "**"}; 
        return isCurrentHintFirstWordOneOf(context, t, uncompleteCmdWords); // MAGIC
    }
    
    private boolean isSearchCmd(GenerationContext context, double t) {
    	String[] searchCmdWords = new String[]{"search", "find", "query", "?"}; 
        return isCurrentHintFirstWordOneOf(context, t, searchCmdWords); // MAGIC
    }

    private boolean isRemoveCmd(GenerationContext context, double t) {
    	String[] removeCmdWords = new String[]{"remove", "delete", "cancel", "-"};
        return isCurrentHintFirstWordOneOf(context, t, removeCmdWords); // MAGIC
    }

    private boolean isEditCmd(GenerationContext context, double t) {
    	String[] editCmdWords = new String[]{"edit", "modify", "change", "update", ":"};
        return isCurrentHintFirstWordOneOf(context, t, editCmdWords); // MAGIC
    }

    private boolean isConfigureCmd(GenerationContext context, double t) {
    	String[] configCmdWords = new String[]{"config", "configuration", "configure"};
        return isCurrentHintFirstWordOneOf(context, t, configCmdWords); // MAGIC
    }
    
    private boolean isCurrentHintFirstWordOneOf(GenerationContext context, double t, String[] stringArray) {
        if (context.getCurrentGeneratedHint() == null) {
            return false;
        }
        
        String firstWord = context.getCurrentGeneratedHint().getWords()[0];
        
        for (String str : stringArray) {
            if (str.equals(firstWord)) {
                return true;
            }
        }

        return false;
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
