package tests;

import java.awt.event.ActionEvent;
import java.io.File;

import gui.GUI;
import gui.MyTab;
import gui.volumetab.VolumeTab;

public class Debug {
	private static GUI gui;
	private static VolumeTab voltab;
	
	public static void main(String []agrs){
		gui = new GUI(true);
		MyTab curtab = gui.getCurrentTab();
		
		if (curtab instanceof VolumeTab){
			voltab = (VolumeTab) curtab;

			voltab.setPath("/opt/dridder_local/TestDicoms/AllDicoms/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/15_si_gre_b0");
			voltab.createVolume();

			while(voltab.isCreatingVolume()){
				sleep(100);
			}
			
			System.out.println("creation finished");
			
//			roitest(100, 100);
		}
	}
	
	
	public static void roitest(int x, int y){
		voltab.setRoiPosition(x, y);
		voltab.showROI(true);
	}
	
	public static void zeroEcho(){
		ActionEvent ae = new ActionEvent(gui, 0, "calc Zero Echo");
		voltab.actionPerformed(ae);
	}
	
	public static void sleep(int milli){
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
