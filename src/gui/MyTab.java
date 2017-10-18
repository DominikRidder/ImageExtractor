package gui;

/**
 * This is used by the inner classes of GUI (VolumeTab and SorterTab).
 * 
 * @author Dominik Ridder
 *
 */
public interface MyTab {

	/**
	 * This Method should be called, when a Tab gains the Focus in the Tabbar.
	 */
	public void onFocus();

	/**
	 * This Method should be called, when a Tab lose the Focus in the Tabbar.
	 */
	public void onExit();
}