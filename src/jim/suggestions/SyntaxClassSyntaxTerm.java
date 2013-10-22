package jim.suggestions;

class SyntaxClassSyntaxTerm extends SyntaxTerm {
	private String syntaxClassName;
	
    public SyntaxClassSyntaxTerm(String className) {
    	syntaxClassName = className;
    }

    @Override
    public boolean matches(String inputTerm) {
        throw new UnsupportedOperationException("Don't match against syntax class.");
    }

    @Override
    public boolean isDisplayable() {
        return "date time description".contains(syntaxClassName);
    }

    @Override
    public String generate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String toString() {
    	return "<" + syntaxClassName + ">";
    }
    
    public String getSyntaxClassName() {
    	return syntaxClassName;
    }
}
