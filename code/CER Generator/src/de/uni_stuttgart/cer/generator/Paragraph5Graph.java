// Declare package
package de.uni_stuttgart.cer.generator;

// Import Classes
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

// Paragraph 5 graph for time consumption
public class Paragraph5Graph extends Paragraph
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
			INFINITE_HIGH_UNCERTAINTY,
			ONLY_TOTAL_TIME,
			OUTGOING_METHOD_CALLS_COUNT,
			TOTAL_AND_SELF_TIME,
			CALLED_METHODS_COUNT,
			ONE_METHOD_CALLED,
			TWO_METHODS_CALLED,
			MANY_METHODS_CALLED,
			UNCERTAINTY_INFORMATION,
			STOP
	};
	
	// Create paragraph
	Paragraph5Graph(ExecutionProfile executionProfile)
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
					nextNode = NodeType.INFINITE_HIGH_UNCERTAINTY;
				}
				break;
			}
			
			case NO_INCOMING_METHOD_CALLS:
			{
				paragraph = paragraph + " was never called.";
				nextNode = NodeType.STOP;
				break;
			}
			
			case INFINITE_HIGH_UNCERTAINTY:
			{
				if(ep.timeUncertainty.globalUncertainty==5)
				{
					nextNode = NodeType.ONLY_TOTAL_TIME ;
				}
				else
				{
					nextNode = NodeType.OUTGOING_METHOD_CALLS_COUNT;
				}
				break;
			}
			
			case ONLY_TOTAL_TIME:
			{
				paragraph = paragraph + " took" +
						msNoVis(executionProfile.methodCallsTime) + ". Due to insufficient information, meaningful information regarding time cannot be concluded.";
				nextNode = NodeType.STOP;
				break;
			}
			
			case OUTGOING_METHOD_CALLS_COUNT:
			{
				if(ep.outgoingMethodCallsSum ==0)
				{
					nextNode = NodeType.ONLY_TOTAL_TIME;
				}
				else
				{
					nextNode = NodeType.TOTAL_AND_SELF_TIME;
				}
				break;
			}
			
			case TOTAL_AND_SELF_TIME:
			{
				paragraph = paragraph + " took"
						+ ms(executionProfile.methodCallsTime) + ", out of which"
						+ ms(executionProfile.methodCallsTime - executionProfile.totalOutgoingMethodCallsTime) + 
						" were consumed as self time and" + ms(executionProfile.totalOutgoingMethodCallsTime) + " by outgoing calls";
				nextNode = NodeType.CALLED_METHODS_COUNT;
				break;
			}
			
			case CALLED_METHODS_COUNT:
			{
				if(ep.outgoingMethodCallsCount.size()  == 1)
				{
					nextNode = NodeType.ONE_METHOD_CALLED;
				}
				else if(ep.outgoingMethodCallsCount.size()  == 2)
				{
					nextNode = NodeType.TWO_METHODS_CALLED;
				}
				else 
				{
					nextNode = NodeType.MANY_METHODS_CALLED;
				}
				break;
			}
			
			case ONE_METHOD_CALLED:
			{
				paragraph = paragraph + " made to method"
						+ " " + addHighlightPopoverVis(ep.outgoingMethodCallsTime[0].getKey().methodName.onlyName,
								methodHighlightPopoverContent(ep.outgoingMethodCallsTime[0].getKey().methodName))
						+ ".";
				nextNode = NodeType.UNCERTAINTY_INFORMATION;
				break;
			}
			
			case TWO_METHODS_CALLED:
			{
				paragraph = paragraph + ". Outgoing calls made to method" + " "
						+ addHighlightPopoverVis(ep.outgoingMethodCallsTime[0].getKey().methodName.onlyName,
								methodHighlightPopoverContent(ep.outgoingMethodCallsTime[0].getKey().methodName)) 
						+ " took" + ms(ep.outgoingMethodCallsTime[0].getValue()) + " and to method " 
						+ addHighlightPopoverVis(ep.outgoingMethodCallsTime[1].getKey().methodName.onlyName,
								methodHighlightPopoverContent(ep.outgoingMethodCallsTime[1].getKey().methodName))
						+  " took" + ms(ep.outgoingMethodCallsTime[1].getValue()) + ".";
				nextNode = NodeType.UNCERTAINTY_INFORMATION;
				break;
			}
			
			case MANY_METHODS_CALLED:
			{
				int lastIndex = ep.outgoingMethodCallsDuration.size() - 1;
				paragraph = paragraph + " made to" + " "
						+ addHighlightPopupVis(Integer.toString(ep.outgoingMethodCallsDuration.size()),
								methodListHighlightPopupContent(ep.outgoingMethodCallsDuration.size(),
										ep.outgoingMethodCallsTime),
								"Called Methods")
						+ " methods." + " Outgoing calls made to method" + " "
						+ addHighlightPopoverVis(ep.outgoingMethodCallsTime[lastIndex].getKey().methodName.onlyName,
								methodHighlightPopoverContent(ep.outgoingMethodCallsTime[lastIndex].getKey().methodName))
						+" took maximum time which was"
						+ms(ep.outgoingMethodCallsTime[lastIndex].getValue()) + ".";
				nextNode = NodeType.UNCERTAINTY_INFORMATION;
				break;
			}
			
			case UNCERTAINTY_INFORMATION:
			{
				paragraph = paragraph + uncertainty(ep.timeUncertainty.globalUncertainty)
						+ uncertaintyReason(ep.timeUncertainty.globalUncertainty, ep.timeUncertainty.totalTimeUncertaintyTM,
								ep.timeUncertainty.unpredictableTimeUncertaintyTM, ep.timeUncertainty.uncertaintyHM, ep.methodName);
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
	
	// Graph method - ms with visualization
	private String ms(double no)
	{
		NumberFormat formatter = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
		return " " + addProgressBarVis(no, executionProfile.methodCallsTime, "#FFD700", true, formatter.format(no) + " ms");
	}
	
	// Graph method - ms without visualization
	private String msNoVis(double no)
	{
		NumberFormat formatter = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
		return " " + formatter.format(no) + " ms";
	}
	
	// Graph method - infinite high uncertainty
	private String infiniteHighUncertainty(double callsTime)
	{
		return "took"+msNoVis(callsTime)+". Due to insufficient information, meaningful information regarding time cannot be concluded.";
	}
	
	// Graph method - uncertainty
	private String uncertainty(int level)
	{
		if(level>1)
		{
			return " Please note the measurements are uncertain due to short runtime of";
		}
		else
		{
			return "";
		}
	}
	
	// Graph - uncertainty reason
	private String uncertaintyReason(int level, int timeTM, int unprecTM, int highMethodTM, MethodName methodName)
	{
		if (level<2)
		{
			return "";
		}
		else
		{
			if((timeTM >1) && (unprecTM <2 && highMethodTM <2))
			{
				return " method" + " "+ addHighlightPopoverVis(methodName.onlyName, methodHighlightPopoverContent(methodName)) +".";
			}
			else if((timeTM >1) && (unprecTM >1 || highMethodTM >1))
			{
				return " method" + " "+ addHighlightPopoverVis(methodName.onlyName, methodHighlightPopoverContent(methodName)) + " and its outgoing calls.";
			}
			else if((timeTM <2) && (unprecTM >1 || highMethodTM >1))
			{
				return " outgoing calls.";
			}
			else
			{
				return "";
			}
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
	private String methodListHighlightPopupContent(int noOfMethods, Map.Entry<OutgoingMethodCall, Double> calledMethodsList[])
	{
		StringBuilder sb = new StringBuilder();
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		sb.append("<div style=\\\"width: 625px;\\\">");
		sb.append(
				"    <table border=\\\"1\\\" style=\\\"border-collapse: collapse; background-color:#DDDDDD; width: 100%;\\\">");
		sb.append("      <tr>");
		sb.append("        <td style=\\\"text-align: center; width: 20%; padding: 8px;\\\"><b>Time Consumption</b></td>");
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
			sb.append("   <td style=\\\"width: 20%; " + height + " padding: 2px 2px 2px 5px;\\\">" + addProgressBarVisInPopup(calledMethodsList[i].getValue(), executionProfile.methodCallsTime, "#FFD700", true, formatter.format(calledMethodsList[i].getValue()) + " ms") + "</td>");
			sb.append("   <td style=\\\"width: 80%; " + height + " padding: 2px 2px 2px 5px;\\\">" + addHighlightPopoverVisInPopup(calledMethodsList[i].getKey().methodName.fullShortName, methodHighlightPopoverInPopupContent(calledMethodsList[i].getKey().methodName)) + "</td>");
			sb.append("</tr>");
		}

		sb.append("   </table>");
		sb.append("</div>");

		return sb.toString();
	}
}
