// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.HashMap;

// Method properties information
public class MethodProperties
{
	// Variables
	public MethodName methodName;
	public MethodModifier methodModifier;
	public int startingLineNumber;
	public HashMap<Integer, OutgoingMethodCall> totalOutgoingMethodCalls;
	public HashMap<Integer, MethodCaller> totalMethodCallers;
	public HashMap<String, Integer> totalMethodCallersIDs;
	public int totalMethodCallersIDCounter;

	// Create method specifications
	MethodProperties(MethodName methodName, MethodModifier methodModifier, int startingLineNumber)
	{
		this.methodName = methodName;
		this.methodModifier = methodModifier;
		this.startingLineNumber = startingLineNumber;
		totalOutgoingMethodCalls = new HashMap<Integer, OutgoingMethodCall>();
		totalMethodCallers = new HashMap<Integer, MethodCaller>();
		totalMethodCallersIDs = new HashMap<String, Integer>();
		totalMethodCallersIDCounter = 1;
	}
	
	// Add total outgoing method call
	public void addTotalOutgoingMethodCall(OutgoingMethodCall outgoingMethodCall)
	{
		totalOutgoingMethodCalls.put(outgoingMethodCall.id, outgoingMethodCall);
	}
	
	// Add total method caller
	public MethodCaller addTotalMethodCaller(MethodName methodName)
	{
		// If method caller is already added then return method caller
		if(totalMethodCallersIDs.containsKey(methodName.fullDetailedLongNameWithReturn))
		{
			return totalMethodCallers.get(totalMethodCallersIDs.get(methodName.fullDetailedLongNameWithReturn));
		}
		
		// If method caller is already not added, then create a new method caller and add
		else
		{
			MethodCaller methodCaller = new MethodCaller(totalMethodCallersIDCounter, methodName);
			totalMethodCallersIDs.put(methodName.fullDetailedLongNameWithReturn, totalMethodCallersIDCounter);
			totalMethodCallers.put(totalMethodCallersIDCounter, methodCaller);
			totalMethodCallersIDCounter++;
			return methodCaller;
		}
	}
}