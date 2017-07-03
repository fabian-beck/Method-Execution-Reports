// Declare package
package de.uni_stuttgart.cer.generator;

// Method modifiers information
public class MethodModifier
{
	// Variables
	public String accessModifier;
	public boolean isStatic;
	public boolean isFinal;
	public boolean isNative;
	public boolean isInterface;
	public boolean isAbstract;

	// Create method modifier
	MethodModifier(String accessModifier, String staticStr, String finalStr, String nativeStr, String interfaceStr, String abstractStr)
	{
		this.accessModifier = accessModifier;
		if(staticStr.equals("Yes"))
		{
			isStatic = true;
		}
		else
		{
			isStatic = false;
		}
		if(finalStr.equals("Yes"))
		{
			isFinal = true;
		}
		else
		{
			isFinal = false;
		}
		if(nativeStr.equals("Yes"))
		{
			isStatic = true;
		}
		else
		{
			isStatic = false;
		}
		if(interfaceStr.equals("Yes"))
		{
			isStatic = true;
		}
		else
		{
			isStatic = false;
		}
		if(abstractStr.equals("Yes"))
		{
			isStatic = true;
		}
		else
		{
			isStatic = false;
		}
	}
}
