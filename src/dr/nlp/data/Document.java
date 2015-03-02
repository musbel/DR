package dr.nlp.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class Document
{
	@XmlAttribute()
	private String name;

	@XmlElementWrapper( name = "sentences" )
	@XmlElement( name = "sentence" )
	private ArrayList<Sentence> sentences;

	@XmlElement()
	private HashMap<String, Word> wordMap;

	private Document() {}	// A workaround when marshalling

	public Document( String name )
	{
		this.name = name;
		sentences = new ArrayList<Sentence>();
		wordMap = new HashMap<String, Word>();
	}

	public void addSentence( List<Word> words )
	{
		Sentence sentence = new Sentence();

		for ( Word word : words )
		{
			String name = word.getName().toLowerCase();
			
			// Add an entry to the word map
			if ( wordMap.containsKey( name ) )
			{
				Word existingWord = wordMap.get( name );
				existingWord.incrementCount();
			}
			else
			{
				wordMap.put( name, word );
			}

			// Add the word to the new sentence
			sentence.addWord( name );
		}

		if ( sentence.hasWords() )
		{
			sentences.add( sentence );
		}
	}
	
	public ArrayList<Sentence> getSentences()
	{
		return sentences;
	}

	public int getNumberOfWordInstances( String word )
	{
		word = word.toLowerCase();
		if ( wordMap.containsKey( word ) )
		{
			return wordMap.get( word ).getCount();
		}

		return 0;
	}

	public void clear()
	{
		wordMap.clear();

		for ( Sentence sentence : sentences )
		{
			sentence.clear();
		}
	}

	public void toXml() throws JAXBException
	{
		JAXBContext ctx = JAXBContext.newInstance( Document.class );
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		marshaller.marshal( this, System.out );
		
		// Output XML document to a file
		File outputFile = new File( "Document.xml" );
		marshaller.marshal( this, outputFile );
	}
}
