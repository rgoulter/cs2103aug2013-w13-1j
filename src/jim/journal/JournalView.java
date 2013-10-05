
package jim.journal;

import jim.JimView;
import java.awt.BorderLayout;
import javax.swing.JTextArea;



public class JournalView extends JimView {

    private JTextArea outputTextArea;
    private JournalManager journalManager;
    private String lastFeedback = "";



    public JournalView() {
        setLayout(new BorderLayout(0, 0));

        outputTextArea = new JTextArea();
        outputTextArea.setText("Current:");
        outputTextArea.setEditable(false);
        add(outputTextArea, BorderLayout.CENTER);

        journalManager = new JournalManager();
    }



    public void setJournalManager(JournalManager journal) {
        this.journalManager = journal;
    }



    public void setFeedbackMessage(String feedback) {
        lastFeedback = feedback;
    }



    public void updateViewWithContent() {
        // Load up and display feedback, if any, from previous command
        // We also clear the feedback to prevent double-display of feedback
        String journalText = "";
        if (!lastFeedback.equals("")) {
            journalText = lastFeedback + "\n\n";
            lastFeedback = "";
        }

        // Build text from current journal content.
        journalText = journalText + journalManager.getDisplayString();

        // Output to the Text Area (or whatever output component).
        outputTextArea.setText(journalText);
    }

}
