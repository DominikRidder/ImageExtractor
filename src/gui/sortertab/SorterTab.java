package gui.sortertab;

import gui.GUI;
import gui.MyTab;

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

import util.SortAlgorithm;

/**
 * Tab of the GUI class. Used to represent a Tab, which is usefull to sort
 * Dicoms. These Dicoms can also be converted and renamed while Sorting them,
 * using this Class. For more information take a look that the class
 * util.SortAlgorithm.
 * 
 * @author Dominik Ridder
 *
 */
public class SorterTab extends JPanel implements ActionListener, MyTab,
		Runnable {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This fields should make the Code more Readable and typesafe. These values
	 * are used to get and Cast the right element out of the left TableRows.
	 */
	private static final int L_STATUS = 0, L_INPUT = 1, L_OPTION = 2,
			L_OUTPUT_NR = 3, L_BROWSE = 4, L_NIFTI = 5;

	/**
	 * This fields should make the Code more Readable and typesafe. These values
	 * are used to get and Cast the right element out of the right TableRows.
	 */
	@SuppressWarnings("unused")
	private static final int R_STATUS = 0, R_OUTPUT = 1, R_IMAGE_DIGITS = 2,
			R_BROWSE = 3;

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
	 * Default Height of a row.
	 */
	private int rowheight;

	/**
	 * Default Constructur.
	 * 
	 * @param filechooser
	 *            The Filechooser, that is used to choose Directorys.
	 * @param gui
	 *            The Gui that contains the SorterTab.
	 */
	public SorterTab(JFileChooser filechooser, GUI gui) {
		parent = gui;
		rowheight = parent.height / 18;

		// Tool tip text's
		image_digits_tooltip = new String(
				"Set the Image Digits to 0, to not change the DICOM names.");

		// Adding tool tip's
		JTextField img_digits = createText("Image Digits", parent.width / 11,
				rowheight, false);
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
		GUI.setfinalSize(upperleft_header, new Dimension(parent.width / 7,
				rowheight));
		upperleft_header.setEditable(false);
		upperleft_header.setBackground(null);
		upperleft_header.setBorder(null);

		// The header shifter is used to put the header to the left side,
		// instead of letting it in the middle of a pannel
		JPanel header_shifter_left = new JPanel();
		header_shifter_left.setLayout(new BoxLayout(header_shifter_left,
				BoxLayout.LINE_AXIS));
		header_shifter_left.add(upperleft_header);
		header_shifter_left.add(Box.createRigidArea(new Dimension(
				(int) (parent.width / 1.1), 0)));

		// Setting the headline of the table, which should be as long as the
		// rows below it
		JPanel table_header_left = new JPanel();
		table_header_left.setLayout(new BoxLayout(table_header_left,
				BoxLayout.LINE_AXIS));
		table_header_left.add(createText("Status", parent.width / 11,
				rowheight, false));
		table_header_left.add(createText("Input Dir", (int) (parent.width / 7),
				rowheight, false));
		table_header_left.add(createText("Option",
				(int) (parent.width / 13.75), rowheight, false));
		table_header_left.add(createText("To Output Nr.", 3 * rowheight,
				rowheight, false));
		table_header_left.add(Box.createRigidArea(new Dimension(rowheight,
				rowheight)));
		table_header_left.add(createText("Nifti", rowheight, rowheight, false));

		GUI.setfinalSize(table_header_left, new Dimension(
				(int) (parent.width / 1.93), rowheight));

		// Panel that contains the upper left rectangle
		JPanel upperleft = new JPanel();
		upperleft.setLayout(new BoxLayout(upperleft, BoxLayout.PAGE_AXIS));
		upperleft.add(header_shifter_left);
		upperleft.add(Box.createRigidArea(new Dimension(0, parent.width / 54)));
		upperleft.add(table_header_left);

		tablerows_left = new JPanel[5];
		for (int i = 0; i < tablerows_left.length; i++) {
			tablerows_left[i] = createInputRow(i + 1);
			upperleft.add(tablerows_left[i]);
		}
		GUI.setfinalSize(upperleft, new Dimension(tablerows_left[0].getWidth(),
				(int) (parent.height / 2.16)));
		upperleft.add(Box.createRigidArea(new Dimension(0, (int) (rowheight))));
		// -- upperleft end

		// upperright rectangle
		JTextField upperright_header = new JTextField("Target Folder:");
		GUI.setfinalSize(upperright_header, new Dimension(
				(int) (parent.width / 6.29), rowheight));
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
		header_shifter_right.add(Box.createRigidArea(new Dimension(
				(int) (parent.width / 1.1), 0)));

		// Setting the headline of the table, which should be as long as the
		// rows below it
		JPanel table_header_right = new JPanel();
		table_header_right.setLayout(new BoxLayout(table_header_right,
				BoxLayout.LINE_AXIS));
		table_header_right.add(createText("Nr.", parent.width / 22, rowheight,
				false));
		table_header_right.add(createText("Output Dir",
				(int) (parent.width / 5.5), rowheight, false));
		table_header_right.add(img_digits);
		table_header_right.add(Box.createRigidArea(new Dimension(rowheight,
				rowheight)));

		// Panel that contains the upper right rectangle
		JPanel upperright = new JPanel();
		upperright.setLayout(new BoxLayout(upperright, BoxLayout.PAGE_AXIS));
		GUI.setfinalSize(upperright, new Dimension((int) (parent.width / 2.2),
				(int) (parent.height / 2.16)));
		upperright.add(header_shifter_right);
		upperright.add(Box
				.createRigidArea(new Dimension(0, parent.height / 54)));
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
		outputScroller = new JScrollPane(outputArea);
		GUI.setfinalSize(outputScroller, new Dimension(parent.width,
				(int) (parent.height / 2.9)));
		outputScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Seperates the left upper side from the right upper side
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		GUI.setfinalSize(separator, new Dimension(1,
				(int) (parent.height / 2.16)));

		// The panel over the output
		JPanel upper = new JPanel();
		upper.setLayout(new BoxLayout(upper, BoxLayout.LINE_AXIS));
		upper.add(upperleft);
		upper.add(Box.createRigidArea(new Dimension(rowheight, rowheight)));
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
		
		resetStatus();
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
				updateTextArea();
			}
			break;
		case "...":
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				boolean found = false;
				int i = 0;
				File f = new File(fileChooser.getSelectedFile()
						.getAbsolutePath());
				String path = f.isDirectory() ? f.getAbsolutePath() : f
						.getParent();
				for (i = 0; i < tablerows_left.length; i++) {
					if (e.getSource().equals(
							tablerows_left[i].getComponent(L_BROWSE))) {
						found = true;
						break;
					}
				}
				if (found) {
					JTextField targetIn = ((JTextField) tablerows_left[i]
							.getComponents()[L_INPUT]);
					targetIn.setText(path);
				} else {
					for (i = 0; i < 5; i++) {
						if (e.getSource().equals(
								tablerows_right[i].getComponent(R_BROWSE))) {
							found = true;
							break;
						}
					}
				}
				if (found) {
					JTextField targetOut = ((JTextField) tablerows_right[i]
							.getComponents()[R_OUTPUT]);
					targetOut.setText(path);
				}
			}

			parent.imec.setOption("LastBrowse", fileChooser
					.getCurrentDirectory().getAbsolutePath());
			parent.imec.save();
			break;
		default:
			if (e.getSource() instanceof JCheckBox) {
				int j = 0;
				for (j = 0; j < 4; j++) {
					if (e.getSource().equals(
							tablerows_left[j].getComponent(L_NIFTI))) {
						break;
					}
				}
				JComboBox<String> target = ((JComboBox<String>) tablerows_left[j]
						.getComponents()[L_OPTION]);
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
	 * Method that keep updating the TextArea, as long as needed.
	 */
	public void updateTextArea() {
		this.outputArea.setText(this.sortListener.toString());
		int i = this.outputScroller.getVerticalScrollBar().getMaximum();
		this.outputScroller.getVerticalScrollBar().setValue(i);
		this.repaint();

		// TODO: Using other way of updating, because this way you can produce a
		// MemoryLeak in Java!
		if (currentSortThread != null) {
			new java.util.Timer().schedule(new java.util.TimerTask() {
				public void run() {
					updateTextArea();
				}
			}, 200);
		}
	}

	/**
	 * This method is usefull for the GUI class to decide what kind of Tab is in
	 * the focus.
	 * 
	 * @return The Name of this Tab
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
			option = JOptionPane
					.showConfirmDialog(
							currentdialog,
							"The Output Dir is empty. Should I sort the Dicoms to the Input folder?\nChoosing yes, setting the Image Digits to 0.\n(After 10 Seconds yes is picked automatically.)",
							"The Outputdir is empty", JOptionPane.YES_NO_OPTION);
		}
	}

	/**
	 * Request the preferedWidth of this Tab in the Gui.
	 */
	public void neededSize() {
		parent.requestWidth(preferedWidth(), this);
	}

	/**
	 * The PreferedWith of this Tab.
	 * 
	 * @return the prefferedWidth of this Tab
	 */
	public int preferedWidth() {
		return parent.width;
	}

	@Override
	public void onFocus() {
		parent.requestWidth(preferedWidth(), this);
		parent.getJMenuBar().repaint();
	}

	@Override
	public void onExit() {

	}

	/**
	 * This method creating a table row for the input table
	 */
	private JPanel createInputRow(int index) {
		// Button for searching a dir
		JButton browseButton = new JButton();
		// the number at the start is used to know, where the browsed
		// directory have to be set. The ":" is important for the splitt. At
		// the end you will just see the a "..." in the button.
		browseButton.setText("...");
		GUI.setfinalSize(browseButton, new Dimension(rowheight, rowheight));
		browseButton.setMargin(new Insets(0, 0, 0, 0));
		browseButton.addActionListener(this);

		JCheckBox tonifti = new JCheckBox();
		GUI.setfinalSize(tonifti, new Dimension(rowheight, rowheight));
		tonifti.addActionListener(this);

		// File transfer options
		String[] options = { "Copy", "Move" };
		JComboBox<String> jc = new JComboBox<String>(options);
		GUI.setfinalSize(jc, new Dimension((int) (parent.width / 13.75),
				rowheight));

		/*
		 * private static final int L_STATUS = 0, L_INPUT = 1, L_OPTION = 2,
		 * L_OUTPUT_NR = 3, L_BROWSE = 4, L_NIFTI = 5;
		 */

		JPanel rowPanel = new JPanel();
		rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.LINE_AXIS));
		// Status
		rowPanel.add(createText("Undefined", parent.width / 11, rowheight, // L_STATUS
				false));
		// Input dir
		rowPanel.add(createText("", (int) (parent.width / 7), rowheight, true)); // L_INPUT
		// File transfer option
		rowPanel.add(jc); // L_OPTION
		// Output Nr field
		rowPanel.add(createText("" + 1, 3 * rowheight, rowheight, true)); // L_OUTPUT_NR
		// browse dir button
		rowPanel.add(browseButton); // L_BROWSE
		// to make it fit
		// option for niftis
		rowPanel.add(tonifti); // L_NIFTI

		int neededwidth = 0;
		for (Component c : rowPanel.getComponents()) {
			neededwidth += c.getWidth();
		}

		GUI.setfinalSize(rowPanel, new Dimension(neededwidth, rowheight));

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
		GUI.setfinalSize(browseButton, new Dimension(rowheight, rowheight));
		browseButton.setMargin(new Insets(0, 0, 0, 0));
		browseButton.addActionListener(this);

		JPanel rowPanel = new JPanel();
		rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.LINE_AXIS));
		// index field
		rowPanel.add(createText("" + index, parent.width / 22, rowheight, false));
		// output dir field
		rowPanel.add(createText("", (int) (parent.width / 5.5), rowheight, true));
		// image digits field
		JTextField imgdigits = createText("0", parent.width / 11, rowheight,
				true);
		imgdigits.setToolTipText(image_digits_tooltip);
		rowPanel.add(imgdigits);
		// browse dir button
		rowPanel.add(browseButton);
		GUI.setfinalSize(rowPanel, new Dimension((int) (parent.width / 11)
				+ (int) (parent.width / 5.5) + (int) (parent.width / 22)
				+ rowheight, rowheight));
		return rowPanel;
	}

	/**
	 * Fast method for creating JTextField classes.
	 */
	private static JTextField createText(String text, int width, int height,
			boolean editable) {
		JTextField textfield = new GradientTextField(text);
		textfield.setEditable(editable);
		if (!editable) {
			textfield.setBackground(null);
		}
		GUI.setfinalSize(textfield, new Dimension(width, height));
		return textfield;
	}

	private void resetStatus() {
		for (int i = 0; i < tablerows_left.length; i++) {
			Component[] left_stuff = tablerows_left[i].getComponents();
			GradientTextField status = (GradientTextField) left_stuff[L_STATUS];
			status.setBackground(new Color(0, 0, 0, 0));
			status.setGradientColors(Color.LIGHT_GRAY.brighter(), Color.LIGHT_GRAY);
			status.setText("Undefined");
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
			GradientTextField status = (GradientTextField) left_stuff[L_STATUS];
			status.setBackground(new Color(0, 0, 0, 0));
			status.setGradientColors(color_inProgress, color_inProgress.darker());
			status.setText("Unchecked");
		}

		// for every input row
		for (int i = 0; i < tablerows_left.length; i++) {
			// getting the components and casting them
			Component[] left_stuff = tablerows_left[i].getComponents();
			GradientTextField status = (GradientTextField) left_stuff[L_STATUS];
			status.setBackground(new Color(0, 0, 0, 0));
			status.setGradientColors(color_inProgress, color_inProgress.darker());
			
			JTextField inputfield = (JTextField) left_stuff[L_INPUT];
			JTextField tooutput = (JTextField) left_stuff[L_OUTPUT_NR];
			@SuppressWarnings("unchecked")
			JComboBox<String> move = (JComboBox<String>) left_stuff[L_OPTION];
			JCheckBox nifti = (JCheckBox) left_stuff[L_NIFTI];

			// catching empty input
			if (inputfield.getText().equals("")) {
				//status.setBackground(color_failed);
				status.setGradientColors(color_failed.brighter(), color_failed);
				status.setText("Empty Input");
				continue;
			}

			// catching empty output nr
			if (tooutput.getText().equals("")) {
				//status.setBackground(color_failed);
				status.setGradientColors(color_failed.brighter(), color_failed);
				status.setText("Index Missing");
				continue;
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
					//status.setBackground(color_failed);
					status.setGradientColors(color_failed.brighter(), color_failed);
					status.setText("Index Error");
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
						//status.setBackground(color_failed);
						status.setGradientColors(color_failed.brighter(), color_failed);
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
				//status.setBackground(color_failed);
				status.setGradientColors(color_failed.brighter(), color_failed);
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
				//status.setBackground(color_sucess);
				status.setGradientColors(color_sucess, color_sucess.darker());
			} else {
				if (sortAlgorithm.gotStopped()) {
					if (sortAlgorithm.getPermissionProblem()) {
						//status.setBackground(color_failed);
						status.setGradientColors(color_failed.brighter(), color_failed);
						status.setText("Permission Err");
						continue;
					} else {
						//status.setBackground(color_failed);
						status.setGradientColors(color_failed.brighter(), color_failed);
						status.setText("Canceled");
						for (int j = i+1; j < tablerows_left.length; j++) {
							left_stuff = tablerows_left[j].getComponents();
							status = (GradientTextField) left_stuff[L_STATUS];
							status.setBackground(new Color(0, 0, 0, 0));
							status.setGradientColors(color_failed.brighter(), color_failed);
							status.setText("Canceled");
						}
						break;
					}
				}
				//status.setBackground(color_failed);
				status.setGradientColors(color_failed.brighter(), color_failed);
				status.setText("Input Dir Error");
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