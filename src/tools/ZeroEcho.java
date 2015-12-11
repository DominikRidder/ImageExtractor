package tools;

import gui.volumetab.VolumeTab;
import ij.ImagePlus;
import ij.gui.PointRoi;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ZeroEcho implements Runnable {

	public boolean isrunning = true;
	public ArrayList<BufferedImage> echo0;
	private Volume vol;
	private VolumeTab parent;

	public ZeroEcho(Volume vol, VolumeTab volumeTab) {
		this.vol = vol;
		parent = volumeTab;
	}

	public void run() {
		// if (!isrunning){
		// isrunning = true;
		CalculateZeroEcho();
		isrunning = false;
		parent.addZeroEcho(echo0, "fitting");
		// }
	}

	private void CalculateZeroEcho() {
		echo0 = new ArrayList<BufferedImage>();

		VolumeFitter volfit = new VolumeFitter();

		ArrayList<ImagePlus> data = vol.getData();
		PointRoi roi = new PointRoi(0, 0);
		System.out.println(data.get(0).getWidth());
		System.out.println(data.get(0).getHeight());

		int slice_perEcho = vol.size()
				/ Integer.parseInt(vol.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S,
						vol.size() - 1));

		echo0 = new ArrayList<BufferedImage>(slice_perEcho);
		int counter = 0, max;
		int last = -1;
		// double values[][][] = new
		// double[slice_perEcho][data.get(0).getWidth()][data
		// .get(0).getHeight()];

		max = data.get(0).getWidth() * data.get(0).getHeight() * slice_perEcho;
		for (int s = 0; s < slice_perEcho; s++) {
			BufferedImage nextimg = new BufferedImage(data.get(s).getWidth(),
					data.get(s).getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			echo0.add(nextimg);
			for (int x = 0; x < data.get(s).getWidth(); x++) {
				for (int y = 0; y < data.get(s).getHeight(); y++) {
					int next = (((++counter)* 100 / max));
					if (next > last) {
						System.out.println(++last + "%");
					}
					
					nextimg.setRGB(x, y,
							(int) volfit.getZeroValue(vol, x, y, s, 0, false));
					
				}
			}
		}

		System.out.println("finished");

	}
}
