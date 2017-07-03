// Declare package
package de.uni_stuttgart.cer.profiler;

// Outgoing function call data information
public class OutgoingFunctionCallData
{
	// Variables
	OutgoingFunctionCall outgoingFunctionCall;
	int counterID;
	public long startTime;
	public long endTime;
	public long overheadTime;
	public long consumedTime;
	public boolean timeAvailable;

	// Store outgoing function call data
	OutgoingFunctionCallData(OutgoingFunctionCall outgoingFunctionCall, boolean timeAvailable)
	{
		this.outgoingFunctionCall = outgoingFunctionCall;
		this.timeAvailable = timeAvailable;
	}
}