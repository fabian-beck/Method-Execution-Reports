// Declare package
package de.uni_stuttgart.cer.generator;

// Method name different string based representations
public class MethodName
{
	// Variables
	
	// Only class and package name "mypackage.myclass"
	String onlyClassPackageName;
	
	// Only class name "myclass"
	String onlyClassName;
	
	// Only method name "mymethod"
	String onlyName;
	
	// Method name with class "myclass.mymethod"
	String shortName;
	
	// Method name with class and package "mypackage.myclass.mymethod"
	String longName;
	
	// Method name with class and short parameters "myclass.mymethod(myparam1, myparam2)"
	String fullShortName;
	
	// Method name with class, package and short parameters "mypackage.myclass.mymethod(myparam1, myparam2)"
	String fullLongName;
	
	// Method name with class, short parameters and short return type "mytype myclass.mymethod(myparam1, myparam2)"
	String fullShortNameWithReturn;
	
	// Method name with class, package, short parameters and short return type "mytype mypackage.myclass.mymethod(myparam1, myparam2)"
	String fullLongNameWithReturn;
	
	// Method name with class, package and full parameters "mypackage.myclass.mymethod(myclass.myparam1, myclass.myparam2)"
	String fullDetailedLongName;
	
	// Method name with class, package, full parameters and full return type "myclass.mytype mypackage.myclass.mymethod(myclass.myparam1, myclass.myparam2)"
	String fullDetailedLongNameWithReturn;
	
	// If full form exists or not
	boolean fullExists;
	
	// If its construcot
	boolean isConstructor;
	
	// Display level
	int displayLevel;
		
	// Display name
	String displayName;
	
	// Create method name if method name already don't exists
	public static MethodName createMethodName(String methodNameString, boolean isConstructor, ExecutionProfile executionProfile)
	{
		// Check if the method already exists then return it
		if(executionProfile.allMethods.containsKey(methodNameString))
		{
			return executionProfile.allMethods.get(methodNameString);
		}
		
		// If method name does not exists create one
		MethodName newMethodName = new MethodName(methodNameString, isConstructor);
		
		// Add method name to list of already existing methods
		if(newMethodName.fullExists)
		{
			executionProfile.allMethods.put(newMethodName.fullDetailedLongNameWithReturn, newMethodName);
		}
		else
		{
			executionProfile.allMethods.put(newMethodName.shortName, newMethodName);
		}
		
		// Define the display name and level
		MethodName conflictMethodName;
		
		// Level 1 check
		if(MethodNameLevel.level1Names.containsKey(newMethodName.onlyName))
		{
			conflictMethodName = MethodNameLevel.level1Names.get(newMethodName.onlyName);
			if(conflictMethodName!=null)
			{
				MethodNameLevel.level1Names.replace(conflictMethodName.onlyName, null);
				conflictMethodName.displayLevel = 2;
				conflictMethodName.displayName = conflictMethodName.shortName;
				MethodNameLevel.level2Names.put(conflictMethodName.shortName, conflictMethodName);
			}
		}
		else
		{
			MethodNameLevel.level1Names.put(newMethodName.onlyName, newMethodName);
			newMethodName.displayLevel = 1;
			newMethodName.displayName = newMethodName.onlyName;
			return newMethodName;
		}
		
		// Level 2 check
		if (MethodNameLevel.level2Names.containsKey(newMethodName.shortName))
		{
			conflictMethodName = MethodNameLevel.level2Names.get(newMethodName.shortName);
			if (conflictMethodName != null)
			{
				MethodNameLevel.level2Names.replace(conflictMethodName.shortName, null);
				if(conflictMethodName.fullExists)
				{
					conflictMethodName.displayLevel = 3;
					conflictMethodName.displayName = conflictMethodName.fullShortName;
					MethodNameLevel.level3Names.put(conflictMethodName.fullShortName, conflictMethodName);
				}
			}
		}
		else
		{
			MethodNameLevel.level2Names.put(newMethodName.shortName, newMethodName);
			newMethodName.displayLevel = 2;
			newMethodName.displayName = newMethodName.shortName;
			return newMethodName;
		}
		if(!newMethodName.fullExists)
		{
			newMethodName.displayLevel = 2;
			newMethodName.displayName = newMethodName.shortName;
			return newMethodName;
		}
		
		// Level 3 check
		if (MethodNameLevel.level3Names.containsKey(newMethodName.fullShortName))
		{
			conflictMethodName = MethodNameLevel.level3Names.get(newMethodName.fullShortName);
			if (conflictMethodName != null)
			{
				MethodNameLevel.level3Names.replace(conflictMethodName.fullShortName, null);
				conflictMethodName.displayLevel = 4;
				conflictMethodName.displayName = conflictMethodName.fullShortNameWithReturn;
				MethodNameLevel.level4Names.put(conflictMethodName.fullShortNameWithReturn, conflictMethodName);
			}
		}
		else
		{
			MethodNameLevel.level3Names.put(newMethodName.fullShortName, newMethodName);
			newMethodName.displayLevel = 3;
			newMethodName.displayName = newMethodName.fullShortName;
			return newMethodName;
		}
		
		// Level 4 check
		if (MethodNameLevel.level4Names.containsKey(newMethodName.fullShortNameWithReturn))
		{
			conflictMethodName = MethodNameLevel.level4Names.get(newMethodName.fullShortNameWithReturn);
			if (conflictMethodName != null)
			{
				MethodNameLevel.level4Names.replace(conflictMethodName.fullShortNameWithReturn, null);
				conflictMethodName.displayLevel = 5;
				conflictMethodName.displayName = conflictMethodName.fullLongNameWithReturn;
				MethodNameLevel.level5Names.put(conflictMethodName.fullLongNameWithReturn, conflictMethodName);
			}
		}
		else
		{
			MethodNameLevel.level4Names.put(newMethodName.fullShortNameWithReturn, newMethodName);
			newMethodName.displayLevel = 4;
			newMethodName.displayName = newMethodName.fullShortNameWithReturn;
			return newMethodName;
		}
				
		// Level 5 check
		if (MethodNameLevel.level5Names.containsKey(newMethodName.fullLongNameWithReturn))
		{
			conflictMethodName = MethodNameLevel.level5Names.get(newMethodName.fullLongNameWithReturn);
			if (conflictMethodName != null)
			{
				MethodNameLevel.level5Names.replace(conflictMethodName.fullLongNameWithReturn, null);
				conflictMethodName.displayLevel = 6;
				conflictMethodName.displayName = conflictMethodName.fullDetailedLongNameWithReturn;
				MethodNameLevel.level6Names.put(conflictMethodName.fullDetailedLongNameWithReturn, conflictMethodName);
			}
		}
		else
		{
			MethodNameLevel.level5Names.put(newMethodName.fullLongNameWithReturn, newMethodName);
			newMethodName.displayLevel = 5;
			newMethodName.displayName = newMethodName.fullLongNameWithReturn;
			return newMethodName;
		}
		
		MethodNameLevel.level6Names.put(newMethodName.fullDetailedLongNameWithReturn, newMethodName);
		newMethodName.displayLevel = 6;
		newMethodName.displayName = newMethodName.fullDetailedLongNameWithReturn;
		return newMethodName;
	}
	
	// Create method name representations
	public MethodName(String methodNameString, boolean isConstructor)
	{
		// Initialize variables
		String returnType;
		String returnTypeShort;
		String [] parameters;
		String [] parametersShort;
		
		// Check if its constructor or not, and if its a full string or not
		this.isConstructor = isConstructor;
		if(methodNameString.contains("("))
		{
			fullExists = true;
		}
		else
		{
			fullExists = false;
		}
		
		// If full string exists
		if(fullExists)
		{
			// Get and process return type
			int spIndex = methodNameString.indexOf(" ");
			returnType = methodNameString.substring(0, spIndex);
			int rtsIndex =  returnType.lastIndexOf(".");
			returnTypeShort = returnType.substring(rtsIndex+1);
			
			// Get and process method class and method name
			int brIndex = methodNameString.indexOf("(");
			int crIndex = methodNameString.indexOf(")");
			longName = methodNameString.substring(spIndex+1, brIndex);
			int nmIndex = longName.lastIndexOf(".");
			onlyClassPackageName = longName.substring(0, nmIndex);
			int csIndex = onlyClassPackageName.lastIndexOf(".");
			onlyClassName = onlyClassPackageName.substring(csIndex+1);
			onlyName = longName.substring(nmIndex + 1);
			shortName = onlyClassName+"."+onlyName;
			
			// Get and process parameters
			String parametersString = methodNameString.substring(brIndex+1, crIndex);
			parameters = parametersString.split(", ");
			parametersShort = new String[parameters.length];
			int pIndex = 0;
			String shortParametersString = "";
			for(int i=0; i<parameters.length; i++)
			{
				pIndex = parameters[i].lastIndexOf(".");
				parametersShort[i] = parameters[i].substring(pIndex + 1);
				if(i==0)
				{
					shortParametersString = shortParametersString + parametersShort[i];
				}
				else
				{
					shortParametersString = shortParametersString + ", " + parametersShort[i];
				}
			}
			
			// Create representations
			fullShortName = shortName+"("+shortParametersString+")";
			fullLongName = longName+"("+shortParametersString+")";
			fullShortNameWithReturn = returnTypeShort+" "+shortName+"("+shortParametersString+")";
			fullLongNameWithReturn = returnTypeShort+" "+longName+"("+shortParametersString+")";
			fullDetailedLongName = longName+"("+parametersString+")";
			fullDetailedLongNameWithReturn = returnType+" "+longName+"("+parametersString+")";
		}
		
		// If full string does not exists
		else
		{
			longName = methodNameString;
			int index =  longName.lastIndexOf(".");
			onlyClassPackageName = longName.substring(0, index);
			int csIndex = onlyClassPackageName.lastIndexOf(".");
			onlyClassName = onlyClassPackageName.substring(csIndex+1);
			onlyName = longName.substring(index + 1);
			shortName = onlyClassName+"."+onlyName;
		}
	}
}
