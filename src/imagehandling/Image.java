package imagehandling;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import ij.plugin.DICOM;
import ij.util.WildcardMatch;

public class Image implements Comparable<Image> {

	private String type;

	private String path;

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
			try {
				if (!wm.match(word, st)) {
					words.remove(i);
					i--;
				}
			} catch (StringIndexOutOfBoundsException e) {
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
		if (key.contains("*") || key.contains("?")) {
			return getAttribute(Image.getKeyWords(key));
		}
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

	@SuppressWarnings("unused")
	private String[] getAttributesDicom(KeyMap en[]) {
		return Image.getAttributesDicom(path, en);
	}

	public static String[] getAttributesDicom(String input, KeyMap en[]) {
		String key[] = new String[en.length];
		for (int i = 0; i < key.length; i++) {
			key[i] = en[i].getValue("dcm");
		}
		return new DicomHeaderExtractor().getInfo(input, key);
	}

	// Comparing by ImageNumber
	public int compareTo(Image o) {
		int thisnumb = Integer.parseInt(this.getAttribute("image number")
				.replace(" ", "").replace("\n", "").split(":")[1]);
		int objnumb = Integer.parseInt(o.getAttribute("image number")
				.replace(" ", "").replace("\n", "").split(":")[1]);
		if (thisnumb > objnumb) {
			return 1;
		} else if (thisnumb < objnumb) {
			return -1;
		}
		return 0;
	}

}
