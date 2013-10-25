
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
        String journalText = "";
        if (!lastFeedback.equals("")) {
            journalText = lastFeedback + "<p />";
        }
        if (!holdingFeedback) {
            lastFeedback = "";
        }
        

        // Build text from current journal content.
        String outputText = journalManager.getDisplayString();
        journalText = journalText + outputText;
        
        journalText = journalText.replace("\n", "<br>");
        journalText = journalText.replace("Upcoming Events:", "<b><u>Upcoming Events:</u></b>");
        journalText = journalText.replace("Todo:", "<b><u>Todo:</u></b>");

        // Output to the Text Area
        outputTextArea.setText(journalText);
        outputTextArea.select(0,0);
    }
    
    public void holdFeedback()   { holdingFeedback = true; }
    public void unholdFeedback() { holdingFeedback = false; }

}
