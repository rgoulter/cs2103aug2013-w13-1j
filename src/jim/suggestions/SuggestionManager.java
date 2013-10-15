
package jim.suggestions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jim.JimInputter;
import jim.journal.AddCommand;
import jim.journal.Command;
import jim.journal.CompleteCommand;
import jim.journal.DeadlineTask;
import jim.journal.DisplayCommand;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.RemoveCommand;
import jim.journal.SearchCommand;
import jim.journal.TimedTask;

import org.joda.time.MutableDateTime;

import com.sun.corba.se.impl.orbutil.graph.Node;



public class SuggestionManager {
    private int highlightedLine = -1;
    private SuggestionHints hints;
    private JimInputter inputSource;
    private static final int LENGTH_OF_DATE = 3; // [DD][MM][YY]
    private static final int INDICATE_YEAR = 2;
    private static final int INDICATE_MONTH = 1;
    /**
     * Matches DD/MM/YY.
     */
    private static final String REGEX_DATE_DDMMYY = "\\d\\d/\\d\\d/\\d\\d";

    /**
     * Matches four digits in a row. e.g. HHMM.
     */
    private static final String REGEX_TIME_HHMM = "\\d\\d\\d\\d";

    private interface SyntaxParser {
        public Object parse(String[] input);
    }

    private abstract class SyntaxTermParser implements SyntaxParser {
        public abstract Object parse(String inputTerm);
        
        public Object parse(String[] input){
        	if (input.length != 1) {
        		// To keep in line with the assumption of SyntaxTermParser
        		throw new IllegalArgumentException("Only 1-length arrays allowed for SyntaxTermParser");
        	}
        	
			return parse(input[0]);
        	
        }
    }



    private enum SearchMatchState {
        YES, NO, MAYBE;
    }



    /**
     * SearchNode class helps us match input with the
     * syntax tree.
     */
    private class SearchNode {
        List<SyntaxNode> syntaxFormat;
        String[] inputArray;

        public SearchNode(List<SyntaxNode> syntax, String[] input){
            syntaxFormat = syntax;
            inputArray = input;
        }

        public SearchMatchState getMatchedState() {
            if (syntaxFormat.size() > inputArray.length) {
                // Cannot have more terms to match against than we have terms.
                return SearchMatchState.NO;
            } else if(!isAllSyntaxNodesTerminal()) {
                return SearchMatchState.MAYBE;
            } else {
                return isMatched() ?
                       SearchMatchState.YES :
                       SearchMatchState.NO;
            }
        }

        private boolean isAllSyntaxNodesTerminal() {
            for (SyntaxNode node : syntaxFormat) {
                if (!node.isTerminal()) {
                    return false;
                }
            }

            return true;
        }

        private boolean isMatched() {
            assert isAllSyntaxNodesTerminal();
            
            if (syntaxFormat.size() < inputArray.length) {
                return false;
            }

            boolean isAllMatched = true;
            
            for (int i = 0; i < syntaxFormat.size(); i++) {
                SyntaxNode node = syntaxFormat.get(i);
                
                if (!node.isMatched(inputArray[i])) {
                	isAllMatched = false;
                	break;
                }
            }
            
            
            if (isAllMatched) {
            	// TODO: SLAP this away?
            	// Add all the nodes to their parents.
            	for (int i = 0; i < syntaxFormat.size(); i++) {
                    SyntaxNode node = syntaxFormat.get(i);
                    node.ensureAddedToParent();
            	}
            } else {
            	// TODO: SLAP this away?
            	// Since we didn't successfully match, reset.
            	for (int i = 0; i < syntaxFormat.size(); i++) {
                    SyntaxNode node = syntaxFormat.get(i);
                    node.inputTerm = null;
            	}
            }

            return isAllMatched;
        }

        public List<SearchNode> nextNodes() {
            assert !isAllSyntaxNodesTerminal();

            // For each definition of the first syntax class found,
            //    expand format with these....
            for (int i = 0; i < syntaxFormat.size(); i++) {
                SyntaxNode node = syntaxFormat.get(i);

                // Expand the first non-terminal term.
                if (!node.isTerminal()) {
                    List<SyntaxNode> preList = syntaxFormat.subList(0, i);
                    List<SyntaxNode> postList = syntaxFormat.subList(i + 1, syntaxFormat.size());

                    String syntaxClassName = stripStringPrefixSuffix(node.syntaxTerm, 1);
                    List<String> syntaxClassDefinitionsList = syntaxClassesMap.get(syntaxClassName);
                    
                    if (syntaxClassDefinitionsList == null) {
                        throw new IllegalArgumentException("Given a syntax term with unknown class: " + node.syntaxTerm);
                    }

                    List<SearchNode> nextSearchNodes = new ArrayList<SearchNode> (syntaxClassDefinitionsList.size());


                    for (String nextSyntaxDefinition : syntaxClassDefinitionsList) {
                        List<SyntaxNode> nextSyntaxNodes = new LinkedList<SyntaxNode>();
                        
                        nextSyntaxNodes.addAll(preList);
                        for(String syntaxTerm : nextSyntaxDefinition.split(" ")){
                        	SyntaxNode newNode = new SyntaxNode(syntaxTerm);
                        	newNode.parent = node;
                        	nextSyntaxNodes.add(newNode);
                        }
                        nextSyntaxNodes.addAll(postList);

                        SearchNode nextSearchNode = new SearchNode(nextSyntaxNodes, inputArray);
                        nextSearchNodes.add(nextSearchNode);
                    }

                    return nextSearchNodes;
                }
            }

            throw new IllegalStateException("Illegal state: Should have found a non-terminal in: " + join(syntaxFormat.toArray(new String[]{}), ' '));
        }
        
        public String toString() {
        	StringBuilder result = new StringBuilder();

        	for (SyntaxNode synTerm : syntaxFormat) {
    			result.append(synTerm.syntaxTerm);
    			result.append(" ");
        	}
        	
			result.append("<-");
			
        	for (String inputStr : inputArray) {
    			result.append(" ");
    			result.append(inputStr);
        	}
        	
        	return result.toString();
        }
    }



    private class SyntaxNode {
        SyntaxNode parent;
        private boolean hasBeenAddedToParent = false;
        
        private List<SyntaxNode> childrenNodes = new ArrayList<SyntaxNode>();
        String syntaxTerm; // e.g. <date>, "add", /abc/, ...
        String inputTerm = null;

        public SyntaxNode(String syntax) {
        	assert !syntax.contains(" "); // Cannot have spaces..
        	
            syntaxTerm = syntax;
        }

        // Return in-order walk of inputTerm
        public String getMatchedInput() {
            if (childrenNodes == null || childrenNodes.isEmpty()) {
                return inputTerm;
            } else {
                return join(getMatchedInputOfChildren(), ' ');
            }
        }
        
        public String[] getMatchedInputOfChildren() {
        	String[] result = new String[childrenNodes.size()];
        	
        	for (int i = 0; i < childrenNodes.size(); i++) {
        		result[i] = childrenNodes.get(i).getMatchedInput();
        	}
        	
        	return result;
        }

        public boolean isTerminal() {
            // "if not a syntax class", <...> -> false, else -> true
            return isSyntaxLiteral(syntaxTerm) ||
                   isSyntaxRegex(syntaxTerm);
        }

        public List<SyntaxNode> getChildren() {
            assert !isTerminal(); // only call this from non-terminals. (i.e. syntax classes..).

            return childrenNodes;
        }
        
        protected void ensureAddedToParent() {
        	if (!hasBeenAddedToParent) {
        		if (parent != null) {
        			parent.childrenNodes.add(this);
        			parent.ensureAddedToParent(); // recurse..
        		}
        		
        		hasBeenAddedToParent = true;
        	}
        }

        public boolean isMatched(String input) {
            // Get the logic here from isMatchSyntaxTermWithInputTerm...
            boolean matched = isMatchSyntaxTermWithInputTerm(syntaxTerm, input);

            if (matched) {
                // We record the last input term we successfully matched against.
                inputTerm = input;
            }
            
            return matched;
        }
    }




    private final Map<String, List<String>> syntaxClassesMap = new HashMap<String, List<String>>();
    private final Map<String, SyntaxParser> syntaxParsers = new HashMap<String, SyntaxParser>();


    public SuggestionManager() {
        initSyntax();
    }

    public void setInputSource(JimInputter source) {
        inputSource = source;
    }

    private void initSyntax() {
        // Initialise our syntax classes dictionary.
        // TODO: Would it be possible to have this in an external file? Or would that be more confusing?
        addSyntax("<date> := /" + REGEX_DATE_DDMMYY + "/ | /\\d\\d\\d\\d\\d\\d/ | /\\d\\d-\\d\\d-\\d\\d/");
        addSyntax("<time> := /" + REGEX_TIME_HHMM + "/ | /\\d\\d:\\d\\d/");
        addSyntax("<word> := /\\S+/"); // non whitespace
        addSyntax("<phrase> := <word> | <word> <phrase>");
        addSyntax("<description> := <phrase>");

        addSyntax("<timedtask> := " +
                  "<date> <time> <date> <time> <description> | " +
                  "<date> <time> 'to' <time> <description> | " +
                  "<date> <time> <time> <description>");
        addSyntax("<deadlinetask> := <date> <description>");
        addSyntax("<floatingtask> := <description>");
        addSyntax("<task> := <timedtask> | <deadlinetask> | <floatingtask>");
        addSyntax("<addcmd> := 'add' <task>");

        initSyntaxParsers();
    }



    private void initSyntaxParsers() {
        //TODO: Abstract Key into a key type.
        // KEY: syntaxTerm + " => " + nextSyntaxTerm
        SyntaxTermParser genericDDMMYYParser =
        new SyntaxTermParser(){
            @Override
            public Object parse(String inputTerm) {
                return parseDate(inputTerm);
            }
        };
        syntaxParsers.put("date => /" + REGEX_DATE_DDMMYY + "/",
                          genericDDMMYYParser);
        syntaxParsers.put("date => /\\d\\d\\d\\d\\d\\d/",
                          genericDDMMYYParser);
        syntaxParsers.put("date => /\\d\\d-\\d\\d-\\d\\d/",
                          genericDDMMYYParser);


        syntaxParsers.put("time => /" + REGEX_TIME_HHMM + "/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  int hh = Integer.parseInt(inputTerm.substring(0, 2));
                                  int mm = Integer.parseInt(inputTerm.substring(2));
                                  
                                  return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
                              }
                          });
        syntaxParsers.put("time => /\\d\\d:\\d\\d/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  int hh = Integer.parseInt(inputTerm.substring(0, 2));
                                  int mm = Integer.parseInt(inputTerm.substring(3));
                                  
                                  return new MutableDateTime(0, 0, 0, hh, mm, 00, 00);
                              }
                          });
        

        // Redundant?
        syntaxParsers.put("word := /\\S+/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  return inputTerm;
                              }
                          });


        // Redundant?
        syntaxParsers.put("phrase => <word>",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  // Get parser for <word> ...
                                  return inputTerm;
                              }
                          });
        syntaxParsers.put("phrase => <word> <phrase>",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String input) {
                                  return input;
                              }
                          });


        syntaxParsers.put("timedtask => <date> <time> <date> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime startDate =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("date", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("time", input[1]);
                                  MutableDateTime endDate =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("date", input[2]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("time", input[3]);
                                  String description = input[4];
                                  return new TimedTask(datetime(startDate, startTime),
                                                       datetime(endDate, endTime), description);
                              }
                          });
        syntaxParsers.put("timedtask => <date> <time> 'to' <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("date", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("time", input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("time", input[3]);
                                  String description = input[4];
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       description);
                              }
                          });
        syntaxParsers.put("timedtask => <date> <time> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("date", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("time", input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("time", input[2]);
                                  String description = input[3];
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       description);
                              }
                          });
        syntaxParsers.put("deadlinetask => <date> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) parseInputTermWithSyntaxClass("date", input[0]);
                                  String description = input[1];
                                  return new DeadlineTask(date, description);
                              }
                          });
        
        
        syntaxParsers.put("floatingtask => <description>",
                          new SyntaxTermParser() {
                              @Override
                              public Object parse(String input) {
                                  return new FloatingTask(input);
                              }
                          });
        
        
        syntaxParsers.put("addcmd => 'add' <task>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  String[] taskArgs = new String[input.length - 1];
                                  System.arraycopy(input, 1, taskArgs, 0, taskArgs.length);
                                  
                                  jim.journal.Task taskToAdd = parseTask(taskArgs);
                                  return new AddCommand(taskToAdd);
                              }
                          });
    }
    
    
    
    /**
     * Merge a date and time value into a datetime.
     */
    private MutableDateTime datetime(MutableDateTime date,
                                     MutableDateTime time) {
        return new MutableDateTime(date.getYear(),
                                   date.getMonthOfYear(),
                                   date.getDayOfMonth(),
                                   time.getHourOfDay(),
                                   time.getMinuteOfHour(),
                                   time.getSecondOfMinute(),
                                   00);
    }

    /**
     * This finds the appropriate parser of the syntaxClass for the inputTerm.
     * e.g. parseInputTermWithSyntaxClass("<time>", "2359");
     */
    private Object parseInputTermWithSyntaxClass(String syntaxTerm, String inputTerm) {
        assert isMatchSyntaxTermWithInputTerm('<' + syntaxTerm + '>', inputTerm);
        
        List<String> syntaxClassDefinitionsList = syntaxClassesMap.get(syntaxTerm);
        
        if (syntaxClassDefinitionsList == null) {
            throw new IllegalArgumentException("Given a syntax term with unknown class: " + syntaxTerm);
        }
        
        for (String nextSyntaxTerm : syntaxClassDefinitionsList) {
            if(isMatchSyntaxTermWithInputTerm(nextSyntaxTerm, inputTerm)){
                // KEY: syntaxTerm + " => " + nextSyntaxTerm
                SyntaxParser parser = syntaxParsers.get(syntaxTerm + " => " + nextSyntaxTerm);
                
                if (parser != null) {
                    return parser.parse(new String[]{inputTerm});
                } else {
                    // This would reach if we don't have '<someclass> => <matchedDefn>' parser.
                    // e.g. "<timedtask> => <date> <time> <description>".
                    // For now, this doesn't happen.
                    
                    throw new IllegalStateException("Could not find parser for " +
                                                    syntaxTerm +
                                                    " => " +
                                                    nextSyntaxTerm); 
                }
            }
        }
        
        throw new IllegalStateException();
    }



    public List<String> getSuggestionsToDisplay() {
        // TODO: Stop cheating on this, as well =P
        List<String> displayedSuggestions = new ArrayList<String>();
        displayedSuggestions.add("Please ignore suggestions for now! ~CC");
        displayedSuggestions.add("add (name) (date) (time)");
        displayedSuggestions.add("remove (name)");
        displayedSuggestions.add("display");
        displayedSuggestions.add("exit");
        
        List<String> hintList = new ArrayList<String>();
        hintList.add("edit");
        hintList.add("add");
        hintList.add("remove");
        hintList.add("nil");
        hintList.add("nil");
        hints = new SuggestionHints(displayedSuggestions, hintList);
        
        return displayedSuggestions;
    }
    
    // Pre-Condition: Requires getSuggestionsToDisplay() to be called first
    public SuggestionHints getSuggestionHints() {
        return hints;
    }



    public String getCurrentSuggestion() {
        String output = "";
        if (getCurrentSuggestionIndex() != -1) {
            List<String> allStrings = getSuggestionsToDisplay();
            output = allStrings.get(getCurrentSuggestionIndex());
        }

        return output;
    }



    public void setCurrentSuggestionIndex(int i) {
        highlightedLine = i;
        hints.setSelected(i);
    }



    public int getCurrentSuggestionIndex() {
        return highlightedLine;
    }



    public void nextSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) %
                                  getSuggestionsToDisplay().size());
    }



    public void prevSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() - 1));
        if (getCurrentSuggestionIndex() < 0) {
            setCurrentSuggestionIndex(getSuggestionsToDisplay().size() - 1);
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
        // TODO: check with user if the result matched what they really want???
        List<jim.journal.Task> matchingTasks = new ArrayList<>();

        for (jim.journal.Task task : journal.getAllTasks()) {
            if (task.getDescription().equals(description)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private static String join(String arrayOfStrings[], char joinChar) {
        return join(arrayOfStrings, joinChar, 0);
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private static String join(String arrayOfStrings[],
                               char joinChar,
                               int startIndex) {
        return join(arrayOfStrings, joinChar, startIndex, arrayOfStrings.length);
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private static String join(String arrayOfStrings[],
                               char joinChar,
                               int startIndex,
                               int endIndex) {
        StringBuilder result = new StringBuilder();

        for (int i = startIndex; i < endIndex - 1; i++) {
            result.append(arrayOfStrings[i]);
            result.append(joinChar);
        }

        result.append(arrayOfStrings[endIndex - 1]);

        return result.toString();
    }



    private static boolean isStringSurroundedBy(String str, char begin, char end) {
        return str.charAt(0) == begin && str.charAt(str.length() - 1) == end;
    }



    private static String stripStringPrefixSuffix(String str, int n){
        // Not an efficient solution. <3 Recursion, though
        if (n <= 0) {
            return str;
        } else {
            // May not make sense if we just strip by n?
            String inner = str.substring(1, str.length() - 1);
            return stripStringPrefixSuffix(inner, n - 1);
        }
    }



    /**
     * Helper method for parsing Syntax.
     * Returns true if the given String is surrounded by '<' and '>'
     */
    private static boolean isSyntaxClass(String s) {
        // TODO: Eliminate magic values.
        return isStringSurroundedBy(s, '<', '>') && !s.contains(" ");
    }



    private static boolean isSyntaxRegex(String s){
        return isStringSurroundedBy(s, '/', '/');
    }



    private static boolean isSyntaxLiteral(String s){
        return isStringSurroundedBy(s, '\'', '\'');
    }



    /**
     * Checks whether the given input term matches the syntax term.
     * 
     * There are currently THREE types of syntax terms:
     * - Literals: e.g. 'add', matches "add" only.
     * - Regular Exrpessions: e.g. /a[bc]/, matches ab, ac
     * - Syntax classes: e.g. <date>, matches 11/22/33, etc.
     * 
     * LIMITATIONS:
     * * This can only handle single-word single-word matches.
     *   This limits both syntax, and input.
     *   It is expected this will be resolved in future.
     */
    private boolean isMatchSyntaxTermWithInputTerm(String syntaxTerm, String inputTerm) {
        // TODO: assert & debug.
        if (syntaxTerm.length() == 0) {
            return false;
        }
        
        //TODO: Memoization of results?
        String strippedTerm = stripStringPrefixSuffix(syntaxTerm, 1);

        if (isSyntaxLiteral(syntaxTerm)) {
            // syntaxTerm == 'something'
            String literal = strippedTerm;

            return literal.equals(inputTerm);
        } else if (isSyntaxRegex(syntaxTerm)) {
            // syntaxTerm = /something/
            // NOTE: Regex can only match one word/term at a time.
            String regex = strippedTerm;
            Pattern regexPattern = Pattern.compile(regex);
            Matcher regexMatcher = regexPattern.matcher(inputTerm);

            return regexMatcher.matches();
        } else if (isSyntaxClass(syntaxTerm)) {
            // syntaxTerm = <something> e.g. <date>, <time>, <task>, ...
            String syntaxClassName = strippedTerm;

            // Get the enum(?) for the current class name,
            //   for each item in that,
            //     try matching the current item...
            List<String> listOfSyntaxTerms = syntaxClassesMap.get(syntaxClassName);

            if (listOfSyntaxTerms == null) {
                throw new IllegalArgumentException("Given a syntax term with unknown class: " + syntaxTerm);
            } else {
                // Descend through the syntax terms.
                for (String nextSyntaxTerm : listOfSyntaxTerms) {
                    if (isMatchSyntaxTermWithInputTerm(nextSyntaxTerm, inputTerm)) {
                        return true;
                    }
                }

                return false;
            }
        } else {
            throw new IllegalStateException("An invalid syntax was given: " +
                                            syntaxTerm);
        }
    }



    private Object doParse(String syntax, String[] input){
        return doParse(syntax.split(" "), input);
    }



    private Object doParse(String syntax[], String[] input){
        // Our search-tree is implemented as a STACK,
        // (i.e. a DFS exploration of solution space).
        // A PriorityQueue may make more sense?
        Stack<SearchNode> searchNodes = new Stack<SearchNode>();

        List<SyntaxNode> initialSyntaxFormat = new ArrayList<SyntaxNode>(syntax.length);
        for (int i = 0; i < syntax.length; i++) {
            initialSyntaxFormat.add(new SyntaxNode(syntax[i]));
        }
        searchNodes.push(new SearchNode(initialSyntaxFormat, input));

        while(!searchNodes.isEmpty()) {
            SearchNode searchNode = searchNodes.pop();

            SearchMatchState searchState = searchNode.getMatchedState();

            switch (searchState) {
                case NO:
                    continue;

                case YES:
                	//TODO: LOG HERE
                    return doParseWithMatchedSearchNode(searchNode);

                case MAYBE:
                    List<SearchNode> nextNodes = searchNode.nextNodes();

                    for (int i = nextNodes.size() - 1; i >= 0; i--) {
                    	SearchNode nextNode = nextNodes.get(i);
                        searchNodes.push(nextNode);
                    }
                    
                    break;

                default:
                    throw new IllegalStateException("Unknown SearchMatchState: " + searchState);
            }
        }

        // If we get to here, then
        // the input could not be matched with any of the
        // defined syntax formats.
        return null;
    }
    
    
    
    // node -> "className => ..." 
    private String getSyntaxParserKeyForSyntaxNode(SyntaxNode node) {
    	StringBuilder result = new StringBuilder();
    	
    	String syntaxClassName = stripStringPrefixSuffix(node.syntaxTerm, 1);
    	result.append(syntaxClassName);
    	result.append(" =>"); // MAGIC
    	
    	for(SyntaxNode childNode : node.getChildren()){
    		result.append(' ');
    		result.append(childNode.syntaxTerm);
    	}
    	
    	return result.toString();
    }



    private Object doParseWithMatchedSearchNode(SearchNode searchNode){
        assert searchNode.getMatchedState() == SearchMatchState.YES;

        // TODO: SLAP This away.
        // Get root SyntaxNode from search.
        SyntaxNode root = searchNode.syntaxFormat.get(0);
        while (root.parent != null) {
        	root = root.parent;
        }

        SyntaxNode node = root;
        
        while (true) {
        	SyntaxParser parser = syntaxParsers.get(getSyntaxParserKeyForSyntaxNode(node));
        	
        	if (parser != null) {
        		// Match terms of children, pass to parser.
        		String[] input = node.getMatchedInputOfChildren();
        		return parser.parse(input);
        	} else if(node.getChildren().size() == 1) {
        		node = node.getChildren().get(0);
        	} else {
        		throw new IllegalStateException("Unable to parse against matched node.");
        	}
        }
    }


    
    /**
     *
     * @param syntaxLine
     *            in format "<syntaxClassName> := somesyntax [| somesyntax]*"
     */
    private void addSyntax(String syntaxLine) {
        String[] syntaxLineParts = syntaxLine.split(" := ");
        String syntaxClassName = stripStringPrefixSuffix(syntaxLineParts[0], 1);
        String[] definedAsSyntaxTerms = syntaxLineParts[1].split(" \\| ");
        
        syntaxClassesMap.put(syntaxClassName,
                             Arrays.asList(definedAsSyntaxTerms));
    }



    private MutableDateTime parseDate(String date) {
        // TODO: Abstract Date parsing like AddCommand
        // ACCEPTED FORMATS: dd/mm/yy
        //SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_DATE_DDMMYY);
    	
        int[] takeDateArray = splitDate(removeAllSymbols(date));
        int YY = takeDateArray[2], MM = takeDateArray[1], DD = takeDateArray[0];

        MutableDateTime result = new MutableDateTime(YY,MM,DD,0,0,0,0);
        /*result.setDateTime(dateFormat.parse(date));*/

        return result;
    }



    public jim.journal.Task parseTask(String[] input) {
        return (jim.journal.Task) doParse(new String[]{"<task>"}, input);
    }



    /**
     * Takes an array of strings, e.g. {"add", "lunch", "Monday", "2pm"}, and
     * returns a Journal command to be executed from this.
     * 
     * TODO: Potentially throw some kind of "Poor format exception" as a better
     * way of giving feedback than return-null.
     */
    public jim.journal.Command parseCommand(String args[]) {
        /*
         * TODO: Here is where some of our time will be spent in V0.0, getting
         * stuff like "add lunch Monday" (or some sensible command) to work.
         * 
         * Adding logic in TDD fashion is *ideal* for this kind of thing, I
         * would think.
         * 
         * Once basic format rules have been established, (e.g. strict syntax
         * for add, remove, etc.), THEN Any language logic here can infer fairly
         * sensibly how to access Journal API. Journal API can then trust that
         * this will be done sensibly by language logic.
         */

        // Parse the words into a Command object.
        // STRICT ASSUMPTION is that the first word is the "operating word".
        // (e.g. add, remove, etc.)
        // Naturally, this assumption will be broken with more flexible inputs.
        if (args[0].equals("add")) {
            return parseAddCommand(args);
        } else if (args[0].equals("complete")) {
            return parseCompleteCommand(args);
        } else if (args[0].equals("remove")) {
            return parseRemoveCommand(args);
        } else if (args[0].equals("edit")) {
            return parseEditCommand(args);
        } else if (args[0].equals("search")) {
            return parseSearchCommand(args);
        } else if (args[0].equals("display")) {
            return parseDisplayCommand(args);
        } else if (args[0].equals("undo")){
            return parseUndoCommand();
        }
        return null;
    }


    private static String removeAllSymbols(String tellDateOrTime) {
        String findDate = tellDateOrTime.replaceAll("[^\\p{L}\\p{Nd}]", "");
        return findDate;
    }



    private int[] splitDate(String date_in_string) {
        // we will accept date format of 090913 - DDMMYY
        int[] dates = new int[LENGTH_OF_DATE];
        String[] temp = date_in_string.split("");
        int counter = 1; // temp[0] is a spacing
        for (int i = 0; i < LENGTH_OF_DATE; i++) {
            if (i == INDICATE_YEAR) {
                dates[i] = Integer.parseInt("20" +
                                            temp[counter++] +
                                            temp[counter++]);
            } else if (i == INDICATE_MONTH) {
                dates[i] = Integer.parseInt(temp[counter++] + temp[counter++]);
                dates[i] = dates[i];
            } else {
                dates[i] = Integer.parseInt(temp[counter++] + temp[counter++]);
            }
        }
        return dates;
    }

    private AddCommand parseAddCommand(String[] args) {
        return (AddCommand) doParse("<addcmd>", args);
    }



    private CompleteCommand parseCompleteCommand(String args[]) { // The
                                                                  // "Complete"
                                                                  // commands
        // Accepted 'complete' syntaxes:
        // complete <description>

        String description = join(args, ' ', 1);
        return new jim.journal.CompleteCommand(description);
    }

    private RemoveCommand parseRemoveCommand(String args[]) { // The "Remove"
                                                              // commands
        // Accepted 'remove' syntaxes:
        // remove <description>
        // TODO: Add more syntaxes/formats for this command

        String description = join(args, ' ', 1);
        return new jim.journal.RemoveCommand(description);
    }



    private EditCommand parseEditCommand(String args[]) { // The "Edit"
                                                          // commands
        // Accepted 'edit' syntaxes:
        // edit <description of old task>
        // (then read in from input what to replace it with).
        // TODO: Add more syntaxes/formats for this command
        // NOTE THAT: The format read in is a format which describes a Task.
        // (Timed, floating, etc.)

        String description = join(args, ' ', 1);
        EditCommand editCmd = new EditCommand(description);
        editCmd.setInputSource(inputSource);
        
        return editCmd;
    }



    private SearchCommand parseSearchCommand(String args[]) { // The "Search"
                                                              // commands
        // Accepted 'search' syntaxes:
        // search <description>
        // TODO: Add more syntaxes/formats for this command

        String description = join(args, ' ', 1);
        return new jim.journal.SearchCommand(description);
    }



    private DisplayCommand parseDisplayCommand(String args[]) { // The
                                                                // "Display"
                                                                // commands
        // Accepted 'display' syntaxes:
        // display
        // display <date>
        // TODO: Basic display command currently displays everything; Ideally we
        // want to limit this ~CC
        // TODO: Add more syntaxes/formats for this command

        if (args.length == 1) {
            // User asks to perform a plain display, which displays everything
            return new jim.journal.DisplayCommand();
        } else if (args.length == 2) {
            // User asks to perform display given a particular date

            // TODO: Currently, display expects a date in DD-MM-YY format; We
            // should change that ~CC
            MutableDateTime date = (MutableDateTime) parseInputTermWithSyntaxClass("date", args[1]);

            return new jim.journal.DisplayCommand(date);
        }

        return null;
    }
    
    private Command parseUndoCommand() { //The
                                         //"undo"
                                         //command
        // TODO Auto-generated method stub
        return new jim.journal.UndoCommand();
    }
    
}
