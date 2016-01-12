package gui.volumetab;

import imagehandling.Volume;

import java.awt.image.BufferedImage;

public interface Roi3D {
	public int getZ();

	public void draw(Volume vol, BufferedImage img, int slice, double scaling);
}
