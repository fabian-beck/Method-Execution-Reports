// Declare package
package de.uni_stuttgart.cer.profiler;

// Function calerl information
public class FunctionCaller
{
	// Variables
	public static int counterID = 1;
	public int id;
	public FunctionName functionName;
	
	// Create function caller
	FunctionCaller(FunctionName functionName, boolean generateID)
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
