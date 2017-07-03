// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// File parser to open and retrieve profiling data from a DOM file
public class FileParser
{
	// Variables
	File file;
	DocumentBuilderFactory documentBuilderFactory;
	DocumentBuilder documentBuilder;
	Document document;
	boolean fileOpened;
	String sourceCodePath;
	
	// Open and load the file
	FileParser(String pathWithFile, String codePath)
	{
		try
		{
			// Open the DOM file
			file = new File(pathWithFile);
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(file);
			fileOpened = true;
			sourceCodePath = codePath;
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			fileOpened = false;
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			fileOpened = false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fileOpened = false;
		}
	}
	
	// Parse the file and create an execution profile
	public ExecutionProfile getProfile()
	{
		// If file is opened
		if(fileOpened)
		{
			// Retrieve the data from file
			document.getDocumentElement().normalize();
			
			// Get properties data
			NodeList methodPropertiesNode = document.getElementsByTagName("FunctionProperties");
			Element methodPropertiesElement = (Element) methodPropertiesNode.item(0);
			Element methodNameElement = (Element) methodPropertiesElement.getElementsByTagName("FunctionName").item(0); 
			String methodNameString = methodNameElement.getTextContent();
			String methodType = methodNameElement.getAttribute("FunctionType");
			Boolean isConstructor = false;
			if(methodType.equals("Constructor"))
			{
				isConstructor = true;
			}
			Element methodModifierElement = (Element) methodPropertiesElement.getElementsByTagName("FunctionModifier").item(0);
			String accessModifier = methodModifierElement.getTextContent();
			String staticStr = methodModifierElement.getAttribute("Static");
			String finalStr = methodModifierElement.getAttribute("Final");
			String nativeStr = methodModifierElement.getAttribute("Native");
			String interfaceStr = methodModifierElement.getAttribute("Interface");
			String abstractStr = methodModifierElement.getAttribute("Abstract");
			MethodModifier methodModifier = new MethodModifier(accessModifier, staticStr, finalStr, nativeStr, interfaceStr, abstractStr);
			Element startingLineNumberElement = (Element) methodPropertiesElement.getElementsByTagName("StartingLineNumber").item(0);
			String startingLineNumber = startingLineNumberElement.getTextContent();
			int startLineNumber = Integer.parseInt(startingLineNumber);
			
			// Create an execution profile of the retrieved data
			ExecutionProfile executionProfile = new ExecutionProfile(methodNameString, isConstructor, methodModifier, startLineNumber, sourceCodePath);
			
			// Get and set properties data
			Element totalOutgoingMethodCalls = (Element) methodPropertiesElement.getElementsByTagName("TotalOutgoingFunctionCalls").item(0);
			NodeList outgoingMethodCallNodes = totalOutgoingMethodCalls.getElementsByTagName("OutgoingFunctionCall");
			for(int i=0; i<outgoingMethodCallNodes.getLength(); i++)
			{
				Element outgoingMethodCallElement = (Element) outgoingMethodCallNodes.item(i);
				boolean isCons = false;
				if(outgoingMethodCallElement.getAttribute("FunctionType").equals("Constructor"))
				{
					isCons = true;
				}
				MethodName outgoingMethodCallName = MethodName.createMethodName(outgoingMethodCallElement.getTextContent(), isCons, executionProfile);
				OutgoingMethodCall outgoingMethodCall = new OutgoingMethodCall(Integer.parseInt(outgoingMethodCallElement.getAttribute("ID")), outgoingMethodCallName);
				executionProfile.addTotalOutgoingMethodCall(outgoingMethodCall);
			}
			
			// Get execution data
			Element methodExecutionElement = (Element) document.getElementsByTagName("Execution").item(0);
			
			// Get and set specifications data
			Element methodSpecificationsElement = (Element) methodExecutionElement.getElementsByTagName("FunctionSpecifications").item(0);
			Element methodNmElement = (Element) methodSpecificationsElement.getElementsByTagName("FunctionName").item(0);
			Element directRecursionElement = (Element) methodSpecificationsElement.getElementsByTagName("DirectRecursion").item(0);
			Element indirectRecursionElement = (Element) methodSpecificationsElement.getElementsByTagName("IndirectRecursion").item(0);
			
			int totalFrequency = Integer.parseInt(methodNmElement.getAttribute("Frequency"));
			int nonRecursionFrequency = Integer.parseInt(methodNmElement.getAttribute("NonRecursiveFrequency"));
			int indirectRecursionFrequency = 0;
			int directRecursionFrequency = 0;
			boolean directRecursion = false;
			boolean indirectRecursion = false;
			if(directRecursionElement.getTextContent().equals("Yes"))
			{
				directRecursion = true;
				directRecursionFrequency = Integer.parseInt(directRecursionElement.getAttribute("Frequency"));
			}
			if(indirectRecursionElement.getTextContent().equals("Yes"))
			{
				indirectRecursion = true;
				indirectRecursionFrequency = Integer.parseInt(indirectRecursionElement.getAttribute("Frequency"));
			}
			executionProfile.setCallFrequencies(totalFrequency, nonRecursionFrequency, directRecursionFrequency, indirectRecursionFrequency);
			executionProfile.setRecursionValues(directRecursion, indirectRecursion);
			
			// Get and set callgraph data
			Element callGraphs = (Element) methodExecutionElement.getElementsByTagName("StackTraces").item(0);
			NodeList callGraphNodes = callGraphs.getElementsByTagName("StackTrace");
			for(int i=0; i<callGraphNodes.getLength(); i++)
			{
				Element callGraphElement = (Element) callGraphNodes.item(i);
				String methodCallerNameString = callGraphElement.getAttribute("FunctionCaller");
				int callGraphFrequency =  Integer.parseInt(callGraphElement.getAttribute("Frequency"));
				int callGraphID =  Integer.parseInt(callGraphElement.getAttribute("ID"));
				boolean isCons = false;
				if(callGraphElement.getAttribute("FunctionType").equals("Constructor"))
				{
					isCons = true;
				}
				
				MethodName methodCallerName = MethodName.createMethodName(methodCallerNameString, isCons, executionProfile);
				
				NodeList methodNodes = callGraphElement.getElementsByTagName("Function");
				String[] callGraphString = new String[methodNodes.getLength()];
				for(int j=0; j<methodNodes.getLength(); j++)
				{
					callGraphString[j] = methodNodes.item(j).getTextContent();
				}
				MethodCaller methodCaller = executionProfile.addTotalMethodCaller(methodCallerName);
				CallGraph callGraph = new CallGraph(callGraphID, callGraphString, methodCaller, callGraphFrequency);
				executionProfile.addCallGraph(callGraph);
			}
			
			// Get and set incoming method calls
			Element incomingMethodCallsElement = (Element) methodExecutionElement.getElementsByTagName("IncomingFunctionCalls").item(0);
			NodeList incomingMethodCallNodes = incomingMethodCallsElement.getElementsByTagName("IncomingFunctionCall");
			for(int i=0; i<incomingMethodCallNodes.getLength(); i++)
			{
				Element incomingMethodCallElement = (Element) incomingMethodCallNodes.item(i);
				int incomingMethodCallID =  Integer.parseInt(incomingMethodCallElement.getAttribute("ID"));
				int incomingMethodCallDepth =  Integer.parseInt(incomingMethodCallElement.getAttribute("Depth"));
				String callTypeString = incomingMethodCallElement.getAttribute("CallType");
				int callType = 0;
				if(callTypeString.equals("Normal"))
				{
					callType = 0;
				}
				else if(callTypeString.equals("DirectRecursive"))
				{
					callType = 1;
				}
				else
				{
					callType = 2;
				}
				String levelTypeString = incomingMethodCallElement.getAttribute("LevelType");
				int levelType = 0;
				if(levelTypeString.equals("Root"))
				{
					levelType = 0;
					String timeString = incomingMethodCallElement.getAttribute("Time");
					executionProfile.addMethodTime(Long.parseLong(timeString));
				}
				else if(levelTypeString.equals("Middle"))
				{
					levelType = 1;
				}
				else
				{
					levelType = 2;
				}
				
				IncomingMethodCall incomingMethodCall = new IncomingMethodCall(incomingMethodCallID, callType, levelType, incomingMethodCallDepth);
				executionProfile.addIncomingMethodCall(incomingMethodCall);
				
				// Add outgoing method calls related to incoming method call
				NodeList outgoingMtdCallNodes = incomingMethodCallElement.getElementsByTagName("OutgoingFunctionCall");
				for(int j=0; j<outgoingMtdCallNodes.getLength(); j++)
				{
					Element outgoingMtdCallElement = (Element) outgoingMtdCallNodes.item(j);					
					int outgoingMtdCallID = Integer.parseInt(outgoingMtdCallElement.getAttribute("ID"));
					String timeString  = outgoingMtdCallElement.getAttribute("Time");
					long outgoingMtdCallTime;
					if(timeString.equals("NA"))
					{
						outgoingMtdCallTime = -1L;
					}
					else
					{
						outgoingMtdCallTime = Long.parseLong(timeString);
					}
					executionProfile.addOutgoingMethodCall(incomingMethodCall.id, outgoingMtdCallID, outgoingMtdCallTime);
				}
			}
			
			// Process the data in execution profile
			executionProfile.processExecutionProfile();
					
			return executionProfile;
		}
		else
		{
			return null;
		}
	}
}
