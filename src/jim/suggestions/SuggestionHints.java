package jim.suggestions;

import java.util.ArrayList;
import java.util.List;


public class SuggestionHints {
    // Command Constants
    private static final String ADD_COMMAND = "add";
    private static final String EDIT_COMMAND = "edit";
    private static final String REMOVE_COMMAND = "remove";
    private static final String SEARCH_COMMAND = "search";
    
    // HTML Coloration Constants
    private static final String BLACK_COLOR = "<font color='black'>";
    private static final String RED_COLOR = "<font color='red'>";
    private static final String BLUE_COLOR = "<font color='blue'>";
    private static final String PURPLE_COLOR = "<font color='purple'>";
    private static final String GREEN_COLOR = "<font color='green'>";
    private static final String GREY_COLOR = "<font color='gray'>";
    private static final String END_COLOR = "</font>";
    
    // HTML Constants (Other Commands)
    private static final String HTML_NEWLINE = "<br>";
    private static final String HTML_START_BOLD = "<b>";
    private static final String HTML_END_BOLD = "</b>";
    private static final String HTML_START_DOCUMENT = "<html><body>";
    private static final String HTML_END_DOCUMENT = "</body></html>";
    
    // Working Variables
    private List<String> suggestions;
    private List<String> hints;
    private static int selectedHint = -1;
    
    // Constructors
    public SuggestionHints(List<String> allSuggestions, List<String> allHints) {
        suggestions = allSuggestions;
        hints = allHints;
    }
    
    public SuggestionHints(List<String> allSuggestions) {
        this(allSuggestions, new ArrayList<String>());
    }
    
    public SuggestionHints() {
        this(new ArrayList<String>(), new ArrayList<String>());
    }
    
    
    // Mutators
    public void add(String newHint) {
        hints.add(newHint);
    }
    
    public void setSelected(int i) {
        selectedHint = i;
    }
    
    
    // Accessors
    @Override
    public String toString() {
        String result = HTML_START_DOCUMENT;
        for (int i=0; i<suggestions.size(); i++) {
            if (i == selectedHint) {
                result = result + HTML_START_BOLD + renderString(i) + HTML_END_BOLD + HTML_NEWLINE;
            }
            else {
                result = result + renderString(i) + HTML_NEWLINE;
            }
        }
        
        result = result + HTML_END_DOCUMENT;
        return result;
    }
    
    
    // Internal Functions
    private String renderString(int index) {
        String suggestion = suggestions.get(index);
        String hint = hints.get(index).toLowerCase();
        String format = "";
        
        if (hint.equals(ADD_COMMAND)) {
            format = BLUE_COLOR;
        }
        else if (hint.equals(EDIT_COMMAND)) {
            format = PURPLE_COLOR;
        }
        else if (hint.equals(REMOVE_COMMAND)) {
            format = RED_COLOR;
        }
        else if (hint.equals(SEARCH_COMMAND)) {
            format = GREEN_COLOR;
        }
        
        return format + suggestion + END_COLOR;
    }
    
    
}
