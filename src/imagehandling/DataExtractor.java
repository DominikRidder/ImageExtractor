package imagehandling;

import java.awt.image.BufferedImage;

interface DataExtractor {
	
	BufferedImage getData(String path);

	void extractData(String path, String outputdir);
	
}
