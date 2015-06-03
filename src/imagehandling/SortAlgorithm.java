package imagehandling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class SortAlgorithm {

	/**
	 * Found is the number of dicoms, that are found in the subfolders of the
	 * input directory.
	 */
	private int found;

	/**
	 * Copyd is the number of dicoms, that are copyd to the output directory.
	 */
	private int copyd;

	/**
	 * The start value is used to calculate the Deltatime.
	 */
	private double deltaTimeHelp;

	/**
	 * For some informations, take a look at the setSubfolders method.
	 */
	private boolean subfolders;

	/**
	 * For some informations, take a look at the setImgDigits method.
	 */
	private int img_digits;

	/**
	 * For some informations, take a look at the setProtocolDigits method.
	 */
	private int protocol_digits;

	/**
	 * The index is important for the case, that subfolder is false. The index
	 * is used to set the praefix of a protocol folder than. The key of this
	 * HashMap is the patient id.
	 */
	private HashMap<String, Integer> index;

	/**
	 * The HashMap protocolnames is used, if subfolders is false. The
	 * protocolnames contains the praefix to a given protocol name.
	 */
	private HashMap<String, String> protocolnames = new HashMap<String, String>();

	/**
	 * This is the default construktur. This construktur setting the following
	 * defaults value:
	 * <p>
	 * - subfolders = false
	 * <p>
	 * - image digits = 5
	 * <p>
	 * - protocol_digits = 3
	 * <p>
	 * Take a look in the Java-doc of the setter methods of these values for
	 * more informations.
	 */
	public SortAlgorithm() {
		subfolders = false;
		img_digits = 5;
		protocol_digits = 3;
	}

	/**
	 * This construktur contains the following default setter: - image digits =
	 * 5
	 * <p>
	 * - protocol digits = 3
	 * <p>
	 * Take a look in the Java-doc of the setter methods of these values for
	 * more informations.
	 * 
	 * @param subfolders
	 */
	public SortAlgorithm(boolean subfolders) {
		this.subfolders = subfolders;
		img_digits = 5;
		protocol_digits = 3;
	}

	/**
	 * This construktur contains the following default setter: - protocol digits
	 * = 3
	 * <p>
	 * Take a look in the Java-doc of the setter methods of these values for
	 * more informations.
	 * 
	 * @param subfolders
	 * @param protocol_digits
	 */
	public SortAlgorithm(boolean subfolders, int protocol_digits) {
		this.subfolders = subfolders;
		img_digits = 5;
		this.protocol_digits = protocol_digits;
	}

	/**
	 * With this construktur, you can set all needed parameters at once, so you
	 * dont need any setter methods. Take a look in the Java-doc of the setter
	 * methods of these values for more informations.
	 * 
	 * @param subfolders
	 * @param protocol_digits
	 * @param img_digits
	 */
	public SortAlgorithm(boolean subfolders, int protocol_digits, int img_digits) {
		this.subfolders = subfolders;
		this.img_digits = img_digits;
		this.protocol_digits = protocol_digits;
	}

	/**
	 * The imgDigits contains the information, how many digits are used to save
	 * the image number of a DICOM in the file name. If the image number of a
	 * DICOM is larger than the imgDigits number, than the image number of the
	 * DICOM is taken.
	 */
	public void setImgDigits(int i) {
		img_digits = i;
	}

	/**
	 * The protocolDigits contains the Information how many digits are used at a
	 * special position. If subfolders is true, than protocolDigits contains the
	 * information for the number of digits for the subfolder of a protocol name
	 * folder. Else, the protocolDigits contains the information, how many
	 * digits are used as a Praefix of a protocol folder.
	 */
	public void setProtocolDigits(int i) {
		protocol_digits = i;
	}

	/**
	 * The boolean value subfolders contains the information, whether the
	 * different angel of the scans are sorted in subfolders in the protocol
	 * name directorys or the different angel are save as the protocol name with
	 * a certain praefix.
	 */
	public void useSubfolders(boolean b) {
		subfolders = b;
	}

	/**
	 * This method calls toNDigits(protocol_digits, given number String).
	 * 
	 * @param image_number
	 * @return
	 */
	private String toProtocolDigits(String image_number) {
		return toNDigits(protocol_digits, image_number);
	}

	/**
	 * This method calls toNDigits(img_digits, given number String).
	 * 
	 * @param image_number
	 * @return
	 */
	private String toImgDigits(String image_number) {
		return toNDigits(img_digits, image_number);
	}

	/**
	 * This method creates a String with the length n, if the number String has
	 * size, that is lower or equal. If the size of number is equal or higher
	 * than n, than the number String is returned. Otherwiese this method fills
	 * some 0 as a praefix or the number String, until it length is equal to n.
	 * 
	 * @param n
	 * @param number
	 * @return
	 */
	private String toNDigits(int n, String number) {
		StringBuilder digits = new StringBuilder(n);
		for (int i = 0; i < n - number.length(); i++) {
			digits.append("0");
		}
		return digits.toString() + number;
	}

	/**
	 * This method checks if a File to the given path exists anf if its not
	 * existing, the method prints the information, that the path gonna be
	 * created and creates the directory to the path.
	 * 
	 * @param path
	 */
	private void existOrCreate(StringBuilder path) {
		File test = new File(path.toString());
		if (!test.exists()) {
			System.out.println("Creating " + test.getAbsolutePath());
			test.mkdir();
		}
	}

	/**
	 * This method searching in the path "searchin" for DICOMs and subfolders,
	 * which contains DICOMs. These DICOMs are sorted in the value "sortInDir".
	 * If "sortInDir" not exists, it gonna be created. To specify the behaivor
	 * of this method, you can use the setter methods.
	 * 
	 * @param searchin
	 *            The Folder, where the Algorithm search for DICOMs.
	 * @param sortInDir
	 *            The Folder, where the Algorithm move and sort the DICOMs.
	 */
	public void searchAndSortIn(String searchin, String sortInDir) {
		deltaTimeHelp = System.currentTimeMillis();
		double start = deltaTimeHelp;
		File file = new File(searchin);

		found = 0;
		copyd = 0;
		if (!file.exists() | !file.isDirectory()) {
			System.out.println("The Given Path seems to be incorrect.");
			return;
		}

		existOrCreate(new StringBuilder(sortInDir));

		// README
		File test = new File(sortInDir + "/README");
		if (!test.exists()) {
			try {
				test.createNewFile();
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(
						test.getAbsolutePath()))) {
					if (subfolders) {
						bw.write("The Sorting structur is the following:\n"
								+ sortInDir
								+ "\tPatient id/Protocol Name/(protocol digits)/img digits.dcm\nThe protocol digits folder is a index, that is generated by the SortAlgorim. If there is only one protocol digit subfolder in a Protocol Name folder, than the DICOMS moved to the parent folder of the protocol digit folder and the protocol digit folder gonna be removed.");
					} else {
						bw.write("The Sorting structur is the following:\n"
								+ sortInDir
								+ "\tPatient id/protocol digits_Protocol Name/img digits.dcm\nThe protocol digits praefix is generated by the SortAlgorithm. If new protocol Name folders are generated, than the next highest protocol digit praefix will be used.");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (subfolders) {
			SASInSubfoldersWrapper(searchin, sortInDir);
		} else {
			SASInNoSubfolderWrapper(searchin, sortInDir);
		}

		start = System.currentTimeMillis() - start;
		System.out.println("I found and sorted " + found + " Dicoms in "
				+ start / 1000 + " seconds! I copied " + copyd
				+ " of them to the Output directory.");
	}

	private void SASInSubfoldersWrapper(String searchin, String sortInDir) {
		for (File patientIdFolder : new File(sortInDir).listFiles()) {
			if (patientIdFolder.isDirectory()) {
				for (File protocolNameFolder : patientIdFolder.listFiles()) {
					File helpfile = new File(
							protocolNameFolder.getAbsolutePath() + "/"
									+ toProtocolDigits(1 + ""));
					if (!helpfile.exists()) {
						helpfile.mkdir();
					}
					boolean empty = true;
					for (File protocolsubfolder : protocolNameFolder
							.listFiles()) {
						if (protocolsubfolder.getAbsolutePath()
								.endsWith(".dcm")) {
							try {
								Files.move(
										protocolsubfolder.toPath(),
										new File(helpfile.getAbsolutePath()
												+ "/"
												+ protocolsubfolder.getName())
												.toPath(),
										StandardCopyOption.REPLACE_EXISTING);
								empty = false;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					if (empty) {
						helpfile.delete();
					}
				}
			}
		}
		SASInSubfoldersSearch(searchin, sortInDir);
		for (File patientIdFolder : new File(sortInDir).listFiles()) {
			if (patientIdFolder.isDirectory()) {
				for (File protocolNameFolder : patientIdFolder.listFiles()) {
					boolean onlyone = true;
					if (protocolNameFolder.isDirectory()) {
						for (File protocolSubfolder : protocolNameFolder
								.listFiles()) {
							if (protocolSubfolder.isDirectory()) {
								if (!protocolSubfolder.getName().startsWith(
										toProtocolDigits(1 + ""))) {
									onlyone = false;
									break;
								}
							}
						}
					}
					if (onlyone) {
						for (File protocolSubfolder : protocolNameFolder
								.listFiles()) {
							if (protocolSubfolder.isDirectory()) {
								protocolSubfolder.deleteOnExit();
								for (File f4 : protocolSubfolder.listFiles()) {
									if (f4.getAbsolutePath().endsWith(".dcm")) {
										try {
											Files.move(
													f4.toPath(),
													new File(protocolNameFolder
															.getAbsoluteFile()
															+ "/"
															+ f4.getName())
															.toPath(),
													StandardCopyOption.REPLACE_EXISTING);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}

					}
				}
			}
		}
	}

	/**
	 * This method is the rekursiv search for the dicoms. If this methods finds
	 * dicoms, than this methods calls the sortInDirWithSubfolders method with
	 * the dicom path and the output folder, where the sorted structur is build
	 * in.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	private void SASInSubfoldersSearch(String searchin, String sortInDir) {
		File file = new File(searchin);
		File[] list = file.listFiles();

		if (list == null) {
			return;
		}

		for (File l : list) {
			String path = l.getAbsolutePath();
			if (path.endsWith(".IMA") || path.endsWith(".dcm")) {
				SASInSubfoldersSort(path, sortInDir);
				if (++found % 50 == 0) {
					System.out.println("I found and sorted so far " + found
							+ " Dicoms. (DeltaTime: " + deltaTime()
							+ " millis.)");
				}
			} else {
				SASInSubfoldersSearch(path, sortInDir);
			}
		}
	}

	/**
	 * This method contains the algorithm for sorting one dicom in a file
	 * structur with protocol subfolders.
	 * 
	 * @param input
	 * @param dir
	 */
	private void SASInSubfoldersSort(String input, String dir) {
		// Getting the necessarie Informations
		KeyMap[] info = { KeyMap.KEY_PATIENT_ID, KeyMap.KEY_PROTOCOL_NAME,
				KeyMap.KEY_IMAGE_NUMBER, KeyMap.KEY_SERIES_INSTANCE_UID,
				KeyMap.KEY_PATIENTS_BIRTH_DATE };
		String[] att = Image.getAttributesDicom(input, info);

		// Check existing
		boolean protocol = true;
		StringBuilder path = new StringBuilder();
		path.append(dir + "/" + att[0]);
		existOrCreate(path);
		int i;
		loop: for (i = 1; i < 1000; i++) {
			protocol = true;
			File test2 = new File(path.toString() + "/" + att[1] + "/"
					+ toProtocolDigits(i + ""));
			if (!test2.exists()) {
				break;
			}
			for (int j = 1; j < 1000; j++) {
				File test3 = new File(test2.getAbsolutePath() + "/"
						+ toImgDigits("" + j) + ".dcm");
				if (test3.exists()) {
					KeyMap twoElement[] = { KeyMap.KEY_SERIES_INSTANCE_UID,
							KeyMap.KEY_PATIENTS_BIRTH_DATE };
					String[] comparing = Image.getAttributesDicom(
							test3.getAbsolutePath(), twoElement);
					if (att[3].equals(comparing[0])
							&& att[4].equals(comparing[1])) {
						break loop;
					}
					continue loop;
				}
			}
		}

		path.append("/" + att[1]);
		existOrCreate(path);
		if (protocol) {
			path.append("/" + toProtocolDigits(i + ""));
			existOrCreate(path);
		}
		path.append("/" + toImgDigits(att[2]) + ".dcm");

		// Copy data
		File test = new File(path.toString());
		if (!test.exists()) {
			try {
				Files.copy(new File(input).toPath(), test.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				copyd++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This methods perpares the sorting structur for dicoms, where the protocol
	 * folders wont contain subfolder of the different angles. Instead of the
	 * subfolders the protocol name is used with a praefix of some digits. After
	 * the preparation is finish, this method calls the rekursiv dicom search
	 * searchAndSortInNoSubfolders2.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	private void SASInNoSubfolderWrapper(String searchin, String sortInDir) {
		File file = new File(sortInDir);
		File[] list = file.listFiles();
		index = new HashMap<>();

		if (list != null) {
			for (File patient : list) {
				File[] newlist = patient.listFiles();
				if (newlist == null) {
					continue;
				}
				for (File protocol : newlist) {
					try {
						String test = protocol.getName().substring(0,
								protocol_digits);
						// is test a number?
						try {
							int nextParse = Integer.parseInt(test);
							if (nextParse > index.get(patient.getName())) {
								index.put(patient.getName(), nextParse);
							}
						} catch (NullPointerException e) {
							index.put(patient.getName(), 1);
						} catch (NumberFormatException e) {
							continue;
						}
						int i = 1;
						while (true) {
							if (!protocolnames.containsKey(patient
									.getAbsolutePath()
									+ "/"
									+ protocol.getName().substring(
											protocol_digits + 1,
											protocol.getName().length()) + i)) {
								protocolnames.put(
										patient.getAbsolutePath()
												+ "/"
												+ protocol.getName().substring(
														protocol_digits + 1,
														protocol.getName()
																.length()) + i,
										test);
								break;
							}
							i++;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		SASInNoSubfoldersSearch(searchin, sortInDir);
	}

	/**
	 * This method is the rekursiv search for dicoms in the directory searchin,
	 * when the subfolder value is set to false. If this method finds dicoms,
	 * than it calls the method sortInDirWithoutSubfolders.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	private void SASInNoSubfoldersSearch(String searchin, String sortInDir) {
		File file = new File(searchin);
		File[] list = file.listFiles();

		if (list == null) {
			return;
		}

		for (File l : list) {
			String path = l.getAbsolutePath();
			if (path.endsWith(".IMA") || path.endsWith(".dcm")) {
				SASInNoSubfoldersSort(path, sortInDir);
				if (++found % 50 == 0) {
					System.out.println("I found and sorted so far " + found
							+ " Dicoms. (DeltaTime: " + deltaTime()
							+ " millis.)");
				}
			} else {
				SASInNoSubfoldersSearch(path, sortInDir);
			}
		}
	}

	/**
	 * This method sorting one dicom to a specific output directory. Each
	 * protocol folder name contains a praefix of numbers. If two protocol have
	 * the same name, but a diffent series instance uid, than they have a
	 * different praefix.
	 * 
	 * @param input
	 * @param dir
	 */
	private void SASInNoSubfoldersSort(String input, String dir) {
		// Getting the necessarie Informations
		KeyMap[] info = { KeyMap.KEY_PATIENT_ID, KeyMap.KEY_PROTOCOL_NAME,
				KeyMap.KEY_IMAGE_NUMBER, KeyMap.KEY_SERIES_INSTANCE_UID,
				KeyMap.KEY_PATIENTS_BIRTH_DATE };
		String[] att = Image.getAttributesDicom(input, info);

		// Check existing
		StringBuilder path = new StringBuilder();
		path.append(dir + "/" + att[0]);
		existOrCreate(path);
		int i;
		loop: for (i = 1; i < 1000; i++) {
			if (!protocolnames.containsKey(path.toString() + "/" + att[1] + i)) {
				try {
					index.put(att[0], index.get(att[0]) + 1);
				} catch (NullPointerException e) {
					index.put(att[0], 1);
				}
				protocolnames.put(path.toString() + "/" + att[1] + i,
						toProtocolDigits(index.get(att[0]) + ""));
			}

			File test2 = new File(path.toString() + "/"
					+ protocolnames.get(path.toString() + "/" + att[1] + i)
					+ "_" + att[1]);
			if (!test2.exists()) {
				break;
			}
			for (int j = 1; j < 1000; j++) {
				File test3 = new File(test2.getAbsolutePath() + "/"
						+ toImgDigits("" + j) + ".dcm");
				if (test3.exists()) {
					KeyMap twoElement[] = { KeyMap.KEY_SERIES_INSTANCE_UID,
							KeyMap.KEY_PATIENTS_BIRTH_DATE };
					String[] comparing = Image.getAttributesDicom(
							test3.getAbsolutePath(), twoElement);
					if (att[3].equals(comparing[0])
							&& att[4].equals(comparing[1])) {
						break loop;
					} else {
						continue loop;
					}
				}
			}
		}
		path.append("/" + protocolnames.get(path.toString() + "/" + att[1] + i)
				+ "_" + att[1]);
		existOrCreate(path);
		path.append("/" + toImgDigits(att[2]) + ".dcm");

		// Copy data
		File test = new File(path.toString());
		if (!test.exists()) {
			try {
				Files.copy(new File(input).toPath(), test.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				copyd++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method is used, to get the time diffence from the last call of this
	 * method and this time. If you use this method for the first time, you may
	 * have to set the start value of this class some time before, to get a
	 * usefull value.
	 * 
	 * @return
	 */
	private double deltaTime() {
		double atm = System.currentTimeMillis();
		double time = atm - deltaTimeHelp;
		deltaTimeHelp = atm;
		return time;
	}
}
