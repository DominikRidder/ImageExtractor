package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is used to save and load the config of the gui.
 * 
 * @author Dominik Ridder
 *
 */
public class ImageExtractorConfig {

	private HashMap<String, String> options = new HashMap<String, String>();
	private String pathConfig;

	/**
	 * Constructor that loads the Config of the ImageExtractor.
	 */
	public ImageExtractorConfig(String filename) {
		pathConfig = filename;
		File file = new File(filename);
		if (!file.exists()) {
			InitCfgFile(file);
		}

		System.out.println("Config: " + file.getAbsolutePath());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (line.length() > 0 && Character.isAlphabetic(line.charAt(0))) {
					String parts[] = line.split("=", 2);
					options.put(parts[0].trim(), parts[1].trim());
				} else if (line.replace("\n", "").replace(" ", "").length() == 0) {
					continue;
				}
			}
		} catch (IOException e) {
			System.out
					.println("Reading the ImageExtractor.cfg Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
		}
	}

	private static void InitCfgFile(File file) {
		try {
			file.createNewFile();
			
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				bw.write("# Image Extractor Config File\n");
				bw.write("#\n");
				bw.write("# You can uncomment the following line, to enable a default starting\n");
				bw.write("# for the filechooser\n");
				bw.write("# StartBrowse = /path/to/dir\n");
			}
		} catch (IOException e) {

		}
	}
	
	/**
	 * Returns the value to a given option Name.
	 * 
	 * @param optionname
	 *            The Name of the Options
	 * @return The value of the Option or null if the option don't exist
	 */
	public String getOption(String optionname) {
		return options.get(optionname);
	}

	/**
	 * This Method sets the parameter of an Option. If the option exist, than
	 * the value is updated. Otherwiese the option is created with the given
	 * value.
	 * 
	 * @param optionname
	 *            The Name of the Option
	 * @param value
	 *            The value, that should be saved for the option
	 */
	public void setOption(String optionname, String value) {
		options.put(optionname, value);
	}

	/**
	 * Saves the Options, that are currently in this objekt.
	 */
	public void save() {
		File file = new File(pathConfig);
		StringBuffer text = new StringBuffer();
		HashSet<String> optionNotFound = new HashSet<String>();

		for (String key : options.keySet()) {
			optionNotFound.add(key);
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					text.append(line + "\n");
				}
				if (line.length() > 0 && Character.isAlphabetic(line.charAt(0))) {
					String parts[] = line.split("=", 2);
					text.append(parts[0].trim() + " = " + options.get(parts[0].trim()) + "\n");
					optionNotFound.remove(parts[0].trim());
				} else if (line.replace("\n", "").replace(" ", "").length() == 0) {
					text.append("\n");
					continue;
				}
			}
		} catch (IOException e) {
			System.out
					.println("Reading the ImageExtractor.cfg Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
		}

		for (String opt : optionNotFound) {
			text.append(opt + " = " + options.get(opt) + "\n");
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(text.toString());
		} catch (IOException e) {
			System.out
					.println("Writing the ImageExtractor.cfg Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
		}
	}
}
