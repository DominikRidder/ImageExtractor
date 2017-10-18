package imagehandling;

import imagehandling.headerhandling.KeyMap;

import java.io.File;

/**
 * This main class is an example for some methods of this Java-project.
 * 
 * @author Dominik Ridder
 *
 */
public class Main {
	/**
	 * The Main method, that can be used to write tests.
	 * 
	 * @param agrs
	 *            This parameter is unused.
	 */
	public static void main(String args[]) {
		/*
		File dicom_sorted = new File("/opt/dridder_local/Desktop/Data_Tuebingen/Water/DICOM_sorted");
		for (File potDir : dicom_sorted.listFiles()) {
			if (potDir.isDirectory()) {
				String name = potDir.getName().replace("_ICEMED", "");
				System.out.println(name);
				
				File testDir = null;
				for (File potGre : potDir.listFiles()) {
					if (!potGre.isHidden() && potGre.getName().contains("m0w")) {
						testDir = potGre;
						break;
					}
				}
				System.out.println("\t"+testDir.getName());
				File testDicom = null;
				for (File potDicom : testDir.listFiles()) {
					if (potDicom.getName().endsWith(".dcm") || potDicom.getName().endsWith(".IMA")) {
						testDicom = potDicom;
						break;
					}
				}
				System.out.println("\t"+testDicom.getName());
				Image img = new Image(testDicom.getAbsolutePath());
				String flipAngle = img.getAttribute(KeyMap.KEY_REPETITION_TIME);
//				Volume vol = new DICOMVolume(testDir.getAbsolutePath());
//				String flipAngle = vol.getAttribute(KeyMap.KEY_FLIP_ANGLE, false);
				System.out.println("\t"+flipAngle);
			}
		}*/
		if (args.length < 1) {
			System.out.println("An directory (command line argument) is needed to search for the genders.");
			return;
		} else if (args.length > 1) {
			System.out.println(args.length+" arguments are given. Please use only one argument (the datalocation directory).");
			return;
		}
		
		//File datalocation = new File("/opt/dridder_local/Desktop/Data_Tuebingen/Water/DICOM_sorted");
		File datalocation = new File(args[0]);
		int dicomAtt = 0;
		boolean foundPatient = false;
		
		if (!datalocation.exists()) {
			System.out.println("The given directory do not exist.");
			return;
		}
		
		if (!datalocation.isDirectory()) {
			System.out.println("The given argument is not a directory.");
			return;
		}
		
		for (File dataset : datalocation.listFiles()) {
			if (!dataset.isDirectory()) {
				continue;
			}
			dicomAtt = 0;
			outer : for (File potDicomDir : dataset.listFiles()) {
				if (!potDicomDir.isDirectory() || potDicomDir.listFiles() == null) {
					continue;
				}
				for (File potDicom : potDicomDir.listFiles()) {
					if (!potDicom.isFile()) {
						continue;
					}
					if (potDicom.getName().endsWith(".dcm") || potDicom.getName().endsWith(".IMA")) {
						try {
							Image img = new Image(potDicom.getPath());
							String sex = img.getAttribute(KeyMap.KEY_PATIENTS_SEX);
							System.out.println(dataset.getName());
							System.out.println("    Patients sex = "+sex);
							foundPatient = true;
							break outer;
						} catch (Exception e) {
							System.out.println("Error: "+e.getMessage());
						}
					}
				}
				
				if (dicomAtt >= 2) {
					break;
				}
			}
		}
		
		if (!foundPatient) {
			System.out.println("No datasets where found.");
			System.out.println("In ordner to find out the patients sex, the following structur have to be followed:");
			System.out.println("<Command Line Argument>/<Datasets | Patient aliases>/<Dicom Folders>/<Dicoms (dcm|IMA)>");
		}
		
		// Volume vol = new
		// Volume("/opt/dridder_local/TestDicoms/AllDicoms/112233/2_si_gre_m0w__w2dfl");
		// Point2D roi = new Point2D(30,30);
		// VolumeFitter vf = new VolumeFitter();

		//
		// TextOptions topt = new TextOptions();
		// topt.addSearchOption(TextOptions.ATTRIBUTE_NAME);
		// topt.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);
		// topt.addSearchOption(TextOptions.ATTRIBUTE_VALUE);
		//
		// // setting the return expression
		// topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER + " "+
		// TextOptions.ATTRIBUTE_NAME + ": "+ TextOptions.ATTRIBUTE_VALUE);
		//
		// vol.setTextOptions(topt);
		//
		// System.out.println(vol.getAttribute("echo"));

		// SortAlgorithm sa = new SortAlgorithm();
		// sa.useSubfolders(false);
		// sa.setImgDigits(0);
		// sa.setProtocolDigits(0);
		// sa.setFilesOptionCopy();
		// sa.setKeepImageName(true);
		// sa.setCreateNiftis(true);
		// sa.searchAndSortIn("/opt/dridder_local/TestDicoms/NiftiTest/112233/2_si_gre_m0w__w2dfl",
		// "/opt/dridder_local/TestDicoms/NiftiTest/");

		// sa.useSubfolders(true);
		// sa.searchAndSortIn("/opt/dridder_local/TestDicoms/AllDicoms",
		// "/opt/dridder_local/TestDicoms/AllDicomsInSubfolder");
	}
}