package gui;

/**
 * This is used by the inner classes of GUI (VolumeTab and SorterTab).
 * 
 * @author dridder_local
 *
 */
public interface MyTab {
	public String getClassName();

	public void lifeUpdate();
	
	public int preferedWidth();
}