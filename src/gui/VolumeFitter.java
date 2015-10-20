package gui;

import fitterAlgorithm.LRDecomposition;
import fitterAlgorithm.PolynomialLowestSquare;
import ij.ImagePlus;
import ij.gui.Roi;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;

import polyfitter.Point;
import polyfitter.Polyfitter;

public class VolumeFitter{

	private Polyfitter fitter;
	
	private ArrayList<ImagePlus> data;
	
	int width,height,echo_numbers,perEcho;
	
	public BufferedImage getPlot(Volume vol, Roi roi, int slice, boolean alsolog){
		data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");

		echo_numbers = Integer.parseInt(str_echo_numbers);
		perEcho = vol.size() / echo_numbers;

		// the programm for the fitting
		if (fitter == null){
			fitter = new Polyfitter();
//			fitter.setAlgorithm(new LRDecomposition(echo_numbers-1));
			fitter.setAlgorithm(new LRDecomposition(2));
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
			fitter.addPoint(i, iArray[0], Math.log10(iArray[0])+10);
		}
		
		return fitter.plotVolume(alsolog);
	}

}
