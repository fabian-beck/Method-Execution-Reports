// Declare package
package de.uni_stuttgart.cer.generator;

// Main controller to handle the report generation process
public class Controller
{
	// Main entry point
	public static void main(String[] args)
	{
		// Open the file and parse it and create the execution profile
		FileParser fileParser = new FileParser(args[0], args[1]);
		ExecutionProfile executionProfile = fileParser.getProfile();
		
		// Execution profile is created then compose, create and generate the report
		if(executionProfile != null)
		{				
			// Create paragraphs
			Paragraph[] summaryParagraphs = new Paragraph[1];
			summaryParagraphs[0] = new Paragraph4Graph(executionProfile);
			
			Paragraph [] methodCallsParagraphs = new Paragraph[3];
			methodCallsParagraphs[0] = new Paragraph1Graph(executionProfile);
			methodCallsParagraphs[1] = new Paragraph2Graph(executionProfile);
			methodCallsParagraphs[2] = new Paragraph3Graph(executionProfile);
			
			Paragraph[] timeConsumptionParagraphs = new Paragraph[1];
			timeConsumptionParagraphs[0] = new Paragraph5Graph(executionProfile);
			
			// Create sections
			Section [] reportSections = new Section[3];
			reportSections[0] = new Section("Summary", summaryParagraphs);
			reportSections[1] = new Section("Method Calls", methodCallsParagraphs);
			reportSections[2] = new Section("Time Consumption", timeConsumptionParagraphs);
			
			// Compose the report
			ReportCompositor reportCompositor = new ReportCompositor(reportSections);
			
			// Create the report
			Report report = new Report(reportCompositor, executionProfile.sourceCode);
			
			// Generate the report
			ReportCompiler reportCompiler = new ReportCompiler(report, executionProfile.methodName.onlyName, "www/");
			reportCompiler.exportReport(executionProfile.methodName.shortName);
		}
	}
}