package imagehandling.headerhandling;


/**
 * This interface defines, how an HeaderExtractor can be called, to extract
 * informations out of a given Image type.
 * 
 * @author dridder_local
 *
 */
public interface HeaderExtractor {

	/**
	 * Returns the header to the given image path.
	 * 
	 * @param path
	 * @return The Header, as a String
	 */
	public String getHeader(String path);

	/**
	 * Returns a single Attribute to a given path and the String item is used,
	 * to find the Attribute.
	 * 
	 * @param path
	 * @param item
	 * @return A single Attribute
	 */
	public String getInfo(String path, String item);

	/**
	 * Returns mutliple Attributes to a given path, so the header dont have to
	 * be loaded multiple times.
	 * 
	 * @param path
	 * @param items
	 * @return Mutliple Attributes in a String array
	 */
	String[] getInfo(String path, String items[]);

	/**
	 * Returns to an given image path all informations, that matching to the
	 * regularExpression. The TextOptions defines the matching and the returning
	 * String.
	 * 
	 * @param path
	 * @param regularExpression
	 * @param topt
	 * @return The Header information, that matches the regularExpression in the
	 *         Header combined by the rules of the TextOptions
	 */
	public String getInfo(String path, String regularExpression,
			TextOptions topt);

	/**
	 * Extracting the header of a given image path, to the outputdir folder.
	 * 
	 * @param path
	 * @param outputdir
	 */
	public void extractHeader(String path, String outputdir);
}
