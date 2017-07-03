// Declare package
package de.uni_stuttgart.cer.generator;

// Composite a report
public class ReportCompositor
{
	// Variables
	public Section[] sections;

	// Create report compositor from sections
	public ReportCompositor(Section[] sections)
	{
		this.sections = sections;
		
		// Build all paragraphs in all sections
		for(int i=0; i<sections.length; i++)
		{
			for (int j=0; j<sections[i].paragraphs.length; j++)
			{
				sections[i].paragraphs[j].build();
			}
		}
	}
}
