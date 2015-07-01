package imagehandling;

import java.util.ArrayList;

/**
 * The TextOptions are used to set the Search and Return options for the getAttribute methods of volume and Image.
 * @author dridder_local
 *
 */
public class TextOptions {
	public static final int SEARCH_IN_ATTRIBUTE_NUMBER = 1;
	public static final int SEARCH_IN_ATTRIBUTE_NAME = 2;
	public static final int SEARCH_IN_ATTRIBUTE_VALUE = 3;
	
	public static final int RETURN_ATTRIBUTE_NUMBER = -1;
	public static final int RETURN_ATTRIBUTE_NAME_WITHOUT_COLON = -2;
	public static final int RETURN_ATTRIBUTE_NAME_WITH_COLON = -3;
	public static final int RETURN_ATTRIBUTE_VALUE = -4;
	
	private ArrayList<Integer> searchoptions;
	private ArrayList<Integer> returnoptions;
	private String splitter = " ";
	
	public void setSplittString(String splitter){
		this.splitter = splitter;
	}
	
	public String getSplittString(){
		return splitter;
	}
	
	public ArrayList<Integer> getSearchOptions(){
		return searchoptions;
		
	}
	
	public ArrayList<Integer> getReturnOptions(){
		return returnoptions;
	}
	
	public void setSearchOptions(int[] textoptionsvalues){
		searchoptions = new ArrayList<Integer>();
		for (int i : textoptionsvalues){
			searchoptions.add(i);
		}
	}
	
	public void setReturnOptions(int[] textoptionsvalues){
		returnoptions = new ArrayList<Integer>();
		for (int i : textoptionsvalues){
			returnoptions.add(i);
		}
	}
	
	public void setSearchOptions(ArrayList<Integer> textoptionsvalues){
		searchoptions = new ArrayList<Integer>();
		for (int i : textoptionsvalues){
			searchoptions.add(i);
		}
	}
	
	public void setReturnOptions(ArrayList<Integer> textoptionsvalues){
		returnoptions = new ArrayList<Integer>();
		for (int i : textoptionsvalues){
			returnoptions.add(i);
		}
	}
	
	public void addSearchOption(int textoptionvalue){
		searchoptions.add(textoptionvalue);
	}
	
	public void addReturnOption(int textoptionvalue){
		returnoptions.add(textoptionvalue);
	}
	
	public TextOptions(){
		searchoptions = new ArrayList<Integer>();
		returnoptions = new ArrayList<Integer>();
	}
}
