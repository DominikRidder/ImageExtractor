package imagehandling;

import java.util.Vector;

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
		
		vol.extractHeader();

//		System.out.println(img.getAttribute(Image.getKeyWords("*birth*")));
		System.out.print(img.getAttribute("patient id"));
		System.out.print(img.getAttribute("study id"));
		System.out.println(img.getAttribute("patients birth date"));
		
		Vector<Integer> choice = new Vector<Integer>();
		choice.add(3);
		choice.add(5);
		choice.add(10);
		System.out.println(vol.getAttribute("pixel data",choice ));
		
		new Gui();
	}
}