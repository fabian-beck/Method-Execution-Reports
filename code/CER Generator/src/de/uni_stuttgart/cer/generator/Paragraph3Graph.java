// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.Map;

// Paragraph 3 graph for recursion depth
public class Paragraph3Graph extends Paragraph
{
	// Variables
	public ExecutionProfile executionProfile;
	
	// NodeType
	enum NodeType
    {
		NONE,
		START,
		RECURSION,
		METHOD_NAME,
		DEPTH_LEVELS_COUNT,
		ONE_DEPTH_LEVEL,
		MANY_DEPTH_LEVELS,
		STOP
	 };
    
	// Create paragraph
	Paragraph3Graph(ExecutionProfile executionProfile)
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
				nextNode = NodeType.RECURSION;
				break;
			}
			
			case RECURSION:
			{
				if(ep.methodSpecifications.directRecursion == true || ep.methodSpecifications.indirectRecursion == true)
				{
					nextNode = NodeType.METHOD_NAME;
				}
				else
				{
					nextNode = NodeType.STOP;
				}
				break;
			}
			
			case METHOD_NAME:
			{
				paragraph = paragraph + "Recursion depth of method" + " "
						+ addHighlightPopoverVis(ep.methodName.onlyName, methodHighlightPopoverContent(ep.methodName));
				nextNode = NodeType.DEPTH_LEVELS_COUNT;
				break;
			}
			
			case DEPTH_LEVELS_COUNT:
			{
				if(ep.recursionDepthOccurrence.length == 1)
				{
					nextNode = NodeType.ONE_DEPTH_LEVEL;
				}
				else
				{
					nextNode = NodeType.MANY_DEPTH_LEVELS;
				}
				break;
			}
			
			case ONE_DEPTH_LEVEL:
			{
				paragraph = paragraph + " went to only 1 level.";
				nextNode = NodeType.STOP;
				break;
			}
			
			case MANY_DEPTH_LEVELS:
			{
				double values[] = new double[ep.recursionDepthOccurrence.length];
				String labels[] = new String[ep.recursionDepthOccurrence.length];
				for(int i=0; i<ep.recursionDepthOccurrence.length; i++)
				{
					values[ep.recursionDepthOccurrence[i].getKey()-1] = ep.recursionDepthOccurrence[i].getValue();
					labels[ep.recursionDepthOccurrence[i].getKey()-1] = "Depth "+ep.recursionDepthOccurrence[i].getKey()+": "+ep.recursionDepthOccurrence[i].getValue();
				}
				paragraph = paragraph + " went up to " + addBarChartVis(values, labels, "#FF004F", "#FFA6C9",
						"Recursion Depth Histogram", true, ep.recursionDepthOccurrence.length + " levels") + "."
						+ mostDepth(ep.recursionDepthOccurrence);
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
	
	// Graph method - most depth
	private String mostDepth(Map.Entry<Integer, Integer> recursionDepthOccurrence[])
	{
		int length = recursionDepthOccurrence.length;
		int maxCount = recursionDepthOccurrence[length-1].getValue();
		int maxReached = 1;
		for(int i=length-2; i >= 0; i--)
		{
			if(maxCount == recursionDepthOccurrence[i].getValue())
			{
				maxReached++;
			}
			else
			{
				break;
			}
		}
		if(maxReached == 1)
		{
			return " It reached depth level "+recursionDepthOccurrence[length-1].getKey()+" maximum times which was "+recursionDepthOccurrence[length-1].getValue()+".";
		}
		else
		{
			return "";
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
}
