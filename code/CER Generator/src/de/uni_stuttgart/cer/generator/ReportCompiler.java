// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Web based report generator for code execution behaviour
public class ReportCompiler
{
	// Variables
	File mainFile;
	BufferedWriter mainFileWriter;
	Report report;
	boolean createReport;
	
	// Initialize report generator
	ReportCompiler (Report report, String reportName, String path)
	{
		// Append report main directory in to the path and create it
		path = path + reportName+"/";
		new File(path).mkdirs();
		
		// Create html file and get buffer writer
    	try
		{
        	mainFile = new File(path+reportName+".html");
    		mainFileWriter = new BufferedWriter(new FileWriter(mainFile));
    		createReport = true;
    		this.report = report;
    		extractFiles(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			createReport = false;
		}
	}
	
	// Generate web based report
	public void exportReport(String reportHeading)
	{	
		if (createReport)
		{
			try
			{
				// Generate HTML report
				mainFileWriter.write(getHeader());
				mainFileWriter.write(getContent(reportHeading));
				mainFileWriter.write(getFooter());
				mainFileWriter.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// Generate header of report
	String getHeader()
	{
		StringBuilder sb = new StringBuilder();

		// Start the report and add all neceassry files
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("  <meta charset=\"UTF-8\">");
		sb.append("  <title>Code Execution Report</title>");
		sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		sb.append("  <link rel=\"stylesheet\" type=\"text/css\" href=\"files/bootstrap-3.3.6/css/bootstrap.min.css\">");
		sb.append("  <link rel=\"stylesheet\" type=\"text/css\" href=\"files/bootstrap-3.3.6/css/bootstrap.min.css\">");
		sb.append("  <link rel=\"stylesheet\" type=\"text/css\" href=\"files/rbf/rbf.css\">");
		sb.append("  <link rel=\"stylesheet\" type=\"text/css\" href=\"files/highlight/styles/github.css\">");
		sb.append("  <script src=\"files/jquery-2.1.4/jquery.min.js\"></script>");
		sb.append("  <script src=\"files/bootstrap-3.3.6/js/bootstrap.min.js\"></script>");
		sb.append("  <script type=\"text/javascript\" src=\"files/d3/d3.min.js\"></script>");
		sb.append("  <script src=\"files/highlight/highlight.pack.js\"></script>");
		sb.append("  <script>hljs.initHighlightingOnLoad();</script>");
		sb.append("</head>");
		
		return sb.toString();
	}
	
	// Generate content of report
	String getContent(String reportHeading)
	{
		StringBuilder sb = new StringBuilder();
		
		// Start body and add method method and sections navigations
		sb.append("<body class=\"body-class\">");
		sb.append("  <div class=\"container container-class\">");
		sb.append("    <div class=\"row header-class\">");
		sb.append("        <div class=\"col-xs-12\">");
		sb.append("          Code Execution Report");
		sb.append("        </div>");
		sb.append("    </div>");
		sb.append("    <div class=\"row heading-class\">");
		sb.append("        <div class=\"col-xs-12\">");
		sb.append("          <h2>"+reportHeading+"</h2>");
		sb.append("        </div>");
		sb.append("    </div>");
		sb.append("    <div class=\"row navigation-class\">");
		sb.append("      <div class=\"col-xs-12\" style=\"padding:2px\">");
		sb.append("         <div class=\"sections-class\" style=\"padding-left:15px\">");
		for (int i = 0; i < report.reportCompositor.sections.length; i++)
		{
			Section reportSection = report.reportCompositor.sections[i];
			sb.append("          <a href=\"#s"+i+"\"> " + reportSection.name + "</a>");
			sb.append("          &nbsp;");
		}
		sb.append("         </div>");
		sb.append("        </div>");
		sb.append("    </div>");
		
		// Add contents
		sb.append("    <div class=\"row\">");
		sb.append("       <div id=\"textColumn\" class=\"col-xs-12\">");
		
		// Add sections
		for(int i=0; i<report.reportCompositor.sections.length; i++)
		{
			Section reportSection = report.reportCompositor.sections[i];
			sb.append("          <h3 id=\"s"+i+"\"> " + reportSection.name + "</h3>");
			
			// Add paragraphs
			for(int j=0; j<reportSection.paragraphs.length; j++)
			{
				Paragraph sectionParagraph = reportSection.paragraphs[j];
				sb.append("          <p>");
				sb.append(sectionParagraph.paragraph);
				sb.append("          </p>");
			}
		}
		
		sb.append("        </div>");
		
		// Add source code
		sb.append("      <div id=\"codeColumn\" class=\"collapse\">");
		sb.append("          <br/>");
		sb.append("          <pre><code class=\"java\">");
		sb.append(report.sourceCode);
		sb.append("</code></pre></div>");
		
		sb.append("    </div>");
		sb.append("    <div class=\"row footer-class\">");
		sb.append("      <div class=\"col-xs-12\" style=\"padding:3px\">");
		sb.append("          <button id=\"codeBtn\" type=\"button\" class=\"btn btn-primary btn-xs pull-right\">Show Code</button>");
		sb.append("          &nbsp;");
		sb.append("        </div>");
		sb.append("    </div>");
		sb.append("  </div>");
		
		// Get visualizations data
		int progressBarsVisIDs = 0;
		boolean progressBarsVisUseComma = false;
		String varProgressBarVisIDs = "";
		String varProgressBarVisValues = "";
		String varProgressBarVisTotal = "";
		String varProgressBarVisFills = "";
		String varProgressBarVisDODs = "";
		
		int barChartsVisIDs = 0;
		int barChartsVisCount = 0;
		List<BarChartVis> barChartsVisObjects = new ArrayList<BarChartVis>();
		boolean barChartsVisUseComma = false;
		String varBarChartVisIDs = "";
		String varBarChartVisFillsEven = "";
		String varBarChartVisFillsOdd = "";
		String varBarChartVisValueMainLabels = "";
		String varBarChartVisDODs = "";
		
		int highlightPopoversVisIDs = 0;
		boolean highlightPopoverVisUseComma = false;
		String varHighlightPopoverVisIDs;
		String varHighlightPopoverVisValues;
		varHighlightPopoverVisIDs = "";
		varHighlightPopoverVisValues = "";
		
		int highlightPopupsVisIDs = 0;
		boolean highlightPopupVisUseComma = false;
		String varHighlightPopupsVisIDs = "";
		String varHighlightPopupVisValues = "";
		String varHighlightPopupValueLabels = "";
		
		for (int i = 0; i < report.reportCompositor.sections.length; i++)
		{
			Section reportSection = report.reportCompositor.sections[i];
			for (int j = 0; j < reportSection.paragraphs.length; j++)
			{
				Paragraph sectionParagraph = reportSection.paragraphs[j];

				// Get progress bar visualization data
				for (ProgressBarVis progressBarVis : sectionParagraph.progressBarsVis)
				{
					if (progressBarsVisUseComma)
					{
						varProgressBarVisIDs = varProgressBarVisIDs + ",";
						varProgressBarVisValues = varProgressBarVisValues + ",";
						varProgressBarVisTotal = varProgressBarVisTotal + ",";
						varProgressBarVisFills = varProgressBarVisFills + ",";
						varProgressBarVisDODs = varProgressBarVisDODs + ",";
					}
					else
					{
						progressBarsVisUseComma = true;
					}

					varProgressBarVisIDs = varProgressBarVisIDs + progressBarsVisIDs;
					varProgressBarVisValues = varProgressBarVisValues + progressBarVis.value;
					varProgressBarVisTotal = varProgressBarVisTotal + progressBarVis.totalValue;
					varProgressBarVisFills = varProgressBarVisFills + "\"" + progressBarVis.fillColor + "\"";
					if (progressBarVis.enableDOD)
					{
						varProgressBarVisDODs = varProgressBarVisDODs + "true";
					}
					else
					{
						varProgressBarVisDODs = varProgressBarVisDODs + "false";
					}
					progressBarsVisIDs++;
				}

				// Get bar charts visualization data
				for (BarChartVis barChartVis : sectionParagraph.barChartsVis)
				{
					if (barChartsVisUseComma)
					{
						varBarChartVisIDs = varBarChartVisIDs + ",";
						varBarChartVisFillsEven = varBarChartVisFillsEven + ",";
						varBarChartVisFillsOdd = varBarChartVisFillsOdd + ",";
						varBarChartVisValueMainLabels = varBarChartVisValueMainLabels + ",";
						varBarChartVisDODs = varBarChartVisDODs + ",";
					}
					else
					{
						barChartsVisUseComma = true;
					}

					varBarChartVisIDs = varBarChartVisIDs + barChartsVisIDs;
					varBarChartVisFillsEven = varBarChartVisFillsEven + "\"" + barChartVis.fillEvenColor + "\"";
					varBarChartVisFillsOdd = varBarChartVisFillsOdd + "\"" + barChartVis.fillOddColor + "\"";
					varBarChartVisValueMainLabels = varBarChartVisValueMainLabels + "\"" + barChartVis.label + "\"";

					if (barChartVis.enableDOD)
					{
						varBarChartVisDODs = varBarChartVisDODs + "true";
					}
					else
					{
						varBarChartVisDODs = varBarChartVisDODs + "false";
					}
					barChartsVisIDs++;
					barChartsVisCount++;
					barChartsVisObjects.add(barChartVis);
				}

				// Get highlight popover visualization data
				for (HighlightPopoverVis highlightPopoverVis : sectionParagraph.highlightPopoversVis)
				{
					if (highlightPopoverVisUseComma)
					{
						varHighlightPopoverVisIDs = varHighlightPopoverVisIDs + ",";
						varHighlightPopoverVisValues = varHighlightPopoverVisValues + ",";
					}
					else
					{
						highlightPopoverVisUseComma = true;
					}

					varHighlightPopoverVisIDs = varHighlightPopoverVisIDs + highlightPopoversVisIDs;
					varHighlightPopoverVisValues = varHighlightPopoverVisValues + "\""
							+ highlightPopoverVis.popoverContent + "\"";
					highlightPopoversVisIDs++;
				}

				// Get highlight popup visualization data
				for (HighlightPopupVis highlightPopupVis : sectionParagraph.highlightPopupsVis)
				{
					if (highlightPopupVisUseComma)
					{
						varHighlightPopupsVisIDs = varHighlightPopupsVisIDs + ",";
						varHighlightPopupVisValues = varHighlightPopupVisValues + ",";
						varHighlightPopupValueLabels = varHighlightPopupValueLabels + ",";
					}
					else
					{
						highlightPopupVisUseComma = true;
					}

					varHighlightPopupsVisIDs = varHighlightPopupsVisIDs + highlightPopupsVisIDs;
					varHighlightPopupVisValues = varHighlightPopupVisValues + "\"" + highlightPopupVis.popupContent
							+ "\"";
					varHighlightPopupValueLabels = varHighlightPopupValueLabels + "\"" + highlightPopupVis.label + "\"";
					highlightPopupsVisIDs++;
				}
			}
		}
		
		int progressBarsVisIDsAH = progressBarsVisIDs;
		
		// Get visualizations data for popups
		for (int i = 0; i < report.reportCompositor.sections.length; i++)
		{
			Section reportSection = report.reportCompositor.sections[i];
			for (int j = 0; j < reportSection.paragraphs.length; j++)
			{
				Paragraph sectionParagraph = reportSection.paragraphs[j];

				// Get progress bar visualization data for popups
				for (ProgressBarVis progressBarVis : sectionParagraph.progressBarsVisInPopup)
				{
					if (progressBarsVisUseComma)
					{
						varProgressBarVisIDs = varProgressBarVisIDs + ",";
						varProgressBarVisValues = varProgressBarVisValues + ",";
						varProgressBarVisTotal = varProgressBarVisTotal + ",";
						varProgressBarVisFills = varProgressBarVisFills + ",";
						varProgressBarVisDODs = varProgressBarVisDODs + ",";
					}
					else
					{
						progressBarsVisUseComma = true;
					}

					varProgressBarVisIDs = varProgressBarVisIDs + progressBarsVisIDs;
					varProgressBarVisValues = varProgressBarVisValues + progressBarVis.value;
					varProgressBarVisTotal = varProgressBarVisTotal + progressBarVis.totalValue;
					varProgressBarVisFills = varProgressBarVisFills + "\"" + progressBarVis.fillColor + "\"";
					if (progressBarVis.enableDOD)
					{
						varProgressBarVisDODs = varProgressBarVisDODs + "true";
					}
					else
					{
						varProgressBarVisDODs = varProgressBarVisDODs + "false";
					}
					progressBarsVisIDs++;
				}

				// Get highlight popover visualization data for popups
				for (HighlightPopoverVis highlightPopoverVis : sectionParagraph.highlightPopoversVisInPopup)
				{
					if (highlightPopoverVisUseComma)
					{
						varHighlightPopoverVisIDs = varHighlightPopoverVisIDs + ",";
						varHighlightPopoverVisValues = varHighlightPopoverVisValues + ",";
					}
					else
					{
						highlightPopoverVisUseComma = true;
					}

					varHighlightPopoverVisIDs = varHighlightPopoverVisIDs + highlightPopoversVisIDs;
					varHighlightPopoverVisValues = varHighlightPopoverVisValues + "\""
							+ highlightPopoverVis.popoverContent + "\"";
					highlightPopoversVisIDs++;
				}
			}
		}
		
		// Add visualizations data
		sb.append("<script>");
		
		// Add progress bar visualization data
		sb.append("      var progressBarVisIDs = [" + varProgressBarVisIDs+ "];");
		sb.append("      var progressBarVisValues = [" + varProgressBarVisValues+ "];");
		sb.append("      var progressBarVisTotal = [" + varProgressBarVisTotal+ "];");
		sb.append("      var progressBarVisFills = [" + varProgressBarVisFills+ "];");
		sb.append("      var progressBarVisDODs = [" + varProgressBarVisDODs+ "];");
		
		// Add bar charts visualization data
		sb.append("      var barChartVisIDs = [" + varBarChartVisIDs+ "];");
		sb.append("      var barChartVisFillsEven = [" + varBarChartVisFillsEven+ "];");
		sb.append("      var barChartVisFillsOdd = [" + varBarChartVisFillsOdd+ "];");
		sb.append("      var barChartVisValueMainLabels = [" + varBarChartVisValueMainLabels+ "];");
		sb.append("      var barChartVisDODs = [" + varBarChartVisDODs+ "];");
		if(barChartsVisCount > 0)
		{
			sb.append("      var barChartVisValues = new Array("+barChartsVisCount+");");
			sb.append("      var barChartVisValueLabels = new Array("+barChartsVisCount+");");
		}
		boolean firstRound;
		StringBuilder sb0 = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		sb0.append("");
		sb1.append("");
		int counter = 0;
		for(BarChartVis barChartsVisObject: barChartsVisObjects)
		{
			sb0.append("   barChartVisValues["+counter+"] = [");
			sb1.append("   barChartVisValueLabels["+counter+"] = [");
			firstRound = true;
			for(int i =0; i<barChartsVisObject.values.length; i++)
			{
				if(firstRound)
				{
					firstRound = false;
				}
				else
				{
					sb0.append(",");
					sb1.append(",");
				}
				
				sb0.append(barChartsVisObject.values[i]);
				sb1.append("\""+ barChartsVisObject.valueLabels[i] +"\"");
			}
			sb0.append("   ];");
			sb1.append("   ];");
			counter++;
		}
		sb.append(sb0.toString());
		sb.append(sb1.toString());
		
		// Add starting ID of progress bars in popup to align their popovers to right 
		sb.append("      raPBVisIDs = "+progressBarsVisIDsAH+";");
		
		// Add highlight popover visualization data
		sb.append("      var highlightPopoverVisIDs = [" + varHighlightPopoverVisIDs+ "];");
		sb.append("      var highlightPopoverVisValues = [" + varHighlightPopoverVisValues + "];");
		
		// Add highlight popup visualization data
		sb.append("      var highlightPopupVisIDs = ["+ varHighlightPopupsVisIDs +"];");
		sb.append("      var highlightPopupVisValues = ["+ varHighlightPopupVisValues +"];");
		sb.append("      var highlightPopupValueLabels = ["+ varHighlightPopupValueLabels +"];");
		
		sb.append("</script>");
		
		// Add javascript file
		sb.append("<script type=\"text/javascript\" src=\"files/rbf/rbf.js\"></script>");
		
		// Close body
		sb.append("</body>");

		return sb.toString();
	}
	
	// Generate footer of report
	String getFooter()
	{
		StringBuilder sb = new StringBuilder();

		// Close the report
		sb.append("</html>");
		
		return sb.toString();
	}
	
	// Extract the supported files
	void extractFiles(String path)
	{
		UnzipUtility unzipper = new UnzipUtility();
		try
		{
			unzipper.unzip("files.zip", path);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
