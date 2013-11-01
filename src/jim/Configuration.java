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
    private static final String FILE_DEFAULT_CONTENT = "outputFileName=taskStorage.txt\n" +
                                                       "dateSeparator=/\n" +
                                                       "timeSeparator=:\n";
    private static final int LINE_1_START_POS = 14;
    private static final int LINE_2_START_POS = 13;
    private static final int LINE_3_START_POS = 13;
    
    // This class uses a singleton pattern!
    private static Configuration configItem;
    private static String outputFileName;
    private static String dateSeparator;
    private static String timeSeparator;
    
    private Scanner initializeFile() {
        try {
            RandomAccessFile initFile = new RandomAccessFile(CONFIG_FILE_NAME, FILE_READWRITE_MODE);
            initFile.writeBytes(FILE_DEFAULT_CONTENT);
            initFile.close();
            
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

    
    // Accessors
    public String getOutputFileName() { return outputFileName; }
    public String getDateSeparator() { return dateSeparator; }
    public String getTimeSeparator() { return timeSeparator; }
}
