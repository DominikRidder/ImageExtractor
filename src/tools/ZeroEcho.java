package tools;

import gui.volumetab.VolumeTab;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ZeroEcho implements Runnable {

	public ArrayList<ImagePlus> echo0;
	private Volume vol;
	private VolumeTab parent;
	private int width, height;
	private int degree;
	private boolean takelog;
	private boolean canceld;
	private boolean failed;

	private int numbertasks;
	private int solvedtasks;
	private int value;

	private PropertyChangeListener progresslistener;

	public ZeroEcho(Volume vol, VolumeTab volumeTab, int fittingfunction,
			boolean log, PropertyChangeListener progress) {
		this.progresslistener = progress;
		this.vol = vol;
		this.degree = fittingfunction;
		parent = volumeTab;
		takelog = log;
	}

	public void Cancle() {
		canceld = true;
	}

	public void run() {
		canceld = false;
		int cores = Runtime.getRuntime().availableProcessors();
		if (cores < 1) {
			cores = 1;
		}

		Thread[] threads = new Thread[cores];
		echo0 = new ArrayList<ImagePlus>();

		int slice_perEcho = vol.size()
				/ Integer.parseInt(vol.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S,
						vol.size() - 1));
		numbertasks = slice_perEcho;

		ImagePlus data = vol.getData().get(0);

		width = data.getWidth();
		height = data.getHeight();

		for (int s = 0; s < slice_perEcho; s++) {
			BufferedImage nextimg = new BufferedImage(data.getWidth(),
					data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			ImagePlus imp = new ImagePlus();
			imp.setImage(nextimg);
			echo0.add(imp);
		}

		final int todo = slice_perEcho / threads.length;
		final int[] offset = new int[threads.length];
		for (int i = 0; i < threads.length; i++) {
			offset[i] = todo * i;
		}

		int max = width * height * slice_perEcho;

		System.out.println("Using " + threads.length
				+ " Threads for calculation...");

		double start = System.currentTimeMillis();
		for (int i = 0; i < threads.length; i++) {
			final int next = todo * i;
			if (i == threads.length - 1) {
				final int tocalc = todo + slice_perEcho - todo * threads.length;
				threads[i] = new Thread(new Runnable() {

					@Override
					public void run() {
						CalculateZeroEcho(tocalc, next);

					}
				});
			} else {
				threads[i] = new Thread(new Runnable() {

					@Override
					public void run() {
						CalculateZeroEcho(todo, next);

					}
				});
			}
			threads[i].start();
		}

		fitting: while (solvedtasks != numbertasks) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			int nextvalue = solvedtasks;
			if (progresslistener != null) {
				progresslistener.propertyChange(new PropertyChangeEvent(this,
						"ZeroEchoCalculation", value, nextvalue));
			}
			value = nextvalue;
			if (canceld || failed) {
				break fitting;
			}
		}
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (canceld) {
			parent.setZeroEcho(null, "failed");
			System.out.println("FITTING FAILED");
		} else {
			double neededtime = (System.currentTimeMillis() - start);
			System.out.println("Needed " + neededtime + " mill. to Perform "
					+ max + " fits.");
			System.out.println("(Performing "
					+ (int) (max / (neededtime / 1000)) + " fits per second)");
			parent.setZeroEcho(echo0, "fitting");
		}
	}

	private void CalculateZeroEcho(int todo, int offset) {
		try {
			VolumeFitter volfit = new VolumeFitter();

			for (int s = offset; s < offset + todo; s++) {
				if (canceld || failed) {
					break;
				}
				echo0.get(s).setProcessor(
						vol.getSlice(s).getData().getProcessor()
								.createProcessor(width, height));
				int rgbArray[] = volfit.getZeroValues(vol, s, degree, takelog, echo0.get(s).getProcessor());
				
//				BufferedImage next = echo0.get(s).getBufferedImage();
//				next.setRGB(0, 0, width, height, rgbArray, 0, width);
//				echo0.get(s).setImage(next);
				solvedtasks++;
			}
		} catch (Exception e) {
			failed = true;
		}

	}
}
