package jim.suggestions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexSyntaxTerm extends SyntaxTerm {
    private Pattern regexPattern;

    public RegexSyntaxTerm(String regexStr) {
        regexPattern = Pattern.compile(regexStr);
    }

    @Override
    public boolean matches(String inputTerm) {
        Matcher regexMatcher = regexPattern.matcher(inputTerm);

        return regexMatcher.matches();
    }

    @Override
    public String toString() {
    	return "/" + regexPattern.toString() + "/";
    }
}
