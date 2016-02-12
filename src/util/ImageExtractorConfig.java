package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class is used to save and load the config of the gui.
 * 
 * @author Dominik Ridder
 *
 */
public class ImageExtractorConfig {

	private HashMap<String, String> options = new HashMap<String, String>();

	/**
	 * Constructor that loads the Config of the ImageExtractor.
	 */
	public ImageExtractorConfig() {
		File file = new File("ImageExtractor.config");
		System.out.println("Config: " + file.getAbsolutePath());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (line.length() > 0 && Character.isAlphabetic(line.charAt(0))) {
					String parts[] = line.split("=", 2);
					options.put(parts[0], parts[1]);
				} else if (line.replace("\n", "").replace(" ", "").length() == 0) {
					continue;
				}
			}
		} catch (IOException e) {
			System.out
					.println("Reading the ImageExtractor.config Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
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
		File file = new File("ImageExtractor.config");
		StringBuffer text = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					text.append(line + "\n");
				}
				if (line.length() > 0 && Character.isAlphabetic(line.charAt(0))) {
					String parts[] = line.split("=", 2);
					text.append(parts[0] + "=" + options.get(parts[0]) + "\n");
				} else if (line.replace("\n", "").replace(" ", "").length() == 0) {
					text.append("\n");
					continue;
				}
			}
		} catch (IOException e) {
			System.out
					.println("Reading the ImageExtractor.config Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(text.toString());
		} catch (IOException e) {
			System.out
					.println("Writing the ImageExtractor.config Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
		}
	}
}
