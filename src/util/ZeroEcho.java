package util;

import gui.volumetab.VolumeTab;
import ij.ImagePlus;
import imagehandling.Volume;
import imagehandling.headerhandling.KeyMap;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This class calculates to a given Volume the estimated Zero Echo.
 * 
 * @author Dominik Ridder
 *
 */
public class ZeroEcho implements Runnable {

	/**
	 * This ArrayList contains the slices of the Zero Echo.
	 */
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

	/**
	 * This Method calculates to the given parameter a Zero Echo.
	 * 
	 * @param vol
	 *            The Volume, that contains the other Echos
	 * @param volumeTab
	 *            The VolumeTab, that calls this function (this is used to
	 *            return the ZeroEcho)
	 * @param fittingfunction
	 *            The Fittingfunction, that sshould be used
	 * @param log
	 *            If true, than the values of the Image taken logarithmic
	 * @param progress
	 *            The Listener, to display the Progress
	 */
	public ZeroEcho(Volume vol, VolumeTab volumeTab, int fittingfunction,
			boolean log, PropertyChangeListener progress) {
		this.progresslistener = progress;
		this.vol = vol;
		this.degree = fittingfunction;
		parent = volumeTab;
		takelog = log;
	}

	/**
	 * Method to cancle the Algorithmen (thread save).
	 */
	public void Cancel() {
		canceld = true;
	}

	/**
	 * Starts the calculation of the ZeroEcho. Called by the thread method
	 * start.
	 */
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

	/**
	 * Privat method, that is used to splitt the Task into smaller task. This is
	 * used for multi threading.
	 * 
	 * @param todo
	 *            The amount of slices, that should be calculated.
	 * @param offset
	 *            The start of the first slice, that should be calculated.
	 */
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
				volfit.getZeroValues(vol, s, degree, takelog, echo0.get(s)
						.getProcessor());

				// BufferedImage next = echo0.get(s).getBufferedImage();
				// next.setRGB(0, 0, width, height, rgbArray, 0, width);
				// echo0.get(s).setImage(next);
				solvedtasks++;
			}
		} catch (Exception e) {
			failed = true;
		}

	}
}
