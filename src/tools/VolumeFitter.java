package tools;

import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;
import functions.ExponentialFunction;
import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.Roi;
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

	public double getZeroValue(Volume vol, int x, int y, int slice, int degree,
			boolean logScale) {
		if (data == null) {
		String str_echo_numbers = vol.getSlice(vol.size() - 1)
				.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");
			echo_numbers = Integer.parseInt(str_echo_numbers);
			perEcho = vol.size() / echo_numbers;

			
			fitter = new Polyfitter();
			if (degree != -2) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
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
			buffimg.add(vol.getSlice(slice + perEcho * e).getData().getBufferedImage());
		}

		int iArray[] = new int[4];
		int itest[] = new int[4];
		for (int i = 0; i < buffimg.size(); i++) {
			BufferedImage img = buffimg.get(i);
			iArray = img.getRaster().getPixel(x, y, itest);
			if (logScale) {
				fitter.addPoint(i + 1, Math.log10(iArray[0]));
			} else {
				fitter.addPoint(i + 1, iArray[0]);
			}
		}

		fitter.fit();
		return fitter.getValue(new Point1D(0));
	}

	public BufferedImage getPlot(Volume vol, Roi relativroi, int slice, int degree,
			boolean logScale) {
		data = vol.getData();
		Roi roi = data.get(slice).getRoi();
		String str_echo_numbers = vol.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S, vol.size() - 1).replace(" ", "");

		echo_numbers = Integer.parseInt(str_echo_numbers);
		perEcho = vol.size() / echo_numbers;
		if (echo_numbers == 1){
			degree = 0;
		}

		// the programm for the fitting
		if (fitter == null) {
			fitter = new Polyfitter();
			if (degree != -2) {
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			} else {
				fitter.setAlgorithm(new LowestSquare(1));
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
			buffimg.add(data.get(slice + perEcho * e).getBufferedImage());
		}

		// int igetValue[] = new int[4];
		for (int i = 0; i < buffimg.size(); i++) {
			BufferedImage img = buffimg.get(i);
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
		return fitter.plotVolume(logScale);
	}

	public int getMin(BufferedImage img, Roi roi) {
		int igetValue[] = null;
		int values[] = null;
		int minvalue = 500;
		WritableRaster r = img.getRaster();
		Rectangle d = r.getBounds();
		for (int x = d.x; x < d.x + d.width; x++) {
			for (int y = d.y; y < d.y + d.height; y++) {
				if (roi.contains(x, y) && d.contains(x, y)) {
					values = r.getPixel(x, y, igetValue);
					if (values == null){
						continue;
					}
					int val = values[0];
					if (val < minvalue) {
						minvalue = val;
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
				nextmin = getMin(data.getBufferedImage(), data.getRoi());
				if (nextmin < minvalue) {
					minvalue = nextmin;
				}
			}
		}

		return minvalue;
	}

}
