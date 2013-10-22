package jim.suggestions;

import java.util.ArrayList;
import java.util.List;


public class SuggestionHints {
    // HTML Coloration Constants
    private static final String BLACK_COLOR = "black";
    private static final String RED_COLOR = "red";
    private static final String BLUE_COLOR = "blue'>";
    private static final String PURPLE_COLOR = "purple";
    private static final String GREEN_COLOR = "green";
    
    // HTML Constants (Other Commands)
    private static final String HTML_NEWLINE = "<br>";
    private static final String HTML_START_BOLD = "<b>";
    private static final String HTML_END_BOLD = "</b>";
    private static final String HTML_START_DOCUMENT = "<html><body>";
    private static final String HTML_END_DOCUMENT = "</body></html>";
    
    // Working Variables
    private List<SuggestionHint> suggestions;
    private static int selectedHint = -1;
    
    // Constructors
    public SuggestionHints(List<SuggestionHint> suggestionHints) {
    	suggestions = suggestionHints;
    }
    
    
    // Accessors
    @Override
    public String toString() {
    	// Assumes whoever does ".toString()" on SuggestionHints wants
    	// an HTML rendering..
        StringBuilder result = new StringBuilder(HTML_START_DOCUMENT);
        
        for (int i = 0; i < suggestions.size(); i++) {
            if (i == selectedHint) {
                result.append(renderHighlighted(renderSuggestion(suggestions.get(i))));
            }
            else {
                result.append(renderSuggestion(suggestions.get(i)));
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
        SyntaxTerm[] terms = suggestion.getSyntaxTerms();
        
        result.append(renderWord(words[0], terms[0]));
        
        for (int i = 1; i < words.length; i++) {
        	result.append(' ');
        	result.append(renderWord(words[i], terms[i]));
        }
        
        return result.toString();
    }
    
    private static String renderWord(String word, SyntaxTerm type) {
    	if (type instanceof LiteralSyntaxTerm) {
    		String htmlColor = BLACK_COLOR;
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
    		
    		return renderTextWithColor(word, htmlColor);
    	} else {
    		return word;
    	}
    }
    
    private static String renderTextWithColor(String text, String htmlColor) {
    	return "<font color='" + htmlColor + "'>" + text + "</font>";
    }
}
