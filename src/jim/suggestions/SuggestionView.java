package jim.suggestions;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import jim.JimView;

import javax.swing.JList;

public class SuggestionView extends JimView {
    
    // Here we want SuggestionView to display a bunch of current suggestions,
    //  and give some indication as to which is the user's "current" selection..
    
    private List<String> displayedSuggestions;
    private JList<String> outputList;
    private SuggestionManager suggestionManager;
    
    public SuggestionView() {
        setLayout(new BorderLayout(0, 0));
        
        outputList = new JList<String>();
        add(outputList, BorderLayout.CENTER);
        
        displayedSuggestions = new ArrayList<String>();
    }
    
    public void setSuggestionManager(SuggestionManager suggestionManager) {
        this.suggestionManager = suggestionManager;
    }
    
    public void updateViewWithContent() {
        displayedSuggestions = suggestionManager.getSuggestionsToDisplay();
        
        // Build text from current suggestion content.
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        
        for(String suggestion : displayedSuggestions){
            listModel.addElement(suggestion);
        }
        
        outputList.setModel(listModel);
    }
    
    /**
     * 
     * @param lineNum from 0 ... n-1 for n lines.
     */
    public void highlightLine(int lineNum){
        outputList.setSelectedIndex(lineNum);
    }
}
