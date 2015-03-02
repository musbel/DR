package dr.nlp.tools;

import dr.nlp.tools.ExecutionUnit;

public class AnalyserThread implements Runnable
{
	ExecutionUnit execution;
	
	public AnalyserThread( ExecutionUnit execution )
	{
		this.execution = execution;
	}

	@Override
	public void run()
	{
		execution.execute();
	}

}
