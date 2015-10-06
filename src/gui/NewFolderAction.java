package gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

public class NewFolderAction extends AbstractAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JFileChooser chooser;
	
	public NewFolderAction(JFileChooser chooser){
		super("New Folder");
		this.chooser = chooser;
	}

	public void actionPerformed(ActionEvent e) {
		File cwd = chooser.getCurrentDirectory();
		if (cwd != null){
			File new_dir = new File(cwd, "New Folder");
			new_dir.mkdir();
			chooser.rescanCurrentDirectory();
		}
		
	}
	
}
