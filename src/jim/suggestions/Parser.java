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


    
    private static abstract class SyntaxTerm {
        public abstract boolean matches(String s);

        public boolean isDisplayable() {
            return false;
        }

        public String generate() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        public static SyntaxTerm valueOf(String syntaxTerm) {
            String strippedTerm = stripStringPrefixSuffix(syntaxTerm, 1);

            if (isSyntaxLiteral(syntaxTerm)) {
                return new LiteralSyntaxTerm(strippedTerm);
            } else if (isSyntaxRegex(syntaxTerm)) {
                return new RegexSyntaxTerm(strippedTerm);
            } else if(isSyntaxClass(syntaxTerm)) {
                return new SyntaxClassSyntaxTerm(strippedTerm);
            } else {
                throw new IllegalStateException("An invalid syntax was given: " +
                                                syntaxTerm);
            }
        }
    }



	private static  class LiteralSyntaxTerm extends SyntaxTerm {
        private String literalValue;

        /**
         * The "escaped" value. e.g. "'a'" -> LiteralSyntaxTerm("a").
         */
        public LiteralSyntaxTerm(String value) {
            literalValue = value;
        }

        @Override
        public boolean matches(String inputTerm) {
            return literalValue.equals(inputTerm);
        }

        @Override
        public boolean isDisplayable() {
            return true;
        }

        @Override
        public String generate() {
            return literalValue;
        }

        @Override
        public String toString() {
        	return "'" + literalValue + "'";
        }
    }



	private static class RegexSyntaxTerm extends SyntaxTerm {
        private Pattern regexPattern;

        public RegexSyntaxTerm(String regexStr) {
            regexPattern = Pattern.compile(regexStr);
        }

        @Override
        public boolean matches(String inputTerm) {
            Matcher regexMatcher = regexPattern.matcher(inputTerm);

            return regexMatcher.matches();
        }

        @Override
        public String toString() {
        	return "/" + regexPattern.toString() + "/";
        }
    }



	private static class SyntaxClassSyntaxTerm extends SyntaxTerm {
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
        public String generate() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public String toString() {
        	return "<" + syntaxClassName + ">";
        }
    }



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



    private static class SyntaxParserKey {
        private String synClass;
        private String synFormat;

        public SyntaxParserKey(String syntaxClassName, String syntaxFormat) {
            synClass = syntaxClassName;
            synFormat = syntaxFormat;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof SyntaxParserKey) {
                SyntaxParserKey otherKey = (SyntaxParserKey) o;
                return synClass.equals(otherKey.synClass) &&
                       synFormat.equals(otherKey.synFormat);
            }

            return false;
        }
        
        @Override
        public int hashCode() {
            return (synClass + synFormat).hashCode();
        }

        @Override
        public String toString() {
            return synClass + " => " + synFormat;
        }

        /**
         * @param keyString in format of "syntaxClassName => syntaxFormat"
         */
        public static SyntaxParserKey valueOf(String keyString) {
            String[] parts = keyString.split(" => ");
            String className = parts[0];
            String format = parts[1];

            return new SyntaxParserKey(className, format);
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
        List<SyntaxTermSearchNode> syntaxFormat;
        String[] inputArray;

        public SearchNode(List<SyntaxTermSearchNode> syntax, String[] input){
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

        protected boolean isDisplayable() {
            for (SyntaxTermSearchNode node : syntaxFormat) {
                if (!node.isDisplayable()) {
                    return false;
                }
            }

            return true;
        }

        private boolean isAllSyntaxNodesTerminal() {
            for (SyntaxTermSearchNode node : syntaxFormat) {
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
                SyntaxTermSearchNode node = syntaxFormat.get(i);
                
                if (!node.isMatched(inputArray[i])) {
                    isAllMatched = false;
                    break;
                }
            }
            
            
            if (isAllMatched) {
                // TODO: SLAP this away?
                // Add all the nodes to their parents.
                for (int i = 0; i < syntaxFormat.size(); i++) {
                    SyntaxTermSearchNode node = syntaxFormat.get(i);
                    node.ensureAddedToParent();
                }
            } else {
                // TODO: SLAP this away?
                // Since we didn't successfully match, reset.
                for (int i = 0; i < syntaxFormat.size(); i++) {
                    SyntaxTermSearchNode node = syntaxFormat.get(i);
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
                SyntaxTermSearchNode node = syntaxFormat.get(i);

                // Expand the first non-terminal term.
                if (!node.isTerminal()) {
                    List<SyntaxTermSearchNode> preList = syntaxFormat.subList(0, i);
                    List<SyntaxTermSearchNode> postList = syntaxFormat.subList(i + 1, syntaxFormat.size());

                    SyntaxClassSyntaxTerm classNode = (SyntaxClassSyntaxTerm) node.syntaxTerm;
                    String syntaxClassName = classNode.syntaxClassName;
                    List<String> syntaxClassDefinitionsList = syntaxClassesMap.get(syntaxClassName);
                    
                    if (syntaxClassDefinitionsList == null) {
                        throw new IllegalArgumentException("Given a syntax term with unknown class: " + node.syntaxTerm);
                    }

                    List<SearchNode> nextSearchNodes = new ArrayList<SearchNode> (syntaxClassDefinitionsList.size());


                    for (String nextSyntaxDefinition : syntaxClassDefinitionsList) {
                        List<SyntaxTermSearchNode> nextSyntaxNodes = new LinkedList<SyntaxTermSearchNode>();
                        
                        nextSyntaxNodes.addAll(preList);
                        for(String syntaxTerm : nextSyntaxDefinition.split(" ")){
                            SyntaxTermSearchNode newNode = new SyntaxTermSearchNode(SyntaxTerm.valueOf(syntaxTerm));
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

            for (SyntaxTermSearchNode synTerm : syntaxFormat) {
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



    private class SyntaxTermSearchNode {
        SyntaxTermSearchNode parent;
        private boolean hasBeenAddedToParent = false;
        
        private List<SyntaxTermSearchNode> childrenNodes = new ArrayList<SyntaxTermSearchNode>();
        SyntaxTerm syntaxTerm; // e.g. <date>, "add", /abc/, ...
        String inputTerm = null;

        public SyntaxTermSearchNode(SyntaxTerm syntax) {
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

        protected boolean isDisplayable() {
            return syntaxTerm.isDisplayable();
        }

        public boolean isTerminal() {
            // "if not a syntax class"
            return syntaxTerm instanceof LiteralSyntaxTerm ||
                   syntaxTerm instanceof RegexSyntaxTerm;
        }

        public List<SyntaxTermSearchNode> getChildren() {
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
            boolean matched = syntaxTerm.matches(input);

            if (matched) {
                // We record the last input term we successfully matched against.
                inputTerm = input;
            }
            
            return matched;
        }
    }




    private final Map<String, List<String>> syntaxClassesMap = new HashMap<String, List<String>>();
    private final Map<SyntaxParserKey, SyntaxParser> syntaxParsers = new HashMap<SyntaxParserKey, SyntaxParser>();
    
    
    
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
                  "<editcmd> | <searchcmd> | <displaycmd>");
        
        initSyntaxParsers();
    }



    private void initSyntaxParsers() {
        // KEY: syntaxTerm + " => " + nextSyntaxTerm
        SyntaxTermParser genericDDMMYYParser =
        new SyntaxTermParser(){
            @Override
            public Object parse(String inputTerm) {
                return parseDate(inputTerm);
            }
        };
        registerSyntaxParser("date => /" + REGEX_DATE_DDMMYY + "/",
                          genericDDMMYYParser);
        registerSyntaxParser("date => /\\d\\d\\d\\d\\d\\d/",
                          genericDDMMYYParser);
        registerSyntaxParser("date => /\\d\\d-\\d\\d-\\d\\d/",
                          genericDDMMYYParser);


        registerSyntaxParser("time => /" + REGEX_TIME_HHMM + "/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  int hh = Integer.parseInt(inputTerm.substring(0, 2));
                                  int mm = Integer.parseInt(inputTerm.substring(2));
                                  
                                  return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
                              }
                          });
        registerSyntaxParser("time => /\\d\\d:\\d\\d/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  int hh = Integer.parseInt(inputTerm.substring(0, 2));
                                  int mm = Integer.parseInt(inputTerm.substring(3));
                                  
                                  return new MutableDateTime(0, 0, 0, hh, mm, 00, 00);
                              }
                          });
        

        // Redundant?
        registerSyntaxParser("word => /\\S+/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  return inputTerm;
                              }
                          });


        // Redundant?
        registerSyntaxParser("phrase => <word>",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  // Get parser for <word> ...
                                  return inputTerm;
                              }
                          });
        registerSyntaxParser("phrase => <word> <phrase>",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String input) {
                                  return input;
                              }
                          });


        registerSyntaxParser("timedtask => <date> <time> <date> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime startDate =
                                          (MutableDateTime) doParse("<date>", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) doParse("<time>", input[1]);
                                  MutableDateTime endDate =
                                          (MutableDateTime) doParse("<date>", input[2]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) doParse("<time>", input[3]);
                                  String description = input[4];
                                  return new TimedTask(datetime(startDate, startTime),
                                                       datetime(endDate, endTime), description);
                              }
                          });
        registerSyntaxParser("timedtask => <description> <date> <time> <date> <time>",
                new SyntaxParser() {
                    @Override
                    public Object parse(String[] input) {
                        String description = input[0];
                        MutableDateTime startDate =
                                (MutableDateTime) doParse("<date>", input[1]);
                        MutableDateTime startTime =
                                (MutableDateTime) doParse("<time>", input[2]);
                        MutableDateTime endDate =
                                (MutableDateTime) doParse("<date>", input[3]);
                        MutableDateTime endTime =
                                (MutableDateTime) doParse("<time>", input[4]);
                        return new TimedTask(datetime(startDate, startTime),
                                             datetime(endDate, endTime), description);
                    }
                });
        registerSyntaxParser("timedtask => <date> <time> 'to' <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) doParse("<date>", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) doParse("<time>", input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) doParse("<time>", input[3]);
                                  String description = input[4];
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       description);
                              }
                          });
        registerSyntaxParser("timedtask => <date> <time> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) doParse("<date>", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) doParse("<time>", input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) doParse("<time>", input[2]);
                                  String description = input[3];
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       description);
                              }
                          });
        registerSyntaxParser("deadlinetask => <date> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) doParse("<date>", input[0]);
                                  String description = input[1];
                                  return new DeadlineTask(date, description);
                              }
                          });
        
        
        registerSyntaxParser("floatingtask => <description>",
                          new SyntaxTermParser() {
                              @Override
                              public Object parse(String input) {
                                  return new FloatingTask(input);
                              }
                          });
        
        
        registerSyntaxParser("addcmd => 'add' <task>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  jim.journal.Task taskToAdd = parseTask(input[1].split(" "));
                                  return new AddCommand(taskToAdd);
                              }
                          });
        
        
        registerSyntaxParser("completecmd => 'complete' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.CompleteCommand(input[1]);
                              }
                          });
        
        
        registerSyntaxParser("removecmd => 'remove' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.RemoveCommand(input[1]);
                              }
                          });
        
        
        registerSyntaxParser("editcmd => 'edit' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  String description = input[1];
                                  EditCommand editCmd = new EditCommand(description);
                                  
                                  return editCmd;
                              }
                          });
        
        
        registerSyntaxParser("searchcmd => 'search' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new SearchCommand(input[1]);
                              }
                          });
        
        
        registerSyntaxParser("displaycmd => 'display'",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.DisplayCommand();
                              }
                          });
        
        
        registerSyntaxParser("displaycmd => 'display' <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                        (MutableDateTime) doParse("<date>", input[1]);

                                  return new jim.journal.DisplayCommand(date);
                              }
                          });
        
        
        registerSyntaxParser("undocmd => 'undo'",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new UndoCommand();
                              }
                          });
        

    }



    /**
     * @param syntaxParserKey in format of "syntaxClassName => syntaxFormat"
     */
    private void registerSyntaxParser(String syntaxParserKey, SyntaxParser syntaxParser) {
        registerSyntaxParser(SyntaxParserKey.valueOf(syntaxParserKey), syntaxParser);
    }



    private void registerSyntaxParser(SyntaxParserKey syntaxParserKey, SyntaxParser syntaxParser) {
    	LOGGER.info("Registering SyntaxParser: " + syntaxParserKey);
        syntaxParsers.put(syntaxParserKey, syntaxParser);
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



    protected Object doParse(String syntax, String input){
        return doParse(syntax.split(" "), input.split(" "));
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

        List<SyntaxTermSearchNode> initialSyntaxFormat = new ArrayList<SyntaxTermSearchNode>(syntax.length);
        for (int i = 0; i < syntax.length; i++) {
            initialSyntaxFormat.add(new SyntaxTermSearchNode(SyntaxTerm.valueOf(syntax[i])));
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
    private SyntaxParserKey getSyntaxParserKeyForSyntaxNode(SyntaxTermSearchNode node) {
        assert node.syntaxTerm instanceof SyntaxClassSyntaxTerm;
        
        String syntaxClassName = ((SyntaxClassSyntaxTerm) node.syntaxTerm).syntaxClassName;
        
        StringBuilder synFormat = new StringBuilder();
        for(SyntaxTermSearchNode childNode : node.getChildren()){
            synFormat.append(childNode.syntaxTerm);
            synFormat.append(' ');
        }
        synFormat.deleteCharAt(synFormat.length() - 1); // remove last ' '

        return new SyntaxParserKey(syntaxClassName, synFormat.toString());
    }



    private Object doParseWithMatchedSearchNode(SearchNode searchNode){
        assert searchNode.getMatchedState() == SearchMatchState.YES;

        // TODO: SLAP This away.
        // Get root SyntaxNode from search.
        SyntaxTermSearchNode root = searchNode.syntaxFormat.get(0);
        while (root.parent != null) {
            root = root.parent;
        }

        SyntaxTermSearchNode node = root;
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




    private String getUserFriendlyDefinitonString(String definition) {
        assert syntaxClassesMap.containsKey(definition);
        List<String> listOfDefns = syntaxClassesMap.get(definition);

        StringBuilder result = new StringBuilder();
        result.append(String.format("%16s", "<" + definition + ">"));
        result.append(" := ");

        result.append(listOfDefns.get(0));

        for (int i = 1; i < listOfDefns.size(); i++) {
            result.append(" | ");
            result.append(listOfDefns.get(i));
        }

        return result.toString();
    }



    /**
     * Returns a user-friendly representation oft the definitions.
     */
    private String getUserFriendlyDefinitionsString() {
        // TODO: Remove magic here, by being able to sort keys in order.
        
        String[] definitionClassNames = new String[]{"date",
                                                    "time",
                                                    "word",
                                                    "phrase",
                                                    "description",
                                                    "timedtask",
                                                    "deadlinetask",
                                                    "floatingtask",
                                                    "task",
                                                    "addcmd",
                                                    "completecmd",
                                                    "removecmd",
                                                    "editcmd",
                                                    "searchcmd",
                                                    "displaycmd",
                                                    "undocmd",
                                                    "cmd"};
        
        StringBuilder result = new StringBuilder();

        for (String defnClassName : definitionClassNames) {
            result.append(getUserFriendlyDefinitonString(defnClassName));
            result.append('\n');
        }

        return result.toString();
    }



    public List<SearchNode> getDisplayableSyntaxTreeLeafNodes() {

        List<SearchNode> result = new ArrayList<SearchNode>();

        // Our search-tree is implemented as a STACK,
        // (i.e. a DFS exploration of solution space).
        // A PriorityQueue may make more sense?
        Stack<SearchNode> searchNodes = new Stack<SearchNode>();

        List<SyntaxTermSearchNode> initialSyntaxFormat = new ArrayList<SyntaxTermSearchNode>();
        initialSyntaxFormat.add(new SyntaxTermSearchNode(SyntaxTerm.valueOf("<cmd>"))); // <cmd> as the root.
        searchNodes.push(new SearchNode(initialSyntaxFormat, new String[]{}));

        while(!searchNodes.isEmpty()) {
            SearchNode searchNode = searchNodes.pop();

            if (searchNode.isDisplayable()) {
                // output
                result.add(searchNode);
            } else {
                List<SearchNode> nextNodes = searchNode.nextNodes();

                for (int i = nextNodes.size() - 1; i >= 0; i--) {
                    SearchNode nextNode = nextNodes.get(i);
                    searchNodes.push(nextNode);
                }
            }
        }

        return result;
    }



    public List<String> getDisplayableSyntaxTreeLeafs() {
        List<String> result = new ArrayList<String>();

        for (SearchNode node : getDisplayableSyntaxTreeLeafNodes()) {
            // - 3 is a MAGIC hack so that the " <-" from SearchNode doesn't show.
            result.add(node.toString().substring(0, node.toString().length() - 3));
        }

        return result;
    }



    public static void main(String args[]) {
        System.out.println("Parser:");

        Parser p = new Parser();

        System.out.println("Syntax Definition:");
        System.out.println(p.getUserFriendlyDefinitionsString());

        System.out.println("\n\n");
        System.out.println("Displayable Formats:");
        for (String s : p.getDisplayableSyntaxTreeLeafs()) {
            System.out.println(s);
        }
    }
}
