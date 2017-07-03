// Declare package
package de.uni_stuttgart.cer.profiler;

// Import Classes
import javassist.Modifier;

// Function modifiers information
public class FunctionModifier
{
	// Variables
	public String accessModifier;
	public boolean isStatic;
	public boolean isFinal;
	public boolean isNative;
	public boolean isInterface;
	public boolean isAbstract;
	
	// Create function modifier
	FunctionModifier(int modifiers)
	{
		if(Modifier.isPublic(modifiers))
		{
			accessModifier = "Public";
		}
		else if(Modifier.isPrivate(modifiers))
		{
			accessModifier = "Private";
		}
		else
		{
			accessModifier = "Protected";
		}
		
		if(Modifier.isStatic(modifiers))
		{
			isStatic = true;
		}
		else
		{
			isStatic = false;
		}
		
		if(Modifier.isFinal(modifiers))
		{
			isFinal = true;
		}
		else
		{
			isFinal = false;
		}
		
		if(Modifier.isNative(modifiers))
		{
			isNative = true;
		}
		else
		{
			isNative = false;
		}
		
		if(Modifier.isInterface(modifiers))
		{
			isInterface = true;
		}
		else
		{
			isInterface = false;
		}
		
		if(Modifier.isAbstract(modifiers))
		{
			isAbstract = true;
		}
		else
		{
			isAbstract = false;
		}
	}
}
