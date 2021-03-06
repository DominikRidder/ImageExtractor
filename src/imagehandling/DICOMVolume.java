package imagehandling;

import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import imagehandling.headerhandling.KeyMap;
import imagehandling.headerhandling.TextOptions;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * @author Dominik Ridder
 *
 */
public class DICOMVolume extends Volume {
	/**
	 * This method calls the Image.getKeyWords() method. Take a look at the
	 * Java-doc of Image.getKeyWords() for more informations.
	 * 
	 * @return All implemented KeyWords
	 */
	public static String getKeyWords() {
		return Image.getKeyWords();
	}

	/**
	 * This method calls the Image.getKeyWords(str) method. Take a look at the
	 * Java-doc of Image.getKeyWords(String str) for more informations.
	 * 
	 * @param str
	 *            An easy Wildcard string.
	 * @return Attribute Names, that matches to the given String
	 */
	public static String getKeyWords(String str) {
		return Image.getKeyWords(str);
	}

	/**
	 * This ArrayList contains all Images, which are in the Volume.
	 */
	private ArrayList<Image> slices;

	/**
	 * The TextOptions are used, to decide where the wildcardmatch gonna be done
	 * and what the getAttributes method should return.
	 */
	private TextOptions topt;

	/**
	 * This default constructor should not be used. If you use this method, it
	 * gonna print some information into the console and call System.exit(1).
	 */
	public DICOMVolume() {
		System.out
				.println("You have to use the Volume(String path) construktor to work with Volume.\nThe path should be the path of the repository, where the images are stored in.");
		System.exit(1);
	}

	/**
	 * This constructor searching in the given path for files. In this folder
	 * there should only be one type of images. If the names of the images don't
	 * end with a known ending, the images handled as a IMA image.
	 *
	 * @param path
	 *            The Location, of the Dicom Volume
	 */
	public DICOMVolume(String path) {
		resetTextOptions();
		slices = new ArrayList<Image>();
		this.path = path;

		// getting the files inhabited in the path
		File file = new File(path);
		if (file.isDirectory()) {
			File[] list = file.listFiles();

			if (list == null) {
				System.out
						.println("The given Volume path seems to be not correct. Please check the path.");
				System.exit(1);
			}

			// adding the Images
			for (File l : list) {
				try {
					if (Image.isDicom(l.toPath())) {
						slices.add(new Image(l.getAbsolutePath()));
					}
				} catch (RuntimeException e) {
					// This was not an Image
				}
			}
			if (size() == 0) {
				throw new RuntimeException();
			}

			// sort images
			Collections.sort(slices);
			
			// Remove duplicates
			String lastHeader = slices.get(0).getHeader();
			for (int i = 0; i < slices.size()-1; i++) {
				String nextHeader = slices.get(i+1).getHeader();
				if (lastHeader.equals(nextHeader)) {
					slices.remove(i+1);
					i--;
				} else {
					lastHeader = nextHeader;
				}
			}
		}

	}

	/**
	 * Creates png files of the images, in the folder, where the image is stored
	 * in.
	 */
	public void extractData() {
		extractData(path);
	}

	/**
	 * Creates png files of the images, in the outputdir folder.
	 * 
	 */
	public void extractData(String Outputdir) {
		for (Image img : slices) {
			img.extractData(Outputdir);
		}
	}

	/**
	 * Creates ***.header txt document in the given path with the header
	 * information of the Images.
	 */
	public void extractHeader() {
		extractHeader(path);
	}

	/**
	 * Creates ***.header txt documents in the outputdir with the header
	 * information of the Images.
	 * 
	 * @param outputdir
	 *            The Destination folder of the header files.
	 */
	public void extractHeader(String outputdir) {
		int excounter = 0;
		for (Image img : slices) {
			try {
				img.extractHeader(true, outputdir);
			} catch (RuntimeException e) {
				excounter++;
			}
		}
		if (excounter != 0) {
			System.out.println("DicomHeaderExtractor failed " + excounter + "/"
					+ slices.size()
					+ " times, because some ***.header files already exist.");
		}
	}

	/**
	 * Returning the Information of the given Key out of the Header. If there is
	 * no such element returning str = " ". If there are any differences in the
	 * Attribute for the given Key, this method will return the Attribute of the
	 * first slice.
	 *
	 * @param en
	 *            KeyMap enum, that contains the information, that is needed to
	 *            get the Attribute
	 * @return The Attribute, that matches the enum name
	 */
	public String getAttribute(KeyMap en, boolean compareslices) {
		if (en == null) {
			System.out
					.println("The given element is 'null'. Cant search without a real KeyMap enum.");
			return "<<no key given>>";
		}

		// getting the first Attribute, for some comparisons
		int index = size() - 1;
		String str = null;
		while (str == null) {
			try {
				str = getAttribute(en, index--);
			} catch (IndexOutOfBoundsException e) {
				// catching files, that are not part of the images
			}
		}

		if (compareslices) {
			// if str == " " the attribute isnt set in the header
			if (str.equals(" ")) {
				return str;
			}

			// starting with i=1, because we already have str = 'slice(0)'...
			for (int i = 1; i < slices.size(); i++) {
				try {
					if (!str.equals(getAttribute(en, i))) {
						System.out
								.println("The Attributes are not the same in all slices.");
						break;
					}
				} catch (IndexOutOfBoundsException e) {
					// catching files, that are not part of the images
				}
			}
		}

		return str;
	}

	/**
	 * returning the Attribute of the given key + slice number. Use int slice =
	 * 0, for the first slice.
	 *
	 * @param en
	 *            The KeyMap enum, that represents the searched value.
	 * @param slice
	 *            The Slice, which Attribute should be returned.
	 * @return The Attribute of the Image with the Image number slice (-1) if
	 *         the enum is not null; "&lt;&lt;no key given&gt;&gt;" else
	 */
	public String getAttribute(KeyMap en, int slice) {
		if (en == null) {
			System.out
					.println("The given element is 'null'. Cant search without a real KeyMap enum.");
			return "<<no key given>>";
		}
		return slices.get(slice).getAttribute(en, topt);
	}

	/**
	 * Returning a Attribute value, to a given enum and a Vector named slices,
	 * which contains the indizies of the slices, which should be used.
	 * 
	 * @param en
	 *            The KeyMap enum, that represents the searched value.
	 * @param slices
	 *            The slices, where the header should be searched for the en.
	 * @return The Attribute that Matches the enum. For each slice one array
	 *         element.
	 */
	public String[] getAttribute(KeyMap en, Vector<Integer> slices) {
		String[] att = new String[slices.size()];
		int index = 0;
		Integer[] a = new Integer[0];
		for (int slice : slices.toArray(a)) {
			att[index++] = getAttribute(en, slice);
		}
		return att;
	}

	/**
	 * This method is used to get Attributes of a Volume. If the Values to a
	 * given key are not the same in all slices, a message is printed into the
	 * console. This method always returns the attribute of the last image.
	 * 
	 * @param key
	 *            An String, that should be searched in the Header.
	 * @param compareslices
	 *            True: checks, if all slices containing value; False: just
	 *            returning the value of the last slice
	 * @return The Information in the Header, that matches the key
	 */
	public String getAttribute(String key, boolean compareslices) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return "<<no key given>>";
		}

		// getting the last Attribute, for some comparisons
		String str = getAttribute(key, this.size() - 1);

		if (compareslices) {
			// if str == "<<no attribute found>>" the attribute isnt set in the
			// header
			if (str.equals("<<no attribute found>>")) {
				return str;
			}

			// ending by i = slices.size-1, because we use this attribute for
			// the
			// comparission
			for (int i = 0; i < slices.size() - 1; i++) {
				try {
					if (!str.equals(getAttribute(key, i))) {
						System.out
								.println("The Attributes are not the same in all slices.");
						break;
					}
				} catch (IndexOutOfBoundsException e) {
					// catching files, that are not part of the images
				}
			}
		}

		return str;
	}

	public String getAttribute(String key, int slice) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return "<<no key given>>";
		}
		if (slice >= this.size()) {
			System.out
					.println("The given int value is to big. Returning instead of this, the Attribute of the last slice.");
			slice = this.size() - 1;
		} else if (slice < 0) {
			System.out
					.println("A negativ slice value dont make sense. Returning instead of this, the Attribute of slice 0.");
			slice = 0;
		}
		return slices.get(slice).getAttribute(key, topt);
	}

	/**
	 * Returning a Attribute value, to a given key and a Vector named slices,
	 * which contains the indices of the slices, which should be used.
	 * 
	 * @param key
	 *            The KeyMap enum, that represents the searched value.
	 * @param slices
	 *            The slices, which value you need.
	 */
	public String[] getAttribute(String key, Vector<Integer> slices) {
		Integer[] a = new Integer[0];
		String[] att = new String[slices.size()];
		int index = 0;
		for (int slice : slices.toArray(a)) {
			att[index++] = getAttribute(key, slice);
		}
		return att;
	}

	/**
	 * This method returning the specific Attribute of all Slices in a
	 * ArrayList.
	 *
	 * @param en
	 *            The KeyMap enum, that represents the searched value.
	 */
	public String[] getAttributeForEachSlice(KeyMap en) {
		if (en == null) {
			System.out
					.println("The given element is 'null'. Cant search without a real KeyMap enum.");
			return null;
		}
		String[] attributes = new String[this.size()];
		int index = 0;
		for (int i = 0; i < slices.size(); i++) {
			attributes[index++] = getAttribute(en, i);
		}
		return attributes;
	}

	/**
	 * Returns an array of Strings, where each String belong to one Slice. One
	 * String may contain more rows, if the given key matches to more than one
	 * search parameter.
	 * 
	 * @param key
	 *            The String, that should be searched in the header lines.
	 */
	public String[] getAttributeForEachSlice(String key) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return null;
		}
		String[] attributes = new String[this.size()];
		for (int i = 0; i < slices.size(); i++) {
			attributes[i] = getAttribute(key, i);
			if (attributes[i].equals("<<key not found>>")) {
				return attributes;
			}
		}
		return attributes;
	}

	public String[] getAttributeList(String key) {
		return getAttribute(key, false).split("\n");
	}

	/**
	 * Returning a one dimensional array, with the informations inside.
	 * 
	 * @param key
	 *            The String, that should be searched in the header lines.
	 * @param slice
	 *            The Slice, which header should be searched through for the
	 *            given key.
	 */
	public String[] getAttributeList(String key, int slice) {
		return getAttribute(key, slice).split("\n");
	}

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 *            The String, that should be searched in the header lines.
	 * @param slices
	 *            The Slices, which header should be searched through for the
	 *            given key.
	 */
	public String[][] getAttributeList(String key, Vector<Integer> slices) {
		String[][] str = new String[size()][];
		String[] tosplitt = getAttribute(key, slices);
		for (int i = 0; i < tosplitt.length; i++) {
			str[i] = tosplitt[i].split("\n");
		}
		return str;
	}

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 *            The String, that should be searched in the header lines.
	 */
	public String[][] getAttributeListForEachSlice(String key) {
		String[][] str = new String[size()][];
		String[] tosplitt = getAttributeForEachSlice(key);
		for (int i = 0; i < tosplitt.length; i++) {
			str[i] = tosplitt[i].split("\n");
		}
		return str;
	}

	/**
	 * Returning the Data of an Image.
	 * 
	 */
	public ArrayList<ImagePlus> getData() {
		ArrayList<ImagePlus> data = new ArrayList<ImagePlus>();
		for (Image img : slices) {
			data.add(img.getData());
		}
		return data;
	}

	/**
	 * Returning the headers of all images separated in a ArrayList
	 * 
	 */
	public ArrayList<String> getHeader() {
		ArrayList<String> header = new ArrayList<String>(slices.size());
		for (Image img : slices) {
			header.add(img.getHeader());
		}
		return header;
	}

	/**
	 * Returning the type of the first image in the slice. This type should be
	 * the same type of the other i
	 * 
	 */
	public String getImageType() {
		return slices.get(0).getType();
	}

	/**
	 * Returns the specific Image, starting with int i = 0.
	 *
	 * @param i
	 *            The number of the Slice, that should be returned.
	 */
	public Image getSlice(int i) {
		int size = slices.size();
		if (size == 0) {
			System.out
					.println("You can't get a slice, when the volume is empty. Returning null instead.");
			return null;
		} else if (i >= size) {
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

	public TextOptions getTextOptions() {
		return topt;
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

	/**
	 * Setting the TextOptions to the Default setting.
	 */
	public void resetTextOptions() {
		topt = new TextOptions();

		topt.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);
		topt.addSearchOption(TextOptions.ATTRIBUTE_NAME);
		topt.addSearchOption(TextOptions.ATTRIBUTE_VALUE);

		topt.setReturnExpression(TextOptions.ATTRIBUTE_NAME + ": "
				+ TextOptions.ATTRIBUTE_VALUE);
	}

	public void setTextOptions(TextOptions topt) {
		this.topt = topt;
	}

	/**
	 * Returning the number of Images, which are contained in the Volume.
	 */
	public int size() {
		return slices.size();
	}

	public void loadData() {
		for (Image img : slices) {
			img.loadData();
		}
	}

}
