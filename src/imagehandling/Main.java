package imagehandling;

public class Main {
	public static void main(String agrs[]) {
		String filepath = "/opt/dridder_local/TestDicoms/Testfolder/B0092/1_wm_gre_rx=PA";
		Volume vol = new Volume(filepath);

		TextOptions topt = new TextOptions();
		topt.addSearchOption(TextOptions.SEARCH_IN_ATTRIBUTE_NAME);
		topt.addSearchOption(TextOptions.SEARCH_IN_ATTRIBUTE_NUMBER);
		topt.addSearchOption(TextOptions.SEARCH_IN_ATTRIBUTE_VALUE);

		// the order of the ReturnOptions matters
		topt.addReturnOption(TextOptions.RETURN_ATTRIBUTE_NUMBER);
		topt.addReturnOption(TextOptions.RETURN_ATTRIBUTE_NAME_WITH_COLON);
		topt.addReturnOption(TextOptions.RETURN_ATTRIBUTE_VALUE);

		topt.setSplittString(" ");

		vol.setTextOptions(topt);

		System.out.println(vol.getAttribute("*echo*"));

		// SortAlgorithm sa = new SortAlgorithm();
		// sa.useSubfolders(false);
		// sa.setImgDigits(4);
		// sa.setProtocolDigits(3);
		// sa.setFilesOptionCopy();
		// sa.searchAndSortIn("/opt/dridder_local/TestDicoms/TestSort6",
		// "/opt/dridder_local/TestDicoms/TestSort6");

		// sa.useSubfolders(true);
		// sa.searchAndSortIn("/opt/dridder_local/TestDicoms",
		// "/opt/dridder_local/TestDicoms/TestSort2");
	}
}