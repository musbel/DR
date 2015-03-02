package dr.nlp.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import dr.nlp.tools.NGramHandler;
import dr.nlp.tools.NamedEntities;
import dr.nlp.tools.SimpleAnalyser;
import dr.nlp.tools.SimpleTokeniser;

public class TestTools
{
	private static String nlpDataFilePath = "nlp_data.txt";
	private static String entityFilename  = "NER.txt";

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

		assertEquals( 36, tokens.length );
	}

	@Test
	public void ngramTest()
	{
		String sentence = "I just saw the Prince of Wales today.";
		SimpleTokeniser tokeniser = new SimpleTokeniser();
		String[] words = tokeniser.tokenise( sentence );
		
		List<String> ngrams = NGramHandler.getAllNGrams( words, 3 );		
		boolean found = false;

		for ( String ngram : ngrams )
		{
			if ( ngram.equals( "Prince of Wales" ) )
			{
				found = true;
			}
		}

		assertTrue( found );
	}

	@Test
	public void entityTest()
	{
		NamedEntities entities = NamedEntities.getInstance( entityFilename );
		
		assertEquals( 45, entities.getNumEntities() );
		assertEquals( 3, entities.getMaxEntityTokens() );
		assertTrue( entities.isNamedEntity( "Sun" ) );
	}
}
