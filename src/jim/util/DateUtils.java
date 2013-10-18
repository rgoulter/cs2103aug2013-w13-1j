package jim.util;

import org.joda.time.MutableDateTime;

public class DateUtils {

    
    
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
