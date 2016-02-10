package imagehandling.headerhandling;

import java.util.HashSet;

/**
 * The TextOptions are used to define the Search and Return options for the
 * getAttribute methods of volume and Image.
 * <p>
 * The Static int values, are 7,8,9. If you use the returnexpression, than you should
 * evoid these values.
 * 
 * @author dridder_local
 *
 */
public class TextOptions {
	// Its important, that the Length of these Attribute is == 1, if make a
	// String like TextOptions.ATTRIBUTE_EXAMPLE+"".
	/**
	 * TextOptions.ATTRIBUTE_NUMBER represents the Attribute number in the
	 * searchoption, aswell as in the returnexpression.
	 */
	public static final int ATTRIBUTE_NUMBER = 7;
	/**
	 * TextOptions.ATTRIBUTE_NUMBER represents the Attribute name in the
	 * searchoption, aswell as in the returnexpression.
	 */
	public static final int ATTRIBUTE_NAME = 8;
	/**
	 * TextOptions.ATTRIBUTE_NUMBER represents the Attribute value in the
	 * searchoption, aswell as in the returnexpression.
	 */
	public static final int ATTRIBUTE_VALUE = 9;
	/**
	 * This String defines the return String of the getAttribute method.
	 */
	private String returnexp = TextOptions.ATTRIBUTE_NAME + ": "
			+ TextOptions.ATTRIBUTE_VALUE;
	private HashSet<Integer> searchoptions;

	/**
	 * The ReturnExpression defines, what the getAttribute method should return.
	 * For example:
	 * <p>
	 * <p>
	 * TextOptions topt = new TextOptions();
	 * <p>
	 * topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER + " "+
	 * TextOptions.ATTRIBUTE_NAME + ": "+ TextOptions.ATTRIBUTE_VALUE);
	 * <p>
	 * System.out.println(vol.getAttribute("echo"));
	 * <p>
	 * <p>
	 * Output:
	 * <p>
	 * 0018,0081 Echo Time: 3.3
	 * <p>
	 * 0018,0086 Echo Numbers(s): 1
	 * <p>
	 * 0018,0091 Echo Train Length: 1
	 * <p>
	 * 
	 * @param returnexp
	 */
	public void setReturnExpression(String returnexp) {
		this.returnexp = returnexp;
	}

	/**
	 * This method returns the String, which defines the output of a
	 * getAttribute method.
	 * 
	 */
	public String getReturnExpression() {
		return returnexp;
	}

	/**
	 * This method returns the searchoptions. The HashSet is not copyd.
	 * 
	 */
	public HashSet<Integer> getSearchOptions() {
		return searchoptions;

	}

	/**
	 * With this method, you can set all searchoptions directly.
	 * 
	 * @param textoptionsvalues
	 */
	public void setSearchOptions(int[] textoptionsvalues) {
		searchoptions = new HashSet<Integer>();
		for (int i : textoptionsvalues) {
			searchoptions.add(i);
		}
	}

	/**
	 * This method adds a searchoption to the searchoption HashSet.
	 * 
	 * @param textoptionvalue
	 */
	public void addSearchOption(int textoptionvalue) {
		searchoptions.add(textoptionvalue);
	}

	/**
	 * This method removes one textoption attribute from the searchoptions
	 * HashSet.
	 * 
	 * @param textoptionvalue
	 */
	public void removeSearchOption(int textoptionvalue) {
		searchoptions.remove(textoptionvalue);
	}

	/**
	 * Default Constructur.
	 */
	public TextOptions() {
		searchoptions = new HashSet<Integer>();
	}
}
