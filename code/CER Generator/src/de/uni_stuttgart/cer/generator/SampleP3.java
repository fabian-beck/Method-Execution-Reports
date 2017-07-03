package de.uni_stuttgart.cer.generator;

public class SampleP3 extends Paragraph
{
	ExecutionProfile executionProfile;
	SampleP3(ExecutionProfile executionProfile)
	{
		super();
		this.executionProfile = executionProfile;
	}
		
	public void build()
	{
		paragraph = "";
		paragraph = "This paragraph presents the examples of highlight visualization; It shows "
				+addHighlightPopoverVis("popover", "Popover content")+" and "
				+addHighlightPopupVis("popup", "Popup content", "Label") +" highlights.";
	}
}
