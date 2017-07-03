// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes

// Calculate time uncertanity level for execution profile
public class TimeUncertainty
{
	// Uncertanity levels
	// 5 - Infinite High
	// 4 - Very High
	// 3 - High
	// 2 - Medium
	// 1 - Low
	// 0 - Very Low
	
	// Variables
	
	// Global uncertainty level of execution profile (maximum of total, unpredictable, highest method and indirect recursion uncertanity [4 variables below]) {Level 1, Root}
	public int globalUncertainty;
	
	// Total time uncertainty level for target method {Level 2}
	public int totalTimeUncertaintyTM;
	
	// Unpredictable time uncertainty level for target method {Level 2}
	public int unpredictableTimeUncertaintyTM;
	
	// Indirect recursion uncertainty level for target method (either 5 or 0) {Level 2}
	public int indirectRecursionUncertaintyTM;
	
	// Highest method uncertainty level (maximum of total and unpredictable time uncertanity for highest method [2 variables below]) {Level 2}
	public int uncertaintyHM;
	
	// Total time uncertainty level for highest method {Level 3}
	public int totalTimeUncertaintyHM;

	// Unpredictable time uncertainty level for highest method {Level 3}
	public int unpredictableTimeUncertaintyHM;
	
	// Calculate uncertainty levels
	public TimeUncertainty(ExecutionProfile executionProfile)
	{

		// Calculate total and unpredictable time uncertanity level for highest method
		if(executionProfile.outgoingMethodCallsTime.length == 0)
		{
			totalTimeUncertaintyHM = 0;
			unpredictableTimeUncertaintyHM = 0;
		}
		else
		{
			OutgoingMethodCall highestMethodCall = executionProfile.outgoingMethodCallsTime[executionProfile.outgoingMethodCallsTime.length - 1].getKey();
			double highestMethodCallTime = executionProfile.outgoingMethodCallsTime[executionProfile.outgoingMethodCallsTime.length - 1].getValue();
			
			// Calculate total time uncertanity level for highest method
			if(highestMethodCallTime<=0.001)
			{
				totalTimeUncertaintyHM = 5;
			}
			else if(highestMethodCallTime<=0.1)
			{
				totalTimeUncertaintyHM = 4;
			}
			else if(highestMethodCallTime<=1)
			{
				totalTimeUncertaintyHM = 3;
			}
			else if(highestMethodCallTime<=50)
			{
				totalTimeUncertaintyHM = 2;
			}
			else if(highestMethodCallTime<=100)
			{
				totalTimeUncertaintyHM = 1;
			}
			else
			{
				totalTimeUncertaintyHM = 0;
			}
			
			// Calculate unpredictable time uncertanity level for highest method
			// If highest method is unstable
			if(executionProfile.flaggedOutgoingMethodCalls.containsKey(highestMethodCall))
			{
				double unpredictableTimePercentage = (highestMethodCallTime / executionProfile.methodCallsTime) * 100;
				
				if(unpredictableTimePercentage>=90)
				{
					unpredictableTimeUncertaintyHM = 5;
				}
				else if(unpredictableTimePercentage>=50)
				{
					unpredictableTimeUncertaintyHM = 4;
				}
				else if(unpredictableTimePercentage>=25)
				{
					unpredictableTimeUncertaintyHM = 3;
				}
				else if(unpredictableTimePercentage>=10)
				{
					unpredictableTimeUncertaintyHM = 2;
				}
				else if(unpredictableTimePercentage>=5)
				{
					unpredictableTimeUncertaintyHM = 1;
				}
				else
				{
					unpredictableTimeUncertaintyHM = 0;
				}
			}
			// If highest method is stable
			else
			{
				if(executionProfile.flaggedOutgoingMethodCalls.size() >= 1)
				{
					double nearestUnstableMethodCallTime = 0;
					
					for(int i=executionProfile.outgoingMethodCallsTime.length-2; i>=0; i--)
					{
						OutgoingMethodCall otherMethodCall = executionProfile.outgoingMethodCallsTime[i].getKey();
						if(executionProfile.flaggedOutgoingMethodCalls.containsKey(otherMethodCall))
						{
							nearestUnstableMethodCallTime = executionProfile.outgoingMethodCallsTime[i].getValue();
							break;
						}
					}
										
					double differencePercentage = ((highestMethodCallTime - nearestUnstableMethodCallTime) / executionProfile.methodCallsTime) * 100;
					
					if(differencePercentage<5)
					{
						unpredictableTimeUncertaintyHM = 5;
					}
					else if(differencePercentage<10)
					{
						unpredictableTimeUncertaintyHM = 4;
					}
					else if(differencePercentage<20)
					{
						unpredictableTimeUncertaintyHM = 3;
					}
					else if(differencePercentage<50)
					{
						unpredictableTimeUncertaintyHM = 2;
					}
					else if(differencePercentage<90)
					{
						unpredictableTimeUncertaintyHM = 1;
					}
					else
					{
						unpredictableTimeUncertaintyHM = 0;
					}
				}
				else
				{
					unpredictableTimeUncertaintyHM = 0;
				}
			}
		}
		
		// Calculate indirect recursion uncertainty level for target method
		if(executionProfile.methodSpecifications.indirectRecursion == true)
		{
			indirectRecursionUncertaintyTM = 5;
		}
		else
		{
			indirectRecursionUncertaintyTM = 0;
		}
		
		// Calculate highest method uncertainty level
		if(totalTimeUncertaintyHM > unpredictableTimeUncertaintyHM)
		{
			uncertaintyHM = totalTimeUncertaintyHM;
		}
		else
		{
			uncertaintyHM = unpredictableTimeUncertaintyHM;
		}
		
		//  Calculate total time uncertainty for target method
		if(executionProfile.methodCallsTime<=0.001)
		{
			totalTimeUncertaintyTM = 5;
		}
		else if(executionProfile.methodCallsTime<=0.1)
		{
			totalTimeUncertaintyTM = 4;
		}
		else if(executionProfile.methodCallsTime<=1)
		{
			totalTimeUncertaintyTM = 3;
		}
		else if(executionProfile.methodCallsTime<=50)
		{
			totalTimeUncertaintyTM = 2;
		}
		else if(executionProfile.methodCallsTime<=100)
		{
			totalTimeUncertaintyTM = 1;
		}
		else
		{
			totalTimeUncertaintyTM = 0;
		}
		
		//  Calculate unpredictable time uncertainty for target method
		double unpredictableTime = 0;
		for(double fTime: executionProfile.flaggedOutgoingMethodCalls.values())
		{
			unpredictableTime = unpredictableTime + fTime;
		}
		double unpredictableTimePercent = (unpredictableTime / executionProfile.methodCallsTime) * 100;
		
		
		if(unpredictableTimePercent>=90)
		{
			unpredictableTimeUncertaintyTM = 5;
		}
		else if(unpredictableTimePercent>=50)
		{
			unpredictableTimeUncertaintyTM = 4;
		}
		else if(unpredictableTimePercent>=25)
		{
			unpredictableTimeUncertaintyTM = 3;
		}
		else if(unpredictableTimePercent>=10)
		{
			unpredictableTimeUncertaintyTM = 2;
		}
		else if(unpredictableTimePercent>=5)
		{
			unpredictableTimeUncertaintyTM = 1;
		}
		else
		{
			unpredictableTimeUncertaintyTM = 0;
		}
		
		// Calculate global uncertainty
		int[] uncertaintyLevels = { totalTimeUncertaintyTM, unpredictableTimeUncertaintyTM, uncertaintyHM, indirectRecursionUncertaintyTM };
		int max = totalTimeUncertaintyTM;
		for (int i = 1; i < uncertaintyLevels.length; i++)
		{
			if (uncertaintyLevels[i] > max)
			{
				max = uncertaintyLevels[i];
			}
		}
		globalUncertainty = max;
	}
}
