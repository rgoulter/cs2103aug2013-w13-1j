package jim.suggestions;

import static jim.util.DateUtils.datetime;
import static jim.util.DateUtils.getCurrentYear;
import static jim.util.DateUtils.getMonthOfYearFromMonthName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.MutableDateTime;

import jim.journal.AddCommand;
import jim.journal.DeadlineTask;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.SearchCommand;
import jim.journal.TimedTask;
import jim.journal.UndoCommand;
import jim.suggestions.Parser.SyntaxFormat;
import static jim.util.DateUtils.REGEX_DATE_DDMMYY;
import static jim.util.DateUtils.REGEX_TIME_HHMM;
import static jim.util.StringUtils.removeAllSymbols;
import static jim.util.StringUtils.splitDate;

public class SyntaxParsers {



    protected interface SyntaxParser {
        public Object parse(String[] input);
    }

    protected static abstract class SyntaxTermParser implements SyntaxParser {
        public abstract Object parse(String inputTerm);
        
        public Object parse(String[] input){
            if (input.length != 1) {
                // To keep in line with the assumption of SyntaxTermParser
                throw new IllegalArgumentException("Only 1-length arrays allowed for SyntaxTermParser");
            }
            
            return parse(input[0]);
            
        }
    }



    protected static class SyntaxParserKey {
        private String synClass;
        private SyntaxFormat synFormat;

        public SyntaxParserKey(String syntaxClassName, SyntaxFormat syntaxFormat) {
            synClass = syntaxClassName;
            synFormat = syntaxFormat;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof SyntaxParserKey) {
                SyntaxParserKey otherKey = (SyntaxParserKey) o;
                return synClass.equals(otherKey.synClass) &&
                       synFormat.toString().equals(otherKey.synFormat.toString());
            }

            return false;
        }
        
        @Override
        public int hashCode() {
            return (synClass + synFormat.toString()).hashCode();
        }

        @Override
        public String toString() {
            return synClass + " => " + synFormat.toString();
        }

        /**
         * @param keyString in format of "syntaxClassName => syntaxFormat"
         */
        public static SyntaxParserKey valueOf(String keyString) {
            String[] parts = keyString.split(" => ");
            String className = parts[0];
            SyntaxFormat format = SyntaxFormat.valueOf(parts[1]);

            return new SyntaxParserKey(className, format);
        }
    }
    
	// Not for instantiation
	private SyntaxParsers() {
		
	}



    protected static void initSyntaxParsers(final Parser p) {
        // KEY: syntaxTerm + " => " + nextSyntaxTerm
        SyntaxTermParser genericDDMMYYParser =
        new SyntaxTermParser(){
            @Override
            public Object parse(String inputTerm) {
                return parseDDMMYY(inputTerm);
            }
        };
        registerSyntaxParser(p,
                          "ddmmyy => /" + REGEX_DATE_DDMMYY + "/",
                          genericDDMMYYParser);
        registerSyntaxParser(p,
                          "ddmmyy => /\\d\\d\\d\\d\\d\\d/",
                          genericDDMMYYParser);
        registerSyntaxParser(p,
                          "ddmmyy => /\\d\\d-\\d\\d-\\d\\d/",
                          genericDDMMYYParser);

        registerSyntaxParser(p,
                             "date => <monthname> <dayofmonth>",
							 new SyntaxParser() {
						 		 @Override
								 public Object parse(String[] inputTerm) {
						 			 String monthName = inputTerm[0];
						 			 String dayOfMonth = inputTerm[1];
									 return new MutableDateTime(getCurrentYear(),
											 					getMonthOfYearFromMonthName(monthName),
											 					parseDayOfMonth(dayOfMonth),
											 					0,
											 					0,
											 					0,
											 					0);
								 }
							 });
        registerSyntaxParser(p,
			                 "date => <dayofmonth> <monthname>",
							  new SyntaxParser() {
						 		  @Override
								  public Object parse(String[] inputTerm) {
						 			  String monthName = inputTerm[1];
						 			  String dayOfMonth = inputTerm[0];
									  return new MutableDateTime(getCurrentYear(),
											 					 getMonthOfYearFromMonthName(monthName),
											 					 parseDayOfMonth(dayOfMonth),
											 					 0,
											 					 0,
											 					 0,
											 					 0);
								  }
							  });


        registerSyntaxParser(p,
                          "hhmm => /" + REGEX_TIME_HHMM + "/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  int hh = Integer.parseInt(inputTerm.substring(0, 2));
                                  int mm = Integer.parseInt(inputTerm.substring(2));
                                  
                                  return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
                              }
                          });
        registerSyntaxParser(p,
                          "hhmm => /\\d\\d:\\d\\d/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  int hh = Integer.parseInt(inputTerm.substring(0, 2));
                                  int mm = Integer.parseInt(inputTerm.substring(3));
                                  
                                  return new MutableDateTime(0, 0, 0, hh, mm, 00, 00);
                              }
                          });
        

        // Redundant?
        registerSyntaxParser(p,
                          "word => /\\S+/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  return inputTerm;
                              }
                          });


        // Redundant?
        registerSyntaxParser(p,
                          "phrase => <word>",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
                                  // Get parser for <word> ...
                                  return inputTerm;
                              }
                          });
        registerSyntaxParser(p,
                          "phrase => <word> <phrase>",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String input) {
                                  return input;
                              }
                          });


        registerSyntaxParser(p,
                          "timedtask => <date> <time> <date> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime startDate =
                                          (MutableDateTime) p.doParse("<date>", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) p.doParse("<time>", input[1]);
                                  MutableDateTime endDate =
                                          (MutableDateTime) p.doParse("<date>", input[2]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) p.doParse("<time>", input[3]);
                                  String description = input[4];
                                  return new TimedTask(datetime(startDate, startTime),
                                                       datetime(endDate, endTime), description);
                              }
                          });
        registerSyntaxParser(p,
                "timedtask => <description> <date> <time> <date> <time>",
                new SyntaxParser() {
                    @Override
                    public Object parse(String[] input) {
                        String description = input[0];
                        MutableDateTime startDate =
                                (MutableDateTime) p.doParse("<date>", input[1]);
                        MutableDateTime startTime =
                                (MutableDateTime) p.doParse("<time>", input[2]);
                        MutableDateTime endDate =
                                (MutableDateTime) p.doParse("<date>", input[3]);
                        MutableDateTime endTime =
                                (MutableDateTime) p.doParse("<time>", input[4]);
                        return new TimedTask(datetime(startDate, startTime),
                                             datetime(endDate, endTime), description);
                    }
                });
        registerSyntaxParser(p,
                          "timedtask => <date> <time> 'to' <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse("<date>", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) p.doParse("<time>", input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) p.doParse("<time>", input[3]);
                                  String description = input[4];
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       description);
                              }
                          });
        registerSyntaxParser(p,
                          "timedtask => <date> <time> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse("<date>", input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) p.doParse("<time>", input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) p.doParse("<time>", input[2]);
                                  String description = input[3];
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       description);
                              }
                          });
        registerSyntaxParser(p,
                          "deadlinetask => <date> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse("<date>", input[0]);
                                  String description = input[1];
                                  return new DeadlineTask(date, description);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "floatingtask => <description>",
                          new SyntaxTermParser() {
                              @Override
                              public Object parse(String input) {
                                  return new FloatingTask(input);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "addcmd => 'add' <task>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  jim.journal.Task taskToAdd = p.parseTask(input[1].split(" "));
                                  return new AddCommand(taskToAdd);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "completecmd => 'complete' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.CompleteCommand(input[1]);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "removecmd => 'remove' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.RemoveCommand(input[1]);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "editcmd => 'edit' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  String description = input[1];
                                  EditCommand editCmd = new EditCommand(description);
                                  
                                  return editCmd;
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "searchcmd => 'search' <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new SearchCommand(input[1]);
                              }
                          });
        
        
        registerSyntaxParser(p,
		                  "displaycmd => 'display'",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.DisplayCommand();
                              }
                          });
        
        
        registerSyntaxParser(p,
		                  "displaycmd => 'display' <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                        (MutableDateTime) p.doParse("<date>", input[1]);

                                  return new jim.journal.DisplayCommand(date);
                              }
                          });
        
        
        registerSyntaxParser(p,
        		          "undocmd => 'undo'",
                          new SyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new UndoCommand();
                              }
                          });
        

    }



    /**
     * @param syntaxParserKey in format of "syntaxClassName => syntaxFormat"
     */
    private static void registerSyntaxParser(Parser p,
    		                                 String syntaxParserKey,
                                             SyntaxParser syntaxParser) {
        p.registerSyntaxParser(SyntaxParserKey.valueOf(syntaxParserKey), syntaxParser);
    }



    private static MutableDateTime parseDDMMYY(String date) {
        // TODO: Abstract Date parsing like AddCommand
        // ACCEPTED FORMATS: dd/mm/yy
        //SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_DATE_DDMMYY);
        
        int[] takeDateArray = splitDate(removeAllSymbols(date));
        int YY = takeDateArray[2], MM = takeDateArray[1], DD = takeDateArray[0];

        MutableDateTime result = new MutableDateTime(YY,MM,DD,0,0,0,0);
        /*result.setDateTime(dateFormat.parse(date));*/

        return result;
    }
    
    
    
    private static int parseDayOfMonth(String dayOfMonthStr) {
    	Pattern dayOfMonthRegex = Pattern.compile("(\\d?\\d)(st|nd|rd|th)?");
    	Matcher matcher = dayOfMonthRegex.matcher(dayOfMonthStr);
    	
    	if (matcher.matches()) {
    		return Integer.parseInt(matcher.group(1));
    	} else {
    		throw new IllegalArgumentException("Not in appropriate format: " + dayOfMonthStr);
    	}
    }
}
