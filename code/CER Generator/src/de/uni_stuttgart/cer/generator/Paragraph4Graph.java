// Declare package
package de.uni_stuttgart.cer.generator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
// Import classes
import java.util.Map;

// Paragraph 4 graph for summary
public class Paragraph4Graph extends Paragraph
{
	// Variables
	public ExecutionProfile executionProfile;
		
	// NodeType
	enum NodeType
    {
		NONE,
		START,
		METHOD_NAME,
		INCOMING_METHOD_CALLS_COUNT,
		NO_INCOMING_METHOD_CALLS,
		RECURSION,
		RECURSION_INFORMATION,
		INCOMING_METHOD_CALLS_INFORMATION,
		OUTGOING_METHOD_CALLS_COUNT,
		OUTGOING_METHOD_CALLS_INFORMATION,
		NO_OUTGOING_METHOD_CALLS,
		TIME_CONSUMPTION,
		STOP
	 };
	 
	// Create paragraph
	Paragraph4Graph(ExecutionProfile executionProfile)
	{
		super();
		this.executionProfile = executionProfile;
	}
		
	// Build paragraph
	public void build()
	{
		// Initialize the string
		paragraph = "";

		// Initialize the next node with head node
		NodeType nextNode = NodeType.START;

		// Traverse the graph till the last node
		while (nextNode != NodeType.STOP)
		{
			nextNode = processNode(nextNode);
		}
	}

	// Process node and return the next node
	private NodeType processNode(NodeType nodeType)
	{
		NodeType nextNode = NodeType.NONE;
		ExecutionProfile ep = executionProfile;

		// Process the node and find the next node
		switch (nodeType)
		{
			case START:
			{
				nextNode = NodeType.METHOD_NAME;
				break;
			}

			case METHOD_NAME:
			{
				paragraph = paragraph + "Method" + " "
						+ addHighlightPopoverVis(ep.methodName.onlyName, methodHighlightPopoverContent(ep.methodName));
				nextNode = NodeType.INCOMING_METHOD_CALLS_COUNT;
				break;
			}

			case INCOMING_METHOD_CALLS_COUNT:
			{
				if(ep.methodSpecifications.totalFrequency == 0)
				{
					nextNode = NodeType.NO_INCOMING_METHOD_CALLS;
				}
				else
				{
					nextNode = NodeType.RECURSION;
				}
				break;
			}

			case NO_INCOMING_METHOD_CALLS:
			{
				paragraph = paragraph + " was never called.";
				nextNode = NodeType.STOP;
				break;
			}

			case RECURSION:
			{
				if((ep.methodSpecifications.directRecursion == true) || (ep.methodSpecifications.indirectRecursion == true))
				{
					nextNode = NodeType.RECURSION_INFORMATION;
				}
				else
				{
					nextNode = NodeType.INCOMING_METHOD_CALLS_INFORMATION;
				}
				break;
			}

			case RECURSION_INFORMATION:
			{
				paragraph = paragraph + " was recursively called with recursion depth of"
						+ useOnly(ep.recursionDepthOccurrence.length) + " " + ep.recursionDepthOccurrence.length + ". It";
				nextNode = NodeType.INCOMING_METHOD_CALLS_INFORMATION;
				break;
			}

			case INCOMING_METHOD_CALLS_INFORMATION:
			{
				paragraph = paragraph + " was called" + totalOrOnlyTime(ep.methodSpecifications.totalFrequency)
						+ maxIncoming(ep.methodSpecifications.totalFrequency,
								ep.methodSpecifications.indirectRecursionFrequency,
								ep.methodSpecifications.directRecursionFrequency,
								ep.methodSpecifications.nonRecursionFrequency, ep.methodCallersOccurrence);
				nextNode = NodeType.OUTGOING_METHOD_CALLS_COUNT;
				break;
			}

			case OUTGOING_METHOD_CALLS_COUNT:
			{
				if(ep.outgoingMethodCallsSum > 0)
				{
					nextNode = NodeType.OUTGOING_METHOD_CALLS_INFORMATION;
				}
				else
				{
					nextNode = NodeType.NO_OUTGOING_METHOD_CALLS;
				}
				break;
			}

			case OUTGOING_METHOD_CALLS_INFORMATION:
			{
				paragraph = paragraph + " It made" + totalOrOnlyCall(ep.outgoingMethodCallsSum)
						+ maxOutgoing(ep.outgoingMethodCallsSum, ep.outgoingMethodCallsNRSum,
								ep.methodSpecifications.directRecursionFrequency, ep.outgoingMethodCallsOccurrence);
				nextNode = NodeType.TIME_CONSUMPTION;
				break;
			}

			case NO_OUTGOING_METHOD_CALLS:
			{
				paragraph = paragraph + " It did not make any method calls.";
				nextNode = NodeType.TIME_CONSUMPTION;
				break;
			}
			
			case TIME_CONSUMPTION:
			{
				paragraph = paragraph + " It took"+ms(ep.methodCallsTime)+".";
				nextNode = NodeType.STOP;
				break;
			}

			case STOP:
			{
				break;
			}
		}

		return nextNode;
	}
		
	// Graph method - use only
	private String useOnly(int no)
	{
		if (no == 1)
		{
			return " only";
		}
		else
		{
			return "";
		}
	}

	// Graph method - total or only time
	private String totalOrOnlyTime(int no)
	{
		if (no == 1)
		{
			return " only 1 time";
		}
		else
		{
			return " total "+no+" times";
		}
	}
	
	// Graph method - total or only call
	private String totalOrOnlyCall(int no)
	{
		if (no == 1)
		{
			return " only 1 outgoing call";
		}
		else
		{
			return " total "+no+" outgoing calls";
		}
	}
	
	// Graph method - max incoming
	private String maxIncoming(int totalFrequency, int indirectRecursionFrequency, int directRecursionFrequency, int nonRecursionFrequency, Map.Entry<MethodCaller, Integer> methodCallersOccurrence[])
	{
		if(totalFrequency == 0 || totalFrequency == 1)
		{
			return ".";
		}
		
		if(directRecursionFrequency == 0 && methodCallersOccurrence.length == 1)
		{
			return ".";
		}
		
		int maxFromCallers = 0;
		int length = methodCallersOccurrence.length;
		int maxCount = methodCallersOccurrence[length-1].getValue();
		int maxCallers = 1;
		for(int i=length-2; i >= 0; i--)
		{
			if(maxCount == methodCallersOccurrence[i].getValue())
			{
				maxCallers++;
			}
			else
			{
				break;
			}
		}
		if(maxCallers == 1)
		{
			maxFromCallers = methodCallersOccurrence[length-1].getValue();
		}
		
		if(maxFromCallers == directRecursionFrequency)
		{
			return ".";
		}
		
		if(maxFromCallers > directRecursionFrequency)
		{
			if(maxFromCallers > indirectRecursionFrequency)
			{
				return " with maximum calls from method "
						+ addHighlightPopoverVis(methodCallersOccurrence[length - 1].getKey().methodName.onlyName,
								methodHighlightPopoverContent(methodCallersOccurrence[length - 1].getKey().methodName))
						+ ".";
			}
			else
			{
				return ".";
			}
		}
		else
		{
			if(directRecursionFrequency > methodCallersOccurrence[length - 1].getValue())
			{
				if(directRecursionFrequency > indirectRecursionFrequency)
				{
					return " with maximum calls from itself as direct recursion.";
				}
				else
				{
					return ".";
				}
			}
			else
			{
				return ".";
			}
			
		}		
	}
	
	// Graph method - max outgoing
	private String maxOutgoing(int outgoingMethodCallsSum, int outgoingMethodCallsNRSum, int directRecursionFrequency, Map.Entry<OutgoingMethodCall, Integer> outgoingMethodCallsOccurrence[])
	{
		if(outgoingMethodCallsSum == 0 || outgoingMethodCallsSum == 1)
		{
			return ".";
		}
		
		if(directRecursionFrequency == 0 && outgoingMethodCallsOccurrence.length == 1)
		{
			return ".";
		}
		
		if(outgoingMethodCallsNRSum == 0)
		{
			return ".";
		}
		
		int maxFromMethod = 0;
		int length = outgoingMethodCallsOccurrence.length;
		int maxCount = outgoingMethodCallsOccurrence[length - 1].getValue();
		int maxCallers = 1;
		for (int i = length - 2; i >= 0; i--)
		{
			if (maxCount == outgoingMethodCallsOccurrence[i].getValue())
			{
				maxCallers++;
			}
			else
			{
				break;
			}
		}
		
		if(maxCallers == 1)
		{
			maxFromMethod = outgoingMethodCallsOccurrence[length-1].getValue();
		}
		
		if(maxFromMethod == directRecursionFrequency)
		{
			return ".";
		}
		
		if(maxFromMethod > directRecursionFrequency)
		{
			return " with maximum calls to method "
					+ addHighlightPopoverVis(outgoingMethodCallsOccurrence[length - 1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[length - 1].getKey().methodName))
					+ ".";
		}
		else
		{
			if(maxFromMethod > outgoingMethodCallsOccurrence[length - 1].getValue())
			{
				return " with maximum calls to itself as direct recursion.";
			}
			else
			{
				return ".";
			}
		}
	}
	
	// Graph method - ms (miliseconds)
	private String ms(double no)
	{
		NumberFormat formatter = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.US));
		return " "+formatter.format(no)+" ms";
	}

	// Helper method - returns content to be displayed in hightlight popover visualization for method
	private String methodHighlightPopoverContent(MethodName methodName)
	{
		if (methodName.displayLevel == 1)
		{
			return methodName.shortName;
		}
		else
		{
			return methodName.displayName;
		}
	}
}
