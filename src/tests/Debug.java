package tests;

import gui.GUI;
import gui.volumetab.VolumeTab;
import imagehandling.Volume;
import imagehandling.headerhandling.KeyMap;
import imagehandling.headerhandling.TextOptions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * This class is used for smaller testcases or for new methods.
 * 
 * @author Dominik Ridder
 *
 */
public class Debug {
	private static GUI gui;
	private static VolumeTab voltab;

	/**
	 * This main method executes some written tests of furture implementations.
	 * 
	 * @param agrs
	 *            This paramater is unused.
	 */
	public static void main(String[] agrs) {
		String filepath = "/opt/dridder_local/TestDicoms/AllDicoms/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/11_gre_t2star";
		Volume vol = Volume.createVolume(filepath);
		vol.getTextOptions().setReturnExpression(
				"" + TextOptions.ATTRIBUTE_VALUE);

		KeyMap att1 = KeyMap.KEY_FLIP_ANGLE;
		KeyMap att2 = KeyMap.KEY_ECHO_NUMBERS_S;
		// KeyMap att2 = KeyMap.KEY_WINDOW_WIDTH;

		int count1 = 0;
		ArrayList<String> foundtypes1 = new ArrayList<String>();
		int count2 = 0;
		ArrayList<String> foundtypes2 = new ArrayList<String>();

		for (int i = 0; i < vol.size(); i++) {
			if (!foundtypes1.contains(vol.getAttribute(att2, i))) {
				foundtypes1.add(vol.getAttribute(att2, i));
				count1++;
			}
			if (!foundtypes2.contains(vol.getAttribute(att1, i))) {
				foundtypes2.add(vol.getAttribute(att1, i));
				count2++;
			}
		}

		System.out.println(count2);
		System.out.println(count1);

		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < vol.size(); i++) {
			data.add(i + "#" + toNDigits(4, vol.getAttribute(att2, i)) + "#"
					+ toNDigits(4, vol.getAttribute(att1, i)));
		}

		data.sort((str1, str2) -> {
			int mark11 = str1.indexOf('#');
			int mark12 = str1.indexOf('#', mark11 + 1);
			int mark21 = str2.indexOf('#');
			int mark22 = str2.indexOf('#', mark21 + 1);

			int comp1 = str1.substring(mark11, mark12).compareTo(
					str2.substring(mark21, mark22));
			if (comp1 != 0) {
				return comp1;
			} else {
				return str1.substring(mark12, str1.length()).compareTo(
						str2.substring(mark22, str2.length()));
			}
		});

		int firstcount = -1;
		int nextcount = 0;
		String lastString = data.get(0).split("#")[1];

		boolean matching = true;
		for (String s : data) {
			if (!s.split("#")[1].equals(lastString)) {
				lastString = s.split("#")[1];
				if (firstcount == -1) {
					firstcount = nextcount;
				} else if (nextcount != firstcount) {
					matching = false;
					break;
				}
				nextcount = 0;
			}
			nextcount++;
		}
		if (firstcount == -1) {
			firstcount = nextcount;
		} else if (nextcount != firstcount) {
			matching = false;
		}

		if (matching) {
			System.out.println("KeyMap combination is ok for the Dimension");

			int[][] indexes = new int[count1][];

			firstcount = -1;
			nextcount = 0;
			lastString = data.get(0).split("#")[1];

			int i = 0;
			int j = 0;
			for (String s : data) {
				if (!s.split("#")[1].equals(lastString)) {
					lastString = s.split("#")[1];
					j++;
					i = 0;
				}

				indexes[j][i] = Integer.parseInt(s.split("#")[0]);

				i++;
			}

			for (int[] a : indexes) {
				System.out.print("{ ");
				for (int b : a) {
					System.out.print(b + ",");
				}
				System.out.println(" }");
			}

		} else {
			System.out
					.println("KeyMap combination is NOT ok for the Dimension");
		}
	}

	private static String toNDigits(int n, String number) {
		StringBuilder digits = new StringBuilder(n);
		for (int i = 0; i < n - number.length(); i++) {
			digits.append("0");
		}
		return digits.toString() + number;
	}

	/**
	 * @param x
	 *            The x coordinate of the roi, that should be set.
	 * @param y
	 *            The y coordinate of the roi, that should be set.
	 */
	public static void roitest(int x, int y) {
		voltab.setRoiPosition(x, y);
		voltab.showROI(true);
	}

	/**
	 * This method calls the "calculate Zero Echo" method of the VolumeTab by
	 * using the Actionperformed Method.
	 */
	public static void zeroEcho() {
		ActionEvent ae = new ActionEvent(gui, 0, "calc Zero Echo");
		voltab.actionPerformed(ae);
	}

	/**
	 * This Method is used to wait an amount of Time.
	 * 
	 * @param milli
	 *            The Time that this thread should sleep.
	 */
	public static void sleep(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
