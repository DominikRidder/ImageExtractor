package gui.volumetab;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ij.gui.OvalRoi;
import ij.gui.Roi;
import imagehandling.KeyMap;
import imagehandling.Volume;

public class SphereRoi extends Roi implements Roi3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int z;

	public SphereRoi(double x, double y, int z, double radius) {
		super(x, y, z, 2 * radius);
		this.z = z;
	}

	@Override
	public int getZ() {
		return z;
	}

	public void draw(Volume vol, BufferedImage bigimg, int slice) {
		BufferedImage orig = vol.getSlice(0).getData().getBufferedImage();
		int thickness = Integer.parseInt(vol.getSlice(0).getAttribute(
				KeyMap.KEY_SLICE_THICKNESS));
		Rectangle rec = this.getBounds();
		Roi3D roi3 = (Roi3D) this;
		double radius = this.getBounds().getHeight() / 2;
		int z = roi3.getZ();
		
		double thisradius = Math.pow(443 / 2, 2) + Math.pow(443 / 2, 2);
		double otherradius = Math.pow(orig.getWidth() / 2, 2)
				+ Math.pow(orig.getHeight() / 2, 2);
		
		thickness *= thisradius/otherradius;
		
		if (Math.abs(z - slice) * thickness < radius) {
			double newr = Math.sqrt(Math.pow(radius, 2) - Math.pow(Math.abs(z - slice) * thickness, 2));
			OvalRoi next = new OvalRoi(rec.getX()+radius-newr, rec.getY()+radius-newr, newr * 2, newr * 2);
			next.draw(bigimg.getGraphics());
		} else {
			return;
		}
	}

}
