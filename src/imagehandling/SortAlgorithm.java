package imagehandling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
	private double start;

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
	 * is used to set the praefix of a protocol folder than.
	 */
	private HashMap<String, Integer> index;

	/**
	 * The HashMap protocolnames is used, if subfolders is false. The
	 * protocolnames contains the praefix to a given protocol name.
	 */
	private HashMap<String, String> protocolnames = new HashMap<String, String>();

	/**
	 * This is the default construktur. This construktur setting the following
	 * defaults value: - subfolders = false; - image digits = 5; -
	 * protocol_digits = 3; Take a look in the Java-doc of the setter methods of
	 * these values for more informations.
	 */
	public SortAlgorithm() {
		subfolders = false;
		img_digits = 5;
		protocol_digits = 3;
	}

	/**
	 * This construktur contains the following default setter: - image digits =
	 * 5; - protocol digits = 3; Take a look in the Java-doc of the setter
	 * methods of these values for more informations.
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
	 * = 3; Take a look in the Java-doc of the setter methods of these values
	 * for more informations.
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
	 * The img_digits contains the information, how many digits are used to save
	 * the image number of a dicom in the file name. If the image number is
	 * larger than the img_digits number, than the image number is taken.
	 */
	public void setImgDigits(int i) {
		img_digits = i;
	}

	/**
	 * The protocol_digits contains the Information how many digits are used at
	 * a special position. If subfolders is true, than protocol_digits contains
	 * the information for the number of digits for the subfolder of a protocol.
	 * Else, the protocol_digits contains the information, how many digits are
	 * used as a Praefix of a protocol folder.
	 */
	public void setProtocolPraefixDigits(int i) {
		protocol_digits = i;
	}

	/**
	 * The boolean value subfolders contains the information, if the different
	 * angel of the scans are sorted in subfolders in the protocol directorys or
	 * if the different angel are save as the protocol name with a certain
	 * praefix.
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
	 * This method searching in the path "searchin" for dicoms and subfolders,
	 * which contains dicoms. These dicoms are sorted in the value "sortInDir".
	 * If "sortInDir" not exists, it gonna be created. To specify the behaivor
	 * of this method, you can use the setter methods.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	public void searchAndSortIn(String searchin, String sortInDir) {
		start = System.currentTimeMillis();
		double start2 = start;
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
					bw.write("The Sorting structur is the following:\n"
							+ sortInDir
							+ "\tPatient id/Protocol Name_DEPENDS/\nAdditionally the name of a Dicom is renamed to his Image number + .dcm\nThe DEPENDS value is equal to the first dir, where the Modality is equal to the Image Modality.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (subfolders) {
			for (File f : new File(sortInDir).listFiles()) {
				if (f.isDirectory()) {
					for (File f2 : f.listFiles()) {
						File helpfile = new File(f2.getAbsolutePath()+"/"+toProtocolDigits(1+""));
						if (!helpfile.exists()){
							helpfile.mkdir();
						}
						boolean empty = true;
						for (File f3 : f2.listFiles()) {
							if (f3.getAbsolutePath().endsWith(".dcm")) {
								try {
									Files.move(f3.toPath(), new File(helpfile.getAbsolutePath()+"/"+f3.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
									empty = false;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						if (empty){
							helpfile.delete();
						}
					}
				}
			}
			searchAndSortInSubfolders(searchin, sortInDir);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (File f : new File(sortInDir).listFiles()) {
				if (f.isDirectory()) {
					for (File f2 : f.listFiles()) {
						boolean onlyone = true;
						if (f2.isDirectory()) {
							for (File f3 : f2.listFiles()) {
								if (f3.isDirectory()) {
									if (!f3.getName().startsWith(
											toProtocolDigits(1 + ""))) {
										onlyone = false;
										break;
									}
								}
							}
						}
						if (onlyone) {
							for (File f3 : f2.listFiles()) {
								if (f3.isDirectory()) {
									f3.deleteOnExit();
									for (File f4 : f3.listFiles()) {
										if (f4.getAbsolutePath().endsWith(
												".dcm")) {
											try {
												Files.move(
														f4.toPath(),
														new File(
																f2.getAbsoluteFile()
																		+ "/"
																		+ f4.getName())
																.toPath(),
														StandardCopyOption.REPLACE_EXISTING);
											} catch (IOException e) {
												// TODO Auto-generated catch
												// block
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
		} else {
			searchAndSortInNoSubfolders(searchin, sortInDir);
		}

		start2 = System.currentTimeMillis() - start2;
		System.out.println("I found and sorted " + found + " Dicoms in "
				+ start2 / 1000 + " seconds! I copied " + copyd
				+ " of them to the Output directory.");
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
	private void searchAndSortInSubfolders(String searchin, String sortInDir) {
		File file = new File(searchin);
		File[] list = file.listFiles();

		if (list == null) {
			return;
		}

		for (File l : list) {
			if (l.getAbsolutePath().endsWith(".IMA")
					|| l.getAbsolutePath().endsWith(".dcm")) {
				sortInDirWithSubfolders(l.getAbsolutePath(), sortInDir);
				if (++found % 50 == 0) {
					System.out.println("I found and sorted so far " + found
							+ " Dicoms. (DeltaTime: " + deltaTime()
							+ " millis.)");
				}
			} else {
				searchAndSortInSubfolders(l.getAbsolutePath(), sortInDir);
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
	private void sortInDirWithSubfolders(String input, String dir) {
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
				// if (i == 1) {
				// test2 = new File(path.toString() + "/" + att[1]);
				// protocol = false;
				// }
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
	private void searchAndSortInNoSubfolders(String searchin, String sortInDir) {
		File file = new File(sortInDir);
		File[] list = file.listFiles();
		index = new HashMap<>();
		ArrayList<Integer> existingPraefix = new ArrayList<Integer>();
		
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
							existingPraefix.add(Integer.parseInt(test));
							if (Integer.parseInt(test) > index.get(patient
									.getName())) {
								index.put(patient.getName(),
										Integer.parseInt(test));
							}
						} catch (NullPointerException e) {
							index.put(patient.getName(), 1);
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
//		System.out.println(existingPraefix);
		searchAndSortInNoSubfolders2(searchin, sortInDir);
	}

	/**
	 * This method is the rekursiv search for dicoms in the directory searchin,
	 * when the subfolder value is set to false. If this method finds dicoms,
	 * than it calls the method sortInDirWithoutSubfolders.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	private void searchAndSortInNoSubfolders2(String searchin, String sortInDir) {
		File file = new File(searchin);
		File[] list = file.listFiles();

		if (list == null) {
			return;
		}

		for (File l : list) {
			if (l.getAbsolutePath().endsWith(".IMA")
					|| l.getAbsolutePath().endsWith(".dcm")) {
				sortInDirWithoutSubfolders(l.getAbsolutePath(), sortInDir);
				if (++found % 50 == 0) {
					System.out.println("I found and sorted so far " + found
							+ " Dicoms. (DeltaTime: " + deltaTime()
							+ " millis.)");
				}
			} else {
				searchAndSortInNoSubfolders2(l.getAbsolutePath(), sortInDir);
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
	private void sortInDirWithoutSubfolders(String input, String dir) {
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
			// System.out.println("HERE "+test2.getAbsolutePath());
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
		double time = atm - start;
		start = atm;
		return time;
	}
}
