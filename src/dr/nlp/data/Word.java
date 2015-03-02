package dr.nlp.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Word
{
	@XmlElement( nillable = false )
	private String name;

	@XmlElementWrapper( name = "entities", nillable = true )
	@XmlElement( name = "entity", nillable = true )
	private List<String> namedEntities;

	private int count;

	private Word() {}

	public Word( String name )
	{
		this.name = name;
		namedEntities = new ArrayList<String>();
		count = 1;
	}

	public String getName()
	{
		return name;
	}

	public void addNamedEntity( String entity )
	{
		if ( !namedEntities.contains( entity ) )
		{
			namedEntities.add( entity );
		}
	}

	public void addNamedEntities( List<String> entities )
	{
		for ( String entity : entities )
		{
			addNamedEntity( entity );
		}
	}

	public List<String> getNamedEntities()
	{
		return namedEntities;
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
