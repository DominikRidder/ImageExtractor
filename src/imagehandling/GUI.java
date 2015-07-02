package imagehandling;

import java.awt.Color;
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
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
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

/**
 * This GUI is used, to look the Header and Images of Dicoms and to Search and Sort Dicoms.
 * @author dridder_local
 *
 */
public class GUI extends JFrame implements ActionListener, Runnable {

	/**
	 * Main method, to start the GUI.
	 * @param agrs
	 */
	public static void main(String[] agrs) {
		new GUI(true);
	}
	
	/**
	 * This is used by the inner classes of GUI (VolumeTab and SorterTab).
	 * @author dridder_local
	 *
	 */
	interface MyTab {
		public String getClassName();
	}

	/**
	 * Inner Class of GUI. Used to represent a Tab, which is usefull to sort Dicoms.
	 * @author dridder_local
	 *
	 */
	class SorterTab extends JPanel implements ActionListener, MyTab, Runnable {
		/**
		 * The current Thread, which is using the sort method/algorithm.
		 */
		private Thread currentSort = null;

		/**
		 * The SortAlgorithm, which is used to perform the sort.
		 */
		private SortAlgorithm sa;

		/**
		 * JTextArea output contains the output made by the SortAlgorithm.
		 */
		private JTextArea output;

		/**
		 * Array of JPanels, where each JPanel representing a row in the left
		 * table.
		 */
		private JPanel[] tablerows_left;

		/**
		 * Array of JPanels, where each JPanel representing a row in the right
		 * table.
		 */
		private JPanel[] tablerows_right;

		/**
		 * The ByteArrayOutputStream, which catches the output made by the
		 * SortAlgorithm.
		 */
		private ByteArrayOutputStream baos;

		/**
		 * JScrollPane, which adding the possibility for scrolling to the
		 * TextArea "output".
		 */
		private JScrollPane scroll;

		/**
		 * The JFileChooser is used to browse for a Volume dir.
		 */
		private JFileChooser chooser = new JFileChooser();

		/**
		 * JButton to call the sort() method and to cancel it again.
		 */
		private JButton startsort;

		/**
		 * Default serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default Constructur.
		 */
		public SorterTab() {
			// Setting up the sortalgorithm with some default stuff
			sa = new SortAlgorithm();
			sa.setFilesOptionCopy();
			sa.setImgDigits(4);
			sa.setProtocolDigits(0);

			// chooser stuff
			chooser.setCurrentDirectory(new java.io.File("$HOME"));
			chooser.setDialogTitle("Search Directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			// upperleft rectangle
			JTextField upperleft_header = new JTextField(
					"Search in and Sort to:");
			setfinalSize(upperleft_header, new Dimension(150, 30));
			upperleft_header.setEditable(false);
			upperleft_header.setBorder(null);

			// The header shifter is used to put the header to the left side,
			// instead of letting it in the middle of a pannel
			JPanel header_shifter_left = new JPanel();
			header_shifter_left.setLayout(new BoxLayout(header_shifter_left,
					BoxLayout.LINE_AXIS));
			header_shifter_left.add(upperleft_header);
			header_shifter_left
					.add(Box.createRigidArea(new Dimension(1000, 0)));

			// Setting the headline of the table, which should be as long as the
			// rows below it
			JPanel table_header_left = new JPanel();
			table_header_left.setLayout(new BoxLayout(table_header_left,
					BoxLayout.LINE_AXIS));
			table_header_left.add(createText("Status", 100, 30, false));
			table_header_left.add(createText("Input Dir", 200, 30, false));
			table_header_left.add(createText("Option", 80, 30, false));
			table_header_left.add(createText("To Output Nr.", 100, 30, false));
			table_header_left.add(Box.createRigidArea(new Dimension(39, 30)));

			// Panel that contains the upper left rectangle
			JPanel upperleft = new JPanel();
			upperleft.setLayout(new BoxLayout(upperleft, BoxLayout.PAGE_AXIS));
			setfinalSize(upperleft, new Dimension(550, 250));
			upperleft.add(header_shifter_left);
			upperleft.add(Box.createRigidArea(new Dimension(0, 10)));
			upperleft.add(table_header_left);

			tablerows_left = new JPanel[5];
			for (int i = 0; i < tablerows_left.length; i++) {
				tablerows_left[i] = createInputRow(i + 1);
				upperleft.add(tablerows_left[i]);
			}
			// -- upperleft end

			// upperright rectangle
			JTextField upperright_header = new JTextField("Target Folder:");
			setfinalSize(upperright_header, new Dimension(175, 30));
			upperright_header.setEditable(false);
			upperright_header.setBorder(null);

			// Button for starting/stopping a sort
			startsort = new JButton("Start Sort");
			startsort.addActionListener(this);

			// The header shifter is used to put the header to the left side,
			// instead of letting it in the middle of a pannel
			JPanel header_shifter_right = new JPanel();
			header_shifter_right.setLayout(new BoxLayout(header_shifter_right,
					BoxLayout.LINE_AXIS));
			header_shifter_right.add(upperright_header);
			header_shifter_right.add(Box
					.createRigidArea(new Dimension(1000, 0)));

			// Setting the headline of the table, which should be as long as the
			// rows below it
			JPanel table_header_right = new JPanel();
			table_header_right.setLayout(new BoxLayout(table_header_right,
					BoxLayout.LINE_AXIS));
			table_header_right.add(createText("Nr.", 50, 30, false));
			table_header_right.add(createText("Output Dir", 200, 30, false));
			table_header_right.add(createText("Image Digits", 100, 30, false));
			table_header_right.add(Box.createRigidArea(new Dimension(29, 30)));

			// Panel that contains the upper right rectangle
			JPanel upperright = new JPanel();
			upperright
					.setLayout(new BoxLayout(upperright, BoxLayout.PAGE_AXIS));
			setfinalSize(upperright, new Dimension(550, 250));
			upperright.add(header_shifter_right);
			upperright.add(Box.createRigidArea(new Dimension(0, 10)));
			upperright.add(table_header_right);
			tablerows_right = new JPanel[5];
			for (int i = 0; i < tablerows_left.length; i++) {
				tablerows_right[i] = createOutputRow(i + 1);
				upperright.add(tablerows_right[i]);
			}
			upperright.add(startsort);
			// -- upperright end

			// creating the output field
			output = new JTextArea();
			output.setEditable(false);
			scroll = new JScrollPane(output);
			setfinalSize(scroll, new Dimension(1100, 225));
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setPreferredSize(new Dimension(1100, 200));

			// Seperates the left upper side from the right upper side
			JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
			setfinalSize(separator, new Dimension(1, 250));

			// The panel over the output
			JPanel upper = new JPanel();
			upper.setLayout(new BoxLayout(upper, BoxLayout.LINE_AXIS));
			upper.add(upperleft);
			upper.add(separator);
			upper.add(upperright);

			// adding everythis to this (class)
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.add(upper);
			this.add(scroll);

			// Create a stream to hold the output
			baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			sa.setPrintStream(ps);
		}

		/**
		 * This method creating a table row for the output table
		 */
		private JPanel createOutputRow(int index) {
			// Button for searching a dir
			JButton jb = new JButton();
			// the number at the start is used to know, where the browsed
			// directory have to be set. The ":" is important for the splitt. At
			// the end you will just the a "..." in the button.
			jb.setText((6 + index) + ":browse");
			jb.setMaximumSize(new Dimension(29, 27));
			jb.setPreferredSize(new Dimension(29, 27));
			jb.setMargin(null);
			jb.addActionListener(this);

			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
			// index field
			row.add(createText("" + index, 50, 30, false));
			// output dir field
			row.add(createText("", 200, 30, true));
			// image digits field
			row.add(createText("4", 100, 30, true));
			// browse dir button
			row.add(jb);
			return row;
		}

		/**
		 * This method creating a table row for the input table
		 */
		private JPanel createInputRow(int index) {
			// Button for searching a dir
			JButton jb = new JButton();
			// the number at the start is used to know, where the browsed
			// directory have to be set. The ":" is important for the splitt. At
			// the end you will just the a "..." in the button.
			jb.setText(index + ":browse");
			jb.setMaximumSize(new Dimension(29, 27));
			jb.setPreferredSize(new Dimension(29, 27));
			jb.setMargin(null);
			jb.addActionListener(this);

			// File transfer options
			String[] options = { "Copy", "Move" };
			JComboBox<String> jc = new JComboBox<String>(options);
			setfinalSize(jc, new Dimension(80, 28));

			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
			// Status
			row.add(createText("Undefined", 100, 30, false));
			// Input dir
			row.add(createText("", 200, 30, true));
			// File transfer option
			row.add(jc);
			// Output Nr field
			row.add(createText("" + index, 100, 30, true));
			// browse dir button
			row.add(jb);
			// to make it fit
			row.add(Box.createRigidArea(new Dimension(10, 30)));
			return row;
		}

		/**
		 * Fast method for creating JTextField classes.
		 */
		private JTextField createText(String text, int width, int height,
				boolean editable) {
			JTextField jtf = new JTextField(text);
			setfinalSize(jtf, new Dimension(width, height));
			jtf.setEditable(editable);
			return jtf;
		}

		/**
		 * This method is called by the buttons of SorterTab.
		 */
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Cancel": // Stopping the sort safely
				sa.stopSort();
				break;
			case "Start Sort":
				// never starting two threads
				if (currentSort == null) {
					// New Thread for the Sort
					Thread t = new Thread(this);

					// Taking always a new baos to clear the output area
					baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);
					sa.setPrintStream(ps);

					// Starting the sort
					t.start();
					currentSort = t;
					startsort.setText("Cancel");
				}
				break;
			default:
				// Using the default for the browse buttons, instead of making
				// 10 case lines
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					int pos = Integer
							.parseInt(e.getActionCommand().split(":")[0]);
					if (pos < 6) {
						((JTextField) tablerows_left[pos - 1].getComponents()[1])
								.setText(chooser.getSelectedFile().toString());
					} else {
						pos -= 6;
						((JTextField) tablerows_right[pos - 1].getComponents()[1])
								.setText(chooser.getSelectedFile().toString());
					}
				}
				break;
			}
		}

		/**
		 * This method is called by a Thread, to start the sorting.
		 */
		private void sort() {
			// Setting every status to Unchecked
			for (int i = 0; i < tablerows_left.length; i++) {
				Component[] left_stuff = tablerows_left[i].getComponents();
				JTextField status = (JTextField) left_stuff[0];
				status.setBackground(null);
				status.setText("Unchecked");
			}

			// for every input row
			for (int i = 0; i < tablerows_left.length; i++) {
				// getting the components and casting them
				Component[] left_stuff = tablerows_left[i].getComponents();
				JTextField status = (JTextField) left_stuff[0];
				status.setBackground(Color.yellow);
				JTextField inputfield = (JTextField) left_stuff[1];
				JTextField tooutput = (JTextField) left_stuff[3];
				@SuppressWarnings("unchecked")
				JComboBox<String> move = (JComboBox<String>) left_stuff[2];

				// catching empty input
				if (inputfield.getText().equals("")) {
					status.setBackground(Color.LIGHT_GRAY);
					status.setText("Empty Input");
					continue;
				}

				// catching empty output nr
				if (tooutput.getText().equals("")) {
					status.setBackground(Color.LIGHT_GRAY);
					status.setText("Index Missing");
				}

				// now getting the output information
				JTextField target;
				JTextField image_digits;
				try {
					// Integer.parseInt may throws an exception
					Component[] right_stuff = tablerows_right[Integer
							.parseInt(tooutput.getText()) - 1].getComponents();
					target = (JTextField) right_stuff[1];
					image_digits = (JTextField) right_stuff[2];
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					status.setBackground(Color.LIGHT_GRAY);
					status.setText("Index Err");
					continue;
				}

				// No Outputdir is set
				if (target.getText().equals("")) {
					status.setBackground(Color.LIGHT_GRAY);
					status.setText("No Outp. Dir");
					continue;
				}

				// setting the img digits and the boolean keepImageName in
				// SortAlgorithm
				try {
					int imgdigits = Integer.parseInt(image_digits.getText());
					if (imgdigits != 0) {
						sa.setImgDigits(imgdigits);
						sa.setKeepImageName(false);
					} else {
						sa.setKeepImageName(true);
					}
				} catch (NumberFormatException e) {
					status.setBackground(Color.LIGHT_GRAY);
					status.setText("Err Img Digits");
					continue;
				}

				// setting the file transfer option
				if (((String) move.getSelectedItem()).equals("Move")) {
					sa.setFilesOptionMove();
				} else {
					sa.setFilesOptionCopy();
				}

				// now the sortalgorithm can start
				status.setText("In Progress...");
				if (sa.searchAndSortIn(inputfield.getText(), target.getText())) {
					status.setText("Finished");
					status.setBackground(Color.GREEN);
				} else {
					if (sa.gotStopped()) {
						if (sa.getPermissionProblem()) {
							status.setBackground(Color.LIGHT_GRAY);
							status.setText("Permission Err");
							continue;
						} else {
							status.setBackground(Color.LIGHT_GRAY);
							status.setText("Canceled");
							break;
						}
					}
					status.setBackground(Color.LIGHT_GRAY);
					status.setText("Input Dir Err");
				}
			}

			// things that have to be done, so the next sort can be called
			currentSort = null;
			startsort.setText("Start Sort");
		}

		/**
		 * This method is usefull for the GUI class to decide what kind of Tab
		 * is in the focus.
		 */
		public String getClassName() {
			return "SorterTab";
		}

		/**
		 * Method, called by Thead.start();
		 */
		public void run() {
			sort();
		}

	}

	/**
	 * This inner class representing a Tab in the GUI window, where you can look up the header and images of a Volume.
	 * @author dridder_local
	 *
	 */
	class VolumeTab extends JPanel implements ActionListener, MyTab {

		/**
		 * Default serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * This JPanel contains a JTextField search with the text "Search:" and
		 * a textfield to search for Attributes.
		 */
		private JPanel att = new JPanel();

		/**
		 * The JPanel dir is a row, which contains two JButtons (browse_path and
		 * apply_path).
		 */
		private JPanel dir = new JPanel();

		/**
		 * The JPanel img is a row, which contains a JTextfield with the Text
		 * "Slice:", the current slice and the maximum chooseable slice.
		 */
		private JPanel img = new JPanel();

		/**
		 * Panel, which contains the hole left side of this tab frame.
		 */
		private JPanel panel = new JPanel();

		/**
		 * Highest Panel is this Tab.
		 */
		private JPanel toppanel = new JPanel();

		/**
		 * The imagepanel is the container, which contains the ImageIcon ic.
		 */
		private JLabel imagepanel;

		/**
		 * The ImageIcon ic wrapping the image.
		 */
		private ImageIcon ic;

		/**
		 * This int is used to communicate between an ActionListener and this
		 * GUI. With the help of these things, the choosen slice (index) can be
		 * changed with the arrow keys.
		 */
		private int change = 0;

		/**
		 * The filter is used to search for Attributes in the header of an
		 * image.
		 */
		private JTextField filter = new JTextField("");

		/**
		 * The index field shows the current selected Volume slice.
		 */
		private JTextField index = new JTextField("0");

		/**
		 * This field shows the number of slices in the volume (minus one).
		 */
		private JTextField max = new JTextField("/0");

		/**
		 * This TextField is used to create a Volume.
		 */
		private JTextField path = new JTextField(
				"/opt/dridder_local/TestDicoms/");

		/**
		 * This TextField is usefull, so the user can take a look which Volume
		 * he is using at the moment, for the case, that the upper Textfield got
		 * changed.
		 */
		private JTextField current_path = new JTextField("Volume: <<not set>>");

		/**
		 * Header and co. output stuff, which is wrapped in the scroll object.
		 */
		private JTextArea output = new JTextArea("status");

		/**
		 * JFileChooser is used to search a Volume (dir).
		 */
		private JFileChooser chooser = new JFileChooser();

		/**
		 * Volume, which is to get the header informations and the image.
		 */
		private Volume vol;

		/**
		 * Image which is on the right side of the Tab.
		 */
		private BufferedImage image = new BufferedImage(443, 443,
				BufferedImage.TYPE_BYTE_GRAY);

		/**
		 * Value, which decides, whether the hole header of a dicom is shown or
		 * if only a searched part in shown.
		 */
		private boolean displayAll = true;

		/**
		 * JButton to try, to create a Volume to a given path.
		 */
		private JButton apply_path;

		/**
		 * JButton to call the JFileChooser.
		 */
		private JButton browse_path;

		/**
		 * JButton to show the hole header of a Volume.
		 */
		private JButton show_attributes;

		/**
		 * Standard Constructur.
		 */
		public VolumeTab() {
			// The ImageIcon is kinda a wrapper for the image
			ic = new ImageIcon(image);
			// imagepanel wrapps the ImageIcon
			imagepanel = new JLabel(ic);

			// initialize the Buttons
			apply_path = new JButton("create Volume");
			browse_path = new JButton("browse");
			show_attributes = new JButton("Display all Attributes");
			addActionListerners(apply_path, browse_path, show_attributes);

			// Next some not editable TextFields
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

			current_path.setEditable(false);
			setfinalSize(current_path, new Dimension(220, 100));

			// creating the output field
			output.setEditable(false);
			setfinalSize(output, new Dimension(100, 1050));
			JScrollPane scroll = new JScrollPane(output);
			scroll.setPreferredSize(new Dimension(100, 100));
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			// creating the directory chooser
			chooser.setCurrentDirectory(new java.io.File("$HOME"));
			chooser.setDialogTitle("Search Path of Volume");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			// Putting everything on the left side together
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			setfinalSize(panel, new Dimension(650, 1100));
			Component[] panelstuff = {
					Box.createRigidArea(new Dimension(0, 10)), path, dir, img,
					att, scroll };
			addComponents(panel, panelstuff);

			// dir contains two buttons
			dir.setLayout(new GridLayout(1, 2, 20, 1));
			setfinalSize(dir, new Dimension(500, 1000));
			Component[] dirstuff = { browse_path, apply_path };
			addComponents(dir, dirstuff);

			// Search option Panel
			att.setLayout(new BoxLayout(att, BoxLayout.LINE_AXIS));
			setfinalSize(att, new Dimension(550, 400));
			Component[] attstuff = { show_attributes,
					Box.createRigidArea(new Dimension(10, 0)), search, filter };
			addComponents(att, attstuff);

			// "Slice:", actual slice and Max slice
			img.setLayout(new BoxLayout(img, BoxLayout.LINE_AXIS));
			setfinalSize(img, new Dimension(500, 500));
			Component[] imgstuff = { current_path,
					Box.createRigidArea(new Dimension(80, 0)), slice, index,
					max };
			addComponents(img, imgstuff);

			// Putting everything together now
			toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
			setfinalSize(toppanel, new Dimension(1100, 450));
			toppanel.add(panel);
			toppanel.add(imagepanel);

			// Some this stuff
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setfinalSize(this, new Dimension(1100, 450));
			this.add(toppanel);

			// Arrow input reaction: (dont work on all systems)
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

		/**
		 * Method for displaying all Attriubtes or display some choosen
		 * Attributes.
		 */
		private void displayAttributes() {
			try {
				// Do we have a Volume? If not we try to create one..
				if (vol == null) {
					createVolume();
				}
				// Did it work?
				if (vol != null) {
					// Is the user searching something or do we show them all?
					if (displayAll) {
						// getting the header of the actual slice
						output.setText(vol.getSlice(
								Integer.parseInt(index.getText())).getHeader());
					} else {
						// Simple: line for line - This line contains this
						// string?
						StringBuilder outputstring = new StringBuilder();
						for (String str : vol
								.getSlice(Integer.parseInt(index.getText()))
								.getHeader().split("\n")) {
							if (str.toLowerCase().contains(
									filter.getText().toLowerCase())) {
								outputstring.append(str + "\n");
							}
						}
						// And here comes the output
						output.setText(outputstring.toString());
					}
				}
			} catch (NumberFormatException e) {

			}
		}

		/**
		 * Method to try, to create a Volume to the given path.
		 */
		private void createVolume() {
			try {
				// speciall Constructur to catch the Exception
				vol = new Volume(path.getText(), this);
				// It worked
				output.setText("Volume created");
				// Saving the Path for the user
				current_path.setText("Volume: " + path.getText());

				// Displaying one image
				image.getGraphics()
						.drawImage(
								vol.getSlice(Integer.parseInt(index.getText()))
										.getData()
										.getScaledInstance(image.getWidth(),
												image.getHeight(),
												BufferedImage.SCALE_SMOOTH), 0,
								0, null);
				// Setting the max slice
				max.setText("/" + (vol.size() - 1));
				// And showing it
				repaint();
			} catch (RuntimeException ert) {
				// thrown by new Volume if it didit worked.
				output.setText("Creating Volume didnt work. Please check the path. (Maybe the Selected Folder is empty)");
			}
		}

		/**
		 * This method is called by the 3 buttons of VolumeTab.
		 */
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "create Volume": // try to initialize the volume
				createVolume();
				break;
			case "browse": // searching for a volume
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					path.setText(chooser.getSelectedFile().toString());
				}
				break;
			case "Display all Attributes": // forcing to display rly all
											// attributes
				displayAll = true;
				displayAttributes();
				break;
			default:
				break;
			}
		}

		/**
		 * This method is used by the GUI to find easily out, which Tab the user
		 * is using right now.
		 */
		public String getClassName() {
			return "VolumeTab";
		}
	}

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The tabber managing the Tabs.
	 */
	private JTabbedPane tabber;

	/**
	 * If there is no Window anymore, the forceEnd boolean can call
	 * System.exit(1) to rly force an End to all remaining Threads. If this GUI
	 * is just a part of another programm, than you should not force an End,
	 * because you would even kill the other Programm.
	 */
	private boolean forceEnd;

	/**
	 * Number of active windows.
	 */
	private static int windows = 0;

	/**
	 * Number of tabs that was created at all.
	 */
	private int tabint = 0;

	/**
	 * One and only Constructur.
	 */
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

	/**
	 * Method for creating a new Tab.
	 */
	private void newTab(JComponent comp) {
		// Max 9 Tabs
		if (tabber.getTabCount() >= 9) {
			return;
		}
		// Making the Counter Higher
		tabint++;

		// Title of the Tab: tabint + type of Tab
		String title = (tabint) + "";
		if (((MyTab) comp).getClassName() == "VolumeTab") {
			title += ": Volume";
		} else {
			title += ": Sorter";
		}
		tabber.addTab(title, comp);

		int index = tabber.indexOfTab(title);
		// The Panel that represents the Tab
		JPanel pnlTab = new JPanel();
		pnlTab.setLayout(new BoxLayout(pnlTab, BoxLayout.LINE_AXIS));
		pnlTab.setOpaque(false);
		// Title of the Tab
		JLabel lblTitle = new JLabel(title);
		// Close Button
		JButton btnClose = new JButton("x");
		btnClose.setMaximumSize(new Dimension(30, 15));
		btnClose.setMargin(new Insets(0, 0, 0, 0));

		// kinda changing the Layout of the Tabs
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

	/**
	 * Method for the Close Buttons in the Tab.
	 */
	class MyCloseActionHandler implements ActionListener {

		/**
		 * Important for the Tab, to find itself.
		 */
		private String tabName;

		public MyCloseActionHandler(String tabName) {
			this.tabName = tabName;
		}

		public String getTabName() {
			return tabName;
		}

		public void actionPerformed(ActionEvent evt) {
			// check if the is a Tab to close
			int index = tabber.indexOfTab(getTabName());
			if (index >= 0) {
				// focus another Tab, if there are enought
				if (tabber.getTabCount() > 1) {
					tabber.setSelectedIndex(index - 1);
				}

				// removing the Tab
				tabber.removeTabAt(index);

			}

		}

	}

	/**
	 * Method, which should force a choosen Size for a Component.
	 */
	private void setfinalSize(Component p, Dimension d) {
		p.setMinimumSize(d);
		p.setMaximumSize(d);
	}

	/**
	 * Life update method for VolumeTabs.
	 */
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
					Integer.parseInt(actual.index.getText());
				}catch(NumberFormatException e){
					actual.index.setText(lasttime_number+"");
				}
				
				try {
					// if this number is to high, i set it back
					if (Integer.parseInt(actual.index.getText()) >= actual.vol
							.size()
							&& Integer.parseInt(actual.index.getText()) != 0) {
							actual.index.setText("" + (actual.vol.size() - 1));	
					}
					// reacting to the changing index
					if (!lasttime_number.equals(actual.index.getText())) {
						if (!(Integer.parseInt(actual.index.getText()) >= actual.vol.size())){
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
												BufferedImage.SCALE_AREA_AVERAGING), 0,
								0, null);
						actual.repaint();
						}
					}
				} catch (NumberFormatException | NullPointerException e) {

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
				int next = Integer.parseInt(actual.index.getText()) + actual.change;
				if (next < actual.vol.size()){
				actual.index
						.setText(""+next);
				}
				actual.change = 0;
			}
		}
	}

	/**
	 * Method to perform a life update in the SorterTab.
	 */
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
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method, which is always running, to handle the lifeupdate of the tabs.
	 */
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

	/**
	 * Method to add a list of Components to a JPanel.
	 */
	private void addComponents(JPanel here, Component[] toadd) {
		for (int i = 0; i < toadd.length; i++) {
			here.add(toadd[i]);
		}
	}

	/**
	 * This Method is called by the JMenuBar and the Buttons inside of the
	 * VolumeTab.
	 */
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

	/**
	 * Method, which is called by new Threads, to make life updates in the tabs.
	 * Each ImageExtractor Window has his own Thread this way.
	 */
	public void run() {
		windows++;
		lifeupdate();
		if (--windows == 0 && forceEnd) {
			System.exit(1);
		}
	}
}
