package jim.suggestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import jim.ConfigCommand;
import static jim.util.StringUtils.filterMatchBySubseq;
import static jim.util.StringUtils.filterSmartCaseMatchBySubseq;

//@author A0088816N
class SyntaxClassSyntaxTerm extends SyntaxTerm {
    private final static Logger LOGGER = Logger.getLogger(SyntaxClassSyntaxTerm.class .getName());

    private static final Set<String> TIME_HHMM_SET = new HashSet<String>();
    
    {
    	// Add to TIME_HHMM_SET, all strings from 00:00 to 23:59
    	for (int hh = 0; hh < 24; hh++) {
    		for (int mm = 0; mm < 60; mm++) {
    			TIME_HHMM_SET.add(String.format("%02d:%02d", hh, mm));
    		}
    	}
    }
    
    private static final Set<String> TIME_FIRSTWORDS_SET = new HashSet<String>();
    
    {
    	TIME_FIRSTWORDS_SET.addAll(TIME_HHMM_SET);
    }
    
	private static final String[] MONTH_WORDS =
    	    new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; // MAGIC
	private static final String[] DAYSOFWEEK_WORDS =
    	    new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}; // MAGIC
	private static final String[] RELATIVEDAYS_WORDS =
    	    new String[]{"yesterday", "today", "tomorrow"}; // MAGIC
	private static final String[] RELATIVEMODIFIERS_WORDS = new String[]{"next", "this", "last"};

	private static final Set<String> MONTHNAMES_SET = new HashSet<String>(Arrays.asList(MONTH_WORDS));
	private static final Set<String> DAYSOFWEEK_SET = new HashSet<String>(Arrays.asList(DAYSOFWEEK_WORDS));
	private static final Set<String> RELATIVEDAYS_SET = new HashSet<String>(Arrays.asList(RELATIVEDAYS_WORDS));
	private static final Set<String> RELATIVEMODIFIERS_SET = new HashSet<String>(Arrays.asList(RELATIVEMODIFIERS_WORDS));
	
	private static final Set<String> DATE_FIRSTWORDS_SET = new HashSet<String>();
	
	{
		DATE_FIRSTWORDS_SET.addAll(MONTHNAMES_SET);
		DATE_FIRSTWORDS_SET.addAll(DAYSOFWEEK_SET);
		DATE_FIRSTWORDS_SET.addAll(RELATIVEDAYS_SET);
		DATE_FIRSTWORDS_SET.addAll(RELATIVEMODIFIERS_SET);
	}
	
	private static Map<String, String[]> xWordsMap = new HashMap<String, String[]>();
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
        
        // LIMITATION: We must assume that the input subsequence has spaces between things matched.
        String[] subseqParts = context.getInputSubsequence().split(" ");
        SuggestionHint currentHint = context.getCurrentGeneratedHint();
        int numWordsSoFar = currentHint.getWords().length;

        // For hints beyond the # of subsequences,
        // SuggestionHints assumes we give a blank string for the value.
        if (numWordsSoFar >= subseqParts.length) {
    		SuggestionHint blankHint =  new SuggestionHint(new String[]{""},
                                                           context.getInputSubsequence(),
                                                           new SyntaxTerm[]{this});
            return blankHint;
        }

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
        String firstWord = generateSuggestionWord(DATE_FIRSTWORDS_SET, subseqParts[numWordsSoFar], t);
        SuggestionHint generatedHint =  new SuggestionHint(new String[]{firstWord},
                context.getInputSubsequence(),
                new SyntaxTerm[]{this});
        
        
        // I'm unsure of the condition so that we generate days this way.
        if (numLeftToGenerate > 1) {
        	// Now generate the second word
        	String nextWord = "";
    		String nextSubseq = (numLeftToGenerate > 1) ? subseqParts[numWordsSoFar + 1] : "";
        	
        	if (MONTHNAMES_SET.contains(firstWord)) {
        		int daysThisMonth = 31; // MAGIC
        		Set<String> dayNumbersSet = new HashSet<String>();
        		for (int i = 1; i <= daysThisMonth; i++) {
        			dayNumbersSet.add(Integer.toString(i));
        		}
        		nextWord = generateSuggestionWord(dayNumbersSet, nextSubseq, t);
            } else if (RELATIVEDAYS_SET.contains(firstWord)) {
                // yesterday|today|tomorrow
        		return generatedHint;
        	} else if (DAYSOFWEEK_SET.contains(firstWord)) {
                // Monday|...
        		return generatedHint;
        	} else if (RELATIVEMODIFIERS_SET.contains(firstWord)) {
                // prev|this|next Monday|Tues...
        		nextWord = generateSuggestionWord(DAYSOFWEEK_SET, nextSubseq, t);
        	} else {
        		return generatedHint;
        	}
        	
    		SuggestionHint nextGeneratedHint =  new SuggestionHint(new String[]{nextWord},
                    context.getInputSubsequence(),
                    new SyntaxTerm[]{this});
    		generatedHint = SuggestionHint.combine(generatedHint, nextGeneratedHint);
        }
        
        return generatedHint;
    }
    
    private SuggestionHint generateTimeSuggestionHint(GenerationContext context, double t) {
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
        
        // Generate a first-word for the time
        String firstWord = generateSuggestionWord(TIME_FIRSTWORDS_SET, subseqParts[numWordsSoFar], t);
        SuggestionHint generatedHint =  new SuggestionHint(new String[]{firstWord},
                context.getInputSubsequence(),
                new SyntaxTerm[]{this});
        
        
        return generatedHint;
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
        
        // It's unclear what behaviour should occur here,
        // so let's just generate from the words from current tasks.
        return generateSuggestionHintFromWordsInCurrentTasks(context, t);
    }
    
    private SuggestionHint generateSuggestionHintFromWordsInCurrentTasks(GenerationContext context, double t) {
        Set<String> wordsFromCurrentTasks = context.getAllWordsFromCurrentTasks();

        return generateSuggestionHintFromSetOfWords(context, t, wordsFromCurrentTasks);
    }
    
    private SuggestionHint generateSuggestionHintForConfiguration(GenerationContext context, double t) {
        // LIMITATION: We must assume that the input subsequence has spaces between things matched.
        String[] subseqParts = context.getInputSubsequence().split(" ");
        SuggestionHint currentHint = context.getCurrentGeneratedHint();
        int numWordsSoFar = currentHint.getWords().length;
        
        Set<String> configurationWords = new HashSet<String>();
        configurationWords.addAll(Arrays.asList(ConfigCommand.getValidArguments()));

        String suggestedWord = numWordsSoFar == 1 ?
                               generateSuggestionWord(configurationWords, subseqParts[numWordsSoFar], t) :
                               "";
        return new SuggestionHint(new String[]{suggestedWord},
                                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
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
        
        while (numWordsSoFar + numWordsGenerated < subseqParts.length &&
               (!isAddCmd(context, t) || numWordsSoFar >= 2)) { // REALLY Dirty hack.
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
        return isCurrentHintFirstWordOneOf(context, t, getXWords("add"));
    }

    private boolean isCompleteCmd(GenerationContext context, double t) {
        return isCurrentHintFirstWordOneOf(context, t, getXWords("complete"));
    }

    private boolean isUncompleteCmd(GenerationContext context, double t) {
        return isCurrentHintFirstWordOneOf(context, t, getXWords("uncomplete"));
    }
    
    private boolean isSearchCmd(GenerationContext context, double t) {
        return isCurrentHintFirstWordOneOf(context, t, getXWords("search"));
    }

    private boolean isRemoveCmd(GenerationContext context, double t) {
        return isCurrentHintFirstWordOneOf(context, t, getXWords("remove"));
    }

    private boolean isEditCmd(GenerationContext context, double t) {
        return isCurrentHintFirstWordOneOf(context, t, getXWords("edit"));
    }

    private boolean isConfigureCmd(GenerationContext context, double t) {
        return isCurrentHintFirstWordOneOf(context, t, getXWords("config"));
    }
    
    /*
     * This is a bit of a hack, but is less magic than what we had.
     * 
     * Assumes grammar definition like:
     * <Xword> := 'a' | 'b' | 'c'...
     * For some X, classname is "Xword",
     * and its definitions are only literal Strings. 
     */
    private String[] getXWords(String x) {
    	if (xWordsMap.containsKey(x)) {
    		return xWordsMap.get(x);
    	}
    	
    	assert syntaxClassesMap.containsKey(x + "word");
    	
    	List<SyntaxFormat> formats = getDefinitionsForSyntaxClassName(x + "word");
    	String[] words = new String[formats.size()];
    	
    	for (int i = 0; i < words.length; i++) {
    		assert formats.get(i).getSyntaxTerms().length == 1;
    		assert formats.get(i).getSyntaxTerms()[0] instanceof LiteralSyntaxTerm;
    		
    		words[i] = ((LiteralSyntaxTerm) formats.get(i).getSyntaxTerms()[0]).getLiteralValue(); 
    	}
    	
    	xWordsMap.put(x, words);
    	return words;
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
