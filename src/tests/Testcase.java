package tests;

import static org.junit.Assert.*; // checking the values

import java.util.List;

import gui.GUI;
import gui.MyTab;
import gui.sortertab.SorterTab;
import gui.volumetab.VolumeTab;

import org.junit.Test; // needed for annotation
import org.junit.runner.notification.Failure; // printing the failures
import org.junit.runner.*;

public class Testcase {

	public static void main(String[] agrs) {
		Result rc = new Result();

		rc = org.junit.runner.JUnitCore.runClasses(Testcase.class);

		System.out.printf("%d tests were executed, %d of them with failures\n",
				rc.getRunCount(), rc.getFailureCount());
		if (!rc.wasSuccessful()) {
			List<Failure> fList = rc.getFailures();
			for (Failure f : fList) {
				System.out.println(f.getTestHeader());
				System.out.println(f.getMessage());
			}
		}
	}

	@Test
	public void VisibleAttributes() {
		GUI g = new GUI(true, false);
		VolumeTab voltab = (VolumeTab) g.getCurrentTab();

		voltab.setPath("/opt/dridder_local/TestDicoms/AllDicoms/B0092/14_wm_gre_rx=PA");
		voltab.createVolume();
		
		double start = System.currentTimeMillis();
		
		while (voltab.isCreatingVolume()) {
			if (System.currentTimeMillis()-start > 5000) {
				break;
			}else{
				try{
					Thread.sleep(300);
				}catch(InterruptedException e){
					// do nothing
				}
			}
		}
		
		assertTrue(!voltab.isOutputAreaEmpty());
	}

}
