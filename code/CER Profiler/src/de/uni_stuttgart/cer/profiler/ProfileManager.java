// Declare package
package de.uni_stuttgart.cer.profiler;

// Import classes
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

// Manager to save profiling details
public class ProfileManager
{
	// Variables
	public static boolean functionAsOutgoingCall;
	public static int functionIDOutgoingCall;
	public static FunctionProperties functionProperties = new FunctionProperties();
	public static HashMap<Integer, OutgoingFunctionCall> totalOutgoingFunctionCalls = new HashMap<Integer, OutgoingFunctionCall>();
	public static HashMap<String, Integer> totalOutgoingDistinctFuncCalls = new HashMap<String, Integer>();
	public static int outgoingFuncCallsCounter = 1;
	public static HashMap<Integer, FunctionCaller> totalFunctionCallers = new HashMap<Integer, FunctionCaller>();
	public static HashMap<String, Integer> totalDistinctFuncCallers = new HashMap<String, Integer>();
	public static FunctionSpecifications functionSpecifications = new FunctionSpecifications();
	public static HashMap<Integer, StackTrace> stackTraces = new HashMap<Integer, StackTrace>();
	public static HashMap<Integer, IncomingFunctionCall> incomingFunctionCalls = new HashMap<Integer, IncomingFunctionCall>();
	public static HashMap<Long, Integer> threadCallMappings = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> threadOutgoingCallMappings = new HashMap<Long, Integer>();
	public static HashMap<Long, Stack<OutgoingFunctionCallData>> threadOutgoingCallMappingsTrack = new HashMap<Long, Stack<OutgoingFunctionCallData>>();
	public static HashMap<Long, Integer> threadOutgoingCallIDMappings = new HashMap<Long, Integer>();
	public static HashMap<Long, Stack<Integer>> threadCallStackMappings = new HashMap<Long, Stack<Integer>>();
	public static HashMap<Long, Integer> threadCallerMappings = new HashMap<Long, Integer>();
	public static HashMap<Long, Integer> prevThreadCallerMappings = new HashMap<Long, Integer>();
	public static HashMap<Long, Stack<Integer>> threadSimilarityCalledMappings = new HashMap<Long, Stack<Integer>>();
	public static HashMap<Long, Integer> threadLastActivityMappings = new HashMap<Long, Integer>();

	// When a program is about to start after instrumentation
	public static void preStart()
	{
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			// Add function properties in the file
			FileHandler.addFunctionProperties(totalOutgoingFunctionCalls);
			
			// Check if function is in outgoing function call (direct recursive call)
			for(OutgoingFunctionCall outgoingFunctionCall: totalOutgoingFunctionCalls.values())
			{
				if(Agent.functionName.fullDetailedLongNameWithReturn.equals(outgoingFunctionCall.functionName.fullDetailedLongNameWithReturn))
				{
					functionAsOutgoingCall = true;
					functionIDOutgoingCall = outgoingFunctionCall.id;
					break;
				}
			}			
		}
	}

	// When a target function is started
	public static void targetFunctionStart(int depth)
	{
		// Time calculation
	    Long startTime = System.nanoTime();
	    Long overheadStartTime = System.nanoTime();
	    
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			// Create incoming function call and map its id with thread id
			IncomingFunctionCall incomingFunctionCall = new IncomingFunctionCall(true);
			incomingFunctionCalls.put(incomingFunctionCall.id, incomingFunctionCall);
			
			// Set the depth of incoming call
			incomingFunctionCall.depth = depth;
			
			// Maintain thread last acitvity record
			long threadID = Thread.currentThread().getId();
			if(!(threadLastActivityMappings.containsKey(threadID)))
			{
				threadLastActivityMappings.put(threadID, -1);
			}
			threadLastActivityMappings.replace(threadID, 0);
			
			// If thread id mapping already exists, it means its a recursive call, create a calling stack
			if(threadCallMappings.containsKey(threadID))
			{
				// If calling stack is already created just push the id
				if(threadCallStackMappings.containsKey(threadID))
				{
					threadCallStackMappings.get(threadID).push(incomingFunctionCall.id);
				}
				
				// Else create a new calling stack and map it with thread id
				else
				{
					Stack<Integer> callingStack = new Stack<Integer>();
					callingStack.push(incomingFunctionCall.id);
					threadCallStackMappings.put(threadID, callingStack);
				}
				
				// If thread caller mapping exists in this recursive call, remove it
				if(threadCallerMappings.containsKey(threadID))
				{
					threadCallerMappings.remove(threadID);
				}
				incomingFunctionCall.functionCalIerID = -1;
			}
			
			// Else add thread id mapping
			else
			{
				threadCallMappings.put(threadID, incomingFunctionCall.id);
				incomingFunctionCall.levelType = 0;
				
				// If thread caller mapping exists, get the function caller id
				if(threadCallerMappings.containsKey(threadID))
				{
					incomingFunctionCall.functionCalIerID = threadCallerMappings.get(threadID);
					threadCallerMappings.remove(threadID);
					if(prevThreadCallerMappings.containsKey(threadID))
					{
						prevThreadCallerMappings.remove(threadID);
					}
					prevThreadCallerMappings.put(threadID, incomingFunctionCall.functionCalIerID);
				}
				
				// If thread caller mapping don't exists, get the function caller id of previous call
				else
				{
					if(prevThreadCallerMappings.containsKey(threadID))
					{
						incomingFunctionCall.functionCalIerID = prevThreadCallerMappings.get(threadID);
					}
					else
					{
						incomingFunctionCall.functionCalIerID = 0;
					}
				}
			}
			
			// Put function marker for similarity check
			if(threadSimilarityCalledMappings.containsKey(threadID))
			{
				threadSimilarityCalledMappings.get(threadID).push(1);
			}
			else
			{
				Stack<Integer> stack = new Stack<Integer>();
				stack.push(1);
				threadSimilarityCalledMappings.put(threadID, stack);
			}
			
			// Time calculation
			incomingFunctionCall.startTime = startTime;
			int rootCallID = threadCallMappings.get(threadID);
			IncomingFunctionCall rootCall = incomingFunctionCalls.get(rootCallID);

			if (threadOutgoingCallMappingsTrack.containsKey(threadID))
			{
				Stack<OutgoingFunctionCallData> threadOutgoingCallMapTrack = threadOutgoingCallMappingsTrack.get(threadID);
				Long currentTime = System.nanoTime();
				for (int i = threadOutgoingCallMapTrack.size() - 1; i >= 0; i--)
				{
					threadOutgoingCallMapTrack.get(i).overheadTime += (currentTime - overheadStartTime);
				}
			}
			
			rootCall.overheadTime = rootCall.overheadTime + (System.nanoTime() - overheadStartTime);
		}
	}

	// When a target function is ended
	public static void targetFunctionEnd()
	{
		// Time calculation
	    Long overheadStartTime = System.nanoTime();
		
		// Get stackTrace
		StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();

		// Variables
		boolean isNewStackTrace = true;
		int specificFrequency = 0;
		String functionLongName = null;

		// If profiling is enabled
		if (Agent.profilingMode)
		{
			// If calling stack mapping exists then get recursive call of incoming function call
			int incomingFunctionCallID;
			long threadID = Thread.currentThread().getId();
			if(threadCallStackMappings.containsKey(threadID))
			{
				incomingFunctionCallID = threadCallStackMappings.get(threadID).pop();
				
				// If stack is empty, remove the calling stack mapping
				if(threadCallStackMappings.get(threadID).isEmpty())
				{
					threadCallStackMappings.remove(threadID);
				}
			}
			
			// Else get incoming function call and remove its id mapping with thread id
			else
			{
				incomingFunctionCallID = threadCallMappings.get(Thread.currentThread().getId());
				threadCallMappings.remove(Thread.currentThread().getId());
			}
			IncomingFunctionCall incomingFunctionCall = incomingFunctionCalls.get(incomingFunctionCallID);
			
			// Maintain thread last acitvity record
			if(incomingFunctionCall.levelType == -1)
			{
				if(threadLastActivityMappings.get(threadID) == 0)
				{
					incomingFunctionCall.levelType = 2;
				}
				else
				{
					incomingFunctionCall.levelType = 1;
				}
			}
			threadLastActivityMappings.replace(threadID, 1);
			
			// Filter out the stack trace and create a string array
			StackTrace stackTrace = new StackTrace(new String[stackTraceElement.length - 2], false);
			
			for (int i = stackTraceElement.length - 1, j = 0; i > 1; i--, j++)
			{
				String functionName = stackTraceElement[i].toString();
				String constructorRemover = functionName.substring(0, functionName.indexOf("("));
				
				// Check if its a constructor in stack trace then normalize it
				if(constructorRemover.contains(".<"))
				{
					String cons = constructorRemover.substring(0, functionName.indexOf(".<"));
					if(cons.indexOf(".")>0)
					{
						stackTrace.stackTrace[j] = cons+cons.substring(cons.lastIndexOf(".")); 
					}
					else
					{
						stackTrace.stackTrace[j] = cons+"."+cons;
					}		
				}
				else
				{
					stackTrace.stackTrace[j] = constructorRemover;
				}
			}
			
			// Get the function caller ID if length is greater then 1 (not the main function)
			FunctionCaller functionCaller;
			String initialFunctionCaller;
			FunctionName tempFunctionName;
			if (stackTrace.stackTrace.length > 1)
			{
				initialFunctionCaller = stackTrace.stackTrace[stackTrace.stackTrace.length - 2];
				
				// If it has valid ID
				if (incomingFunctionCall.functionCalIerID != 0 && incomingFunctionCall.functionCalIerID != -1)
				{
					functionCaller = totalFunctionCallers.get(incomingFunctionCall.functionCalIerID);

					// Cross check if function long name matches or not
					if (functionCaller.functionName.longName.equals(initialFunctionCaller))
					{
						stackTrace.functionCaller = functionCaller;
					}
					else
					{
						tempFunctionName = new FunctionName(initialFunctionCaller, null, false);
						functionCaller = new FunctionCaller(tempFunctionName, false);
						stackTrace.functionCaller = functionCaller;
					}
				}

				// If has not valid ID
				else
				{
					tempFunctionName = new FunctionName(initialFunctionCaller, null, false);
					functionCaller = new FunctionCaller(tempFunctionName, false);
					stackTrace.functionCaller = functionCaller;
				}
			}

			// If length is 1 or less (main function)
			else
			{
				tempFunctionName = new FunctionName("de.uni_stuttgart.cer.none", null, false);
				functionCaller = new FunctionCaller(tempFunctionName, false);
				stackTrace.functionCaller = functionCaller;
			}
						
			// Check if stack trace already exists
			if (stackTraces.size() > 0)
			{
				for (StackTrace stackTraceObject : stackTraces.values())
				{
					if (stackTraceObject.same(stackTrace.stackTrace, stackTrace.functionCaller))
					{
						isNewStackTrace = false;
						stackTrace.id = stackTraceObject.id;
						break;
					}
					else
					{
						continue;
					}
				}
			}

			// If the stack trace does not exists already
			if (isNewStackTrace)
			{
				// Check for recursion before adding a new stack trace
				functionLongName = stackTrace.stackTrace[stackTrace.stackTrace.length - 1];
				if (stackTrace.stackTrace.length > 1)
				{
					if (stackTrace.stackTrace[stackTrace.stackTrace.length - 2].equals(Agent.functionName.longName))
					{
						int similarityMappingIndex = threadSimilarityCalledMappings.get(threadID).size() - 2;
						if(similarityMappingIndex >= 0)
						{
							if(threadSimilarityCalledMappings.get(threadID).get(similarityMappingIndex) == 1)
							{
								incomingFunctionCall.callType = 1;
							}
						}
					}
					else
					{
						if (stackTrace.stackTrace.length > 2)
						{
							int similarityMappingIndex = threadSimilarityCalledMappings.get(threadID).size() - 2;
							if(similarityMappingIndex >= 0)
							{
								for (int i = stackTrace.stackTrace.length - 2; i >= 0; i--)
								{
									if (stackTrace.stackTrace[i].equals(functionLongName))
									{
										if(threadSimilarityCalledMappings.get(threadID).get(similarityMappingIndex) == 1)
										{
											incomingFunctionCall.callType = 2;
											break;
										}
										else
										{
											similarityMappingIndex--;
											if(similarityMappingIndex < 0)
											{
												break;
											}
										}
									}
								}
							}
						}
					}
				}

				// If it is not a recursive call, add a new stack trace
				if (incomingFunctionCall.callType == 0)
				{
					StackTrace newStackTrace = new StackTrace(stackTrace.stackTrace, true);
					stackTrace.id = newStackTrace.id;
					newStackTrace.functionCaller = stackTrace.functionCaller;
					stackTraces.put(newStackTrace.id, newStackTrace);
				}

				// If it is a recursive call, then don't add a new stack trace
				else
				{
					isNewStackTrace = false;
				}
			}

			// If the stack trace already exists, just increase the frequency
			else
			{
				stackTrace.frequency = stackTraces.get(stackTrace.id).increaseFrequency();
			}

			// Increment the appropriate frequency and update file
			if (incomingFunctionCall.callType == 0)
			{
				functionSpecifications.nonRecursionFrequency++;
				specificFrequency = functionSpecifications.nonRecursionFrequency;
			}
			else if (incomingFunctionCall.callType == 1)
			{
				functionSpecifications.directRecursionFrequency++;
				specificFrequency = functionSpecifications.directRecursionFrequency;
				functionSpecifications.directRecursion = true;
			}
			else
			{
				functionSpecifications.indirectRecursionFrequency++;
				specificFrequency = functionSpecifications.indirectRecursionFrequency;
				functionSpecifications.indirectRecursion = true;
			}
			functionSpecifications.totalFrequency++;
			
			// Remove function marker for similarity check
			threadSimilarityCalledMappings.get(threadID).pop();
			if (threadSimilarityCalledMappings.get(threadID).isEmpty())
			{
				threadSimilarityCalledMappings.remove(threadID);
			}

			// Update the file
			FileHandler.targetFunctionUpdate(functionSpecifications.totalFrequency, specificFrequency, isNewStackTrace, stackTrace, incomingFunctionCall, overheadStartTime);
			
			if(incomingFunctionCall.depth != 0)
			{
				int rootCallID = threadCallMappings.get(threadID);
				IncomingFunctionCall rootCall = incomingFunctionCalls.get(rootCallID);
				if (threadOutgoingCallMappingsTrack.containsKey(threadID))
				{
					Stack<OutgoingFunctionCallData> threadOutgoingCallMapTrack = threadOutgoingCallMappingsTrack.get(threadID);
					Long currentTime = System.nanoTime();
					for (int i = threadOutgoingCallMapTrack.size() - 1; i >= 0; i--)
					{
						threadOutgoingCallMapTrack.get(i).overheadTime += (currentTime - overheadStartTime);
					}
				}
				rootCall.overheadTime = rootCall.overheadTime + (System.nanoTime() - overheadStartTime);
			}
		}
	}
	
	// Add total function caller before the execution
	public static int addTotalFunctionCaller(String functionLongName, String functionSignature, boolean isConstructor)
	{
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			FunctionName functionName = new FunctionName(functionLongName, functionSignature, isConstructor);
			FunctionCaller functionCaller = new FunctionCaller(functionName, true);
			totalFunctionCallers.put(functionCaller.id, functionCaller);
			
			return functionCaller.id;
		}
		return 0;
	}
	
	// Add function caller
	public static void addFunctionCaller(int functionCallerID)
	{
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			long threadID = Thread.currentThread().getId();
			if(threadCallerMappings.containsKey(threadID))
			{
				threadCallerMappings.remove(threadID);
			}
			threadCallerMappings.put(threadID, functionCallerID);
		}
	}
	
	// Add total outgoing function call before the execution
	public static int addTotalOutgoingFunctionCall(String functionLongName, String functionSignature, boolean isConstructor)
	{
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			FunctionName functionName = new FunctionName(functionLongName, functionSignature, isConstructor);
			OutgoingFunctionCall totalOutgoingFunctionCall = new OutgoingFunctionCall(functionName, true);
			totalOutgoingFunctionCalls.put(totalOutgoingFunctionCall.id, totalOutgoingFunctionCall);
			
			return totalOutgoingFunctionCall.id;
		}
		
		return 0;
	}

	// Add outgoing function call
	public static void addOutgoingFunctionCall(int outgoingFunctionCallID)
	{
		// Time calculation
		Long overheadStartTime = System.nanoTime();
		
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			long threadID = Thread.currentThread().getId();

			// If calling stack mapping exists then get recursive call of incoming function call
			int incomingFunctionCallID;
			if (threadCallStackMappings.containsKey(threadID))
			{
				incomingFunctionCallID = threadCallStackMappings.get(threadID).peek();
			}

			// Else get incoming function call
			else
			{
				incomingFunctionCallID = threadCallMappings.get(Thread.currentThread().getId());
			}
			IncomingFunctionCall incomingFunctionCall = incomingFunctionCalls.get(incomingFunctionCallID);

			// Get outgoing function call and save it in incoming function call
			OutgoingFunctionCall outgoingFunctionCall = totalOutgoingFunctionCalls.get(outgoingFunctionCallID);
			OutgoingFunctionCallData outgoingFunctionCallData;
			if(functionAsOutgoingCall)
			{
				if(functionIDOutgoingCall == outgoingFunctionCallID)
				{
					outgoingFunctionCallData = new OutgoingFunctionCallData(outgoingFunctionCall, false);
				}
				else
				{
						outgoingFunctionCallData = new OutgoingFunctionCallData(outgoingFunctionCall, true);
				}
			}
			else
			{
					outgoingFunctionCallData = new OutgoingFunctionCallData(outgoingFunctionCall, true);
			}
			incomingFunctionCall.outgoingFunctionCallsData.put(outgoingFuncCallsCounter, outgoingFunctionCallData);
			incomingFunctionCall.lastEntryID = outgoingFuncCallsCounter;
			outgoingFunctionCallData.counterID = outgoingFuncCallsCounter;
						
			// Add root ID of outgoing function call for indirect recursion
			if(incomingFunctionCall.depth == 0)
			{
				threadOutgoingCallMappings.put(threadID, outgoingFuncCallsCounter);
				threadOutgoingCallIDMappings.put(threadID, outgoingFunctionCallID);
			}
			
			if(threadOutgoingCallMappingsTrack.containsKey(threadID))
			{
				Stack<OutgoingFunctionCallData> threadOutgoingCallMapTrack = threadOutgoingCallMappingsTrack.get(threadID);

				for (int i = threadOutgoingCallMapTrack.size() - 1; i >= 0; i--)
				{
					if(threadOutgoingCallMapTrack.get(i).outgoingFunctionCall.id == outgoingFunctionCallID)
					{
						outgoingFunctionCallData.timeAvailable = false;
						break;
					}
				}
				
				threadOutgoingCallMapTrack.push(outgoingFunctionCallData);
				threadOutgoingCallMappingsTrack.replace(threadID, threadOutgoingCallMapTrack);
			}
			else
			{
				Stack<OutgoingFunctionCallData> threadOutgoingCallMapTrack = new Stack<OutgoingFunctionCallData>();
				threadOutgoingCallMapTrack.push(outgoingFunctionCallData);
				threadOutgoingCallMappingsTrack.put(threadID, threadOutgoingCallMapTrack);
			}
			
			outgoingFuncCallsCounter++;

			int rootCallID = threadCallMappings.get(threadID);
			IncomingFunctionCall rootCall = incomingFunctionCalls.get(rootCallID);
			rootCall.overheadTime = rootCall.overheadTime + (System.nanoTime() - overheadStartTime);
			outgoingFunctionCallData.startTime = System.nanoTime();
		}
	}
	
	// End outgoing function call
	public static void endOutgoingFunctionCall(int outgoingFunctionCallID)
	{
		// Time calculation
		Long overheadStartTime = System.nanoTime();
		
		// If profiling is enabled
		if (Agent.profilingMode)
		{
			long threadID = Thread.currentThread().getId();
			
			// If calling stack mapping exists then get recursive call of incoming function call
			int incomingFunctionCallID;
			if (threadCallStackMappings.containsKey(threadID))
			{
				incomingFunctionCallID = threadCallStackMappings.get(threadID).peek();
			}

			// Else get incoming function call
			else
			{
				incomingFunctionCallID = threadCallMappings.get(Thread.currentThread().getId());
			}
			IncomingFunctionCall incomingFunctionCall = incomingFunctionCalls.get(incomingFunctionCallID);
			
			// Get outgoing function call data inside incoming function call
			OutgoingFunctionCallData outgoingFunctionCallData = incomingFunctionCall.outgoingFunctionCallsData.get(incomingFunctionCall.lastEntryID);
			outgoingFunctionCallData.overheadTime = outgoingFunctionCallData.overheadTime + (System.nanoTime() - overheadStartTime);
			outgoingFunctionCallData.endTime = System.nanoTime();
			outgoingFunctionCallData.consumedTime = outgoingFunctionCallData.endTime - outgoingFunctionCallData.startTime - outgoingFunctionCallData.overheadTime;
			
			// Remove root ID of outgoing function call for indirect recursion
			if(incomingFunctionCall.depth == 0)
			{
				threadOutgoingCallMappings.remove(threadID);
				threadOutgoingCallIDMappings.remove(threadID);
			}
			
			if(threadOutgoingCallMappingsTrack.containsKey(threadID))
			{
				Stack<OutgoingFunctionCallData> threadOutgoingCallMapTrack = threadOutgoingCallMappingsTrack.get(threadID);
				threadOutgoingCallMapTrack.pop();
				if(threadOutgoingCallMapTrack.size()==0)
				{
					threadOutgoingCallMappingsTrack.remove(threadID);
				}
				else
				{
					threadOutgoingCallMappingsTrack.replace(threadID, threadOutgoingCallMapTrack);
				}
			}
			incomingFunctionCall.lastEntryID = -1;

			int rootCallID = threadCallMappings.get(threadID);
			IncomingFunctionCall rootCall = incomingFunctionCalls.get(rootCallID);
			rootCall.overheadTime = rootCall.overheadTime + (System.nanoTime() - overheadStartTime);
		}
	}
	
	// When a similar target function is started
	public static void similarTargetFunctionStart()
	{
		// Put function marker for similarity check
		long threadID = Thread.currentThread().getId();
		if (threadSimilarityCalledMappings.containsKey(threadID))
		{
			threadSimilarityCalledMappings.get(threadID).push(0);
		}
		else
		{
			Stack<Integer> stack = new Stack<Integer>();
			stack.push(0);
			threadSimilarityCalledMappings.put(threadID, stack);
		}
	}
	
	// When a similar target function is ended
	public static void similarTargetFunctionEnd()
	{
		// Remove function marker for similarity check
		long threadID = Thread.currentThread().getId();
		threadSimilarityCalledMappings.get(threadID).pop();
		if (threadSimilarityCalledMappings.get(threadID).isEmpty())
		{
			threadSimilarityCalledMappings.remove(threadID);
		}
	}
}
