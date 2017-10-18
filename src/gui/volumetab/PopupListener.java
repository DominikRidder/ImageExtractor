package gui.volumetab;

import gui.GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * @author Dominik Ridder
 *
 */
class PopupListener extends MouseAdapter {
	JPopupMenu popup;

	PopupListener(JPopupMenu popupMenu) {
		popup = popupMenu;
	}

	public void mousePressed(MouseEvent e) {
		if (!popup.isVisible()) {
			if (e.getButton() == GUI.RIGHT_CLICK) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {
			popup.setVisible(false);
		}
	}

	public void mouseReleased(MouseEvent e) {

	}
}
