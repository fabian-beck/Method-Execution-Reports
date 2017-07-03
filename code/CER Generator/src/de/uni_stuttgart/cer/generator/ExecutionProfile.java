// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

// Execution profile containing the profiling data of the code
public class ExecutionProfile
{
	// Variables (method information)
	public MethodName methodName;
	public MethodProperties methodProperties;	
	public MethodSpecifications methodSpecifications;
	public HashMap<Integer, CallGraph> callGraphs;
	public HashMap<Integer, IncomingMethodCall> incomingMethodCalls;
	public int outgoingMethodCallsNRSum;
	public int outgoingMethodCallsSum;
	public boolean entryPointCall;
	
	// Variables (method processed information)
	public Map.Entry<MethodCaller, Integer> methodCallersOccurrence[];
	public Map.Entry<OutgoingMethodCall, Integer> outgoingMethodCallsOccurrence[];
	public Map.Entry<Integer, Integer> recursionDepthOccurrence[];
	public Map.Entry<Integer, Integer> directRecursionDepthOccurrence[];
	public Map.Entry<Integer, Integer> indirectRecursionDepthOccurrence[];
	public Map.Entry<OutgoingMethodCall, Double> outgoingMethodCallsTime[];
	public boolean isSameClassMethodCallers;
	public boolean isSameClassOutgoingMethodCalls;
	public boolean isFromOutside;
	public boolean allToOutside;
	public double methodCallsTime;
	public double totalOutgoingMethodCallsTime;
	public TimeUncertainty timeUncertainty;
	
	// Variables (method data)
	public HashMap<String, MethodName> allMethods; 
	public HashMap<MethodCaller, Integer> methodCallersCount;
	public Map<OutgoingMethodCall, Integer> outgoingMethodCallsCount;
	public Map<Integer, Integer> recursionDepthCount;
	public Map<Integer, Integer> directRecursionDepthCount;
	public Map<Integer, Integer> indirectRecursionDepthCount;
	public Map<OutgoingMethodCall, Double> outgoingMethodCallsDuration;
	public Map<OutgoingMethodCall, Double> flaggedOutgoingMethodCalls;
	public int outgoingMethodCallsCounter;
	public MethodNameLevel methodNameLevel;
	public String sourceCodePath;
	public String sourceCode;
	public int linesDifference;
	
	// Create execution profile
	ExecutionProfile(String methodNameString, boolean isConstructor, MethodModifier methodModifier, int startingLineNumber, String codePath)
	{
		// Initialize Variables
		allMethods = new HashMap<String, MethodName>();
		methodNameLevel = new MethodNameLevel();
		methodName = MethodName.createMethodName(methodNameString, isConstructor, this);
		methodProperties = new MethodProperties(methodName, methodModifier, startingLineNumber);
		methodSpecifications = new MethodSpecifications(methodName);
		callGraphs = new HashMap<Integer, CallGraph>();
		incomingMethodCalls = new HashMap<Integer, IncomingMethodCall>();
		methodCallersCount = new HashMap<MethodCaller, Integer>();
		outgoingMethodCallsCount = new HashMap<OutgoingMethodCall, Integer>();
		recursionDepthCount = new HashMap<Integer, Integer>();
		directRecursionDepthCount  = new HashMap<Integer, Integer>();
		indirectRecursionDepthCount = new HashMap<Integer, Integer>();
		outgoingMethodCallsDuration = new HashMap<OutgoingMethodCall, Double>();
		flaggedOutgoingMethodCalls = new HashMap<OutgoingMethodCall, Double>();
		entryPointCall = false;
		outgoingMethodCallsCounter = 1;
		outgoingMethodCallsNRSum = 0;
		outgoingMethodCallsSum = 0;
		methodCallsTime = 0;
		totalOutgoingMethodCallsTime = 0;
		linesDifference = -1;

		// Create path for target method Java class
		sourceCodePath = codePath;
		sourceCodePath += "/src/";
		sourceCodePath += (methodName.onlyClassPackageName).replace(".", "/")+".java";
	}
	
	// Set the call frequencies
	public void setCallFrequencies(int totalFrequency, int nonRecursionFrequency, int directRecursionFrequency, int indirectRecursionFrequency)
	{
		methodSpecifications.totalFrequency = totalFrequency;
		methodSpecifications.nonRecursionFrequency = nonRecursionFrequency;
		methodSpecifications.directRecursionFrequency = directRecursionFrequency;
		methodSpecifications.indirectRecursionFrequency = indirectRecursionFrequency;
	}
	
	// Set recursion values
	public void setRecursionValues(boolean directRecursion, boolean indirectRecursion)
	{
		methodSpecifications.directRecursion = directRecursion;
		methodSpecifications.indirectRecursion = indirectRecursion;
	}
	
	// Add total method caller
	public MethodCaller addTotalMethodCaller(MethodName methodName)
	{
		// If method caller is JVM return null
		if(methodName.longName.equals("de.uni_stuttgart.cer.none"))
		{
			entryPointCall = true;
			return null;
		}
		
		// If method caller is not JVM then add method caller
		else
		{
			return methodProperties.addTotalMethodCaller(methodName);
		}
	}
	
	// Add a call graph
	public void addCallGraph(CallGraph callGraph)
	{
		// Add callgraph to the list
		callGraphs.put(callGraph.id, callGraph);
		
		if(callGraph.methodCaller != null)
		{
			// Get the method caller and add to method callers
			MethodCaller methodCaller = methodProperties.totalMethodCallers.get(callGraph.methodCaller.id);
					
			// Count the total method calls
			if (methodCallersCount.containsKey(methodCaller))
			{
				int count = methodCallersCount.get(methodCaller);
				count = count + callGraph.frequency;
				methodCallersCount.replace(methodCaller, count);
			}
			else
			{
				methodCallersCount.put(methodCaller, callGraph.frequency);
			}
		}
	}
	
	// Add incoming method call
	public void addIncomingMethodCall(IncomingMethodCall incomingMethodCall)
	{
		// Add incoming method call to the list
		incomingMethodCalls.put(incomingMethodCall.id, incomingMethodCall);
		
		// Count the depth
		if(incomingMethodCall.callType == 1 || incomingMethodCall.callType == 2)
		{
			if (recursionDepthCount.containsKey(incomingMethodCall.depth))
			{
				int countOfDepth = recursionDepthCount.get(incomingMethodCall.depth);
				countOfDepth++;
				recursionDepthCount.replace(incomingMethodCall.depth, countOfDepth);
			}
			else
			{
				recursionDepthCount.put(incomingMethodCall.depth, 1);
			}
		}
		
		if(incomingMethodCall.callType == 1)
		{
			if (directRecursionDepthCount.containsKey(incomingMethodCall.depth))
			{
				int count = directRecursionDepthCount.get(incomingMethodCall.depth);
				count++;
				directRecursionDepthCount.replace(incomingMethodCall.depth, count);
			}
			else
			{
				directRecursionDepthCount.put(incomingMethodCall.depth, 1);
			}
		}
		else if (incomingMethodCall.callType == 2)
		{
			if (indirectRecursionDepthCount.containsKey(incomingMethodCall.depth))
			{
				int count = indirectRecursionDepthCount.get(incomingMethodCall.depth);
				count++;
				indirectRecursionDepthCount.replace(incomingMethodCall.depth, count);
			}
			else
			{
				indirectRecursionDepthCount.put(incomingMethodCall.depth, 1);
			}
		}
	}
	
	// Add total outgoing method call
	public void addTotalOutgoingMethodCall(OutgoingMethodCall outgoingMethodCall)
	{
		methodProperties.addTotalOutgoingMethodCall(outgoingMethodCall);
	}
	
	// Add outgoing method call to respective incoming method call
	public void addOutgoingMethodCall(int incomingMethodCallID, int outgoingMethodCallID, long methodTime)
	{
		// Get the outgoing method call
		OutgoingMethodCall outgoingMethodCall = methodProperties.totalOutgoingMethodCalls.get(outgoingMethodCallID);
		
		// Add outgoing method call to respective incoming method call
		incomingMethodCalls.get(incomingMethodCallID).outgoingMethodCalls.put(outgoingMethodCallsCounter, outgoingMethodCall);
		outgoingMethodCallsCounter++;
		
		// Count the outgoing method calls
		outgoingMethodCallsSum++;
		
		// Exclude the direct recursive outgoing call
		if(!(outgoingMethodCall.methodName == methodName))
		{
			outgoingMethodCallsNRSum++;
			if(outgoingMethodCallsCount.containsKey(outgoingMethodCall))
			{
				int count = outgoingMethodCallsCount.get(outgoingMethodCall);
				count++;
				outgoingMethodCallsCount.replace(outgoingMethodCall, count);
			}
			else
			{
				outgoingMethodCallsCount.put(outgoingMethodCall, 1);
			}
		}
		
		// Calculate time for outgoing method call
		if(methodTime != -1L)
		{
			Double outgoingMtdCallTime = 0.0;
			Double methodTimeMS = methodTime/1000000.0;
			totalOutgoingMethodCallsTime = totalOutgoingMethodCallsTime + methodTimeMS;
			
			// If a single method call drops below threshold, flag it and calculate its total time
			if(methodTime <= 1000)
			{
				if(!flaggedOutgoingMethodCalls.containsKey(outgoingMethodCall))
				{
					flaggedOutgoingMethodCalls.put(outgoingMethodCall, 0.0);
				}
			}
			
			if(outgoingMethodCallsDuration.containsKey(outgoingMethodCall))
			{
				outgoingMtdCallTime = outgoingMethodCallsDuration.get(outgoingMethodCall);
				outgoingMtdCallTime = outgoingMtdCallTime + methodTimeMS;
				outgoingMethodCallsDuration.replace(outgoingMethodCall, outgoingMtdCallTime);
			}
			else
			{
				outgoingMethodCallsDuration.put(outgoingMethodCall, methodTimeMS);
			}
		}
	}
	
	// Add method time (for root nodes)
	public void addMethodTime(Long nanoSecondsTime)
	{
		methodCallsTime = methodCallsTime + (nanoSecondsTime/1000000.0);
	}
	
	// Process the gathered data from file and convert in to meaning information 
	@SuppressWarnings("unchecked")
	void processExecutionProfile()
	{
		// Sort counting values and convert them to array
		SortedSet<Map.Entry<MethodCaller, Integer>> methodCallersSCount = mapToSortedSet(methodCallersCount);
		methodCallersOccurrence = new Map.Entry[methodCallersSCount.size()];
		methodCallersOccurrence = methodCallersSCount.toArray(methodCallersOccurrence);

		SortedSet<Map.Entry<OutgoingMethodCall, Integer>> outgoingMethodCallsSCount = mapToSortedSet(outgoingMethodCallsCount);
		outgoingMethodCallsOccurrence = new Map.Entry[outgoingMethodCallsSCount.size()];
		outgoingMethodCallsOccurrence = outgoingMethodCallsSCount.toArray(outgoingMethodCallsOccurrence); 

		SortedSet<Map.Entry<Integer, Integer>> recursionDepthSCount = mapToSortedSet(recursionDepthCount);
		recursionDepthOccurrence = new Map.Entry[recursionDepthSCount.size()];
		recursionDepthOccurrence = recursionDepthSCount.toArray(recursionDepthOccurrence);
		
		SortedSet<Map.Entry<Integer, Integer>> directRecursionDepthSCount = mapToSortedSet(directRecursionDepthCount);
		directRecursionDepthOccurrence = new Map.Entry[directRecursionDepthSCount.size()];
		directRecursionDepthOccurrence = directRecursionDepthSCount.toArray(directRecursionDepthOccurrence);

		SortedSet<Map.Entry<Integer, Integer>> indirectRecursionDepthSCount = mapToSortedSet(indirectRecursionDepthCount);
		indirectRecursionDepthOccurrence = new Map.Entry[indirectRecursionDepthSCount.size()];
		indirectRecursionDepthOccurrence = indirectRecursionDepthSCount.toArray(indirectRecursionDepthOccurrence);
		
		SortedSet<Map.Entry<OutgoingMethodCall, Double>> outgoingMethodCallsSDuration = mapToSortedSet(outgoingMethodCallsDuration);
		outgoingMethodCallsTime = new Map.Entry[outgoingMethodCallsSDuration.size()];
		outgoingMethodCallsTime = outgoingMethodCallsSDuration.toArray(outgoingMethodCallsTime);
		
		// Check if all method callers belong to the same class or not
		isSameClassMethodCallers = true;
		String compareMethodCallersClass = "";
		if(methodCallersCount.size() > 1)
		{
			compareMethodCallersClass = "";
			boolean firstLoop = true;
			for(int i=0; i<methodCallersOccurrence.length; i++)
			{
				MethodCaller methodCaller = methodCallersOccurrence[i].getKey();
				
				if(firstLoop)
				{
					firstLoop = false;
					compareMethodCallersClass = methodCaller.methodName.onlyClassPackageName;
				}
				else
				{
					if(!methodCaller.methodName.onlyClassPackageName.equals(compareMethodCallersClass))
					{
						isSameClassMethodCallers = false;
						break;
					}
				}
			}
		}
		else
		{
			isSameClassMethodCallers = false;
		}
		
		// Check if all method callers belong to the class of target method
		if (methodCallersCount.size() > 1)
		{
			if (isSameClassMethodCallers)
			{
				if (methodName.onlyClassPackageName.equals(compareMethodCallersClass))
				{
					isFromOutside = false;
				}
				else
				{
					isFromOutside = true;
				}
			}
			else
			{
				isFromOutside = true;
			}
		}
		else if (methodCallersCount.size() == 1)
		{
			if(methodCallersOccurrence[0].getKey().methodName.onlyClassPackageName.equals(methodName.onlyClassPackageName))
			{
				isFromOutside = false;
			}
			else
			{
				isFromOutside = true;
			}
		}
		else
		{
			isFromOutside = false;
		}
		
		// Check if all methods called belong to the same class or not
		isSameClassOutgoingMethodCalls = true;
		String compareMethodsCalledClass = "";
		if (outgoingMethodCallsCount.size() > 1)
		{
			compareMethodsCalledClass = "";
			boolean firstLoop = true;
			for (int i = 0; i < outgoingMethodCallsOccurrence.length; i++)
			{
				OutgoingMethodCall outgoingMethodCall = outgoingMethodCallsOccurrence[i].getKey();

				if (firstLoop)
				{
					firstLoop = false;
					compareMethodsCalledClass = outgoingMethodCall.methodName.onlyClassPackageName;
				}
				else
				{
					if (!outgoingMethodCall.methodName.onlyClassPackageName.equals(compareMethodsCalledClass))
					{
						isSameClassOutgoingMethodCalls = false;
						break;
					}
				}
			}
		}
		else
		{
			isSameClassOutgoingMethodCalls = false;
		}
		
		// Check if all methods are called outside class
		allToOutside = false;
		if (outgoingMethodCallsCount.size() > 1)
		{
			if(!(compareMethodsCalledClass.equals(methodName.onlyClassPackageName)))
			{
				allToOutside = true;
			}
		}
		
		// Get the source of target method
		FileInputStream fileInputStream = null;
		CompilationUnit compilationUnit = null;
		try
		{
			fileInputStream = new FileInputStream(sourceCodePath);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			compilationUnit = JavaParser.parse(fileInputStream);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fileInputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		new MethodVisitor().visit(compilationUnit, this);
		
		// Calculate time for flagged calls for all flagged methods
		for(int i=0; i<outgoingMethodCallsTime.length; i++)
		{
			if(flaggedOutgoingMethodCalls.containsKey(outgoingMethodCallsTime[i].getKey()))
			{
				flaggedOutgoingMethodCalls.replace(outgoingMethodCallsTime[i].getKey(), outgoingMethodCallsTime[i].getValue());
			}
		}
		
		NumberFormat formatter = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.US));    
		//System.out.println("Total Method Time ("+methodName.displayName+"): "+formatter.format(methodCallsTime)+" ms");
		//System.out.println("Self Time: "+formatter.format(methodCallsTime-totalOutgoingMethodCallsTime)+" ms  [" + formatter.format(((methodCallsTime-totalOutgoingMethodCallsTime)/methodCallsTime)*100)+"%]");
		//System.out.println("Total Outgoing Calls Time: "+formatter.format(totalOutgoingMethodCallsTime)+" ms  [" + formatter.format(((totalOutgoingMethodCallsTime)/methodCallsTime)*100)+"%]");
		
		for(int i=0; i<outgoingMethodCallsTime.length; i++)
		{
			String flag = "";
			if(flaggedOutgoingMethodCalls.containsKey(outgoingMethodCallsTime[i].getKey()))
			{
				flag = " - Flagged";
			}
			//System.out.println("     Outgoing Call Time ("+outgoingMethodCallsTime[i].getKey().methodName.displayName+"): "+formatter.format(outgoingMethodCallsTime[i].getValue())+" ms"+flag+" ["+formatter.format(((outgoingMethodCallsTime[i].getValue())/methodCallsTime)*100)+"%]");
		}
		
		double flaggedTime = 0.0;
		for(double fTime: flaggedOutgoingMethodCalls.values())
		{
			flaggedTime = flaggedTime + fTime;
		}
		//System.out.println("Flagged Time: "+formatter.format(flaggedTime)+" ms  " + formatter.format(((flaggedTime)/methodCallsTime)*100)+"%");
		
		timeUncertainty = new TimeUncertainty(this);
	}
	
	// Sort hashmap by values and return a sorted set of enties
	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> mapToSortedSet(Map<K, V> map)
	{
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>()
		{
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2)
			{
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
}
