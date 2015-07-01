package imagehandling;

interface HeaderExtractor {

	String getHeader(String path);
	
	String getInfo(String path, String item);
	
	String[] getInfo(String path, String items[]);
	
	public String getInfo(String path, String regularExpression, TextOptions topt);
	
	void extractHeader(String path, String outputdir);
}
