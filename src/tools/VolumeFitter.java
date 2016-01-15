package tools;

import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;
import functions.ExponentialFunction;
import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import polyfitter.Polyfitter;

public class VolumeFitter {

	private Polyfitter fitter;

	private ArrayList<ImagePlus> data;

	private int echo_numbers, perEcho;

	private int lastdegree;

	private boolean useproccessor;

	public int[] getZeroValues(Volume vol, int slice, int degree,
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
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;

		} else if (lastdegree != degree) {
			if (degree != -2) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
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
		int width = buffimg.get(0).getWidth();
		ImageProcessor ip = vol.getSlice(slice).getData().getProcessor();
		int rgb[] = new int[width * buffimg.get(0).getHeight()];
		for (int x = 0; x < buffimg.get(0).getWidth(); x++) {
			for (int y = 0; y < buffimg.get(0).getHeight(); y++) {
				float pointcloud[][] = new float[echo_numbers][3];
				if (buffimg.size() != 1) {
					for (int i = 0; i < buffimg.size(); i++) {
						// iArray = buffimg.get(i).getRGB(x, y);
						// iArray = getMin(data, data.getRoi());
						iArray = ip.get(x, y);
						if (logScale) {
							pointcloud[i][0] = i + 1;
							pointcloud[i][1] = (float) Math.log10(iArray);
						} else {
							pointcloud[i][0] = i + 1;
							pointcloud[i][1] = iArray;
						}
					}
					fitter.setPoints(pointcloud);
					fitter.fit();
					fitter.removeBadPoints();
					fitter.fit();
					rgb[x + y * width] = (int) fitter.getValue(0);
				} else {
					iArray = getMin(vol.getData().get(slice),
							new PointRoi(x, y));
					if (logScale) {
						rgb[x + y * width] = (int) Math.log10(iArray);
					} else {
						rgb[x + y * width] = iArray;
					}
				}
			}
		}
		return rgb;
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
				fitter.addPoint(i + 1, Math.log10(val), 0);
			} else {
				fitter.addPoint(i + 1, val, 0);
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
		ImagePlus data = null;

		for (int i = echon * perEcho; i < (echon + 1) * perEcho; i++) {
			data = vol.getSlice(i).getData();
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
