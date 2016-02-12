package imagehandling.headerhandling;

/**
 * This interface defines, how an HeaderExtractor can be called, to extract
 * informations out of a given Image type.
 * 
 * @author Dominik Ridder
 *
 */
public interface HeaderExtractor {

	/**
	 * Returns the header to the given image path.
	 * 
	 * @param path
	 *            The Filesource, that containing the header information.
	 * @return The Header, as a String
	 */
	public String getHeader(String path);

	/**
	 * Returns a single Attribute to a given path and the String item is used,
	 * to find the Attribute.
	 * 
	 * @param path
	 *            The Filepath of the File, that contains the header
	 * @param item
	 *            The value, that should be searched in the header
	 * @return A single Attribute.
	 */
	public String getInfo(String path, String item);

	/**
	 * Returns mutliple Attributes to a given path, so the header dont have to
	 * be loaded multiple times.
	 * 
	 * @param path
	 *            The Filepath of the file, that contains the header
	 * @param items
	 *            The value, for which is searched for in the header
	 * @return Mutliple Attributes in a String array
	 */
	String[] getInfo(String path, String items[]);

	/**
	 * Returns to an given image path all informations, that matching to the
	 * regularExpression. The TextOptions defines the matching and the returning
	 * String.
	 * 
	 * @param path
	 *            The Path of the File, that contains the header.
	 * @param regularExpression
	 *            A String for which is searched in the header.
	 * @param topt
	 *            The TextOptions, that determinate, how the header lines are
	 *            returned
	 * @return The Header information, that matches the regularExpression in the
	 *         Header combined by the rules of the TextOptions
	 */
	public String getInfo(String path, String regularExpression,
			TextOptions topt);

	/**
	 * Extracting the header of a given image path, to the outputdir folder.
	 * 
	 * @param path
	 *            The Filesourcedestionation, where the header should be readed
	 *            from.
	 * @param outputdir
	 *            The Filedestination of the header files.
	 */
	public void extractHeader(String path, String outputdir);
}
