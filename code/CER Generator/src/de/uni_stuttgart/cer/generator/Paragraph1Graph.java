// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.Map;

// Paragraph 1 graph for incoming method calls
public class Paragraph1Graph extends Paragraph
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
		RECURSION_TYPE,
		ONE_TYPE_RECURSION,
		TWO_TYPES_RECURSION,
		INCOMING_METHOD_CALLS,
		NON_RECURSIVE_INCOMING_METHOD_CALLS,
		METHOD_CALLERS_COUNT,
		ENTRY_POINT_CALL,
		ONE_METHOD_CALLER,
		TWO_METHOD_CALLERS_AND_CLASS_DETAILS,
		MANY_METHOD_CALLERS_AND_CLASS_DETAILS,
		METHOD_ACCESS_MODIFIER,
		ANY_METHOD_CALLER_OUTSIDE_CLASS,
		PUBLIC_METHOD_CALLED_WITHIN_CLASS,
		STOP
    };
	
	// Create paragraph
	Paragraph1Graph(ExecutionProfile executionProfile)
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
		while(nextNode != NodeType.STOP)
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
		switch(nodeType)
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
				if(ep.methodSpecifications.totalFrequency==0)
				{
					nextNode = NodeType.NO_INCOMING_METHOD_CALLS;
				}
				else
				{
					nextNode = NodeType.RECURSION_TYPE;
				}
				break;
			}
				
			case NO_INCOMING_METHOD_CALLS:
			{
				paragraph = paragraph + " was never called.";
				nextNode = NodeType.STOP;
				break;
			}
				
			case RECURSION_TYPE:
			{
				if(ep.methodSpecifications.directRecursion == false && ep.methodSpecifications.indirectRecursion == false)
				{
					nextNode = NodeType.INCOMING_METHOD_CALLS;
				}
				else if(ep.methodSpecifications.directRecursion == true && ep.methodSpecifications.indirectRecursion == true)
				{
					nextNode = NodeType.TWO_TYPES_RECURSION;
				}
				else
				{
					nextNode = NodeType.ONE_TYPE_RECURSION;
				}
				break;
			}
				
			case ONE_TYPE_RECURSION:
			{
				paragraph = paragraph + " is"
						+ recursionTypeInfo(ep.methodSpecifications.directRecursion,
								ep.methodSpecifications.indirectRecursion)
						+ " method which was called" + timesCalled(ep.methodSpecifications.totalFrequency)
						+ " out of which" + timesCalled(ep.methodSpecifications.directRecursionFrequency
								+ ep.methodSpecifications.indirectRecursionFrequency)
						+ " it was recursive.";
				nextNode = NodeType.NON_RECURSIVE_INCOMING_METHOD_CALLS;
				break;
			}
				
			case TWO_TYPES_RECURSION:
			{
				paragraph = paragraph + " is a recursive method which was called"
						+ timesCalled(ep.methodSpecifications.totalFrequency) + " out of which"
						+ timesCalled(ep.methodSpecifications.directRecursionFrequency
								+ ep.methodSpecifications.indirectRecursionFrequency)
						+ " it was recursive," + twoTypesRecursionInfo(ep.methodSpecifications.directRecursionFrequency,
								ep.methodSpecifications.indirectRecursionFrequency)
						+ ".";
				nextNode = NodeType.NON_RECURSIVE_INCOMING_METHOD_CALLS;
				break;
			}
				
			case INCOMING_METHOD_CALLS:
			{
				paragraph = paragraph + " was called" + timesCalled(ep.methodSpecifications.totalFrequency);
				nextNode = NodeType.METHOD_CALLERS_COUNT;
				break;
			}
				
			case NON_RECURSIVE_INCOMING_METHOD_CALLS:
			{
				paragraph = paragraph + " It was called non-recursively" + timesCalled(ep.methodSpecifications.nonRecursionFrequency);
				nextNode = NodeType.METHOD_CALLERS_COUNT;
				break;
			}
				
			case METHOD_CALLERS_COUNT:
			{
				if(ep.methodCallersCount.size() == 0)
				{
					nextNode = NodeType.ENTRY_POINT_CALL;
				}
				else if(ep.methodCallersCount.size() == 1)
				{
					nextNode = NodeType.ONE_METHOD_CALLER;
				}
				else if(ep.methodCallersCount.size() == 2)
				{
					nextNode = NodeType.TWO_METHOD_CALLERS_AND_CLASS_DETAILS;
				}
				else
				{
					nextNode = NodeType.MANY_METHOD_CALLERS_AND_CLASS_DETAILS;
				}
				break;
			}
				
			case ENTRY_POINT_CALL:
			{
				paragraph = paragraph + " as entry point by Java virtual machine.";
				nextNode = NodeType.STOP;
				break;
			}
				
			case ONE_METHOD_CALLER:
			{
				paragraph = paragraph + " by" + useOnlyOne(ep.methodSpecifications.nonRecursionFrequency) + " method"
						+ " " + addHighlightPopoverVis(ep.methodCallersOccurrence[0].getKey().methodName.onlyName,
								methodHighlightPopoverContent(ep.methodCallersOccurrence[0].getKey().methodName))
						+ ".";
				nextNode = NodeType.METHOD_ACCESS_MODIFIER;
				break;
			}
			
			case TWO_METHOD_CALLERS_AND_CLASS_DETAILS:
			{
				paragraph = paragraph
						+ calledOrComma(ep.methodSpecifications.directRecursion,
								ep.methodSpecifications.indirectRecursion)
						+ twoMethodCallersInfo(ep.methodCallersOccurrence)
						+ classDetails(ep.isSameClassMethodCallers, ep.isFromOutside, ep.methodName) + ".";
				nextNode = NodeType.METHOD_ACCESS_MODIFIER;
				break;
			}
				
			case MANY_METHOD_CALLERS_AND_CLASS_DETAILS:
			{
				paragraph = paragraph + " by" + " "
						+ addHighlightPopupVis(Integer.toString(ep.methodCallersCount.size()),
								methodListHighlightPopupContent(ep.methodCallersCount.size(),
										ep.methodCallersOccurrence),
								"Method Callers")
						+ " methods" + classDetails(ep.isSameClassMethodCallers, ep.isFromOutside, ep.methodName)
						+ ". It was called" 
						+ manyMethodCallersInfo(ep.methodCallersOccurrence) + ".";
				nextNode = NodeType.METHOD_ACCESS_MODIFIER;
				break;
			}
				
			case METHOD_ACCESS_MODIFIER:
			{
				if(ep.methodProperties.methodModifier.accessModifier.equals("Public"))
				{
					nextNode = NodeType.ANY_METHOD_CALLER_OUTSIDE_CLASS;
				}
				else
				{
					nextNode = NodeType.STOP;
				}
				break;
			}
				
			case ANY_METHOD_CALLER_OUTSIDE_CLASS:
			{
				if(ep.isFromOutside == false)
				{
					nextNode = NodeType.PUBLIC_METHOD_CALLED_WITHIN_CLASS;
				}
				else
				{
					nextNode = NodeType.STOP;
				}
				break;
			}
				
			case PUBLIC_METHOD_CALLED_WITHIN_CLASS:
			{
				paragraph = paragraph + " It is a public method but it was"
						+ neverOrNot(ep.methodSpecifications.nonRecursionFrequency) + " called"
						+ useRecursively(ep.methodSpecifications.indirectRecursion) + " from outside its class.";
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
	
	// Graph method - times called
	private String timesCalled(int no)
	{
		if(no == 1)
		{
			return  " only"+" "+addProgressBarVis(1, executionProfile.methodSpecifications.totalFrequency, "#00BFFF", true, "1 time");
		}
		else
		{
			return " "+addProgressBarVis(no, executionProfile.methodSpecifications.totalFrequency, "#00BFFF", true, no+" times");
		}
	}
	
	// Graph method - use only one
	private String useOnlyOne(int no)
	{
		if(no == 1)
		{
			return "";
		}
		else
		{
			return " only 1";
		}
	}
	
	// Graph method - called or comma
	private String calledOrComma(boolean directFlag, boolean indirectFlag)
	{
		if (directFlag == false && indirectFlag == false)
		{
			return ". It was called";
		}
		else
		{
			return ",";
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
	
	// Graph method - two method callers info
	private String twoMethodCallersInfo(Map.Entry<MethodCaller, Integer> methodCallersOccurrence[])
	{
		int count0 = methodCallersOccurrence[0].getValue();
		int count1 = methodCallersOccurrence[1].getValue();
		
		if (count1 == count0)
		{
			return timesCalled(count1) + " each by methods "
					+ addHighlightPopoverVis(methodCallersOccurrence[1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[1].getKey().methodName))
					+ " and " + addHighlightPopoverVis(methodCallersOccurrence[0].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[0].getKey().methodName));
		}
		else
		{
			return timesCalled(count1) + " by method "
					+ addHighlightPopoverVis(methodCallersOccurrence[1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[1].getKey().methodName))
					+ " and" + timesCalled(count0) + " by method "
					+ addHighlightPopoverVis(methodCallersOccurrence[0].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[0].getKey().methodName));
		}
	}
	
	// Graph method - many method callers info
	private String manyMethodCallersInfo(Map.Entry<MethodCaller, Integer> methodCallersOccurrence[])
	{
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
			
		if (maxCallers == 1)
		{
			return " maximum" + timesCalled(maxCount) + " by method "
					+ addHighlightPopoverVis(methodCallersOccurrence[length - 1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[length - 1].getKey().methodName));
		}
		else if (maxCallers == 2)
		{
			return " maximum" + timesCalled(maxCount) + " each by methods "
					+ addHighlightPopoverVis(methodCallersOccurrence[length - 1].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[length - 1].getKey().methodName))
					+ " and " + addHighlightPopoverVis(methodCallersOccurrence[length - 2].getKey().methodName.onlyName,
							methodHighlightPopoverContent(methodCallersOccurrence[length - 2].getKey().methodName));
		}
		else if (maxCallers == length)
		{
			return timesCalled(maxCount) + " each by all methods";
		}
		else
		{
			return " maximum" + timesCalled(maxCount) + " each by "
					+ addHighlightPopupVis(Integer.toString(maxCallers),
							methodListHighlightPopupContent(maxCallers, methodCallersOccurrence),
							"Method Callers (Maximum Calls)")
					+ " methods";
		}
	}
	
	// Graph method - recursion type info
	private String recursionTypeInfo(boolean directFlag, boolean indirectFlag)
	{
		if (directFlag == true && indirectFlag == false)
		{
			return  " a direct recursive";
		}
		else if (directFlag == false && indirectFlag == true)
		{
			return " an indirect recursive";
		}
		else if (directFlag == true && indirectFlag == true)
		{
			return " a recursive";
		}
		else
		{
			return "";
		}
	}
	
	// Graph method - two types recursion info
	private String twoTypesRecursionInfo(int directNo, int indirectNo)
	{
		if (directNo == indirectNo)
		{
			return timesCalled(directNo)+" each direct recursive and indirect recursive";
		}
		else 
		{
			return timesCalled(directNo)+" direct recursive and"+timesCalled(indirectNo)+" indirect recursive";
		}
	}
	
	// Graph method - never or not
	private String neverOrNot(int no)
	{
		if (no == 1)
		{
			return " not";
		}
		else
		{
			return " never";
		}
	}
	
	// Graph method - use recursively
	private String useRecursively(boolean indirectFlag)
	{
		if (indirectFlag == true)
		{
			return " non-recursively";
		}
		else
		{
			return "";
		}
	}
	
	// Helper method - returns content to be displayed in hightlight popover visualization for method
	private String methodHighlightPopoverContent(MethodName methodName)
	{
		if(methodName.displayLevel == 1)
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
	private String methodListHighlightPopupContent(int noOfMethods, Map.Entry<MethodCaller, Integer> methodCallersList[])
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<div style=\\\"width: 625px;\\\">");
		sb.append("    <table border=\\\"1\\\" style=\\\"border-collapse: collapse; background-color:#DDDDDD; width: 100%;\\\">");
		sb.append("      <tr>");
		sb.append("        <td style=\\\"text-align: center; width: 20%; padding: 8px;\\\"><b>Frequency</b></td>");
		sb.append("        <td style=\\\"text-align: center; width: 80%; padding: 8px;\\\"><b>Method Name</b></td>");
		sb.append("      </tr>");
		sb.append("    </table>");
		sb.append("</div>");
		
		String height = "";
		String totalHeight ="";
	    if(noOfMethods < 12)
		{
			height = "height: "+(100.0/noOfMethods)+"%;";
			totalHeight = "height: 100%;";
		}
			
		sb.append("<div style=\\\"width: 625px; height:300px; overflow-y:auto;\\\">");
		sb.append("   <table border=\\\"1\\\" style=\\\"text-align: center; border-collapse: collapse; "+totalHeight+" width: 100%;\\\">");
		
		int listCount = methodCallersList.length - noOfMethods;
		for(int i= (methodCallersList.length -1); i >= listCount; i--)
		{
			sb.append("<tr>");
			sb.append("   <td style=\\\"width: 20%; "+height+" padding: 2px 2px 2px 5px;\\\">"+addProgressBarVisInPopup(methodCallersList[i].getValue(), executionProfile.methodSpecifications.totalFrequency, "#00BFFF", true, Integer.toString(methodCallersList[i].getValue()))+"</td>");
			sb.append("   <td style=\\\"width: 80%; "+height+" padding: 2px 2px 2px 5px;\\\">"+addHighlightPopoverVisInPopup(methodCallersList[i].getKey().methodName.fullShortName, methodHighlightPopoverInPopupContent(methodCallersList[i].getKey().methodName))+"</td>");
			sb.append("</tr>");
		}
		
		sb.append("   </table>");
		sb.append("</div>");
		
		return sb.toString();
	}
}
