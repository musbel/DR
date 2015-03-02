package dr.nlp.tools;

import java.util.ArrayList;
import java.util.List;

public class NGramHandler
{
	public static List<String> getNGrams( String[] words, int n )
	{
		List<String> ngrams = new ArrayList<String>();

		for( int i = 0; i < words.length - n + 1; i++ )
		{
			ngrams.add( concat( words, i, i + n ) );
		}
		
		return ngrams;
	}
	
	public static List<String> getAllNGrams( String[] words, int n )
	{
		List<String> ngrams = new ArrayList<String>();
		
		for ( int i = 1; i <= n; ++i )
		{
			for ( String ngram : getNGrams( words, i ) )
			{
				ngrams.add( ngram );
			}
		}
		
		return ngrams;
	}

	public static String concat( String[] words, int start, int end )
	{
		StringBuilder sb = new StringBuilder();
		
		for( int i = start; i < end; i++ )
		{
			sb.append( ( i > start ? " " : "" ) + words[i] );
		}
		
		return sb.toString();
	}
}
