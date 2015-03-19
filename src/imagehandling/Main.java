package imagehandling;

import java.util.Scanner;

public class Main {
	public static void main(String agrs[]) {
		String filepath = "/opt/dridder_local/TestDicoms/Testfolder";
		Volume vol = new Volume(filepath);

		// try to get a Attribute
		String str = vol
				.getAttribute("I have no clue what i should type here.");
		System.out.println("The attribute i searched for: " + str + "\n");

		// working with the Image
		Image img = vol.getSlice(10000);
		img = vol.getSlice(-50);
		// It should be the same header as ArrayList<String>headers.get(0)
		if (img.getHeader().equals(vol.getHeader().get(0))) {
			System.out.println("These headers are the same.\n");
		} else {
			System.out.println("These headers are different.\n");
		}
		
		// Extract 2 times
		Scanner sc = new Scanner(System.in);
		System.out.println("Is it ok for you, if i extract the header?(y/n)");
		if (sc.next().charAt(0) == 'y') {
			System.out.println("First time vol.extractHeader():");
			vol.extractHeader();
			System.out.println("Second time vol.extractHeader():");
			vol.extractHeader();
		} else {
			System.out.println("Well than i wont extract them.");
		}
		sc.close();
		
		System.out.println(Image.getKeyWords("*data"));
	}
}