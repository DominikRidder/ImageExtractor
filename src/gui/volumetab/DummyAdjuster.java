package gui.volumetab;

import java.awt.Adjustable;
import java.awt.event.AdjustmentListener;

/**
 * A class that can be used, to avoid nullpointer.
 * 
 * @author Dominik Ridder
 *
 */
public class DummyAdjuster implements Adjustable {

	/**
	 * Adjuster that can be used, if a Adjustable object is needed.
	 */
	public static DummyAdjuster dummy = new DummyAdjuster();

	@Override
	public int getOrientation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMinimum(int min) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMinimum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaximum(int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaximum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setUnitIncrement(int u) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getUnitIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBlockIncrement(int b) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getBlockIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setVisibleAmount(int v) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getVisibleAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setValue(int v) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addAdjustmentListener(AdjustmentListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAdjustmentListener(AdjustmentListener l) {
		// TODO Auto-generated method stub

	}

}
