package imagehandling;

interface HeaderExtractor {

	String getHeader(String path);
	
	String getInfo(String path, String item);
	
	String[] getInfo(String path, String items[]);
	
	void extractHeader(String path);
}
