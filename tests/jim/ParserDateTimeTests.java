package jim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jim.journal.FloatingTask;
import jim.journal.Task;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;

public class ParserDateTimeTests {

    @Test
    public void testCanParseVariousDateFormats () {
        SuggestionManager suggMan = new SuggestionManager();
        
        MutableDateTime dateTime;
        
        dateTime = suggMan.parseDate("311213");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
        
        dateTime = suggMan.parseDate("December 31");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
        
        dateTime = suggMan.parseDate("December 31st");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
        
        dateTime = suggMan.parseDate("31 Dec");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
        
        dateTime = suggMan.parseDate("Dec. 31");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
        
        dateTime = suggMan.parseDate("31/12");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
        
        dateTime = suggMan.parseDate("2013/12/31");
        assertEquals(31, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getMonthOfYear());
    }

    @Test
    public void testCanParseVariousTimeFormats () {
        SuggestionManager suggMan = new SuggestionManager();
        
        MutableDateTime dateTime;
        
        dateTime = suggMan.parseTime("0600");
        assertEquals(6, dateTime.getHourOfDay());
        assertEquals(0, dateTime.getMinuteOfHour());
        
        dateTime = suggMan.parseTime("0600h");
        assertEquals(6, dateTime.getHourOfDay());
        assertEquals(0, dateTime.getMinuteOfHour());
        
        dateTime = suggMan.parseTime("6:00");
        assertEquals(18, dateTime.getHourOfDay());
        assertEquals(0, dateTime.getMinuteOfHour());
        
        dateTime = suggMan.parseTime("06:00");
        assertEquals(6, dateTime.getHourOfDay());
        assertEquals(0, dateTime.getMinuteOfHour());
        
        dateTime = suggMan.parseTime("6a");
        assertEquals(6, dateTime.getHourOfDay());
        assertEquals(0, dateTime.getMinuteOfHour());
        
        dateTime = suggMan.parseTime("6P");
        assertEquals(18, dateTime.getHourOfDay());
        assertEquals(0, dateTime.getMinuteOfHour());
        
        dateTime = suggMan.parseTime("6:30pm");
        assertEquals(18, dateTime.getHourOfDay());
        assertEquals(30, dateTime.getMinuteOfHour());
        
        
        dateTime = suggMan.parseTime("6:30 pm");
        assertEquals(18, dateTime.getHourOfDay());
        assertEquals(30, dateTime.getMinuteOfHour());
    }
}
