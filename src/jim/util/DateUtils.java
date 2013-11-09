package jim.util;

import java.util.Arrays;

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
    
    public static int getMonthOfYearFromMonthName(String monthName) {
    	final String MONTHS_OF_YEAR = "jan feb mar apr may jun jul aug sep oct nov dec";
    	
    	int zeroBasedMonthOfYear = MONTHS_OF_YEAR.indexOf(monthName.toLowerCase().substring(0, 3)) / 4;
    	
    	return zeroBasedMonthOfYear + 1;
    }
    
    public static int getCurrentYear() {
    	return new MutableDateTime().getYear();
    }
    
    public static boolean isHourLikelyToBePM(int h) {
    	return !(8 <= h && h <= 11);
    }
    
    public static int ensureHourIsPM(int h) {
    	return (h + 12) % 12 + 12;
    }
    
    // Not to be instantiated.
    private DateUtils() {
    }
    
    //@author A0096790N
    public static int getDayOfWeekFromDayName(String dayName) {
        final String DAYS_OF_WEEK = "mon tue wed thu fri sat sun";
        int zeroBasedDayOfWeek = DAYS_OF_WEEK.indexOf(dayName.toLowerCase().substring(0, 3)) / 4;
        
        return zeroBasedDayOfWeek+1;
    }
}
