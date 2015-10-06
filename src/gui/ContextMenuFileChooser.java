package gui;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sun.swing.FilePane;

public class ContextMenuFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPopupMenu popup;

	public ContextMenuFileChooser() {
		super();
	}

	public int showOpenDialog(Component parent) throws HeadlessException {
		JDialog d = createDialog(parent);

		// FI Remove when we get ancestor property
		d.setTitle("Open");
		setDialogType(OPEN_DIALOG);

		JMenuItem menuItem = new JMenuItem("Delete Folder");
		menuItem.addActionListener(new DeleteAction(this));

		FilePane output = (FilePane) this.getComponent(2);
		output.getComponentPopupMenu().add(menuItem);

		d.setVisible(true);
		return ERROR_OPTION;
	}

}
