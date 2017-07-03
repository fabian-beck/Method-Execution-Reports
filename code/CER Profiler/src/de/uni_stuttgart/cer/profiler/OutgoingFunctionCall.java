// Declare package
package de.uni_stuttgart.cer.profiler;

// Outgoing function call information
public class OutgoingFunctionCall
{
	// Variables
	public static int counterID = 1;
	public int id;
	public FunctionName functionName;
	
	// Create outgoing function call
	OutgoingFunctionCall(FunctionName functionName, boolean generateID)
	{
		this.functionName = functionName;
		if(generateID)
		{
			id = counterID;
			counterID++;
		}
		else
		{
			id = 0;
		}
	}
}
