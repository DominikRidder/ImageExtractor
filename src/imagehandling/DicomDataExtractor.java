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

	public void extractData(String path) {
		BufferedImage bi = getData(path);
		File testoutput = new File(path.substring(0,path.length()-3)+"png");
		try {
			ImageIO.write(bi, "png", testoutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
