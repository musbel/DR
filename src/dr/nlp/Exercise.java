package dr.nlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import dr.nlp.data.Document;
import dr.nlp.tools.AnalysisExecution;
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
	
	private void runExercise()
	{
		String contents;

		try
		{
			contents = new String( Files.readAllBytes( Paths.get( nlpDataFilePath ) ) );

			Document doc = new Document( nlpDataFilePath );
			SimpleAnalyser analyser = new SimpleAnalyser();
			SimpleTokeniser tokeniser = new SimpleTokeniser();	

			AnalysisExecution e = new AnalysisExecution( contents, doc, analyser, tokeniser );
			e.execute();

			doc.toXml();
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
