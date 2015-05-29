package imagehandling;

import java.io.File;

public class TestMain {
	public static void main(String []agrs){
		File f = new File("/opt/dridder_local/TestDicoms/TestSort/1");
		f.renameTo(new File("/opt/dridder_local/TestDicoms/TestSort/2"));
	}
}
