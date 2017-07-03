// Declare package
package de.uni_stuttgart.cer.generator;

// Highlight popover visualization
public class HighlightPopoverVis
{
	// Variables
	public String displayText;
	public String popoverContent;
	public String displayString;

	// Create a highlight popover visualization
	public HighlightPopoverVis(String displayText, String popoverContent)
	{
		this.displayText = displayText;
		this.popoverContent = popoverContent;
		this.displayString = "<span class='highlightPopoverVis'>" + displayText + "</span>";
	}
}
