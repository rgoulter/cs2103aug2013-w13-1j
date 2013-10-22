package jim.suggestions;

import static jim.util.StringUtils.isStringSurroundedBy;
import static jim.util.StringUtils.stripStringPrefixSuffix;

abstract class SyntaxTerm {
    public abstract boolean matches(String s);

    public boolean isDisplayable() {
        return false;
    }

    public String generate() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public static SyntaxTerm valueOf(String syntaxTerm) {
    	assert syntaxTerm.length() >= 3 : "Given SyntaxTerm: " + syntaxTerm;
        String strippedTerm = stripStringPrefixSuffix(syntaxTerm, 1);

        if (isSyntaxLiteral(syntaxTerm)) {
            return new LiteralSyntaxTerm(strippedTerm);
        } else if (isSyntaxRegex(syntaxTerm)) {
            return new RegexSyntaxTerm(strippedTerm);
        } else if(isSyntaxClass(syntaxTerm)) {
            return new SyntaxClassSyntaxTerm(strippedTerm);
        } else {
            throw new IllegalStateException("An invalid syntax was given: " +
                                            syntaxTerm);
        }
    }



    /**
     * Helper method for parsing Syntax.
     * Returns true if the given String is surrounded by '<' and '>'
     */
    private static boolean isSyntaxClass(String s) {
        // TODO: Eliminate magic values.
        return isStringSurroundedBy(s, '<', '>') && !s.contains(" ");
    }



    private static boolean isSyntaxRegex(String s){
        return isStringSurroundedBy(s, '/', '/');
    }



    private static boolean isSyntaxLiteral(String s){
        return isStringSurroundedBy(s, '\'', '\'');
    }
}
