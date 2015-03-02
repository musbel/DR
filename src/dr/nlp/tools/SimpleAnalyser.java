package dr.nlp.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleAnalyser implements SentenceAnalyser
{
	private static String regexBoundary = "\\.+((?=[\"\\]\\}\\)\\'\\s]))|\\.\\z|[!?]+|\\z";

	@Override
	public String[] detectSentences( String text )
	{
		if ( text.isEmpty() )
		{
			return null;
		}

		List<String> sentences = new ArrayList<String>();

		Pattern pattern = Pattern.compile( regexBoundary );
		Matcher matcher = pattern.matcher( text );

		int prevEnd = 0;
		while ( matcher.find() )
		{
			String matchedPattern = matcher.group();

			if( matchedPattern.length() > 1 )
			{
				continue;
			}

			// Extract sentence from the last place we ended a sentence to the location prior
			// to where a match was found
			String sentence = text.substring( prevEnd, matcher.start() );

			// Remove unwanted newlines and leading/trailing whitespace
			sentence = sentence.replaceAll( "\\n+|^\\s|\\s$", "" );
			if ( sentence.isEmpty() )
			{
				continue;
			}

			sentences.add( sentence );

			prevEnd = matcher.end();
		}

		// Add any remaining text as a sentence. This can happen if e.g. the last 
		if ( prevEnd < text.length() )
		{
			String line = text.substring( prevEnd, text.length() );
			line = line.replaceAll( "\\n+|^\\s|\\s$", "" );
			System.out.println( line );
		}

		return sentences.toArray( new String[sentences.size()] );
	}

}
