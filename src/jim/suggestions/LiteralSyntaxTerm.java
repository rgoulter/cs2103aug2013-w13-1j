package jim.suggestions;

class LiteralSyntaxTerm extends SyntaxTerm {
    private String literalValue;

    /**
     * The "escaped" value. e.g. "'a'" -> LiteralSyntaxTerm("a").
     */
    public LiteralSyntaxTerm(String value) {
        literalValue = value;
    }

    @Override
    public boolean matches(String inputTerm) {
        return literalValue.equals(inputTerm);
    }

    @Override
    public boolean isDisplayable() {
        return true;
    }

    @Override
    public SuggestionHint generate(GenerationContext context, double t) {
    	assert context != null;
        return new SuggestionHint(new String[]{literalValue},
                                  context.getInputSubsequence(),
                                  new SyntaxTerm[]{this});
    }
    
    public String getLiteralValue() {
    	return literalValue;
    }

    @Override
    public String toString() {
    	return "'" + literalValue + "'";
    }
}
