package imagehandling;

import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.io.FileInfo;
import ij.plugin.NiftiHeader;
import ij.plugin.Nifti_Reader;
import ij.plugin.filter.Info;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class NIFTIVolume extends Volume {

	private ImagePlus nifti;

	private ArrayList<Image> slices = new ArrayList<Image>();

	private TextOptions to = new TextOptions();

	NiftiHeader nifti_hdr;
	
	public NIFTIVolume(String path) {
		File file = new File(path);
		Nifti_Reader nr = new Nifti_Reader();
		nifti = nr.load(file.getParent(), file.getName());
		this.path = path;

		setUpInfo();

		for (int i = 0; i < nifti.getNSlices(); i++) {
			Image img = new Image(path, "nii");
			nifti.setSlice(i);

			img.setData(createImageData(i));

			slices.add(img);
		}
		
		nifti_hdr = (NiftiHeader) nifti.getProperty("nifti");
	}

	private void setUpInfo() {
		File file = new File(path);
		FileInfo fi = nifti.getOriginalFileInfo();

		fi.fileName = file.getName();

		nifti.setFileInfo(fi);
	}

	private ImagePlus createImageData(int imagenumber) {
		nifti.setSlice(imagenumber);
		FileInfo origfi = nifti.getFileInfo();

		ImagePlus data = new ImagePlus("image " + imagenumber,
				nifti.getBufferedImage());
		data.setCalibration(nifti.getCalibration());
		data.setFileInfo(origfi);

		FileInfo fi = data.getFileInfo();
		fi.fileName = origfi.fileName;

		data.setFileInfo(fi);
		data.setProperty("nifti", nifti.getProperty("nifti"));
		return data;
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
			return nifti_hdr.dim[4]+ "";// getNFrames() + "";
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
		for (int i = 0; i < size(); i++) {
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
			double thickness = 2;
			try {
				thickness = Double.parseDouble(slices.get(0).getAttribute(
						KeyMap.KEY_SLICE_THICKNESS));
			} catch (NumberFormatException | NullPointerException e) {

			}
			int per_echo = 1;
			try{
			per_echo = size()
					/ Integer.parseInt(getAttribute(KeyMap.KEY_ECHO_NUMBERS_S,
							0));
			}catch (NumberFormatException | NullPointerException e) {
				
			}
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
		NIFTIHeaderWriter nhw = new NIFTIHeaderWriter();

		return nhw.writeHeader(data);
		// return nhw.writeHeader(data);
	}

}
