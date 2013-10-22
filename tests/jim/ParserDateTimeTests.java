package jim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jim.journal.FloatingTask;
import jim.journal.Task;
import jim.suggestions.SuggestionManager;

import org.joda.time.MutableDateTime;
import org.junit.Test;

public class ParserDateTimeTests {
	
	private void assertDateParsesAs(SuggestionManager suggMan,
                                    int expectedDayOfMonth,
                                    int expectedMonth,
                                    String input) {
		MutableDateTime dateTime = suggMan.parseDate(input);
		assertEquals(expectedDayOfMonth, dateTime.getDayOfMonth());
		assertEquals(expectedMonth, dateTime.getMonthOfYear());
	}

    @Test
    public void testCanParseVariousDateFormats () {
        SuggestionManager suggMan = new SuggestionManager();

        assertDateParsesAs(suggMan, 31, 12, "311213");
        assertDateParsesAs(suggMan, 31, 12, "December 31");
        assertDateParsesAs(suggMan, 31, 12, "December 31st");
        assertDateParsesAs(suggMan, 31, 12, "31 Dec");
        assertDateParsesAs(suggMan, 31, 12, "Dec. 31");
        assertDateParsesAs(suggMan, 31, 12, "31/12");
        assertDateParsesAs(suggMan, 31, 12, "2013/12/31");
    }

	private void assertTimeParsesAs(SuggestionManager suggMan,
                                    int expectedHourOfDay,
                                    int expectedMinuteOfHour,
                                    String input) {
		MutableDateTime dateTime = suggMan.parseTime(input);
		assertEquals(expectedHourOfDay, dateTime.getHourOfDay());
		assertEquals(expectedMinuteOfHour, dateTime.getMinuteOfHour());
	}
    

    @Test
    public void testCanParseVariousTimeFormats () {
        SuggestionManager suggMan = new SuggestionManager();

        assertTimeParsesAs(suggMan, 6, 0, "0600");
        assertTimeParsesAs(suggMan, 6, 0, "0600h");
        assertTimeParsesAs(suggMan, 18, 0, "6:00");
        assertTimeParsesAs(suggMan, 6, 0, "06:00");
        assertTimeParsesAs(suggMan, 6, 0, "6a");
        assertTimeParsesAs(suggMan, 18, 0, "6P");
        assertTimeParsesAs(suggMan, 18, 30, "6:30pm");
        assertTimeParsesAs(suggMan, 18, 30, "6:30 pm");
    }
}
