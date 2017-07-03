// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes

// Report of the code execution behaviour
public class Report
{
	// Variables
	public ReportCompositor reportCompositor;	
	public String sourceCode;
	
	// Create report from execution profile
	Report(ReportCompositor reportCompositor)
	{
		this.reportCompositor = reportCompositor;
		this.sourceCode = "Source code is not available";
	}
	
	// Create report from execution profile with source code
	Report(ReportCompositor reportCompositor, String sourceCode)
	{
		this.reportCompositor = reportCompositor;
		this.sourceCode = sourceCode;
	}
}
