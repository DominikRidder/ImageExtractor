package imagehandling;

/**
 * This main class is an example for some methods of this Java-project.
 * 
 * @author dridder_local
 *
 */
public class Main {
	public static void main(String agrs[]) {
		String filepath = "/opt/dridder_local/TestDicoms/AllDicoms/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/1_vg_gre_m0w__w3dfl";
		Volume vol = new Volume(filepath);

		TextOptions topt = new TextOptions();
		topt.addSearchOption(TextOptions.ATTRIBUTE_NAME);
		topt.addSearchOption(TextOptions.ATTRIBUTE_NUMBER);
		topt.addSearchOption(TextOptions.ATTRIBUTE_VALUE);

		// setting the return expression
		topt.setReturnExpression(TextOptions.ATTRIBUTE_NUMBER + " "+ TextOptions.ATTRIBUTE_NAME + ": "+ TextOptions.ATTRIBUTE_VALUE);

		vol.setTextOptions(topt);

		System.out.println(vol.getAttribute("echo"));

//		 SortAlgorithm sa = new SortAlgorithm();
//		 sa.useSubfolders(false);
//		 sa.setImgDigits(4);
//		 sa.setProtocolDigits(3);
//		 sa.setFilesOptionCopy();
//		 sa.setKeepImageName(false);
		// sa.searchAndSortIn("/opt/dridder_local/TestDicoms/TestSort6",
		// "/opt/dridder_local/TestDicoms/TestSort6");

//		 sa.useSubfolders(true);
//		 sa.searchAndSortIn("/opt/dridder_local/TestDicoms/InSubfolders",
//		 "/opt/dridder_local/TestDicoms/InSubfolders2");
	}
}