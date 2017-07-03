// Declare package
package de.uni_stuttgart.cer.generator;

// Section for organizing paragraphs
public class Section
{
	// Variables
	public String name;
	public Paragraph[] paragraphs;

	// Create section from paragraphs
	public Section(String name, Paragraph[] paragraphs)
	{
		this.name = name;
		this.paragraphs = paragraphs;
	}
}
