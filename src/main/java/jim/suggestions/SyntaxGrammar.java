package jim.suggestions;

import static jim.util.DateUtils.REGEX_DATE_DDMMYY;
import static jim.util.DateUtils.REGEX_TIME_HHMM;
import static jim.util.StringUtils.stripStringPrefixSuffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@author A0088816N
public class SyntaxGrammar {

	// Not to be instantiated.
	private SyntaxGrammar() {
		
	}

    protected static void initSyntax(Map<String, List<SyntaxFormat>> syntaxClassesMap) {
    	
        // Initialise our syntax classes dictionary.
        final String[] syntaxes = new String[]{
			"<monthname> := " +
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
			  "'December' | 'Dec' | 'Dec.'",
			"<dayOfWeek> := " +
			  "'Monday' | 'Mon' | 'Mon.' | " +
			  "'Tuesday' | 'Tue' | 'Tue.' | 'Tues' | 'Tues.' | " +
			  "'Wednesday' | 'Wed' | 'Wed.' | " +
			  "'Thursday' | 'Thu' | 'Thu.' | 'Thur' | 'Thur.' | " +
			  "'Friday' | 'Fri' | 'Fri.' | " +
			  "'Saturday' | 'Sat' | 'Sat.' | " +
			  "'Sunday' | 'Sun' | 'Sun.'",
			"<dayofmonth> := /(\\d?\\d)(st|nd|rd|th)?/",
			"<ddmmyy> := /" + REGEX_DATE_DDMMYY + "/ | /\\d\\d\\d\\d\\d\\d/ | /\\d\\d-\\d\\d-\\d\\d/",
			"<yyyymmdd> := /(\\d\\d\\d\\d)[/-]?(\\d\\d)[/-]?(\\d\\d)/",
			"<monthday> := /\\d\\d/\\d\\d/ | <monthname> <dayofmonth> | <dayofmonth> <monthname>",
			"<constantRelativeDates> := 'yesterday' | 'today' | 'tomorrow' ",
			"<relativeDays> := <dayOfWeek> | 'this' <dayOfWeek> | 'next' <dayOfWeek> | 'last' <dayOfWeek>",
			"<date> := <ddmmyy> | <yyyymmdd> | <monthday> | <constantRelativeDates> | <relativeDays>",
			"<hhmm> := /(\\d\\d):?(\\d\\d)[Hh]/ | /(\\d?\\d):?(\\d\\d)/",
			"<ampmtime> := " +
			  "/(\\d?\\d)([AaPp])[Mm]?/ | " +
			  "/(\\d?\\d):?(\\d\\d)([AaPp])[Mm]?/ | " +
			  "/(\\d?\\d):?(\\d\\d)/ /([AaPp])[Mm]?/",
			"<time> := <hhmm> | <ampmtime>",
			"<word> := /\\S+/",
			"<phrase> := <word> | <word> <phrase>",
			"<description> := <phrase>",
			
			"<timedtask> := " +
			  "<date> <time> <date> <time> <description> | " +
			  "<description> <date> <time> <date> <time> | " +
			  "<date> <time> 'to' <time> <description> | " +
			  "<date> <time> <time> <description>",
			"<deadlinetask> := <date> <time> <description> | <date> <description>",
			"<floatingtask> := <description>",
			"<task> := <timedtask> | <deadlinetask> | <floatingtask>",

			"<addword> := 'add' | 'create' | 'new' | '+'",
			"<addcmd> := <addword> <task>",
			"<completeword> := 'complete' | 'done' | 'finish' | '*'",
			"<uncompleteword> := 'uncomplete' | 'undone' | 'unfinish' | '**'",
			"<completecmd> := <completeword> <date> | <completeword> <description>",
			"<uncompletecmd> := <uncompleteword> <date> | <uncompleteword> <description>",
			"<removeword> := 'remove' | 'delete' | 'cancel' | '-'",
			"<removecmd> := <removeword> <date> | <removeword> <description>",
			"<editword> := 'edit' | 'modify' | 'change' | 'update' | ':'",
			"<editcmd> := <editword> <date> | <editword> <description>",
			"<searchword> := 'search' | 'find' | 'query' | '?'",
			"<searchcmd> := <searchword> <date> | <searchword> <word>",
			"<displayword> := 'display' | 'show' | '!' | 'ls'",
			"<displaycmd> := <displayword> | <displayword> <date>",
			"<configword> := 'config' | 'configuration' | 'configure'",
			"<configcmd> := <configword> | <configword> <description> <description> | <configword> <description>",
			"<helpword> := 'help'",
			"<helpcmd> := <helpword> | <helpword> <description>",
			"<undocmd> := 'undo'",
			"<redocmd> := 'redo'",
			
			"<cmd> := " +
			"<addcmd> | <completecmd> | <uncompletecmd> | <removecmd> | <editcmd> | <searchcmd> | " + 
			"<displaycmd> | <undocmd> | <redocmd> | <configcmd> | <helpcmd>"
			  
        };
        
        for (String syntaxDefinitionLine : syntaxes) {
        	addSyntax(syntaxClassesMap, syntaxDefinitionLine);
        }
    }
    
    protected static void addSyntax(Map<String, List<SyntaxFormat>> syntaxClassesMap, String syntaxLine) {
        String[] syntaxLineParts = syntaxLine.split(" := ");
        String syntaxClassName = stripStringPrefixSuffix(syntaxLineParts[0], 1);
        String[] definedAsSyntaxTerms = syntaxLineParts[1].split(" \\| ");
        
        List<SyntaxFormat> definitions = new ArrayList<SyntaxFormat>(definedAsSyntaxTerms.length);
        
        for(int i = 0; i < definedAsSyntaxTerms.length; i++) {
        	definitions.add(SyntaxFormat.valueOf(definedAsSyntaxTerms[i]));
        }
        
        syntaxClassesMap.put(syntaxClassName,
                             definitions);
    }
}
