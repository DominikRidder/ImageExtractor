package gui.volumetab;

import ij.gui.OvalRoi;
import ij.gui.Roi;
import imagehandling.Volume;
import imagehandling.headerhandling.KeyMap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

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

	public void draw(Volume vol, BufferedImage bigimg, int slice, double scaling) {
		if (this.getProperty("unit").equals("mm")) {
			double thickness = 1;
			try {
				thickness = Double.parseDouble(vol.getSlice(0).getAttribute(
						KeyMap.KEY_SLICE_THICKNESS));
			} catch (Exception e) {
				System.out
						.println("Found no thickness parameter. Setting thickness to 1.");
			}
			Rectangle rec = this.getBounds();
			Roi3D roi3 = (Roi3D) this;
			double radius = this.getBounds().getHeight() / 2;
			int z = roi3.getZ();

			thickness *= scaling;

			if (Math.abs(z - slice) * thickness < radius) {
				double newr = Math.sqrt(Math.pow(radius, 2)
						- Math.pow(Math.abs(z - slice) * thickness, 2));
				OvalRoi next = new OvalRoi(rec.getX() + radius - newr,
						rec.getY() + radius - newr, newr * 2, newr * 2);
				next.draw(bigimg.getGraphics());
			} else {
				return;
			}
		} else {
			Rectangle rec = this.getBounds();
			Roi3D roi3 = (Roi3D) this;
			double radius = this.getBounds().getHeight() / 2;
			int z = roi3.getZ();

			if (Math.abs(z - slice) < radius) {
				double newr = Math.sqrt(Math.pow(radius, 2)
						- Math.pow(Math.abs(z - slice), 2));
				OvalRoi next = new OvalRoi(rec.getX() + radius - newr,
						rec.getY() + radius - newr, newr * 2, newr * 2);
				next.draw(bigimg.getGraphics());
			} else {
				System.out.println("nope");
				return;
			}
		}
	}

}
