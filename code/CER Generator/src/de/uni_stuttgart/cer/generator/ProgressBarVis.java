// Declare package
package de.uni_stuttgart.cer.generator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

// Progress bar visualization
public class ProgressBarVis
{
	// Variables
	public double value;
	public double totalValue;
	public String fillColor;
	public boolean enableDOD;
	public String displayText;
	public String displayString;
	
	// Create a progress bar visualization
	public ProgressBarVis(double value, double totalValue, String fillColor, boolean enableDOD, String displayText)
	{
		NumberFormat formatter = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
		this.value = Double.parseDouble(formatter.format(value));
		this.totalValue = Double.parseDouble(formatter.format(totalValue));
		this.fillColor = fillColor;
		this.enableDOD = enableDOD;
		this.displayString = displayText;
		this.displayString = "<span class='progressBarVis'>"+displayText+" "+"</span>";
	}
}
