package dr.nlp.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dr.nlp.data.Document;
import dr.nlp.data.Sentence;
import dr.nlp.data.Word;

public class TestDataStructures
{
	@Test
	public void testWord()
	{
		Word word = new Word( "dog" );
		word.incrementCount();
		word.incrementCount();

		assertEquals( 3, word.getCount() );
	}

	@Test
	public void testSentence()
	{
		Sentence s = new Sentence();
		s.addWord( "The" );
		s.addWord( "cat" );
		s.addWord( "spat" );
		s.addWord( "in" );
		s.addWord( "the" );
		s.addWord( "hat" );

		assertTrue( s.hasWords() );
		assertEquals( 6, s.getNumWords() );

		s.clear();
		assertFalse( s.hasWords() );
		assertEquals( 0, s.getNumWords() );
	}

	@Test
	public void testDocument() throws JAXBException
	{
		Document d = new Document( "test_nlp.txt" );

		ArrayList<Word> words = new ArrayList<Word>();
		words.add( new Word( "The" ) );
		words.add( new Word( "cat" ) );
		words.add( new Word( "spat" ) );
		words.add( new Word( "in" ) );
		words.add( new Word( "the" ) );
		words.add( new Word( "hat" ) );

		d.addSentence( words );

		assertEquals( 2, d.getNumberOfWordInstances( "the" ) );
	}
}
