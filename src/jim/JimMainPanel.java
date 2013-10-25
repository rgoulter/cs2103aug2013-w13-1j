
package jim;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import java.awt.CardLayout;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import jim.suggestions.SuggestionManager;
import jim.suggestions.SuggestionView;
import jim.journal.Command;
import jim.journal.JournalManager;
import jim.journal.JournalView;
import jim.journal.Task;



@SuppressWarnings("serial")
public class JimMainPanel extends JPanel {

    protected JEditorPane inputTextField;
    protected JFrame applicationWindow;
    protected JPanel viewPanel;
    protected JLabel helperTextLabel;
    protected JLabel clockLabel;
    protected JLabel dateLabel;
    protected JLabel progNameLabel;

    protected static String lastCommandState;
    protected Command lastCommand;
    
    protected JournalView journalView;
    protected SuggestionView suggestionView;
    protected SuggestionManager suggestionManager;
    protected JournalManager journalManager;
    protected boolean isRunning;
    
    // Objects defining style / color / other aesthetics
    private static final Color COLOR_DARK_BLUE = new Color(100, 100, 188);
    private static final Color COLOR_BLUE = new Color(225, 225, 255);
    private static final Color COLOR_BLACK = new Color(0, 0, 0);
    private static final Color COLOR_TITLE_BLUE = new Color(205, 205, 235);
    private static final int LARGE_FONT_SIZE = 20;
    private static final Font FONT_IMPACT = new Font("Impact", Font.PLAIN, LARGE_FONT_SIZE);
    private static final Font FONT_IMPACT_ITALICS = new Font("Impact", Font.ITALIC, LARGE_FONT_SIZE);

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

    public JimMainPanel() {
        initialiseUIComponents();
        lastCommandState = "Ready";
        isRunning = true;

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
        
        inputTextField = new JEditorPane();
        Border outerBorder = BorderFactory.createLineBorder(COLOR_BLUE, 4);
        Border innerBorder = BorderFactory.createLineBorder(COLOR_BLACK, 1);
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
                                                  executeInput();
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

        // Set up top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_TITLE_BLUE);
        
        dateLabel = new JLabel("  xx-xx-xx");
        clockLabel = new JLabel("xx:xx:xx", JLabel.CENTER);
        progNameLabel = new JLabel("JIM! v0.3 Internal    ");
        
        dateLabel.setFont(FONT_IMPACT);
        clockLabel.setFont(FONT_IMPACT);
        progNameLabel.setFont(FONT_IMPACT_ITALICS);
        
        topPanel.add(dateLabel, BorderLayout.WEST);
        topPanel.add(clockLabel, BorderLayout.CENTER);
        topPanel.add(progNameLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Set up input box
        JPanel inputPanel = new JPanel(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        helperTextLabel = new JLabel("");
        inputPanel.add(inputTextField, BorderLayout.CENTER);
        inputPanel.add(helperTextLabel, BorderLayout.WEST);
        
        // The viewPanel here is to contain the "Views" which JIM! may need to
        // display,
        // i.e. show a JournalView, or a SuggestionView (or maybe
        // half-and-half).
        viewPanel = new JPanel();
        outerBorder = BorderFactory.createLineBorder(COLOR_BLUE, 4);
        innerBorder = BorderFactory.createLoweredSoftBevelBorder();
        Border outputFieldBorder = new CompoundBorder(outerBorder, innerBorder);
        viewPanel.setBorder(outputFieldBorder);

        viewPanel.setPreferredSize(new Dimension(VIEW_AREA_WIDTH,
                                                 VIEW_AREA_HEIGHT));
        add(viewPanel, BorderLayout.SOUTH);
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
        if (inputTextField.getText().length() > 0 && !lastCommandState.equals("Pending")) {
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
        String feedback = "";
        
        // Check before execution
        if (lastCommandState.equals("Pending")) {
            lastCommandState = lastCommand.secondExecute(input);
            feedback = lastCommand.getOutput();
        }
        
        else if (lastCommandState.equals("NeedNewTask")) {
            Task newTask = suggestionManager.parseTask(inputTokens);
            lastCommandState = lastCommand.thirdExecute(newTask);
            feedback = lastCommand.getOutput();
        }
        
        else {
            jim.journal.Command command = suggestionManager.parseCommand(inputTokens);

            if (command != null) {
                lastCommand = command;

                lastCommandState = command.execute(journalManager);
                feedback = command.getOutput();
            }
            
            if (lastCommandState.equals("Pending") || lastCommandState.equals("NeedNewTask")) {
                helperTextLabel.setText("  " + lastCommand.toString() + " :  ");
            }
        }

        // Check after execution
        if (lastCommandState.equals("Success") || lastCommandState.equals("Failure")) {
            clearLastCommand();
            journalView.unholdFeedback();
        }
        else if (lastCommandState.equals("Pending")) {
            journalView.holdFeedback();
        }
        
        
        journalView.setFeedbackMessage(feedback);
    }

    private void clearLastCommand() {
        lastCommandState = "";
        helperTextLabel.setText("");
    }


    // To be lazy, this method is called to run the JIM! GUI
    public void runWindow() {
        applicationWindow = new JFrame("JIM!");
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Beautify Program Window
        applicationWindow.setUndecorated(true);
        
        Border border = BorderFactory.createLineBorder(COLOR_DARK_BLUE, 3, true);
        this.setBorder(border);
        this.setBackground(COLOR_BLUE);

        applicationWindow.getContentPane().add(this);
        applicationWindow.pack();

        // We bind the key "Escape" from the InputField so that
        // when pressed, our window closes.
        this.inputTextField.getInputMap()
                               .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                                                           0),
                                    ACTION_EXIT_WINDOW);

        this.inputTextField.getActionMap().put(ACTION_EXIT_WINDOW,
                                                   new AbstractAction() {

                                                       @Override
                                                       public void actionPerformed(ActionEvent e) {
                                                           
                                                           if (lastCommandState.equals("Pending") ||
                                                               lastCommandState.equals("NeedNewTask")) {
                                                               clearLastCommand();
                                                           }
                                                           else {
                                                               applicationWindow.dispose();
                                                               isRunning = false;
                                                           }
                                                       }
                                                   });

        // Centering Window
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int locX = ((int) screen.getWidth() / 2) - (VIEW_AREA_WIDTH / 2);
        int locY = ((int) screen.getHeight() / 2) - (VIEW_AREA_HEIGHT / 2);

        applicationWindow.setLocation(locX, locY);
        applicationWindow.setVisible(true);

        this.refreshUI(); // Load current journal on startup
        runClock();
    }

    private void runClock() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                while (isRunning) {
                    
                    GregorianCalendar calendar = new GregorianCalendar();
                    String dateString = String.format("  %02d-%02d-%02d",
                                                      calendar.get(Calendar.DATE),
                                                      calendar.get(Calendar.MONTH)+1,
                                                      calendar.get(Calendar.YEAR));
                    String timeString = String.format("%02d:%02d:%02d",
                                                      calendar.get(Calendar.HOUR),
                                                      calendar.get(Calendar.MINUTE),
                                                      calendar.get(Calendar.SECOND));
                    
                    dateLabel.setText(dateString);
                    clockLabel.setText(timeString);
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            
        }).run();
    }

    public static void main(String args[]) {
        JimMainPanel jimPanel = new JimMainPanel();
        jimPanel.runWindow();
    }

}
