package main;

public class TestMainInput {

	public static void main(String[] args) {
		args = new String[]{"-i","~/Datadir/T12009/3D2P/1mm/6axes/gre3D_40deg.nii","-v","slice*","-c"};
		
		ImageExtractor.main(args);
	}
	
}
