
package jim;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import java.awt.CardLayout;
import java.util.Collections;

import jim.suggestions.SuggestionManager;
import jim.suggestions.SuggestionView;
import jim.journal.JournalManager;
import jim.journal.JournalView;



public class JimMainPanel extends JPanel {

    private JEditorPane inputTextField;
    private JPanel viewPanel;
    private boolean verbatimMode;
    private JimInputter inputSource;

    private JournalView journalView;
    private SuggestionView suggestionView;
    private SuggestionManager suggestionManager;
    private JournalManager journalManager;

    // Border Objects
    private static final Color BORDER_DARK_BLUE = new Color(100, 100, 188);
    private static final Color BORDER_BLUE = new Color(225, 225, 255);
    private static final Color BORDER_BLACK = new Color(0, 0, 0);

    // Arbitrary objects for ActionMap.
    private static final String ACTION_EXIT_WINDOW = "exit window";
    private static final String ACTION_EXECUTE_INPUT = "execute input";
    private static final String ACTION_SUGGESTIONS_FORWARD = "suggestions forward";
    private static final String ACTION_SUGGESTIONS_BACKWARD = "suggestions backward";
    private static final String ACTION_JOURNAL_UNDO = "undo";
    private static final String ACTION_JOURNAL_REDO = "redo";

    private static final String CARDLAYOUT_JOURNAL_VIEW = "journal view";
    private static final String CARDLAYOUT_SUGGESTION_VIEW = "suggestion view";

    public static final int VIEW_AREA_WIDTH = 600;
    public static final int VIEW_AREA_HEIGHT = 400;

    class GUIInputter extends JimInputter {
        public String getInput() {
            String result = (String)JOptionPane.showInputDialog("JIM!");
            if (result == null) { result = "(no input provided)"; }
            
            return result;
        }

    }
    


    public JimMainPanel() {
        initialiseUIComponents();
        verbatimMode = false;

        // Initialise the logic
        suggestionManager = new SuggestionManager();
        journalManager = new JournalManager();

        // Setup the View parts for the Jim-specific stuff.
        suggestionView = new SuggestionView();
        suggestionView.setSuggestionManager(suggestionManager);

        // Setup GUIInputter
        inputSource = new GUIInputter();
        
        journalView = new JournalView();
        journalView.setJournalManager(journalManager);

        viewPanel.add(suggestionView, CARDLAYOUT_SUGGESTION_VIEW);
        viewPanel.add(journalView, CARDLAYOUT_JOURNAL_VIEW);
    }



    private void initialiseUIComponents() {
        // Add UI components.
        setLayout(new BorderLayout(0, 0));

        inputTextField = new JEditorPane();
        Border outerBorder = BorderFactory.createLineBorder(BORDER_BLUE, 4);
        Border innerBorder = BorderFactory.createLineBorder(BORDER_BLACK, 1);
        Border inputFieldBorder = new CompoundBorder(outerBorder, innerBorder);
        inputTextField.setBorder(inputFieldBorder);

        // Disable default focus traversal keys so we can rebind TAB and
        // SHIFT+TAB
        inputTextField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                                             Collections.EMPTY_SET);
        inputTextField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                                             Collections.EMPTY_SET);

        inputTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent arg0) {
            }



            // Catches keystrokes as the user inputs them
            @Override
            public void keyReleased(KeyEvent arg0) {
                int keyCode = arg0.getKeyCode();
                int keyModifiers = arg0.getModifiers();
                
                switch (keyCode) {
                case KeyEvent.VK_SHIFT:
                case KeyEvent.VK_CONTROL:
                case KeyEvent.VK_ALT:
                case KeyEvent.VK_META:
                    break;
                default:
                    suggestionManager.updateBuffer(inputTextField.getText());
                    refreshUI();
                    break;
                }
            }



            @Override
            public void keyTyped(KeyEvent arg0) {
            }

        });

        inputTextField.addFocusListener(new FocusListener() {

            public void focusLost(FocusEvent e) {
                inputTextField.requestFocusInWindow();
            }



            @Override
            public void focusGained(FocusEvent arg0) {
                // CC: This is here to fully implement FocusListener. We won't
                // use this.
            }
        });

        // Bind ENTER to execute
        inputTextField.getInputMap()
                      .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                           ACTION_EXECUTE_INPUT);
        inputTextField.getActionMap().put(ACTION_EXECUTE_INPUT,
                                          new AbstractAction() {

                                              @Override
                                              public void actionPerformed(ActionEvent e) {
                                                  if (verbatimMode) {
                                                      verbatimMode = false;
                                                  }
                                                  else {
                                                      executeInput();
                                                  }
                                                  inputTextField.setText("");
                                              }
                                          });

        // Bind TAB to nextSuggestion
        inputTextField.getInputMap()
                      .put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                           ACTION_SUGGESTIONS_FORWARD);
        inputTextField.getActionMap().put(ACTION_SUGGESTIONS_FORWARD,
                                          new AbstractAction() {

                                              @Override
                                              public void actionPerformed(ActionEvent e) {
                                                  suggestionManager.nextSuggestion();
                                                  String selection = suggestionManager.getCurrentSuggestion();
                                                  displayAutoComplete(selection);
                                                  refreshUI();
                                              }
                                          });

        // Bind SHIFT+TAB to prevSuggestion
        inputTextField.getInputMap()
                      .put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                                                  java.awt.event.InputEvent.SHIFT_DOWN_MASK),
                           ACTION_SUGGESTIONS_BACKWARD);
        inputTextField.getActionMap().put(ACTION_SUGGESTIONS_BACKWARD,
                                          new AbstractAction() {

                                              @Override
                                              public void actionPerformed(ActionEvent e) {
                                                  suggestionManager.prevSuggestion();
                                                  String selection = suggestionManager.getCurrentSuggestion();
                                                  displayAutoComplete(selection);
                                                  refreshUI();
                                              }
                                          });
        
     // Bind CTRL+Z to undo (Work in progress; Waiting for Undo to be implemented)
        inputTextField.getInputMap()
                      .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                                                  java.awt.event.InputEvent.CTRL_DOWN_MASK),
                                                  ACTION_JOURNAL_UNDO);
        inputTextField.getActionMap().put(ACTION_JOURNAL_UNDO,
                                          new AbstractAction() {

                                              @Override
                                              public void actionPerformed(ActionEvent e) {
                                                  inputTextField.setText("undo");
                                                  executeInput();
                                                  inputTextField.setText("");
                                                  // refreshUI();
                                              }
                                          });
        
        // Bind CTRL+Y to undo (Work in progress; Waiting for Redo to be implemented)
        inputTextField.getInputMap()
                      .put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                                                  java.awt.event.InputEvent.CTRL_DOWN_MASK),
                                                  ACTION_JOURNAL_REDO);
        inputTextField.getActionMap().put(ACTION_JOURNAL_REDO,
                                          new AbstractAction() {

                                              @Override
                                              public void actionPerformed(ActionEvent e) {
                                                  JOptionPane.showMessageDialog(null, "Redo!", "Work In Progress", 1);
                                                  refreshUI();
                                              }
                                          });

        add(inputTextField, BorderLayout.NORTH);

        // The viewPanel here is to contain the "Views" which JIM! may need to
        // display,
        // i.e. show a JournalView, or a SuggestionView (or maybe
        // half-and-half).
        viewPanel = new JPanel();
        outerBorder = BorderFactory.createLineBorder(BORDER_BLUE, 4);
        innerBorder = BorderFactory.createLoweredSoftBevelBorder();
        Border outputFieldBorder = new CompoundBorder(outerBorder, innerBorder);
        viewPanel.setBorder(outputFieldBorder);

        viewPanel.setPreferredSize(new Dimension(VIEW_AREA_WIDTH,
                                                 VIEW_AREA_HEIGHT));
        add(viewPanel, BorderLayout.CENTER);
        viewPanel.setLayout(new CardLayout(0, 0));

    }



    private void displayAutoComplete(String text) {
        inputTextField.setText(text);
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
            suggestionManager.setCurrentSuggestionIndex(-1); // Reset suggestion
                                                             // to -1 (No
                                                             // selection)
        }
    }



    // This method is called when we want to execute input
    private void executeInput() {
        // Discussion: One thing which may have been neglected is 'feedback' to
        // user.
        // Feedback mechanism now implemented! Read commit log for details ~CC

        String input = inputTextField.getText();
        String inputTokens[] = input.split(" ");

        jim.journal.Command command = suggestionManager.parseCommand(inputTokens);

        if (command != null) {
        	command.setInputSource(inputSource);
            command.execute(journalManager);
            String feedback = command.getOutput();
            journalView.setFeedbackMessage(feedback);
        }
    }



    // To be lazy, this method is called to run the JIM! GUI
    public static void runWindow() {
        final JFrame applicationWindow = new JFrame("JIM!");
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Beautify Program Window
        applicationWindow.setUndecorated(true);

        JimMainPanel jimPanel = new JimMainPanel();
        jimPanel.setBorder(BorderFactory.createLineBorder(BORDER_DARK_BLUE,
                                                          3,
                                                          true));

        applicationWindow.getContentPane().add(jimPanel);
        applicationWindow.pack();

        // We bind the key "Escape" from the InputField so that
        // when pressed, our window closes.
        jimPanel.inputTextField.getInputMap()
                               .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                                                           0),
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
        int locX = ((int) screen.getWidth() / 2) - (VIEW_AREA_WIDTH / 2);
        int locY = ((int) screen.getHeight() / 2) - (VIEW_AREA_HEIGHT / 2);

        applicationWindow.setLocation(locX, locY);
        applicationWindow.setVisible(true);

        jimPanel.refreshUI(); // Load current journal on startup
    }



    public static void main(String args[]) {
        JimMainPanel.runWindow();
    }

}
