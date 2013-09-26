package jim;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import java.awt.CardLayout;
import java.util.Collections;

import jim.suggestions.SuggestionManager;
import jim.suggestions.SuggestionView;
import jim.journal.JournalManager;
import jim.journal.JournalView;


public class JimMainPanel extends JPanel {
    private JTextField inputTextField;
    private JPanel viewPanel;
    
    private JournalView journalView;
    private SuggestionView suggestionView;
    private SuggestionManager suggestionManager;
    private JournalManager journalManager;

    // Arbitrary objects for ActionMap.
    private static final String ACTION_EXIT_WINDOW = "exit window";
    private static final String ACTION_EXECUTE_INPUT = "execute input";
    private static final String ACTION_SUGGESTIONS_FORWARD = "suggestions forward";
    private static final String ACTION_SUGGESTIONS_BACKWARD = "suggestions backward";

    private static final String CARDLAYOUT_JOURNAL_VIEW = "journal view";
    private static final String CARDLAYOUT_SUGGESTION_VIEW = "suggestion view";
    
    public static final int VIEW_AREA_WIDTH = 600;
    public static final int VIEW_AREA_HEIGHT = 400;
    
    
    public JimMainPanel() {
        initialiseUIComponents();
 
        // Initialise the logic
        suggestionManager = new SuggestionManager();
        journalManager = new JournalManager();
        
        
        // Setup the View parts for the Jim-specific stuff.
        suggestionView = new SuggestionView(); 
        suggestionView.setSuggestionManager(suggestionManager);
        
        journalView = new JournalView();
        journalView.setJournalManager(journalManager);

        viewPanel.add(suggestionView, CARDLAYOUT_SUGGESTION_VIEW);
        viewPanel.add(journalView, CARDLAYOUT_JOURNAL_VIEW);
    }
    
    
    
    private void initialiseUIComponents() {
        // Add UI components.
        setLayout(new BorderLayout(0, 0));
        
        inputTextField = new JTextField();
        
        // Disable default focus traversal keys so we can rebind TAB and SHIFT+TAB
        inputTextField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
        									 Collections.EMPTY_SET);
        inputTextField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
        									 Collections.EMPTY_SET);
        
        inputTextField.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            // Catches keystrokes as the user inputs them
            @Override
            public void keyReleased(KeyEvent arg0) {
            	suggestionManager.updateBuffer(inputTextField.getText());
                refreshUI();
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
            
        });
        
        inputTextField.addFocusListener(new FocusListener() {
        	public void focusLost(FocusEvent e) {
        		inputTextField.requestFocus();
        	}

			@Override
			public void focusGained(FocusEvent arg0) {
				// CC: This is here to fully implement FocusListener. We won't use this.
			}
        });
        
        // Bind ENTER to execute
        inputTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                                         ACTION_EXECUTE_INPUT);
        inputTextField.getActionMap().put(ACTION_EXECUTE_INPUT, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                executeInput();
                inputTextField.setText("");
            }
        });
        
        // Bind TAB to nextSuggestion
        inputTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
        								 ACTION_SUGGESTIONS_FORWARD);
        inputTextField.getActionMap().put(ACTION_SUGGESTIONS_FORWARD, new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	suggestionManager.nextSuggestion();
            	String selection = suggestionManager.getCurrentSuggestion();
            	displayAutoComplete(selection);
            	refreshUI();
            }
        });
        
        // Bind SHIFT+TAB to prevSuggestion
        inputTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
        								 java.awt.event.InputEvent.SHIFT_DOWN_MASK),
        								 ACTION_SUGGESTIONS_BACKWARD);
        inputTextField.getActionMap().put(ACTION_SUGGESTIONS_BACKWARD, new AbstractAction(){
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		suggestionManager.prevSuggestion();
        		String selection = suggestionManager.getCurrentSuggestion();
            	displayAutoComplete(selection);
        		refreshUI();
        	}
        });

        inputTextField.setColumns(10);
        add(inputTextField, BorderLayout.NORTH);
        
        
        // The viewPanel here is to contain the "Views" which JIM! may need to display,
        // i.e. show a JournalView, or a SuggestionView (or maybe half-and-half).
        viewPanel = new JPanel();
        viewPanel.setPreferredSize(new Dimension(VIEW_AREA_WIDTH, VIEW_AREA_HEIGHT));
        add(viewPanel, BorderLayout.CENTER);
        viewPanel.setLayout(new CardLayout(0, 0));
        
    }
    
    private void displayAutoComplete(String text) {
    	// TODO: Splice suggestion into user's input and reposition cursor in place
    }
    
    
    private void refreshUI() {
        journalView.updateViewWithContent();
        suggestionView.updateViewWithContent();
        
        // Ensure that the "View" is the right "View".
        // Skeleton rule: If empty, show calendar. Otherwise, show suggestion.
        // Some Magic here:
        // Depends on the way we add the things to viewPanel's CardLayout.
        CardLayout cardLayout = (CardLayout) viewPanel.getLayout();
        
        // TODO: 0 is a magic number here, oddly enough.
        if (inputTextField.getText().length() > 0) {
           // Show the Suggestion view
            cardLayout.show(viewPanel, CARDLAYOUT_SUGGESTION_VIEW); 
        } else {
            // Show the Journal view
            cardLayout.show(viewPanel, CARDLAYOUT_JOURNAL_VIEW);
            suggestionManager.setCurrentSuggestionIndex(-1);	// Reset suggestion to -1 (No selection)
        }
    }

    
    
    // This method is called when we want to execute input
    private void executeInput() {
        // Discussion: One thing which may have been neglected is 'feedback' to user.
        //  This can be managed/displayed from here, however it's implemented.
        //  (Possibly just using 'Alert' message-boxes for early stages?)
        
        String input = inputTextField.getText();
        String inputTokens[] = input.split(" ");
        
        jim.journal.Command command = suggestionManager.parseCommand(inputTokens);
        
        if (command != null) {
           command.execute(journalManager); 
        }
    }

    
    
    // To be lazy, this method is called to run the JIM! GUI
    public static void runWindow() {
        final JFrame applicationWindow = new JFrame("JIM!");
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // FYI: "Undecorated" means no borders, etc.
        applicationWindow.setUndecorated(true);

        JimMainPanel jimPanel = new JimMainPanel();
        applicationWindow.getContentPane().add(jimPanel);
        applicationWindow.pack();

        // We bind the key "Escape" from the InputField so that 
        //  when pressed, our window closes.
        jimPanel.inputTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                                  ACTION_EXIT_WINDOW);
        
        jimPanel.inputTextField.getActionMap().put(ACTION_EXIT_WINDOW,
                                                   new AbstractAction() {
                                                       @Override
                                                       public void actionPerformed(ActionEvent e) {
                                                           applicationWindow.dispose();
                                                       }
                                                   });
        
        // Centering Window
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int locX = ((int)screen.getWidth() / 2) - (VIEW_AREA_WIDTH/2);
		int locY = ((int)screen.getHeight() / 2) - (VIEW_AREA_HEIGHT/2);

		applicationWindow.setLocation(locX, locY); 
        applicationWindow.setVisible(true);
        
        jimPanel.refreshUI();	// Load current journal on startup
    }
    
    
    
    public static void main(String args[]) {
        JimMainPanel.runWindow();
    }

}
