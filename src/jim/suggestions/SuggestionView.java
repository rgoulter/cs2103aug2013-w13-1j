
package jim.suggestions;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import jim.JimView;

import javax.swing.JList;



public class SuggestionView extends JimView {

    // Here we want SuggestionView to display a bunch of current suggestions,
    // and give some indication as to which is the user's "current" selection..

    private List<String> displayedSuggestions;
    private JTextPane outputPane;
    private SuggestionManager suggestionManager;



    public SuggestionView() {
        setLayout(new BorderLayout(0, 0));

        outputPane = new JTextPane();
        outputPane.setContentType("text/html");
        add(outputPane, BorderLayout.CENTER);

        displayedSuggestions = new ArrayList<String>();
    }



    public void setSuggestionManager(SuggestionManager suggestionManager) {
        this.suggestionManager = suggestionManager;
    }



    public void updateViewWithContent() {
        displayedSuggestions = suggestionManager.getSuggestionsToDisplay();

        // Build text from current suggestion content.
        String output = "<html><font family='verdana'>";

        for (int i = 0; i < displayedSuggestions.size(); i++) {
            String suggestion = displayedSuggestions.get(i);
            if (i == suggestionManager.getCurrentSuggestionIndex()) {
                output = output + "<b>" + suggestion + "</b><br>";
            } else {
                output = output + suggestion + "<br>";
            }
        }

        output = output + "</font></html>";
        outputPane.setText(output);
    }

    /**
     * 
     * @param lineNum
     *            from 0 ... n-1 for n lines.
     */
    /*
     * public void highlightLine(int lineNum){ highlightedLine = lineNum; }
     */
}
