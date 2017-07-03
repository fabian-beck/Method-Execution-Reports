// Declare package
package de.uni_stuttgart.cer.generator;

// Bar chart visualization
public class BarChartVis
{
	// Variables
	public double [] values;
	public String [] valueLabels;
	public String fillEvenColor;
	public String fillOddColor;
	public String label;
	public boolean enableDOD;
	public String displayText;
	public String displayString;
	
	// Create a bar chart visualization
	public BarChartVis(double[] values, String[] valueLabels, String fillEvenColor, String fillOddColor, String label, boolean enableDOD, String displayText)
	{
		this.values = values;
		this.valueLabels = valueLabels;
		this.fillEvenColor = fillEvenColor;
		this.fillOddColor = fillOddColor;
		this.label = label;
		this.enableDOD = enableDOD;
		this.displayString = displayText;
		this.displayString = "<span class='barChartVis'>" + displayText + " " + "</span>";
	}
}
