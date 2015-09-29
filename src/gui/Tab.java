package gui;

/**
 * This is used by the inner classes of GUI (VolumeTab and SorterTab).
 * 
 * @author dridder_local
 *
 */
interface MyTab {
	public String getClassName();

	public void lifeUpdate();
}