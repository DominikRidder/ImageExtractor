package imagehandling;

import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.SortingFocusTraversalPolicy;

import ij.plugin.DICOM;
import ij.util.WildcardMatch;

public class Image implements Comparable {

	private String type;

	private String path;

	private static int found;
	
	/**
	 * Simple constructor. The path should be the path of the image.If the name
	 * of the image dont end with a known ending, the images handeld as a IMA
	 * image.
	 */
	public Image(String path) {
		String type = "";

		String str[] = (path.split("\\."));

		type = str[str.length - 1];

		switch (type) {
		case "dcm":
			break;
		case "IMA":
			break;
		default:
			if (!isImage()) {
				throw new RuntimeException(
						"The given path cant be handeld as an Image");
			}
			type = "IMA";
			break;
		}

		this.type = type;
		this.path = path;
	}

	public Image(String type, String path) {
		this.type = type;
		this.path = path;
	}

	public static String getKeyWords() {
		ArrayList<String> words = new ArrayList<String>();
		String str = "";
		for (KeyMap en : KeyMap.values()) {
			String part = en.name().replace("KEY", "").replace("_", " ")
					.toLowerCase()
					+ "\n";
			words.add(part.substring(1, part.length()));

		}
		Collections.sort(words);

		for (String word : words) {
			str += word;
		}
		str = (str.substring(0, str.length() - 2));
		return str;
	}

	public static String getKeyWords(String st) {
		ArrayList<String> words = new ArrayList<String>();
		String str = "";
		for (KeyMap en : KeyMap.values()) {
			String part = en.name().replace("KEY", "").replace("_", " ")
					.toLowerCase()
					+ "\n";
			words.add(part.substring(1, part.length()));

		}
		Collections.sort(words);

		int i = 0;
		WildcardMatch wm = new WildcardMatch();
		wm.setCaseSensitive(false);
		while (i < words.size()) {
			String word = words.get(i);
			word = word.substring(0, word.length() - 1);
			if (!wm.match(word, st)) {
				words.remove(i);
				i--;
			}
			i++;
		}
		for (String word : words) {
			str += word;
		}
		if (str.length() == 0) {
			return "";
		}
		str = (str.substring(0, str.length() - 1));
		return str;
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public String getAttribute(String key) {
		String attribute = "";
		switch (type) {
		case "dcm":
		case "IMA":
			attribute = getAttributeDicom(key);
			break;
		default:
			System.out
					.println("getting Attributes didnt work. Image Format is not supported.");
			break;
		}
		return attribute;
	}

	/**
	 * Returns the attribute of the Image to the given key.
	 */
	public String getAttribute(KeyMap en) {
		String attribute = "";
		switch (type) {
		case "dcm":
		case "IMA":
			attribute = getAttributeDicom(en);
			break;
		default:
			System.out
					.println("getting Attributes didnt work. Image Format is not supported.");
			break;
		}
		return attribute;
	}

	/**
	 * Returns the Header of the Image, given by the path.
	 */
	public String getHeader() {
		HeaderExtractor he = null;
		switch (type) {
		case "dcm":
		case "IMA":
			he = new DicomHeaderExtractor();
			break;
		default:
			throw new RuntimeException("The Image Type can't be handeld.");
		}
		return he.getHeader(path);
	}

	public BufferedImage getData() {
		DataExtractor he = null;
		switch (type) {
		case "dcm":
		case "IMA":
			he = new DicomDataExtractor();
			break;
		default:
			throw new RuntimeException("The Image Type can't be handeld.");
		}
		return he.getData(path);
	}

	public boolean isImage() {
		switch (type) {
		case "dcm":
		case "IMA":
			try {
				// just to find out if it is a Dicom
				new DICOM().open(path);
				return true;
			} catch (IndexOutOfBoundsException e) {
			}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * Creates ***.header txt document in the given path with the header
	 * information of the Image.
	 */
	public void extractHeader() {
		extractHeader(false);
	}

	public void extractData() {
		// TODO
	}

	protected void extractHeader(boolean bool) {
		HeaderExtractor he = null;
		switch (type) {
		case "dcm":
		case "IMA":
			he = new DicomHeaderExtractor();
			break;
		default:
			throw new RuntimeException("The Image Type can't be handeld.");
		}
		try {
			he.extractHeader(path);
		} catch (Exception e) {
			if (bool) {
				throw new RuntimeException(e.getMessage());
			} else {
				System.out.println(e.getMessage());
			}
		}
	}

	// private String getAttributeDicom2(KeyMap en) {
	// String key = en.getValue(type);
	// String header = getHeader();
	// String attribute = "";
	// try {
	// // cutting the start of the header until the given key appear.
	// // Than splitting the rows and taking the the first row. Finally
	// // getting the attribute by splitting with ": " and taking the
	// // second argument
	// attribute = header.substring(header.indexOf(key),
	// header.length() - 1).split("\n")[0].split(": ")[1];
	// } catch (ArrayIndexOutOfBoundsException
	// | StringIndexOutOfBoundsException e) {
	// //System.out.println("The attribute to the given key " + key
	// // + " wasnt found.");
	// attribute = "<<no attribute found>>";
	// }
	// return attribute;
	// }

	private String getAttributeDicom(KeyMap en) {
		String key = en.getValue(type);
		return new DicomHeaderExtractor().getInfo(this.path, key);
	}

	private String getAttributeDicom(String keyword) {
		String str = "";
		KeyMap enu = null;
		String output = "";
		for (String keywordo : keyword.split("\n")) {
			output = keywordo;
			keywordo = keywordo.replace(" ", "").toLowerCase();
			for (KeyMap en : KeyMap.values()) {
				if (keywordo.equals(en.name().replace("KEY", "")
						.replace("_", "").toLowerCase())) {
					enu = en;
					break;
				}
			}
			if (enu == null) {
				System.out
						.println("The given key is not implemented. Use Image.getKeyWords() or Volume.getKeyWords() \nto get a String with all implemented keys. The keyword is not case-sensitive.");
				str += "<<key not found>>";
			} else {
				str += output + ": " + getAttributeDicom(enu) + "\n";
			}
		}
		return str;
	}

	private String[] getAttributesDicom(KeyMap en[]) {
		return Image.getAttributesDicom(path, en);
	}

	private static String[] getAttributesDicom(String input, KeyMap en[]) {
		String key[] = new String[en.length];
		for (int i = 0; i < key.length; i++) {
			key[i] = en[i].getValue("dcm");
		}
		return new DicomHeaderExtractor().getInfo(input, key);
	}

	// Comparing by ImageNumber
	public int compareTo(Object o) {
		int thisnumb = Integer.parseInt(this.getAttribute("image number")
				.replace(" ", "").replace("\n", "").split(":")[1]);
		int objnumb = Integer.parseInt(((Image) o).getAttribute("image number")
				.replace(" ", "").replace("\n", "").split(":")[1]);
		if (thisnumb > objnumb) {
			return 1;
		} else if (thisnumb < objnumb) {
			return -1;
		}
		return 0;
	}

	public void sortInDir(String dir) {
		Image.sortInDir(path, dir);
	}

	public static void sortInDir(String input, String dir) {
		KeyMap[] info = { KeyMap.KEY_PATIENT_ID, KeyMap.KEY_PROTOCOL_NAME,
				KeyMap.KEY_IMAGE_NUMBER, KeyMap.KEY_MODALITY };
		String[] att = getAttributesDicom(input, info);

		// Check existing
		StringBuilder path = new StringBuilder();
		path.append(dir + "/" + att[0]);
		existOrCreate(path);
		int i;
		loop: for (i = 1; i < 100; i++) {
			File test2 = new File(path.toString() + "/" + att[1]+ "_"+i);
			if (!test2.exists()) {
				break;
			}
			for (int j = 1; j < 10; j++) {
				File test3 = new File(path.toString() + "/" + att[1]+ "_"+i
						+ "/000" + j + ".dcm");
				if (test3.exists()) {
					KeyMap oneElement[] = { KeyMap.KEY_MODALITY };
					if (att[3].equals(Image.getAttributesDicom(
							test3.getAbsolutePath(), oneElement)[0])) {
						break;
					} else {
						continue loop;
					}
				}
			}
			break;
		}
		path.append("/" + att[1]+ "_"+i);
		existOrCreate(path);
		path.append("/" + fourDigits(att[2]) + ".dcm");

		// Copy data
		File test = new File(path.toString());
		if (!test.exists()) {
			try {
				Files.copy(new File(input).toPath(), test.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static String fourDigits(String image_number) {
		StringBuilder fourdigits = new StringBuilder(4);
		for (int i = 0; i < 4 - image_number.length(); i++) {
			fourdigits.append("0");
		}
		return fourdigits.toString() + image_number;
	}

	private static void existOrCreate(StringBuilder path) {
		File test = new File(path.toString());
		if (!test.exists()) {
			test.mkdir();
		}
	}

	public static void searchAndSortIn(String searchin, String sortInDir) {
		File file = new File(searchin);
		File[] list = file.listFiles();

		found=0;
		if (list == null) {
			System.out.println("The Given Path seems to be incorrect.");
			return;
		}

		existOrCreate(new StringBuilder(sortInDir));
		
		// README
		File test = new File(sortInDir + "/README");
		if (!test.exists()) {
			try {
				test.createNewFile();
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(
						test.getAbsolutePath()))) {
					bw.write("The Sorting structur is the following:\n"
							+ sortInDir
							+ "\tPatient id/Protocol Name_DEPENDS/\nAdditionally the name of a Dicom is renamed to his Image number + .dcm\nThe DEPENDS value is equal to the first dir, where the Modality is equal to the Image Modality.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (File l : list) {
			if (l.getAbsolutePath().endsWith(".IMA")
					|| l.getAbsolutePath().endsWith(".dcm")) {
				Image.sortInDir(l.getAbsolutePath(), sortInDir);
				if (++found%50 == 0){
					System.out.println("I found and sorted so far "+found+" Dicoms.");
				}
			} else {
				Image.searchAndSortInReku(l.getAbsolutePath(), sortInDir);
			}
		}
	}

	public static void searchAndSortInReku(String searchin, String sortInDir) {
		File file = new File(searchin);
		File[] list = file.listFiles();

		if (list == null) {
			return;
		}

		for (File l : list) {
			if (l.getAbsolutePath().endsWith(".IMA")
					|| l.getAbsolutePath().endsWith(".dcm")) {
				Image.sortInDir(l.getAbsolutePath(), sortInDir);
				if (++found%50 == 0){
					System.out.println("I found and sorted so far "+found+" Dicoms.");
				}
			} else {
				searchAndSortInReku(l.getAbsolutePath(), sortInDir);
			}
		}
	}

}
