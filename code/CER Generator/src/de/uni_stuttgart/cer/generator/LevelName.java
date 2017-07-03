// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.util.HashMap;

// Keep track of all level names 
public class LevelName
{
	// Variables
	public HashMap<String, MethodName> level1Names;
	public HashMap<String, MethodName> level2Names;
	public HashMap<String, MethodName> level3Names;
	public HashMap<String, MethodName> level4Names;
	public HashMap<String, MethodName> level5Names;
	public HashMap<String, MethodName> level6Names;
	
	// Create level name
	LevelName()
	{
		level1Names = new HashMap<String, MethodName>();
		level2Names = new HashMap<String, MethodName>();
		level3Names = new HashMap<String, MethodName>();
		level4Names = new HashMap<String, MethodName>();
		level5Names = new HashMap<String, MethodName>();
		level6Names = new HashMap<String, MethodName>();
	}
}
