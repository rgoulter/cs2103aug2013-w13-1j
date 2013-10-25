package jim.suggestions;

import static jim.util.DateUtils.REGEX_DATE_DDMMYY;
import static jim.util.DateUtils.REGEX_TIME_HHMM;
import static jim.util.StringUtils.stripStringPrefixSuffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyntaxGrammar {

	// Not to be instantiated.
	private SyntaxGrammar() {
		
	}
    
    


    protected static void initSyntax(Map<String, List<SyntaxFormat>> syntaxClassesMap) {
    	
        // Initialise our syntax classes dictionary.
        // TODO: Would it be possible to have this in an external file? Or would that be more confusing?
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
			"<dayofmonth> := /(\\d?\\d)(st|nd|rd|th)?/",
			"<ddmmyy> := /" + REGEX_DATE_DDMMYY + "/ | /\\d\\d\\d\\d\\d\\d/ | /\\d\\d-\\d\\d-\\d\\d/",
			"<yyyymmdd> := /(\\d\\d\\d\\d)[/-]?(\\d\\d)[/-]?(\\d\\d)/",
			"<monthday> := /\\d\\d/\\d\\d/ | <monthname> <dayofmonth> | <dayofmonth> <monthname>",
			"<date> := <ddmmyy> | <yyyymmdd> | <monthday>",
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
			"<deadlinetask> := <date> <description>",
			"<floatingtask> := <description>",
			"<task> := <timedtask> | <deadlinetask> | <floatingtask>",

			"<addword> := 'add' | 'create'",
			"<addcmd> := <addword> <task>",
			"<completeword> := 'complete'",
			"<completecmd> := <completeword> <description>",
			"<removeword> := 'remove'",
			"<removecmd> := <removeword> <description>",
			"<editword> := 'edit'",
			"<editcmd> := <editword> <description>",
			"<searchword> := 'search'",
			"<searchcmd> := <searchword> <description>",
			"<displayword> := 'display'",
			"<displaycmd> := <displayword> | <displayword> <date>",
			"<undocmd> := 'undo'",
			
			"<cmd> := " +
			  "<addcmd> | <completecmd> | <removecmd> | " + 
			  "<editcmd> | <searchcmd> | <displaycmd> | <undocmd>"
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
