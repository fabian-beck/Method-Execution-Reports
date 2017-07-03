package de.uni_stuttgart.cer.generator;

public class SampleP1 extends Paragraph
{
	ExecutionProfile executionProfile;
	SampleP1(ExecutionProfile executionProfile)
	{
		super();
		this.executionProfile = executionProfile;
	}
		
	public void build()
	{
		paragraph = "";
		paragraph = "This paragraph shows the examples of progress bar visualization; It shows "
				+addProgressBarVis(0, 100, "#00BFFF", true, "empty") +", "
				+addProgressBarVis(25, 100, "#00BFFF", true, "quarterly filled") +", "
				+addProgressBarVis(50, 100, "#32CD32", true, "half filled") +", "
				+addProgressBarVis(99.94, 100, "#FFD700", true, "almost filled") +" with a decimal point precision and "
				+addProgressBarVis(100, 100, "#DF73FF", true, "completely filled") + " progress bars."
				+" It shows different fill color options in the progress bars.";
	}
}
