package tests;

import static org.junit.Assert.assertTrue; // checking the values
import gui.GUI;
import gui.volumetab.VolumeTab;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JSlider;
import javax.swing.JTextField;

import org.junit.Test; // needed for annotation
import org.junit.runner.Result;
import org.junit.runner.notification.Failure; // printing the failures

import util.SortAlgorithm;

// Everytime an error occurs, please write a Test for this (take a look at Software Engineering).

/**
 * Test Class for some simple stuff.
 * 
 * @author Dominik Ridder
 *
 */
public class Testcase {

	/**
	 * Main class for executing the JUnit Testcases. This main is normally not
	 * used, because there is a better way for running all Tests with an
	 * build.xml and ant.
	 * 
	 * @param agrs
	 *            This parameter is currently unused.
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
	 * Tests if the Attributes are displayed after opening a Volume.
	 * <p>
	 * These are the steps of the tests:
	 * <p>
	 * - Create GUI
	 * <p>
	 * - Select VolumeTab
	 * <p>
	 * - Set Path and create Volume
	 * <p>
	 * (Wait max 5 seconds for opening the Volume)
	 * <p>
	 * - Check if the OutputArea is empty
	 */
	@Test
	public void VisibleAttributes() {
		GUI g = new GUI(true, false);
		VolumeTab voltab = (VolumeTab) g.getCurrentTab();

		voltab.setPath("/opt/dridder_local/TestDicoms/AllDicoms/B0092/14_wm_gre_rx=PA");
		voltab.createVolume();

		int millisec = 5000; // 5 sec max
		waitForVolumeCreation(voltab, millisec);

		assertTrue(!voltab.isOutputAreaEmpty());
	}

	/**
	 * This Test should check if the sorting algorithm is working properly.
	 */
	@Test
	public void CheckSort() {
		String unsorted = "/opt/dridder_local/Datadir/Testcase/Unsorted";
		String sorted = "/opt/dridder_local/Datadir/Testcase/Sorted";
		SortAlgorithm sorter = new SortAlgorithm();
		sorter.setCreateNiftis(false);
		sorter.setFilesOptionCopy();
		sorter.setKeepImageName(false);
		sorter.setProtocolDigits(0);
		sorter.setImgDigits(0);
		sorter.useSubfolders(false);
		
		for (int i = 0; i < 2; i++) { // Check once with existant source and once without
			assertTrue("Testdata not Found: "+unsorted+".", new File(unsorted).exists());
			sorter.searchAndSortIn(unsorted, sorted);
		
			File subjectDir = new File(sorted+"/B0316");
			assertTrue("Subject Directory B0316 was not created.", subjectDir.exists());
			
			int dircount = 0;
			int dcmcount = 0;
			for (File potDirectory : subjectDir.listFiles()) {
				if (potDirectory.isDirectory()) {
					dircount++;
					
					for (File dcm : potDirectory.listFiles()) {
						if (dcm.getName().endsWith(".dcm")) {
							dcmcount++;
						}
					}
				}
			}
			
			assertTrue("Subject Directory B0316 should contain 10 directorys.", dircount == 8);
			assertTrue("Subject Directory B0316 should contain 14 dicoms.", dcmcount == 9);
		}
		
		// Clean up Sorted directory
		Stack<File> todelete = new Stack<File>();
		for (File subfile : new File(sorted).listFiles()) {
			todelete.push(subfile);
		}
		
		while(todelete.size() != 0) {
			File next = todelete.pop();
			
			if (next.isDirectory() && next.listFiles().length != 0) {
				todelete.push(next) // Retry later
				for (File subfile : next.listFiles()) {
					todelete.push(subfile);
				}
			} else {
				boolean worked = next.delete();
				if (!worked) {
					break;
				}
			}
		}
		
		assertTrue("Error while deleting sorted directory.", new File(sorted).listFiles().length == 0);
	}

	/**
	 * Test which tries to detect slidebar and textfield equality for slice and
	 * echo. This includes mainly 3 tests:
	 * <p>
	 * o Slice and Echo equality given after instanciating the volume?
	 * <p>
	 * o Does a slider change effect the textfield?
	 * <p>
	 * o Does a textfield change effect the slider?
	 */
	@Test
	public void Slide2Textfield() {
		String volPath = "/opt/dridder_local/Datadir/Sorted/B0316/16_wm_gre_b0";
		int millisec = 5000;
		boolean forceEnd = true;
		boolean visible = false;
		GUI g = new GUI(forceEnd, visible);
		VolumeTab voltab = (VolumeTab) g.getCurrentTab();

		// Setting up gui and volume
		voltab.setPath(volPath);
		voltab.createVolume();
		waitForVolumeCreation(voltab, millisec);
		
		if (voltab.isCreatingVolume()) {
			assertTrue("Volume creation timeout.", false);
			return;
		}
		
		// Finding the components
		String[] names = {"SliceIndex", "EchoIndex", "SliceSlider", "EchoSlider"};
		ArrayList<Component> components = new ArrayList<>();
		Stack<Component> searchThrough = new Stack<Component>();
		
		for (String compName : names) {
			searchThrough.push(voltab);
			
			while(searchThrough.size() > 0) {
				Component comp = searchThrough.pop();
				
				if (comp instanceof Container) {
					for (Component child : ((Container) comp).getComponents()) {
						searchThrough.push(child);
					}
				}
			
				if (compName.equals(comp.getName())) {
					components.add(comp);
					break;
				}
			}
		}
		
		// Did we found all components?
		if (components.size() != 4) {
			assertTrue("Components Not Found.", false);
			return;
		}
		
		// Typecasting
		JTextField sliceIndex = (JTextField) components.get(0);
		JTextField echoIndex = (JTextField) components.get(1);
		JSlider sliceSlider = (JSlider) components.get(2);
		JSlider echoSlider = (JSlider) components.get(3);
		
		// Should be equal after creation
		assertTrue("Slice error after creation.", Integer.parseInt(sliceIndex.getText()) == sliceSlider.getValue());
		assertTrue("Echo error after creation.", Integer.parseInt(echoIndex.getText()) == echoSlider.getValue());
		
		// Testing slidebar to field
		sliceSlider.setValue(80);
		echoSlider.setValue(3);
		assertTrue("Slice Slider to Textfield error.", Integer.parseInt(sliceIndex.getText()) == sliceSlider.getValue());
		assertTrue("Echo Slider to Textfield error.", Integer.parseInt(echoIndex.getText()) == echoSlider.getValue());
		
		// Testing field to slidebar
		sliceIndex.setText("40");
		echoIndex.setText("2");
		sliceIndex.setCaretPosition(1); // Invoke caret change event
		echoIndex.setCaretPosition(1);
		assertTrue("Slice Textfield to Slider error.", Integer.parseInt(sliceIndex.getText()) == sliceSlider.getValue());
		assertTrue("Echo Textfield to Slider error.", Integer.parseInt(echoIndex.getText()) == echoSlider.getValue());
	}

	public void waitForVolumeCreation(VolumeTab voltab, int duration) {
		double start = System.currentTimeMillis();
		
		while (voltab.isCreatingVolume()) { // Finished Creation?
			if (System.currentTimeMillis() - start > duration) {
				break; // Timeout
			} else {
				try {
					Thread.sleep(300); // Wait
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
