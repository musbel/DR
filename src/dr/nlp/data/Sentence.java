package dr.nlp.data;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Sentence
{
	@XmlElementWrapper( name = "words" )
	@XmlElement( name = "word" )
	public ArrayList<String> words;
	
	public Sentence()
	{
		words = new ArrayList<String>();
	}
	
	public void addWord( String word )
	{
		words.add( word );
	}
	
	public void addWords( ArrayList<String> words )
	{
		this.words = words;
	}
	
	public ArrayList<String> getWords()
	{
		return words;
	}
	
	public int getNumWords()
	{
		return words.size();
	}

	public boolean hasWords()
	{
		return ( getNumWords() > 0 );
	}
	
	public void clear()
	{
		words.clear();
	}
}
