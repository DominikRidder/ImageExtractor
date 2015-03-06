package imagehandling;

interface HeaderExtractor {

	String getHeader(String path);
	
	void extractHeader(String path);
}
