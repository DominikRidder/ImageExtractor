package gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

class DeleteAction extends AbstractAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JFileChooser chooser;

	public DeleteAction(JFileChooser chooser){
		super("Delete");
		this.chooser = chooser;
	}

	public void actionPerformed(ActionEvent evt) {
		File file = chooser.getSelectedFile();
		if (file != null){
			file.delete();
			chooser.rescanCurrentDirectory();
		}
		
	}
}
