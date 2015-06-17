package imagehandling;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class GUI extends JFrame implements ActionListener, Runnable {

	interface MyTab {
		public String getClassName();
	}

	class SorterTab extends JPanel implements ActionListener, MyTab {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

		}

		public String getClassName() {
			return "SorterTab";
		}
		
		
		
	}

	class VolumeTab extends JPanel implements ActionListener, MyTab {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		JPanel att = new JPanel();

		JPanel dir = new JPanel();

		JPanel img = new JPanel();

		JPanel panel = new JPanel();

		JPanel toppanel = new JPanel();

		JLabel imagepanel;

		ImageIcon ic;

		int change = 0;

		JTextField filter = new JTextField("");

		JTextField index = new JTextField("0");

		JTextField path = new JTextField("/opt/dridder_local/TestDicoms/");

		JTextField text_slice = new JTextField("0");

		JTextField current_path = new JTextField("Volume: <<not set>>");

		JTextArea output = new JTextArea("status");

		JFileChooser chooser = new JFileChooser();

		Volume vol;

		BufferedImage image = new BufferedImage(450, 450,
				BufferedImage.TYPE_BYTE_GRAY);

		boolean displayAll = true;

		JButton apply_path;
		JButton browse_path;
		JButton show_attributes;

		public VolumeTab() {
			JButton apply_path = new JButton("create Volume");
			JButton browse_path = new JButton("browse");
			JButton show_attributes = new JButton("Display all Attributes");
			addActionListerners(apply_path, browse_path, show_attributes);

			JTextField slice = new JTextField("Slice: ");
			slice.setEditable(false);
			JTextField search = new JTextField("Search:");
			search.setEditable(false);

			setfinalSize(this, new Dimension(1100, 450));
			setfinalSize(toppanel, new Dimension(1100, 450));
			setfinalSize(path, new Dimension(500, 100));
			setfinalSize(output, new Dimension(100, 1000));
			setfinalSize(current_path, new Dimension(300, 100));
			setfinalSize(index, new Dimension(75, 100));
			setfinalSize(slice, new Dimension(50, 100));
			setfinalSize(search, new Dimension(50, 100));
			setfinalSize(show_attributes, new Dimension(500, 100));
			setfinalSize(filter, new Dimension(500, 100));
			setfinalSize(panel, new Dimension(650, 1000));
			setfinalSize(img, new Dimension(500, 500));

			output.setEditable(false);
			current_path.setEditable(false);
			JScrollPane scroll = new JScrollPane(output);
			scroll.setPreferredSize(new Dimension(100, 100));

			chooser.setCurrentDirectory(new java.io.File("$HOME"));
			chooser.setDialogTitle("Search Path of Volume");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			dir.setLayout(new GridLayout(1, 2, 20, 1));
			att.setLayout(new BoxLayout(att, BoxLayout.LINE_AXIS));
			img.setLayout(new BoxLayout(img, BoxLayout.LINE_AXIS));
			toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setfinalSize(att, new Dimension(550, 400));
			setfinalSize(img, new Dimension(1000, 1000));
			setfinalSize(dir, new Dimension(500, 1000));

			Component[] dirstuff = { browse_path, apply_path };
			Component[] imgstuff = { current_path,
					Box.createRigidArea(new Dimension(80, 0)), slice, index,
					Box.createRigidArea(new Dimension(10, 0)) };
			Component[] attstuff = { show_attributes,
					Box.createRigidArea(new Dimension(10, 0)), search, filter };
			Component[] panelstuff = {
					Box.createRigidArea(new Dimension(0, 10)), path, dir, img,
					att, scroll };
			addComponents(dir, dirstuff);
			addComponents(img, imgstuff);
			addComponents(att, attstuff);
			addComponents(panel, panelstuff);
			toppanel.add(panel);
			ic = new ImageIcon(image);
			imagepanel = new JLabel(ic);
			toppanel.add(imagepanel);
			this.add(toppanel);

			int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
			InputMap inputMap = this.getInputMap(condition);
			ActionMap actionMap = this.getActionMap();

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
					if (!index.getText().equals("0")
							&& !index.getText().equals("")) {
						change = -1;
					}
				}
			});

			setVisible(true);
		}

		public void displayAttributes() {
			try {
				if (vol == null) {
					createVolume();
				}
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

		private void createVolume() {
			try {
				vol = new Volume(path.getText(), this);
				output.setText("Volume created");
				current_path.setText("Volume: " + path.getText());

				image.getGraphics()
						.drawImage(
								vol.getSlice(Integer.parseInt(index.getText()))
										.getData()
										.getScaledInstance(image.getWidth(),
												image.getHeight(),
												BufferedImage.SCALE_SMOOTH), 0,
								0, null);
				repaint();
			} catch (RuntimeException ert) {
				output.setText("Creating Volume didnt work. Please check the path.");
			}
		}

		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "create Volume":
				createVolume();
				break;
			case "browse":
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					path.setText(chooser.getSelectedFile().toString());
				}
				break;
			case "Display all Attributes":
				displayAll = true;
				displayAttributes();
				break;
			default:
				break;
			}
		}

		public String getClassName() {
			return "VolumeTab";
		}
	}

	public static void main(String[] agrs) {
		new GUI(true);
	}

	private static final long serialVersionUID = 1L;

	JTabbedPane tabber;

	boolean forceEnd;

	static int windows = 0;

	int tabint = 0;

	public GUI(boolean forceProgrammEndIfThereIsNoWindow) {
		forceEnd = forceProgrammEndIfThereIsNoWindow;
		JMenuBar menuBar;
		JMenu menu /** ,submenu */
		;
		JMenuItem /** menuItem, */
		newGuiWindow, newVolumeTab, newSortTab;
		// JRadioButtonMenuItem rbMenuItem;
		// JCheckBoxMenuItem cbMenuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build first menu in the menu bar.
		newVolumeTab = new JMenuItem("new Volume Tab");
		newVolumeTab.addActionListener(this);
		newSortTab = new JMenuItem("new Sort Tab");
		newSortTab.addActionListener(this);
		newGuiWindow = new JMenuItem("new Window");
		newGuiWindow.addActionListener(this);
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
				"This menu does nothing");
		menu.add(newGuiWindow);
		menu.add(newVolumeTab);
		menu.add(newSortTab);
		menuBar.add(menu);

		this.setJMenuBar(menuBar);

		// Menu stuff ends here

		tabber = new JTabbedPane();
		newTab(new VolumeTab());

		add(tabber);
		setLocationRelativeTo(null);
		setTitle("ImageExtractor");
		setMaximizedBounds(new Rectangle(0, 0));
		setfinalSize(this, new Dimension(1100, 550));
		setVisible(true);

		new Thread(this).start();
	}

	private void newTab(JComponent comp) {
		if (tabber.getTabCount() >= 9) {
			return;
		}
		tabint++;
		String title = (tabint) + "";
		if (((MyTab) comp)
				.getClassName() == "VolumeTab") {
			title += ": Volume";
		} else {
			title += ": Sorter";
		}
		tabber.addTab(title, comp);

		int index = tabber.indexOfTab(title);
		JPanel pnlTab = new JPanel();
		pnlTab.setLayout(new BoxLayout(pnlTab, BoxLayout.LINE_AXIS));
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		JButton btnClose = new JButton("x");
		btnClose.setMaximumSize(new Dimension(30, 15));
		btnClose.setMargin(new Insets(0, 0, 0, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;

		pnlTab.add(lblTitle, gbc);

		pnlTab.add(Box.createRigidArea(new Dimension(10, 10)));

		gbc.gridx++;
		gbc.weightx = 0;
		pnlTab.add(btnClose, gbc);

		tabber.setTabComponentAt(index, pnlTab);

		btnClose.addActionListener(new MyCloseActionHandler(title));
		tabber.setSelectedIndex(tabber.getTabCount() - 1);
	}

	class MyCloseActionHandler implements ActionListener {

		private String tabName;

		public MyCloseActionHandler(String tabName) {
			this.tabName = tabName;
		}

		public String getTabName() {
			return tabName;
		}

		public void actionPerformed(ActionEvent evt) {

			int index = tabber.indexOfTab(getTabName());
			if (index >= 0) {
				if (tabber.getTabCount() > 1) {
					tabber.setSelectedIndex(index - 1);
				}

				tabber.removeTabAt(index);

				// It would probably be worthwhile getting the source
				// casting it back to a JButton and removing
				// the action handler reference ;)

			}

		}

	}

	private void setfinalSize(Component p, Dimension d) {
		p.setMinimumSize(d);
		p.setMaximumSize(d);
	}

	private void lifeupdate() {
		VolumeTab actual = (VolumeTab) tabber.getComponentAt(tabber
				.getSelectedIndex());
		String lasttime_number = actual.index.getText();
		String lasttime_filter = actual.filter.getText();
		while (this.isVisible()) {
			if (tabber.getTabCount() == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			try {
				if (((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
						.getClassName() == "VolumeTab") {
					actual = (VolumeTab) tabber.getComponentAt(tabber
							.getSelectedIndex());
				} else {
					continue;
				}
			} catch (IndexOutOfBoundsException e) {
				continue;
			}
			if (!actual.index.getText().equals("")) {
				try {
					if (Integer.parseInt(actual.index.getText()) >= actual.vol
							.size()
							&& Integer.parseInt(actual.index.getText()) != 0) {
						if (!(Integer.parseInt(actual.index.getText()) / 1000 > 0)) {
							actual.index.setText("" + (actual.vol.size() - 1));
						}
					}
					if (!lasttime_number.equals(actual.index.getText())) {
						lasttime_number = actual.index.getText();
						actual.displayAttributes();
						actual.image.getGraphics().drawImage(
								actual.vol
										.getSlice(
												Integer.parseInt(actual.index
														.getText()))
										.getData()
										.getScaledInstance(
												actual.image.getWidth(),
												actual.image.getHeight(),
												BufferedImage.SCALE_FAST), 0,
								0, null);
						actual.repaint();
					}
				} catch (NumberFormatException | NullPointerException e) {
					if (actual.index.getText().equals(lasttime_number)) {
						lasttime_number = "0";
					}
					actual.index.setText(lasttime_number);
				}
			}
			if (!actual.filter.getText().equals("")) {
				if (!lasttime_filter.equals(actual.filter.getText())) {
					lasttime_filter = actual.filter.getText();
					lasttime_number = actual.index.getText();
					actual.displayAll = false;
					actual.displayAttributes();
				}
			}
			if (actual.change != 0) {
				actual.index
						.setText(""
								+ (Integer.parseInt(actual.index.getText()) + actual.change));
				actual.change = 0;
			}
		}
	}

	private void addActionListerners(JButton b1, JButton b2, JButton b3) {
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
	}

	private void addComponents(JPanel here, Component[] toadd) {
		for (int i = 0; i < toadd.length; i++) {
			here.add(toadd[i]);
		}
	}

	public void actionPerformed(ActionEvent e) {
		VolumeTab actual = null;
		if (tabber.getTabCount() != 0
				&& ((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
						.getClassName() == "VolumeTab") {
			actual = (VolumeTab) tabber.getComponentAt(tabber
					.getSelectedIndex());
		}
		switch (e.getActionCommand()) {
		case "create Volume":
			actual.createVolume();
			break;
		case "browse":
			if (actual.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				actual.path
						.setText(actual.chooser.getSelectedFile().toString());
			}
			break;
		case "Display all Attributes":
			actual.displayAll = true;
			actual.displayAttributes();
			break;
		case "new Volume Tab":
			newTab(new VolumeTab());
			break;
		case "new Sort Tab":
			newTab(new SorterTab());
			break;
		case "new Window":
			new GUI(forceEnd);
			break;
		default:
			System.out.println(e.getActionCommand());
			break;
		}
	}

	public void run() {
		windows++;
		lifeupdate();
		if (--windows == 0 && forceEnd) {
			System.exit(1);
		}
	}
}
