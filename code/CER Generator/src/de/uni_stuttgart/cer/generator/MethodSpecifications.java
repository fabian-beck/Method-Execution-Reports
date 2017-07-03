// Declare package
package de.uni_stuttgart.cer.generator;

// Method specifications information
public class MethodSpecifications
{
	// Variables
	public MethodName methodName;
	public int totalFrequency;
	public int nonRecursionFrequency;
	public int directRecursionFrequency;
	public int indirectRecursionFrequency;
	public boolean directRecursion;
	public boolean indirectRecursion;

	// Create function specifications
	MethodSpecifications(MethodName methodName)
	{
		this.methodName = methodName;
		totalFrequency = 0;
		nonRecursionFrequency = 0;
		directRecursionFrequency = 0;
		indirectRecursionFrequency = 0;
		directRecursion = false;
		indirectRecursion = false;
	}
}
