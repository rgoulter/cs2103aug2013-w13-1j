package jim.suggestions;

import java.util.ArrayList;
import java.util.List;


public class SuggestionHints {
    // HTML Coloration Constants
    private static final String BLACK_COLOR = "black";
    private static final String RED_COLOR = "red";
    private static final String BLUE_COLOR = "blue";
    private static final String PURPLE_COLOR = "purple";
    private static final String GREEN_COLOR = "green";

    private static final String TEXT_COLOR = BLACK_COLOR;
    private static final String SYNTAX_CLASS_COLOR = PURPLE_COLOR;

    // HTML Constants (Other Commands)
    private static final String HTML_NEWLINE = "<br>";
    private static final String HTML_START_BOLD = "<b>";
    private static final String HTML_END_BOLD = "</b>";
    private static final String HTML_START_DOCUMENT = "<html><body>";
    private static final String HTML_END_DOCUMENT = "</body></html>";
    
    // Working Variables
    private List<SuggestionHint> suggestions;
    private int selectedHint = 0;
    private String currentInput;
    
    // Constructors
    public SuggestionHints(List<SuggestionHint> suggestionHints, String currentInput) {
    	suggestions = suggestionHints;
    	this.currentInput = currentInput;
    }
    
    
    // Accessors
    @Override
    public String toString() {
    	// Assumes whoever does ".toString()" on SuggestionHints wants
    	// an HTML rendering..
        StringBuilder result = new StringBuilder(HTML_START_DOCUMENT);
        
        // Display the current input as the top suggestion.
        // Partly so the user can return to it; partly for debugging value..
        result.append(selectedHint == 0 ? renderHighlighted(currentInput) : currentInput);
        result.append(HTML_NEWLINE);
        
        for (int i = 1; i <= suggestions.size(); i++) {            
            if (i == selectedHint) {
                result.append(renderHighlighted(renderSuggestion(suggestions.get(i - 1))));
            }
            else {
                result.append(renderSuggestion(suggestions.get(i - 1)));
            }
            
            result.append(HTML_NEWLINE);
        }
        
        result.append(HTML_END_DOCUMENT);
        return result.toString();
    }
    
    
    
    private static String renderHighlighted(String text) {
    	return HTML_START_BOLD + text + HTML_END_BOLD;
    }
    
    
    // Internal Functions
    private static String renderSuggestion(SuggestionHint suggestion) {
        StringBuilder result = new StringBuilder();
        
        String[] words = suggestion.getWords();
        boolean[][] masks = suggestion.getMatchingMask();
        SyntaxTerm[] terms = suggestion.getSyntaxTerms();
        
        result.append(renderWord(words[0], masks[0], terms[0]));
        
        for (int i = 1; i < words.length; i++) {
        	result.append(' ');
        	result.append(renderWord(words[i], masks[i], terms[i]));
        }
        
        return result.toString();
    }
    
    private static String renderWord(String word, boolean[] matchMask, SyntaxTerm type) {
    	if (type instanceof LiteralSyntaxTerm) {
    		String htmlColor = TEXT_COLOR;
    		String synTermValue = ((LiteralSyntaxTerm) type).getLiteralValue();
    		
    		if (synTermValue.equals("add")) {
    			htmlColor = BLUE_COLOR;
    		} else if (synTermValue.equals("edit")) {
    			htmlColor = PURPLE_COLOR;
    		} else if (synTermValue.equals("remove")) {
    			htmlColor = RED_COLOR;
    		} else if (synTermValue.equals("search")) {
    			htmlColor = GREEN_COLOR;
    		}
    		
    		return renderTextWithColor(word, matchMask, htmlColor);
    	} else if (word.isEmpty()) {
            return renderSyntaxClass(type);
        } else {
    		return renderTextWithColor(word, matchMask, TEXT_COLOR);
    	}
    }

    private static String renderSyntaxClass(SyntaxTerm type) {
        assert type instanceof SyntaxClassSyntaxTerm;

        String synClassName = ((SyntaxClassSyntaxTerm) type).getSyntaxClassName();
        StringBuilder result = new StringBuilder();
        String htmlColor = SYNTAX_CLASS_COLOR;


    	result.append("<font color='");
    	result.append(htmlColor);
    	result.append("'>");

        result.append("&lt;");
        result.append(synClassName);
        result.append("&gt;");
        
    	result.append("</font>");

        return result.toString();
    }
    
    private static String renderTextWithColor(String text, boolean[] matchMask, String htmlColor) {
    	StringBuilder result = new StringBuilder();
    	result.append("<font color='");
    	result.append(htmlColor);
    	result.append("'>");

    	for (int i = 0; i < text.length(); i++) {
    		if (matchMask[i]){
    			result.append("<u>");
    			result.append(text.charAt(i));
    			result.append("</u>");
    		} else {
    			result.append(text.charAt(i));
    		}
    	}

    	result.append("</font>");
    	return result.toString();
    }
    
    // Mutators
    public void setSelectedHint(int selection) {
        selectedHint = selection;
    }
}
