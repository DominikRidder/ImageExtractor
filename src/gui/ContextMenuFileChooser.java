package gui;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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

//		for (Component c : this.getComponents()){
////			if (c instanceof FilePane){
////				((FilePane)c).getComponentPopupMenu().add(menuItem);
////			}
//		}
	}


}
