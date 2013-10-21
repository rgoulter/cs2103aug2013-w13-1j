package jim.suggestions;

import static jim.util.StringUtils.isStringSurroundedBy;
import static jim.util.StringUtils.join;
import static jim.util.StringUtils.stripStringPrefixSuffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jim.journal.AddCommand;
import jim.journal.DeadlineTask;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.SearchCommand;
import jim.journal.TimedTask;
import jim.journal.UndoCommand;

import org.joda.time.MutableDateTime;

import static jim.util.StringUtils.isStringSurroundedBy;
import static jim.util.StringUtils.join;
import static jim.util.StringUtils.stripStringPrefixSuffix;
import static jim.util.StringUtils.splitDate;
import static jim.util.StringUtils.removeAllSymbols;
import static jim.util.DateUtils.datetime;

public class Parser {
    /**
     * Matches DD/MM/YY.
     */
    private static final String REGEX_DATE_DDMMYY = "\\d\\d/\\d\\d/\\d\\d";

    /**
     * Matches four digits in a row. e.g. HHMM.
     */
    private static final String REGEX_TIME_HHMM = "\\d\\d\\d\\d";
    
    private final static Logger LOGGER = Logger.getLogger(Parser.class .getName()); 

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
    
    
    
    public Parser() {
    	initSyntax();
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
                  "<description> <date> <time> <date> <time> | " +
                  "<date> <time> 'to' <time> <description> | " +
                  "<date> <time> <time> <description>");
        addSyntax("<deadlinetask> := <date> <description>");
        addSyntax("<floatingtask> := <description>");
        addSyntax("<task> := <timedtask> | <deadlinetask> | <floatingtask>");
        
        addSyntax("<addcmd> := 'add' <task>");
        addSyntax("<completecmd> := 'complete' <description>");
        addSyntax("<removecmd> := 'remove' <description>");
        addSyntax("<editcmd> := 'edit' <description>");
        addSyntax("<searchcmd> := 'search' <description>");
        addSyntax("<displaycmd> := 'display' | 'display' <date>");
        addSyntax("<undocmd> := 'undo'");
        
        addSyntax("<cmd> := " +
                  "<addcmd> | <completecmd> | <removecmd> | " + 
        		  "<editcmd> | <searchcmd> | <displaycmd> | <undocmd>");
        
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
        syntaxParsers.put("timedtask => <description> <date> <time> <date> <time>",
                new SyntaxParser() {
                    @Override
                    public Object parse(String[] input) {
                        String description = input[0];
                        MutableDateTime startDate =
                                (MutableDateTime) parseInputTermWithSyntaxClass("date", input[1]);
                        MutableDateTime startTime =
                                (MutableDateTime) parseInputTermWithSyntaxClass("time", input[2]);
                        MutableDateTime endDate =
                                (MutableDateTime) parseInputTermWithSyntaxClass("date", input[3]);
                        MutableDateTime endTime =
                                (MutableDateTime) parseInputTermWithSyntaxClass("time", input[4]);
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
                                  jim.journal.Task taskToAdd = parseTask(input[1].split(" "));
                                  return new AddCommand(taskToAdd);
                              }
                          });
        
        
        syntaxParsers.put("completecmd => 'complete' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                            	  return new jim.journal.CompleteCommand(input[1]);
                              }
                          });
        
        
        syntaxParsers.put("removecmd => 'remove' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                            	  return new jim.journal.RemoveCommand(input[1]);
                              }
                          });
        
        
        syntaxParsers.put("editcmd => 'edit' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  String description = input[1];
                                  EditCommand editCmd = new EditCommand(description);
                                  // TODO: Reduce coupling from this statement.
                                  //editCmd.setInputSource(inputSource);
                                  
                                  return editCmd;
                              }
                          });
        
        
        syntaxParsers.put("searchcmd => 'search' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new SearchCommand(input[1]);
                              }
                          });
        
        
        syntaxParsers.put("displaycmd => 'display'",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.DisplayCommand();
                              }
                          });
        
        
        syntaxParsers.put("displaycmd => 'display' <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                            	  MutableDateTime date =
                        			  	(MutableDateTime) parseInputTermWithSyntaxClass("date", input[1]);

                                  return new jim.journal.DisplayCommand(date);
                              }
                          });
        
        
        syntaxParsers.put("undocmd => 'undo'",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                            	  return new UndoCommand();
                              }
                          });
        

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



    protected Object doParse(String syntax, String[] input){
        return doParse(syntax.split(" "), input);
    }



    protected Object doParse(String syntax[], String[] input){
    	LOGGER.log(Level.INFO, "doParse: " + join(syntax, ' ') + " " + join(input, ' '));
    	
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
                	LOGGER.log(Level.INFO, "Matched SearchNode: " + searchNode.toString());
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
        throw new IllegalArgumentException("Given arguments do not conform to any defined syntax: " + join(input, ' '));
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
    	LOGGER.log(Level.INFO, "Parsing, matching search node, root node: " + root.syntaxTerm);
        
        while (true) {
        	LOGGER.log(Level.INFO, "Parsing, looking for parser: " + getSyntaxParserKeyForSyntaxNode(node));
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



    protected jim.journal.Task parseTask(String[] input) {
        return (jim.journal.Task) doParse(new String[]{"<task>"}, input);
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
}
