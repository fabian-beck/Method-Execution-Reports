// Declare package
package de.uni_stuttgart.cer.generator;

// Outgoing method call information
public class OutgoingMethodCall
{
	// Variables
	public int id;
	public MethodName methodName;
	
	// Create outgoing method call
	OutgoingMethodCall(int id, MethodName methodName)
	{
		this.id = id;
		this.methodName = methodName;
	}
}
