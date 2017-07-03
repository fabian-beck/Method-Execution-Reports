// Declare package
package de.uni_stuttgart.cer.profiler;

// Import classes
import java.util.HashMap;

// Incoming function call information
public class IncomingFunctionCall
{
	// Variables
	public static int counterID = 1;
	public int id;
	public int callType;
	public int levelType;
	public HashMap<Integer, OutgoingFunctionCallData> outgoingFunctionCallsData;
	public int lastEntryID;
	public int functionCalIerID;
	public int depth;
	public long startTime;
	public long endTime;
	public long overheadTime;
	public long consumedTime;
	
	// Create incoming function call
	IncomingFunctionCall(boolean generateID)
	{
		callType = 0;
		levelType = -1;
		outgoingFunctionCallsData = new HashMap<Integer, OutgoingFunctionCallData>();
		//outgoingConsumedTimes = new HashMap<Integer, Long>();
		depth = 0;
		startTime = 0;
		endTime = 0;
		overheadTime = 0;
		consumedTime = 0;
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
