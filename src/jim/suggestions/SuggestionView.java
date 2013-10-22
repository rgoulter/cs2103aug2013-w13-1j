
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

    private JTextPane outputPane;
    private SuggestionManager suggestionManager;



    public SuggestionView() {
        setLayout(new BorderLayout(0, 0));

        outputPane = new JTextPane();
        outputPane.setContentType("text/html");
        add(outputPane, BorderLayout.CENTER);
    }



    public void setSuggestionManager(SuggestionManager suggestionManager) {
        this.suggestionManager = suggestionManager;
        assert(suggestionManager != null);
        assert(suggestionManager instanceof SuggestionManager);
    }



    public void updateViewWithContent() {
        assert(suggestionManager != null);
        
        SuggestionHints hintSet = suggestionManager.getSuggestionHints();
        outputPane.invalidate();
        outputPane.setText(hintSet.toString());
        outputPane.repaint();
    }

}
