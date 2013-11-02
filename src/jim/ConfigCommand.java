package jim;

import jim.journal.JournalManager;
import jim.journal.Task;


public class ConfigCommand extends jim.journal.Command {
    private static final String QUERY_MODE_OUTPUT = "Available Settings:\n"+
                                                    "%s\n";
    private static final String STRING_NEED_RESTART = "You may need to restart <i>JIM!</i> for " + 
                                                      "your changes to be applied.";
    
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
    
    @Override
    public String execute(JournalManager journalManager) {        
        if (selectedConfiguration == null) {
            // Query Mode
            outputln(String.format(QUERY_MODE_OUTPUT, configManager.toString()));
            return "Success";
        }
        
        else {
            // Setting Change Mode
            if (selectedConfiguration.equals("reset")) {
                configManager.setOutputFileName(Configuration.DEFAULT_FILENAME);
                configManager.setDateSeparator(Configuration.DEFAULT_DATE_SEPARATOR);
                configManager.setTimeSeparator(Configuration.DEFAULT_TIME_SEPARATOR);
                outputln("Configuration has been reset to defaults");
                outputln(STRING_NEED_RESTART);
            }
            
            else if (selectedConfiguration.equals("outputfilename")) {
                configManager.setOutputFileName(newParameter);
                outputln("Task Storage file has been set to " + newParameter);
                outputln(STRING_NEED_RESTART);
            }
            
            else if (selectedConfiguration.equals("dateseparator")) {
                configManager.setDateSeparator(newParameter);
                outputln("Date separator has been set to " + newParameter);
                outputln(STRING_NEED_RESTART);
            }
            
            else if (selectedConfiguration.equals("timeseparator")) {
                configManager.setTimeSeparator(newParameter);
                outputln("Time separator has been set to " + newParameter);
                outputln(STRING_NEED_RESTART);
            }
            
            else {
                outputln("Your input was not recognized");
                return "Failure";
            }
            
            configManager.writeSettings();
            return "Success";
        }
    }

    @Override
    public String secondExecute(String secondInput) {
        // Will never be called
        return null;
    }

    @Override
    public String thirdExecute(Task task) {
        // Will never be called
        return null;
    }
    
}
