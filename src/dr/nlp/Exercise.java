package dr.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;

import dr.nlp.data.Document;
import dr.nlp.tools.AnalyserThread;
import dr.nlp.tools.AnalysisExecution;
import dr.nlp.tools.NamedEntities;
import dr.nlp.tools.SimpleAnalyser;
import dr.nlp.tools.SimpleTokeniser;

/** SUMMARY
 * As much as I would have liked to use a ML model to perform the boundary detection
 * and tokenisation, I had to use the limited time I had and go for a simple regex
 * model. I use interfaces here to make it easier to add new model, features, and
 * behaviours. With this I aim to encapsulate and abstract some of the processes.
 * Furthermore, this allows taking advantage of design patterns such as visitor,
 * strategy, factory pattern, etc.
 * 
 * Initially I was using custom readers which could be passed to objects, where they
 * could abstract different read methods, such as whole document, text chunks, and
 * zip files. However, to make the exercise less obfuscated and simpler, I opted for
 * implementing that in this main exercise class.
 */

/** ASSUMPTIONS, LIMITATIONS AND DESIGN CONSIDERATIONS
 * - English language only. Some languages don't necessarily seperate words using a
 *   whitespace, and common things like dates differ between languages (and even
 *   counties that use the same language).
 * - Sentences are a part of a document object which contains a hash map of words
 *   for that document. Aggregating documents is treated as a separate process which
 *   is not done here.
 * - Hyphened words are treated as a single word although in some cases, they could
 *   arguably be treated as separate words, particularly if they are long.
 * - Text within quotes is never treated as a single entity even though it might
 *   conceptually make sense in some cases to do so.
 * - Tagged words like isn't, haven't, they'll, and we'd are separated using the '
 *   to is n't, have n't, they 'll, and we 'd.
 * - Separation of responsibilities: The data processing and the data model is kept
 *   separate here, where the tokeniser and sentence analyser only use string objects,
 *   and the actual data is built afterwards.
 * - No known list of abbreviation is used which means punctuations at the end of
 *   words like 'Inc.' will be considered a sentence boundary.
 * - Boundary issues not covered: 
 *     - Names such as Yahoo!, E! Entertainment, etc.
 *     - e-mail and internet addresses
 *     - Commas after a quote containing a punctuation does not prevent a sentence
 *       boundary, e.g. "Don't drop it!", said his mother.
 *       Punctuation used for emphasis, surprise, etc. Tomorrow?! What time??
 * - After pondering on how to represent the NER data, I ended up going for a basic
 *   hash table, and create N-grams based on the maximum number of entities in the
 *   NER container. I then compare the N-grams while processing the sentences, and
 *   tag the words in the sentence with the named entity for reference. I was also
 *   thinking about creating a more elaborate NER container, where the hash table
 *   would be constructed using every token in the named entities, where duplicates
 *   are represented in e.g. a linked list. Then instead of N-grams, I check each
 *   processed word for an entry in the hash table. The possible candidates are then
 *   compared against subsequent words in the sentence until a match is found. This
 *   seemed like just as much work as using N-grams, and having a more complicated
 *   NER structure as opposed to N-gram processing might be less attractive once
 *   the number of named entities grows.
 *
 * [Threading strategy thoughts]
 * For maximum efficiency, each thread should work on a part of a document and move
 * onto a different document when it's free, as opposed to having one thread working
 * on a single document. That avoids having one thread grafting hard on a really long
 * document, while other threads have finished their work and are just sat their idling.
 * However, without serialisation using e.g. condition variable, the order in which 
 * sentences are added to the documents is not guaranteed to be correct.
 * 
 * For simplicity, this implementation makes threads responsible for a whole document,
 * and all the document is read in at once (which is not ideal for very large documents).
 * 
 * I tested having the thread spawned within the scanner read process instead of reading
 * the whole document before passing the text to a document, and it works quite well, 
 * albeit the sentence order is obviously not necessarily always correct with respect to
 * the document. 
 * 
 * Using a document reader which can read chunks of the document on demand
 * is preferable for really large documents, although care must be taken to make sure
 * sentence boundaries are not spread across different chunks, and that objects pulling
 * data don't contain any scheduling logic.
 */
 
/** POSSIBLE IMPROVEMENTS
 *  Hand crafting the classification rules is time consuming and inaccurate. Regular
 *  expressions and heuristics need to be maintained to cover ever growing number of
 *  edge cases which leads to brittle and obfuscated code. Slight variations can
 *  easily result in an inaccurate or incorrect (or completely missed) analysis.
 *  
 *  It would be better to e.g. train a model on corpus annotated with sentence boundaries.
 *  Other ways to improve the system is to use lexicon with part-of-speech probability,
 *  decision trees where the boundaries are determined by maximising the entropy gain,
 *  or HMM for sentence and word boundary detection, where they are treated as hidden states.
 * 
 * @author orn
 *
 */
public class Exercise
{
	private static String nlpDataFilePath = "nlp_data.txt";
	private static String entityFilename  = "NER.txt";
	private static String zipPath         = "nlp_data.zip";
	
	private void runExercise()
	{
		ExecutorService executor = Executors.newFixedThreadPool( 8 );

		SimpleAnalyser analyser = new SimpleAnalyser();
		SimpleTokeniser tokeniser = new SimpleTokeniser();	
		NamedEntities entities = NamedEntities.getInstance( entityFilename );

		List<Document> docs = new ArrayList<Document>();

		try
		{
			File zip = new File( zipPath );
			ZipInputStream zin = new ZipInputStream( new FileInputStream( zip ) );

			for ( ZipEntry zipEntry; ( zipEntry = zin.getNextEntry() ) != null; )
			{
				String entryName = zipEntry.getName();

				if ( !entryName.endsWith( ".txt" ) || entryName.startsWith( "_" ) )
				{
					continue;
				}

				Document doc = new Document( entryName );
				Scanner scanner = new Scanner( zin );

				StringBuffer docBuffer = new StringBuffer();
				while ( scanner.hasNextLine() )
				{
					String line = scanner.nextLine();
					if ( !line.isEmpty() )
					{
						docBuffer.append( line );
					}
				}

				AnalysisExecution model = new AnalysisExecution( docBuffer.toString(), doc, analyser, tokeniser, entities );
				Runnable worker = new AnalyserThread( model );
				executor.execute( worker );

				docs.add( doc );
			}

			executor.shutdown();
			while( !executor.isTerminated() )
			{
			}

			zin.close();

			for ( Document d : docs )
			{
				d.toXml();
			}
		}
		catch( IOException e1 )
		{
			System.out.println( "Unable to load file: " + e1.getMessage() );
		}
		catch( JAXBException e2 )
		{
			System.out.println( "Unable to print XML document: " + e2.getMessage() );
		}
	}

	public static void main( String[] args )
	{
		Exercise exercise = new Exercise();
		exercise.runExercise();
	}

}
