package jim.util;

import org.joda.time.MutableDateTime;

public class DateUtils {
    /**
     * Matches DD/MM/YY.
     */
    public static final String REGEX_DATE_DDMMYY = "\\d\\d/\\d\\d/\\d\\d";

    /**
     * Matches four digits in a row. e.g. HHMM.
     */
    public static final String REGEX_TIME_HHMM = "\\d\\d\\d\\d";

    
    
    /**
     * Merge a date and time value into a datetime.
     */
    public static MutableDateTime datetime(MutableDateTime date,
                                           MutableDateTime time) {
        return new MutableDateTime(date.getYear(),
                                   date.getMonthOfYear(),
                                   date.getDayOfMonth(),
                                   time.getHourOfDay(),
                                   time.getMinuteOfHour(),
                                   time.getSecondOfMinute(),
                                   00);
    }
}
