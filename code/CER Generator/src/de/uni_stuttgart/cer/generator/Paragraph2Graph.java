// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.Map;

// Paragraph 2 graph for outgoing method calls
public class Paragraph2Graph extends Paragraph
{
	// Variables
	public ExecutionProfile executionProfile;
	
	// NodeType
	enum NodeType
    {
		NONE,
		START,
		METHOD_NAME,
		OUTGOING_METHOD_CALLS_COUNT,
		NO_OUTGOING_METHOD_CALLS,
		DIRECT_RECURSION,
		ANY_NON_RECURSIVELY_CALLED_METHODS,
		DIRECT_AND_NON_RECURSIVE_OUTGOING_METHOD_CALLS,
		OUTGOING_METHOD_CALLS,
		NON_RECURSIVELY_CALLED_METHODS_COUNT,
		ONE_METHOD_CALLED,
		TWO_METHODS_CALLED_AND_CLASS_DETAILS,
		MANY_METHODS_CALLED_AND_CLASS_DETAILS,
		DIRECT_RECURSIVE_OUTGOING_METHOD_CALLS,
		STOP
	 };
    
	// Create paragraph
	Paragraph2Graph(ExecutionProfile executionProfile)
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
				nextNode = NodeType.OUTGOING_METHOD_CALLS_COUNT;
				break;
			}
			
			case OUTGOING_METHOD_CALLS_COUNT:
			{
				if(ep.outgoingMethodCallsSum ==0)
				{
					nextNode = NodeType.NO_OUTGOING_METHOD_CALLS;
				}
				else
				{
					nextNode = NodeType.DIRECT_RECURSION;
				}
				break;
			}
			
			case NO_OUTGOING_METHOD_CALLS:
			{
				paragraph = paragraph + " did not make any method calls.";
				nextNode = NodeType.STOP;
				break;
			}
			
			case DIRECT_RECURSION:
			{
				if(ep.methodSpecifications.directRecursion == true)
				{
					nextNode = NodeType.ANY_NON_RECURSIVELY_CALLED_METHODS;
				}
				else
				{
					nextNode = NodeType.OUTGOING_METHOD_CALLS;
				}
				break;
			}
			
			case ANY_NON_RECURSIVELY_CALLED_METHODS:
			{
				if(ep.outgoingMethodCallsNRSum  > 0)
				{
					nextNode = NodeType.DIRECT_AND_NON_RECURSIVE_OUTGOING_METHOD_CALLS;
				}
				else
				{
					nextNode = NodeType.DIRECT_RECURSIVE_OUTGOING_METHOD_CALLS;
				}
				break;
			}
			
			case DIRECT_AND_NON_RECURSIVE_OUTGOING_METHOD_CALLS:
			{
				paragraph = paragraph + " made" + callsMade(ep.outgoingMethodCallsSum) + " out of which"
						+ callsMade(ep.methodSpecifications.directRecursionFrequency)
						+ wasOrWere(ep.methodSpecifications.directRecursionFrequency)
						+ " direct recursive to itself. It made" + callsMade(ep.outgoingMethodCallsNRSum);
				nextNode = NodeType.NON_RECURSIVELY_CALLED_METHODS_COUNT;
				break;
			}
			
			case OUTGOING_METHOD_CALLS:
			{
				paragraph = paragraph + " made" + callsMade(ep.outgoingMethodCallsSum);
				nextNode = NodeType.NON_RECURSIVELY_CALLED_METHODS_COUNT;
				break;
			}
			
			case NON_RECURSIVELY_CALLED_METHODS_COUNT:
			{
				if(ep.outgoingMethodCallsCount.size()  == 1)
				{
					nextNode = NodeType.ONE_METHOD_CALLED;
				}
				else if(ep.outgoingMethodCallsCount.size()  == 2)
				{
					nextNode = NodeType.TWO_METHODS_CALLED_AND_CLASS_DETAILS;
				}
				else 
				{
					nextNode = NodeType.MANY_METHODS_CALLED_AND_CLASS_DETAILS;
				}
				break;
			}
			
			case ONE_METHOD_CALLED:
			{
				paragraph = paragraph + " to" + useOnlyOne(ep.outgoingMethodCallsNRSum) + " method"
						+ " " + addHighlightPopoverVis(ep.outgoingMethodCallsOccurrence[0].getKey().methodName.onlyName,
								methodHighlightPopoverContent(ep.outgoingMethodCallsOccurrence[0].getKey().methodName))
						+ ".";
				nextNode = NodeType.STOP;
				break;
			}
			
			case TWO_METHODS_CALLED_AND_CLASS_DETAILS:
			{
				paragraph = paragraph
						+ itMadeOrOther(ep.methodSpecifications.directRecursion)
						+ twoMethodsCalledInfo(ep.outgoingMethodCallsOccurrence)
						+ classDetails(ep.isSameClassOutgoingMethodCalls, ep.allToOutside, ep.methodName) + ".";
				nextNode = NodeType.STOP;
				break;
			}
			
			case MANY_METHODS_CALLED_AND_CLASS_DETAILS:
			{
				paragraph = paragraph + " to" + " "
						+ addHighlightPopupVis(Integer.toString(ep.outgoingMethodCallsCount.size()),
								methodListHighlightPopupContent(ep.outgoingMethodCallsCount.size(),
										ep.outgoingMethodCallsOccurrence),
								"Called Methods")
						+ " methods" + classDetails(ep.isSameClassOutgoingMethodCalls, ep.allToOutside, ep.methodName)
						+ ". It made" 
						+ manyMethodsCalledInfo (ep.outgoingMethodCallsOccurrence) + ".";
				nextNode = NodeType.STOP;
				break;
			}
			
			case DIRECT_RECURSIVE_OUTGOING_METHOD_CALLS:
			{
				paragraph = paragraph + " made" + callsMade(ep.methodSpecifications.directRecursionFrequency) + " which"
						+ wasOrWere(ep.methodSpecifications.directRecursionFrequency) + " direct recursive to itself.";
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
	
	// Graph method - calls made
	private String callsMade(int no)
	{
		if(no == 1)
		{
			return  " only"+" "+addProgressBarVis(1, executionProfile.outgoingMethodCallsSum, "#32CD32", true, "1 call");
		}
		else
		{
			return " "+addProgressBarVis(no, executionProfile.outgoingMethodCallsSum, "#32CD32", true, no+" calls");
		}
	}
	
	// Graph method - use only one
	private String useOnlyOne(int no)
	{
		if (no == 1)
		{
			return "";
		}
		else
		{
			return " only 1";
		}
	}
	
	// Graph method - was or were
	private String wasOrWere(int no)
	{
		if (no == 1)
		{
			return " was";
		}
		else
		{
			return " were";
		}
	}
	
	// Graph method - it made or other
	private String itMadeOrOther(boolean directFlag)
	{
		if (directFlag == false)
		{
			return ". It made";
		}
		else
		{
			return " to other methods,";
		}
	}
	
	// Graph method - class details
	private String classDetails(boolean sameClassFlag, boolean outsideClassFlag, MethodName methodName)
	{
		if (sameClassFlag == true && outsideClassFlag == true)
		{
			return " which belong to the same class but different from method "
					+ addHighlightPopoverVis(methodName.onlyName, methodHighlightPopoverContent(methodName));
		}
		else if (sameClassFlag == true && outsideClassFlag == false)
		{
			return " which belong to the same class as method "
					+ addHighlightPopoverVis(methodName.onlyName, methodHighlightPopoverContent(methodName));
		}
		else
		{
			return "";
		}
	}
	
	// Graph method - two methods called info
	private String twoMethodsCalledInfo(Map.Entry<OutgoingMethodCall, Integer> outgoingMethodCallsOccurrence [])
	{
		int count0 = outgoingMethodCallsOccurrence [0].getValue();
		int count1 = outgoingMethodCallsOccurrence [1].getValue();

		if (count1 == count0)
		{
			return callsMade(count1) + " each to methods "
					+ addHighlightPopoverVis(outgoingMethodCallsOccurrence[1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[1].getKey().methodName))
					+ " and " + addHighlightPopoverVis(outgoingMethodCallsOccurrence[0].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[0].getKey().methodName));
		}
		else
		{
			return callsMade(count1) + " to method "
					+ addHighlightPopoverVis(outgoingMethodCallsOccurrence[1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[1].getKey().methodName))
					+ " and" + callsMade(count0) + " to method "
					+ addHighlightPopoverVis(outgoingMethodCallsOccurrence[0].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[0].getKey().methodName));
		}
	}
	
	// Graph method - many methods called info
	private String manyMethodsCalledInfo(Map.Entry<OutgoingMethodCall, Integer> outgoingMethodCallsOccurrence[])
	{
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

		if (maxCallers == 1)
		{
			return " maximum" + callsMade(maxCount) + " to method "
					+ addHighlightPopoverVis(outgoingMethodCallsOccurrence[length - 1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[length - 1].getKey().methodName));
		}
		else if (maxCallers == 2)
		{
			return " maximum" + callsMade(maxCount) + " each to methods "
					+ addHighlightPopoverVis(outgoingMethodCallsOccurrence[length - 1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[length - 1].getKey().methodName))
					+ " and " + addHighlightPopoverVis(outgoingMethodCallsOccurrence[length - 2].getKey().methodName.onlyName,
							methodHighlightPopoverContent(outgoingMethodCallsOccurrence[length - 2].getKey().methodName));
		}
		else if (maxCallers == length)
		{
			return callsMade(maxCount) + " each to all methods";
		}
		else
		{
			return " maximum" + callsMade(maxCount) + " each to "
					+ addHighlightPopupVis(Integer.toString(maxCallers),
							methodListHighlightPopupContent(maxCallers, outgoingMethodCallsOccurrence),
							"Called Methods (Maximum Calls)")
					+ " methods";
		}
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
	
	// Helper method - returns content to be displayed in hightlight popover inside popup visualization for method
	private String methodHighlightPopoverInPopupContent(MethodName methodName)
	{
		if (methodName.displayLevel <= 3)
		{
			return methodName.fullShortNameWithReturn;
		}
		else
		{
			return methodName.displayName;
		}
	}
	
	// Helper method - returns content to be displayed in hightlight popup visualization for method list
	@SuppressWarnings("unused")
	private String methodListHighlightPopupContent(int noOfMethods, Map.Entry<OutgoingMethodCall, Integer> calledMethodsList[])
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<div style=\\\"width: 625px;\\\">");
		sb.append(
				"    <table border=\\\"1\\\" style=\\\"border-collapse: collapse; background-color:#DDDDDD; width: 100%;\\\">");
		sb.append("      <tr>");
		sb.append("        <td style=\\\"text-align: center; width: 20%; padding: 8px;\\\"><b>Frequency</b></td>");
		sb.append("        <td style=\\\"text-align: center; width: 80%; padding: 8px;\\\"><b>Method Name</b></td>");
		sb.append("      </tr>");
		sb.append("    </table>");
		sb.append("</div>");

		String height = "";
		String totalHeight = "";
		if (noOfMethods < 12)
		{
			height = "height: " + (100.0 / noOfMethods) + "%;";
			totalHeight = "height: 100%;";
		}

		sb.append("<div style=\\\"width: 625px; height:300px; overflow-y:auto;\\\">");
		sb.append("   <table border=\\\"1\\\" style=\\\"text-align: center; border-collapse: collapse; " + totalHeight
				+ " width: 100%;\\\">");

		int listCount = calledMethodsList.length - noOfMethods;
		for (int i = (calledMethodsList.length - 1); i >= listCount; i--)
		{
			sb.append("<tr>");
			sb.append("   <td style=\\\"width: 20%; " + height + " padding: 2px 2px 2px 5px;\\\">" + addProgressBarVisInPopup(calledMethodsList[i].getValue(), executionProfile.outgoingMethodCallsSum, "#32CD32", true, Integer.toString(calledMethodsList[i].getValue())) + "</td>");
			sb.append("   <td style=\\\"width: 80%; " + height + " padding: 2px 2px 2px 5px;\\\">" + addHighlightPopoverVisInPopup(calledMethodsList[i].getKey().methodName.fullShortName, methodHighlightPopoverInPopupContent(calledMethodsList[i].getKey().methodName)) + "</td>");
			sb.append("</tr>");
		}

		sb.append("   </table>");
		sb.append("</div>");

		return sb.toString();
	}
}
