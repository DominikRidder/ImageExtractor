package util;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.plugin.DICOM;
import ij.plugin.Nifti_Writer;
import imagehandling.Image;
import imagehandling.headerhandling.KeyMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
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
	 * The Default value for the PatientID, that is used, in case that the
	 * PatientID wasn't saved in the file.
	 */
	public final static String DEFAULT_PATIENTID = "NO_ID";

	/**
	 * The Default value for the Protocol name, that is used, in case that the
	 * Protocol name wasn't saved in the file.
	 */
	public final static String DEFAULT_PROCOLNAME = "NO_PROT_NAME";

	/**
	 * The Default value for the Image number, that is used, in case that the
	 * Image number wasn't saved in the file.
	 */
	public final static String DEFAULT_IMAGENUMBER = "0";

	/**
	 * The Default value for the Birth date, that is used, in case that the
	 * Birth date wasn't saved in the file.
	 */
	public final static String DEFAULT_BIRTHDATE = "NO_BIRTH_DATE";
	/**
	 * The Default value for the number of echos, that is used, in case that the
	 * number of echos wasn't saved in the file.
	 */
	public final static String DEFAULT_ECHONUMBERS = "1";

	/**
	 * The Default value for the series number, that is used, in case that the
	 * series number wasn't saved in the file.
	 */
	public final static String DEFAULT_SERIES_NUMBERS = "1";

	/**
	 * The Default value for the instance uid, that is used, in case that the
	 * instance uid wasn't saved in the file.
	 */
	public final static String DEFAULT_INSTANCE_UID = "1";

	/**
	 * The Default value for the patient name, that is used, in case that the
	 * instance uid wastn't saved in the file.
	 */
	public final static String DEFAULT_PATIENTNAME = "No_Name_Found";

	/**
	 * This boolean is used to print a error Message, when corrupt data was
	 * found. This way i only print one time a error Message.
	 */
	private boolean anyCurropt;

	/**
	 * Defines, if the dicoms transefered as a dicom or as a nifty.
	 */
	private boolean createNiftis = false;

	/**
	 * This hashmap helps, to create nifties.
	 */
	private HashMap<String, ArrayList<String>> dicomtonifti;

	/**
	 * 
	 */
	private HashMap<String, Integer> numbOfEchos;

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
	 * The value keepImgName decides, whether the images are renamed to their
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
	 * Copied is the number of dicoms, that are copied to the output directory.
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
	 * false. The index is used to set the prefix of a protocol folder than. The
	 * key of this HashMap is the patient id and the value is the highest index,
	 * that is missing in the subfolder of a patient folder.
	 */
	private HashMap<String, Integer> index;

	/**
	 * The HashMap protocolnames is used, if subfolders is false. The
	 * protocolnames is used, to check, if 2 Dicoms with the same protocol Name
	 * + series Number + patient ID are equal.
	 */
	private HashMap<String, ArrayList<String>> protocolInfo;

	/**
	 * Missing is used, to fill gaps in the protocol prefix (in the subfolder
	 * sort). If someone would delete the folder 010_protocolname, than the next
	 * folder would use the prefix 010_, even if there are higher prefix
	 * numbers.
	 */
	private HashMap<String, ArrayList<Integer>> missing;

	/**
	 * This is the default constructor. This constructor setting the following
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
	 * This constructor contains the following default setter: - image digits =
	 * 5
	 * <p>
	 * - protocol digits = 3
	 * <p>
	 * Take a look in the Java-doc of the setter methods of these values for
	 * more informations.
	 * 
	 * @param subfolders
	 *            The Options that deciding about the way the generated
	 *            Directory is structured
	 */
	public SortAlgorithm(boolean subfolders) {
		this.subfolders = subfolders;
		img_digits = 5;
		protocol_digits = 3;
	}

	/**
	 * This constructor contains the following default setter: - protocol digits
	 * = 3
	 * <p>
	 * Take a look in the Java-doc of the setter methods of these values for
	 * more informations.
	 * 
	 * @param subfolders
	 *            This Options decides what the generated Directory looks like
	 * @param protocol_digits
	 *            The Number of digits, that should be at least in a Protocol
	 *            name
	 */
	public SortAlgorithm(boolean subfolders, int protocol_digits) {
		this.subfolders = subfolders;
		img_digits = 5;
		this.protocol_digits = protocol_digits;
	}

	/**
	 * With this constructor, you can set all needed parameters at once, so you
	 * don't need any setter methods. Take a look in the Java-doc of the setter
	 * methods of these values for more informations.
	 * 
	 * @param subfolders
	 *            This Options changing the Structure of the generated
	 *            Dictionary
	 * @param protocol_digits
	 *            The Number of digits, that a protocol should at least
	 * @param img_digits
	 *            The Number of digits, that a Image should have at least
	 */
	public SortAlgorithm(boolean subfolders, int protocol_digits, int img_digits) {
		this.subfolders = subfolders;
		this.img_digits = img_digits;
		this.protocol_digits = protocol_digits;
	}

	/**
	 * This method is used, to decide, wether the Names of the files gonna be
	 * changened to their Image Number or if their keep their names.
	 * 
	 * @param keepit
	 *            true if the image name should not change; else false
	 */
	public void setKeepImageName(boolean keepit) {
		keepImgName = keepit;
	}

	/**
	 * The setPrinStream method can be used, to catch the output or the place,
	 * where it is printed.
	 * 
	 * @param out
	 *            The printstream, that should be used, for printing the
	 *            progress
	 */
	public void setPrintStream(PrintStream out) {
		this.out = out;
	}

	/**
	 * The imgDigits contains the information, how many digits are used to save
	 * the image number of a DICOM in the file name. If the image number of a
	 * DICOM is larger than the imgDigits number, than the image number of the
	 * DICOM is taken.
	 * 
	 * @param i
	 *            the amount of digits, that a Image Name should contain at
	 *            least
	 */
	public void setImgDigits(int i) {
		img_digits = i;
	}

	/**
	 * The protocolDigits contains the Information how many digits are used at a
	 * special position. If subfolders is true, than protocolDigits contains the
	 * information for the number of digits for the subfolder of a protocol name
	 * folder. Else, the protocolDigits contains the information, how many
	 * digits are used as a prefix of a protocol folder.
	 * 
	 * @param i
	 *            The Number of Protocol digits that should be used at least.
	 */
	public void setProtocolDigits(int i) {
		protocol_digits = i;
	}

	/**
	 * The boolean value subfolders contains the information, whether the
	 * different angel of the scans are sorted in subfolders in the protocol
	 * name directorys or the different angel are save as the protocol name with
	 * a certain prefix.
	 * 
	 * @param useSubfolders
	 *            The boolean that indicates, whether the Algorithm should use
	 *            subfolders or not.
	 */
	public void useSubfolders(boolean useSubfolders) {
		subfolders = useSubfolders;
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
	 * Defines, if the dicoms transfered as a dicom or as a nifty.
	 * 
	 * @param createniftis
	 *            true, if the found dicoms should be saved as a nifty; false
	 *            otherwise
	 */
	public void setCreateNiftis(boolean createniftis) {
		this.createNiftis = createniftis;
	}

	/**
	 * This method calls toNDigits(protocol_digits, given number String).
	 * 
	 * @param image_number
	 * @return A String representation filled with leading zeros
	 */
	private String toProtocolDigits(String image_number) {
		return toNDigits(protocol_digits, image_number);
	}

	/**
	 * This method calls toNDigits(img_digits, given number String).
	 * 
	 * @param image_number
	 * @return A String representation of a number filled with leading zeros
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
	 * @return A String representation of a number filled with leading zeros
	 */
	private static String toNDigits(int n, String number) {
		StringBuilder digits = new StringBuilder(n);
		for (int i = 0; i < n - number.length(); i++) {
			digits.append("0");
		}
		return digits.toString() + number;
	}

	/**
	 * This method checks if a File to the given path exists and if its not
	 * existing, the method prints the information, that the path gonna be
	 * created and creates the directory to the path.
	 * 
	 * @param path
	 *            The Path to check
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
	 * 
	 * @return True, if the algorithm was stopped; else false
	 */
	public boolean gotStopped() {
		return stopsort;
	}

	/**
	 * Returns the information, if there was a problem with the permission.
	 * 
	 * @return true, if there was a Problem to transfer/copy the Files; false
	 *         else
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
	 * If "sortInDir" not exists, it gonna be created. To specify the behavior
	 * of this method, you can use the setter methods.
	 * 
	 * @param searchin
	 *            The Folder, where the Algorithm search for DICOMs.
	 * @param sortInDir
	 *            The Folder, where the Algorithm move and sort the DICOMs.
	 * @return True if sort algorithm wasn't stopped; else otherwise
	 */
	public boolean searchAndSortIn(String searchin, String sortInDir) {
		// a list of found dicoms is needed, if nifti should be used
		dicomtonifti = new HashMap<>();
		// true if an IOException appears somewhere
		permissionProblem = false;
		// false until the user calls stopSort()
		stopsort = false;
		// time between two system.out.println
		deltaTimeHelp = System.currentTimeMillis();
		double start = deltaTimeHelp;
		// new File, to test if the path is correct
		File file = new File(searchin);
		protocolInfo = new HashMap<String, ArrayList<String>>(100);
		anyCurropt = false;
		numbOfEchos = new HashMap<String, Integer>();

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

		int numberofnii = createNiftis();

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
			operation = "created";
			// transfered = numberofnii;
		}
		if (!createNiftis) {
			out.println("I found and sorted " + found + " Dicoms in " + start
					/ 1000 + " seconds! I " + operation + " " + transfered
					+ " of them to the Output directory.");
		} else {
			out.println("I found " + found + " Dicoms in " + start / 1000
					+ " seconds! I " + operation + " " + numberofnii
					+ " niftis in the Output directory.");
		}
		return true;
	}

	/**
	 * This Method creates the Nifties at the end of the SortAlgorithmen.
	 */
	private int createNiftis() {
		int numberofnii = 0;
		if (createNiftis) {
			out.println("Creating Niftis..");
			Set<String> keys = dicomtonifti.keySet();
			Nifti_Writer writer = new Nifti_Writer();
			writer.dicom_to_nifti = true;

			for (String key : keys) {
				int echon = numbOfEchos.get(key);
				ImageStack is = null;
				dicomtonifti.get(key).sort(new Comparator<String>() {

					public int compare(String o1, String o2) {
						int nr1 = Integer.parseInt(o1.split("#")[0]);
						int nr2 = Integer.parseInt(o2.split("#")[0]);
						if (nr1 > nr2) {
							return 1;
						} else if (nr1 < nr2) {
							return -1;
						} else {
							return 0;
						}
					}
				});
				ImagePlus ip = new ImagePlus();
				int needlast = 0;
				for (String str : dicomtonifti.get(key)) {
					DICOM dcm = new DICOM();
					String imagepath = str.split("#")[1];
					dcm.open(imagepath);
					if (is == null) {
						is = new ImageStack(dcm.getWidth(), dcm.getHeight());
						ip.setCalibration(dcm.getCalibration());
					}
					if (++needlast == dicomtonifti.get(key).size()) {
						ip.setProperty("Filepath", imagepath);
					}

					try {
						is.addSlice(dcm.getProcessor());
					} catch (IllegalArgumentException e) {
						out.println("Dimension missmatched. Image: "
								+ imagepath + " do not match.");
						continue;
					}
				}
				ip.setStack(is);
				if (echon != 1) {
					ip.setDimensions(1, is.getSize() / echon, echon);
					ip.setOpenAsHyperStack(true);
				}
				WindowManager.setTempCurrentImage(ip);

				File test = new File(key);
				String name = key.substring(test.getParent().length(),
						key.length())
						+ ".nii";

				if (writer.save(ip, test.getParent(), name)) {
					numberofnii++;
				}
			}
		}
		return numberofnii;
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
								// protocolInfo.put(key, test);
								protocolInfo.put(key, new ArrayList<String>());
								protocolInfo.get(key).add(test);
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
				if (!potentialDicom.isDirectory()
						&& (path.endsWith(".dcm") || path.endsWith(".IMA") || Image
								.isDicom(potentialDicom.toPath()))) {
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
				KeyMap.KEY_PATIENTS_BIRTH_DATE, KeyMap.KEY_ECHO_NUMBERS_S };
		String[] att = Image.getAttributesDicom(input, info);
		// This makes it more readable
		String patientID = att[0];
		String protocolName = att[1];
		String imageNumber = att[2];
		String instanceUID = att[3];
		String birthDate = att[4];
		String echoNumbers = att[5];

		if (patientID.equals("")) { // att[0]
			patientID = DEFAULT_PATIENTID;
			out.println("PatientID not found. Using Default for sorting: "
					+ DEFAULT_PATIENTID);
		}
		if (protocolName.equals("")) { // att[1]
			patientID = DEFAULT_PROCOLNAME;
			out.println("Protocolname not found. Using Default for sorting: "
					+ DEFAULT_PROCOLNAME);
		}
		if (imageNumber.equals("")) { // att[2]
			patientID = DEFAULT_IMAGENUMBER;
			out.println("Imagenumber not found. Using Default for sorting: "
					+ DEFAULT_IMAGENUMBER);
		}
		if (instanceUID.equals("")) { // att[3]
			patientID = DEFAULT_INSTANCE_UID;
			out.println("InstanceUID not found. Using Default for sorting: "
					+ DEFAULT_INSTANCE_UID);
		}
		if (birthDate.equals("")) { // att[4]
			patientID = DEFAULT_PATIENTID;
			out.println("PatientID not found. Using Default for sorting: "
					+ DEFAULT_PATIENTID);
		}
		if (echoNumbers.equals("")) { // att[5]
			patientID = DEFAULT_ECHONUMBERS;
			out.println("Echonumber not found. Using Default for sorting: "
					+ DEFAULT_ECHONUMBERS);
		}

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
			ArrayList<String> toAdd = new ArrayList<String>();
			toAdd.add(numb);
			protocolInfo.put(
					patientID + protocolName + instanceUID + birthDate, toAdd);
		}
		// check next protocol and creating it, if its not existant
		path.append("/"
				+ protocolInfo.get(patientID + protocolName + instanceUID
						+ birthDate));

		String pathname = path.toString();
		try {
			byte[] latin1 = pathname.getBytes("ISO-8859-1");
			pathname = new String(
					new String(latin1, "ISO-8859-1").getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		if (!createNiftis) {
			existOrCreate(new StringBuilder(pathname));
		}

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

		try {
			byte[] latin1 = name.getBytes("ISO-8859-1");
			name = new String(
					new String(latin1, "ISO-8859-1").getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		// Copy/Move the data
		if (createNiftis) {
			prepareNiftis(input, pathname, echoNumbers, imageNumber);
		} else {
			moveDicom(input, pathname, name);
		}
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
					if (protocolfolder.listFiles() == null) {
						continue;
					}
					try {

						// getting two informations, which are needed to compare
						// the dicoms
						KeyMap info[] = { KeyMap.KEY_SERIES_INSTANCE_UID,
								KeyMap.KEY_PATIENTS_BIRTH_DATE,
								KeyMap.KEY_SERIES_NUMBER, KeyMap.KEY_PATIENTS_NAME,
								KeyMap.KEY_PATIENT_ID, KeyMap.KEY_SERIES_NUMBER,
								KeyMap.KEY_PROTOCOL_NAME};
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
						String key = att[3] + att[5] + att[6];
						// If there is no key, i gonna create it
						if (!protocolInfo.containsKey(key)) {
							protocolInfo.put(key, new ArrayList<String>());
							protocolInfo.get(key).add(att[4] + att[1] + att[0]);
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
				KeyMap.KEY_PATIENTS_BIRTH_DATE, KeyMap.KEY_SERIES_NUMBER,
				KeyMap.KEY_ECHO_NUMBERS_S, KeyMap.KEY_PATIENTS_NAME };
		String[] att = Image.getAttributesDicom(input, info);
		// This makes the code more readable
		String patientID = att[0];
		String protocolName = att[1];
		String imageNumber = att[2];
		String instanceUID = att[3];
		String birthDate = att[4];
		String seriesNumber = att[5];
		String echoNumbers = att[6];
		String patientName = att[7];

		if (patientID.equals("")) { // att[0]
			patientID = DEFAULT_PATIENTID;
			out.println("PatientID not found. Using default for sorting: "
					+ DEFAULT_PATIENTID);
		}
		if (protocolName.equals("")) { // att[1]
			protocolName = DEFAULT_PROCOLNAME;
			out.println("Protocolname not found. Using default for sorting: "
					+ DEFAULT_PROCOLNAME);
		}
		if (imageNumber.equals("")) { // att[2]
			imageNumber = DEFAULT_IMAGENUMBER;
			out.println("Imagenumber not found. Using default for sorting: "
					+ DEFAULT_IMAGENUMBER);
		}
		if (instanceUID.equals("")) { // att[3]
			instanceUID = DEFAULT_INSTANCE_UID;
			out.println("InstanceUID not found. Using default for sorting: "
					+ DEFAULT_INSTANCE_UID);
		}
		if (birthDate.equals("")) { // att[4]
			birthDate = DEFAULT_PATIENTID;
			out.println("PatientID not found. Using default for sorting: "
					+ DEFAULT_PATIENTID);
		}
		if (seriesNumber.equals("")) { // att[5]
			seriesNumber = DEFAULT_SERIES_NUMBERS;
			out.println("Seriesnumber not found. Using default for sorting: "
					+ DEFAULT_SERIES_NUMBERS);
		}
		if (echoNumbers.equals("")) { // att[6]
			echoNumbers = DEFAULT_ECHONUMBERS;
			out.println("Echonumber not found. Using default for sorting: "
					+ DEFAULT_ECHONUMBERS);
		}
		if (patientName.equals("")) {
			patientName = DEFAULT_PATIENTNAME;
			out.println("Patient name not found. Using default for sorting: "
					+ DEFAULT_PATIENTNAME);
		}

		StringBuilder path = new StringBuilder();

		// Block for checking birth date with target folder
		String key = patientName + seriesNumber + protocolName;
		String curValue = patientID + birthDate + instanceUID;
		int valueNumber = -1;
		if (protocolInfo.containsKey(key)) {
			ArrayList<String> existingValues = protocolInfo.get(key);
			for (int i = 0; i < existingValues.size(); i++) {
				String value = existingValues.get(i);
				if (value.equals(curValue)) {
					valueNumber = i;
					break;
				}
			}

			if (valueNumber == -1) {
				valueNumber = existingValues.size();
				existingValues.add(curValue);
			}
		} else {
			protocolInfo.put(key, new ArrayList<String>());
			protocolInfo.get(key).add(curValue);
			valueNumber = 0;
		}

		// Check existing
		if (valueNumber == 0) {
			path.append(dir + "/" + patientName);
		} else {
			path.append(dir + "/" + patientName + "_" + (valueNumber + 1));
		}
		existOrCreate(path);

		// check next protocol and creating it, if its not existant
		path.append("/" + toProtocolDigits(seriesNumber + "") + "_"
				+ protocolName);

		String pathname = path.toString();
		try {
			byte[] latin1 = pathname.getBytes("ISO-8859-1");
			pathname = new String(
					new String(latin1, "ISO-8859-1").getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		if (!createNiftis) {
			existOrCreate(new StringBuilder(pathname));
		}

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

		try {
			byte[] latin1 = name.getBytes("ISO-8859-1");
			name = new String(
					new String(latin1, "ISO-8859-1").getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		// Copy/Move the data
		if (createNiftis) {
			prepareNiftis(input, pathname, echoNumbers, imageNumber);
		} else {
			moveDicom(input, pathname, name);
		}
	}

	private void prepareNiftis(String input, String output, String echoNumbers,
			String imageNumber) {

		if (!dicomtonifti.containsKey(output)) {
			dicomtonifti.put(output, new ArrayList<String>());
		}
		if (!numbOfEchos.containsKey(output)) {
			numbOfEchos.put(output, 1);
		}

		int echon = Integer.parseInt(echoNumbers);

		if (echon > numbOfEchos.get(output)) {
			numbOfEchos.put(output, echon);
		}

		dicomtonifti.get(output).add(imageNumber + "#" + input);
		transfered++;
		//
	}

	private void moveDicom(String input, String output, String name) {
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
				e.printStackTrace();
				out.println("Filetransfer didnt worked");
				permissionProblem = true;
				stopSort();
			}
		}
	}

	/**
	 * This method is used, to get the time diffence from the last call of this
	 * method and this time. If you use this method for the first time, you may
	 * have to set the start value of this class some time before, to get a
	 * usefull value.
	 * 
	 * @return The Time, that elapsed since the last call of deltaTime
	 */
	private double deltaTime() {
		// atm -> at the moment (current time)
		double atm = System.currentTimeMillis();
		double time = atm - deltaTimeHelp;
		deltaTimeHelp = atm;
		return time;
	}

}
