package tools;

import fitterAlgorithm.LRDecomposition;
import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;
import functions.ExponentialFunction;
import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagehandling.Image;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import polyfitter.Point1D;
import polyfitter.Polyfitter;

public class VolumeFitter {

	private Polyfitter fitter;

	private ArrayList<ImagePlus> data;

	private int echo_numbers, perEcho;

	private int lastdegree;

	private boolean useproccessor;

	public double getZeroValue(Volume vol, int x, int y, int slice, int degree,
			boolean logScale) {
		if (fitter == null) {
			useproccessor = false;
			String str_echo_numbers = vol.getSlice(vol.size() - 1)
					.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");
			echo_numbers = Integer.parseInt(str_echo_numbers);
			perEcho = vol.size() / echo_numbers;

			fitter = new Polyfitter();
			if (degree != -2) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
//				 fitter.setAlgorithm(new LRDecomposition(degree));
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;

		} else if (lastdegree != degree) {
			fitter.removeAlgorithm();
			if (degree > -1) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			} else if (degree == -1) {
				fitter.setAlgorithm(new PolynomialLowestSquare(echo_numbers - 1));
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;
		}
		fitter.removePoints();

		ArrayList<BufferedImage> buffimg = new ArrayList<BufferedImage>(
				echo_numbers);

		for (int e = 0; e < echo_numbers; e++) {
			buffimg.add(vol.getSlice(slice + perEcho * e).getData()
					.getBufferedImage());
		}

		int iArray;
		if (buffimg.size() != 1) {
			for (int i = 0; i < buffimg.size(); i++) {
				iArray = getMin(vol.getData().get(slice + perEcho * i),
						new PointRoi(x, y));
				if (logScale) {
					fitter.addPoint(i + 1, Math.log10(iArray));
				} else {
					fitter.addPoint(i + 1, iArray);
				}
			}
		} else {
			iArray = getMin(vol.getData().get(slice), new PointRoi(x, y));
			if (logScale) {
				return Math.log10(iArray);
			} else {
				return iArray;
			}
		}

		fitter.fit();
		return fitter.getValue(new Point1D(0));
	}

	public BufferedImage getPlot(Volume vol, Roi relativroi, int slice,
			int degree, boolean logScale, int wwidth, int wheight) {
		data = vol.getData();

		Roi roi = data.get(slice).getRoi();
		String str_echo_numbers = vol.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S,
				vol.size() - 1).replace(" ", "");

		echo_numbers = Integer.parseInt(str_echo_numbers);
		perEcho = vol.size() / echo_numbers;
		if (echo_numbers == 1) {
			degree = 0;
		}

		// the programm for the fitting
		if (fitter == null) {
			useproccessor = true;
			fitter = new Polyfitter();
			if (degree != -2) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;

		} else if (lastdegree != degree) {
			fitter.removeAlgorithm();
			if (degree > -1) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			} else if (degree == -1) {
				fitter.setAlgorithm(new PolynomialLowestSquare(echo_numbers - 1));
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;
		}

		fitter.removePoints();

		ArrayList<ImagePlus> buffimg = new ArrayList<ImagePlus>(echo_numbers);

		for (int e = 0; e < echo_numbers; e++) {
			buffimg.add(data.get(slice + perEcho * e));
		}

		// int igetValue[] = new int[4];
		for (int i = 0; i < buffimg.size(); i++) {
			ImagePlus img = buffimg.get(i);
			int val = 0;

			if (relativroi instanceof Roi3D) {
				val = getMin(vol, i);
			} else {
				val = getMin(img, roi);
			}

			if (logScale) {
				fitter.addPoint(i + 1, Math.log10(val));
			} else {
				fitter.addPoint(i + 1, val);
			}
		}
		return fitter.plotVolume(logScale, wwidth, wheight);
	}

	public int getMin(ImagePlus imp, Roi roi) {
		int value = 0;
		int minvalue = Integer.MAX_VALUE;

		Rectangle d = new Rectangle(0, 0, imp.getWidth(), imp.getHeight());
		Rectangle roib = roi.getBounds();
		ImageProcessor ip = imp.getProcessor();
		BufferedImage img = imp.getBufferedImage();

		for (int x = roib.x; x < roib.x + roib.getWidth() + 1; x++) {
			for (int y = roib.y; y < roib.y + roib.getHeight() + 1; y++) {
				if (roi.contains(x, y) && d.contains(x, y)) {
					if (useproccessor) {
						value = ip.getPixel(x, y);
					} else {
						value = img.getRGB(x, y);
					}
					if (value < minvalue) {
						minvalue = value;
					}
				}
			}
		}

		return minvalue;
	}

	public int getMin(Volume vol, int echon) {
		int minvalue = 500;
		int nextmin = 0;
		Image img = null;
		ImagePlus data = null;

		for (int i = echon * perEcho; i < (echon + 1) * perEcho; i++) {
			img = vol.getSlice(i);
			data = img.getData();
			if (data.getRoi() != null) {
				nextmin = getMin(data, data.getRoi());
				if (nextmin < minvalue) {
					minvalue = nextmin;
				}
			}
		}

		return minvalue;
	}

}
