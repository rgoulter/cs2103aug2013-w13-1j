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
        if (isSearchCmd(context, t) ||
            isCompleteCmd(context, t) ||
            isUncompleteCmd(context, t) ||
            isEditCmd(context, t) ||
            isRemoveCmd(context, t)) {
            return generateSuggestionHintFromWordsInCurrentTasks(context, t);
        } else if(isAddCmd(context, t)) {
            return generateSuggestionHintFromWordsInCurrentTasks(context, t);
        }
        
        List<String> wordList = Arrays.asList(new String[]{"monkey", "banana"}); // Temporary MAGIC
        String suggestedWord = wordList.get((int) Math.floor(t * wordList.size()));
        
        return new SuggestionHint(new String[]{suggestedWord},
                                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    private SuggestionHint generateSuggestionHintFromWordsInCurrentTasks(GenerationContext context, double t) {
        Set<String> wordsFromCurrentTasks = context.getAllWordsFromCurrentTasks();

        return generateSuggestionHintFromSetOfWords(context, t, wordsFromCurrentTasks);
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
        Set<String> matchingWordSet = filterMatchBySubseq(words,
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
