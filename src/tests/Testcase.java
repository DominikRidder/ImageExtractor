package tests; 

import static org.junit.Assert.assertTrue; // checking the values
import gui.GUI;
import gui.volumetab.VolumeTab;

import java.util.List;

import org.junit.Test; // needed for annotation
import org.junit.runner.Result;
import org.junit.runner.notification.Failure; // printing the failures

// Everytime an error occurs, please write a Test for this (take a look at Software Engineering).

/**
 * Test Class for some simple stuff.
 *  
 * @author Dominik Ridder
 *
 */
public class Testcase {

	/**
	 * Main class for executing the JUnit Testcases. This main is normally not used, 
	 * because there is a better way for running all Tests with an build.xml and ant.
	 */
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

	/**
	 * Tests if the Attributes are displayed after opening a Volume.<p>
	 * These are the steps of the tests:<p>
	 * - Create GUI <p>
	 * - Select VolumeTab <p>
	 * - Set Path and create Volume <p>
	 * (Wait max 5 seconds for opening the Volume) <p>
	 * - Check if the OutputArea is empty <p>
	 */
	@Test
	public void VisibleAttributes() {
		GUI g = new GUI(true, false);
		VolumeTab voltab = (VolumeTab) g.getCurrentTab();

		voltab.setPath("/opt/dridder_local/TestDicoms/AllDicoms/B0092/14_wm_gre_rx=PA");
		voltab.createVolume();
		
		double start = System.currentTimeMillis();
		
		while (voltab.isCreatingVolume()) { // wait max 5 sec for open the volume
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
