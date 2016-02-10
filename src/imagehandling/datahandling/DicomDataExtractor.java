package imagehandling.datahandling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ij.ImagePlus;
import ij.plugin.DICOM;

/**
 * Implementation of the interface DataExtractor, which is used for dicoms.
 * @author dridder_local
 *
 */
public class DicomDataExtractor implements DataExtractor {

	/**
	 * Returns the image of a Dicom.
	 */
	public ImagePlus getData(String path) {
		DICOM dcm = new DICOM();
		dcm.open(path);
		return dcm;
	}

	/**
	 * This methods creates png Files in the outputdir folder, of the image to the given dicom path.
	 */
	public void extractImage(String path, String outputdir) {
		BufferedImage bi = getData(path).getBufferedImage();
		File img = new File(path);
		File output = new File(outputdir + "/"
				+ img.getName().substring(0, img.getName().length() - 3)
				+ "png");
		try {
			ImageIO.write(bi, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
