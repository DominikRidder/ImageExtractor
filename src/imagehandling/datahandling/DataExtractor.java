package imagehandling.datahandling;

import ij.ImagePlus;

/**
 * This interface defines the methods, that can be called, to get the data of an
 * image.
 * 
 * @author dridder_local
 *
 */
public interface DataExtractor {

	/**
	 * This method returns the information of an Image.
	 * 
	 * @param path
	 *            The Destination of the File, to read from
	 * @return The data wrapped by the ij.ImagePlus class
	 */
	public ImagePlus getData(String path);

	/**
	 * This class creates png files to in the outputdir folder, to a given path
	 * of an image.
	 * 
	 * @param path The Destination of the File, to read from
	 * @param outputdir The Destination, where the Data is saved to
	 */
	public void extractImage(String path, String outputdir);

}
