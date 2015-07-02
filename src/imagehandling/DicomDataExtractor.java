package imagehandling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ij.plugin.DICOM;

public class DicomDataExtractor implements DataExtractor {

	public BufferedImage getData(String path) {
		DICOM dcm = new DICOM();
		dcm.open(path);
		return dcm.getBufferedImage();
	}

	public void extractData(String path, String outputdir) {
		BufferedImage bi = getData(path);
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
