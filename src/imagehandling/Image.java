package imagehandling;

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

	private String getAttributeDicom(KeyMap en) {
		String key = en.getValue(type);
		String header = getHeader();
		String attribute = "";
		try {
			// cutting the start of the header until the given key appear.
			// Than splitting the rows and taking the the first row. Finally
			// getting the attribute by splitting with ": " and taking the
			// second argument
			attribute = header.substring(header.indexOf(key),
					header.length() - 1).split("\n")[0].split(": ")[1];
		} catch (ArrayIndexOutOfBoundsException
				| StringIndexOutOfBoundsException e) {
			//System.out.println("The attribute to the given key " + key
			//		+ " wasnt found.");
			attribute = "<<no attribute found>>";
		}
		return attribute;
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
	
	public void sortInDir(String dir){
		String study_id = this.getAttribute(KeyMap.KEY_STUDY_ID).replace(" ", "");
		String patient_id = this.getAttribute(KeyMap.KEY_PATIENT_ID).replace(" ", "");
		String patients_birth_date = this.getAttribute(KeyMap.KEY_PATIENTS_BIRTH_DATE).replace(" ", "");
		String protocol_name = this.getAttribute(KeyMap.KEY_PROTOCOL_NAME).replace(" ", "");
		String image_number = this.getAttribute(KeyMap.KEY_IMAGE_NUMBER).replace(" ", "");
		
		StringBuilder path = new StringBuilder();
		path.append(dir);
		existOrCreate(path);
		path.append("/"+patient_id);
		existOrCreate(path);
		path.append("/"+protocol_name+"_"+study_id);
		existOrCreate(path);
		path.append("/"+patients_birth_date);
		existOrCreate(path);
		path.append("/"+image_number+".IMA");
		
		File test = new File(path.toString());
		if (!test.exists()){
			try{
				Files.copy(new File(this.path).toPath(),test.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		test = new File(dir + "/README");
		if (!test.exists()){
			try{
				test.createNewFile();
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(test.getAbsolutePath()))){
					bw.write("The Sorting structur is the following:\n"+dir+"\tPatient id/Protocol Name _ Study id/Patient Birth Date/\nAdditionally the name of a Dicom is renamed to his Image number.");
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private static void existOrCreate(StringBuilder path){
		File test = new File(path.toString());
		if (!test.exists()){
			test.mkdir();
		}
	}
	
	public static void searchAndSortIn(String searchin, String sortInDir){
		File file = new File(searchin);
		File[] list = file.listFiles();
		
		if (list == null){
			return;
		}
		
		for (File l: list){
			if (l.getAbsolutePath().endsWith(".IMA") || l.getAbsolutePath().endsWith(".dcm")){
				new Image(l.getAbsolutePath()).sortInDir(sortInDir);
			}else{
				searchAndSortIn(l.getAbsolutePath(), sortInDir);
			}
		}
	}

}
