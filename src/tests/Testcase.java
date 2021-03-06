package tests;

import static org.junit.Assert.*; // checking the values
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

		assertFalse("File header did not show up in GUI.", voltab.isOutputAreaEmpty());
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
				todelete.push(next); // Retry later
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
		assertTrue("Volume creation timeout.", !voltab.isCreatingVolume());
		
		// Finding the components
		String[] names = {"SliceIndex", "EchoIndex", "SliceSlider", "EchoSlider"};
		ArrayList<Component> components = findComponentsByName(voltab, names);
		
		// Did we found all components?
		assertNotEquals("Components Not Found.", components, null);
		
		// Typecasting
		JTextField sliceIndex = (JTextField) components.get(0);
		JTextField echoIndex = (JTextField) components.get(1);
		JSlider sliceSlider = (JSlider) components.get(2);
		JSlider echoSlider = (JSlider) components.get(3);
		
		// Should be equal after creation
		assertEquals("Slice error after creation.", Integer.parseInt(sliceIndex.getText()), sliceSlider.getValue());
		assertEquals("Echo error after creation.", Integer.parseInt(echoIndex.getText()), echoSlider.getValue());
		
		// Testing slidebar to field
		sliceSlider.setValue(80);
		echoSlider.setValue(3);
		assertEquals("Slice Slider to Textfield error.", Integer.parseInt(sliceIndex.getText()), sliceSlider.getValue());
		assertEquals("Echo Slider to Textfield error.", Integer.parseInt(echoIndex.getText()), echoSlider.getValue());
		
		// Testing field to slidebar
		sliceIndex.setText("40");
		echoIndex.setText("2");
		sliceIndex.setCaretPosition(1); // Invoke caret change event
		echoIndex.setCaretPosition(1);
		assertEquals("Slice Textfield to Slider error.", Integer.parseInt(sliceIndex.getText()), sliceSlider.getValue());
		assertEquals("Echo Textfield to Slider error.", Integer.parseInt(echoIndex.getText()), echoSlider.getValue());
	}
	
	/**
	 * Tests, if the textfields for the slice and the echo number is resistant to letters.
	 */
	@Test
	public void lettersInNumericField() {
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
		assertTrue("Volume creation timeout.", !voltab.isCreatingVolume());
		
		// Finding the components
		String[] names = {"SliceIndex", "EchoIndex"};
		ArrayList<Component> components = findComponentsByName(voltab, names);
		
		// Did we found all components?
		assertNotEquals("Components Not Found.", components, null);
		
		// Typecasting
		JTextField sliceIndex = (JTextField) components.get(0);
		JTextField echoIndex = (JTextField) components.get(1);

		// Testing field to slidebar
		sliceIndex.setText("40a");
		echoIndex.setText("2b");
		assertFalse("Slice textfield did accept the letter a.", sliceIndex.getText().contains("a"));
		assertFalse("Echo textfield did accept the letter b.", echoIndex.getText().contains("b"));
	}
	
	/**
	 * Tests, if the textfields for the slice and the echo number is resistant to high numbers.
	 */
	@Test
	public void toHighSliceOrEcho() {
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
		assertFalse("Volume creation timeout.", voltab.isCreatingVolume());
		
		// Finding the components
		String[] names = {"SliceIndex", "EchoIndex"};
		ArrayList<Component> components = findComponentsByName(voltab, names);
		
		// Did we found all components?
		assertNotEquals("Components Not Found.", components, null);
		
		// Typecasting
		JTextField sliceIndex = (JTextField) components.get(0);
		JTextField echoIndex = (JTextField) components.get(1);

		// Testing field to slidebar
		sliceIndex.setText("800");
		echoIndex.setText("4");
		try{
			int slice = Integer.parseInt(sliceIndex.getText());
			int echo = Integer.parseInt(echoIndex.getText());
			assertTrue("Slice textfield become higher than maxslice by inserting 800. Slice textfield value = "+slice+".", slice<=80);
			assertTrue("Slice textfield become higher than maxecho by inserting 4. Echo textfield value = "+echo+".", echo<=3);
		}catch(NumberFormatException e) {
			assertTrue("Could not parse Integer from slice field.", false);
			assertTrue("Could not parse Integer from echo field.", false);
		}
	}
	
	
	public ArrayList<Component> findComponentsByName(Container container, String[] names) {
		ArrayList<Component> components = new ArrayList<Component>();
		Stack<Component> searchThrough = new Stack<Component>();
		
		for (String compName : names) {
			searchThrough.push(container);
			
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
			
			if (components.size()<1 || components.get(components.size()-1).getName() != compName) {
				return null;
			}
		}
		
		return components;
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
