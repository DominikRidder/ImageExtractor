package imagehandling;

import ij.ImagePlus;
import ij.gui.FreehandRoi;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.util.DicomTools;
import ij.util.WildcardMatch;
import imagehandling.datahandling.DataExtractor;
import imagehandling.datahandling.DicomDataExtractor;
import imagehandling.headerhandling.DicomHeaderExtractor;
import imagehandling.headerhandling.HeaderExtractor;
import imagehandling.headerhandling.KeyMap;
import imagehandling.headerhandling.TextOptions;

import java.awt.Polygon;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The Image class is at the moment used for Dicoms and Nifties. You can use it
 * to get informations about the image, to test if you have a Dicom and to
 * Extract the Image of a file or the header of the file.
 * 
 * @author Dominik Ridder
 *
 */
public class Image implements Comparable<Image> {

	@SuppressWarnings("javadoc")
	public static final int ROI_RECTANGLE = 1, ROI_POINT = 2, ROI_OVAL = 3,
			ROI_POLYGON = 4, ROI_FREEHAND = 5;

	/**
	 * Type of the Image. Normally the ending of the file.
	 */
	private String type;

	/**
	 * Path of the Image File.
	 */
	private String path;

	/**
	 * This value can be set with different types of ROI. This roi is added to
	 * the image, when calling getData().
	 */
	private Roi roi;

	/**
	 * Once the data is requestet, the Image class will save the data, so it
	 * dont have to be computed over and over again.
	 */
	private ImagePlus data;

	/**
	 * @param data
	 *            The Data of the Image
	 */
	public void setData(ImagePlus data) {
		this.data = data;
	}

	/**
	 * Simple constructor. The path should be the path of the image.If the name
	 * of the image don't end with a known ending, the images handled as a IMA
	 * image.
	 * 
	 * @param path
	 *            The Location of the Image
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
			if (!Image.isDicom(new File(path).toPath())) {
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
	 * With this Constructor, you can tell the Image class, which type of Image,
	 * belong to this file.
	 * 
	 * @param path
	 *            The Location of the Image
	 * @param type
	 *            The Image extension
	 */
	public Image(String path, String type) {
		this.type = type;
		this.path = path;
	}

	/**
	 * Returning all Implemented KeyWords in a String, where one line is one
	 * Attribute Name.
	 * 
	 * @return The name of all existing KeyMap enumerations
	 * 
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
	 *            A regular expression, that determinates the form of the
	 *            searched KeyWord
	 * @return The KeyWords, that matches the regular expression
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
	 * This method returns the current roi.
	 * 
	 * @return The current roi
	 * 
	 * 
	 */
	public Roi getRoi() {
		return roi;
	}

	/**
	 * Returns the Image type. Known Implementation in this class: IMA and dcm.
	 * 
	 * @return The Image type
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the path of the Image as a String.
	 * 
	 * @return The path of the Image as a String.
	 * 
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returning one or more Attributes, to a given String. If this String
	 * contains "*" or "?" a wildcard match is used to find all matching
	 * Attributes. In this case you have in each row of the String one value.
	 * Else you only have 1 row and 1 value.
	 * 
	 * @param key
	 *            A Part of the line, that your are searching in the header
	 * @return One or more Attributes, to a given String.
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

		topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER + "  "
				+ TextOptions.ATTRIBUTE_NAME + ": "
				+ TextOptions.ATTRIBUTE_VALUE);

		return getAttribute(key, topt);
	}

	/**
	 * Returning the information ,to the given key, and the given textoptions.
	 * 
	 * @param key
	 *            A Part of the line, that your are searching in the header
	 * @param topt
	 *            The Options, for searching and returning the header or a part
	 *            of it
	 * @return The information ,to the given key, and the given textoptions
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
	 * 
	 * @param en
	 *            The enum, that represents the Information in the header
	 * @return The attribute of the Image to the given key.
	 */
	public String getAttribute(KeyMap en) {
		TextOptions topt = new TextOptions();

		topt.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);

		topt.setReturnExpression(TextOptions.ATTRIBUTE_VALUE + "");

		return getAttribute(en, topt);
	}

	/**
	 * Returns the information from the header of the Image to the given enum
	 * and the given Textoption. The searchoptions of the Textoptions are
	 * ignored in this case.
	 * 
	 * @param en
	 *            The enum, that represents the Information in the header
	 * @param topt
	 *            The Options, for searching and returning the header or a part
	 *            of it
	 * @return The information from the header of the Image to the given enum
	 *         and the given Textoption.
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
	 * Returns all key matching separated in an array.
	 * 
	 * @param key
	 *            A Part of the line, that your are searching in the header
	 * 
	 * @return The lines, that contains the key
	 */
	public String[] getAttributeList(String key) {
		return getAttribute(key).split("\n");
	}

	/**
	 * Returns all key matching separated in an array.
	 * 
	 * @param key
	 *            A Part of the line, that your are searching in the header
	 * @param topt
	 *            The Options, for searching and returning the header or a part
	 *            of it
	 * @return The formated lines to the gives key
	 * 
	 */
	public String[] getAttributeList(String key, TextOptions topt) {
		return getAttribute(key, topt).split("\n");
	}

	/**
	 * Returns the Header of the Image, given by the path.
	 * 
	 * @return The Header information of this Image
	 */
	public String getHeader() {
		HeaderExtractor he = null;
		switch (type) {
		case "dcm":
		case "IMA":
			if (data != null) {
				return data.getInfoProperty().toString();
			}
			he = new DicomHeaderExtractor();
			break;
		case "nii":
			return NIFTIVolume.createHeader(data);

		default:
			throw new RuntimeException("The Image Type can't be handeld.");
		}
		return he.getHeader(path);
	}

	/**
	 * Return the Image, that is in the data of this file, as a BufferedImage.
	 * 
	 * @return The Data of this Image
	 */
	public ImagePlus getData() {
		if (data == null) {
			loadData();
		}
		return data;
	}

	/**
	 * Method for loading the data.
	 */
	public void loadData() {
		DataExtractor de = null;
		switch (type) {
		case "dcm":
		case "IMA":
			de = new DicomDataExtractor();
			break;
		default:
			throw new RuntimeException("The Image Type can't be handeld.");
		}
		data = de.getData(path);
	}

	/**
	 * This Method sets and saves the Roi on an Image.
	 * 
	 * @param roi
	 *            The Roi that should be save at this Image.
	 */
	public void setROI(Roi roi) {
		this.roi = roi;
		if (data == null) {
			loadData();
		}
		data.setRoi(roi);
	}

	/**
	 * Creates, sets and saves a Roi on the Image.
	 * 
	 * @param roitype
	 *            the roi type
	 * @param x
	 *            coordinate from the upper left corner of the rectangle, that
	 *            contains the Roi
	 * @param y
	 *            coordinate from the upper left corner of the rectangle, that
	 *            contains the Roi
	 * @param width
	 *            of the rectangle, that contains the Roi
	 * @param height
	 *            of the rectangle, that contains the Roi
	 */
	public void setROI(int roitype, int x, int y, int width, int height) {
		switch (roitype) {
		case ROI_RECTANGLE:
			roi = new Roi(x, y, width, height);
			break;
		case ROI_OVAL:
			roi = new OvalRoi(x, y, width, height);
			break;
		case ROI_POINT:
			roi = new PointRoi(x, y);
			break;
		case ROI_FREEHAND:
			roi = new FreehandRoi(x, y, null);
			break;
		case ROI_POLYGON:
			roi = new PolygonRoi(new Polygon(), PolygonRoi.POLYGON);
			break;
		default:
			roi = null;
			break;
		}
		setROI(roi);
	}

	/**
	 * Sets the roi, with the given position. If the Roi take any with or
	 * height, than this height is set to 10.
	 * 
	 * @param roitype
	 *            The integer that indicates the roi
	 * @param x
	 *            The x coordinate of the Roi
	 * @param y
	 *            The y coordinate of the Roi
	 */
	public void setROI(int roitype, int x, int y) {
		setROI(roitype, x, y, 10, 10);
	}

	/**
	 * This method trys to find out, if the Initialized path can be handeld as a
	 * Dicom.
	 * 
	 * @param path
	 *            The path of the potential Dicom
	 * @return True if the file can may be handled as a Dicom; false otherwiese
	 * 
	 */
	public static boolean isDicom(Path path) {
		ImagePlus imp = new ImagePlus(path.toString());

		KeyMap testdata[] = { KeyMap.KEY_PROTOCOL_NAME, KeyMap.KEY_PATIENT_ID,
				KeyMap.KEY_IMAGE_NUMBER };

		for (KeyMap test : testdata) {
			String k = DicomTools.getTag(imp, test.getValue("IMA"));
			if (k == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates ***.header text document in the given path with the header
	 * information of the Image.
	 */
	public void extractHeader() {
		extractHeader(getParentFolder());
	}

	/**
	 * Creates ***.header text document in the outputdir with the header
	 * information of the Image.
	 * 
	 * @param outputdir
	 *            The Location, where the header files are saved to
	 */
	public void extractHeader(String outputdir) {
		extractHeader(false, outputdir);
	}

	/**
	 * Creates png files of the image, in the folder, where is image is stored
	 * in.
	 */
	public void extractData() {
		extractData(getParentFolder());
	}

	/**
	 * Creates png files of the image, in the outputdir folder.
	 * 
	 * @param outputdir
	 *            The Location, where the Data is saved to
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
		de.extractImage(path, outputdir);
	}

	/**
	 * Extracting the Header to a given outputdir path name. The boolean value
	 * decides if this method throws an Exception, if it failed.
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
	 * @return The Folder, that contains this Image
	 * 
	 */
	public String getParentFolder() {
		File f = new File(path);
		return f.getAbsolutePath().substring(0,
				f.getAbsolutePath().length() - f.getName().length());
	}

	/**
	 * Returns Dicom attributes, to a given enum of the KeyMap.
	 * 
	 * @param en
	 *            The KeyMap enum, that describes the needed header value.
	 * @return The Dicom attribute, that belong to the enum
	 * 
	 */
	public String getAttributeDicom(KeyMap en) {
		String key = en.getValue(type);
		return new DicomHeaderExtractor().getInfo(this.path, key);
	}

	/**
	 * Returns Dicom attributes, to a given enum of the KeyMap.
	 * 
	 * @param en
	 *            The KeyMap enum, that describes the needed header value.
	 * @return The Dicom attributes as a String, that belong to the enums.
	 * 
	 */
	public String[] getAttributesDicom(KeyMap en[]) {
		return Image.getAttributesDicom(path, en);
	}

	/**
	 * Static method to get some Attributes of a Dicom. This can be usefull, if
	 * you call this method often and you dont want to loose to much time on
	 * initializing Images.
	 * 
	 * @param path
	 *            The Path of the Dicom, that contains the needed informations.
	 * @param en
	 *            The KeyMap enums, that describes the needed header values.
	 * @return The needed information of the dicom header
	 * 
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
		KeyMap[] en = { KeyMap.KEY_IMAGE_NUMBER };
		int thisnumb = Integer
				.parseInt(Image.getAttributesDicom(this.path, en)[0]);
		int objnumb = Integer.parseInt(Image.getAttributesDicom(o.path, en)[0]);
		if (thisnumb > objnumb) {
			return 1;
		} else if (thisnumb < objnumb) {
			return -1;
		}
		return 0;
	}

}
