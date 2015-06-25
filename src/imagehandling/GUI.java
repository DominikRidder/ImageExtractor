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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class GUI extends JFrame implements ActionListener, Runnable {

	interface MyTab {
		public String getClassName();
	}

	class SorterTab extends JPanel implements ActionListener, MyTab, Runnable {
		private Thread currentSort = null;
		private SortAlgorithm sa;
		private JTextArea output;
		private JPanel[] rows_left;
		private JPanel[] rows_right;
		private ByteArrayOutputStream baos;
		private JScrollPane scroll;
		private JFileChooser chooser = new JFileChooser();
		private JButton startsort;

		private static final long serialVersionUID = 1L;

		public SorterTab() {
			sa = new SortAlgorithm();
			sa.setFilesOptionCopy();
			sa.setImgDigits(4);
			sa.setProtocolDigits(0);

			chooser.setCurrentDirectory(new java.io.File("$HOME"));
			chooser.setDialogTitle("Search Path of Volume");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			// upperleft rectangle
			JTextField upperleft_header = new JTextField(
					"Search in and Sort to:");
			setfinalSize(upperleft_header, new Dimension(150, 30));
			upperleft_header.setEditable(false);
			upperleft_header.setBorder(null);

			JPanel header_shifter_left = new JPanel();
			header_shifter_left.setLayout(new BoxLayout(header_shifter_left,
					BoxLayout.LINE_AXIS));
			header_shifter_left.add(upperleft_header);
			header_shifter_left
					.add(Box.createRigidArea(new Dimension(1000, 0)));

			JPanel table_header_left = new JPanel();
			table_header_left.setLayout(new BoxLayout(table_header_left,
					BoxLayout.LINE_AXIS));
			table_header_left.add(createText("Status", 100, 30, false));
			table_header_left.add(createText("Input Dir", 200, 30, false));
			table_header_left.add(createText("Option", 80, 30, false));
			table_header_left.add(createText("To Output Nr.", 100, 30, false));
			table_header_left.add(Box.createRigidArea(new Dimension(39, 30)));

			JPanel upperleft = new JPanel();
			upperleft.setLayout(new BoxLayout(upperleft, BoxLayout.PAGE_AXIS));
			setfinalSize(upperleft, new Dimension(550, 250));
			upperleft.add(header_shifter_left);
			upperleft.add(Box.createRigidArea(new Dimension(0, 10)));
			upperleft.add(table_header_left);

			rows_left = new JPanel[5];
			for (int i = 0; i < rows_left.length; i++) {
				rows_left[i] = createInputRow(i + 1);
				upperleft.add(rows_left[i]);
			}
			// -- upperleft end

			// upperright rectangle
			JTextField upperright_header = new JTextField("Target Folder:");
			setfinalSize(upperright_header, new Dimension(175, 30));
			upperright_header.setEditable(false);
			upperright_header.setBorder(null);

			startsort = new JButton("Start Sort");
			startsort.addActionListener(this);

			JPanel header_shifter_right = new JPanel();
			header_shifter_right.setLayout(new BoxLayout(header_shifter_right,
					BoxLayout.LINE_AXIS));
			header_shifter_right.add(upperright_header);
			header_shifter_right.add(Box
					.createRigidArea(new Dimension(1000, 0)));

			JPanel table_header_right = new JPanel();
			table_header_right.setLayout(new BoxLayout(table_header_right,
					BoxLayout.LINE_AXIS));
			table_header_right.add(createText("Nr.", 50, 30, false));
			table_header_right.add(createText("Output Dir", 200, 30, false));
			// table_header_right
			// .add(createText("Protocol Digits", 100, 30, false));
			table_header_right.add(createText("Image Digits", 100, 30, false));
			table_header_right.add(Box.createRigidArea(new Dimension(29, 30)));

			JPanel upperright = new JPanel();
			upperright
					.setLayout(new BoxLayout(upperright, BoxLayout.PAGE_AXIS));
			setfinalSize(upperright, new Dimension(550, 250));
			upperright.add(header_shifter_right);
			upperright.add(Box.createRigidArea(new Dimension(0, 10)));
			upperright.add(table_header_right);
			rows_right = new JPanel[5];
			for (int i = 0; i < rows_left.length; i++) {
				rows_right[i] = createOutputRow(i + 1);
				upperright.add(rows_right[i]);
			}
			upperright.add(startsort);
			// -- upperright end

			output = new JTextArea();
			output.setEditable(false);
			// setfinalSize(output, new Dimension(1100, 250));

			scroll = new JScrollPane(output);
			setfinalSize(scroll, new Dimension(1100, 225));
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setPreferredSize(new Dimension(1100, 200));

			JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
			setfinalSize(separator, new Dimension(1, 250));

			JPanel upper = new JPanel();
			upper.setLayout(new BoxLayout(upper, BoxLayout.LINE_AXIS));
			upper.add(upperleft);
			upper.add(separator);
			upper.add(upperright);

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.add(upper);
			this.add(scroll);

			// Create a stream to hold the output
			baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			sa.setPrintStream(ps);
		}

		private JPanel createOutputRow(int index) {
			JButton jb = new JButton();
			jb.setText((6 + index) + ":browse");
			jb.setMaximumSize(new Dimension(29, 27));
			jb.setPreferredSize(new Dimension(29, 27));
			jb.addActionListener(this);

			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
			row.add(createText("" + index, 50, 30, false));
			row.add(createText("", 200, 30, true));
			row.add(createText("4", 100, 30, true));
			row.add(jb);
			return row;
		}

		private JPanel createInputRow(int index) {
			JButton jb = new JButton();
			jb.setText(index + ":browse");
			jb.setMaximumSize(new Dimension(29, 27));
			jb.setPreferredSize(new Dimension(29, 27));
			jb.addActionListener(this);

			String[] options = { "Copy", "Move" };
			JComboBox<String> jc = new JComboBox<String>(options);
			setfinalSize(jc, new Dimension(80, 28));

			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
			row.add(createText("Undefined", 100, 30, false));
			row.add(createText("", 200, 30, true));
			row.add(jc);
			row.add(createText("" + index, 100, 30, true));
			row.add(jb);
			row.add(Box.createRigidArea(new Dimension(10, 30)));
			return row;
		}

		private JTextField createText(String text, int width, int height,
				boolean editable) {
			JTextField jtf = new JTextField(text);
			setfinalSize(jtf, new Dimension(width, height));
			jtf.setEditable(editable);
			return jtf;
		}

		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Cancel": sa.stopSort();break;
			case "Start Sort":
				if (currentSort == null) {
					baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);
					sa.setPrintStream(ps);
					Thread t = new Thread(this);
					t.start();
					currentSort = t;
					startsort.setText("Cancel");
				}
				break;
			default:
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					int pos = Integer
							.parseInt(e.getActionCommand().split(":")[0]);
					if (pos < 6) {
						((JTextField) rows_left[pos - 1].getComponents()[1])
								.setText(chooser.getSelectedFile().toString());
					} else {
						pos -= 6;
						((JTextField) rows_right[pos - 1].getComponents()[1])
								.setText(chooser.getSelectedFile().toString());
					}
				}
				break;
			}
		}

		private void sort() {
			for (int i = 0; i < rows_left.length; i++) {
				Component[] left_stuff = rows_left[i].getComponents();
				JTextField status = (JTextField) left_stuff[0];
				status.setText("Unchecked");
			}

			for (int i = 0; i < rows_left.length; i++) {
				Component[] left_stuff = rows_left[i].getComponents();
				JTextField status = (JTextField) left_stuff[0];
				JTextField inputfield = (JTextField) left_stuff[1];
				JTextField tooutput = (JTextField) left_stuff[3];
				@SuppressWarnings("unchecked")
				JComboBox<String> move = (JComboBox<String>) left_stuff[2];
				if (inputfield.getText().equals("")) {
					status.setText("Empty Input");
					continue;
				}

				if (tooutput.getText().equals("")) {
					status.setText("Index Missing");
				}

				JTextField target;
				// JTextField protocol_digits;
				JTextField image_digits;
				try {
					Component[] right_stuff = rows_right[Integer
							.parseInt(tooutput.getText()) - 1].getComponents();
					target = (JTextField) right_stuff[1];
					// protocol_digits = (JTextField) right_stuff[2];
					image_digits = (JTextField) right_stuff[2];
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					status.setText("Index Err");
					continue;
				}

				status.setText("In Progress");

				try {
					int imgdigits = Integer.parseInt(image_digits.getText());
					if (imgdigits != 0) {
						sa.setImgDigits(imgdigits);
						sa.setKeepImageName(false);
					} else {
						sa.setKeepImageName(true);
					}
				} catch (NumberFormatException e) {
					status.setText("Err Img Digits");
					continue;
				}
				if (((String) move.getSelectedItem()).equals("Move")) {
					sa.setFilesOptionMove();
				} else {
					sa.setFilesOptionCopy();
				}
				status.setText("In Progress...");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				if (sa.searchAndSortIn(inputfield.getText(), target.getText())) {
					status.setText("Finished");
				} else {
					if (sa.gotStopped()){
						status.setText("Canceld");
						break;
					}
					status.setText("Input Dir Err");
				}
			}
			currentSort = null;
			startsort.setText("Start Sort");
		}

		public String getClassName() {
			return "SorterTab";
		}

		public void run() {
			sort();
		}

	}

	class VolumeTab extends JPanel implements ActionListener, MyTab {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private JPanel att = new JPanel();

		private JPanel dir = new JPanel();

		private JPanel img = new JPanel();

		private JPanel panel = new JPanel();

		private JPanel toppanel = new JPanel();

		private JLabel imagepanel;

		private ImageIcon ic;

		private int change = 0;

		private JTextField filter = new JTextField("");

		private JTextField index = new JTextField("0");

		private JTextField max = new JTextField("/0");

		private JTextField path = new JTextField(
				"/opt/dridder_local/TestDicoms/");

		private JTextField current_path = new JTextField("Volume: <<not set>>");

		private JTextArea output = new JTextArea("status");

		private JFileChooser chooser = new JFileChooser();

		private Volume vol;

		private BufferedImage image = new BufferedImage(443, 443,
				BufferedImage.TYPE_BYTE_GRAY);

		private boolean displayAll = true;

		private JButton apply_path;
		private JButton browse_path;
		private JButton show_attributes;

		public VolumeTab() {
			ic = new ImageIcon(image);
			imagepanel = new JLabel(ic);

			apply_path = new JButton("create Volume");
			browse_path = new JButton("browse");
			show_attributes = new JButton("Display all Attributes");
			addActionListerners(apply_path, browse_path, show_attributes);

			max.setEditable(false);
			setfinalSize(max, new Dimension(75, 100));
			max.setBorder(null);

			JTextField slice = new JTextField("Slice:");
			slice.setEditable(false);
			setfinalSize(slice, new Dimension(35, 100));
			slice.setBorder(null);

			JTextField search = new JTextField("Search:");
			search.setEditable(false);
			setfinalSize(search, new Dimension(50, 100));
			search.setBorder(null);

			setfinalSize(path, new Dimension(500, 100));
			setfinalSize(index, new Dimension(75, 100));
			setfinalSize(show_attributes, new Dimension(500, 100));
			setfinalSize(filter, new Dimension(500, 100));

			output.setEditable(false);
			setfinalSize(output, new Dimension(100, 1050));

			current_path.setEditable(false);
			setfinalSize(current_path, new Dimension(220, 100));

			JScrollPane scroll = new JScrollPane(output);
			scroll.setPreferredSize(new Dimension(100, 100));
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			chooser.setCurrentDirectory(new java.io.File("$HOME"));
			chooser.setDialogTitle("Search Path of Volume");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			setfinalSize(panel, new Dimension(650, 1100));
			Component[] panelstuff = {
					Box.createRigidArea(new Dimension(0, 10)), path, dir, img,
					att, scroll };
			addComponents(panel, panelstuff);

			dir.setLayout(new GridLayout(1, 2, 20, 1));
			setfinalSize(dir, new Dimension(500, 1000));
			Component[] dirstuff = { browse_path, apply_path };
			addComponents(dir, dirstuff);

			att.setLayout(new BoxLayout(att, BoxLayout.LINE_AXIS));
			setfinalSize(att, new Dimension(550, 400));
			Component[] attstuff = { show_attributes,
					Box.createRigidArea(new Dimension(10, 0)), search, filter };
			addComponents(att, attstuff);

			img.setLayout(new BoxLayout(img, BoxLayout.LINE_AXIS));
			setfinalSize(img, new Dimension(500, 500));
			Component[] imgstuff = { current_path,
					Box.createRigidArea(new Dimension(80, 0)), slice, index,
					max };
			addComponents(img, imgstuff);

			toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
			setfinalSize(toppanel, new Dimension(1100, 450));
			toppanel.add(panel);
			toppanel.add(imagepanel);

			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setfinalSize(this, new Dimension(1100, 450));
			this.add(toppanel);

			// Arrow input reaction:
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

			this.setVisible(true);
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
				max.setText("/" + (vol.size() - 1));
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

	private JTabbedPane tabber;

	private boolean forceEnd;

	private static int windows = 0;

	private int tabint = 0;

	public GUI(boolean forceProgrammEndIfThereIsNoWindow) {
		forceEnd = forceProgrammEndIfThereIsNoWindow;
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem newGuiWindow, newVolumeTab, newSortTab;

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
		newTab(new SorterTab());

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
		if (((MyTab) comp).getClassName() == "VolumeTab") {
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

			}

		}

	}

	private void setfinalSize(Component p, Dimension d) {
		p.setMinimumSize(d);
		p.setMaximumSize(d);
	}

	private void updateVolume() {
		VolumeTab actual = null;
		String lasttime_number = "0";
		String lasttime_filter = "";
		if (((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
				.getClassName().equals("VolumeTab")) {
			actual = (VolumeTab) tabber.getComponentAt(tabber
					.getSelectedIndex());
			lasttime_number = actual.index.getText();
			lasttime_filter = actual.filter.getText();
		}

		while (this.isVisible()) {
			if (tabber.getTabCount() == 0) {
				break;
			}
			try {
				if (((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
						.getClassName().equals("VolumeTab")) {
					actual = (VolumeTab) tabber.getComponentAt(tabber
							.getSelectedIndex());
				} else {
					break;
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

	private void updateSort() {
		SorterTab actual = null;
		boolean lastupdate = true;
		if (((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
				.getClassName().equals("SorterTab")) {
			actual = (SorterTab) tabber.getComponentAt(tabber
					.getSelectedIndex());
		}

		while (this.isVisible()) {
			if (tabber.getTabCount() == 0) {
				break;
			}
			try {
				if (((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
						.getClassName().equals("SorterTab")) {
					actual = (SorterTab) tabber.getComponentAt(tabber
							.getSelectedIndex());
				} else {
					break;
				}
			} catch (IndexOutOfBoundsException e) {
				continue;
			}

			try {
				if (actual.currentSort != null || lastupdate) {
					if (actual.currentSort == null) {
						lastupdate = false;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						lastupdate = true;
					}
					actual.output.setText(actual.baos.toString());
					int i = actual.scroll.getVerticalScrollBar().getMaximum();
					actual.scroll.getVerticalScrollBar().setValue(i);
				}
			} catch (NullPointerException e) {

			} finally {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void lifeupdate() {
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
						.getClassName().equals("VolumeTab")) {
					updateVolume();
				} else if (((MyTab) tabber.getComponentAt(tabber
						.getSelectedIndex())).getClassName()
						.equals("SorterTab")) {
					updateSort();
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					continue;
				}
			} catch (IndexOutOfBoundsException e) {
				continue;
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
