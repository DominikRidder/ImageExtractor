package imagehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ij.plugin.DICOM;
import ij.util.WildcardMatch;

public class DicomHeaderExtractor implements HeaderExtractor {

	/**
	 * This method returning the Header of the given Dicom Image.
	 */
	public String getHeader(String path) {
		return new DICOM().getInfo(path);
	}

	/**
	 * This method writing the header information in the same dictionary, where
	 * the Image is. The Name of the information file is the name of the Image,
	 * with ".header" instead of ".IMA" or ".dcm".
	 */
	public void extractHeader(String path, String outputdir) {
		String dicom = path;

		// cutting of ending like IMA or dcm
		if (path.substring(path.length() - 3, path.length()).equals("IMA")
				|| path.substring(path.length() - 3, path.length()).equals(
						"dcm")) {
			path = path.substring(0, path.length() - 4);
		}
		path += ".header";

		// str = headers
		String str;
		try {
			str = new DICOM().getInfo(dicom);
		} catch (IndexOutOfBoundsException e) {
			return;
		}

		// creating the file
		try (PrintWriter pw = new PrintWriter(new FileWriter(outputdir+"/"+new File(path).getName()))) {
			pw.print(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getInfo(String path, String item) {
		StringBuilder att = new StringBuilder();
		for (String line : getHeader(path).split("\n")){
			if (!line.startsWith(item)){
				continue;
			}
			int i =item.length();
			while(line.charAt(i++) != ':');
			while(line.charAt(i) == ' ' && ++i<line.length());
			while(i<line.length() && line.charAt(i)!= ' '){
				att.append(line.charAt(i++));
			}
			
		}
		return att.toString();
	}

	public String[] getInfo(String path, String[] items) {
		int numberOfItems = items.length;
		String[] infos = new String[numberOfItems];
		String lines[] = getHeader(path).split("\n");
		for (int index = 0; index < numberOfItems; index++) {
			StringBuilder att = new StringBuilder();
			for (String line : lines){
				if (!line.startsWith(items[index])){
					continue;
				}
				int i =items[index].length();
				while(line.charAt(i++) != ':');
				while(line.charAt(i) == ' ' && ++i<line.length());
				while(i<line.length() && line.charAt(i)!= ' '){
					att.append(line.charAt(i++));
				}
				
			}
			infos[index] = att.toString();
		}
		return infos;
	}
	
	public String getInfo(String path, String regularExpression, TextOptions topt) {
		StringBuilder str = new StringBuilder();
		WildcardMatch wm = new WildcardMatch();
		wm.setCaseSensitive(false);
		String splitter = topt.getSplittString();
		ArrayList<Integer> searchopt = topt.getSearchOptions();
		ArrayList<Integer> returnopt = topt.getReturnOptions();
		
		boolean searchInNumber = searchopt.contains(TextOptions.SEARCH_IN_ATTRIBUTE_NUMBER);
		boolean searchInName = searchopt.contains(TextOptions.SEARCH_IN_ATTRIBUTE_NAME);
		boolean searchInValue = searchopt.contains(TextOptions.SEARCH_IN_ATTRIBUTE_VALUE);
		
		for (String line : getHeader(path).split("\n")){
			String[] firstsplitt = line.split(":");
			String[] secondsplitt = firstsplitt[0].split("  ", 2);
			String number = secondsplitt[0];
			String name = secondsplitt[1];
			String value = firstsplitt[1].substring(1, firstsplitt[1].length());
			
			StringBuilder searchin = new StringBuilder();
			
			if (searchInNumber){
				searchin.append(number+" ");
			}
			if (searchInName){
				searchin.append(" "+name+": ");
			}
			if (searchInValue){
				searchin.append(" "+value);
			}
			
			if (wm.match(searchin.toString(), regularExpression)){
				for (int i = 0 ; i < returnopt.size(); i++){
					switch(returnopt.get(i)){
					case TextOptions.RETURN_ATTRIBUTE_NAME_WITH_COLON: str.append(name+":");break;
					case TextOptions.RETURN_ATTRIBUTE_NAME_WITHOUT_COLON: str.append(name);break;
					case TextOptions.RETURN_ATTRIBUTE_NUMBER:str.append(number);break;
					case TextOptions.RETURN_ATTRIBUTE_VALUE:str.append(value);break;
					default:break;
					}
					if (i!= returnopt.size()-1){
						str.append(splitter);
					}
				}
				str.append("\n");
			}
		}
		
		return str.toString();
	}
	
}
