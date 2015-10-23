package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ImageExtractorConfig {

	private String customExternal = null;

	public ImageExtractorConfig() {
		File file = new File("ImageExtractor.config");
		System.out.println(file.getAbsolutePath());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (line.startsWith("External")) {
					customExternal = line.split("=")[1];//.replace("\"", "");
				} else if (line.replace("\n", "").replace(" ", "").length() == 0){
					continue;
				}else {
					System.out
							.println("Unkown line found in the ImageExtractorConfig:"
									+ line + "\n");
				}
			}
		} catch (IOException e) {
			System.out
					.println("Reading the ImageExtractor.config Textfile failed.");
			System.out.println("Guessed dest: " + file.getAbsolutePath());
			System.exit(0);
		}
	}
	
	public String getCustomExternal(){
		return customExternal;
	}
}
