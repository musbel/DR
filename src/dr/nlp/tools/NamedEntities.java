package dr.nlp.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class NamedEntities
{
	private static NamedEntities instance = null;
	private static HashMap<String, Integer> entities;
	private static String entityFile;
	private static int maxEntityTokens;
	
	protected NamedEntities()
	{
		entities = new HashMap<String, Integer>();
		maxEntityTokens = 0;
	}
	
	private void loadEntities( String filename )
	{
		File file = new File( filename );
		Scanner sc;
		try
		{
			sc = new Scanner( file );

		    while ( sc.hasNextLine() ) 
		    {
		    	String entity = sc.nextLine();
		    	if ( entity.length() != 0 )
		    	{
		    		String[] tokens = entity.split( " " );
		    		int numTokens = tokens.length;
		    		maxEntityTokens = Math.max( maxEntityTokens, numTokens );
			    	entities.put( entity, new Integer( numTokens ) );
		    	}
		    }
		    
		    sc.close();
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}
	
	public int getNumEntities()
	{
		return entities.size();
	}
	
	public int getMaxEntityTokens()
	{
		return maxEntityTokens;
	}
	
	public boolean isNamedEntity( String entity )
	{
		return ( entities.get( entity ) != null );
	}
	
	public List<String> getNamedEntities( List<String> list )
	{
		List<String> entityList = new ArrayList<String>();
		
		for ( String entry : list )
		{
			if ( isNamedEntity( entry ) )
			{
				entityList.add( entry );
			}
		}
		
		return entityList;
	}
	
	public static NamedEntities getInstance( String filename )
	{
		if ( instance == null || entityFile != filename )
		{
			instance = new NamedEntities();
			instance.loadEntities( filename );
		}
		
		return instance;
	}
}
