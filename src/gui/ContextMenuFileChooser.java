package gui;

import java.awt.Component;

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
		
		JMenuItem menuItem = new JMenuItem("Delete Folder");
		menuItem.addActionListener(new DeleteAction(this));

		for (Component c : this.getComponents()){
			if (c instanceof FilePane){
				((FilePane)c).getComponentPopupMenu().add(menuItem);
			}
		}
	}


}
