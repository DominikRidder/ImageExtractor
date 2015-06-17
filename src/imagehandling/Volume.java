package imagehandling;

import imagehandling.GUI.VolumeTab;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class Volume {

	private String path;

	private ArrayList<Image> slices;

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
		slices = new ArrayList<Image>();
		this.path = path;

		// getting the files inhabited in the path
		File file = new File(path);
		File[] list = file.listFiles();

		if (list == null) {
			System.out
					.println("The given Volume path seems to be not correct. Please check the path.");
			System.exit(1);
		}

		// adding the Images
		for (File l : list) {
			try {
				slices.add(new Image(l.getAbsolutePath()));
			} catch (RuntimeException e) {
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
	protected Volume(String path, GUI gui) {
		slices = new ArrayList<Image>();
		this.path = path;

		// getting the files inhabited in the path
		File file = new File(path);
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

		// sort images
		Collections.sort(slices);
	}
	/**
	 * This construktur is used by the gui class. The diffence is, that the
	 * normal construktur would call System.exit(1) if the Volume path is not
	 * correct, while this method throws a RuntimeException.
	 * 
	 * @param path
	 * @param gui
	 */
	protected Volume(String path, VolumeTab volumetab) {
		slices = new ArrayList<Image>();
		this.path = path;

		// getting the files inhabited in the path
		File file = new File(path);
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

		if (size() == 0){
			throw new RuntimeException(
					"The given Volume path seems to be not correct. Please check the path.");
		}
		
		// sort images
		Collections.sort(slices);
	}

	/**
	 * Returns the specific Image, starting with int i = 0
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
							+ size
							+ " in this case.\nReturning instead the first slice (0).");
			return slices.get(0);
		}
		return slices.get(i);
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
	 * Returning the number of Images, which are contained in the Volume.
	 */
	public int size() {
		return slices.size();
	}

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

	public String getAttribute(String key) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return "<<no key given>>";
		}

		// getting the first Attribute, for some comparisons
		String str = getAttribute(key, 0);
		if (str.equals("<<key not found>>")) {
			return str;
		}

		// if str == "<<no attribute found>>" the attribute isnt set in the
		// header
		if (str.equals("<<no attribute found>>")) {
			return str;
		}

		// starting with i=1, because we already have str = 'slice(0)'...
		for (int i = 1; i < slices.size(); i++) {
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

	/**
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
			att[index++] = this.slices.get(slice).getAttribute(key);
		}
		return att;
	}

	public String[] getAttribute(KeyMap en, Vector<Integer> slices) {
		String[] att = new String[slices.size()];
		int index = 0;
		Integer[] a = new Integer[0];
		for (int slice : slices.toArray(a)) {
			att[index++] = this.slices.get(slice).getAttribute(en);
		}
		return att;
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
		return slices.get(slice).getAttribute(en);
	}

	public String getAttribute(String key, int slice) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return "<<no key given>>";
		}
		return slices.get(slice).getAttribute(key);
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

	public String[] getAttributeList(String key) {
		return getAttribute(key).split("\n");
	}

	public String[][] getAttributeList(String key, Vector<Integer> slices) {
		String[][] str = new String[size()][];
		String[] tosplitt = getAttribute(key, slices);
		for (int i=0; i<tosplitt.length; i++){
			str[i] = tosplitt[i].split("\n");
		}
		return str;
	}

	public String[] getAttributeList(String key, int slice){
		return getAttribute(key, slice).split("\n");
	}
	
	public String[][] getAttributeListForEachSlice(String key){
		String[][] str = new String[size()][];
		String[] tosplitt = getAttributeForEachSlice(key);
		for (int i=0; i<tosplitt.length; i++){
			str[i] = tosplitt[i].split("\n");
		}
		return str;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<BufferedImage> getData() {
		ArrayList<BufferedImage> data = new ArrayList<BufferedImage>();
		for (Image img : slices) {
			data.add(img.getData());
		}
		return data;
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
	 * Creates png files of the images, in the folder, where is image is stored
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
}
