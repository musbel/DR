package dr.nlp.tools;

public interface Tokeniser
{
	public String[] tokenise( String sentence );
	public boolean match( String text, String searchToken );
}
