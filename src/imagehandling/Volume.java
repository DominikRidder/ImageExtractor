package imagehandling;

import gui.volumetab.Roi3D;
import gui.volumetab.VolumeTab;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.plugin.Nifti_Reader;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * The Volume class is a class, to work with a single volume. A Volume contains
 * multiple images.
 * 
 * @author dridder_local
 *
 */
public class Volume {

	/**
	 * This method calls the Image.getKeyWords() method. Take a look at the
	 * Java-doc of Image.getKeyWords() for more informations.
	 * 
	 * @return
	 */
	public static String getKeyWords() {
		return Image.getKeyWords();
	}

	/**
	 * This method calls the Image.getKeyWords(str) method. Take a look at the
	 * Java-doc of Image.getKeyWords(String str) for more informations.
	 * 
	 * @return
	 */
	public static String getKeyWords(String str) {
		return Image.getKeyWords(str);
	}

	/**
	 * Path to the folder, that contains the Images of the Volume.
	 */
	private String path;

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
	 * This default construktur should not be used. If you use this method, it
	 * gonna print some information into the console and call System.exit(1).
	 */
	public Volume() {
		System.out
				.println("You have to use the Volume(String path) construktor to work with Volume.\nThe path should be the path of the repository, where the images are stored in.");
		System.exit(1);
	}

	/**
	 * This constructor searching in the given path for files. In this folder
	 * there should only be one type of images. If the names of the images dont
	 * end with a known ending, the images handeld as a IMA image.
	 *
	 * @param path
	 */
	public Volume(String path) {
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

			// sort images
			Collections.sort(slices);
		} else if (path.endsWith(".nii")) {
			Nifti_Reader nr = new Nifti_Reader();
			ImagePlus nifti = nr.load(file.getParent(), file.getName());
			System.out.println(nifti.getT());
			for (int i = 0; i < nifti.getImageStackSize(); i++) {
				Image img = new Image(path, "nii");
				ImagePlus data = new ImagePlus();
				nifti.setSlice(i);
				data.setImage(nifti.getBufferedImage());
				img.setData(data);
				slices.add(img);
			}
		}

	}

	/**
	 * This construktur is used by the gui class. The diffence is, that the
	 * normal construktur would call System.exit(1) if the Volume path is not
	 * correct, while this method throws a RuntimeException.
	 * 
	 * @param path
	 * @param gui
	 */
	public Volume(String path, VolumeTab volumetab) {
		resetTextOptions();
		slices = new ArrayList<Image>();
		this.path = path;

		// getting the files inhabited in the path
		File file = new File(path);
		if (file.isDirectory()) {
			File[] list = file.listFiles();

			if (list == null) {
				throw new RuntimeException(
						"The given Volume path seems to be not correct. Please check the path.");
			}

			// adding the Images
			for (File l : list) {
				try {
					slices.add(new Image(l.getAbsolutePath()));
				} catch (RuntimeException e) {
				}
			}
		} else if (file.getName().endsWith(".nii")) {
			Nifti_Reader nr = new Nifti_Reader();
			ImagePlus nifti = nr.load(file.getParent(), file.getName());
			System.out.println(nifti.getNFrames());
			for (int i = 0; i < nifti.getImageStackSize(); i++) {
				Image img = new Image(path, "nii");
				ImagePlus data = new ImagePlus();
				nifti.setSlice(i);
				data.setImage(nifti.getBufferedImage());
				img.setData(data);
				slices.add(img);
			}
		}

		if (size() == 0) {
			throw new RuntimeException(
					"The given Volume path seems to be not correct. Please check the path.");
		}

		// sort images
		Collections.sort(slices);
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
	 * @param outputdir
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
	 * @return
	 */
	public String getAttribute(KeyMap en) {
		if (en == null) {
			System.out
					.println("The given element is 'null'. Cant search without a real KeyMap enum.");
			return "<<no key given>>";
		}

		// getting the first Attribute, for some comparisons
		int index = 0;
		String str = null;
		while (str == null) {
			try {
				str = getAttribute(en, index++);
			} catch (IndexOutOfBoundsException e) {
				// catching files, that are not part of the images
			}
		}

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

		return str;
	}

	/**
	 * returning the Attribute of the given key + slice number. Use int slice =
	 * 0, for the first slice.
	 *
	 * @param en
	 * @param slice
	 * @return
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
	 * @param key
	 * @param slices
	 * @return
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
	 * @return
	 */
	public String getAttribute(String key) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return "<<no key given>>";
		}

		// getting the last Attribute, for some comparisons
		String str = getAttribute(key, this.size() - 1);
		if (str.equals("<<key not found>>")) {
			return str;
		}

		// if str == "<<no attribute found>>" the attribute isnt set in the
		// header
		if (str.equals("<<no attribute found>>")) {
			return str;
		}

		// ending by i = slices.size-1, because we use this attribute for the
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
	 * which contains the indizies of the slices, which should be used.
	 * 
	 * @param key
	 * @param slices
	 * @return
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
	 * @return
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
	 * searchparameter.
	 * 
	 * @param key
	 * @return
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
		return getAttribute(key).split("\n");
	}

	/**
	 * Returning a one dimensional array, with the informations inside.
	 * 
	 * @param key
	 * @param slice
	 * @return
	 */
	public String[] getAttributeList(String key, int slice) {
		return getAttribute(key, slice).split("\n");
	}

	/**
	 * Returning a two dimensional array, with the informations inside.
	 * 
	 * @param key
	 * @param slices
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
	 */
	public String getImageType() {
		return slices.get(0).getType();
	}

	/**
	 * Returns the specific Image, starting with int i = 0.
	 *
	 * @param i
	 * @return
	 */
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

	public TextOptions getTextOptions() {
		return topt;
	}

	public void setRoi(Roi roi) {
		if (roi instanceof Roi3D) {
			int thickness = Integer.parseInt(slices.get(0).getAttribute(KeyMap.KEY_SLICE_THICKNESS));
			Rectangle rec = roi.getBounds();
			Roi3D roi3 = (Roi3D) roi;
			double radius = roi.getBounds().getWidth()/2;
			int z = roi3.getZ();
			for (int i=0; i<size(); i++){
				if (Math.abs(z-i)*thickness < radius){
					double newr = Math.sqrt(Math.pow(radius, 2)-Math.pow(Math.abs(z-i)*thickness, 2));
					OvalRoi next = new OvalRoi(rec.getX(), rec.getY(), newr,newr);
					slices.get(i).setROI(next);
				}
			}
		} else {
			for (Image img : slices) {
				img.setROI(roi);
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

	// /**
	// * Returning the decay of Signal belonging to different Echoes.
	// */
	// public ArrayList<BufferedImage> getDecayImages(){
	// int size = size();
	// ArrayList<BufferedImage> rightnow = getData();
	// KeyMap[] info = {KeyMap.KEY_ECHO_NUMBERS_S};
	// String[] att = getSlice(size-1).getAttributesDicom(info);
	// int echoNumbers = Integer.parseInt(att[0]);
	// int slices = size/echoNumbers;
	// int width = rightnow.get(0).getWidth();
	// int height = rightnow.get(0).getHeight();
	// int rgbtype = rightnow.get(0).getType();
	//
	// ArrayList<BufferedImage> ret = new ArrayList<>(slices);
	// int values[][][] = new int[slices][echoNumbers][width*height];
	// int sum = 0;
	//
	// for (int s=0; s<slices; s++){
	// BufferedImage next = new BufferedImage(width,height,rgbtype);
	// for (int e=0; e<echoNumbers; e++){
	// BufferedImage val = rightnow.get(s+e*slices);
	// for (int x=0; x<height; x++){
	// for (int y=0; y<width; y++){
	// values[s][e][y+x*width] = val.getRGB(y, x);
	// }
	// }
	// }
	// }
	// System.out.println("done");
	// System.out.println(slices*echoNumbers*height*width+" Pixels");
	//
	// return null;
	// }
}
