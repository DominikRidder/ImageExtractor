package imagehandling;

import ij.ImagePlus;
import ij.gui.Roi;
import imagehandling.headerhandling.KeyMap;
import imagehandling.headerhandling.TextOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * The Volume Class is a container for images.
 * 
 * @author Dominik Ridder
 *
 */
public abstract class Volume {

	/**
	 * The location of the Volume in the Filesystem of the Operating System.
	 */
	protected String path;

	/**
	 * This method calls the Image.getKeyWords() method. Take a look at the
	 * Java-doc of Image.getKeyWords() for more informations.
	 * 
	 * @return The diffent known Value Names seperated by a newline, that can be
	 *         in a Dicom header.
	 */
	public static String getKeyWords() {
		return Image.getKeyWords();
	}

	/**
	 * This method calls the Image.getKeyWords(str) method. Take a look at the
	 * Java-doc of Image.getKeyWords(String str) for more informations.
	 * 
	 * @param str
	 *            The str, that should be searched in the KeyWords
	 * 
	 * @return The KeyWords, that contains the string.
	 */
	public static String getKeyWords(String str) {
		return Image.getKeyWords(str);
	}

	/**
	 * This method, tells you if this volume is empty.
	 * 
	 * @return true, if the volume don't contain any Image; false otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Creates a Volume to the given path.
	 * 
	 * @param path
	 *            The path of the Volume
	 * @return The new Volume or null if failed
	 */
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
	 *            The Location, where the Data is saved to
	 */
	public abstract void extractData(String Outputdir);

	/**
	 * Creates ***.header text document in the given path with the header
	 * information of the Images.
	 */
	public abstract void extractHeader();

	/**
	 * Creates ***.header text documents in the outputdir with the header
	 * information of the Images.
	 * 
	 * @param outputdir
	 *            The Location, where the Header is saved to
	 */
	public abstract void extractHeader(String outputdir);

	/**
	 * Returning the Information of the given Key out of the Header. If there is
	 * no such element returning str = " ". If there are any differences in the
	 * Attribute for the given Key, this method will return the Attribute of the
	 * first slice.
	 *
	 * @param en
	 *            The needed value
	 * @return The String, that matches belong to the enum
	 */
	public abstract String getAttribute(KeyMap en);

	/**
	 * returning the Attribute of the given key + slice number. Use int slice =
	 * 0, for the first slice.
	 *
	 * @param en
	 *            The needed value
	 * @param slice
	 *            The slice, that should be used for this operation
	 * @return The String, that matches belong to the enum
	 */
	public abstract String getAttribute(KeyMap en, int slice);

	/**
	 * Returning a Attribute value, to a given enum and a Vector named slices,
	 * which contains the indices of the slices, which should be used.
	 * 
	 * @param en
	 *            The needed value
	 * @param slices
	 *            The number of the Slices, to search throught
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
	 * @param key
	 *            The key, that should be searched in the header
	 * 
	 * @return The Information in the volume, that matches the key
	 */
	public abstract String getAttribute(String key);

	/**
	 * This method is used to get Attributes of a Volume. If the Values to a
	 * given key are not the same in all slices, a message is printed into the
	 * console. This method always returns the attribute of the last image.
	 * 
	 * @param key
	 *            The Key, that should be searched
	 * @param slice
	 *            The slice, that should be used for this operation The slice
	 *            number
	 * @return The values, that contains the key
	 */
	public abstract String getAttribute(String key, int slice);

	/**
	 * Returning a Attribute value, to a given key and a Vector named slices,
	 * which contains the indices of the slices, which should be used.
	 * 
	 * @param key
	 *            The Key, that should be searched
	 * @param slices
	 *            The number of the Slices, to search through
	 * @return a Attribute value, to a given key and a Vector named slices,
	 *         which contains the indices of the slices, which should be used.
	 */
	public abstract String[] getAttribute(String key, Vector<Integer> slices);

	/**
	 * This method returning the specific Attribute of all Slices in a
	 * ArrayList.
	 *
	 * @param en
	 *            The needed value
	 * @return The Attributes for each slice, that matches the enum
	 */
	public abstract String[] getAttributeForEachSlice(KeyMap en);

	/**
	 * Returns an array of Strings, where each String belong to one Slice. One
	 * String may contain more rows, if the given key matches to more than one
	 * searchparameter.
	 * 
	 * @param key
	 *            The needed Value or a part of it
	 * @return The Attribute for each Slice, that contains the key
	 */
	public abstract String[] getAttributeForEachSlice(String key);

	/**
	 * This method returns the Lines, that contains the key.
	 * 
	 * @param key
	 *            The needed Value or a part of it The key, that should be
	 *            searched for
	 * @return The lines, that contains the keys
	 */
	public abstract String[] getAttributeList(String key);

	/**
	 * Returning a one dimensional array, with the informations inside.
	 * 
	 * @param key
	 *            The needed Value or a part of it
	 * @param slice
	 *            The slice, that should be used for this operation
	 * @return a one dimensional array, with the informations inside.
	 */
	public abstract String[] getAttributeList(String key, int slice);

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 *            The needed Value or a part of it
	 * @param slices
	 *            The number of the Slices, to search through
	 * @return The Lines of the chosen slices, that contains the key
	 */
	public abstract String[][] getAttributeList(String key,
			Vector<Integer> slices);

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 *            The needed Value or a part of it
	 * @return The Lines of each Image, that contains the key
	 */
	public abstract String[][] getAttributeListForEachSlice(String key);

	/**
	 * Returning the Data of an Image.
	 * 
	 * @return A List of the Data elements
	 * 
	 */
	public abstract ArrayList<ImagePlus> getData();

	/**
	 * Returning the headers of all images separated in a ArrayList
	 * 
	 * @return The List of the Headers
	 * 
	 */
	public abstract ArrayList<String> getHeader();

	/**
	 * Getter for the Path of this Volume.
	 * 
	 * @return The Path of the Volume
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returning the type of the first image in the slice. This type should be
	 * the same type of the other i
	 * 
	 * @return The ImageTyp as an String
	 * 
	 */
	public abstract String getImageType();

	/**
	 * Returns the specific Image, starting with int i = 0.
	 *
	 * @param i
	 *            The number of the slice
	 * @return The Slice as an Image
	 */
	public abstract Image getSlice(int i);

	/**
	 * Getter for the TextOptions.
	 * 
	 * @return The current TextOptions
	 */
	public abstract TextOptions getTextOptions();

	/**
	 * Sets the specified roi.
	 * 
	 * @param realroi
	 *            The roi
	 */
	public abstract void setRoi(Roi realroi);

	/**
	 * Creates and sets the needed Roi.
	 * 
	 * @param roitype
	 *            The Roi type
	 * @param x
	 *            The x coordinate of the Roi
	 * @param y
	 *            The y coordinate of the Roi
	 */
	public abstract void setRoi(int roitype, int x, int y);

	/**
	 * Creates and sets the needed Roi.
	 * 
	 * @param roitype
	 *            The Roi type
	 * @param x
	 *            The x coordinate of the Roi
	 * @param y
	 *            The y coordinate of the Roi
	 * @param width
	 *            The width of the Roi
	 * @param height
	 *            The height of the Roi
	 */
	public abstract void setRoi(int roitype, int x, int y, int width, int height);

	/**
	 * Setting the TextOptions to the Default setting.
	 */
	public abstract void resetTextOptions();

	/**
	 * Sets the Textoptions of this Volume.
	 * 
	 * @param topt
	 *            The Textoptions that change the header.
	 */
	public abstract void setTextOptions(TextOptions topt);

	/**
	 * Returning the number of Images, which are contained in the Volume.
	 * 
	 * @return The size of this Volume
	 */
	public abstract int size();

	/**
	 * Loads the Volume data.
	 */
	public abstract void loadData();

}
