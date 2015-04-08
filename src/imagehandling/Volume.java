package imagehandling;

import java.io.File;
import java.util.ArrayList;

public class Volume {

	private ArrayList<Image> slices;
	
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
	
	protected Volume(String path, Gui gui) {
		slices = new ArrayList<Image>();

		// getting the files inhabited in the path
		File file = new File(path);
		File[] list = file.listFiles();

		if (list == null) {
			throw new RuntimeException("The given Volume path seems to be not correct. Please check the path.");
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
	 * returns the specific Image, starting with int i = 0
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

	public static String getKeyWords() {
		return Image.getKeyWords();
	}

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
	public ArrayList<String> getAttributeList(KeyMap en) {
		if (en == null) {
			System.out
					.println("The given element is 'null'. Cant search without a real KeyMap enum.");
			return new ArrayList<String>();
		}
		ArrayList<String> attributes = new ArrayList<String>();
		for (int i = 0; i < slices.size(); i++) {
			attributes.add(getAttribute(en, i));
		}
		return attributes;
	}

	public ArrayList<String> getAttributeList(String key) {
		if (key == null) {
			System.out
					.println("The given element is 'null'. Cant search without a String.");
			return new ArrayList<String>();
		}
		ArrayList<String> attributes = new ArrayList<String>();
		for (int i = 0; i < slices.size(); i++) {
			attributes.add(getAttribute(key, i));
			if (attributes.get(i).equals("<<key not found>>")) {
				return attributes;
			}
		}
		return attributes;
	}

	/**
	 * returning the headers of all images separated in a ArrayList
	 * 
	 * @return
	 */
	public ArrayList<String> getHeader() {
		ArrayList<String> header = new ArrayList<String>();
		for (Image img : slices) {
			header.add(img.getHeader());
		}
		return header;
	}

	// This method maybe have to be chnaged, if u dont want the Data in form of
	// an ArrayList
	public ArrayList<String> getData() {
		ArrayList<String> data = new ArrayList<String>();
		for (Image img : slices) {
			data.add(img.getData());
		}
		return data;
	}

	/**
	 * Creates ***.header txt documents in the given path with the header
	 * information.
	 */
	public void extractHeader() {
		int excounter = 0;
		for (Image img : slices) {
			try {
				img.extractHeader(true);
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

	// This method dont work yet. This method should later create files with the
	// data of the given volume. Implement some kind of DataExtractor therefore.
	public void extractData() {
		for (Image img : slices) {
			img.extractData();
		}
	}
}
