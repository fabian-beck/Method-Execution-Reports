package de.uni_stuttgart.cer.generator;

public class SampleP2 extends Paragraph
{
	ExecutionProfile executionProfile;
	SampleP2(ExecutionProfile executionProfile)
	{
		super();
		this.executionProfile = executionProfile;
	}
		
	public void build()
	{
		double [] values8 = {4,6,8,10,8,6,4,2};
		String [] values8Str = new String[values8.length];
		for(int i=0; i<values8.length; i++) values8Str[i] = Integer.toString((int) values8[i]);
		
		double [] values16 = {5,7,9,11,13,14,15,14,12,10,9,8,9,10,12,14};
		String [] values16Str = new String[values16.length];
		for(int i=0; i<values16.length; i++) values16Str[i] = Integer.toString((int) values16[i]);
		
		double [] values32 = {5,7,9,12,14,16,17,18,18,16,15,14,12,10,8,7,5,5,6,7,8,10,11,12,12,11,10,8,7,6,5,4};
		String [] values32Str = new String[values32.length];
		for(int i=0; i<values32.length; i++) values32Str[i] = Integer.toString((int) values32[i]);
		
		double [] values64 = {5,7,9,12,14,16,17,18,18,16,15,14,12,10,8,7,5,5,6,7,8,10,12,12,12,14,16,18,20,22,24,26,28,30,30,28,26,24,22,20,22,24,26,28,30,32,34,34,34,34,32,30,28,26,24,22,20,18,16,14,12,10,8,6};
		String [] values64Str = new String[values64.length];
		for(int i=0; i<values64.length; i++) values64Str[i] = Integer.toString((int) values64[i]);
		
		paragraph = "";
		paragraph = "This paragraph presents the examples of bar chart visualization; It shows "
				+addBarChartVis(values8, values8Str, "#FF004F", "#FFA6C9", "Label", true, "8 values")+", "
				+addBarChartVis(values16, values16Str, "#800080", "#DA70D6", "Label", true, "16 values")+", "
				+addBarChartVis(values32, values32Str, "#177245", "#74C365", "Label", true, "32 values")+" and "
				+addBarChartVis(values64, values64Str, "#CD5700", "#FF9F00", "Label", true, "64 values")+" bar charts."
				+" It shows different even and odd fill color options in the bar charts.";
	}
}
