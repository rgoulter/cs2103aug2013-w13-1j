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
}
