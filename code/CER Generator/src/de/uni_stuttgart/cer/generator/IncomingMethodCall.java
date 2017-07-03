// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.HashMap;

// Incoming method call information
public class IncomingMethodCall
{
	// Variables
	public int id;
	public int callType;
	public int levelType;
	public HashMap<Integer, OutgoingMethodCall> outgoingMethodCalls;
	public int depth;
		
	// Create incoming method call
	IncomingMethodCall(int id, int callType, int levelType, int depth)
	{
		this.id = id;
		this.callType = callType;
		this.levelType = levelType;
		this.depth = depth;
		outgoingMethodCalls = new HashMap<Integer, OutgoingMethodCall>();
	}
}