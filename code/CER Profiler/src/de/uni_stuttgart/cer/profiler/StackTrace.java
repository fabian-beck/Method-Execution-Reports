// Declare package
package de.uni_stuttgart.cer.profiler;

// Import classes
import java.util.Arrays;

// Stacktrace to store the callgraph information
public class StackTrace
{
	// Variables
	public static int counterID = 1;
	public int id;
	public String[] stackTrace;
	public int frequency;
	public FunctionCaller functionCaller;
	
	// Create stacktrace
	StackTrace(String[] stackTraceString, boolean generateID)
	{
		stackTrace = stackTraceString;
		frequency = 1;
		functionCaller = null;
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
	
	// Increase the frequency of stack trace by 1
	public int increaseFrequency()
	{
		frequency++;
		return frequency;
	}
	
	// Compare if stack trace is same or different
	public boolean same(String[] stackTraceString, FunctionCaller funcCaller)
	{
		if (Arrays.equals(stackTrace, stackTraceString))
		{
			if(funcCaller.id == -1)
			{
				return true;
			}
			else if(functionCaller.functionName.fullExists && funcCaller.functionName.fullExists)
			{
				if(functionCaller.functionName.fullDetailedLongNameWithReturn.equals(funcCaller.functionName.fullDetailedLongNameWithReturn))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			if(functionCaller.functionName.longName.equals(funcCaller.functionName.longName))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
