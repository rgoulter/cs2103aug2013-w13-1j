package jim.suggestions;

import static jim.util.DateUtils.datetime;
import static jim.util.DateUtils.getCurrentYear;
import static jim.util.DateUtils.getMonthOfYearFromMonthName;
import static jim.util.DateUtils.getDayOfWeekFromDayName;
import static jim.util.StringUtils.unescape;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.MutableDateTime;

import jim.journal.AddCommand;
import jim.journal.DeadlineTask;
import jim.journal.EditCommand;
import jim.journal.FloatingTask;
import jim.journal.RedoCommand;
import jim.journal.SearchCommand;
import jim.journal.Task;
import jim.journal.TimedTask;
import jim.journal.UndoCommand;
import jim.suggestions.Parser.SyntaxTermSearchNode;
import static jim.util.DateUtils.REGEX_DATE_DDMMYY;
import static jim.util.DateUtils.REGEX_TIME_HHMM;
import static jim.util.DateUtils.isHourLikelyToBePM;
import static jim.util.DateUtils.ensureHourIsPM;
import static jim.util.StringUtils.removeAllSymbols;
import static jim.util.StringUtils.splitDate;

//@author A0088816N
public class SyntaxParsers {



    protected static abstract class SyntaxParser {
        public abstract Object parse(SyntaxTermSearchNode[] input);
    }
    
    protected static abstract class SimpleSyntaxParser extends SyntaxParser {
        public Object parse(SyntaxTermSearchNode[] input) {
        	String[] strInput = new String[input.length];
        	
        	for(int i = 0; i < input.length; i++) { strInput[i] = input[i].getMatchedInput(); }
        	
        	return parse(strInput);
        }
        
        public abstract Object parse(String[] input);
    }

    protected static abstract class SyntaxTermParser extends SimpleSyntaxParser {
        public abstract Object parse(String inputTerm);
        
        @Override
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



	/*
	 * NOTE: initSyntaxParsers Authored by various group members. 
	 */
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
                "yyyymmdd => /(\\d\\d\\d\\d)[/-]?(\\d\\d)[/-]?(\\d\\d)/",
				 new SimpleSyntaxParser() {
			 		 @Override
					 public Object parse(String[] inputTerm) {
			 			 Matcher m = Pattern.compile("(\\d\\d\\d\\d)[/-]?(\\d\\d)[/-]?(\\d\\d)").matcher(inputTerm[0]);
			 			 if(!m.matches()) { throw new IllegalArgumentException(); };

			 			 int year = Integer.parseInt(m.group(1));
			 			 int monthOfYear = Integer.parseInt(m.group(2));
			 			 int dayOfMonth = Integer.parseInt(m.group(3));
						 return new MutableDateTime(year,
								 					monthOfYear,
								 					dayOfMonth,
								 					0,
								 					0,
								 					0,
								 					0);
					 }
				 });

        registerSyntaxParser(p,
                             "monthday => /\\d\\d/\\d\\d/",
							 new SimpleSyntaxParser() {
						 		 @Override
								 public Object parse(String[] inputTerm) {
						 			 String[] parts = inputTerm[0].split("/");
						 			 int dayOfMonth = Integer.parseInt(parts[0]);
						 			 int monthOfYear = Integer.parseInt(parts[1]);
									 return new MutableDateTime(getCurrentYear(),
											 					monthOfYear,
											 					dayOfMonth,
											 					0,
											 					0,
											 					0,
											 					0);
								 }
							 });

        registerSyntaxParser(p,
                             "monthday => <monthname> <dayofmonth>",
							 new SimpleSyntaxParser() {
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
			                 "monthday => <dayofmonth> <monthname>",
							  new SimpleSyntaxParser() {
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
                             "constantRelativeDates => 'yesterday'",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      today.addDays(-1);
                                      return today;
                                  }
                              });
        
        registerSyntaxParser(p,
                             "constantRelativeDates => 'today'",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      return today;
                                  }
                              });
        
        registerSyntaxParser(p,
                             "constantRelativeDates => 'tomorrow'",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      today.addDays(1);
                                      return today;
                                  }
                              });
        
        registerSyntaxParser(p,
                             "relativeDays => <dayOfWeek>",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      today.setDayOfWeek(getDayOfWeekFromDayName(inputTerm[0]));
                                      return today;
                                  }
                              });
        
        registerSyntaxParser(p,
                             "relativeDays => 'this' <dayOfWeek>",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      today.setDayOfWeek(getDayOfWeekFromDayName(inputTerm[1]));
                                      return today;
                                  }
                              });
        
        registerSyntaxParser(p,
                             "relativeDays => 'next' <dayOfWeek>",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      today.setDayOfWeek(getDayOfWeekFromDayName(inputTerm[1]));
                                      today.addDays(7);
                                      
                                      return today;
                                  }
                              });
        
        registerSyntaxParser(p,
                             "relativeDays => 'last' <dayOfWeek>",
                              new SimpleSyntaxParser() {
                                  @Override
                                  public Object parse(String[] inputTerm) {
                                      MutableDateTime today = new MutableDateTime();
                                      today.setTime(0,0,0,0);
                                      today.setDayOfWeek(getDayOfWeekFromDayName(inputTerm[1]));
                                      today.addDays(-7);
                                      
                                      return today;
                                  }
                              });

        registerSyntaxParser(p,
                          "hhmm => /(\\d\\d):?(\\d\\d)[Hh]/",
                          new SyntaxTermParser() {

                              @Override
                              public Object parse(String inputTerm) {
         			 			 Matcher m = Pattern.compile("(\\d\\d):?(\\d\\d)[Hh]").matcher(inputTerm);
        			 			 if(!m.matches()) { throw new IllegalArgumentException(); };

        			 			 int hh = Integer.parseInt(m.group(1));
        			 			 int mm = Integer.parseInt(m.group(2));
                                  
                                  return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
                              }
                          });
        registerSyntaxParser(p,
			                 "hhmm => /(\\d?\\d):?(\\d\\d)/",
			                 new SyntaxTermParser() {
			                     @Override
			                     public Object parse(String inputTerm) {
						 		 	   Matcher m = Pattern.compile("(\\d?\\d):?(\\d\\d)").matcher(inputTerm);
						 			   if(!m.matches()) { throw new IllegalArgumentException(); };
			
						 			   int hh = parseHourOfDay(m.group(1));
						 			   int mm = Integer.parseInt(m.group(2));
			                        
			                           return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
			                     }
			                 });

        registerSyntaxParser(p,
			                 "ampmtime => /(\\d?\\d)([AaPp])[Mm]?/",
			                 new SyntaxTermParser() {
			                     @Override
			                     public Object parse(String inputTerm) {
									Matcher m = Pattern.compile("(\\d?\\d)([AaPp])[Mm]?").matcher(inputTerm);
									if(!m.matches()) { throw new IllegalArgumentException(); };
									
									int hh = Integer.parseInt(m.group(1));
									int mm = 0;
									boolean isAm = m.group(2).toLowerCase().charAt(0) == 'a';
											  
									hh = (hh % 12) + (isAm ? 0 : 12);
											  
									return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
			                     }
			                 });
        registerSyntaxParser(p,
			                 "ampmtime => /(\\d?\\d):?(\\d\\d)([AaPp])[Mm]?/",
			                 new SyntaxTermParser() {
			                     @Override
			                     public Object parse(String inputTerm) {
									 Matcher m = Pattern.compile("(\\d?\\d):?(\\d\\d)([AaPp])[Mm]?").matcher(inputTerm);
									 if(!m.matches()) { throw new IllegalArgumentException(); };
										
									 int hh = Integer.parseInt(m.group(1));
									 int mm = Integer.parseInt(m.group(2));
									 boolean isAm = m.group(3).toLowerCase().charAt(0) == 'a';
											  
									 hh = (hh % 12) + (isAm ? 0 : 12);
									   
									 return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
			                     }
			                 });
        registerSyntaxParser(p,
							 "ampmtime => /(\\d?\\d):?(\\d\\d)/ /([AaPp])[Mm]?/",
							 new SimpleSyntaxParser() {
							     @Override
							     public Object parse(String[] inputTerms) {
									  Matcher m1 = Pattern.compile("(\\d?\\d):?(\\d\\d)").matcher(inputTerms[0]);
									  if(!m1.matches()) { throw new IllegalArgumentException(); };
									  Matcher m2 = Pattern.compile("([AaPp])[Mm]?").matcher(inputTerms[1]);
									  if(!m2.matches()) { throw new IllegalArgumentException(); };
										
									  int hh = Integer.parseInt(m1.group(1));
									  int mm = Integer.parseInt(m1.group(2));
									  boolean isAm = m2.group(1).toLowerCase().charAt(0) == 'a';
											  
									  hh = (hh % 12) + (isAm ? 0 : 12);
									   
									  return new MutableDateTime(0, 1, 1, hh, mm, 00, 00);
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
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime startDate =
                                          (MutableDateTime) p.doParse(input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) p.doParse(input[1]);
                                  MutableDateTime endDate =
                                          (MutableDateTime) p.doParse(input[2]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) p.doParse(input[3]);
                                  String description = input[4].getMatchedInput();
                                  return new TimedTask(datetime(startDate, startTime),
                                                       datetime(endDate, endTime), unescape(description));
                              }
                          });
        registerSyntaxParser(p,
                "timedtask => <description> <date> <time> <date> <time>",
                new SyntaxParser() {
                    @Override
                    public Object parse(SyntaxTermSearchNode[] input) {
                        String description = input[0].getMatchedInput();
                        MutableDateTime startDate =
                                (MutableDateTime) p.doParse(input[1]);
                        MutableDateTime startTime =
                                (MutableDateTime) p.doParse(input[2]);
                        MutableDateTime endDate =
                                (MutableDateTime) p.doParse(input[3]);
                        MutableDateTime endTime =
                                (MutableDateTime) p.doParse(input[4]);
                        return new TimedTask(datetime(startDate, startTime),
                                             datetime(endDate, endTime), unescape(description));
                    }
                });
        registerSyntaxParser(p,
                          "timedtask => <date> <time> 'to' <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) p.doParse(input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) p.doParse(input[3]);
                                  String description = input[4].getMatchedInput();
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       unescape(description));
                              }
                          });
        registerSyntaxParser(p,
                          "timedtask => <date> <time> <time> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[0]);
                                  MutableDateTime startTime =
                                          (MutableDateTime) p.doParse(input[1]);
                                  MutableDateTime endTime =
                                          (MutableDateTime) p.doParse(input[2]);
                                  String description = input[3].getMatchedInput();
                                  return new TimedTask(datetime(date, startTime),
                                                       datetime(date, endTime),
                                                       unescape(description));
                              }
                          });
        registerSyntaxParser(p,
                          "deadlinetask => <date> <description>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[0]);
                                  String description = input[1].getMatchedInput();
                                  return new DeadlineTask(date, unescape(description));
                              }
                          });
        registerSyntaxParser(p,
                             "deadlinetask => <date> <time> <description>",
                             new SyntaxParser() {
                                 @Override
                                 public Object parse(SyntaxTermSearchNode[] input) {
                                     MutableDateTime date =
                                             (MutableDateTime) p.doParse(input[0]);
                                     MutableDateTime time =
                                             (MutableDateTime) p.doParse(input[1]);
                                     String description = input[2].getMatchedInput();
                                     return new DeadlineTask(datetime(date, time),
                                                             unescape(description));
                                 }
                             });
        
        registerSyntaxParser(p,
                          "floatingtask => <description>",
                          new SyntaxTermParser() {
                              @Override
                              public Object parse(String input) {
                                  return new FloatingTask(unescape(input));
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "addcmd => <addword> <task>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  Task taskToAdd =
                                		  (Task) p.doParse(input[1]);
                                  return new AddCommand(taskToAdd);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "completecmd => <completeword> <description>",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.CompleteCommand(unescape(input[1]));
                              }
                          });
        
        registerSyntaxParser(p,
                          "completecmd => <completeword> <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[1]);
                                  return new jim.journal.CompleteCommand(date);
                              }
                          });
        
        registerSyntaxParser(p,
                "uncompletecmd => <uncompleteword> <description>",
                new SimpleSyntaxParser() {
                    @Override
                    public Object parse(String[] input) {
                        return new jim.journal.UncompleteCommand(unescape(input[1]));
                    }
                });
        
        registerSyntaxParser(p,
                "uncompletecmd => <uncompleteword> <date>",
                new SyntaxParser() {
                    @Override
                    public Object parse(SyntaxTermSearchNode[] input) {
                        MutableDateTime date =
                                (MutableDateTime) p.doParse(input[1]);
                        return new jim.journal.UncompleteCommand(date);
                    }
                });
        
        registerSyntaxParser(p,
                          "removecmd => <removeword> <description>",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.RemoveCommand(unescape(input[1]));
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "removecmd => <removeword> <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[1]);
                                  return new jim.journal.RemoveCommand(date);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "editcmd => <editword> <description>",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  String description = input[1];
                                  EditCommand editCmd = new EditCommand(unescape(description));
                                  
                                  return editCmd;
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "editcmd => <editword> <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[1]);
                                  return new jim.journal.EditCommand(date);
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "searchcmd => <searchword> <word>",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new SearchCommand(unescape(input[1]));
                              }
                          });
        
        
        registerSyntaxParser(p,
                          "searchcmd => <searchword> <date>",
                          new SyntaxParser() {
                              @Override
                              public Object parse(SyntaxTermSearchNode[] input) {
                                  MutableDateTime date =
                                          (MutableDateTime) p.doParse(input[1]);
                                  return new jim.journal.SearchCommand(date);
                              }
                          });
        
        
        registerSyntaxParser(p,
		                  "displaycmd => <displayword>",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new jim.journal.DisplayCommand();
                              }
                          });
        
        
        registerSyntaxParser(p,
		                  "displaycmd => <displayword> <date>",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  MutableDateTime date =
                                        (MutableDateTime) p.doParse("<date>", unescape(input[1]));

                                  return new jim.journal.DisplayCommand(date);
                              }
                          });
        
        
        registerSyntaxParser(p,
        		          "undocmd => 'undo'",
                          new SimpleSyntaxParser() {
                              @Override
                              public Object parse(String[] input) {
                                  return new UndoCommand();
                              }
                          });
        registerSyntaxParser(p,
				          "redocmd => 'redo'",
		                new SimpleSyntaxParser() {
		                    @Override
		                    public Object parse(String[] input) {
		                        return new RedoCommand();
		                    }
		                });
        
        registerSyntaxParser(p,
                             "configcmd => <configword>",
                             new SimpleSyntaxParser() {
                                 @Override
                                 public Object parse(String[] input) {
                                     return new jim.ConfigCommand();
                                 }
                             });
        
        registerSyntaxParser(p,
                             "configcmd => <configword> <description> <description>",
                             new SimpleSyntaxParser() {
                                 @Override
                                 public Object parse(String[] input) {
                                     return new jim.ConfigCommand(input[1], input[2]);
                                 }
                             });
        
        registerSyntaxParser(p,
                             "configcmd => <configword> <description>",
                             new SimpleSyntaxParser() {
                                 @Override
                                 public Object parse(String[] input) {
                                     return new jim.ConfigCommand(input[1]);
                                 }
                             });
        
        registerSyntaxParser(p,
                             "helpcmd => <helpword>",
                             new SimpleSyntaxParser() {
                                 @Override
                                 public Object parse(String[] input) {
                                     return new jim.HelpCommand("");
                                 }
                             });
        
        registerSyntaxParser(p,
                             "helpcmd => <helpword> <description>",
                             new SimpleSyntaxParser() {
                                 @Override
                                 public Object parse(String[] input) {
                                     return new jim.HelpCommand(input[1]);
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
        // ACCEPTED FORMATS: dd/mm/yy
        
        int[] takeDateArray = splitDate(removeAllSymbols(date));
        int YY = takeDateArray[2], MM = takeDateArray[1], DD = takeDateArray[0];

        MutableDateTime result = new MutableDateTime(YY,MM,DD,0,0,0,0);

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
    
    
    
    /**
     * e.g. 
     * 06 -> Likely to be 24 hour time, => 6 am.
     *  6 -> More likely to refer to 6pm than 6am.
     * @return Returns a sensible interpretation of String as hour of day.
     */
    private static int parseHourOfDay(String hourStr) {
    	int hour = Integer.parseInt(hourStr);
    	
    	if(!hourStr.startsWith("0") && isHourLikelyToBePM(hour)){
    		return ensureHourIsPM(hour);
    	} else {
    		return hour;
    	}
    }
}
