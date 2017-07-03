// Declare package
package de.uni_stuttgart.cer.profiler;

// Function name different string based representations
public class FunctionName
{
	// Variables
	
	// Only class and package name "mypackage.myclass"
	String onlyClassPackageName;
	
	// Only class name "myclass"
	String onlyClassName;
	
	// Only function name "myfunction"
	String onlyName;
	
	// Function name with class "myclass.myfunction"
	String shortName;
	
	// Function name with class and package "mypackage.myclass.myfunction"
	String longName;
	
	// Function name with class and short parameters "myclass.myfunction(myparam1, myparam2)"
	String fullShortName;
	
	// Function name with class, package and short parameters "mypackage.myclass.myfunction(myparam1, myparam2)"
	String fullLongName;
	
	// Function name with class, short parameters and short return type "mytype myclass.myfunction(myparam1, myparam2)"
	String fullShortNameWithReturn;
	
	// Function name with class, package, short parameters and short return type "mytype mypackage.myclass.myfunction(myparam1, myparam2)"
	String fullLongNameWithReturn;
	
	// Function name with class, package and full parameters "mypackage.myclass.myfunction(myclass.myparam1, myclass.myparam2)"
	String fullDetailedLongName;
	
	// Function name with class, package, full parameters and full return type "myclass.mytype mypackage.myclass.myfunction(myclass.myparam1, myclass.myparam2)"
	String fullDetailedLongNameWithReturn;
	
	// If full form exists or not
	boolean fullExists;
	
	// If its construcot
	boolean isConstructor;
	
	// Create function name representations
	public FunctionName(String functionLongName, String functionSignature, boolean isConstructor)
	{
		int index = functionLongName.lastIndexOf(".");
		String[] names = { functionLongName.substring(0, index), functionLongName.substring(index + 1) };
		if (isConstructor)
		{
			names[0] = functionLongName;
		}
		onlyClassPackageName = names[0];
		int csIndex = onlyClassPackageName.lastIndexOf(".");
		onlyName = names[1];
		longName = functionLongName;
		onlyClassName = onlyClassPackageName.substring(csIndex+1);
		shortName = onlyClassName+"."+onlyName;
		if (isConstructor)
		{
			longName = longName+"."+onlyName;
		}
		fullLongName = null;
		fullLongNameWithReturn = null;
		fullDetailedLongName = null;
		fullDetailedLongNameWithReturn = null;
		fullLongName = null;
		fullDetailedLongName = null;
		fullExists = false;
		this.isConstructor = isConstructor;

		// If it has function singature
		if (functionSignature != null)
		{
			setFunctionSignature(functionSignature);
		}

		ProfileManager.functionProperties.functionName = this;
		ProfileManager.functionSpecifications.functionName = this;
	}
	
	// Convert a function name representation to full function name representation
	public void setFunctionSignature(String functionSignature)
	{
		int index = functionSignature.lastIndexOf(")");
		String[] names = { functionSignature.substring(1, index), functionSignature.substring(index + 1) };
		String parameters = names[0];
		String returnType = names[1];
		String shortParameters = null;
		String shortReturnType = null;
		int stringIndex = 0;
		String result = null;
				
		// Process parameters
		if(parameters.length() == 0)
		{
			parameters = "";
			shortParameters = "";
		}
		else
		{
			StringBuilder parametersStringBuilder = new StringBuilder();
			StringBuilder parametersShortStrBuilder = new StringBuilder();
			for(int i=0; i<parameters.length(); i++)
			{
				if(i!=0)
				{
					if(parameters.charAt(i-1)!='[')
					{
						parametersStringBuilder.append(", ");
						parametersShortStrBuilder.append(", ");
					}
				}
				String charString = String.valueOf(parameters.charAt(i));
				if(charString.equals("L"))
				{
					String param = parameters.substring(i, parameters.length());
					stringIndex = param.indexOf(";");
					i = i+stringIndex;
					String paramToAdd = param.substring(1, stringIndex);
					parametersStringBuilder.append(paramToAdd);
					parametersShortStrBuilder.append(paramToAdd.substring(paramToAdd.lastIndexOf("/")+1));
				}
				else
				{
					result = decodeJVMSignature(charString);
					parametersStringBuilder.append(result);
					parametersShortStrBuilder.append(result);
				}
			}
			parameters = parametersStringBuilder.toString().replace("/",".");
			shortParameters = parametersShortStrBuilder.toString();
		}
		
		// Process return type
		StringBuilder returnTypeStringBuilder = new StringBuilder();
		StringBuilder returnTypeShortStrBuilder = new StringBuilder();
		for(int i=0; i<returnType.length(); i++)
		{
			String charString = String.valueOf(returnType.charAt(i));
			if(charString.equals("L"))
			{
				String rt = returnType.substring(i, returnType.length());
				stringIndex = rt.indexOf(";");
				i = i+stringIndex;
				String rtToAdd = rt.substring(1, stringIndex);
				returnTypeStringBuilder.append(rtToAdd);
				returnTypeShortStrBuilder.append(rtToAdd.substring(rtToAdd.lastIndexOf("/")+1));
			}
			else
			{
				result = decodeJVMSignature(charString);
				returnTypeStringBuilder.append(result);
				returnTypeShortStrBuilder.append(result);
			}
		}
		returnType = returnTypeStringBuilder.toString().replace("/",".");
		shortReturnType = returnTypeShortStrBuilder.toString().replace("/",".");
		
		// Create full representations
		fullExists = true;
		fullShortName = shortName+"("+shortParameters+")";
		fullLongName = longName+"("+shortParameters+")";
		fullShortNameWithReturn = shortReturnType+" "+shortName+"("+shortParameters+")";
		fullLongNameWithReturn = shortReturnType+" "+longName+"("+shortParameters+")";
		fullDetailedLongName = longName+"("+parameters+")";
		fullDetailedLongNameWithReturn = returnType+" "+longName+"("+parameters+")";
	}
	
	// Decode JVM represenation of function signature
	public String decodeJVMSignature(String charString)
	{
		if(charString.equals("V"))
		{
			return "void";
		}
		else if(charString.equals("I"))
		{
			return "int";
		}
		else if(charString.equals("F"))
		{
			return "float";
		}
		else if(charString.equals("J"))
		{
			return "long";
		}
		else if(charString.equals("S"))
		{
			return "short";
		}
		else if(charString.equals("D"))
		{
			return "double";
		}
		else if(charString.equals("Z"))
		{
			return "boolean";
		}
		else if(charString.equals("C"))
		{
			return "char";
		}
		else if(charString.equals("B"))
		{
			return "byte";
		}
		else if(charString.equals("["))
		{
			return "[]";
		}
		
		return "NULL";
	}
}
