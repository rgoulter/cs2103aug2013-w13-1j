
package jim.suggestions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jim.journal.AddCommand;
import jim.journal.CompleteCommand;
import jim.journal.DisplayCommand;
import jim.journal.EditCommand;
import jim.journal.RemoveCommand;
import jim.journal.SearchCommand;
import jim.journal.TimedTask;



public class SuggestionManager {

    private static final String BLACK_COLOR = "</font>"; // Only use this to
                                                         // return to black from
                                                         // other color
    private static final String RED_COLOR = "<font color='red'>";
    private static final String BLUE_COLOR = "<font color='blue'>";
    private static final String PURPLE_COLOR = "<font color='purple'>";
    private static final String GREEN_COLOR = "<font color='green'>";
    private static final String GREY_COLOR = "<font color='gray'>";

    private int highlightedLine = -1;
    private static final int DEADLINE_TASK_DETECTED = 1;
    private static final int DEADLINE_TASK_WITHOUT_TIME = 0;
    private static final int LENGTH_OF_DATE = 3; // [DD][MM][YY]
    private static final int LENGTH_OF_TIME = 2; // [22(hrs)][00(mins)]
    private static final int INDICATE_YEAR = 2;
    private static final int INDICATE_MONTH = 1;
    private static final int INDICATE_TIME_STRING = 4;
    private static final int INDICATE_DATE_STRING = 6;
    private static final int INDICATE_TASK_ENDS_IN_A_DAY = 1;

    /**
     * Matches DD/MM/YY.
     */
    private static final String REGEX_DATE_DDMMYY = "\\d\\d/\\d\\d/\\d\\d";

    /**
     * Matches four digits in a row. e.g. HHMM.
     */
    private static final String REGEX_TIME_HHMM = "\\d\\d\\d\\d";

    /**
     * "Phrase" here is a minimal amount of words.
     */
    private static final String REGEX_PHRASE = "(?:\\S+\\s?)+";

    /**
     * Accepted Add Command Formats:
     * 
     * add DD/MM/YY HHMM DD/MM/YY HHMM <description>
     */
    public enum AddCommandFormats {
        AddDateTimeDateTimeDescription(join(new String[] {"add",
                                                          REGEX_DATE_DDMMYY,
                                                          REGEX_TIME_HHMM,
                                                          REGEX_DATE_DDMMYY,
                                                          REGEX_TIME_HHMM,
                                                          REGEX_PHRASE}, ' '));

        String format;



        AddCommandFormats(String fmt) {
            format = fmt;
        }

    }



    public List<String> getSuggestionsToDisplay() {
        // TODO: Stop cheating on this, as well =P
        List<String> displayedSuggestions = new ArrayList<String>();

        displayedSuggestions.add("<h2><font color='red'>Please ignore suggestions for now! ~CC</font></h2>");
        displayedSuggestions.add(BLUE_COLOR +
                                 "add" +
                                 BLACK_COLOR +
                                 " (name) (date) (time)");
        displayedSuggestions.add(RED_COLOR + "remove" + BLACK_COLOR + " (name)");
        displayedSuggestions.add("display");
        displayedSuggestions.add("exit");

        return displayedSuggestions;
    }



    public String getCurrentSuggestion() {
        String output = "";
        if (getCurrentSuggestionIndex() != -1) {
            List<String> allStrings = getSuggestionsToDisplay();
            output = allStrings.get(getCurrentSuggestionIndex());
        }

        return output;
    }



    public void setCurrentSuggestionIndex(int i) {
        highlightedLine = i;
    }



    public int getCurrentSuggestionIndex() {
        return highlightedLine;
    }



    public void nextSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() + 1) %
                                  getSuggestionsToDisplay().size());
    }



    public void prevSuggestion() {
        setCurrentSuggestionIndex((getCurrentSuggestionIndex() - 1));
        if (getCurrentSuggestionIndex() < 0) {
            setCurrentSuggestionIndex(getSuggestionsToDisplay().size() - 1);
        }
    }



    /**
     * Update the current 'buffer' of content for the suggestion manager to
     * process.
     * 
     * @param text
     *            The input currently in the textfield.
     */
    public void updateBuffer(String text) {

    }



    /**
     * Search the given Journal for a list of tasks which match the given
     * description. At present, "matching" is defined strictly as
     * "has the same description".
     * 
     * @param journal
     * @param description
     * @return
     */
    public List<jim.journal.Task> searchForTasksByDescription(jim.journal.JournalManager journal,
                                                              String description) {
        // TODO: check with user if the result matched what they really want???
        List<jim.journal.Task> matchingTasks = new ArrayList<>();

        for (jim.journal.Task task : journal.getAllTasks()) {
            if (task.getDescription().equals(description)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private static String join(String arrayOfStrings[], char joinChar) {
        return join(arrayOfStrings, joinChar, 0);
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private static String join(String arrayOfStrings[],
                               char joinChar,
                               int startIndex) {
        return join(arrayOfStrings, joinChar, startIndex, arrayOfStrings.length);
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    private static String join(String arrayOfStrings[],
                               char joinChar,
                               int startIndex,
                               int endIndex) {
        StringBuilder result = new StringBuilder();

        for (int i = startIndex; i < endIndex - 1; i++) {
            result.append(arrayOfStrings[i]);
            result.append(joinChar);
        }

        result.append(arrayOfStrings[endIndex - 1]);

        return result.toString();
    }



    private Calendar parseDate(String date) {
        return null;
    }



    private Calendar parseTime(String time) {
        return null;
    }



    private Calendar parseDateTime(String date, String time) {
        // Accepted Date Formats:
        // DD/MM/YY
        // Accepted Time Formats:
        // 24-hour

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HHmm");

        try {
            GregorianCalendar result = new GregorianCalendar();
            result.setTime(dateFormat.parse(date + " " + time));

            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



    public jim.journal.Task parseTask(String[] words) {
        // Accepted Task Syntaxes:
        // TimedTask:
        // <start-date> <start-time> <end-date> <end-time> <description..>
        // where date format is DD/MM/YY,
        // where time format is 24-hour

        Calendar startDateTime = parseDateTime(words[0], words[1]);
        Calendar endDateTime = parseDateTime(words[2], words[3]);
        String description = join(words, ' ', 2 + 2);

        return new TimedTask(startDateTime, endDateTime, description);
    }



    /**
     * Takes an array of strings, e.g. {"add", "lunch", "Monday", "2pm"}, and
     * returns a Journal command to be executed from this.
     * 
     * TODO: Potentially throw some kind of "Poor format exception" as a better
     * way of giving feedback than return-null.
     */
    public jim.journal.Command parseCommand(String args[]) {
        /*
         * TODO: Here is where some of our time will be spent in V0.0, getting
         * stuff like "add lunch Monday" (or some sensible command) to work.
         * 
         * Adding logic in TDD fashion is *ideal* for this kind of thing, I
         * would think.
         * 
         * Once basic format rules have been established, (e.g. strict syntax
         * for add, remove, etc.), THEN Any language logic here can infer fairly
         * sensibly how to access Journal API. Journal API can then trust that
         * this will be done sensibly by language logic.
         */

        // Parse the words into a Command object.
        // STRICT ASSUMPTION is that the first word is the "operating word".
        // (e.g. add, remove, etc.)
        // Naturally, this assumption will be broken with more flexible inputs.
        if (args[0].equals("add")) {
            return parseAddCommand(args);
        } else if (args[0].equals("complete")) {
            return parseCompleteCommand(args);
        } else if (args[0].equals("remove")) {
            return parseRemoveCommand(args);
        } else if (args[0].equals("edit")) {
            return parseEditCommand(args);
        } else if (args[0].equals("search")) {
            return parseSearchCommand(args);
        } else if (args[0].equals("display")) {
            return parseDisplayCommand(args);
        } else if (args[0].equals("exit")) {
            System.exit(0);
        }
        return null;
    }



    public String removeAllSymbols(String tellDateOrTime) {
        String findDate = tellDateOrTime.replaceAll("[^\\p{L}\\p{Nd}]", "");
        return findDate;
    }



    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }



    public int[] splitDate(String date_in_string) {
        // we will accept date format of 090913 - DDMMYY
        int[] dates = new int[LENGTH_OF_DATE];
        String[] temp = date_in_string.split("");
        int counter = 1; // temp[0] is a spacing
        for (int i = 0; i < LENGTH_OF_DATE; i++) {
            if (i == INDICATE_YEAR) {
                dates[i] = Integer.parseInt("20" +
                                            temp[counter++] +
                                            temp[counter++]);
            } else if (i == INDICATE_MONTH) {
                dates[i] = Integer.parseInt(temp[counter++] + temp[counter++]);
                dates[i] = dates[i] - 1; // month is 0-based, eg. January = 0
            } else {
                dates[i] = Integer.parseInt(temp[counter++] + temp[counter++]);
            }
        }
        return dates;
    }



    public int[] splitTime(String time_in_string) {
        // we will accept time format of 24 hours - 2200 hrs, default seconds is
        // 00
        int[] time_24hours = new int[LENGTH_OF_TIME];
        String[] temp = time_in_string.split("");
        int counter = 1; // temp[0] is a spacing
        for (int i = 0; i < LENGTH_OF_TIME; i++) {
            time_24hours[i] = Integer.parseInt(temp[counter++] +
                                               temp[counter++]);
        }
        return time_24hours;
    }



    private AddCommand parseAddCommand(String args[]) {
        // Accepted 'add' syntaxes:
        // add <start-date> <start-time> <end-date> <end-time> <words describing
        // event>
        // TODO: Add more syntaxes/formats for this command

        Calendar startDateTime = null;
        Calendar endDateTime = null;
        String description = null;

        for (AddCommandFormats format : AddCommandFormats.values()) {
            Pattern regexPattern = Pattern.compile(format.format);

            Matcher inputRegexMatcher = regexPattern.matcher(join(args, ' '));
            if (inputRegexMatcher.matches()) {
                switch (format) {
                case AddDateTimeDateTimeDescription:
                    startDateTime = parseDateTime(args[1], args[2]);
                    endDateTime = parseDateTime(args[3], args[4]);
                    description = join(args, ' ', 5);
                    break;

                default:
                    throw new IllegalStateException("A new AddCommandFormat was added, but not handled in ParseCommand.");
                }

                break;
            }
        }

        // TODO: Process values in a sensible way.
        if (startDateTime == null && endDateTime == null) {
            return new jim.journal.AddCommand(description);
        } else
            return new jim.journal.AddCommand(startDateTime,
                                              endDateTime,
                                              description);
    }



    private CompleteCommand parseCompleteCommand(String args[]) { // The
                                                                  // "Complete"
                                                                  // commands
        // Accepted 'complete' syntaxes:
        // complete <description>

        String description = join(args, ' ', 1);
        return new jim.journal.CompleteCommand(description);
    }



    private RemoveCommand parseRemoveCommand(String args[]) { // The "Remove"
                                                              // commands
        // Accepted 'remove' syntaxes:
        // remove <description>
        // TODO: Add more syntaxes/formats for this command

        String description = join(args, ' ', 1);
        return new jim.journal.RemoveCommand(description);
    }



    private EditCommand parseEditCommand(String args[]) { // The "Edit"
                                                          // commands
        // Accepted 'edit' syntaxes:
        // edit <description of old task>
        // (then read in from input what to replace it with).
        // TODO: Add more syntaxes/formats for this command
        // NOTE THAT: The format read in is a format which describes a Task.
        // (Timed, floating, etc.)

        String description = join(args, ' ', 1);
        return new EditCommand(description);
    }



    private SearchCommand parseSearchCommand(String args[]) { // The "Search"
                                                              // commands
        // Accepted 'search' syntaxes:
        // search <description>
        // TODO: Add more syntaxes/formats for this command

        String description = join(args, ' ', 1);
        return new jim.journal.SearchCommand(description);
    }



    private DisplayCommand parseDisplayCommand(String args[]) { // The
                                                                // "Display"
                                                                // commands
        // Accepted 'display' syntaxes:
        // display
        // display <date predicate>
        // TODO: Basic display command currently displays everything; Ideally we
        // want to limit this ~CC
        // TODO: Add more syntaxes/formats for this command

        if (args.length == 1) {
            // User asks to perform a plain display, which displays everything
            return new jim.journal.DisplayCommand();
        } else if (args.length == 2) {
            // User asks to perform display given a particular date

            // TODO: Currently, display expects a date in DD-MM-YY format; We
            // should change that ~CC
            // Rudimentary way to parse date ~CC
            String[] ddmmyy = args[1].split("-");
            GregorianCalendar date = new GregorianCalendar(Integer.parseInt(ddmmyy[2]),
                                                           Integer.parseInt(ddmmyy[1]),
                                                           Integer.parseInt(ddmmyy[0]));

            return new jim.journal.DisplayCommand(date);
        }

        return null;
    }
}
