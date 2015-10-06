package gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import sun.swing.FilePane;

public class ContextMenuFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPopupMenu popup;

	public ContextMenuFileChooser() {
		super();
		
		JMenuItem menuItem = new JMenuItem("Delete Folder");
		menuItem.addActionListener(new DeleteAction(this));

		FilePane output = (FilePane) this.getComponent(2);
		output.getComponentPopupMenu().add(menuItem);
	}


}
