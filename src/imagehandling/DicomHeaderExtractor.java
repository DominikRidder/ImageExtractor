package imagehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import ij.plugin.DICOM;

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
	public void extractHeader(String path) {
		String dicom = path;

		// cutting of ending like IMA or dcm
		if (path.substring(path.length() - 3, path.length()).equals("IMA")
				|| path.substring(path.length() - 3, path.length()).equals(
						"dcm")) {
			path = path.substring(0, path.length() - 4);
		}
		path += ".header";

		// check if header file already exist
		File file = new File(path);
		if (file.exists()) {
			throw new RuntimeException(
					"DicomExtractor failed. File already exist: " + path);
		} else {
			try {
				// just to find out if it is a Dicom
				new DICOM().open(dicom);
			} catch (IndexOutOfBoundsException e) {
				return;
			}
		}

		// str = headers
		String str;
		try {
			str = new DICOM().getInfo(dicom);
		} catch (IndexOutOfBoundsException e) {
			return;
		}

		// creating the file
		try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
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
	
//	public String[] getInfo(String path, String[] items) {
//        int numberOfItems = items.length;
//        ArrayList<String>itemsort = new ArrayList<String>(numberOfItems);
//        for (String item: items){
//            itemsort.add(item);
//        }
//        Collections.sort(itemsort);
//       
//        String[] infos = new String[numberOfItems];
//        String lines[] = getHeader(path).split("\n");
//        int index = 0;
//        StringBuilder att;
//            for (String line : lines){
//                att = new StringBuilder();
//                if (!line.startsWith(itemsort.get(index))){
//                    continue;
//                }
//                int i =items[index].length();
//                while(line.charAt(i++) != ':');
//                while(line.charAt(i) == ' ' && ++i<line.length());
//                while(i<line.length() && line.charAt(i)!= ' '){
//                    att.append(line.charAt(i++));
//                }
//                infos[index] = att.toString();
//                if (++index==numberOfItems){
//                    break;
//                }
//            }
//       
//        return infos;
//    }

}
