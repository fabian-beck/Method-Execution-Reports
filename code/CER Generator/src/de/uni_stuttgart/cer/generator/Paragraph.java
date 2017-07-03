// Declare package
package de.uni_stuttgart.cer.generator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

// Base class for paragraph
public abstract class Paragraph
{
	
	// Variables
	public String paragraph;
	public List<ProgressBarVis> progressBarsVis;
	public List<BarChartVis> barChartsVis;
	public List<HighlightPopoverVis> highlightPopoversVis;
	public List<HighlightPopupVis> highlightPopupsVis;
	public List<ProgressBarVis> progressBarsVisInPopup;
	public List<HighlightPopoverVis> highlightPopoversVisInPopup;
	
	// Create paragraph
	Paragraph()
	{
		this.progressBarsVis = new ArrayList<ProgressBarVis>();
		this.barChartsVis = new ArrayList<BarChartVis>();
		this.highlightPopoversVis = new ArrayList<HighlightPopoverVis>();
		this.highlightPopupsVis = new ArrayList<HighlightPopupVis>();
		this.progressBarsVisInPopup = new ArrayList<ProgressBarVis>();
		this.highlightPopoversVisInPopup = new ArrayList<HighlightPopoverVis>();
	}
	
	// Add progress bar visualization and return its representation in paragraph for report
	public String addProgressBarVis(double value, double totalValue, String fillColor, boolean enableDOD, String displayText)
	{		
		ProgressBarVis progressBarVis = new ProgressBarVis(value, totalValue, fillColor, enableDOD, displayText);
		progressBarsVis.add(progressBarVis);
		
		return progressBarVis.displayString;
	}
	
	// Add bar char visualization and return its representation in paragraph for report
	public String addBarChartVis(double[] values, String[] valueLabels, String fillEvenColor, String fillOddColor, String label, boolean enableDOD, String displayText)
	{
		BarChartVis barChartVis = new BarChartVis(values, valueLabels, fillEvenColor, fillOddColor, label, enableDOD, displayText);
		barChartsVis.add(barChartVis);

		return barChartVis.displayString;
	}
	
	// Add highlight popover visualization and return its representation in paragraph for report
	public String addHighlightPopoverVis(String displayText, String popoverContent)
	{
		HighlightPopoverVis highlightPopoverVis = new HighlightPopoverVis(displayText, popoverContent);
		highlightPopoversVis.add(highlightPopoverVis);

		return highlightPopoverVis.displayString;
	}
	
	// Add highlight popup visualization and return its representation in paragraph for report
	public String addHighlightPopupVis(String displayText, String popupContent, String label)
	{
		HighlightPopupVis highlightPopupVis = new HighlightPopupVis(displayText, popupContent, label);
		highlightPopupsVis.add(highlightPopupVis);

		return highlightPopupVis.displayString;
	}
	
	// Add progress bar visualization in popup and return its representation in paragraph for report
	public String addProgressBarVisInPopup(double value, double totalValue, String fillColor, boolean enableDOD, String displayText)
	{
		ProgressBarVis progressBarVis = new ProgressBarVis(value, totalValue, fillColor, enableDOD, displayText);
		progressBarsVisInPopup.add(progressBarVis);
		
		return progressBarVis.displayString;
	}
	
	// Add highlight popover visualization in popup and return its representation in paragraph for report
	public String addHighlightPopoverVisInPopup(String displayText, String popoverContent)
	{
		HighlightPopoverVis highlightPopoverVis = new HighlightPopoverVis(displayText, popoverContent);
		highlightPopoversVisInPopup.add(highlightPopoverVis);

		return highlightPopoverVis.displayString;
	}

	// Build method to override
	public abstract void build();
}
