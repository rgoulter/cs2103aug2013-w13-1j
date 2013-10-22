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

import static jim.suggestions.SyntaxGrammar.initSyntax;
import static jim.suggestions.SyntaxParsers.SyntaxParserKey;
import static jim.suggestions.SyntaxParsers.SyntaxParser;
import static jim.suggestions.SyntaxParsers.initSyntaxParsers;

public class Parser {
    
    private final static Logger LOGGER = Logger.getLogger(Parser.class .getName()); 



    /**
	 * Abstraction for sequences of terms.
	 * e.g. Strings like:
	 * "'add' <date> <time> <description>"
	 */
    protected static class SyntaxFormat {
		private SyntaxTerm[] syntaxTerms;
		
		public SyntaxFormat(SyntaxTerm[] terms) {
			syntaxTerms = terms;
		}
		
		public SyntaxTerm[] getSyntaxTerms() {
			return syntaxTerms;
		}
		
		@Override
		public String toString() {
			String[] syntaxTermStrings = new String[syntaxTerms.length];
			
			for (int i = 0; i < syntaxTermStrings.length; i++) {
				syntaxTermStrings[i] = syntaxTerms[i].toString();
			}
			
			return join(syntaxTermStrings, ' ');
		}
		
		public static SyntaxFormat valueOf(String formatStr) {
			return valueOf(formatStr.split(" "));
		}
		
		public static SyntaxFormat valueOf(String[] syntaxTermStrings) {
			SyntaxTerm[] syntaxTerms = new SyntaxTerm[syntaxTermStrings.length];
			
			for (int i = 0; i < syntaxTermStrings.length; i++) {
				assert syntaxTermStrings[i].length() > 0 : "SyntaxFormat given syntax term is zero length";
				syntaxTerms[i] = SyntaxTerm.valueOf(syntaxTermStrings[i]);
			}
			
			return new SyntaxFormat(syntaxTerms);
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
                    String syntaxClassName = classNode.getSyntaxClassName();
                    List<SyntaxFormat> syntaxClassDefinitionsList = syntaxClassesMap.get(syntaxClassName);
                    
                    if (syntaxClassDefinitionsList == null) {
                        throw new IllegalArgumentException("Given a syntax term with unknown class: " + node.syntaxTerm);
                    }

                    List<SearchNode> nextSearchNodes = new ArrayList<SearchNode> (syntaxClassDefinitionsList.size());


                    for (SyntaxFormat nextSyntaxDefinition : syntaxClassDefinitionsList) {
                        List<SyntaxTermSearchNode> nextSyntaxNodes = new LinkedList<SyntaxTermSearchNode>();
                        
                        nextSyntaxNodes.addAll(preList);
                        for(SyntaxTerm syntaxTerm : nextSyntaxDefinition.getSyntaxTerms()){
                            SyntaxTermSearchNode newNode = new SyntaxTermSearchNode(syntaxTerm);
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




    private final Map<String, List<SyntaxFormat>> syntaxClassesMap = new HashMap<String, List<SyntaxFormat>>();
    private final Map<SyntaxParserKey, SyntaxParser> syntaxParsers = new HashMap<SyntaxParserKey, SyntaxParser>();
    
    
    
    public Parser() {
        initSyntax(this);
        initSyntaxParsers(this);
    }



    protected void registerSyntaxParser(SyntaxParserKey syntaxParserKey, SyntaxParser syntaxParser) {
    	LOGGER.info("Registering SyntaxParser: " + syntaxParserKey);
        syntaxParsers.put(syntaxParserKey, syntaxParser);
    }



    protected Object doParse(String syntax, String input){
        return doParse(SyntaxFormat.valueOf(syntax), input.split(" "));
    }



    protected Object doParse(String syntax, String[] input){
        return doParse(SyntaxFormat.valueOf(syntax), input);
    }



    protected Object doParse(SyntaxFormat syntaxFormat, String[] input){
        LOGGER.log(Level.INFO, "doParse: " + syntaxFormat + " " + join(input, ' '));
        
        // Our search-tree is implemented as a STACK,
        // (i.e. a DFS exploration of solution space).
        // A PriorityQueue may make more sense?
        Stack<SearchNode> searchNodes = new Stack<SearchNode>();

        // TODO: SLAP derive searchFormatFromSyntaxFormat
        List<SyntaxTermSearchNode> initialSyntaxFormat =
        		new ArrayList<SyntaxTermSearchNode>(syntaxFormat.getSyntaxTerms().length);
        for (int i = 0; i < syntaxFormat.getSyntaxTerms().length; i++) {
        	SyntaxTerm term = syntaxFormat.getSyntaxTerms()[i];
            initialSyntaxFormat.add(new SyntaxTermSearchNode(term));
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
    
    
    
    // node -> (className, syntaxFormat) 
    private SyntaxParserKey getSyntaxParserKeyForSyntaxNode(SyntaxTermSearchNode node) {
        assert node.syntaxTerm instanceof SyntaxClassSyntaxTerm;
        
        SyntaxTerm[] syntaxTerms = new SyntaxTerm[node.getChildren().size()];
        List<SyntaxTermSearchNode> childNodes = node.getChildren();
        
        for(int i = 0; i < syntaxTerms.length; i++){
    		syntaxTerms[i] = childNodes.get(i).syntaxTerm;
        }
        
        String syntaxClassName = ((SyntaxClassSyntaxTerm) node.syntaxTerm).getSyntaxClassName();
        SyntaxFormat syntaxFormat = new SyntaxFormat(syntaxTerms);

        return new SyntaxParserKey(syntaxClassName, syntaxFormat);
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
        return (jim.journal.Task) doParse("<task>", input);
    }


    
    /**
     *
     * @param syntaxLine
     *            in format "<syntaxClassName> := somesyntax [| somesyntax]*"
     */
    protected void addSyntax(String syntaxLine) {
        String[] syntaxLineParts = syntaxLine.split(" := ");
        String syntaxClassName = stripStringPrefixSuffix(syntaxLineParts[0], 1);
        String[] definedAsSyntaxTerms = syntaxLineParts[1].split(" \\| ");
        
        List<SyntaxFormat> definitions = new ArrayList<SyntaxFormat>(definedAsSyntaxTerms.length);
        
        for(int i = 0; i < definedAsSyntaxTerms.length; i++) {
        	definitions.add(SyntaxFormat.valueOf(definedAsSyntaxTerms[i]));
        }
        
        syntaxClassesMap.put(syntaxClassName,
                             definitions);
    }




    private String getUserFriendlyDefinitonString(String definition) {
        assert syntaxClassesMap.containsKey(definition);
        List<SyntaxFormat> listOfDefns = syntaxClassesMap.get(definition);

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
