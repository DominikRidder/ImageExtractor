package imagehandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import ij.plugin.DICOM;
import ij.util.WildcardMatch;

/**
 * Implemtation of the interface HeaderExtractor for the image type "dcm"
 * ("IMA").
 * 
 * @author dridder_local
 *
 */
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
		try (PrintWriter pw = new PrintWriter(new FileWriter(outputdir + "/"
				+ new File(path).getName()))) {
			pw.print(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getInfo(String path, String item) {
		StringBuilder att = new StringBuilder();
		for (String line : getHeader(path).split("\n")) {
			if (!line.startsWith(item)) {
				continue;
			}
			int i = item.length();
			while (line.charAt(i++) != ':')
				;
			while (line.charAt(i) == ' ' && ++i < line.length())
				;
			while (i < line.length() && line.charAt(i) != ' ') {
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
			for (String line : lines) {
				if (!line.startsWith(items[index])) {
					continue;
				}
				int i = items[index].length();
				while (line.charAt(i++) != ':')
					;
				while (line.charAt(i) == ' ' && ++i < line.length())
					;
				while (i < line.length() && line.charAt(i) != ' ') {
					att.append(line.charAt(i++));
				}

			}
			infos[index] = att.toString();
		}
		return infos;
	}

	public String getInfo(String path, String regularExpression,
			TextOptions topt) {
		StringBuilder str = new StringBuilder();
		WildcardMatch wm = new WildcardMatch();
		wm.setCaseSensitive(false);
		StringBuilder returnexp = new StringBuilder(topt.getReturnExpression());
		HashSet<Integer> searchopt = topt.getSearchOptions();
		String linetest = "*" + regularExpression + "*";

		boolean searchInNumber = searchopt
				.contains(TextOptions.ATTRIBUTE_NUMBER);
		boolean searchInName = searchopt.contains(TextOptions.ATTRIBUTE_NAME);
		boolean searchInValue = searchopt.contains(TextOptions.ATTRIBUTE_VALUE);

		ArrayList<Integer> replaceListPos = new ArrayList<Integer>();
		ArrayList<Integer> replaceListType = new ArrayList<Integer>();

		for (int i = 0; i < returnexp.length(); i++) {
			String c = "" + returnexp.charAt(i);
			switch (c) {
			case TextOptions.ATTRIBUTE_NUMBER + "":
				replaceListPos.add(i);
				replaceListType.add(TextOptions.ATTRIBUTE_NUMBER);
				break;
			case TextOptions.ATTRIBUTE_NAME + "":
				replaceListPos.add(i);
				replaceListType.add(TextOptions.ATTRIBUTE_NAME);
				break;
			case TextOptions.ATTRIBUTE_VALUE + "":
				replaceListPos.add(i);
				replaceListType.add(TextOptions.ATTRIBUTE_VALUE);
				break;
			default:
				break;
			}
		}

		String[] firstsplitt;
		String[] secondsplitt;
		String number;
		String name;
		String value;
		StringBuilder searchin;

		for (String line : getHeader(path).split("\n")) {
			try {
				if (!wm.match(line, linetest)) {
					continue;
				}
				firstsplitt = line.split(":");
				secondsplitt = firstsplitt[0].split("  ", 2);
				number = secondsplitt[0];
				name = secondsplitt[1];
				value = firstsplitt[1].replace(" ", "");

				searchin = new StringBuilder();

				if (searchInNumber) {
					searchin.append(number + " ");
				}
				if (searchInName) {
					searchin.append(" " + name + ": ");
				}
				if (searchInValue) {
					searchin.append(" " + value + " ");
				}

				if (wm.match(searchin.toString(), regularExpression)) {
					returnexp = new StringBuilder(topt.getReturnExpression());
					int offset = 0;
					int pos = 0;
					String replace = "";
					for (int i = 0; i < replaceListPos.size(); i++) {
						switch (replaceListType.get(i)) {
						case TextOptions.ATTRIBUTE_NUMBER:
							replace = number;
							break;
						case TextOptions.ATTRIBUTE_NAME:
							replace = name;
							break;
						case TextOptions.ATTRIBUTE_VALUE:
							replace = value;
							break;
						default:
							break;
						}
						pos = replaceListPos.get(i) + offset;
						returnexp.replace(pos, pos + 1, replace);
						offset += replace.length() - 1;
					}
					str.append(returnexp);
					str.append("\n");
				}
			} catch (ArrayIndexOutOfBoundsException e) {

			}
		}
		if (str.length() > 0) {
			return str.substring(0, str.length() - 1);
		} else {
			return str.toString();
		}
	}

}
