package tests;

import gui.GUI;
import gui.MyTab;
import gui.volumetab.VolumeTab;

import java.awt.event.ActionEvent;

public class Debug {
	private static GUI gui;
	private static VolumeTab voltab;

	public static void main(String[] agrs) {
		// String nifti =
		// "/opt/dridder_local/TestDicoms/Nifti/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/015_si_gre_b0.nii";
		// Volume vol = new Volume(nifti);
		// System.out.println(vol.getHeader().get(0));

		// SortAlgorithm sa = new SortAlgorithm();
		// sa.setFilesOptionCopy();
		// sa.setImgDigits(0);
		// sa.setKeepImageName(true);
		// sa.setCreateNiftis(true);
		//
		String input = "/opt/dridder_local/TestDicoms/AllDicoms/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/15_si_gre_b0";
		// String output = "/opt/dridder_local/TestDicoms/Nifti";
		//
		// File f = new
		// File("/opt/dridder_local/TestDicoms/Nifti/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/015_si_gre_b0.nii");
		//
		// if (f.exists()){
		// System.out.println("File exist. I try to delete it.");
		// if (f.delete()){
		// System.out.println("Delete sucessfull");
		// }else{
		// System.out.println("Delete failed!");
		// }
		// }
		//
		//
		// if (sa.searchAndSortIn(input, output)){
		// System.out.println("sa sucessfull");
		// }else{
		// System.out.println("sa failed");
		// }
		//
		//
		//
		gui = new GUI(true, true);
		MyTab curtab = gui.getCurrentTab();
		//
		// // if (curtab instanceof SorterTab){
		// // sorttab = (SorterTab) curtab;
		// //
		// // }
		if (curtab instanceof VolumeTab) {
			voltab = (VolumeTab) curtab;
			voltab.setPath(input);
			// //
			// voltab.setPath("C:/Users/Dominik/Desktop/Dominik_ordner/Learning/Dicom/Testfolder");
			// //
			// voltab.setPath("/opt/dridder_local/TestDicoms/AllDicoms/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/15_si_gre_b0");
			voltab.createVolume();
			while (voltab.isCreatingVolume()) {
				sleep(100);
			}
			voltab.setShape("Circle");
			// voltab.setRoiPosition(100, 100);
			voltab.setRoiPosition(200, 200);
			// voltab.setShape("Circle");
			// // voltab.actionOpenInExternal();
			// //
			// // while(voltab.isCreatingVolume()){
			// // sleep(100);
			// // }
			// //
			// // System.out.println("creation finished");
			//
			// // roitest(100, 100);
		}
	}

	public static void roitest(int x, int y) {
		voltab.setRoiPosition(x, y);
		voltab.showROI(true);
	}

	public static void zeroEcho() {
		ActionEvent ae = new ActionEvent(gui, 0, "calc Zero Echo");
		voltab.actionPerformed(ae);
	}

	public static void sleep(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
