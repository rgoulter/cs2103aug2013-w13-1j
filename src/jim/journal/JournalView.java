//@author A0096790N
package jim.journal;

import jim.JimView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;



public class JournalView extends JimView {

    private JTextPane outputTextArea;
    private JScrollPane scrollingPane;
    private JournalManager journalManager;
    private String lastFeedback = "";
    private boolean holdingFeedback = false;

    private int selectionPosition = 0;
    private String lastFeedbackSource = "";


    public JournalView() {
        journalManager = null;
        setLayout(new BorderLayout(0, 0));

        outputTextArea = new JTextPane();
        outputTextArea.setContentType("text/html");
        outputTextArea.setText("Current:");
        outputTextArea.setEditable(false);
        
        scrollingPane = new JScrollPane(outputTextArea);
        add(scrollingPane, BorderLayout.CENTER);
    }



    public void setJournalManager(JournalManager journal) {
        this.journalManager = journal;
        assert(journal != null);
        assert(journal instanceof JournalManager);
    }
    
    public void setFeedbackMessage(String feedback) {
        lastFeedback = feedback;
    }

    public void updateViewWithContent() {
        assert(journalManager != null);
        
        // Load up and display feedback, if any, from previous command
        // We also clear the feedback (unless explicitly told not to) to prevent double-display of feedback
        String journalText = "<font face=Tahoma>";
        if (!lastFeedback.equals("")) {
            journalText = journalText + lastFeedback + "<br><br>";
        }
        if (!holdingFeedback) {
            lastFeedback = "";
        }
        

        // Build text from current journal content.
        if (!lastFeedbackSource.equals("Display") && !lastFeedbackSource.equals("Search") &&
            !lastFeedbackSource.equals("Help")) {
            String outputText = journalManager.getDisplayString();
            journalText = journalText + outputText;
        } else {
            lastFeedbackSource = "";
        }
        
        journalText = journalText + "</font>";
        
            
        journalText = journalText.replace("\n", "<br>");
        journalText = journalText.replace("Upcoming Events:", "<b><u>Upcoming Events:</u></b>");
        journalText = journalText.replace("Todo:", "<b><u>Todo:</u></b>");

        // Output to the Text Area
        outputTextArea.setText(journalText);
        outputTextArea.select(0,0);
    }
    
    public void scrollPageDown() {
        selectionPosition += 300;
        int maxPosition = scrollingPane.getVerticalScrollBar().getMaximum() - 383;
        
        if (selectionPosition > maxPosition) {
            selectionPosition = maxPosition;
        }

        scrollingPane.getVerticalScrollBar().setValue(selectionPosition);
    }
    
    public void scrollPageUp() {
        selectionPosition -= 300;
        
        if (selectionPosition < 0) {
            selectionPosition = 0;
        }

        scrollingPane.getVerticalScrollBar().setValue(selectionPosition);
    }
    
    public void holdFeedback()   { holdingFeedback = true; }
    public void unholdFeedback() { holdingFeedback = false; }

    public void setFeedbackSource(String source) {
        lastFeedbackSource = source;
    }
}
