package gui.volumetab;

import imagehandling.Volume;

import java.awt.image.BufferedImage;

/**
 * This interface is used to define 3D roi's, because these have be handled
 * diffently.
 * 
 * @author Dominik Ridder
 *
 */
public interface Roi3D {
	/**
	 * Getter for the z coodinate of the roi.
	 * 
	 * @return The z coodinate
	 */
	public int getZ();

	/**
	 * Drawing the intersection of the roi and the slice on the Image.
	 * 
	 * @param vol
	 *            The Volume, that contains the roi
	 * @param img
	 *            The Image, where the roi should be drawn at
	 * @param slice
	 *            The Slice of the Image
	 * @param scaling
	 *            The Scaling that should be used between the Volume and the
	 *            Image
	 */
	public void draw(Volume vol, BufferedImage img, int slice, double scaling);
}
