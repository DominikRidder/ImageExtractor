package imagehandling;

import ij.ImagePlus;

import java.awt.image.BufferedImage;

/**
 * This interface defines the methods, that can be called, to get the data of an
 * image.
 * 
 * @author dridder_local
 *
 */
interface DataExtractor {

	/**
	 * This method returns the picture to the path of a given image.
	 * 
	 * @param path
	 * @return
	 */
	public ImagePlus getData(String path);

	/**
	 * This class creates png files to in the outputdir folder, to a given path
	 * of an image.
	 * 
	 * @param path
	 * @param outputdir
	 */
	public void extractData(String path, String outputdir);

}
