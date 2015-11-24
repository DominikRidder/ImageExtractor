package imagehandling;

import ij.ImagePlus;
import ij.io.FileInfo;

public class NIFTIHeaderWriter {
	
	private StringBuilder header;
	
	public String writeHeader(ImagePlus data){
		header = new StringBuilder();
		FileInfo fi = data.getFileInfo();
		
		/*** HEADER STRUCTUR ********/
		Title(fi);
		Dimension(data);
		
		newline();
		resolution(fi);
		newline();
		
		bitsPerPixel(fi);
		newline();
		
		displayRange(fi);
		
		calibrationFunction(fi);
		unit(fi);
		path(fi);
		/***************************/
		
		return header.toString();
	}
	
	private void path(FileInfo fi){
		header.append("Path: \n");
	}
	
	private void unit(FileInfo fi){
		String unit = fi.unit == null ? "" : fi.unit;
		header.append("Unit: " + unit + "\n");
	}
	
	private void calibrationFunction(FileInfo fi){
		header.append("Calibration function: " + fi.calibrationFunction + "\n");
	}
	
	private void displayRange(FileInfo fi){
		double[] range = fi.displayRanges;
		if (range != null && range.length > 1) {
			header.append("Display Range: " + fi.displayRanges[0] + "-"
					+ fi.displayRanges[1] + "\n");
		}
	}
	
	private void bitsPerPixel(FileInfo fi){
		header.append("Bits per Pixel: " + fi.getBytesPerPixel() + "\n");
	}
	
	private void resolution(FileInfo fi){
		header.append("Resolution: " + fi.pixelDepth + "\n");
	}
	
	private void newline(){
		header.append("\n");
	}
	
	private void Title(FileInfo fi){
		header.append("Title: " + fi.fileName + "\n");
	}
	
	private void Dimension(ImagePlus data){
		FileInfo fi = data.getFileInfo();
		
		header.append("Width: " + (int) (fi.width * fi.pixelWidth) +" "+ fi.unit+" (" + fi.width
				+ ")\n");
		header.append("Height: " + (int) (fi.height * fi.pixelHeight) +" "+ fi.unit+ " (" + fi.height
				+ ")\n");
		header.append("Depth: " + (int) (data.getSlice() * fi.pixelDepth) +" "+ fi.unit+ " ("
				+ fi.nImages + ")\n");
	}
	
}
