package util;

import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;
import functions.ExponentialFunction;
import gui.GUI;
import gui.volumetab.Roi3D;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import imagehandling.Volume;
import imagehandling.headerhandling.KeyMap;

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
			boolean logScale, ImageProcessor processor) {
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

		ArrayList<ImagePlus> buffimg = new ArrayList<ImagePlus>(echo_numbers);

		for (int e = 0; e < echo_numbers; e++) {
			buffimg.add(vol.getSlice(slice + perEcho * e).getData());
		}

		float iArray;
		int width = buffimg.get(0).getWidth();
		int height = buffimg.get(0).getHeight();
		int rgb[] = new int[width * height];

		ImageProcessor ip[] = new ImageProcessor[echo_numbers];
		for (int i = 0; i<echo_numbers; i++){
			ip[i] = buffimg.get(i).getProcessor();
		}

		int backgroundmax = 50;

		float pointcloud[][] = new float[echo_numbers][3];
		for (int i = 0; i < buffimg.size(); i++) {
			iArray = ip[i].getf(0, 0);
			if (logScale) {
				pointcloud[i][0] = i + 1;
				pointcloud[i][1] = (float) Math.log1p(iArray);
			} else {
				pointcloud[i][0] = i + 1;
				pointcloud[i][1] = iArray;
			}
		}

		fitter.setPoints(pointcloud);
		fitter.fit();
		fitter.removeBadPoints();
		fitter.fit();
		float nullrgb = (float) fitter.getValue(0);
		
		if (GUI.DEBUG){
			nullrgb = 3000;
		}

		boolean[][] background = new boolean[buffimg.get(0).getWidth()][ buffimg.get(0).getHeight()];
		
		for (int x = 0; x < buffimg.get(0).getWidth(); x++) {
			Yloop: for (int y = 0; y < buffimg.get(0).getHeight()/2; y++) {
					pointcloud = new float[echo_numbers][3];
					boolean possback = true;
					for (int i = 0; i < buffimg.size(); i++) {
						iArray = ip[i].getf(x, y);

						if (iArray >= backgroundmax){
							possback = false;
						}
						if (possback && i == buffimg.size()-1 && ((x>0? background[x-1][y] : true) || (y>0? background[x][y-1] : true))) {
							processor.setf(x,y,nullrgb);
							background[x][y] = true;
							continue Yloop;
						}
						
						pointcloud[i][0] = i + 1;
						if (logScale) {
							pointcloud[i][1] = (float) Math.log1p(iArray);
						} else {
							pointcloud[i][1] = iArray;
						}
					}
					
					fitter.setPoints(pointcloud);
					fitter.fit();
					fitter.removeBadPoints();
					fitter.fit();

					processor.setf(x,y,(float)fitter.getValue(0));
			}
		}
		
		for (int x = 0; x < buffimg.get(0).getWidth(); x++) {
			Yloop: for (int y = buffimg.get(0).getHeight()-1; y >= buffimg.get(0).getHeight()/2; y--) {
					pointcloud = new float[echo_numbers][3];
					boolean possback = true;
					for (int i = 0; i < buffimg.size(); i++) {
						iArray = ip[i].getf(x, y);

						if (iArray >= backgroundmax){
							possback = false;
						}
						if (possback && i == buffimg.size()-1 && ((x>0? background[x-1][y] : true) || (y !=  buffimg.get(0).getHeight()-1? background[x][y+1] : true))) {
							processor.setf(x,y,nullrgb);
							background[x][y] = true;
							continue Yloop;
						}
						
						pointcloud[i][0] = i + 1;
						if (logScale) {
							pointcloud[i][1] = (float) Math.log1p(iArray);
						} else {
							pointcloud[i][1] = iArray;
						}
					}
					
					fitter.setPoints(pointcloud);
					fitter.fit();
					fitter.removeBadPoints();
					fitter.fit();

					processor.setf(x,y,(float)fitter.getValue(0));
			}
		}
		return rgb;
	}

	public BufferedImage getPlot(Volume vol, Roi relativroi, int slice,
			int degree, boolean logScale, int wwidth, int wheight, int echonr) {
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
				fitter.addPoint(i + 1, Math.log1p(val), 0);
			} else {
				fitter.addPoint(i + 1, val, 0);
			}
		}
		return fitter.plotVolume(logScale, wwidth, wheight, echonr);
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
