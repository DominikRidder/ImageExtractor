package imagehandling.headerhandling;

import java.util.HashSet;

/**
 * The TextOptions are used to define the Search and Return options for the
 * getAttribute methods of volume and Image.
 * <p>
 * The Static int values, are 7,8,9. If you use the return expression, than you
 * should avoid these values.
 * 
 * @author Dominik Ridder
 *
 */
public class TextOptions {
	// Its important, that the Length of these Attribute is == 1, for a
	// String like TextOptions.ATTRIBUTE_EXAMPLE+"".
	/**
	 * TextOptions.ATTRIBUTE_NUMBER represents the Attribute number in the
	 * search option, as well as in the return expression.
	 */
	public static final int ATTRIBUTE_NUMBER = 7;
	/**
	 * TextOptions.ATTRIBUTE_NUMBER represents the Attribute name in the search
	 * option, as well as in the return expression.
	 */
	public static final int ATTRIBUTE_NAME = 8;
	/**
	 * TextOptions.ATTRIBUTE_NUMBER represents the Attribute value in the search
	 * option, as well as in the return expression.
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
	 * TextOptions topt = new TextOptions();
	 * <p>
	 * topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER + " "+
	 * TextOptions.ATTRIBUTE_NAME + ": "+ TextOptions.ATTRIBUTE_VALUE);
	 * <p>
	 * System.out.println(vol.getAttribute("echo"));
	 * <p>
	 * Output:
	 * <p>
	 * 0018,0081 Echo Time: 3.3
	 * <p>
	 * 0018,0086 Echo Numbers(s): 1
	 * <p>
	 * 0018,0091 Echo Train Length: 1
	 * 
	 * @param returnexp
	 *            The String, that represents the search option
	 */
	public void setReturnExpression(String returnexp) {
		this.returnexp = returnexp;
	}

	/**
	 * This method returns the String, which defines the output of a
	 * getAttribute method.
	 * 
	 * @return The current ReturnExpression
	 * 
	 */
	public String getReturnExpression() {
		return returnexp;
	}

	/**
	 * This method returns the search options. The HashSet is not copied.
	 * 
	 * @return The Options, listed in a HashSet&lt;Integer&gt;
	 */
	public HashSet<Integer> getSearchOptions() {
		return searchoptions;

	}

	/**
	 * With this method, you can set all search options directly.
	 * 
	 * @param textoptionsvalues
	 *            The options that should be used as the search options.
	 */
	public void setSearchOptions(int[] textoptionsvalues) {
		searchoptions = new HashSet<Integer>();
		for (int i : textoptionsvalues) {
			searchoptions.add(i);
		}
	}

	/**
	 * This method adds a search option to the search option HashSet.
	 * 
	 * @param textoptionvalue
	 *            The option that should be added to the search options.
	 */
	public void addSearchOption(int textoptionvalue) {
		searchoptions.add(textoptionvalue);
	}

	/**
	 * This method removes one text option attribute from the search options
	 * HashSet.
	 * 
	 * @param textoptionvalue
	 *            The option that should be removed from the search options.
	 */
	public void removeSearchOption(int textoptionvalue) {
		searchoptions.remove(textoptionvalue);
	}

	/**
	 * Default Constructor.
	 */
	public TextOptions() {
		searchoptions = new HashSet<Integer>();
	}
}
