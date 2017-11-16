package main;

import gui.GUI;
import gui.volumetab.VolumeTab;
import imagehandling.Volume;

public class ImageExtractor {
	/**
	 * Main method, to start the GUI.
	 * 
	 * @param agrs
	 *            This value is currently ignored.
	 */
	public static void main(String[] args) {
		String toOpen = null;
		String key = null;
		boolean close = false;
		
	    for (int i = 0; i < args.length; i++) {
	    	switch(args[i]) {
	    	 case "-i": 
	    		 if (args.length > i+1) {
	    			 i++;
	    			 toOpen = args[i];
	    		 } else {
	    			 System.out.println("No filename was provided for input option "+args[i]);
	    			 return;
	    		 }
	    		 break;
	    	 case "-v":
	    		 if (args.length > i+1) {
	    			 i++;
	    			 key = args[i];
	    		 } else {
	    			 System.out.println("No key was provided for input option "+args[i]);
	    			 return;
	    		 }
	    		 break;
	    	 case "-c": close = true; break;
	    	 default:
	    		 System.out.println("Unkown argument: "+args[i]);
	    		 System.exit(1);
	    	}
	    }
		

		GUI gui = new GUI(true, false);

		if (toOpen != null) {
			VolumeTab tab = gui.getFirstVolumeTab();
			if (tab != null) {
				tab.open(toOpen);
			}
			
			if (key != null) {
				if (tab == null) {
					System.out.println("No file is opened. Please open a file using option -i.");
				} else {
					Volume vol = tab.getVolume();
					if (vol != null) {
						String value = vol.getAttribute(key, false);
						System.out.println(value);
					}
				}
			}
		}
		
		if (close) {
			System.exit(0);
		}
		
		gui.setVisible(true);
	}
}
