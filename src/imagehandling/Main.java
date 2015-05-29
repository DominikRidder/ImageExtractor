package imagehandling;

public class Main {
	public static void main(String agrs[]) {
		// String filepath = "/opt/dridder_local/TestDicoms/Testfolder";
		// Volume vol = new Volume(filepath);
		// String compare = vol.getAttribute(KeyMap.KEY_SERIES_INSTANCE_UID);
		// for (int i = 1; i < 11; i++) {
		// filepath =
		// "/opt/dridder_local/TestDicoms/TestSort2/112233/si_gre_m0w__w2dfl/"
		// + i;
		// vol = new Volume(filepath);
		// System.out.println(compare.equals(vol.getAttribute(KeyMap.KEY_SERIES_INSTANCE_UID)));
		// }

		// // try to get a Attribute
		// String str = vol
		// .getAttribute("I have no clue what i should type here.");
		// System.out.println("The attribute i searched for: " + str + "\n");
		//
		// // working with the Image
		// Image img = vol.getSlice(10000);
		// img = vol.getSlice(-50);
		// // It should be the same header as ArrayList<String>headers.get(0)
		// if (img.getHeader().equals(vol.getHeader().get(0))) {
		// System.out.println("These headers are the same.\n");
		// } else {
		// System.out.println("These headers are different.\n");
		// }
		//
		// vol.extractHeader();
		//
		// //
		// System.out.println(Image.getKeyWords("*birth*"));
		// System.out.println(img.getAttribute("*y?id"));
		// System.out.print(img.getAttribute("patient id"));
		// System.out.print(img.getAttribute("study id"));
		// System.out.println(img.getAttribute("patients birth date"));
		//
		// Vector<Integer> choice = new Vector<Integer>();
		// choice.add(3);
		// choice.add(5);
		// choice.add(10);
		// System.out.println(vol.getAttribute("pixel data",choice ));
		//
		// new Gui();

		// System.out.println(">>"+vol.getSlice(0).getAttribute(KeyMap.KEY_ACQUISITION_DATE)+"<<");

		SortAlgorithm sa = new SortAlgorithm();
		sa.useSubfolders(true);
		sa.setImgDigits(4);
		sa.setProtocolPraefixDigits(3);
		sa.searchAndSortIn("/opt/dridder_local/TestDicoms/30000",
				"/opt/dridder_local/TestDicoms/TestSort");
		//
		// Volume.searchAndSortIn("/data/mr_qi/zabbas/Dominik/",
		// "/opt/dridder_local/TestDicoms/TestSort");
	}
}