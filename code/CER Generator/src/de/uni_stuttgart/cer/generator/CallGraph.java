// Declare Package
package de.uni_stuttgart.cer.generator;

// Callgraph to store the stacktrace information
public class CallGraph
{
	// Variables
	public int id;
	public String[] callGraph;
	public MethodCaller methodCaller;
	public int frequency;
	
	// Create a callgraph
	CallGraph (int id, String[] callGraph, MethodCaller methodCaller, int frequency)
	{
		this.id = id;
		this.callGraph = callGraph;
		this.frequency = frequency;
		this.methodCaller = methodCaller;		
	}
}