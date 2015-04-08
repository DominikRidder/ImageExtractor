package imagehandling;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	JPanel panel;

	JPanel dir;

	Volume vol;

	JTextField path;

	JFileChooser chooser;

	JTextArea output;

	JTextField current_path;

	public Gui() {
		setSize(500, 400);
		path = new JTextField();
//		path.setText("/path/to/volume");
		path.setMaximumSize(new Dimension(10000, 100));
		path.setText("/opt/dridder_local/TestDicoms/Testfolder");
		output = new JTextArea();
		output.setText("status");
		output.setMinimumSize(new Dimension(100, 1000));
		output.setEditable(false);
		current_path = new JTextField();
		current_path.setText("Volume: <<not set>>");
		current_path.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		scroll.setPreferredSize(new Dimension(100, 100));
		
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Search Path of Volume");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		JButton apply_path = new JButton();
		JButton search_path = new JButton();
		JButton show_attributes = new JButton();
		apply_path.setText("create Volume");
		search_path.setText("search");
		show_attributes.setText("Display Attributes");
		apply_path.addActionListener(this);
		search_path.addActionListener(this);
		show_attributes.addActionListener(this);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		dir = new JPanel();
		dir.setLayout(new GridLayout(1, 2, 20, 1));

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(path);
		panel.add(dir);
		panel.add(current_path);
		panel.add(show_attributes);
		panel.add(scroll);
		dir.add(search_path);
		dir.add(apply_path);
		
		getContentPane().add(panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ImageExtractor");
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "create Volume":
			try {
				vol = new Volume(path.getText(), this);
				output.setText("Volume created");
				current_path.setText("Volume: " + path.getText());
			} catch (RuntimeException ert) {
				output.setText(ert.getMessage());
			}
			break;
		case "search":
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				path.setText(chooser.getSelectedFile().toString());
			}
			break;
		case "Display Attributes":
			output.setText(vol.getHeader().get(0));
			break;
		default:
			break;
		}
	}

	public static void main(String[] agrs) {
		new Gui();
	}

}
