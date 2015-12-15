package tools;

import gui.volumetab.VolumeTab;
import ij.ImagePlus;
import ij.gui.PointRoi;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ZeroEcho implements Runnable {

	public ArrayList<BufferedImage> echo0;
	private Volume vol;
	private VolumeTab parent;
	private boolean isfirst = true;
	private int width,height;
	private int degree;
	private boolean takelog;

	public ZeroEcho(Volume vol, VolumeTab volumeTab, int fittingfunction, boolean log) {
		this.vol = vol;
		this.degree = fittingfunction;
		parent = volumeTab;
		takelog = log;
	}

	public void run() {
		int cores = Runtime.getRuntime().availableProcessors();
		if (cores < 1){
			cores = 1;
		}
		
		Thread[] threads = new Thread[cores];
		echo0 = new ArrayList<BufferedImage>();
		
		int slice_perEcho = vol.size()
				/ Integer.parseInt(vol.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S,
						vol.size() - 1));
		
		ImagePlus data = vol.getData().get(0);
		
		width = data.getWidth();
		height = data.getHeight();
		
		for (int s = 0; s < slice_perEcho; s++) {
			BufferedImage nextimg = new BufferedImage(data.getWidth(),
					data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			echo0.add(nextimg);
		}
		
		
		final int todo = slice_perEcho / threads.length;
		final int[] offset = new int[threads.length];
		for (int i = 0; i < threads.length; i++) {
			offset[i] = todo*i;
		}
		
		int max = width*height*slice_perEcho;
		
		System.out.println("Using "+threads.length+" Threads for calculation...");
		
		double start = System.currentTimeMillis();
		for (int i = 0; i < threads.length; i++) {
			final int next = todo*i;
			if (i == threads.length-1) {
                                final int tocalc = todo + slice_perEcho - todo * threads.length;
				threads[i] = new Thread(new Runnable(){

					@Override
					public void run() {
						CalculateZeroEcho(tocalc, next);
						
					}});
			} else {
				threads[i] = new Thread(new Runnable(){

					@Override
					public void run() {
						CalculateZeroEcho(todo, next);
						
					}});
			}
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("100%");
		System.out.println("finished");

		double neededtime = (System.currentTimeMillis()-start);
		System.out.println("Needed "+neededtime+" mill. to Perform "+max+" fits.");
		System.out.println("(Performing "+(int)(max/(neededtime/1000))+" fits per second)");
		
		parent.addZeroEcho(echo0, "fitting");
	}

	private void CalculateZeroEcho(int todo, int offset) {
		VolumeFitter volfit = new VolumeFitter();
		
		int counter = 0;
		int last = -1;
		int max = width * height * todo;

		for (int s = offset; s < offset+todo; s++) {
			int rgbArray[] = new int[width*height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if ((offset==0) && (((++counter) * 100 / max)) > last && last != 99) {
						System.out.println(++last + "%");
					}
					rgbArray[x+y*width] = (int) volfit.getZeroValue(vol, x, y, s, degree, takelog);
				}
			}
			BufferedImage next = echo0.get(s);
			next.setRGB(0, 0, width, height, rgbArray, 0, width);
		}

	}
}
