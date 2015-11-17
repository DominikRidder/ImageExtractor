package imagehandling;

import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.io.FileInfo;
import ij.plugin.Nifti_Reader;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class NIFTIVolume extends Volume {

	private ImagePlus nifti;

	private ArrayList<Image> slices = new ArrayList<Image>();

	private TextOptions to = new TextOptions();

	private String path;

	public NIFTIVolume(String path) {
		File file = new File(path);
		Nifti_Reader nr = new Nifti_Reader();
		nifti = nr.load(file.getParent(), file.getName());

		for (int i = 0; i < nifti.getNSlices(); i++) {
			Image img = new Image(path, "nii");
			nifti.setSlice(i);
			ImagePlus data = new ImagePlus("image " + i,
					nifti.getBufferedImage());
			data.setCalibration(nifti.getCalibration());
			data.setFileInfo(nifti.getFileInfo());
			img.setData(data);
			slices.add(img);
		}
	}

	@Override
	public void extractData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void extractData(String Outputdir) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extractHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void extractHeader(String outputdir) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAttribute(KeyMap en) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttribute(KeyMap en, int slice) {
		if (en == KeyMap.KEY_ECHO_NUMBERS_S) {
			return nifti.getNFrames() + "";
		}
		return null;
	}

	@Override
	public String[] getAttribute(KeyMap en, Vector<Integer> slices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttribute(String key, int slice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAttribute(String key, Vector<Integer> slices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAttributeForEachSlice(KeyMap en) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAttributeForEachSlice(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAttributeList(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAttributeList(String key, int slice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] getAttributeList(String key, Vector<Integer> slices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] getAttributeListForEachSlice(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ImagePlus> getData() {
		ArrayList<ImagePlus> data = new ArrayList<ImagePlus>();
		for (int i=0; i<size(); i++){
			data.add(slices.get(i).getData());
		}
		return data;
	}

	@Override
	public ArrayList<String> getHeader() {
		String head = getSlice(0).getHeader();
		ArrayList<String> header = new ArrayList<String>();
		for (int i = 0; i < size(); i++) {
			header.add(head);
		}
		return header;
	}

	@Override
	public String getImageType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getSlice(int i) {
		int size = slices.size();
		if (i >= size) {
			System.out.println("Out of range (Index: " + i + ", Slices: "
					+ size + "). The Last Image is returned instead ("
					+ (size - 1) + ").");
			return slices.get(size - 1);
		} else if (i < 0) {
			System.out
					.println("You can not get the Image of a negative Index. The Index should be betweeen 0-"
							+ (size - 1)
							+ " in this case.\nReturning instead the first slice (0).");
			return slices.get(0);
		}
		return slices.get(i);
	}

	@Override
	public TextOptions getTextOptions() {
		return to;
	}

	public void setRoi(Roi realroi) {
		if (realroi instanceof Roi3D) {
			double thickness = Double.parseDouble(slices.get(0).getAttribute(
					KeyMap.KEY_SLICE_THICKNESS));
			int per_echo = size()
					/ Integer.parseInt(slices.get(size() - 1).getAttribute(
							KeyMap.KEY_ECHO_NUMBERS_S));
			Rectangle rec = realroi.getBounds();
			Roi3D roi3 = (Roi3D) realroi;
			double radius = realroi.getBounds().getHeight() / 2;
			int z = roi3.getZ();
			for (int i = 0; i < per_echo; i++) {
				if (Math.abs(z - i) * thickness < radius) {
					double newr = Math.sqrt(Math.pow(radius, 2)
							- Math.pow(Math.abs(z - i) * thickness, 2));
					OvalRoi next = new OvalRoi(rec.getX() + radius - newr,
							rec.getY() + radius - newr, newr * 2, newr * 2);
					for (int j = i; j < size(); j += per_echo) {
						getSlice(j).setROI(next);
					}
				} else {
					for (int j = i; j < size(); j += per_echo) {
						getSlice(j).setROI(null);
					}
				}
			}
		} else {
			for (Image img : slices) {
				img.setROI(realroi);
			}
		}
	}

	public void setRoi(int roitype, int x, int y) {
		for (Image img : slices) {
			img.setROI(roitype, x, y);
		}
	}

	public void setRoi(int roitype, int x, int y, int width, int height) {
		for (Image img : slices) {
			img.setROI(roitype, x, y, width, height);
		}
	}

	@Override
	public void resetTextOptions() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTextOptions(TextOptions topt) {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return nifti.getNSlices() * nifti.getNFrames();
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub

	}

	public static String createHeader(ImagePlus data) {
		FileInfo fi = data.getFileInfo();
		StringBuilder header = new StringBuilder();
		header.append("Title: " + fi.fileName + "\n");
		header.append("Width: " + fi.width * fi.pixelWidth + "(" + fi.width
				+ ")\n");
		header.append("Height: " + fi.height * fi.pixelHeight + "(" + fi.height
				+ ")\n");
		header.append("Height: " + fi.nImages * fi.pixelDepth + "("
				+ fi.nImages + ")\n");
		header.append("\n");
		header.append("Resolution: " + fi.pixelDepth + "\n");
		header.append("\n");
		header.append("Bits per Pixel: " + fi.samplesPerPixel + "\n");
		double[] range = fi.displayRanges;
		if (range != null && range.length > 1) {
			header.append("Display Range: " + fi.displayRanges[0] + "-"
					+ fi.displayRanges[1] + "\n");
		}
		header.append("\n");
		header.append("Calibration function: " + fi.calibrationFunction + "\n");
		String unit = fi.unit == null ? "" : fi.unit;
		header.append("Unit: " + unit + "\n");

		header.append("Path: \n");
		return header.toString();
	}

}
