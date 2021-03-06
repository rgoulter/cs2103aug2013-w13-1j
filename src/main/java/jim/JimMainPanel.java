//@author A0096790N
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import jim.suggestions.SuggestionManager;
import jim.suggestions.SuggestionView;
import jim.journal.Command;
import jim.journal.EditCommand;
import jim.journal.JournalManager;
import jim.journal.JournalView;
import jim.journal.Task;



@SuppressWarnings("serial")
public class JimMainPanel extends JPanel {
    public JTextField inputTextField;
    protected JFrame applicationWindow;
    public JPanel viewPanel;
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

    private static final int HISTORY_MAX_COMMANDS = 10;
    protected ArrayList<String> commandHistory;
    protected int historyIndex;
    protected int historyBrowsingIndex;

    private static Configuration configManager = Configuration.getConfiguration();
    private static final String DATE_SEPARATOR = configManager.getDateSeparator();
    private static final String TIME_SEPARATOR = configManager.getTimeSeparator();
    private static final String DATE_TEMPLATE_STRING = " %02d" + DATE_SEPARATOR + "%02d" + DATE_SEPARATOR + "%02d";
    private static final String TIME_TEMPLATE_STRING = "%02d" + TIME_SEPARATOR + "%02d" + TIME_SEPARATOR + "%02d     ";

    // Objects defining style and formatting
    private static final int LARGE_FONT_SIZE = 20;
    private static final Color COLOR_DARK_BLUE = new Color(100, 100, 188);
    private static final Color COLOR_BLUE = new Color(225, 225, 255);
    private static final Color COLOR_BLACK = new Color(0, 0, 0);
    private static final Color COLOR_TITLE_BLUE = new Color(205, 205, 235);
    private static final Font FONT_MAIN = new Font("Arial Black", Font.PLAIN, LARGE_FONT_SIZE);
    private static final Font FONT_TITLE = new Font("Impact", Font.ITALIC, LARGE_FONT_SIZE);

    // Arbitrary objects for ActionMap.
    private static final String ACTION_EXIT_WINDOW = "exit window";
    private static final String ACTION_HISTORY_PREVIOUS = "history previous";
    private static final String ACTION_HISTORY_NEXT = "history next";
    private static final String ACTION_EXECUTE_INPUT = "execute input";
    private static final String ACTION_SUGGESTIONS_FORWARD = "suggestions forward";
    private static final String ACTION_SUGGESTIONS_BACKWARD = "suggestions backward";
    private static final String ACTION_JOURNAL_UNDO = "undo";
    private static final String ACTION_JOURNAL_REDO = "redo";
    private static final String ACTION_JOURNAL_SCROLLUP = "scroll up";
    private static final String ACTION_JOURNAL_SCROLLDOWN = "scroll down";

    private static final String CARDLAYOUT_JOURNAL_VIEW = "journal view";
    private static final String CARDLAYOUT_SUGGESTION_VIEW = "suggestion view";

    // Some possible input values
    private static final String INPUT_REDO = "redo";
    private static final String INPUT_BLANK = "";
    private static final String INPUT_UNDO = "undo";

    // Command States
    private static final String COMMAND_STATE_FAILURE = "Failure";
    private static final String COMMAND_STATE_SUCCESS = "Success";
    private static final String COMMAND_STATE_NEEDNEWTASK = "NeedNewTask";
    private static final String COMMAND_STATE_PENDING = "Pending";
    private static final String COMMAND_STATE_READY = "Ready";

    // Misc Strings
    private static final String FEEDBACK_INPUT_NOT_RECOGNIZED = "Your input was not recognized.";
    private static final String HELPER_LABEL_TEMPLATE = "  %s :  ";
    private static final String JIM_CURRENT_VERSION = "JIM! v0.5    ";
    private static final String TOP_BAR_PLACEHOLDER_TEXT = "xxxxxxxxxx";

    // Actually meaningful attributes that can tweak JIM!'s behavior
    public static final int VIEW_AREA_WIDTH = 600;
    public static final int VIEW_AREA_HEIGHT = 400;
    public static final int SUGGESTIONS_DISPLAY_THRESHOLD = 0;

    // -----------------------------------------------------------
    // Initialization of GUI and important variables
    // -----------------------------------------------------------
    
    public JimMainPanel() {
        initializeUIComponents();
        initializeCommandHistoryLogic();
        initializeProgramLogic();

        initializeMainJFrame();
        refreshUI();
        runClock();
    }


    private void initializeMainJFrame() {
        applicationWindow = new JFrame(JIM_CURRENT_VERSION);
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationWindow.setUndecorated(true);

        Border border = BorderFactory.createLineBorder(COLOR_DARK_BLUE, 3, true);
        this.setBorder(border);
        this.setBackground(COLOR_BLUE);

        applicationWindow.getContentPane().add(this);
        applicationWindow.pack();

        // Centering Window
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int locX = ((int) screen.getWidth() / 2) - (VIEW_AREA_WIDTH / 2);
        int locY = ((int) screen.getHeight() / 2) - (VIEW_AREA_HEIGHT / 2);

        applicationWindow.setLocation(locX, locY);
        applicationWindow.setVisible(true);
    }

    private void runClock() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRunning) {
                    displayDateAndTime();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            private void displayDateAndTime() {
                GregorianCalendar calendar = new GregorianCalendar();
                String dateString = String.format(DATE_TEMPLATE_STRING,
                                                  calendar.get(Calendar.DATE),
                                                  calendar.get(Calendar.MONTH)+1,
                                                  calendar.get(Calendar.YEAR));
                String timeString = String.format(TIME_TEMPLATE_STRING,
                                                  calendar.get(Calendar.HOUR_OF_DAY),
                                                  calendar.get(Calendar.MINUTE),
                                                  calendar.get(Calendar.SECOND));

                dateLabel.setText(dateString);
                clockLabel.setText(timeString);
            }

        }).run();
    }

    public static void main(String args[]) {
        new JimMainPanel();
    }
    
    private void initializeCommandHistoryLogic() {
        lastCommandState = COMMAND_STATE_READY;
        isRunning = true;
        commandHistory = new ArrayList<String>(HISTORY_MAX_COMMANDS);
        for (int i=0; i<HISTORY_MAX_COMMANDS; i++) {
            commandHistory.add(INPUT_BLANK);
        }
        historyIndex = 0;
        historyBrowsingIndex = 0;
    }

    private void initializeProgramLogic() {
        suggestionManager = new SuggestionManager();
        journalManager = new JournalManager();

        // Give JournalManager to SuggestionManager.
        // (Dependency for generating Suggestions).
        suggestionManager.setJournalManager(journalManager);

        // Setup the View parts for the Jim-specific stuff.
        suggestionView = new SuggestionView();
        suggestionView.setSuggestionManager(suggestionManager);

        journalView = new JournalView();
        journalView.setJournalManager(journalManager);

        viewPanel.add(suggestionView, CARDLAYOUT_SUGGESTION_VIEW);
        viewPanel.add(journalView, CARDLAYOUT_JOURNAL_VIEW);
    }

    private void initializeUIComponents() {
        setUpTopBar();
        setUpInputArea();
        bindKeystrokes();
        setUpFeedbackArea();
    }

    private void setUpFeedbackArea() {
        viewPanel = new JPanel();
        Border outerBorder = BorderFactory.createLineBorder(COLOR_BLUE, 4);
        Border innerBorder = BorderFactory.createLoweredSoftBevelBorder();
        Border outputFieldBorder = new CompoundBorder(outerBorder, innerBorder);
        viewPanel.setBorder(outputFieldBorder);

        viewPanel.setPreferredSize(new Dimension(VIEW_AREA_WIDTH, VIEW_AREA_HEIGHT));
        add(viewPanel, BorderLayout.SOUTH);
        viewPanel.setLayout(new CardLayout(0, 0));
    }

    private void setUpInputArea() {
        inputTextField = new JTextField();
        Border outerBorder = BorderFactory.createLineBorder(COLOR_BLUE, 5);
        Border innerBorder = BorderFactory.createLineBorder(COLOR_BLACK, 1);
        Border inputFieldBorder = new CompoundBorder(outerBorder, innerBorder);
        inputTextField.setBorder(inputFieldBorder);

        // Set up input box
        JPanel inputPanel = new JPanel(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        helperTextLabel = new JLabel(INPUT_BLANK);
        inputPanel.add(inputTextField, BorderLayout.CENTER);
        inputPanel.add(helperTextLabel, BorderLayout.WEST);
    }

    private void setUpTopBar() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_TITLE_BLUE);

        DraggingListener listener = new DraggingListener();
        topPanel.addMouseMotionListener(listener);
        topPanel.addMouseListener(listener);
        
        dateLabel = new JLabel(TOP_BAR_PLACEHOLDER_TEXT);
        clockLabel = new JLabel(TOP_BAR_PLACEHOLDER_TEXT, JLabel.CENTER);
        progNameLabel = new JLabel(JIM_CURRENT_VERSION);

        dateLabel.setFont(FONT_MAIN);
        clockLabel.setFont(FONT_MAIN);
        progNameLabel.setFont(FONT_TITLE);

        topPanel.add(dateLabel, BorderLayout.WEST);
        topPanel.add(clockLabel, BorderLayout.CENTER);
        topPanel.add(progNameLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }

    private void bindKeystrokes() {
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

                switch (keyCode) {

                // No action for key modifiers; PageUp/PageDown
                case KeyEvent.VK_SHIFT:
                case KeyEvent.VK_CONTROL:
                case KeyEvent.VK_ALT:
                case KeyEvent.VK_META:
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_PAGE_DOWN:
                    break;

                    // Only refresh view for esc and tab
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_TAB:
                    refreshUI();
                    break;

                    // Send text to suggestionmanager
                default:
                    
                    try {
                        suggestionManager.updateBuffer(inputTextField.getText());
                    } catch (IndexOutOfBoundsException e) {
                        // No need to handle exception. Program continues to run with expected behavior
                    }
                    
                    
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
                commandHistory.set(historyIndex, inputTextField.getText());
                historyIndex++;
                historyBrowsingIndex = historyIndex;
                if (historyIndex > HISTORY_MAX_COMMANDS-1) { historyIndex = 0; }

                executeInput();
            }
        });

        // Bind UP to previous in command history
        inputTextField.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
             ACTION_HISTORY_PREVIOUS);
        inputTextField.getActionMap().put(ACTION_HISTORY_PREVIOUS,
                                          new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                historyBrowsingIndex--;
                if (historyBrowsingIndex < 0) { historyBrowsingIndex = HISTORY_MAX_COMMANDS-1; }

                inputTextField.setText(commandHistory.get(historyBrowsingIndex));
                suggestionManager.updateBuffer(inputTextField.getText());
                refreshUI();
            }
        });

        // Bind DOWN to next in command history
        inputTextField.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
             ACTION_HISTORY_NEXT);
        inputTextField.getActionMap().put(ACTION_HISTORY_NEXT,
                                          new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                historyBrowsingIndex++;
                if (historyBrowsingIndex > HISTORY_MAX_COMMANDS-1) { historyBrowsingIndex = 0; }

                inputTextField.setText(commandHistory.get(historyBrowsingIndex));
                suggestionManager.updateBuffer(inputTextField.getText());
                refreshUI();
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

        // Bind CTRL+Z to undo
        inputTextField.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                                    java.awt.event.InputEvent.CTRL_DOWN_MASK),
                                    ACTION_JOURNAL_UNDO);
        inputTextField.getActionMap().put(ACTION_JOURNAL_UNDO,
                                          new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                inputTextField.setText(INPUT_UNDO);
                executeInput();
                inputTextField.setText(INPUT_BLANK);
            }
        });

        // Bind CTRL+Y to undo
        inputTextField.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                                    java.awt.event.InputEvent.CTRL_DOWN_MASK),
                                    ACTION_JOURNAL_REDO);
        inputTextField.getActionMap().put(ACTION_JOURNAL_REDO,
                                          new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                inputTextField.setText(INPUT_REDO);
                executeInput();
                inputTextField.setText(INPUT_BLANK);
            }
        });
        // Bind PAGEUP to scroll up for JournalView
        inputTextField.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
             ACTION_JOURNAL_SCROLLUP);
                inputTextField.getActionMap().put(ACTION_JOURNAL_SCROLLUP,
                                                  new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        journalView.scrollPageUp();
                    }
                });

                // Bind PAGEDOWN to scroll down for JournalView
                inputTextField.getInputMap()
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
                     ACTION_JOURNAL_SCROLLDOWN);
                inputTextField.getActionMap().put(ACTION_JOURNAL_SCROLLDOWN,
                                                  new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        journalView.scrollPageDown();
                    }
                });

                // We bind the key "Escape" from the InputField so that
                // when pressed, our window closes.
                inputTextField.getInputMap()
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                                            0),
                                            ACTION_EXIT_WINDOW);

                inputTextField.getActionMap().put(ACTION_EXIT_WINDOW,
                                                  new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (lastCommandState.equals(COMMAND_STATE_PENDING) ||
                                lastCommandState.equals(COMMAND_STATE_NEEDNEWTASK)) {
                            clearLastCommand();
                            journalView.unholdFeedback();
                            refreshUI();
                            inputTextField.setText(INPUT_BLANK);
                        }
                        else if(!inputTextField.getText().isEmpty()) {
                            inputTextField.setText(INPUT_BLANK);
                            refreshUI();
                        }
                        else {
                            applicationWindow.dispose();
                            isRunning = false;
                        }
                    }
                });
    }

    // Nested class to facilitate window dragging
    class DraggingListener implements MouseListener, MouseMotionListener {

        private static final int STATE_NOT_TRACKING = 0;
        private static final int STATE_TRACKING = 1;

        private int state;
        private int lastX;
        private int lastY;

        @Override
        public void mousePressed(MouseEvent e) {
            lastX = e.getXOnScreen();
            lastY = e.getYOnScreen();
            state = STATE_TRACKING;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (state == STATE_TRACKING) {
                int newX = e.getXOnScreen();
                int newY = e.getYOnScreen();

                int changeX = newX - lastX;
                int changeY = newY - lastY;

                int frameCurrentX = applicationWindow.getLocation().x;
                int frameCurrentY = applicationWindow.getLocation().y;

                int updatedX = frameCurrentX + changeX;
                int updatedY = frameCurrentY + changeY;

                applicationWindow.setLocation(updatedX, updatedY);

                lastX = newX;
                lastY = newY;
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            state = STATE_NOT_TRACKING;
        }

        // Unused methods: Required to fully implement listeners, but are not used
        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}


    }

    // -----------------------------------------------------------
    
    
    // -----------------------------------------------------------
    // Methods used in Refreshing
    // -----------------------------------------------------------
    
    private void displayAutoComplete(String text) {
        inputTextField.setText(text);
    }

    private void refreshUI() {
        journalView.updateViewWithContent();
        suggestionView.updateViewWithContent();
        this.selectDisplayedCard();
    }

    private void selectDisplayedCard() {
        CardLayout cardLayout = (CardLayout) viewPanel.getLayout();

        if (inputTextField.getText().length() > SUGGESTIONS_DISPLAY_THRESHOLD &&
                (!lastCommandState.equals(COMMAND_STATE_PENDING) && !lastCommandState.equals(COMMAND_STATE_NEEDNEWTASK))) {
            cardLayout.show(viewPanel, CARDLAYOUT_SUGGESTION_VIEW);
        } else {
            cardLayout.show(viewPanel, CARDLAYOUT_JOURNAL_VIEW);
            suggestionManager.updateBuffer(null); // Reset suggestion
        }
    }

    // -----------------------------------------------------------

    
    // -----------------------------------------------------------
    // Drivers for parsing and executing input
    // -----------------------------------------------------------

    private void executeInput() {
        String input = inputTextField.getText();
        String inputTokens[] = input.split(" ");

        if (lastCommandState.equals(COMMAND_STATE_PENDING)) {
            executePendingCommand(input);
        }
        else if (lastCommandState.equals(COMMAND_STATE_NEEDNEWTASK)) {
            executeNeedNewTaskCommand(inputTokens);
        }
        else {
            executeNewCommand(inputTokens);
        }

        // Post Execution Checks
        if (lastCommandState.equals(COMMAND_STATE_SUCCESS) || lastCommandState.equals(COMMAND_STATE_FAILURE)) {
            clearCompletedCommand();
        }
        else if (lastCommandState.equals(COMMAND_STATE_PENDING)) {
            holdPendingCommand();
        }
        else if (lastCommandState.equals(COMMAND_STATE_NEEDNEWTASK)) {
            holdNeedNewTaskCommand();
        }

    }

    private void holdNeedNewTaskCommand() {
        helperTextLabel.setText(String.format(HELPER_LABEL_TEMPLATE, lastCommand.toString()));
        inputTextField.setText( ((EditCommand) lastCommand).getSelectedTaskDescription() );
        refreshUI();
    }

    private void holdPendingCommand() {
        helperTextLabel.setText(String.format(HELPER_LABEL_TEMPLATE, lastCommand.toString()));
        journalView.holdFeedback();
        inputTextField.setText(INPUT_BLANK);
    }

    private void clearCompletedCommand() {
        clearLastCommand();
        journalView.unholdFeedback();
        inputTextField.setText(INPUT_BLANK);

        lastCommandState = COMMAND_STATE_READY;
        journalView.setFeedbackSource(lastCommand.toString());
    }


    private void executeNewCommand(String[] inputTokens) {
        jim.journal.Command command;
        try {
            command = suggestionManager.parseCommand(inputTokens);
        } catch (IllegalArgumentException e) {
            command = null;
        }

        if (command == null) {
            reactToUnparsableCommand();
        }

        else if (command != null) {
            reactToParsedCommand(command);
        }

    }

    private void reactToParsedCommand(jim.journal.Command command) {
        lastCommand = command;

        lastCommandState = command.execute(journalManager);
        journalView.setFeedbackMessage( command.getOutput() );
    }

    private void reactToUnparsableCommand() {
        journalView.setFeedbackMessage(FEEDBACK_INPUT_NOT_RECOGNIZED);
        inputTextField.setText(INPUT_BLANK);
    }

    private void executeNeedNewTaskCommand(String[] inputTokens) {
        Task newTask = suggestionManager.parseTask(inputTokens);
        lastCommandState = lastCommand.thirdExecute(newTask);
        journalView.setFeedbackMessage( lastCommand.getOutput() );
    }

    private void executePendingCommand(String input) {
        lastCommandState = lastCommand.secondExecute(input);
        journalView.setFeedbackMessage( lastCommand.getOutput() );
    }

    private void clearLastCommand() {
        lastCommandState = INPUT_BLANK;
        helperTextLabel.setText(INPUT_BLANK);
    }

    // -----------------------------------------------------------

}
