package imagehandling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import ij.plugin.DICOM;
import ij.util.WildcardMatch;

/**
 * The Image class is at the moment only used for Dicoms. You can use it to get
 * informations about the dicom, to test if you have a Dicom and to excract the
 * Image of a Dicom or the header of the Dicom.
 * 
 * @author dridder_local
 *
 */
public class Image implements Comparable<Image> {

	/**
	 * Type of the Image. Normally the ending of the file.
	 */
	private String type;

	/**
	 * Path of the Image File.
	 */
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

	/**
	 * With this Constructur, you can tell the Image class, which type of Image,
	 * belong to this file.
	 * 
	 * @param path
	 * @param type
	 */
	public Image(String path, String type) {
		this.type = type;
		this.path = path;
	}

	/**
	 * Returning all Implemented KeyWords in a String, where one line is one
	 * Attribute Name.
	 * 
	 * @return
	 */
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

	/**
	 * This static method searching for Attribute Names, which match to a given
	 * regular expression.
	 * 
	 * @param regularExpression
	 * @return
	 */
	public static String getKeyWords(String regularExpression) {
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
				if (!wm.match(word, regularExpression)) {
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

	/**
	 * Returns the Image typ. Known Implementation in this class: IMA and dcm.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the path of the Image as a String.
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returning one or more Attriubtes, to a given String. If this String
	 * contains "*" or "?" a wildcard match is used to find all matching
	 * Attributes. In this case you have in each row of the String one value.
	 * Else you only have 1 row and 1 value.
	 * 
	 * @param key
	 * @return
	 */
	public String getAttribute(String key) {
		TextOptions topt = new TextOptions();

		if (key.contains("*") | key.contains("?")) {
			topt.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);
			topt.addSearchOption(TextOptions.ATTRIBUTE_NAME);
			topt.addSearchOption(TextOptions.ATTRIBUTE_VALUE);
		} else {
			key = "*" + key + "*";
			topt.addSearchOption(TextOptions.ATTRIBUTE_NAME);
		}

		topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER+"  "+TextOptions.ATTRIBUTE_NAME+": "+TextOptions.ATTRIBUTE_VALUE);

		return getAttribute(key, topt);
	}

	/**
	 * Returning the information ,to the given key, and the given textoptions.
	 * 
	 * @param key
	 * @param topt
	 * @return
	 */
	public String getAttribute(String key, TextOptions topt) {
		if (!key.contains("*") && !key.contains("?")) {
			key = "*" + key + "*";
		}
		HeaderExtractor he = null;
		switch (type) {
		case "dcm":
		case "IMA":
			he = new DicomHeaderExtractor();
			break;
		default:
			System.out
					.println("getting Attributes didnt work. Image Format is not supported.");
			break;
		}
		return he.getInfo(path, key, topt);
	}

	/**
	 * Returns the attribute of the Image to the given key.
	 */
	public String getAttribute(KeyMap en) {
		TextOptions topt = new TextOptions();

		topt.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);

		topt.setReturnExpression(TextOptions.ATTRIBUTE_VALUE+"");

		return getAttribute(en, topt);
	}

	/**
	 * Returns the a information from the header of the Image to the given enum
	 * and the given Textoption. The searchoptions of the Textoptions are
	 * ignored in this case.
	 * 
	 * @param en
	 * @param topt
	 * @return
	 */
	public String getAttribute(KeyMap en, TextOptions topt) {
		TextOptions to = new TextOptions();
		to.setReturnExpression(topt.getReturnExpression());
		to.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);

		HeaderExtractor he = null;
		switch (type) {
		case "dcm":
		case "IMA":
			he = new DicomHeaderExtractor();
			break;
		default:
			System.out
					.println("getting Attributes didnt work. Image Format is not supported.");
			break;
		}
		if (he == null) {
			return null;
		}

		return he.getInfo(path, en.getValue(type) + "*", to);
	}

	/**
	 * Returns all key matching sepperated in an array.
	 * 
	 * @param key
	 * @param topt
	 * @return
	 */
	public String[] getAttributeList(String key) {
		return getAttribute(key).split("\n");
	}

	/**
	 * Returns all key matching sepperated in an array.
	 * 
	 * @param key
	 * @param topt
	 * @return
	 */
	public String[] getAttributeList(String key, TextOptions topt) {
		return getAttribute(key, topt).split("\n");
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

	/**
	 * Return the Image, that is in the data of this file, as a BufferedImage.
	 * 
	 * @return
	 */
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

	/**
	 * This method trys to find out, if the Initialized path is a Image.
	 * 
	 * @return
	 */
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
		extractHeader(getFatherFolder());
	}

	/**
	 * Creates ***.header txt document in the outputdir with the header
	 * information of the Image.
	 * 
	 * @param outputdir
	 */
	public void extractHeader(String outputdir) {
		extractHeader(false, outputdir);
	}

	/**
	 * Creates png files of the image, in the folder, where is image is stored
	 * in.
	 */
	public void extractData() {
		extractData(getFatherFolder());
	}

	/**
	 * Creates png files of the image, in the outputdir folder.
	 * 
	 * @param outputdir
	 */
	public void extractData(String outputdir) {
		DataExtractor de = null;
		switch (type) {
		case "dcm":
		case "IMA":
			de = new DicomDataExtractor();
			break;
		default:
			throw new RuntimeException("The Image Type can't be handeld.");
		}
		de.extractData(path, outputdir);
	}

	/**
	 * Extracting the Header to a given outputdir path name. The boolean value
	 * decides if this method throws an Exception, if it failed. This is needed,
	 * for
	 * 
	 * @param throwException
	 * @param outputdir
	 */
	protected void extractHeader(boolean throwException, String outputdir) {
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
			he.extractHeader(path, outputdir);
		} catch (Exception e) {
			if (throwException) {
				throw new RuntimeException(e.getMessage());
			} else {
				System.out.println("Extracting the header failed.");
			}
		}
	}

	/**
	 * Return a String, which contains the Absolute path name of the folder,
	 * which is above this image.
	 * 
	 * @return
	 */
	public String getFatherFolder() {
		File f = new File(path);
		return f.getAbsolutePath().substring(0,
				f.getAbsolutePath().length() - f.getName().length());
	}

	/**
	 * Returns Dicom attributes, to a given enum of the KeyMap.
	 * 
	 * @param en
	 * @return
	 */
	public String getAttributeDicom(KeyMap en) {
		String key = en.getValue(type);
		return new DicomHeaderExtractor().getInfo(this.path, key);
	}

	public String[] getAttributesDicom(KeyMap en[]) {
		return Image.getAttributesDicom(path, en);
	}

	/**
	 * Static method to get some Attributes of a Dicom. This can be usefull, if
	 * you call this method often and you dont want to loose to much time on
	 * initializing Images.
	 * 
	 * @param path
	 * @param en
	 * @return
	 */
	public static String[] getAttributesDicom(String path, KeyMap en[]) {
		String key[] = new String[en.length];
		for (int i = 0; i < key.length; i++) {
			key[i] = en[i].getValue("dcm");
		}
		return new DicomHeaderExtractor().getInfo(path, key);
	}

	/**
	 * This method comparing two Images, by their Image Number.
	 */
	public int compareTo(Image o) {
		KeyMap[] en = {KeyMap.KEY_IMAGE_NUMBER};
		int thisnumb = Integer.parseInt(Image.getAttributesDicom(this.path, en)[0]);
		int objnumb = Integer.parseInt(Image.getAttributesDicom(o.path, en)[0]);
		if (thisnumb > objnumb) {
			return 1;
		} else if (thisnumb < objnumb) {
			return -1;
		}
		return 0;
	}

}
