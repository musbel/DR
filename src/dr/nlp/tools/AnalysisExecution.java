package dr.nlp.tools;

import java.util.ArrayList;
import java.util.List;

import dr.nlp.data.Document;
import dr.nlp.data.Word;

public class AnalysisExecution implements ExecutionUnit
{
	private String text;
	private Document doc;
	private SentenceAnalyser analyser;
	private Tokeniser tokeniser;

	public AnalysisExecution( String text, Document doc, SentenceAnalyser analyser, Tokeniser tokeniser )
	{
		this.text = text;
		this.doc = doc;
		this.analyser = analyser;
		this.tokeniser = tokeniser;
	}

	@Override
	public void execute()
	{
		String[] sentences = analyser.detectSentences( text );

		for ( String sentence : sentences )
		{
			String[] words = tokeniser.tokenise( sentence );
			List<Word> wordList = new ArrayList<Word>();

			for ( String word : words )
			{
				Word wordObj = new Word( word.toLowerCase() );
				wordList.add( wordObj );
			}

			doc.addSentence( wordList );
		}
	}

}
