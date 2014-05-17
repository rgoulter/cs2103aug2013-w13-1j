package jim.suggestions;

import static jim.util.StringUtils.join;



/**
 * Abstraction for sequences of terms.
 * e.g. Strings like:
 * "'add' <date> <time> <description>"
 */
//@author A0088816N
class SyntaxFormat {
	private SyntaxTerm[] syntaxTerms;
	
	public SyntaxFormat(SyntaxTerm[] terms) {
		syntaxTerms = terms;
	}
	
	public SyntaxTerm[] getSyntaxTerms() {
		return syntaxTerms;
	}

    public SuggestionHint generate(GenerationContext context, double t) {		
		SuggestionHint generatedHint = syntaxTerms[0].generate(context, t);
		
		for (int i = 1; i < syntaxTerms.length; i++) {
			context.setCurrentGeneratedHint(generatedHint);
			SuggestionHint tmpHint = syntaxTerms[i].generate(context, t);
			generatedHint = SuggestionHint.combine(generatedHint, tmpHint);
		}
		
		return generatedHint;
    }
	
	@Override
	public String toString() {
		String[] syntaxTermStrings = new String[syntaxTerms.length];
		
		for (int i = 0; i < syntaxTermStrings.length; i++) {
			syntaxTermStrings[i] = syntaxTerms[i].toString();
		}
		
		return join(syntaxTermStrings, ' ');
	}
	
	public static SyntaxFormat valueOf(String formatStr) {
		return valueOf(formatStr.split(" "));
	}
	
	public static SyntaxFormat valueOf(String[] syntaxTermStrings) {
		SyntaxTerm[] syntaxTerms = new SyntaxTerm[syntaxTermStrings.length];
		
		for (int i = 0; i < syntaxTermStrings.length; i++) {
			assert syntaxTermStrings[i].length() > 0 : "SyntaxFormat given syntax term is zero length";
			syntaxTerms[i] = SyntaxTerm.valueOf(syntaxTermStrings[i]);
		}
		
		return new SyntaxFormat(syntaxTerms);
	}
}
