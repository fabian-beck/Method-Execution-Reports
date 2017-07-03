// Declare package
package de.uni_stuttgart.cer.generator;

// Import classes
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// Unzip the compressed zip files
public class UnzipUtility
{
	// Variables
	private static final int BUFFER_SIZE = 4096;

	// Extracts a zip file specified by the zipFilePath to a directory specified
	public void unzip(String path, String destination) throws IOException
	{
		File destinationFolder = new File(destination);
		if (!destinationFolder.exists())
		{
			destinationFolder.mkdir();
		}
		
		// For eclipse project / exported jar
		// ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(path));
		ZipInputStream zipInputStream = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream(path));
		
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		
		// Loop over all files in zip file
		while (zipEntry != null)
		{
			String filePath = destination + File.separator + zipEntry.getName();
			if (!zipEntry.isDirectory())
			{
				// if the entry is a file, extracts it
				extractFile(zipInputStream, filePath);
			}
			else
			{
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipInputStream.closeEntry();
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
	}

	// Extracts a zip entry
	private void extractFile(ZipInputStream zipInputStream, String path) throws IOException
	{
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path));
		byte[] bytesInput = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipInputStream.read(bytesInput)) != -1)
		{
			bufferedOutputStream.write(bytesInput, 0, read);
		}
		bufferedOutputStream.close();
	}
}