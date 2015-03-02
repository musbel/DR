package dr.nlp.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import dr.nlp.tools.SimpleAnalyser;
import dr.nlp.tools.SimpleTokeniser;

public class TestTools
{
	private static String nlpDataFilePath = "nlp_data.txt";

	@Test
	public void testSimpleAnalyser() throws IOException
	{
		String contents = new String( Files.readAllBytes( Paths.get( nlpDataFilePath ) ) );
		SimpleAnalyser analyser = new SimpleAnalyser();
		String[] sentences = analyser.detectSentences( contents );

		assertEquals( 5, sentences.length );
	}

	@Test
	public void testTokeniser()
	{
		String testString = "'We'll have to see', said John's dog to the mistress's goat-likeness and "
				+ "they'd reckon that there isn't a 5cm bone in sight, e.g. on the 21/02/2004 containing "
				+ "%2.5f. units of hard grafting tuna";

		SimpleTokeniser tokeniser = new SimpleTokeniser();
		String[] tokens = tokeniser.tokenise( testString );

		System.out.println( tokens.length );
		for ( String token : tokens )
		{
			System.out.println( "> " + token );
		}
	}

}
