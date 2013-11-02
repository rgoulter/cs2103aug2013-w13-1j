/* CONFIG FILE FORMAT:
 * 
 * outputFileName=<output file name>
 * dateSeparator=<date separator>
 * timeSeparator=<time separator>
 */

package jim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Configuration {
    private static final String CONFIG_FILE_NAME = "config.ini";
    private static final String FILE_READWRITE_MODE = "rw";
    public static final String DEFAULT_FILENAME = "taskStorage.txt";
    public static final String DEFAULT_DATE_SEPARATOR = "/";
    public static final String DEFAULT_TIME_SEPARATOR = ":";
    private static final String TEMPLATE_STRING = "outputFileName=%s\n" +
                                                  "dateSeparator=%s\n" +
                                                  "timeSeparator=%s\n";
    private static final int LINE_1_START_POS = 15;
    private static final int LINE_2_START_POS = 14;
    private static final int LINE_3_START_POS = 14;
    
    // This class uses a singleton pattern!
    private static Configuration configItem;
    private static String outputFileName;
    private static String dateSeparator;
    private static String timeSeparator;
    
    private Scanner initializeFile() {
        try {
            setOutputFileName(DEFAULT_FILENAME);
            setDateSeparator(DEFAULT_DATE_SEPARATOR);
            setTimeSeparator(DEFAULT_TIME_SEPARATOR);
            
            writeSettings();
            return new Scanner(new File(CONFIG_FILE_NAME));
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    private Configuration() {
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new File(CONFIG_FILE_NAME));
        } catch (FileNotFoundException e) {
            fileScanner = initializeFile();
        }
        
        outputFileName = fileScanner.nextLine().substring(LINE_1_START_POS);
        dateSeparator = fileScanner.nextLine().substring(LINE_2_START_POS);
        timeSeparator = fileScanner.nextLine().substring(LINE_3_START_POS);
        
        fileScanner.close();
    }
    
    private static void conditionallyInitialize() {
        if (configItem == null) {
            configItem = new Configuration();
        }
    }
    
    public static Configuration getConfiguration() {
        conditionallyInitialize();
        return configItem;
    }

    public String toString() {
        return String.format(TEMPLATE_STRING, getOutputFileName(),
                                              getDateSeparator(),
                                              getTimeSeparator());
    }
    
    // Accessors
    public String getOutputFileName() { return outputFileName; }
    public String getDateSeparator() { return dateSeparator; }
    public String getTimeSeparator() { return timeSeparator; }
    
    // Mutator
    public void setOutputFileName(String newFileName) { outputFileName = newFileName; }
    public void setDateSeparator(String newSeparator) { dateSeparator = newSeparator; }
    public void setTimeSeparator(String newSeparator) { timeSeparator = newSeparator; }
    
    public void writeSettings() {
        try {
            RandomAccessFile settingsFile = new RandomAccessFile(CONFIG_FILE_NAME, FILE_READWRITE_MODE);
            settingsFile.setLength(0);
            
            settingsFile.writeBytes(this.toString());
            settingsFile.close();
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
