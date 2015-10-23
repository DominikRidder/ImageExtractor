package tools;

import ij.ImagePlus;
import ij.gui.PointRoi;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ZeroEcho implements Runnable{

	public boolean isrunning = true;
	public ArrayList<BufferedImage> echo0;
	private Volume vol;
	
	public ZeroEcho(Volume vol){
		this.vol = vol;
	}
	
	public void run() {
//		if (!isrunning){
//			isrunning = true;
			CalculateZeroEcho();
			isrunning = false;
//		}
	}
	
	private void CalculateZeroEcho(){
		echo0 = new ArrayList<BufferedImage>();
		
		VolumeFitter volfit = new VolumeFitter();
		
		ArrayList<ImagePlus> data = vol.getData();
		PointRoi roi = new PointRoi(0,0);
		
		int slice_perEcho = Integer.parseInt(vol.getAttribute(KeyMap.KEY_ECHO_NUMBERS_S, vol.size()-1));
		
		echo0 = new ArrayList<BufferedImage>(slice_perEcho);
		int counter = 0,max;
		int last = -1;
		double values[][][] = new double[slice_perEcho][data.get(0).getWidth()][data.get(0).getHeight()];
		
//		max = data.get(0).getWidth()*data.get(0).getHeight()*slice_perEcho;
		max = data.get(0).getWidth()*data.get(0).getHeight();
		for (int s = 0; s<1; s++){
			echo0.add(new BufferedImage(data.get(15).getWidth(),data.get(15).getHeight(),BufferedImage.TYPE_BYTE_GRAY));
			for (int x=0; x<data.get(s).getWidth(); x++){
				for (int y=0; y<data.get(s).getHeight(); y++){
					float next = (((float)(++counter)/max)*100);
					if (next > last){
						System.out.println(++last+"%");
					}
//					System.out.println(counter+"/"+max);
//					roi = new PointRoi(x,y);
					echo0.get(0).setRGB(x, y, (int)volfit.getZeroValue(vol, x,y, 15, 1, false));
//					values[s][x][y] = (int)volfit.getZeroValue(vol, x,y, s, 1, false);
				}
			}
		}
		System.out.println("finished");
		
	}
}
