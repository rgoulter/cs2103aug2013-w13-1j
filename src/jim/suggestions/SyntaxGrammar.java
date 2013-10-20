package jim.suggestions;

import static jim.util.DateUtils.REGEX_DATE_DDMMYY;
import static jim.util.DateUtils.REGEX_TIME_HHMM;

public class SyntaxGrammar {

	// Not to be instantiated.
	private SyntaxGrammar() {
		
	}
    
    


    protected static void initSyntax(Parser p) {
        // Initialise our syntax classes dictionary.
        // TODO: Would it be possible to have this in an external file? Or would that be more confusing?
        p.addSyntax("<monthname> := " +
                    "'January' | 'Jan' | 'Jan.' | " +
                    "'February' | 'Feb' | 'Feb.' | " +
                    "'March' | 'Mar' | 'Mar.' | " +
                    "'April' | 'Apr' | 'Apr.' | " +
                    "'May' | " +
                    "'June' | 'Jun' | 'Jun.' | " +
                    "'July' | 'Jul' | 'Jul.' | " +
                    "'August' | 'Aug' | 'Aug.' | " +
                    "'September' | 'Sep' | 'Sep.' | 'Sept' | 'Sept.' | " +
                    "'October' | 'Oct' | 'Oct.' | " +
                    "'November' | 'Nov' | 'Nov.' | " +
                    "'December' | 'Dec' | 'Dec.'");
        p.addSyntax("<dayofmonth> := /(\\d?\\d)(st|nd|rd|th)?/");
        p.addSyntax("<ddmmyy> := /" + REGEX_DATE_DDMMYY + "/ | /\\d\\d\\d\\d\\d\\d/ | /\\d\\d-\\d\\d-\\d\\d/");
        p.addSyntax("<yyyymmdd> := /(\\d\\d\\d\\d)[/-]?(\\d\\d)[/-]?(\\d\\d)/");
        p.addSyntax("<monthday> := /\\d\\d/\\d\\d/ | <monthname> <dayofmonth> | <dayofmonth> <monthname>");
        p.addSyntax("<date> := <ddmmyy> | <yyyymmdd> | <monthday>");
        p.addSyntax("<hhmm> := /(\\d\\d):?(\\d\\d)[Hh]/ | /(\\d?\\d):?(\\d\\d)/");
        p.addSyntax("<time> := <hhmm>");
        p.addSyntax("<word> := /\\S+/"); // non whitespace
        p.addSyntax("<phrase> := <word> | <word> <phrase>");
        p.addSyntax("<description> := <phrase>");

        p.addSyntax("<timedtask> := " +
                    "<date> <time> <date> <time> <description> | " +
                    "<description> <date> <time> <date> <time> | " +
                    "<date> <time> 'to' <time> <description> | " +
                    "<date> <time> <time> <description>");
        p.addSyntax("<deadlinetask> := <date> <description>");
        p.addSyntax("<floatingtask> := <description>");
        p.addSyntax("<task> := <timedtask> | <deadlinetask> | <floatingtask>");
        
        p.addSyntax("<addcmd> := 'add' <task>");
        p.addSyntax("<completecmd> := 'complete' <description>");
        p.addSyntax("<removecmd> := 'remove' <description>");
        p.addSyntax("<editcmd> := 'edit' <description>");
        p.addSyntax("<searchcmd> := 'search' <description>");
        p.addSyntax("<displaycmd> := 'display' | 'display' <date>");
        p.addSyntax("<undocmd> := 'undo'");
        
        p.addSyntax("<cmd> := " +
                    "<addcmd> | <completecmd> | <removecmd> | " + 
                    "<editcmd> | <searchcmd> | <displaycmd>");
    }
}
