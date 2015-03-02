package dr.nlp.data;

import javax.xml.bind.annotation.XmlElement;

public class Word
{
	@XmlElement( nillable = false )
	private String name;

	private int count;

	private Word() {}

	public Word( String name )
	{
		this.name = name;
		count = 1;
	}

	public String getName()
	{
		return name;
	}

	public void incrementCount()
	{
		count++;
	}

	@XmlElement( nillable = false )
	public int getCount()
	{
		return count;
	}
}
