package gui;

import imagehandling.SortAlgorithm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Tab of the GUI class. Used to represent a Tab, which is usefull to sort
 * Dicoms.
 * 
 * @author dridder_local
 *
 */
public class SorterTab extends JPanel implements ActionListener, MyTab,
		Runnable {
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
	 * JScrollPane, which adding the possibility for scrolling to the TextArea
	 * "output".
	 */
	private JScrollPane outputScroller;

	/**
	 * Array of JPanels, where each JPanel representing a row in the left table.
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
	 * This is the parent of this SorterTab istance.
	 */
	private GUI parent;

	/**
	 * Default Constructur.
	 */
	public SorterTab(JFileChooser filechooser, GUI gui) {
		parent = gui;

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
		JTextField upperleft_header = new JTextField("Search in and Sort to:");
		GUI.setfinalSize(upperleft_header, new Dimension(150, 30));
		upperleft_header.setEditable(false);
		upperleft_header.setBackground(null);
		upperleft_header.setBorder(null);

		// The header shifter is used to put the header to the left side,
		// instead of letting it in the middle of a pannel
		JPanel header_shifter_left = new JPanel();
		header_shifter_left.setLayout(new BoxLayout(header_shifter_left,
				BoxLayout.LINE_AXIS));
		header_shifter_left.add(upperleft_header);
		header_shifter_left.add(Box.createRigidArea(new Dimension(1000, 0)));

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
		GUI.setfinalSize(upperleft, new Dimension(600, 250));
		upperleft.add(header_shifter_left);
		upperleft.add(Box.createRigidArea(new Dimension(0, 10)));
		upperleft.add(table_header_left);

		tablerows_left = new JPanel[5];
		for (int i = 0; i < tablerows_left.length; i++) {
			tablerows_left[i] = createInputRow(i + 1);
			upperleft.add(tablerows_left[i]);
		}
		upperleft.add(Box.createRigidArea(new Dimension(0, 50)));
		// -- upperleft end

		// upperright rectangle
		JTextField upperright_header = new JTextField("Target Folder:");
		GUI.setfinalSize(upperright_header, new Dimension(175, 30));
		upperright_header.setEditable(false);
		upperright_header.setBackground(null);
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
		header_shifter_right.add(Box.createRigidArea(new Dimension(1000, 0)));

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
		upperright.setLayout(new BoxLayout(upperright, BoxLayout.PAGE_AXIS));
		GUI.setfinalSize(upperright, new Dimension(500, 250));
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
		outputArea.setMargin(new Insets(0, 0, 0, 0));
		GUI.setfinalSize(outputArea, new Dimension(1050, 200));
		outputScroller = new JScrollPane(outputArea);
		GUI.setfinalSize(outputScroller, new Dimension(1100, 225));
		outputScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outputScroller.setPreferredSize(new Dimension(1100, 225));

		// Seperates the left upper side from the right upper side
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		GUI.setfinalSize(separator, new Dimension(1, 250));

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
		browseButton.setMargin(new Insets(0, 0, 0, 0));
		browseButton.addActionListener(this);

		JCheckBox tonifti = new JCheckBox();
		tonifti.setSize(29, 27);
		tonifti.addActionListener(this);

		// File transfer options
		String[] options = { "Copy", "Move" };
		JComboBox<String> jc = new JComboBox<String>(options);
		GUI.setfinalSize(jc, new Dimension(80, 28));

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
		// at the end you will just the a "..." in the button.
		browseButton.setText("...");
		browseButton.setMaximumSize(new Dimension(29, 27));
		browseButton.setPreferredSize(new Dimension(29, 27));
		browseButton.setMargin(new Insets(0, 0, 0, 0));
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
		GUI.setfinalSize(textfield, new Dimension(width, height));
		textfield.setEditable(editable);
		if (!editable) {
			textfield.setBackground(null);
		}
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
			 if (fileChooser.showOpenDialog(this) ==
			 JFileChooser.APPROVE_OPTION) {
				boolean found = false;
				int i = 0;
				 File f = new File(fileChooser.getSelectedFile()
				 .getAbsolutePath());
				String path = f.isDirectory() ? f.getAbsolutePath() : f
						.getParent();
				for (i = 0; i < 5; i++) {
					if (e.getSource().equals(tablerows_left[i].getComponent(4))) {
						found = true;
						break;
					}
				}
				if (found) {
					JTextField targetIn = ((JTextField) tablerows_left[i]
							.getComponents()[1]);
					targetIn.setText(path);
				} else {
					for (i = 0; i < 5; i++) {
						if (e.getSource().equals(
								tablerows_right[i].getComponent(3))) {
							found = true;
							break;
						}
					}
				}
				if (found) {
					JTextField targetOut = ((JTextField) tablerows_right[i]
							.getComponents()[1]);
					targetOut.setText(path);
					if (e.getSource() instanceof JButton){
						GUI.setfinalSize(((JButton)e.getSource()),new Dimension(29, 27));
					}
				}
			}
			break;
		default:
			if (e.getSource() instanceof JCheckBox) {
				int j = 0;
				for (j = 0; j < 4; j++) {
					if (e.getSource().equals(tablerows_left[j].getComponent(6))) {
						break;
					}
				}
				JComboBox<String> target = ((JComboBox<String>) tablerows_left[j]
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
	 * This method is usefull for the GUI class to decide what kind of Tab is in
	 * the focus.
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
							"The Outputdir is empty", JOptionPane.YES_NO_OPTION);
		}
	}

	public void lifeUpdate() {
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
							.parseInt(tooutput.getText()) - 1].getComponents();
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