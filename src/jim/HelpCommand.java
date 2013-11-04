package jim;

import jim.journal.Command;
import jim.journal.JournalManager;
import jim.journal.Task;


public class HelpCommand extends Command {
    
    private static final String HELP_MULTIPART = "You do not need to enter a full description. If your entry " +
                                                 "is ambiguous, an indexed list will be shown to you, and you " +
                                                 "can pick the item you want using <b>%s (number)</b>";
    
    private static final String HELP_FAILED = "The command '%s' is not recognized. Type <b>help</b> to see " +
                                              "all available commands.";
    private static final String HELP_GENERIC = "<b><u><i>JIM!</i> Quick Start Guide</u></b><br>" + 
                                               "<i>JIM!</i> is powered by a set of the following commands:<br><br>" +
                                               "<b>add</b> - Adds a new task to storage<br>" + 
                                               "<b>complete</b> - Marks a task as 'done'<br>" + 
                                               "<b>config</b> - Allows you to change configuration<br>" +
                                               "<b>display</b> - Displays saved tasks<br>" + 
                                               "<b>edit</b> - Edits an existing task<br>" + 
                                               "<b>help</b> - Provides usage instructions<br>" +
                                               "<b>redo</b> - Reverses an undo operation<br>" + 
                                               "<b>remove</b> - Removes an existing task<br>" + 
                                               "<b>search</b> - Searches for a task<br>" + 
                                               "<b>undo</b> - Reverts the last performed action<br><br>" +  
                                               "Type <b>help (command name)</b> for more information<br>" +
                                               "Type <b>help keys</b> for a keystrokes guide!";
    private static final String HELP_ADD = "<b><u>Add Command</u></b><br>" +
                                           "<b>Aliases: </b>create, new, +<br>" + 
                                           "Use the <b>add</b> command to create one of three types of tasks: <br><br>" +
                                           "<b>add (task description)</b><br>Creates a 'To-Do' task which persists until cleared<br><br>" +
                                           "<b>add (deadline date) (task description)</b><br>Creates a 'Deadline' task with only an end date<br><br>" +
                                           "<b>add (start date) (start time) (end date) (end time) (description)</b><br>Creates a 'timed' task with start and end times and dates";
    private static final String HELP_COMPLETE = "<b><u>Complete Command</u></b><br>" +
                                                "<b>Aliases: </b>done, finish, *<br><br>" +
                                                "Use the <b>complete</b> command to mark any task as 'completed': <br>" +
                                                "<b>complete (description)</b> - Selects an item to mark as complete<br><br>" +
                                                String.format(HELP_MULTIPART, "complete");
    private static final String HELP_CONFIG = "<b><u>Config Command</u></b><br>" +
                                              "<b>Aliases: </b>configuration, configure<br><br>" +
                                              "Use the <b>config</b> command to review or set the <i>JIM!</i> configuration<br>" +
                                              "<b>config</b> - Shows a list of available settings<br>" +
                                              "<b>config reset</b> - Resets all settings to default<br>" +
                                              "<b>config (setting name) (new value)</b> - Sets a setting to the new value";
    private static final String HELP_DISPLAY = "<b><u>Display Command</u></b><br>" +
                                               "<b>Aliases: </b>show, !" +
                                               "Use the <b>display</b> command to show all items within a date window:<br>" + 
                                               "<b>display</b> - Shows the tasks from the past and coming weeks<br>" + 
                                               "<b>display (date)</b> - Shows all tasks occuring on a particular date";
    private static final String HELP_EDIT = "<b><u>Edit Command</u></b><br>" +
                                            "<b>Aliases: </b>modify, change, update, :" +
                                            "Use the <b>edit</b> command to edit an existing task:<br>" +
                                            "<b>edit (description)</b> - Selects an item for editing<br><br>" +
                                            String.format(HELP_MULTIPART, "edit") +
                                            "<br><br>You will be prompted to enter the updated version of the task.";
    private static final String HELP_HELP = "<b><u>Help Command</u></b><br>" +
                                            "Use the <b>help</b> command to get information about how to use <i>JIM!</i><br>" +
                                            "<b>help</b> - Displays a list of all of <i>JIM!</i>'s commands<br>" +
                                            "<b>help (command)</b> - Displays detailed help for the selected command";
    private static final String HELP_REDO = "<b><u>Redo Command</u></b><br>" + 
                                            "Use the <b>redo</b> command to reverse the effects of the <b>undo</b> command.<br>" +
                                            "This can be done as many times as <B>undo</B> was used in the same session.<br><br>" +
                                            "<b>Redo</b> can also be triggered with the <b><i>CTRL-Y</i></b> key combination";
    private static final String HELP_REMOVE = "<b><u>Remove Command</u></b><br>" +
                                              "<b>Aliases: </b>delete, cancel, -<br>" +
                                              "Use the <b>remove</b> command to delete existing tasks<br>" + 
                                              "<b>remove (description)</b> - Selects an item for removal<br><br>" +
                                              String.format(HELP_MULTIPART, "remove");
    private static final String HELP_SEARCH = "<b><u>Search Command</u></b><br>" +
                                              "<b>Aliases: </b>find, query, ?<br>" +
                                              "Use the <b>search</b> command to search existing tasks<br>" +
                                              "<b>search (description)</b> - Displays a list of items that match the search term";
    private static final String HELP_UNDO = "<b><u>Undo Command</u></b><br>" +
                                            "Use the <b>undo</b> command to reverse the last executed action<br>" +
                                            "Multiple steps of undo is possible for all actions in a session.<br><br>" +
                                            "<b>Undo</b> can also be triggered with the <b><i>CTRL-Z</i></b> key combination";
    private static final String HELP_KEYS = "<b><u>Keystrokes Guide</u></b><br>" +
                                            "<b>ESC</b> - Closes <i>JIM!</i><br>" +
                                            "<b>Enter</b> - Accepts a typed command<br>" +
                                            "<b>Backspace</b> - Returns screen to Journal View if input box is empty<br>" +
                                            "<b>TAB</b> - Select the next suggestion when suggestions are displayed<br>" +
                                            "<b>Shift-TAB</b> - Select the previous suggestion when suggestions are displayed<br>" +
                                            "<b>CTRL-Z</b> - Performs an Undo<br>" +
                                            "<b>CTRL-Y</b> - Performs a Redo";
    
    private String command;

    public HelpCommand(String selectedItem) {
        command = selectedItem.toLowerCase();
    }
    
    @Override
    public String execute(JournalManager journalManager) {
        if      (command.equals("")) { outputln(HELP_GENERIC); }
        else if (command.equals("add")) { outputln(HELP_ADD); }
        else if (command.equals("complete")) { outputln(HELP_COMPLETE); }
        else if (command.equals("config")) { outputln(HELP_CONFIG); }
        else if (command.equals("display")) { outputln(HELP_DISPLAY); }
        else if (command.equals("edit")) { outputln(HELP_EDIT); }
        else if (command.equals("help")) { outputln(HELP_HELP); }
        else if (command.equals("redo")) { outputln(HELP_REDO); }
        else if (command.equals("remove")) { outputln(HELP_REMOVE); }
        else if (command.equals("undo")) { outputln(HELP_UNDO); }
        else if (command.equals("search")) { outputln(HELP_SEARCH); } 
        else if (command.equals("keys")) { outputln(HELP_KEYS); }
        
        
        else { 
            outputln(String.format(HELP_FAILED, command));
            return "Failure";
        }

        return "Success";
    }

    @Override
    public String secondExecute(String secondInput) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String toString() {
        return "Help";
    }

}
