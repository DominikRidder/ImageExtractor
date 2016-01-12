package imagehandling;

import ij.ImagePlus;
import ij.gui.Roi;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * The Volume class is a class, to work with a single volume. A Volume contains
 * multiple images.
 * 
 * @author dridder_local
 *
 */
public abstract class Volume {

	protected String path;

	/**
	 * This method calls the Image.getKeyWords() method. Take a look at the
	 * Java-doc of Image.getKeyWords() for more informations.
	 * 
	 */
	public static String getKeyWords() {
		return Image.getKeyWords();
	}

	/**
	 * This method calls the Image.getKeyWords(str) method. Take a look at the
	 * Java-doc of Image.getKeyWords(String str) for more informations.
	 * 
	 */
	public static String getKeyWords(String str) {
		return Image.getKeyWords(str);
	}

	// /**
	// * Path to the folder, that contains the Images of the Volume.
	// */
	// private String path;
	//
	// /**
	// * This ArrayList contains all Images, which are in the Volume.
	// */
	// private ArrayList<Image> slices;
	//
	// /**
	// * The TextOptions are used, to decide where the wildcardmatch gonna be
	// done
	// * and what the getAttributes method should return.
	// */
	// private TextOptions topt;

	// /**
	// * This default construktur should not be used. If you use this method, it
	// * gonna print some information into the console and call System.exit(1).
	// */
	// public Volume() {
	// System.out
	// .println("You have to use the Volume(String path) construktor to work with Volume.\nThe path should be the path of the repository, where the images are stored in.");
	// System.exit(1);
	// }
	//
	// /**
	// * This constructor searching in the given path for files. In this folder
	// * there should only be one type of images. If the names of the images
	// dont
	// * end with a known ending, the images handeld as a IMA image.
	// *
	// * @param path
	// */
	// public Volume(String path) {
	// resetTextOptions();
	// slices = new ArrayList<Image>();
	// this.path = path;
	//
	// // getting the files inhabited in the path
	// File file = new File(path);
	// if (file.isDirectory()) {
	// File[] list = file.listFiles();
	//
	// if (list == null) {
	// System.out
	// .println("The given Volume path seems to be not correct. Please check the path.");
	// System.exit(1);
	// }
	//
	// // adding the Images
	// for (File l : list) {
	// try {
	// if (Image.isDicom(l.toPath())) {
	// slices.add(new Image(l.getAbsolutePath()));
	// }
	// } catch (RuntimeException e) {
	// // This was not an Image
	// }
	// }
	//
	// // sort images
	// Collections.sort(slices);
	// } else if (path.endsWith(".nii")) {
	// Nifti_Reader nr = new Nifti_Reader();
	// ImagePlus nifti = nr.load(file.getParent(), file.getName());
	// System.out.println(nifti.getT());
	// for (int i = 0; i < nifti.getImageStackSize(); i++) {
	// Image img = new Image(path, "nii");
	// ImagePlus data = new ImagePlus();
	// nifti.setSlice(i);
	// data.setImage(nifti.getBufferedImage());
	// img.setData(data);
	// slices.add(img);
	// }
	// }
	//
	// }
	//
	// /**
	// * This construktur is used by the gui class. The diffence is, that the
	// * normal construktur would call System.exit(1) if the Volume path is not
	// * correct, while this method throws a RuntimeException.
	// *
	// * @param path
	// * @param gui
	// */
	// public Volume(String path, VolumeTab volumetab) {
	// resetTextOptions();
	// slices = new ArrayList<Image>();
	// this.path = path;
	//
	// // getting the files inhabited in the path
	// File file = new File(path);
	// if (file.isDirectory()) {
	// File[] list = file.listFiles();
	//
	// if (list == null) {
	// throw new RuntimeException(
	// "The given Volume path seems to be not correct. Please check the path.");
	// }
	//
	// // adding the Images
	// for (File l : list) {
	// try {
	// slices.add(new Image(l.getAbsolutePath()));
	// } catch (RuntimeException e) {
	// }
	// }
	// } else if (file.getName().endsWith(".nii")) {
	// Nifti_Reader nr = new Nifti_Reader();
	// ImagePlus nifti = nr.load(file.getParent(), file.getName());
	// System.out.println(nifti.getNFrames());
	// for (int i = 0; i < nifti.getImageStackSize(); i++) {
	// Image img = new Image(path, "nii");
	// ImagePlus data = new ImagePlus();
	// nifti.setSlice(i);
	// data.setImage(nifti.getBufferedImage());
	// img.setData(data);
	// slices.add(img);
	// }
	// }
	//
	// if (size() == 0) {
	// throw new RuntimeException(
	// "The given Volume path seems to be not correct. Please check the path.");
	// }
	//
	// // sort images
	// Collections.sort(slices);
	// }

	public static Volume createVolume(String path) {
		try {
			if (path.endsWith(".nii")) {
				return new NIFTIVolume(path);
			} else {
				return new DICOMVolume(path);
			}
		} catch (RuntimeException e) {
			File f = new File(path);
			ArrayList<String> possibilitys = new ArrayList<String>();
			for (File niftitest : f.listFiles()) {
				if (niftitest.getName().endsWith(".nii")) {
					possibilitys.add(niftitest.getAbsolutePath());
				}
			}
			try {
				if (possibilitys.size() == 1) {
					return new NIFTIVolume(possibilitys.get(0));
				}
			} catch (RuntimeException e2) {
			}
		}
		return null;
	}

	/**
	 * Creates png files of the images, in the folder, where the image is stored
	 * in.
	 */
	public abstract void extractData();

	/**
	 * Creates png files of the images, in the outputdir folder.
	 * 
	 * @param Outputdir
	 */
	public abstract void extractData(String Outputdir);

	/**
	 * Creates ***.header txt document in the given path with the header
	 * information of the Images.
	 */
	public abstract void extractHeader();

	/**
	 * Creates ***.header txt documents in the outputdir with the header
	 * information of the Images.
	 * 
	 * @param outputdir
	 */
	public abstract void extractHeader(String outputdir);

	/**
	 * Returning the Information of the given Key out of the Header. If there is
	 * no such element returning str = " ". If there are any differences in the
	 * Attribute for the given Key, this method will return the Attribute of the
	 * first slice.
	 *
	 * @param en
	 */
	public abstract String getAttribute(KeyMap en);

	/**
	 * returning the Attribute of the given key + slice number. Use int slice =
	 * 0, for the first slice.
	 *
	 * @param en
	 * @param slice
	 */
	public abstract String getAttribute(KeyMap en, int slice);

	/**
	 * Returning a Attribute value, to a given enum and a Vector named slices,
	 * which contains the indizies of the slices, which should be used.
	 * 
	 * @param en
	 * @param slices
	 * @return A String array, where each value is the result of the call:
	 *         <p>
	 *         Image.getAttribute(en)
	 */
	public abstract String[] getAttribute(KeyMap en, Vector<Integer> slices);

	/**
	 * This method is used to get Attributes of a Volume. If the Values to a
	 * given key are not the same in all slices, a message is printed into the
	 * console. This method always returns the attribute of the last image.
	 * 
	 * @return The Information in the volume, that matches the key
	 */
	public abstract String getAttribute(String key);

	public abstract String getAttribute(String key, int slice);

	/**
	 * Returning a Attribute value, to a given key and a Vector named slices,
	 * which contains the indizies of the slices, which should be used.
	 * 
	 * @param key
	 * @param slices
	 */
	public abstract String[] getAttribute(String key, Vector<Integer> slices);

	/**
	 * This method returning the specific Attribute of all Slices in a
	 * ArrayList.
	 *
	 * @param en
	 */
	public abstract String[] getAttributeForEachSlice(KeyMap en);

	/**
	 * Returns an array of Strings, where each String belong to one Slice. One
	 * String may contain more rows, if the given key matches to more than one
	 * searchparameter.
	 * 
	 * @param key
	 */
	public abstract String[] getAttributeForEachSlice(String key);

	public abstract String[] getAttributeList(String key);

	/**
	 * Returning a one dimensional array, with the informations inside.
	 * 
	 * @param key
	 * @param slice
	 */
	public abstract String[] getAttributeList(String key, int slice);

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 * @param slices
	 */
	public abstract String[][] getAttributeList(String key,
			Vector<Integer> slices);

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 */
	public abstract String[][] getAttributeListForEachSlice(String key);

	/**
	 * Returning the Data of an Image.
	 * 
	 */
	public abstract ArrayList<ImagePlus> getData();

	/**
	 * Returning the headers of all images separated in a ArrayList
	 * 
	 */
	public abstract ArrayList<String> getHeader();

	public String getPath() {
		return path;
	}

	/**
	 * Returning the type of the first image in the slice. This type should be
	 * the same type of the other i
	 * 
	 */
	public abstract String getImageType();

	/**
	 * Returns the specific Image, starting with int i = 0.
	 *
	 * @param i
	 */
	public abstract Image getSlice(int i);

	public abstract TextOptions getTextOptions();

	public abstract void setRoi(Roi realroi);

	public abstract void setRoi(int roitype, int x, int y);

	public abstract void setRoi(int roitype, int x, int y, int width, int height);

	/**
	 * Setting the TextOptions to the Default setting.
	 */
	public abstract void resetTextOptions();

	public abstract void setTextOptions(TextOptions topt);

	/**
	 * Returning the number of Images, which are contained in the Volume.
	 */
	public abstract int size();

	public abstract void loadData();

}
