package jim.journal;

import jim.JimView;
import java.awt.BorderLayout;
import javax.swing.JTextArea;

public class JournalView extends JimView {
    private JTextArea outputTextArea;
    private JournalManager journalManager;
    
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
    
    public void updateViewWithContent() {
        // Build text from current journal content.
        String journalText = journalManager.getDisplayString();
        
        // Output to the Text Area (or whatever output component).
        outputTextArea.setText(journalText);
    }

}
