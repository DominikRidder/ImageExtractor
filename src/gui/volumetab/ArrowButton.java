package gui.volumetab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * ArrowButton represents a JButton, that has an Arrow as an Image. Additionaly
 * this class contains a good solution, in case you need a Behaivior as long as
 * the Button is pressed, like a counter that is increased.
 * 
 * @author Dominik Ridder
 */
public class ArrowButton extends BasicArrowButton implements ActionListener {

	/**
	 * Default serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This int Field indicates, that the Arrow have an impact on the slice.
	 */
	public static final int SLICE_ARROW = 1;

	/**
	 * This int Field indicates, that the Arrow have an impact on the echo.
	 */
	public static final int ECHO_ARROW = 2;

	/**
	 * The change is the value, that is added repeadly to a value, while the
	 * Button is pressed.
	 */
	private int change;

	/**
	 * The Type of ArrowButton that is given (slice or echo changer).
	 */
	private int type;

	/**
	 * The VolumeTab, where the Value is changed later.
	 */
	private VolumeTab tocall;

	/**
	 * The frequenzy of value changing, that is processed, while the Button is
	 * pressed.
	 */
	private final int timerDelay = 100;

	/**
	 * Timer, that repeadly calls the method, that changes the needed value in a
	 * period of time.
	 */
	final Timer timer = new Timer(timerDelay, this);

	/**
	 * Constructor that creates a new ArrowButton with the given direction.
	 * 
	 * @param direction
	 */
	public ArrowButton(int direction) {
		super(direction);

		final ButtonModel bModel = this.getModel();

		bModel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent cEvt) {
				if (bModel.isPressed() && !timer.isRunning()) {
					timer.setInitialDelay(0);
					timer.start();
				} else if (!bModel.isPressed() && timer.isRunning()) {
					timer.stop();
				}
			}
		});
	}

	/**
	 * This Method sets the VolumeTab, that getting called, when this Button is
	 * pressed.
	 * 
	 * @param tab
	 *            The VolumeTab, that should be called.
	 */
	public void setToCall(VolumeTab tab) {
		tocall = tab;
	}

	/**
	 * Setting the Type of ButtonChange, that is needed (slice or echo).
	 * 
	 * @param i
	 *            The Integer, that indicates, whether this field is used for
	 *            the slice field or echo field.
	 */
	public void setType(int i) {
		type = i;
	}

	/**
	 * Setting the change that is added to the current FieldValue, while the
	 * Button is pressed.
	 * 
	 * @param change
	 *            The change, that is added to the actual value.
	 */
	public void setChange(int change) {
		this.change = change;
	}

	/**
	 * Performing the onclick action. This Method changing the slice or echo
	 * field of the Textfield.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.type == SLICE_ARROW) {
			tocall.addtoSlice(change);
		} else if (this.type == ECHO_ARROW) {
			tocall.addtoEcho(change);
		}
	}

}
