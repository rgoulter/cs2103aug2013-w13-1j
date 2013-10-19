package jim;

import static org.junit.Assert.*;
import jim.journal.JournalManager;
import jim.journal.JournalView;
import jim.suggestions.SuggestionView;

import org.junit.Test;


public class ViewTests extends JimMainPanel {

    @Test
    public void testFeedbackPanelLoading() {
        // Tests the following boundary cases:
        //   Size of contents of text field == 0 ==> Show JournalView
        //   Size of contents of text field >= 1 ==> Show SuggestionView
        
        JimMainPanel mainPanel = new JimMainPanel();
        mainPanel.runWindow();
        
        assert(viewPanel instanceof JournalView);
        mainPanel.inputTextField.setText("a");
        assert(viewPanel instanceof SuggestionView);
        mainPanel.inputTextField.setText("");
        assert(viewPanel instanceof JournalView);
    }


    
}
