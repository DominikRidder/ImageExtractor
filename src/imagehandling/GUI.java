package imagehandling;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.DragAndDrop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * This GUI is used, to look the Header and Images of Dicoms and to Search and
 * Sort Dicoms.
 * 
 * @author dridder_local
 *
 */
public class GUI extends JFrame implements ActionListener, Runnable {

	/**
	 * Main method, to start the GUI.
	 * 
	 * @param agrs
	 */
	public static void main(String[] agrs) {
		new GUI(true);
	}

	/**
	 * This is used by the inner classes of GUI (VolumeTab and SorterTab).
	 * 
	 * @author dridder_local
	 *
	 */
	interface MyTab {
		public String getClassName();

		public void lifeUpdate(JFrame parent);
	}

	/**
	 * Inner Class of GUI. Used to represent a Tab, which is usefull to sort
	 * Dicoms.
	 * 
	 * @author dridder_local
	 *
	 */
	class SorterTab extends JPanel implements ActionListener, MyTab, Runnable {
		/**
		 * Default serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The SortAlgorithm, which is used to perform the sort.
		 */
		private SortAlgorithm sortAlgorithm;

		/**
		 * The current Thread, which is using the sort method/algorithm.
		 */
		private Thread currentSortThread = null;

		/**
		 * The ByteArrayOutputStream, which catches the output made by the
		 * SortAlgorithm.
		 */
		private ByteArrayOutputStream sortListener;

		/**
		 * JTextArea output contains the output made by the SortAlgorithm.
		 */
		private JTextArea outputArea;

		/**
		 * JScrollPane, which adding the possibility for scrolling to the
		 * TextArea "output".
		 */
		private JScrollPane outputScroller;

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
		 * The JFileChooser is used to browse for a Volume dir.
		 */
		private JFileChooser fileChooser = new JFileChooser();

		/**
		 * JButton to call the sort() method and to cancel it again.
		 */
		private JButton startOrCancelSort;

		/**
		 * This String defines the tooltip test for the Image Digits column.
		 */
		private String image_digits_tooltip;

		/**
		 * This integer represents a option, of a JDialogPanel.
		 */
		private int option;

		/**
		 * This boolean is needed, because we using 2 run methods.
		 */
		private boolean sortingstarted;

		/**
		 * Current JOptionPane.
		 */
		private JFrame currentdialog;

		/**
		 * Default Constructur.
		 */
		public SorterTab(JFileChooser filechooser) {
			// Tool tip text's
			image_digits_tooltip = new String(
					"Set the Image Digits to 0, to not change the DICOM names.");

			// Adding tool tip's
			JTextField img_digits = createText("Image Digits", 100, 30, false);
			img_digits.setToolTipText(image_digits_tooltip);

			// Setting up the sortalgorithm with some default stuff
			sortAlgorithm = new SortAlgorithm();
			sortAlgorithm.setFilesOptionCopy();
			sortAlgorithm.setImgDigits(4);
			sortAlgorithm.setProtocolDigits(0);

			// chooser stuff
			this.fileChooser = filechooser;

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
			table_header_left.add(Box.createRigidArea(new Dimension(30, 30)));
			table_header_left.add(createText("Nifti", 50, 30, false));

			// Panel that contains the upper left rectangle
			JPanel upperleft = new JPanel();
			upperleft.setLayout(new BoxLayout(upperleft, BoxLayout.PAGE_AXIS));
			setfinalSize(upperleft, new Dimension(600, 250));
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
			startOrCancelSort = new JButton("Start Sort");
			startOrCancelSort.addActionListener(this);

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
			table_header_right.add(img_digits);
			table_header_right.add(Box.createRigidArea(new Dimension(29, 30)));

			// Panel that contains the upper right rectangle
			JPanel upperright = new JPanel();
			upperright
					.setLayout(new BoxLayout(upperright, BoxLayout.PAGE_AXIS));
			setfinalSize(upperright, new Dimension(500, 250));
			upperright.add(header_shifter_right);
			upperright.add(Box.createRigidArea(new Dimension(0, 10)));
			upperright.add(table_header_right);
			tablerows_right = new JPanel[5];
			for (int i = 0; i < tablerows_left.length; i++) {
				tablerows_right[i] = createOutputRow(i + 1);
				upperright.add(tablerows_right[i]);
			}
			upperright.add(startOrCancelSort);
			// -- upperright end

			// creating the output area
			outputArea = new JTextArea();
			outputArea.setEditable(false);
			outputArea.setMargin(new Insets(0,0,0,0));
			setfinalSize(outputArea, new Dimension(1050, 200));
			outputScroller = new JScrollPane(outputArea);
			setfinalSize(outputScroller, new Dimension(1100, 225));
			outputScroller
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			outputScroller.setPreferredSize(new Dimension(1100, 225));

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
			this.add(outputScroller);

			// Create a stream to hold the output
			sortListener = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(sortListener);
			sortAlgorithm.setPrintStream(ps);
		}

		/**
		 * This method creating a table row for the input table
		 */
		private JPanel createInputRow(int index) {
			// Button for searching a dir
			JButton browseButton = new JButton();
			// the number at the start is used to know, where the browsed
			// directory have to be set. The ":" is important for the splitt. At
			// the end you will just the a "..." in the button.
			browseButton.setText("...");
			browseButton.setMaximumSize(new Dimension(29, 27));
			browseButton.setPreferredSize(new Dimension(29, 27));
			browseButton.setMargin(new Insets(0,0,0,0));
			browseButton.addActionListener(this);

			JCheckBox tonifti = new JCheckBox();
			tonifti.setSize(29, 27);
			tonifti.addActionListener(this);

			// File transfer options
			String[] options = { "Copy", "Move" };
			JComboBox<String> jc = new JComboBox<String>(options);
			setfinalSize(jc, new Dimension(80, 28));

			JPanel rowPanel = new JPanel();
			rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.LINE_AXIS));
			// Status
			rowPanel.add(createText("Undefined", 100, 30, false));
			// Input dir
			rowPanel.add(createText("", 200, 30, true));
			// File transfer option
			rowPanel.add(jc);
			// Output Nr field
			rowPanel.add(createText("" + 1, 100, 30, true));
			// browse dir button
			rowPanel.add(browseButton);
			// to make it fit
			rowPanel.add(Box.createRigidArea(new Dimension(30, 30)));
			// option for niftis
			rowPanel.add(tonifti);
			return rowPanel;
		}

		/**
		 * This method creating a table row for the output table
		 */
		private JPanel createOutputRow(int index) {
			// Button for searching a dir
			JButton browseButton = new JButton();
			// the number at the start is used to know, where the browsed
			// directory have to be set. The ":" is important for the splitt. At
			// the end you will just the a "..." in the button.
			browseButton.setText("...");
			browseButton.setMaximumSize(new Dimension(29, 27));
			browseButton.setPreferredSize(new Dimension(29, 27));
			browseButton.setMargin(new Insets(0,0,0,0));
			browseButton.addActionListener(this);

			JPanel rowPanel = new JPanel();
			rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.LINE_AXIS));
			// index field
			rowPanel.add(createText("" + index, 50, 30, false));
			// output dir field
			rowPanel.add(createText("", 200, 30, true));
			// image digits field
			JTextField imgdigits = createText("0", 100, 30, true);
			imgdigits.setToolTipText(image_digits_tooltip);
			rowPanel.add(imgdigits);
			// browse dir button
			rowPanel.add(browseButton);
			return rowPanel;
		}

		/**
		 * Fast method for creating JTextField classes.
		 */
		private JTextField createText(String text, int width, int height,
				boolean editable) {
			JTextField textfield = new JTextField(text);
			setfinalSize(textfield, new Dimension(width, height));
			textfield.setEditable(editable);
			return textfield;
		}

		/**
		 * This method is called by the buttons of SorterTab.
		 */
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Cancel": // Stopping the sort safely
				sortAlgorithm.stopSort();
				break;
			case "Start Sort":
				// never starting two threads
				if (currentSortThread == null) {
					// New Thread for the Sort
					Thread t = new Thread(this);

					// Taking always a new baos to clear the output area
					sortListener = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(sortListener);
					sortAlgorithm.setPrintStream(ps);

					// Starting the sort
					t.start();
					currentSortThread = t;
					startOrCancelSort.setText("Cancel");
				}
				break;
			case "...":
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					boolean found = false;
					int i = 0;
					for (i = 0; i < 5; i++) {
						if (e.getSource().equals(
								tablerows_left[i].getComponent(4))) {
							found = true;
							break;
						}
					}
					if (found) {
						JTextField target = ((JTextField) tablerows_left[i]
								.getComponents()[1]);
						target.setText(fileChooser.getSelectedFile()
								.getAbsolutePath());
					} else {
						for (i = 0; i < 5; i++) {
							if (e.getSource().equals(
									tablerows_right[i].getComponent(3))) {
								found = true;
								break;
							}
						}
						if (found) {
							JTextField target = ((JTextField) tablerows_right[i]
									.getComponents()[1]);
							target.setText(fileChooser.getSelectedFile()
									.getAbsolutePath());
						}

					}
				}
				break;
			default:
				if (e.getSource() instanceof JCheckBox) {
					int i = 0;
					for (i = 0; i < 4; i++) {
						if (e.getSource().equals(
								tablerows_left[i].getComponent(6))) {
							break;
						}
					}
					JComboBox<String> target = ((JComboBox<String>) tablerows_left[i]
							.getComponents()[2]);
					if (target.isEnabled()) {
						target.setEnabled(false);
						target.setSelectedIndex(0);
					} else {
						target.setEnabled(true);
					}
				}
				break;
			}
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
			if (currentSortThread != null && !sortingstarted) {
				sortingstarted = true;
				sort();
				sortingstarted = false;
			} else {
				currentdialog = new JFrame();
				currentdialog.setLocationRelativeTo(this);
				// currentdialog.setVisible(true);
				option = JOptionPane
						.showConfirmDialog(
								currentdialog,
								"The Output Dir is empty. Should I sort the Dicoms to the Input folder?\nChoosing yes, setting the Image Digits to 0.\n(After 10 Seconds yes is picked automatically.)",
								"The Outputdir is empty",
								JOptionPane.YES_NO_OPTION);
			}
		}

		public void lifeUpdate(JFrame parent) {
			while (this.isVisible() && parent.isVisible()) {
				try {
					if (this.currentSortThread != null) {
						if (this.currentSortThread == null) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						this.outputArea.setText(this.sortListener.toString());
						int i = this.outputScroller.getVerticalScrollBar()
								.getMaximum();
						this.outputScroller.getVerticalScrollBar().setValue(i);
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
		 * This method is called by a Thread, to start the sorting.
		 */
		private void sort() {
			Color color_inProgress = Color.YELLOW;
			Color color_failed = Color.LIGHT_GRAY;
			Color color_sucess = Color.GREEN;

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
				status.setBackground(color_inProgress);
				JTextField inputfield = (JTextField) left_stuff[1];
				JTextField tooutput = (JTextField) left_stuff[3];
				@SuppressWarnings("unchecked")
				JComboBox<String> move = (JComboBox<String>) left_stuff[2];
				JCheckBox nifti = (JCheckBox) left_stuff[6];

				// catching empty input
				if (inputfield.getText().equals("")) {
					status.setBackground(color_failed);
					status.setText("Empty Input");
					continue;
				}

				// catching empty output nr
				if (tooutput.getText().equals("")) {
					status.setBackground(color_failed);
					status.setText("Index Missing");
				}

				// now getting the output information
				JTextField target = null;
				JTextField image_digits = null;
				if (tooutput.getText().equals("<-")) {
					target = inputfield;
					image_digits = new JTextField("0");
				} else {
					try {
						// Integer.parseInt may throws an exception
						Component[] right_stuff = tablerows_right[Integer
								.parseInt(tooutput.getText()) - 1]
								.getComponents();
						target = (JTextField) right_stuff[1];
						image_digits = (JTextField) right_stuff[2];
					} catch (IndexOutOfBoundsException | NumberFormatException e) {
						status.setBackground(color_failed);
						status.setText("Index Err");
						continue;
					}

					// No Outputdir is set
					if (target.getText().equals("")) {
						option = -2;
						new Thread(this).start();
						int time = 0;
						while (time++ < 10 && option == -2) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						currentdialog.dispose();

						if (option == -2) {
							option = 0;
						}

						if (option == -1) {
							option = 1;
						}

						if (option == 1) {
							status.setBackground(color_failed);
							status.setText("No Outp. Dir");
							continue;
						} else if (option == 0) {
							tooutput.setText("<-");
							target = inputfield;
							image_digits = new JTextField("0");
						}
					}
				}

				// setting the img digits and the boolean keepImageName in
				// SortAlgorithm
				try {
					int imgdigits = Integer.parseInt(image_digits.getText());
					if (imgdigits != 0) {
						sortAlgorithm.setImgDigits(imgdigits);
						sortAlgorithm.setKeepImageName(false);
					} else {
						sortAlgorithm.setKeepImageName(true);
					}
				} catch (NumberFormatException e) {
					status.setBackground(color_failed);
					status.setText("Err Img Digits");
					continue;
				}

				// setting the file transfer option
				String option = (String) move.getSelectedItem();
				if (nifti.isSelected()) {
					sortAlgorithm.setCreateNiftis(true);
					sortAlgorithm.setFilesOptionCopy();
				} else {
					if (option.equals("Move")) {
						sortAlgorithm.setCreateNiftis(false);
						sortAlgorithm.setFilesOptionMove();
					} else {
						sortAlgorithm.setCreateNiftis(false);
						sortAlgorithm.setFilesOptionCopy();
					}
				}

				// now the sortalgorithm can start
				status.setText("In Progress...");
				if (sortAlgorithm.searchAndSortIn(inputfield.getText(),
						target.getText())) {
					status.setText("Finished");
					status.setBackground(color_sucess);
				} else {
					if (sortAlgorithm.gotStopped()) {
						if (sortAlgorithm.getPermissionProblem()) {
							status.setBackground(color_failed);
							status.setText("Permission Err");
							continue;
						} else {
							status.setBackground(color_failed);
							status.setText("Canceled");
							break;
						}
					}
					status.setBackground(color_failed);
					status.setText("Input Dir Err");
				}
			}

			// To be sure, the user see the hole text.
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.outputArea.setText(this.sortListener.toString());
			int i = this.outputScroller.getVerticalScrollBar().getMaximum();
			this.outputScroller.getVerticalScrollBar().setValue(i);
			// things that have to be done, so the next sort can be called
			currentSortThread = null;
			startOrCancelSort.setText("Start Sort");
		}

	}

	/**
	 * This inner class representing a Tab in the GUI window, where you can look
	 * up the header and images of a Volume.
	 * 
	 * @author dridder_local
	 *
	 */
	class VolumeTab extends JPanel implements ActionListener, MyTab,
			ChangeListener, MouseWheelListener, KeyListener, Runnable {

		/**
		 * Default serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Volume, which is to get the header informations and the image.
		 */
		private Volume volume;

		/**
		 * Highest Panel is this Tab, which contains the leftSideStuff and the
		 * imagepanel.
		 */
		private JPanel toppanel;

		/**
		 * Panel, which contains the hole left side of this tab frame.
		 */
		private JPanel leftSidePanel;

		/**
		 * This TextField is used to create a Volume.
		 */
		private JTextField path;

		/**
		 * The JPanel dir is a row, which contains two JButtons (browse_path and
		 * apply_path).
		 */
		private JPanel volumePanel;

		/**
		 * JButton to show the hole header of a Volume.
		 */
		private JButton show_attributes;

		/**
		 * JButton to call the JFileChooser.
		 */
		private JButton browse_path;

		/**
		 * JButton to try, to create a Volume to a given path.
		 */
		private JButton open_imagej;

		/**
		 * JFileChooser is used to search a Volume (dir).
		 */
		private JFileChooser chooser;

		/**
		 * The JPanel img is a row, which contains a JTextfield with the Text
		 * "Slice:", the current slice and the maximum chooseable slice.
		 */
		private JPanel index_Panel;

		/**
		 * JPanel, that contains two arrows for the index_slice/index_echo
		 * field.
		 */
		private JPanel arrows_slice, arrows_echo;

		/**
		 * Arrow, for changing the index.
		 */
		private JButton arrow_up_slice, arrow_down_slice;

		/**
		 * The index field shows the current selected Volume slice.
		 */
		private JTextField index_slice;

		/**
		 * This field shows the number of slices in the volume (minus one).
		 */
		private JTextField max_slice;

		/**
		 * Arrows, for changing the index.
		 */
		private JButton arrow_up_echo, arrow_down_echo;

		/**
		 * This field is the current choosen echo.
		 */
		private JTextField index_echo;

		/**
		 * This field shows the maximum echo.
		 */
		private JTextField max_echo;

		/**
		 * This int is used to communicate between an ActionListener and this
		 * GUI. With the help of these things, the choosen slice (index) can be
		 * changed with the arrow keys.
		 */
		private int change_slice;

		/**
		 * Value for changing the index of the current echo.
		 */
		private int change_echo;

		/**
		 * Number of echos.
		 */
		private int echoNumbers;

		/**
		 * Number of slices per echo.
		 */
		private int perEcho;

		/**
		 * The acutal slice/echo as an int value.
		 */
		private int actual_slice, actual_echo;

		/**
		 * This JPanel contains a JTextField search with the text "Search:" and
		 * a textfield to search for Attributes.
		 */
		private JPanel attributeConfig;

		/**
		 * Value, which decides, whether the hole header of a dicom is shown or
		 * if only a searched part in shown.
		 */
		private boolean displayAll;

		/**
		 * The filter is used to search for Attributes in the header of an
		 * image.
		 */
		private JTextField filter;

		/**
		 * Header and co. output stuff, which is wrapped in the scroll object.
		 */
		private JTextArea outputArea;

		/**
		 * Image which is on the right side of the Tab.
		 */
		private BufferedImage image;

		/**
		 * The ImageIcon ic wrapping the image.
		 */
		private ImageIcon imgicon;

		/**
		 * The imagepanel is the container, which contains the ImageIcon ic.
		 */
		private JLabel imagelabel;

		/**
		 * True if a Thread creates a volume.
		 */
		private boolean creatingVolume;

		/**
		 * The Current ImageJ Frame
		 */
		private ImageJ imgj;

		/**
		 * Standard Constructur.
		 */
		public VolumeTab(JFileChooser filechooser) {
			// The path for the Volume
			path = new JTextField("");
			path.addKeyListener(this);
			path.setEditable(false);
			setfinalSize(path, new Dimension(500, 100));

			// The Attribute Filter
			filter = new JTextField("");
			filter.addKeyListener(this);
			setfinalSize(filter, new Dimension(500, 100));

			// image
			image = new BufferedImage(443, 443, BufferedImage.TYPE_BYTE_GRAY);
			// The ImageIcon is kinda a wrapper for the image
			imgicon = new ImageIcon(image);
			// imagepanel wrapps the ImageIcon
			imagelabel = new JLabel(imgicon);
			imagelabel.addMouseWheelListener(this);
			imagelabel.addKeyListener(this);

			// initialize the Buttons
			open_imagej = new JButton("open in Imagej");
			open_imagej.addActionListener(this);
			open_imagej.addKeyListener(this);

			browse_path = new JButton("browse");
			browse_path.addActionListener(this);
			browse_path.addKeyListener(this);

			show_attributes = new JButton("Display all Attributes");
			show_attributes.addActionListener(this);
			show_attributes.addKeyListener(this);
			setfinalSize(show_attributes, new Dimension(500, 100));

			arrow_up_slice = new BasicArrowButton(BasicArrowButton.NORTH);
			arrow_up_slice.setText("arrow_up_slice");
			arrow_up_slice.addChangeListener(this);

			arrow_down_slice = new BasicArrowButton(BasicArrowButton.SOUTH);
			arrow_down_slice.setText("arrow_down_slice");
			arrow_down_slice.addChangeListener(this);

			arrow_up_echo = new BasicArrowButton(BasicArrowButton.NORTH);
			arrow_up_echo.setText("arrow_up_echo");
			arrow_up_echo.addChangeListener(this);

			arrow_down_echo = new BasicArrowButton(BasicArrowButton.SOUTH);
			arrow_down_echo.setText("arrow_down_echo");
			arrow_down_echo.addChangeListener(this);

			// Next some not editable TextFields
			max_slice = new JTextField("/0");
			max_slice.setEditable(false);
			max_slice.setBorder(null);
			max_slice.addMouseWheelListener(this);
			setfinalSize(max_slice, new Dimension(75, 100));

			max_echo = new JTextField("/0");
			max_echo.setEditable(false);
			max_echo.setBorder(null);
			max_echo.addMouseWheelListener(this);
			setfinalSize(max_echo, new Dimension(75, 100));

			index_slice = new JTextField("0");
			index_slice.setEditable(false);
			index_slice.addMouseWheelListener(this);
			index_slice.addKeyListener(this);
			setfinalSize(index_slice, new Dimension(75, 100));

			index_echo = new JTextField("0");
			index_echo.setEditable(false);
			index_echo.addMouseWheelListener(this);
			index_echo.addKeyListener(this);
			setfinalSize(index_echo, new Dimension(75, 100));

			JTextField slice = new JTextField("Slice:");
			slice.setEditable(false);
			setfinalSize(slice, new Dimension(35, 100));
			slice.setBorder(null);
			slice.addMouseWheelListener(this);

			JTextField echo = new JTextField("Echo:");
			echo.setEditable(false);
			setfinalSize(echo, new Dimension(35, 100));
			echo.setBorder(null);
			echo.addMouseWheelListener(this);

			JTextField search = new JTextField("Search:");
			search.setEditable(false);
			setfinalSize(search, new Dimension(50, 100));
			search.setBorder(null);

			// creating 2 arrow panel
			arrows_slice = new JPanel();
			arrows_slice.setLayout(new BoxLayout(arrows_slice,
					BoxLayout.PAGE_AXIS));
			arrows_slice.add(arrow_up_slice);
			arrows_slice.add(arrow_down_slice);
			arrows_slice.addMouseWheelListener(this);
			setfinalSize(arrows_slice, new Dimension(20, 40));

			arrows_echo = new JPanel();
			arrows_echo.setLayout(new BoxLayout(arrows_echo,
					BoxLayout.PAGE_AXIS));
			arrows_echo.add(arrow_up_echo);
			arrows_echo.add(arrow_down_echo);
			arrows_echo.addMouseWheelListener(this);
			setfinalSize(arrows_echo, new Dimension(20, 40));

			// creating the output field
			outputArea = new JTextArea("status");
			outputArea.setEditable(false);
			setfinalSize(outputArea, new Dimension(100, 1050));
			JScrollPane outputScroller = new JScrollPane(outputArea);
			outputScroller.setPreferredSize(new Dimension(100, 100));
			outputScroller
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			outputScroller
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			// creating the directory chooser
			chooser = filechooser;

			// dir contains two buttons
			volumePanel = new JPanel();
			volumePanel.setLayout(new GridLayout(1, 2, 20, 1));
			setfinalSize(volumePanel, new Dimension(500, 1000));
			Component[] dirstuff = { browse_path, open_imagej };
			addComponents(volumePanel, dirstuff);

			// "Slice:", actual slice and Max slice
			index_Panel = new JPanel();
			index_Panel.setLayout(new BoxLayout(index_Panel,
					BoxLayout.LINE_AXIS));
			setfinalSize(index_Panel, new Dimension(500, 500));
			// current_path,Box.createRigidArea(new Dimension(80, 0)),
			Component[] imgstuff = { slice, arrows_slice, index_slice,
					max_slice, echo, arrows_echo, index_echo, max_echo };
			addComponents(index_Panel, imgstuff);

			// Search option Panel
			attributeConfig = new JPanel();
			attributeConfig.setLayout(new BoxLayout(attributeConfig,
					BoxLayout.LINE_AXIS));
			setfinalSize(attributeConfig, new Dimension(550, 400));
			Component[] attstuff = { show_attributes,
					Box.createRigidArea(new Dimension(10, 0)), search, filter };
			addComponents(attributeConfig, attstuff);

			// Putting everything on the left side together
			leftSidePanel = new JPanel();
			leftSidePanel.setLayout(new BoxLayout(leftSidePanel,
					BoxLayout.PAGE_AXIS));
			setfinalSize(leftSidePanel, new Dimension(650, 1100));
			Component[] panelstuff = {
					Box.createRigidArea(new Dimension(0, 10)), path,
					volumePanel, index_Panel, attributeConfig, outputScroller };
			addComponents(leftSidePanel, panelstuff);

			// Putting everything together now
			toppanel = new JPanel();
			toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
			setfinalSize(toppanel, new Dimension(1100, 450));
			toppanel.add(leftSidePanel);
			toppanel.add(imagelabel);

			// Some this stuff
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setfinalSize(this, new Dimension(1100, 450));
			this.add(toppanel);

			this.setVisible(true);
		}

		/**
		 * Method to try, to create a Volume to the given path.
		 */
		private void createVolume() {
			creatingVolume = true;
			try {
				// speciall Constructur which throws an Exception if new Volume
				// fails, instead of calling System.exit(1)
				volume = new Volume(path.getText(), this);

				// Default Index
				actual_slice = 1;
				actual_echo = 1;
				this.index_slice.setText("1");
				index_echo.setText("1");
				// User can change fields again
				index_slice.setEditable(true);
				index_echo.setEditable(true);

				// Getting some values
				volume.getTextOptions().setReturnExpression(
						TextOptions.ATTRIBUTE_VALUE + "");
				echoNumbers = Integer.parseInt(volume.getAttribute(
						KeyMap.KEY_ECHO_NUMBERS_S, volume.size() - 1));
				perEcho = volume.size() / echoNumbers;
				max_echo.setText("/" + echoNumbers);
				max_slice.setText("/" + perEcho);

				index_slice.requestFocus();
				displayAttributes();
				displayImage();
			} catch (RuntimeException ert) {
				// thrown by new Volume() if it didit worked.
				outputArea
						.setText("Creating Volume didnt work. Please check the path. (Maybe the Selected Folder is empty)");

				index_slice.setEditable(false);
				index_slice.setText("0");
				max_slice.setText("/0");

				index_echo.setEditable(false);
				index_echo.setText("0");
				max_echo.setText("/0");
			}
			creatingVolume = false;
		}

		/**
		 * Method for displaying all Attriubtes or display some choosen
		 * Attributes.
		 */
		private void displayAttributes() {
			try {
				// Do we have a Volume? If not we try to create one..
				if (volume == null) {
					createVolume();
				}
				// Did it work?
				if (volume != null) {
					// Is the user searching something or do we show them all?
					if (displayAll) {
						// getting the header of the actual slice
						String header = volume.getSlice(
								Integer.parseInt(index_slice.getText())
										- 1
										+ perEcho
										* (Integer.parseInt(index_echo
												.getText()) - 1)).getHeader();
						// The Document, which is used by the output is very
						// slow
						outputArea.setText(header);
					} else {
						// Simple: line for line - This line contains this
						// string?
						StringBuilder outputstring = new StringBuilder();
						for (String str : volume
								.getSlice(
										Integer.parseInt(index_slice.getText())
												- 1
												+ perEcho
												* (Integer.parseInt(index_echo
														.getText()) - 1))
								.getHeader().split("\n")) {
							if (str.toLowerCase().contains(
									filter.getText().toLowerCase())) {
								outputstring.append(str + "\n");
							}
						}
						// And here comes the output
						outputArea.setText(outputstring.toString());
					}
				}
			} catch (NumberFormatException e) {

			}
		}

		/**
		 * This method is called by the 5 buttons of VolumeTab.
		 */
		public void actionPerformed(ActionEvent e) {
			if (creatingVolume) {
				return;
			}
			switch (e.getActionCommand()) {
			case "open in Imagej":
				if (volume != null) {
					if (imgj == null || !imgj.isVisible()) {
						imgj = new ImageJ();
					}
					DragAndDrop dad = new DragAndDrop();
					dad.openFile(new File(path.getText()));
				}
				break;
			case "browse": // searching for a volume
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					if (chooser.getSelectedFile().isDirectory()) {
						path.setText(chooser.getSelectedFile().toString());
					} else if (chooser.getSelectedFile().isFile()) {
						path.setText(chooser.getSelectedFile().getParent()
								.toString());
					}
				}
				new Thread(this).start();
				break;
			case "Display all Attributes": // forcing to display really all
											// attributes
				displayAll = true;
				displayAttributes();
				break;
			default:
				break;
			}
		}

		public void stateChanged(ChangeEvent e) {
			if (arrow_up_slice.getModel().isPressed()) {
				change_slice = 1;
			}
			if (arrow_down_slice.getModel().isPressed()) {
				change_slice = -1;
			}
			if (arrow_up_echo.getModel().isPressed()) {
				change_echo = 1;
			}
			if (arrow_down_echo.getModel().isPressed()) {
				change_echo = -1;
			}
		}

		/**
		 * This method is used by the GUI to find easily out, which Tab the user
		 * is using right now.
		 */
		public String getClassName() {
			return "VolumeTab";
		}

		@Override
		public void lifeUpdate(JFrame parent) {
			String lasttime_echo = "0";
			String lasttime_slice = "0";
			String lasttime_filter = "";
			actual_slice = 0;
			actual_echo = 0;
			int status = 0;

			while (this.isVisible() && parent.isVisible()) {
				if (creatingVolume) {
					switch (status++) {
					case 0:
						outputArea.setText("Creating Volume.");
						break;
					case 1:
						outputArea.setText("Creating Volume..");
						break;
					case 2:
						outputArea.setText("Creating Volume...");
						break;
					default:
						break;
					}
					outputArea.repaint();
					status = status % 3;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
				// No Volume = nothing to do
				if (volume == null) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				if (!this.index_slice.getText().equals("")) {
					// index to high / index is not a Number
					try {
						actual_slice = Integer.parseInt(this.index_slice
								.getText());
						if (actual_slice > perEcho) {
							actual_slice = perEcho;
							index_slice.setText(perEcho + "");
						}
					} catch (NumberFormatException e) {
						// Not a number -> i dont accept the new text
						this.index_slice.setText(lasttime_slice + "");
						continue;
					}
					// something wanna change the slice over buttons/arrows?
					if (this.change_slice != 0) {
						// We wont accept a negativ slice
						if (!((actual_slice + this.change_slice) <= 0)) {
							// the next slice
							int next = actual_slice + this.change_slice;
							// perEcho is max for next
							if (next <= perEcho) {
								this.index_slice.setText("" + next);
							}
							// max 40 changes per second
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// one arrow still getting pressed?
						if (!(arrow_up_slice.getModel().isPressed() || arrow_down_slice
								.getModel().isPressed())
								| (change_slice < 0 && index_slice.getText()
										.equals("1"))) {
							this.change_slice = 0;
						}
					}
				}

				if (!this.index_echo.getText().equals("")) {
					// index to high / index is not a Number
					try {
						actual_echo = Integer.parseInt(this.index_echo
								.getText());
						if (actual_echo > echoNumbers) {
							actual_echo = echoNumbers;
							index_echo.setText(echoNumbers + "");
						}
					} catch (NumberFormatException e) {
						// Not a number -> i dont accept the new text
						this.index_echo.setText(lasttime_echo + "");
						continue;
					}
					// something wanna change the echo over buttons/arrows?
					if (this.change_echo != 0) {
						// We wont accept a negativ echo
						if (!((actual_echo + this.change_echo) <= 0)) {
							// the next echo
							int next = actual_echo + this.change_echo;
							// echoNumbers is max for next
							if (next <= echoNumbers) {
								this.index_echo.setText("" + next);
							}
							// max 40 changes per second
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// one arrow still getting pressed?
						if (!(arrow_up_echo.getModel().isPressed() || arrow_down_echo
								.getModel().isPressed())
								| (change_echo < 0 && index_echo.getText()
										.equals("1"))) {
							this.change_echo = 0;
						}
					}
				}

				// do we have a text in the indexes?
				if ((!this.index_slice.getText().equals(""))
						&& (!this.index_echo.getText().equals(""))) {
					try {
						// reacting to the changing index
						if (!lasttime_slice.equals(this.index_slice.getText())
								|| !lasttime_echo.equals(this.index_echo
										.getText())) {
							if (!(actual_slice > perEcho)
									&& !(actual_echo > echoNumbers)) {
								lasttime_echo = this.index_echo.getText();
								lasttime_slice = this.index_slice.getText();
								this.displayAttributes();
								this.displayImage();
								this.repaint();

							}
						}
					} catch (NumberFormatException | NullPointerException e) {

					}
				}
				// is there a filter?
				if (!this.filter.getText().equals("")) {
					// filter got changed?
					if (!lasttime_filter.equals(this.filter.getText())) {
						lasttime_filter = this.filter.getText();
						lasttime_slice = this.index_slice.getText();
						this.displayAll = false;
						this.displayAttributes();
						this.displayImage();
					}
				} else if (!lasttime_filter.equals(this.filter.getText())) {
					lasttime_filter = this.filter.getText();
					lasttime_slice = this.index_slice.getText();
					this.displayAll = true;
					this.displayAttributes();
					this.displayImage();
				}
			}
		}

		private int actualSliceIndex() {
			return actual_slice - 1 + perEcho * (actual_echo - 1);
		}

		private void displayImage() {
			this.image.getGraphics().drawImage(
					this.volume
							.getSlice(actualSliceIndex())
							.getData()
							.getScaledInstance(this.image.getWidth(),
									this.image.getHeight(),
									BufferedImage.SCALE_AREA_AVERAGING), 0, 0,
					null);
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			Object obj = e.getSource();
			int change = -1 * e.getWheelRotation();

			if (obj instanceof JTextField) {
				JTextField index = (JTextField) obj;
				if (index.equals(index_slice) || index.equals(max_slice)
						|| index.getText().equals("Slice:")) {
					change_slice += change;
				} else {
					change_echo += change;
				}
			} else if (obj instanceof JLabel) {
				JLabel img = (JLabel) obj;
				if (img.equals(imagelabel)) {
					if (index_echo.hasFocus()) {
						change_echo += change;
					} else {
						change_slice += change;
					}
				}
			} else if (obj instanceof JPanel) {
				JPanel arrow = (JPanel) obj;
				if (arrow.equals(arrows_slice)) {
					change_slice += change;
				} else {
					change_echo += change;
				}

			}

		}

		public void keyPressed(KeyEvent e) {
			int change = 0;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				change = 1;
				break;
			case KeyEvent.VK_DOWN:
				change = -1;
				break;
			default:
				return;
			}
			if (index_echo.hasFocus()) {
				change_echo += change;
			} else {
				change_slice += change;
			}
		}

		public void keyReleased(KeyEvent e) {

		}

		public void keyTyped(KeyEvent e) {

		}

		/**
		 * Creating Volumes this way, so the window wont freeze.
		 */
		public void run() {
			createVolume();
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
	 * System.exit(1) to really force an end to all remaining Threads. If this GUI
	 * is just a part of another programm, than you should not force an end,
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
	 * Filechooser for all Tabs.
	 */
	private JFileChooser filechooser;

	/**
	 * One and only Constructur.
	 */
	public GUI(boolean forceProgrammEndIfThereIsNoWindow) {
		filechooser = new JFileChooser();
		filechooser.setCurrentDirectory(new java.io.File("$HOME"));
		filechooser.setDialogTitle("Search Directory");
		filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		filechooser.setAcceptAllFileFilterUsed(false);

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
		newTab(new VolumeTab(filechooser));
		newTab(new SorterTab(filechooser));

		add(tabber);
		setLocationRelativeTo(null);
		setTitle("ImageExtractor");
		setfinalSize(this, new Dimension(1100, 550));
		setResizable(false);
		setVisible(true);

		new Thread(this).start();
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

	/**
	 * This Method is called by the JMenuBar and the Buttons inside of the
	 * VolumeTab.
	 */
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "new Volume Tab":
			newTab(new VolumeTab(filechooser));
			break;
		case "new Sort Tab":
			newTab(new SorterTab(filechooser));
			break;
		case "new Window":
			new GUI(forceEnd);
			break;
		default:
			break;
		}
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

			((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
					.lifeUpdate(this);
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
	 * Method to add a list of Components to a JPanel.
	 */
	private void addComponents(JPanel here, Component[] toadd) {
		for (int i = 0; i < toadd.length; i++) {
			here.add(toadd[i]);
		}
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
}
