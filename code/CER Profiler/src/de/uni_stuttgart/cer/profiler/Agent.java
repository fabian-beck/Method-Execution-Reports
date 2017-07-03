// Declare package
package de.uni_stuttgart.cer.profiler;

// Import classes
import java.lang.instrument.Instrumentation;

// Agent for instrumentation
public class Agent
{
	// Variables
	public static FunctionName functionName;
	public static FunctionModifier functionModifier;
	public static int startingLineNumber;
	public static boolean profilingMode;
	public static boolean fileCreation;
	
	// Premain function called before the execution of program
	public static void premain(String agentArgument,Instrumentation instrumentation)
	{	
		// Create a function name (not constructor)
		functionName = new FunctionName(agentArgument, null, false);
		fileCreation = false;
		
		// Add class transformer for instrumentation
		ClassTransformer classTransformer = new ClassTransformer();
		instrumentation.addTransformer(classTransformer);
    }
	
	// Create file for profiling and enable the profiling mode if its successful
	public static void createFile()
	{
		if(!fileCreation)
		{
			profilingMode = FileHandler.create(functionName, functionModifier, startingLineNumber);
			fileCreation = true;
		}
	}
}
