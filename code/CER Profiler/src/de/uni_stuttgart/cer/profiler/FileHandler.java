// Declare package
package de.uni_stuttgart.cer.profiler;

// Import classes
import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// File handler to create, update and save profiling details into DOM file
public class FileHandler
{
	// Variables
	public static String filename;
	static DocumentBuilderFactory documentBuilderFactory;
	static DocumentBuilder documentBuilder;
	static Document document;
	static Element rootElement;
	static Element functionPropertiesElement;
	static Element totalOutgoingFunctionCallsElement;
	static Element executionElement;
	static Element functionSpecificationsElement;
	static Element functionNameElement;
	static Element directRecursionElement;
	static Element indirectRecursionElement;
	static Element stackTracesElement;
	static Element incomingFunctionCallsElement;
	public static HashMap<Integer, Element> stackTraceElements = new HashMap<Integer, Element>();
	
	// Create a file for profiling details
	public static boolean create(FunctionName functionName, FunctionModifier functionModifier, int startingLineNumber)
	{
		try
		{
			// Save the filename
			filename = functionName.onlyName;
			
			// Declare document builder for file
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();

			// Add root element "Profile" with namespace to the file
			document = documentBuilder.newDocument();
			rootElement = document.createElementNS("http://cer_profiler.uni-stuttgart.de", "Profile");
			document.appendChild(rootElement);
			
			// Add "Properties" element for general information with properties inside
			functionPropertiesElement = document.createElement("FunctionProperties");
			rootElement.appendChild(functionPropertiesElement);
			Element funcNameElement = document.createElement("FunctionName");
			functionPropertiesElement.appendChild(funcNameElement);
			funcNameElement.setTextContent(functionName.fullDetailedLongNameWithReturn);
			if(functionName.isConstructor)
			{
				funcNameElement.setAttribute("FunctionType", "Constructor");
			}
			else
			{
				funcNameElement.setAttribute("FunctionType", "Normal");
			}
			Element funcModifierElement = document.createElement("FunctionModifier");
			functionPropertiesElement.appendChild(funcModifierElement);
			funcModifierElement.setTextContent(functionModifier.accessModifier);
			if(functionModifier.isStatic)
			{
				funcModifierElement.setAttribute("Static", "Yes");
			}
			else
			{
				funcModifierElement.setAttribute("Static", "No");
			}
			if(functionModifier.isFinal)
			{
				funcModifierElement.setAttribute("Final", "Yes");
			}
			else
			{
				funcModifierElement.setAttribute("Final", "No");
			}
			if(functionModifier.isNative)
			{
				funcModifierElement.setAttribute("Native", "Yes");
			}
			else
			{
				funcModifierElement.setAttribute("Native", "No");
			}
			if(functionModifier.isInterface)
			{
				funcModifierElement.setAttribute("Interface", "Yes");
			}
			else
			{
				funcModifierElement.setAttribute("Interface", "No");
			}
			if(functionModifier.isAbstract)
			{
				funcModifierElement.setAttribute("Abstract", "Yes");
			}
			else
			{
				funcModifierElement.setAttribute("Abstract", "No");
			}
			Element startingLineNumberElement = document.createElement("StartingLineNumber");
			functionPropertiesElement.appendChild(startingLineNumberElement);
			startingLineNumberElement.setTextContent(Integer.toString(startingLineNumber));
			totalOutgoingFunctionCallsElement = document.createElement("TotalOutgoingFunctionCalls");
			functionPropertiesElement.appendChild(totalOutgoingFunctionCallsElement);
			
			// Add "Execution" element for execution behaviour information
			executionElement = document.createElement("Execution");
			rootElement.appendChild(executionElement);
			
			// Add "Specifications" element with specifications inside
			functionSpecificationsElement = document.createElement("FunctionSpecifications");
			executionElement.appendChild(functionSpecificationsElement);
			functionNameElement = document.createElement("FunctionName");
			functionSpecificationsElement.appendChild(functionNameElement);
			functionNameElement.setTextContent(functionName.fullDetailedLongNameWithReturn);
			if(functionName.isConstructor)
			{
				functionNameElement.setAttribute("FunctionType", "Constructor");
			}
			else
			{
				functionNameElement.setAttribute("FunctionType", "Normal");
			}
			functionNameElement.setAttribute("Frequency", "0");	
			functionNameElement.setAttribute("NonRecursiveFrequency", "0");	
			directRecursionElement = document.createElement("DirectRecursion");
			functionSpecificationsElement.appendChild(directRecursionElement);
			directRecursionElement.setTextContent("No");
			directRecursionElement.setAttribute("Frequency", "0");
			indirectRecursionElement = document.createElement("IndirectRecursion");
			functionSpecificationsElement.appendChild(indirectRecursionElement);
			indirectRecursionElement.setTextContent("No");
			indirectRecursionElement.setAttribute("Frequency", "0");
			
			// Add "StackTraces" element to save stack traces
			stackTracesElement = document.createElement("StackTraces");
			executionElement.appendChild(stackTracesElement);
			
			// Add "IncomingFunctionCalls" element to save incoming function calls
			incomingFunctionCallsElement = document.createElement("IncomingFunctionCalls");
			executionElement.appendChild(incomingFunctionCallsElement);
			
			// Save the file
			save();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}

		return true;
	}
	
	// Save profiling details to the file
	public static void save()
	{
		try
		{			
			// Ident and save as XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(filename+".xml"));
			transformer.transform(source, result);
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
	}
	
	// Change values of all elements of specific name in memory
	public static void changeElementValue(String elementName, String value)
	{
		// Get all the elements by name and update their value
		NodeList nodeListElements = document.getElementsByTagName(elementName);
		for (int i = 0; i < nodeListElements.getLength(); i++)
		{
			Element nodeListElement = (Element) nodeListElements.item(i);
			nodeListElement.setTextContent(value);
		}
	}
	
	// Update values of all elements of specific name in file
	public static void updateElementValue(String elementName, String value)
	{
		// Get all the elements by name and update their value
		NodeList nodeListElements = document.getElementsByTagName(elementName);
		for(int i=0; i<nodeListElements.getLength(); i++)
		{
			Element nodeListElement = (Element) nodeListElements.item(i);
			nodeListElement.setTextContent(value);
		}
		
		// Save the file
		save();
	}
	
	// Update file with profiling details after target function has terminated
	public static void targetFunctionUpdate(int totalFrequency, int specificFrequency, boolean newStackTrace, StackTrace stackTrace, IncomingFunctionCall incomingFunctionCall, long overheadStartTime)
	{
		// Update the total frequency
		functionNameElement.setAttribute("Frequency", Integer.toString(totalFrequency));

		// Update the appropriate frequency
		if (incomingFunctionCall.callType == 0)
		{
			functionNameElement.setAttribute("NonRecursiveFrequency", Integer.toString(specificFrequency));
		}
		else if (incomingFunctionCall.callType == 1)
		{
			directRecursionElement.setAttribute("Frequency", Integer.toString(specificFrequency));
			directRecursionElement.setTextContent("Yes");
		}
		else
		{
			indirectRecursionElement.setAttribute("Frequency", Integer.toString(specificFrequency));
			indirectRecursionElement.setTextContent("Yes");
		}

		// If it is the new stack trace, add it
		if (newStackTrace)
		{
			// Create stack trace element and add id and frequency attributes
			Element stackTraceElement = document.createElement("StackTrace");
			stackTracesElement.appendChild(stackTraceElement);
			stackTraceElements.put(stackTrace.id, stackTraceElement);
			stackTraceElement.setAttribute("Frequency", Integer.toString(stackTrace.frequency));
			stackTraceElement.setAttribute("ID", Integer.toString(stackTrace.id));
			if(stackTrace.functionCaller.functionName.fullExists)
			{
				stackTraceElement.setAttribute("FunctionCaller", stackTrace.functionCaller.functionName.fullDetailedLongNameWithReturn);
			}
			else
			{
				stackTraceElement.setAttribute("FunctionCaller", stackTrace.functionCaller.functionName.longName);
			}
			if(stackTrace.functionCaller.functionName.isConstructor)
			{
				stackTraceElement.setAttribute("FunctionType", "Constructor");
			}
			else
			{
				stackTraceElement.setAttribute("FunctionType", "Normal");
			}


			// Add function stackTrace
			for (int i = 0; i < stackTrace.stackTrace.length; i++)
			{
				Element functionElement = document.createElement("Function");
				stackTraceElement.appendChild(functionElement);
				functionElement.setTextContent(stackTrace.stackTrace[i]);
			}
		}

		// If stack trace already exists, update frequency
		else
		{
			if (incomingFunctionCall.callType == 0)
			{
				stackTraceElements.get(stackTrace.id).setAttribute("Frequency", Integer.toString(stackTrace.frequency));
			}
		}
		
		// Add incoming function call information
		Element incomingFunctionCallElement = document.createElement("IncomingFunctionCall");
		incomingFunctionCallsElement.appendChild(incomingFunctionCallElement);
		incomingFunctionCallElement.setAttribute("ID", Integer.toString(incomingFunctionCall.id));
		incomingFunctionCallElement.setAttribute("Depth", Integer.toString(incomingFunctionCall.depth));
		
		// Update the appropriate call type
		if (incomingFunctionCall.callType == 0)
		{
			incomingFunctionCallElement.setAttribute("CallType", "Normal");
		}
		else if (incomingFunctionCall.callType == 1)
		{
			incomingFunctionCallElement.setAttribute("CallType", "DirectRecursive");
		}
		else
		{
			incomingFunctionCallElement.setAttribute("CallType", "InDirectRecursive");
		}
		
		// Update the appropriate level type
		if (incomingFunctionCall.levelType == 0)
		{
			incomingFunctionCallElement.setAttribute("LevelType", "Root");
		}
		else if (incomingFunctionCall.levelType == 1)
		{
			incomingFunctionCallElement.setAttribute("LevelType", "Middle");
		}
		else
		{
			incomingFunctionCallElement.setAttribute("LevelType", "Leaf");
		}

		// Add outgoing function calls information
		Element outgoingFunctionCallsElement = document.createElement("OutgoingFunctionCalls");
		incomingFunctionCallElement.appendChild(outgoingFunctionCallsElement);
		outgoingFunctionCallsElement.setAttribute("Count", Integer.toString(incomingFunctionCall.outgoingFunctionCallsData.size()));
		for (OutgoingFunctionCallData outgoingFunctionCallData : incomingFunctionCall.outgoingFunctionCallsData.values())
		{
			Element outgoingFunctionCallElement = document.createElement("OutgoingFunctionCall");
			outgoingFunctionCallsElement.appendChild(outgoingFunctionCallElement);
			outgoingFunctionCallElement.setAttribute("ID", Integer.toString(outgoingFunctionCallData.outgoingFunctionCall.id));
			if(outgoingFunctionCallData.timeAvailable)
			{
				outgoingFunctionCallElement.setAttribute("Time", Long.toString(outgoingFunctionCallData.consumedTime));
			}
			else
			{
				outgoingFunctionCallElement.setAttribute("Time", "NA");
			}
			//outgoingFunctionCallElement.setTextContent(outgoingFunctionCall.functionName.longName);
		}
		
		// Time calculation and reocrding
		if(incomingFunctionCall.depth==0)
		{
			incomingFunctionCall.overheadTime = incomingFunctionCall.overheadTime + (System.nanoTime() - overheadStartTime);
			incomingFunctionCall.endTime = System.nanoTime();
			incomingFunctionCall.consumedTime = incomingFunctionCall.endTime - incomingFunctionCall.startTime - incomingFunctionCall.overheadTime;
			incomingFunctionCallElement.setAttribute("Time", Long.toString(incomingFunctionCall.consumedTime));
		}
		
		// Save the file
		save();
	}
	
	// Add function properties in the file
	public static void addFunctionProperties(HashMap<Integer, OutgoingFunctionCall> totalOutgoingFunctionCalls)
	{
		// Add total outgoing function calls in the code before exeuction
		totalOutgoingFunctionCallsElement.setAttribute("Count", Integer.toString(totalOutgoingFunctionCalls.size()));
		
		for (OutgoingFunctionCall outgoingFunctionCall : totalOutgoingFunctionCalls.values())
		{
			Element outgoingFunctionCallElement = document.createElement("OutgoingFunctionCall");
			totalOutgoingFunctionCallsElement.appendChild(outgoingFunctionCallElement);
			outgoingFunctionCallElement.setTextContent(outgoingFunctionCall.functionName.fullDetailedLongNameWithReturn);
			outgoingFunctionCallElement.setAttribute("ID", Integer.toString(outgoingFunctionCall.id));
			if(outgoingFunctionCall.functionName.isConstructor)
			{
				outgoingFunctionCallElement.setAttribute("FunctionType", "Constructor");
			}
			else
			{
				outgoingFunctionCallElement.setAttribute("FunctionType", "Normal");

			}
		}

		// Save the file
		save();
	}
}