package imagehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ij.plugin.DICOM;

public class DicomHeaderExtractor implements HeaderExtractor {

	/**
	 * This method returning the Header of the given Dicom Image.
	 */
	public String getHeader(String path) {
			return new DICOM().getInfo(path);
	}

	/**
	 * This method writing the header information in the same dictionary, where
	 * the Image is. The Name of the information file is the name of the Image,
	 * with ".header" instead of ".IMA" or ".dcm".
	 */
	public void extractHeader(String path) {
		String dicom = path;

		// cutting of ending like IMA or dcm
		if (path.substring(path.length() - 3, path.length()).equals("IMA")
				|| path.substring(path.length() - 3, path.length()).equals(
						"dcm")) {
			path = path.substring(0, path.length() - 4);
		}
		path += ".header";

		// check if header file already exist
		File file = new File(path);
		if (file.exists()) {
			throw new RuntimeException("DicomExtractor failed. File already exist: "+ path);
		} else {
			try {
				// just to find out if it is a Dicom
				new DICOM().open(dicom);
			} catch (IndexOutOfBoundsException e) {
				return;
			}
		}

		// str = headers
		String str;
		try {
			str = new DICOM().getInfo(dicom);
		} catch (IndexOutOfBoundsException e) {
			return;
		}

		// creating the file
		try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
			pw.print(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
