//@author A0096790N
package jim;

import jim.journal.JournalManager;
import jim.journal.Task;


public class ConfigCommand extends jim.journal.Command {
    
    private static final String QUERY_MODE_OUTPUT = "Available Settings:\n"+
                                                    "%s\n";
    private static final String STRING_NEED_RESTART = "You may need to restart <i>JIM!</i> for " + 
                                                      "your changes to be applied.";
    
    // Feedback Strings
    private static final String FEEDBACK_STRING_UNRECOGNIZED = "Your input was not recognized. You may only modify one of the following:\n\n%s";
    private static final String FEEDBACK_TIME_SUCCESSFUL = "Time separator has been set to %s";
    private static final String FEEDBACK_DATE_SUCCESSFUL = "Date separator has been set to %s";
    private static final String FEEDBACK_FILENAME_SUCCESSFUL = "Task Storage file has been set to %s";
    private static final String FEEDBACK_RESET = "Configuration has been reset to defaults";
    
    // Execution States
    private static final String EXECUTION_FAILED = "Failure";
    private static final String EXECUTION_SUCCESSFUL = "Success";
    
    private String selectedConfiguration;
    private String newParameter;
    private Configuration configManager;

    public ConfigCommand(String configType, String parameter) {        
        if (configType != null) { selectedConfiguration = configType.toLowerCase(); }
        newParameter = parameter;
        
        configManager = Configuration.getConfiguration();
    }
    
    public ConfigCommand(String configType) {
        this(configType, null);
    }
    
    public ConfigCommand() {
        this(null, null);
    }
    
    public static String[] getValidArguments() {
    	// This is a bit magic, but not much more than 
    	// the way Configuration and ConfigCommand have
    	// already been implemented.
    	
    	// see execute() for why these values are these.
    	return new String[]{"reset",
	                        "outputfilename",
	                        "dateseparator",
	                        "timeseparator"};
    }
    
    @Override
    public String execute(JournalManager journalManager) {        
        if (selectedConfiguration == null) {
            // Query Mode
            outputln(String.format(QUERY_MODE_OUTPUT, configManager.toString()));
            return EXECUTION_SUCCESSFUL;
        }
        
        else {
            // Setting Change Mode
            if (selectedConfiguration.equals("reset")) {
                configManager.setOutputFileName(Configuration.DEFAULT_FILENAME);
                configManager.setDateSeparator(Configuration.DEFAULT_DATE_SEPARATOR);
                configManager.setTimeSeparator(Configuration.DEFAULT_TIME_SEPARATOR);
                outputln(FEEDBACK_RESET);
                outputln(STRING_NEED_RESTART);
            }
            
            else if (selectedConfiguration.equals("outputfilename") && newParameter != null) {
                configManager.setOutputFileName(newParameter);
                outputln(String.format(FEEDBACK_FILENAME_SUCCESSFUL, newParameter));
                outputln(STRING_NEED_RESTART);
            }
            
            else if (selectedConfiguration.equals("dateseparator") && newParameter != null) {
                configManager.setDateSeparator(newParameter);
                outputln(String.format(FEEDBACK_DATE_SUCCESSFUL, newParameter));
                outputln(STRING_NEED_RESTART);
            }
            
            else if (selectedConfiguration.equals("timeseparator") && newParameter != null) {
                configManager.setTimeSeparator(newParameter);
                outputln(String.format(FEEDBACK_TIME_SUCCESSFUL, newParameter));
                outputln(STRING_NEED_RESTART);
            }
            
            else {
                outputln(String.format(FEEDBACK_STRING_UNRECOGNIZED, configManager.toString()));
                return EXECUTION_FAILED;
            }
            
            configManager.writeSettings();
            return EXECUTION_SUCCESSFUL;
        }
    }

    // The following functions will never be called, and are only present to correctly extend Command.class
    public String secondExecute(String secondInput) { return null; }
    public String thirdExecute(Task task) { return null; }
    
}
