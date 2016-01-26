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
	public void First() {
		GUI g = new GUI(true, false);
		MyTab tab = g.getCurrentTab();

		assertTrue(tab instanceof SorterTab);
	}
}
