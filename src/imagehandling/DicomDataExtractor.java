package imagehandling;

import java.awt.image.BufferedImage;

import ij.plugin.DICOM;

public class DicomDataExtractor implements DataExtractor {

	public BufferedImage getData(String path) {
		DICOM dcm = new DICOM();
		dcm.open(path);
		return dcm.getBufferedImage();
	}
	
}
