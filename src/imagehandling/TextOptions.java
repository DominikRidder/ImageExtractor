package imagehandling;

import java.util.HashSet;

/**
 * The TextOptions are used to set the Search and Return options for the
 * getAttribute methods of volume and Image.
 * 
 * @author dridder_local
 *
 */
public class TextOptions {
	// Its important, that the Length of these Attribute is == 1, if make a String like TextOptions.ATTRIBUTE_EXAMPLE+"".
	public static final int ATTRIBUTE_NUMBER = 7;
	public static final int ATTRIBUTE_NAME = 8;
	public static final int ATTRIBUTE_VALUE = 9;
	/**
	 * This String defines the return String of the getAttribute method.
	 */
	private String returnexp = TextOptions.ATTRIBUTE_NAME+": "+TextOptions.ATTRIBUTE_VALUE;
	private HashSet<Integer> searchoptions;

	/**
	 * The ReturnExpression defines, what the getAttribute method should return. For example:<p>
	 * <p>
	 * TextOptions topt = new TextOptions();<p>
	 * topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER + " "+ TextOptions.ATTRIBUTE_NAME + ": "+ TextOptions.ATTRIBUTE_VALUE);<p>
	 * System.out.println(vol.getAttribute("echo"));<p>
	 * <p>
	 * Output:<p>
	 * 0018,0081 Echo Time: 3.3<p>
	 * 0018,0086 Echo Numbers(s): 1<p>
	 * 0018,0091 Echo Train Length: 1<p>
	 * @param returnexp
	 */
	public void setReturnExpression(String returnexp) {
		this.returnexp = returnexp;
	}

	public String getReturnString() {
		return returnexp;
	}

	public HashSet<Integer> getSearchOptions() {
		return searchoptions;

	}

	public void setSearchOptions(int[] textoptionsvalues) {
		searchoptions = new HashSet<Integer>();
		for (int i : textoptionsvalues) {
			searchoptions.add(i);
		}
	}

	public void addSearchOption(int textoptionvalue) {
		searchoptions.add(textoptionvalue);
	}

	public void removeSearchOption(int textoptionvalue){
		searchoptions.remove(textoptionvalue);
	}


	public TextOptions() {
		searchoptions = new HashSet<Integer>();
	}
}
