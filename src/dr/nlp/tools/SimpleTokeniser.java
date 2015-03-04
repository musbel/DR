package dr.nlp.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTokeniser implements Tokeniser
{
	private static String decAndAbbr = "[\\w$£]*\\.\\w+\\.?";
	private static String apostrophe = "(\\w+\\'\\w+)\\b";
	private static String dates = "\\d+/\\d+/\\d+\\b";
	private static String hyphened = "\\w+(?:[-–]\\w+)*";
	private static String wordsNoPunc = "(\\w+(?!\\.)";
	private static String anyWord = "\\w+";
	private static String regex = "\\b(" + decAndAbbr + "|" + apostrophe + "|" + dates + "|" + 
									hyphened + "|" + wordsNoPunc + "|" + anyWord + ")\\b)";
	private static String regexMatch = "\\b(" + decAndAbbr + "|" + apostrophe + "|" + dates + "|" + 
										wordsNoPunc + "|" + anyWord + ")\\b)";
	
	private static Pattern pattern;
	private static Pattern patternMatch;

	public SimpleTokeniser()
	{
		pattern = Pattern.compile( regex );
		patternMatch = Pattern.compile( regexMatch );
	}
	
	@Override
	public String[] tokenise( String sentence )
	{
		if ( sentence.isEmpty() )
		{
			return null;
		}

		List<String> tokens = new ArrayList<String>();

		Matcher matcher = pattern.matcher( sentence );

		while( matcher.find() )
		{
			String matchedToken = matcher.group();
			int loc = matchedToken.indexOf( "'" );
			if ( loc != -1 )
			{
				int tokenLength = matchedToken.length();
				String apostropheSuffix = matchedToken.substring( loc + 1, tokenLength );
				
				// Heuristic warning:
				// An naive assumption that 's represents conjugated nouns is made
				// where it is consequently removed.
				// Alternatively, it is assumed that this is a short form of e.g. isn't,
				// we'll, etc.
				if ( apostropheSuffix.equals( "s" ) )
				{
					tokens.add( matchedToken.substring( 0, loc ) );
				}
				else
				{
					if ( apostropheSuffix.equals( "t" ) )
					{
						// The word is split and the character before the apostrophe becomes 
						// a part of the second token.
						tokens.add( matchedToken.substring( 0, loc - 1 ) );
						tokens.add( matchedToken.substring( loc - 1, tokenLength ) );
					}
					else
					{
						tokens.add( matchedToken.substring( 0, loc ) );
						tokens.add( matchedToken.substring( loc, tokenLength ) );
					}
				}
			}
			else
			{
				tokens.add( matchedToken );
			}
		}

		return tokens.toArray( new String[tokens.size()] );
	}
	
	public boolean match( String text, String searchToken )
	{
		Matcher matcher = patternMatch.matcher( text.toLowerCase() );

		while( matcher.find() )
		{
			if ( matcher.group().equals( searchToken.toLowerCase() ) )
			{
				return true;
			}
		}
		
		return false;
	}
}
