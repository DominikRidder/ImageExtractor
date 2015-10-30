package tools;

import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;
import functions.ExponentialFunction;
import ij.ImagePlus;
import ij.gui.Roi;
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

public class VolumeFitter{

	private Polyfitter fitter;
	
	private ArrayList<ImagePlus> data;
	
	private int width,height,echo_numbers,perEcho;
	
	private int lastdegree;
	
	public double getZeroValue(Volume vol, int x, int y, int slice, int degree, boolean logScale){
		data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");

		echo_numbers = Integer.parseInt(str_echo_numbers);
		perEcho = vol.size() / echo_numbers;

		// the programm for the fitting
		if (fitter == null){
			fitter = new Polyfitter();
			if (degree != -2){
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			}else{
				fitter.setAlgorithm(new LowestSquare(1));
			}
			lastdegree = degree;

		}else if(lastdegree != degree){
			fitter.removeAlgorithm();
			if (degree > -1){
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			}else if (degree == -1){
				fitter.setAlgorithm(new PolynomialLowestSquare(echo_numbers-1));
			}else{
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;
		}
		fitter.removePoints();
		
		// Just the window, to display the fit
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		ArrayList<BufferedImage>buffimg = new ArrayList<BufferedImage>(echo_numbers);
		
		for (int e=0; e<echo_numbers; e++){
			buffimg.add(data.get(slice+perEcho*e).getBufferedImage());
		}
		
		int iArray[] = new int[4];
		int itest[] = new int[100];
		for (int i=0; i<buffimg.size(); i++){
			BufferedImage img = buffimg.get(i);
			iArray = img.getRaster().getPixel( x, y, itest);
			if (logScale){
				fitter.addPoint(i+1, Math.log10(iArray[0]));
			}else{
				fitter.addPoint(i+1, iArray[0]);
			}
		}
		
		fitter.fit();
		return fitter.getValue(new Point1D(0));
	}
	
	public BufferedImage getPlot(Volume vol, Roi roi, int slice, int degree, boolean logScale){
		data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");

		echo_numbers = Integer.parseInt(str_echo_numbers);
		perEcho = vol.size() / echo_numbers;

		// the programm for the fitting
		if (fitter == null){
			fitter = new Polyfitter();
			if (degree != -2){
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			}else{
				fitter.setAlgorithm(new LowestSquare(1));
			}
			lastdegree = degree;

		}else if(lastdegree != degree){
			fitter.removeAlgorithm();
			if (degree > -1){
				fitter.setAlgorithm(new PolynomialLowestSquare(degree));
			}else if (degree == -1){
				fitter.setAlgorithm(new PolynomialLowestSquare(echo_numbers-1));
			}else{
				fitter.setAlgorithm(new LowestSquare(1));
				fitter.setFunction(new ExponentialFunction());
			}
			lastdegree = degree;
		}
		fitter.removePoints();
		
		ArrayList<BufferedImage>buffimg = new ArrayList<BufferedImage>(echo_numbers);
		
		for (int e=0; e<echo_numbers; e++){
			buffimg.add(data.get(slice+perEcho*e).getBufferedImage());
		}
		
//		int igetValue[] = new int[4];
		for (int i=0; i<buffimg.size(); i++){
			BufferedImage img = buffimg.get(i);
			int val = getMin(img,roi);
//			int val = img.getRaster().getPixel((int)roi.getXBase(), (int)roi.getXBase(), igetValue)[0];
			if (logScale){
				fitter.addPoint(i+1, Math.log10(val));
			}else{
				fitter.addPoint(i+1, val);
			}
		}
		
		return fitter.plotVolume(logScale);
	}
	
	public int getMin(BufferedImage img, Roi roi){
		int igetValue[] = new int[4];
		int minvalue = 500;
		WritableRaster r = img.getRaster();
		Rectangle d = roi.getBounds();
		for (int x=d.x; x<d.x+d.width; x++){
			for (int y=d.y; y<d.y+d.height; y++){
				if (roi.contains(x, y)){
					int val = r.getPixel(x, y, igetValue)[0];
					if (val < minvalue){
						minvalue = val;
					}
				}
			}
		}
		return minvalue;
	}

}
