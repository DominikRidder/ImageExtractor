package tools;

import fitterAlgorithm.LRDecomposition;
import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;
import functions.ExponentialFunction;
import ij.ImagePlus;
import ij.gui.Roi;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;

import polyfitter.Point;
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
			iArray = img.getRaster().getPixel((int) roi.getBounds().getX(), (int) roi.getBounds().getY(), itest);
			if (logScale){
				fitter.addPoint(i+1, Math.log10(iArray[0]));
			}else{
				fitter.addPoint(i+1, iArray[0]);
			}
		}
		
		return fitter.plotVolume(logScale);
	}

}
