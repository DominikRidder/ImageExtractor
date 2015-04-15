package imagehandling;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

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

	JTextField text_slice;

	JTextField index;

	JTextField filter;

	int change = 0;
	
	public Gui() {
		setSize(500, 400);
		path = new JTextField();
		current_path = new JTextField();
		path.setMaximumSize(new Dimension(10000, 100));
		path.setText("/opt/dridder_local/TestDicoms/Testfolder");
		output = new JTextArea();
		output.setText("status");
		output.setMinimumSize(new Dimension(100, 1000));
		output.setEditable(false);
		current_path
				.setText("Volume: <<not set>>                               ");
		current_path.setEditable(false);
		current_path.setMaximumSize(new Dimension(10000, 10000));
		JScrollPane scroll = new JScrollPane(output);
		scroll.setPreferredSize(new Dimension(100, 100));
		index = new JTextField();
		index.setText("0");
		index.setMinimumSize(new Dimension(100, 100));
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
		att = new JPanel();
		img = new JPanel();
		dir.setLayout(new GridLayout(1, 2, 20, 1));
		att.setLayout(new GridLayout(1, 2, 20, 1));
		img.setLayout(new BoxLayout(img, BoxLayout.LINE_AXIS));
		att.setMaximumSize(new Dimension(10000, 1000));
		img.setMaximumSize(new Dimension(10000, 1000));

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(path);
		panel.add(dir);
		img.add(current_path);
		img.add(Box.createRigidArea(new Dimension(10, 0)));
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
		JPanel contentPane = (JPanel) this.getContentPane();
		int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = contentPane.getInputMap(condition);
		ActionMap actionMap = contentPane.getActionMap();

		String up = "left";
		String down = "right";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), up);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), down);
		actionMap.put(up, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!index.getText().equals("")) {
					change = 1;
				}
			}
		});
		actionMap.put(down, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!index.getText().equals("0") && !index.getText().equals("")) {
					change = -1;
				}
			}
		});
		setVisible(true);

		String numb = index.getText();
		String filt = filter.getText();
		while (true) {
			if (!index.getText().equals("")) {
				try {
					if (Integer.parseInt(index.getText()) >= vol.size()) {
						if (! (Integer.parseInt(index.getText())/1000 > 0)){
						index.setText("29");
						}
					}
					if (!numb.equals(index.getText())) {
						numb = index.getText();
						displayAttributes();
					}
				} catch (NumberFormatException | NullPointerException e) {
					if (index.getText().equals(numb)) {
						numb = "0";
					}
					index.setText(numb);
				}
			}
			if (!filter.getText().equals("")) {
				if (!filt.equals(filter.getText())) {
					numb = index.getText();
					displayAttributes();
				}
			}
			if (change != 0){
				index.setText(""+(Integer.parseInt(index.getText())+change));
				change = 0;
			}
		}
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
			displayAttributes();
			break;
		default:
			break;
		}
	}

	public void displayAttributes() {
		try {
			if (vol != null) {
				if (filter.getText().equals("") && !index.getText().equals("")) {
					output.setText(vol.getSlice(
							Integer.parseInt(index.getText())).getHeader());
				} else {
					output.setText(vol.getSlice(
							Integer.parseInt(index.getText())).getAttribute(
							Image.getKeyWords("*" + filter.getText() + "*")));
				}
			}
		} catch (NumberFormatException e) {

		}
	}

	public static void main(String[] agrs) {
		new Gui();
	}

}
