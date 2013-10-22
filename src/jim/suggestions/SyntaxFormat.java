package jim.suggestions;

import static jim.util.StringUtils.join;



/**
 * Abstraction for sequences of terms.
 * e.g. Strings like:
 * "'add' <date> <time> <description>"
 */
class SyntaxFormat {
	private SyntaxTerm[] syntaxTerms;
	
	public SyntaxFormat(SyntaxTerm[] terms) {
		syntaxTerms = terms;
	}
	
	public SyntaxTerm[] getSyntaxTerms() {
		return syntaxTerms;
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
