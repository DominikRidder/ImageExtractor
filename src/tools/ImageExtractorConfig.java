package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ImageExtractorConfig {

	private HashMap<String, String> options = new HashMap<String, String>();

	public ImageExtractorConfig() {
		File file = new File("ImageExtractor.config");
		System.out.println("Config: "+file.getAbsolutePath());
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

	public String getOption(String optionname) {
		return options.get(optionname);
	}
	
	public void setOption(String optionname, String value){
		options.put(optionname, value);
	}

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
					text.append(parts[0] + "=" + options.get(parts[0])+"\n");
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
