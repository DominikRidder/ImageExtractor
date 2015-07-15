package imagehandling;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.DICOM;
import ij.plugin.Nifti_Writer;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * This class is used to sort Dicoms.
 * 
 * @author dridder_local
 *
 */
public class SortAlgorithm {

	/**
	 * This boolean is used to print a error Message, when curropt data was
	 * found. This way i only print one time a error Message.
	 */
	private boolean anyCurropt;

	/**
	 * Defines, if the dicoms transefered as a dicom or as a nifti.
	 */
	private boolean createNiftis = false;

	/**
	 * This hashmap helps, to create niftis.
	 */
	private HashMap<String, ArrayList<String>> niftihelp;

	/**
	 * Value, which can be called, to know, if there is a problem with the
	 * permissions.
	 */
	private boolean permissionProblem = false;
	/**
	 * Value, that can be set to true by the stopSort() method.
	 */
	private boolean stopsort = false;
	/**
	 * The value keepImgName decides, wether the images are renamed to their
	 * Image Number or if they keep their name.
	 */
	private boolean keepImgName = false;

	/**
	 * With this value, you can change the output stream.
	 */
	private PrintStream out = System.out;

	/**
	 * This boolean decide, whether the dicoms are moved or if they copied.
	 */
	private boolean move = false;

	/**
	 * Found is the number of dicoms, that are found in the subfolders of the
	 * input directory.
	 */
	private int found;

	/**
	 * Copyd is the number of dicoms, that are copyd to the output directory.
	 */
	private int transfered;

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
	 * The index is important for the case, that the boolean value subfolder is
	 * false. The index is used to set the praefix of a protocol folder than.
	 * The key of this HashMap is the patient id and the value is the highest
	 * index, that is missing in the subfolder of a patient folder.
	 */
	private HashMap<String, Integer> index;

	/**
	 * The HashMap protocolnames is used, if subfolders is false. The
	 * protocolnames is used, to check, if 2 Dicoms with the same protocol Name
	 * + series Number + patient ID are equal.
	 */
	private HashMap<String, String> protocolInfo;

	/**
	 * Missing is used, to fill gaps in the protocol praefix (in the subfolder
	 * sort). If someone would delete the folder 010_protocolname, than the next
	 * folder would use the praefix 010_, even if there are higher praefix
	 * numbers.
	 */
	private HashMap<String, ArrayList<Integer>> missing;

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
	 * This method is used, to decide, wether the Names of the files gonna be
	 * changened to their Image Number or if their keep their names.
	 */
	public void setKeepImageName(boolean keepit) {
		keepImgName = keepit;
	}

	/**
	 * The setPrinStream method can be used, to catch the output or the place,
	 * where it is printed.
	 */
	public void setPrintStream(PrintStream out) {
		this.out = out;
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
	 * Sets the Filetransferoption to copy.
	 */
	public void setFilesOptionCopy() {
		move = false;
	}

	/**
	 * Sets the Filetransferoption to move.
	 */
	public void setFilesOptionMove() {
		move = true;
	}

	/**
	 * Defines, if the dicoms transefered as a dicom or as a nifti.
	 */
	public void setCreateNiftis(boolean createniftis) {
		this.createNiftis = createniftis;
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
			out.println("Creating " + test.getAbsolutePath());
			test.mkdir();
		}
	}

	/**
	 * This tells you, if someone forced the SortAlgorithm to stop the sort,
	 * with the stopSort() method.
	 */
	public boolean gotStopped() {
		return stopsort;
	}

	/**
	 * Returns the information, if there was a problem with the permission.
	 * 
	 * @return
	 */
	public boolean getPermissionProblem() {
		return permissionProblem;
	}

	/**
	 * Method, to stop the Sort of another Thread.
	 */
	public void stopSort() {
		stopsort = true;
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
	public boolean searchAndSortIn(String searchin, String sortInDir) {
		// a list of found dicoms is needed, if nifti should be used
		niftihelp = new HashMap<>();
		// true if an IOException appears somewhere
		permissionProblem = false;
		// false until the user calls stopSort()
		stopsort = false;
		// time between two system.out.println
		deltaTimeHelp = System.currentTimeMillis();
		double start = deltaTimeHelp;
		// new File, to test if the path is correct
		File file = new File(searchin);
		protocolInfo = new HashMap<String, String>(100);
		anyCurropt = false;

		// number of dicoms that the programm found
		found = 0;
		// number of dicoms, that the programm copyd or moved
		transfered = 0;

		if (!file.exists() | !file.isDirectory()) {
			out.println("The Given Path seems to be incorrect.");
			return false;
		}

		// check if the target dir exist or if we have to create it
		existOrCreate(new StringBuilder(sortInDir));

		// README (exist or create)
		File readmeDir = new File(sortInDir + "/README");
		if (!readmeDir.exists()) {
			try {
				readmeDir.createNewFile();
				try (BufferedWriter bwriter = new BufferedWriter(
						new FileWriter(readmeDir.getAbsolutePath()))) {
					if (subfolders) {
						bwriter.write("The Sorting structur is the following:\n"
								+ sortInDir
								+ "\tPatient id/Protocol Name/(protocol digits)/img digits.dcm\nThe protocol digits folder is a index, that is generated by the SortAlgorim. If there is only one protocol digit subfolder in a Protocol Name folder, than the DICOMS moved to the parent folder of the protocol digit folder and the protocol digit folder gonna be removed.");
					} else {
						bwriter.write("The Sorting structur is the following:\n"
								+ sortInDir
								+ "\tPatient id/protocol digits_Protocol Name/img digits.dcm\nThe protocol digits praefix is generated by the SortAlgorithm. If new protocol Name folders are generated, than the next highest protocol digit praefix will be used.");
					}
				}
			} catch (IOException e) {
				out.println("Creating a file in the outputdir failed.");
				permissionProblem = true;
				stopSort();
			}
		}

		// Preparing the Sort and Sort
		if (subfolders) {
			SASInSubfoldersWrapper(searchin, sortInDir);
		} else {
			SASInNoSubfolderWrapper(searchin, sortInDir);
		}
		// Sort Finished

		// Was the sort end forced by the user?
		if (stopsort) {
			return false;
		}

		int numberofnii = 0;
		if (createNiftis) {
			out.println("Creating Niftis..");
			Set<String> keys = niftihelp.keySet();
			 Nifti_Writer writer = new Nifti_Writer();
			 int slices = 0;
			 int frames = 0;
			 int width = 0;
			 int height = 0;
			for (String key : keys) {
				KeyMap[] info = { KeyMap.KEY_ECHO_NUMBERS_S, KeyMap.KEY_ECHO_NUMBERS_S};
				ArrayList<String> dicoms = niftihelp.get(key);
				boolean image4d = false;
				DICOM dcm = new DICOM();
				for (String dicom : dicoms) {
					dcm.open(dicom);
					slices++;
					String[] att = Image.getAttributesDicom(dicom, info);
					if (image4d == false && Integer.parseInt(att[0]) != 1) {
						image4d = true;
						BufferedImage img = dcm.getBufferedImage();
						width = img.getWidth();
						height = img.getHeight();
					}
					if (image4d == true && Integer.parseInt(att[1]) > frames){
						frames = Integer.parseInt(att[1]);
						}
				}
				ImagePlus imp = dcm.duplicate();
				if (image4d){
					slices /= frames;
					ImagePlus hyper = IJ.createHyperStack(imp.getTitle(),width,height, 1, slices, frames, 8);
					hyper.setImage(imp);
					imp = hyper;
				}
				 writer.save(imp, key, "data.nii");
				 numberofnii++;
			}
		}

		// The last output
		start = System.currentTimeMillis() - start;
		String operation = "";
		if (!createNiftis) {
			if (move) {
				operation = "moved";
			} else {
				operation = "copied";
			}
		} else {
			operation = "created niftis";
			transfered = numberofnii;
		}
		out.println("I found and sorted " + found + " Dicoms in " + start
				/ 1000 + " seconds! I " + operation + " " + transfered
				+ " of them to the Output directory.");
		return true;
	}

	/**
	 * This Method prepares the sort and finishing the Sort. If there is only
	 * one subfolder in a protocol Folder, than the folder gets "unpacked".
	 */
	private void SASInSubfoldersWrapper(String searchin, String sortInDir) {
		// Index to find the next praefix, that is needed for a protocol
		index = new HashMap<>();
		// missing praefix, that i need
		missing = new HashMap<String, ArrayList<Integer>>();
		// The patientFolder is the highest folder after the target sortInDir
		// folder
		for (File patientIdFolder : new File(sortInDir).listFiles()) {
			// Hopefully i didnt catched a File
			if (patientIdFolder.isDirectory()) {
				String patientID = patientIdFolder.getName();
				// The next instance in the Sort structur is a patientIdFolder
				for (File protocolNameFolder : patientIdFolder.listFiles()) {
					// helpfile is the first subfolder of a protocolFolder
					File helpfile = new File(
							protocolNameFolder.getAbsolutePath() + "/"
									+ toProtocolDigits(1 + ""));
					if (!helpfile.exists()) {
						helpfile.mkdir();
					}
					boolean empty = true;
					// Does the Protocol contains already dicoms?
					for (File dicomcheck : protocolNameFolder.listFiles()) {
						if (dicomcheck.getAbsolutePath().endsWith(".dcm")) {
							try {
								// If we find dicoms, than we put them in a
								// folder, so the sort wont be confused
								Files.move(dicomcheck.toPath(), new File(
										helpfile.getAbsolutePath() + "/"
												+ dicomcheck.getName())
										.toPath(),
										StandardCopyOption.REPLACE_EXISTING);
								empty = false;
							} catch (IOException e) {
								out.println("IOProblem appeared.");
								permissionProblem = true;
								stopSort();
							}
						}
					}
					// If we dont need this folder, we can delete it
					if (empty) {
						helpfile.delete();
					}
					for (File protocolSubfolder : protocolNameFolder
							.listFiles()) {

						String protocolSubName = protocolSubfolder.getName();
						String protocolName = protocolNameFolder.getName();
						// I dont need folders, when they are to short
						if (protocolSubName.length() < protocol_digits + 1) {
							continue;
						}
						try {
							// getting the paefix index
							String test = protocolSubName;

							try {
								// is test a number?
								int nextParse = Integer.parseInt(test);
								// Initializing the Integer in the HashMap
								// index, if there is no
								// entry
								if (index.get(patientID + protocolName) == null) {
									index.put(patientID + protocolName, 0);
								}
								if (missing.get(patientID + protocolName) == null) {
									missing.put(patientID + protocolName,
											new ArrayList<Integer>());
								}

								// maybe a praefix is "marked" as missing, but
								// we found it now
								if (missing.get(patientID + protocolName)
										.contains(nextParse)) {
									missing.get(patientID + protocolName)
											.remove(new Integer(nextParse));
								}
								// filling the missing praefix, if we guess
								// there are gaps
								if (nextParse > index.get(patientID
										+ protocolName)) {
									if (nextParse
											- index.get(patientID
													+ protocolName) > 1) {
										for (int i = index.get(patientID
												+ protocolName) + 1; i < nextParse; i++) {
											missing.get(
													patientID + protocolName)
													.add(i);
										}
									}
									// making the highest index higher
									index.put(patientID + protocolName,
											nextParse);
								}
							} catch (NullPointerException e) {
								// The index was missing i guess. Maybe not
								// needed
								// anymore.
								index.put(patientID + protocolName, 1);
							} catch (NumberFormatException e) {
								// I couldnt cut out a number of the praefix
								continue;
							} catch (IndexOutOfBoundsException e) {
								continue;
							}
							// getting two informations, which are needed to
							// compare
							// the dicoms
							KeyMap info[] = { KeyMap.KEY_SERIES_INSTANCE_UID,
									KeyMap.KEY_PATIENTS_BIRTH_DATE };
							String[] att = null;
							for (File dicom : protocolSubfolder.listFiles()) {
								if (dicom.getAbsolutePath().endsWith(".dcm")) {
									// static method, for getting the
									// attributes,
									// because if we always Initialize a new
									// Image
									// class, than we would loose a lot of time.
									att = Image.getAttributesDicom(
											dicom.getAbsolutePath(), info);
									break;
								}
							}

							// Key for the protocolnames HashMap
							String key = patientID + protocolName + att[0]
									+ att[1];
							// If there is no key, i gonna create it
							if (!protocolInfo.containsKey(key)) {
								protocolInfo.put(key, test);
								continue;
							}

						} catch (Exception e) {
							e.printStackTrace();
							stopSort();
						}

					}

				}
			}
		}
		// Searching for Dicoms and sorting them.
		SASSearch(searchin, sortInDir);

		// Now i have to go some instances into the target dir
		for (File patientIdFolder : new File(sortInDir).listFiles()) {
			if (patientIdFolder.isDirectory()) {
				for (File protocolNameFolder : patientIdFolder.listFiles()) {
					// If there is only one subfolder in the protocol folder,
					// than i gonna unpack this one
					boolean onlyone = true;
					if (protocolNameFolder.isDirectory()) {
						for (File protocolSubfolder : protocolNameFolder
								.listFiles()) {
							if (protocolSubfolder.isDirectory()) {
								// test if there is a second folder
								if (protocolSubfolder.getName().startsWith(
										toProtocolDigits(2 + ""))) {
									onlyone = false;
									break;
								}
							}
						}
					}
					// If there is only one, i gonna unpack it
					if (onlyone) {
						for (File protocolSubfolder : protocolNameFolder
								.listFiles()) {
							if (protocolSubfolder.isDirectory()) {
								// searching the lonley subfolder
								if (!protocolSubfolder.getName().equals(
										toProtocolDigits(1 + ""))) {
									continue;
								}
								// delete it, when im finished
								protocolSubfolder.deleteOnExit();
								for (File dicoms : protocolSubfolder
										.listFiles()) {
									if (dicoms.getAbsolutePath().endsWith(
											".dcm")) {
										try {
											// try to unpack them
											Files.move(
													dicoms.toPath(),
													new File(protocolNameFolder
															.getAbsoluteFile()
															+ "/"
															+ dicoms.getName())
															.toPath(),
													StandardCopyOption.REPLACE_EXISTING);
										} catch (IOException e) {
											out.print("IOProblem appeared.");
											permissionProblem = true;
											stopSort();
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
	 * This method is the iterativ search for the dicoms. If this methods finds
	 * dicoms, than this methods calls the
	 * SASInSubfoldersSort/SASInNoSubfoldersSort method with the dicom path and
	 * the output folder, where the sorted structur is build in.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	private void SASSearch(String searchin, String sortInDir) {
		Stack<File> nextFolders = new Stack<File>();
		String path;
		File file = new File(searchin);
		nextFolders.push(file);
		File[] list;

		while (!nextFolders.isEmpty()) {
			file = nextFolders.pop();
			list = file.listFiles();
			if (list == null) {
				// Maybe we dont have a Directory than
				continue;
			}
			for (File potentialDicom : list) {
				// Stopping the sort if the user called stopSort()
				if (stopsort) {
					return;
				}
				path = potentialDicom.getAbsolutePath();
				// We found a dicom?
				if (path.endsWith(".dcm") || path.endsWith(".IMA")
						|| Image.isDicom(potentialDicom.toPath())) {
					// Using the sort structur the user have choosen
					try {
						if (subfolders) {
							SASInSubfoldersSort(path, sortInDir);
						} else {
							SASInNoSubfoldersSort(path, sortInDir);
						}
						// Everytime we have another 50 Dicoms, we communicate
						// with
						// the
						// user.
						if (++found % 50 == 0) {
							out.println("I found and sorted so far " + found
									+ " Dicoms. (DeltaTime: " + deltaTime()
									+ " millis.)");
						}
					} catch (Exception e) {
						// catching potential currupt data
						continue;
					}
				} else {
					// If we didnt found a Dicom, than we put the folder on a
					// stack
					// Also we check, if the Directory is a softlink, because we
					// ignore them, so we dont end in a loop
					if (!Files.isSymbolicLink(potentialDicom.toPath())) {
						nextFolders.push(potentialDicom);
					}
				}
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
		// This makes it more readable
		String patientID = att[0];
		String protocolName = att[1];
		String imageNumber = att[2];
		String instanceUID = att[3];
		String birthDate = att[4];

		// Check existing
		StringBuilder path = new StringBuilder();
		path.append(dir + "/" + patientID);
		existOrCreate(path);
		// I may have to create the protocol folder
		path.append("/" + protocolName);
		existOrCreate(path);

		// Block for protocolname/praefix
		if (!protocolInfo.containsKey(patientID + protocolName + instanceUID
				+ birthDate)) {
			// The praefix of a protocol folder
			String numb;
			if (missing.get(patientID) != null
					&& missing.get(patientID).size() != 0) {
				// filling the missing praefix
				numb = toProtocolDigits(missing.get(patientID).get(0) + "");
				missing.get(patientID).remove(0);
			} else {
				// initializing a the index value to this patientID if its
				// missing
				if (index.get(patientID + protocolName) == null) {
					index.put(patientID + protocolName, 1);
				}
				// If the index is equal to zero, than i put it to 1
				if (index.get(patientID + protocolName) == 0) {
					index.put(patientID + protocolName, 1);
				}
				// getting the praefix number
				numb = toProtocolDigits(index.get(patientID + protocolName)
						+ "");
				index.put(patientID + protocolName,
						index.get(patientID + protocolName) + 1);
			}
			// new protocol subfolder
			protocolInfo.put(
					patientID + protocolName + instanceUID + birthDate, numb);
		}
		// check next protocol and creating it, if its not existant
		path.append("/"
				+ protocolInfo.get(patientID + protocolName + instanceUID
						+ birthDate));
		existOrCreate(path);

		// Whether I change the dicom name to imageNumber.dcm or I just keep the
		// Name.
		String name;
		if (keepImgName) {
			name = new File(input).getName();
		} else {
			if (createNiftis) {
				name = toImgDigits(imageNumber) + ".nii";
			} else {
				name = toImgDigits(imageNumber) + ".dcm";
			}
		}

		// Copy/Move the data
		moveDicom(input, path.toString(), name);
	}

	/**
	 * This methods prepares the sorting structur for dicoms, where the protocol
	 * folders wont contain subfolder of the different angles. Instead of the
	 * subfolders the protocol name is used with a praefix of some digits. After
	 * the preparation is finish, this method calls the rekursiv dicom search
	 * SASSearch.
	 * 
	 * @param searchin
	 * @param sortInDir
	 */
	private void SASInNoSubfolderWrapper(String searchin, String sortInDir) {
		File file = new File(sortInDir);
		File[] list = file.listFiles();

		if (list != null) {
			for (File patientfolder : list) {
				// here im only searching directorys
				if (!patientfolder.isDirectory()) {
					continue;
				}

				String patientID = patientfolder.getName();

				File[] newlist = patientfolder.listFiles();
				if (newlist == null) {
					continue;
				}
				for (File protocolfolder : newlist) {
					String protocolName = protocolfolder.getName();

					try {

						// getting two informations, which are needed to compare
						// the dicoms
						KeyMap info[] = { KeyMap.KEY_SERIES_INSTANCE_UID,
								KeyMap.KEY_PATIENTS_BIRTH_DATE,
								KeyMap.KEY_SERIES_NUMBER };
						String[] att = null;
						for (File dicom : protocolfolder.listFiles()) {
							if (dicom.getAbsolutePath().endsWith(".dcm")
									|| dicom.getAbsolutePath().endsWith(".IMA")) {
								// static method, for getting the attributes,
								// because if we always Initialize a new Image
								// class, than we would loose a lot of time.
								att = Image.getAttributesDicom(
										dicom.getAbsolutePath(), info);
								break;
							}
						}
						if (att == null) {
							continue;
						}

						// Key for the protocolnames HashMap
						String key = patientID
								+ att[2]
								+ protocolName.substring(
										protocolName.indexOf("_") + 1,
										protocolName.length());
						// If there is no key, i gonna create it
						if (!protocolInfo.containsKey(key)) {
							protocolInfo.put(key, att[1] + att[0]);
							continue;
						}

					} catch (Exception e) {
						e.printStackTrace();
						stopSort();
					}
				}
			}
		}
		// Searching and sorting the dicoms here
		SASSearch(searchin, sortInDir);

		// This Method dont have to clean something up, after the sort is
		// finished
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
				KeyMap.KEY_PATIENTS_BIRTH_DATE, KeyMap.KEY_SERIES_NUMBER };
		String[] att = Image.getAttributesDicom(input, info);
		// This makes the code more readable
		String patientID = att[0];
		String protocolName = att[1];
		String imageNumber = att[2];
		String instanceUID = att[3];
		String birthDate = att[4];
		String seriesNumber = att[5];

		StringBuilder path = new StringBuilder();
		// Check existing
		path.append(dir + "/" + patientID);
		existOrCreate(path);

		// Block for checking birth date with target folder
		String key = patientID + seriesNumber + protocolName;
		String curValue = birthDate + instanceUID;
		if (protocolInfo.containsKey(key)) {
			String value = protocolInfo.get(key);
			if (!value.equals(curValue)) {
				if (anyCurropt == false) {
					out.println("ALERT! SOME DATA SEEMS TO BE CORRUPT.\nTHE MOST INFORMATION ARE EQUAL, BUT THE BIRTH DATE + INSTANCE UID ARE NOT.");
					anyCurropt = true;
				}
				path.append("/Corrupt");
				existOrCreate(path);
			}
		} else {
			protocolInfo.put(key, curValue);
		}

		// check next protocol and creating it, if its not existant
		path.append("/" + toProtocolDigits(seriesNumber + "") + "_"
				+ protocolName);
		existOrCreate(path);

		// next the dicom name
		String name;
		if (keepImgName) {
			name = new File(input).getName();
		} else {
			if (createNiftis) {
				name = toImgDigits(imageNumber) + ".nii";
			} else {
				name = toImgDigits(imageNumber) + ".dcm";
			}
		}

		// Copy/Move the data
		moveDicom(input, path.toString(), name);
	}

	private void moveDicom(String input, String output, String name) {
		if (createNiftis) {
			File test = new File(output + "/data.nii");
			if (!test.exists()) {
				ArrayList<String> target = niftihelp.get(output);
				if (target == null) {
					target = new ArrayList<String>();
				}
				target.add(input);
				niftihelp.put(output, target);
				transfered++;
			}
		} else {
			File test = new File(output + "/" + name);
			if (!test.exists()) {
				try {

					if (move) {
						Files.move(new File(input).toPath(), test.toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					} else {
						Files.copy(new File(input).toPath(), test.toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					}

					transfered++;
				} catch (IOException e) {
					out.println("Filetransfer didnt worked");
					permissionProblem = true;
					stopSort();
				}
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
		// atm -> at the moment (current time)
		double atm = System.currentTimeMillis();
		double time = atm - deltaTimeHelp;
		deltaTimeHelp = atm;
		return time;
	}

}
