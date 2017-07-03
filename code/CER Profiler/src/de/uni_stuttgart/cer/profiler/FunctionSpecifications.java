// Declare package
package de.uni_stuttgart.cer.profiler;

// Function specifications information
public class FunctionSpecifications
{
	// Variables
	public FunctionName functionName;
	public int totalFrequency;
	public int nonRecursionFrequency;
	public int directRecursionFrequency;
	public int indirectRecursionFrequency;
	public boolean directRecursion;
	public boolean indirectRecursion;	
	
	// Create function specifications
	FunctionSpecifications()
	{
		functionName = null;
		totalFrequency = 0;
		nonRecursionFrequency = 0;
		directRecursionFrequency = 0;
		indirectRecursionFrequency = 0;
		directRecursion = false;
		indirectRecursion = false;
	}
}
