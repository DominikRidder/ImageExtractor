package gui.volumetab;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ij.gui.OvalRoi;
import ij.gui.Roi;
import imagehandling.Volume;

public class SphereRoi extends Roi implements Roi3D{
	
	private int z;
	
	public SphereRoi(int x, int y, int z, double radius){
		super(x,y,z,radius);
		this.z = z;
	}

	@Override
	public int getZ() {
		return z;
	}
	
	public void draw(Volume vol,BufferedImage img, int slice){
		BufferedImage orig = vol.getSlice(0).getData().getBufferedImage();
		Roi roi = vol.getSlice(slice).getRoi();
		Rectangle rect = roi.getBounds();
		Roi todraw = new OvalRoi(rect.x*img.getWidth()/orig.getWidth(), rect.y*img.getHeight()/orig.getHeight(), roi.getFloatHeight(),roi.getFloatHeight());
		todraw.draw(img.getGraphics());
	}
	
	

}
