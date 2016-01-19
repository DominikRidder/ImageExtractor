package gui.volumetab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

public class ArrowButton extends BasicArrowButton implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int SLICE_ARROW = 1, ECHO_ARROW = 2;
	private int change, type;
	private VolumeTab tocall;
	
	private final int timerDelay = 100;
	final Timer timer = new Timer(timerDelay, this);

	public ArrowButton(int direction) {
		super(direction);

		final ButtonModel bModel = this.getModel();

		bModel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent cEvt) {
				if (bModel.isPressed() && !timer.isRunning()) {
					timer.start();
				} else if (!bModel.isPressed() && timer.isRunning()) {
					timer.stop();
				}
			}
		});
	}

	public void setToCall(VolumeTab tab) {
		tocall = tab;
	}

	public void setType(int i) {
		type = i;
	}

	public void setChange(int change) {
		this.change = change;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.type == SLICE_ARROW) {
			tocall.addtoSlice(change);
		} else if (this.type == ECHO_ARROW) {
			tocall.addtoEcho(change);
		}
	}

}
