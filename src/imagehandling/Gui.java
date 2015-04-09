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
	
	JPanel img;
	
	JPanel att;

	Volume vol;

	JTextField path;

	JFileChooser chooser;

	JTextArea output;

	JTextField current_path;
	
	JTextField index;
	
	JTextField filter;

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
		current_path.setMaximumSize(new Dimension(1000, 10000));
		JScrollPane scroll = new JScrollPane(output);
		scroll.setPreferredSize(new Dimension(100, 100));
		index = new JTextField();
		index.setText("0");
		filter = new JTextField();
		filter.setText("");
		
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
		att = new JPanel();
		att.setLayout(new GridLayout(1,2,20,1));
		img = new JPanel();
		img.setLayout(new GridLayout(1,2,20,1));

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(path);
		panel.add(dir);
		img.add(current_path);
		img.add(index);
		panel.add(img);
		att.add(show_attributes);
		att.add(filter);
		panel.add(att);
		panel.add(scroll);
		dir.add(search_path);
		dir.add(apply_path);
		dir.setMaximumSize(new Dimension(500, 1000));
		
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
			if (filter.getText().equals("")){
			output.setText(vol.getSlice(Integer.parseInt(index.getText())).getHeader());
			}else{
				output.setText(vol.getSlice(Integer.parseInt(index.getText())).getAttribute(Image.getKeyWords("*"+filter.getText()+"*")));
			}
			break;
		default:
			break;
		}
	}

	public static void main(String[] agrs) {
		new Gui();
	}

}
