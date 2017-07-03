// Declare package
package de.uni_stuttgart.cer.generator;

// Highlight popup visualization
public class HighlightPopupVis
{
	// Variables
	public String displayText;
	public String popupContent;
	public String label;
	public String displayString;
	
	// Create a highlight popup visualization
	public HighlightPopupVis(String displayText, String popupContent, String label)
	{
		this.displayText = displayText;
		this.popupContent = popupContent;
		this.label = label;
		this.displayString = "<span class='highlightPopupVis'>" + displayText + "</span>";
	}
}
