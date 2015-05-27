package imagehandling;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class Gui extends JFrame implements ActionListener {

	public static void main(String[] agrs) {
		new Gui();
	}

	private static final long serialVersionUID = 1L;

	JPanel att = new JPanel();

	JPanel dir = new JPanel();

	JPanel img = new JPanel();

	JPanel panel = new JPanel();

	JPanel contentPane = (JPanel) this.getContentPane();

	int change = 0;

	JTextField filter = new JTextField("");

	JTextField index = new JTextField("0");

	JTextField path = new JTextField("/opt/dridder_local/TestDicoms/Testfolder");

	JTextField text_slice;

	JTextField current_path = new JTextField("Volume: <<not set>>");

	JTextArea output = new JTextArea("status");

	JFileChooser chooser = new JFileChooser();

	Volume vol;

	BufferedImage image = new BufferedImage(1, 1, 1);

	boolean displayAll = true;
	
	public Gui() {
		JButton apply_path = new JButton("create Volume");
		JButton browse_path = new JButton("browse");
		JButton show_attributes = new JButton("Display all Attributes");
		addActionListerners(apply_path, browse_path, show_attributes);

		JTextField slice = new JTextField("Slice: ");
		slice.setEditable(false);
		JTextField search = new JTextField("Search:");
		search.setEditable(false);

		setfinalSize(this, new Dimension(1000, 700));
		setfinalSize(path, new Dimension(10000, 100));
		setfinalSize(output, new Dimension(100, 1000));
		setfinalSize(current_path, new Dimension(800, 100));
		setfinalSize(index, new Dimension(50, 100));
		setfinalSize(slice, new Dimension(50, 100));
		setfinalSize(search, new Dimension(50, 100));
		setfinalSize(show_attributes, new Dimension(500, 100));
		setfinalSize(filter, new Dimension(500, 100));

		output.setEditable(false);
		current_path.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		scroll.setPreferredSize(new Dimension(100, 100));

		chooser.setCurrentDirectory(new java.io.File("/opt/dridder_local/TestDicoms/Testfolder"));
		chooser.setDialogTitle("Search Path of Volume");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		dir.setLayout(new GridLayout(1, 2, 20, 1));
		att.setLayout(new BoxLayout(att, BoxLayout.LINE_AXIS));
		img.setLayout(new BoxLayout(img, BoxLayout.LINE_AXIS));
		setfinalSize(att, new Dimension(1000, 1000));
		setfinalSize(img, new Dimension(1000, 1000));
		setfinalSize(dir, new Dimension(500, 1000));

		addComponents(dir, browse_path, apply_path);
		addComponents(img, current_path,
				Box.createRigidArea(new Dimension(80, 0)), slice, index,
				Box.createRigidArea(new Dimension(10, 0)));
		addComponents(att, show_attributes,
				Box.createRigidArea(new Dimension(10, 0)), search, filter);
		addComponents(panel, Box.createRigidArea(new Dimension(0, 10)), path,
				dir, img, att, scroll);

		panel.add(new JLabel(new ImageIcon(image)));
		add(panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ImageExtractor");
		setMaximizedBounds(new Rectangle(0, 0));

		int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = contentPane.getInputMap(condition);
		ActionMap actionMap = contentPane.getActionMap();

		String up = "left";
		String down = "right";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), up);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), down);
		actionMap.put(up, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (!index.getText().equals("")) {
					change = 1;
				}
			}
		});
		actionMap.put(down, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (!index.getText().equals("0") && !index.getText().equals("")) {
					change = -1;
				}
			}
		});
		setVisible(true);
		
		lifeupdate();
	}

	private void setfinalSize(Component p, Dimension d) {
		p.setMinimumSize(d);
		p.setMaximumSize(d);
	}

	private void lifeupdate() {
		String lasttime_number = index.getText();
		String lasttime_filter = filter.getText();
		while (true) {
			if (!index.getText().equals("")) {
				try {
					if (Integer.parseInt(index.getText()) >= vol.size()) {
						if (!(Integer.parseInt(index.getText()) / 1000 > 0)) {
							index.setText("" + (vol.size() - 1));
						}
					}
					if (!lasttime_number.equals(index.getText())) {
						lasttime_number = index.getText();
						displayAttributes();
					}
				} catch (NumberFormatException | NullPointerException e) {
					if (index.getText().equals(lasttime_number)) {
						lasttime_number = "0";
					}
					index.setText(lasttime_number);
				}
			}
			if (!filter.getText().equals("")) {
				if (!lasttime_filter.equals(filter.getText())) {
					//Text got changed
					lasttime_filter = filter.getText();
					lasttime_number = index.getText();
					displayAll = false;
					displayAttributes();
				}
			}
			if (change != 0) {
				index.setText("" + (Integer.parseInt(index.getText()) + change));
				change = 0;
			}
		}
	}

	private void addActionListerners(JButton b1, JButton b2, JButton b3) {
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
	}

	private void addComponents(JPanel here, Component addthis,
			Component addthis2) {
		here.add(addthis);
		here.add(addthis2);
	}

	private void addComponents(JPanel here, Component addthis,
			Component addthis2, Component addthis3) {
		here.add(addthis);
		here.add(addthis2);
		here.add(addthis3);
	}

	private void addComponents(JPanel here, Component addthis,
			Component addthis2, Component addthis3, Component addthis4) {
		here.add(addthis);
		here.add(addthis2);
		here.add(addthis3);
		here.add(addthis4);
	}

	private void addComponents(JPanel here, Component addthis,
			Component addthis2, Component addthis3, Component addthis4,
			Component addthis5) {
		here.add(addthis);
		here.add(addthis2);
		here.add(addthis3);
		here.add(addthis4);
		here.add(addthis5);
	}

	private void addComponents(JPanel here, Component addthis,
			Component addthis2, Component addthis3, Component addthis4,
			Component addthis5, Component addthis6) {
		here.add(addthis);
		here.add(addthis2);
		here.add(addthis3);
		here.add(addthis4);
		here.add(addthis5);
		here.add(addthis6);
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
		case "browse":
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				path.setText(chooser.getSelectedFile().toString());
			}
			break;
		case "Display all Attributes":
			displayAll = true;
			displayAttributes();
			image = vol.getSlice(0).getData();
			repaint();
			break;
		default:
			break;
		}
	}

	public void displayAttributes() {
		try {
			if (vol != null) {
				if (displayAll) {
					output.setText(vol.getSlice(
							Integer.parseInt(index.getText())).getHeader());
				} else {
					StringBuilder outputstring = new StringBuilder();
					for (String str : vol
							.getSlice(Integer.parseInt(index.getText()))
							.getHeader().split("\n")) {
						if (str.toLowerCase().contains(
								filter.getText().toLowerCase())) {
							outputstring.append(str + "\n");
						}
					}
					output.setText(outputstring.toString());
				}
			}
		} catch (NumberFormatException e) {

		}
	}

}
