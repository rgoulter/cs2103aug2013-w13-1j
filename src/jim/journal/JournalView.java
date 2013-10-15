
package jim.journal;

import jim.JimView;

import java.awt.BorderLayout;

import javax.swing.JTextArea;
import javax.swing.JTextPane;



public class JournalView extends JimView {

    private JTextPane outputTextArea;
    private JournalManager journalManager;
    private String lastFeedback = "";



    public JournalView() {
        setLayout(new BorderLayout(0, 0));

        outputTextArea = new JTextPane();
        outputTextArea.setContentType("text/html");
        outputTextArea.setText("Current:");
        outputTextArea.setEditable(false);
        add(outputTextArea, BorderLayout.CENTER);

        journalManager = new JournalManager();
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
        // We also clear the feedback to prevent double-display of feedback
        String journalText = "";
        if (!lastFeedback.equals("")) {
            journalText = lastFeedback + "<p />";
            lastFeedback = "";
        }

        // Build text from current journal content.
        String outputText = journalManager.getDisplayString();
        outputText = outputText.replace("\n", "<br>");
        outputText = outputText.replace("Upcoming Events:", "<b><u>Upcoming Events:</u></b>");
        outputText = outputText.replace("Todo:", "<b><u>Todo:</u></b>");
        journalText = journalText + outputText;

        // Output to the Text Area
        outputTextArea.setText(journalText);
    }

}
