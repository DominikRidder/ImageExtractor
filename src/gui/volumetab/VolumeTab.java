package gui.volumetab;

import gui.GUI;
import gui.MyTab;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.Roi;
import imagehandling.Image;
import imagehandling.KeyMap;
import imagehandling.TextOptions;
import imagehandling.Volume;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import sun.java2d.pipe.DrawImage;
import tools.ImageExtractorConfig;
import tools.VolumeFitter;
import tools.ZeroEcho;

import com.sun.javafx.geom.transform.SingularMatrixException;

/**
 * This class representing a Tab in the GUI window, where you can look up the
 * header and images of a Volume.
 * 
 * @author dridder_local
 *
 */
public class VolumeTab extends JPanel implements ActionListener, MyTab,
		ChangeListener, MouseWheelListener, MouseListener, KeyListener,
		Runnable, CaretListener {

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
	 * JPanel, that contains two arrows for the index_slice/index_echo field.
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
	 * This int is used to communicate between an ActionListener and this GUI.
	 * With the help of these things, the choosen slice (index) can be changed
	 * with the arrow keys.
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
	 * This JPanel contains a JTextField search with the text "Search:" and a
	 * textfield to search for Attributes.
	 */
	private JPanel attributeConfig;

	/**
	 * Value, which decides, whether the hole header of a dicom is shown or if
	 * only a searched part in shown.
	 */
	private boolean displayAll;

	/**
	 * The filter is used to search for Attributes in the header of an image.
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
	 * The parent is the actual window, that created this VolumeTab.
	 */
	private GUI parent;

	/**
	 * This boolean is important to know, if this tab was in an extended state
	 * or not.
	 */
	private Boolean ownExtended = false;

	private JPanel roiPanel;

	private BufferedImage roiimage;

	private ImageIcon roiimgicon;

	private JLabel roilabel;

	private Roi relativroi;

	private JCheckBox alsolog;

	private RotatePanel leg_gray;

	private VolumeFitter vf;

	private JComboBox<String> dimension;

	private JScrollPane outputScroller;

	private String lastfilter = "";

	private int creatingTextStatus = 0;

	private JComboBox<String> shape;
	/**
	 * Standard Constructur.
	 */
	public VolumeTab(JFileChooser filechooser, GUI gui) {
		parent = gui;

		// ImageExtractorConfig iec = new ImageExtractorConfig();
		// customImagej.

		// The path for the Volume
		path = new JTextField("");
		path.addKeyListener(this);
		path.setEditable(false);
		path.setBackground(null);
		GUI.setfinalSize(path, new Dimension(500, 100));

		// The Attribute Filter
		filter = new JTextField("");
		filter.addCaretListener(this);
		GUI.setfinalSize(filter, new Dimension(500, 100));

		String[] shapes = { "Point", "Circle" };
		shape = new JComboBox<String>(shapes);
		GUI.setfinalSize(shape, new Dimension(70, 30));

		// image
		image = new BufferedImage(443, 443, BufferedImage.TYPE_4BYTE_ABGR);
		// The ImageIcon is kinda a wrapper for the image
		imgicon = new ImageIcon(image);
		// imagepanel wrapps the ImageIcon
		imagelabel = new JLabel(imgicon);
		GUI.setfinalSize(imagelabel, new Dimension(443, 443));
		imagelabel.addMouseWheelListener(this);
		imagelabel.addKeyListener(this);
		imagelabel.addMouseListener(this);

		// initialize the Buttons
		open_imagej = new JButton("open in External");
		open_imagej.addActionListener(this);
		open_imagej.addKeyListener(this);

		browse_path = new JButton("browse");
		browse_path.addActionListener(this);
		browse_path.addKeyListener(this);

		show_attributes = new JButton("Display all Attributes");
		show_attributes.addActionListener(this);
		show_attributes.addKeyListener(this);
		GUI.setfinalSize(show_attributes, new Dimension(500, 100));

		arrow_up_slice = new BasicArrowButton(BasicArrowButton.NORTH);
		arrow_up_slice.setText("arrow_up_slice@1");
		arrow_up_slice.addChangeListener(this);

		arrow_down_slice = new BasicArrowButton(BasicArrowButton.SOUTH);
		arrow_down_slice.setText("arrow_down_slice@-1");
		arrow_down_slice.addChangeListener(this);

		arrow_up_echo = new BasicArrowButton(BasicArrowButton.NORTH);
		arrow_up_echo.setText("arrow_up_echo@1");
		arrow_up_echo.addChangeListener(this);

		arrow_down_echo = new BasicArrowButton(BasicArrowButton.SOUTH);
		arrow_down_echo.setText("arrow_down_echo@-1");
		arrow_down_echo.addChangeListener(this);

		// Next some not editable TextFields
		max_slice = new JTextField("/0");
		max_slice.setEditable(false);
		max_slice.setBackground(null);
		max_slice.setBorder(null);
		max_slice.addMouseWheelListener(this);
		GUI.setfinalSize(max_slice, new Dimension(75, 100));

		max_echo = new JTextField("/0");
		max_echo.setEditable(false);
		max_echo.setBackground(null);
		max_echo.setBorder(null);
		max_echo.addMouseWheelListener(this);
		GUI.setfinalSize(max_echo, new Dimension(75, 100));

		index_slice = new JTextField("0");
		index_slice.setEditable(false);
		index_slice.addMouseWheelListener(this);
		index_slice.addKeyListener(this);
		index_slice.addCaretListener(this);
		GUI.setfinalSize(index_slice, new Dimension(75, 100));

		index_echo = new JTextField("0");
		index_echo.setEditable(false);
		index_echo.addMouseWheelListener(this);
		index_echo.addKeyListener(this);
		GUI.setfinalSize(index_echo, new Dimension(75, 100));

		JTextField slice = new JTextField("Slice:");
		slice.setEditable(false);
		slice.setBackground(null);
		GUI.setfinalSize(slice, new Dimension(35, 100));
		slice.setBorder(null);
		slice.addMouseWheelListener(this);

		JTextField echo = new JTextField("Echo:");
		echo.setEditable(false);
		echo.setBackground(null);
		GUI.setfinalSize(echo, new Dimension(35, 100));
		echo.setBorder(null);
		echo.addMouseWheelListener(this);

		JTextField search = new JTextField("Search:");
		search.setEditable(false);
		search.setBackground(null);
		GUI.setfinalSize(search, new Dimension(50, 100));
		search.setBorder(null);

		// creating 2 arrow panel
		arrows_slice = new JPanel();
		arrows_slice
				.setLayout(new BoxLayout(arrows_slice, BoxLayout.PAGE_AXIS));
		arrows_slice.add(arrow_up_slice);
		arrows_slice.add(arrow_down_slice);
		arrows_slice.addMouseWheelListener(this);
		GUI.setfinalSize(arrows_slice, new Dimension(20, 40));

		arrows_echo = new JPanel();
		arrows_echo.setLayout(new BoxLayout(arrows_echo, BoxLayout.PAGE_AXIS));
		arrows_echo.add(arrow_up_echo);
		arrows_echo.add(arrow_down_echo);
		arrows_echo.addMouseWheelListener(this);
		GUI.setfinalSize(arrows_echo, new Dimension(20, 40));

		// creating the output field
		outputArea = new JTextArea("status");
		outputArea.setEditable(false);
		GUI.setfinalSize(outputArea, new Dimension(100, 1050));
		outputScroller = new JScrollPane(outputArea);
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
		GUI.setfinalSize(volumePanel, new Dimension(500, 1000));
		Component[] dirstuff = { browse_path, open_imagej };
		GUI.addComponents(volumePanel, dirstuff);

		// "Slice:", actual slice and Max slice
		index_Panel = new JPanel();
		index_Panel.setLayout(new BoxLayout(index_Panel, BoxLayout.LINE_AXIS));
		GUI.setfinalSize(index_Panel, new Dimension(500, 500));
		// current_path,Box.createRigidArea(new Dimension(80, 0)),
		Component[] imgstuff = { slice, arrows_slice, index_slice, max_slice,
				echo, arrows_echo, index_echo, max_echo };
		GUI.addComponents(index_Panel, imgstuff);

		// Search option Panel
		attributeConfig = new JPanel();
		attributeConfig.setLayout(new BoxLayout(attributeConfig,
				BoxLayout.LINE_AXIS));
		GUI.setfinalSize(attributeConfig, new Dimension(550, 400));
		Component[] attstuff = { show_attributes,
				Box.createRigidArea(new Dimension(10, 0)), search, filter };
		GUI.addComponents(attributeConfig, attstuff);

		// Putting everything on the left side together
		leftSidePanel = new JPanel();
		leftSidePanel.setLayout(new BoxLayout(leftSidePanel,
				BoxLayout.PAGE_AXIS));
		GUI.setfinalSize(leftSidePanel, new Dimension(650, 1100));
		Component[] panelstuff = { Box.createRigidArea(new Dimension(0, 10)),
				path, volumePanel, index_Panel, attributeConfig, outputScroller };
		GUI.addComponents(leftSidePanel, panelstuff);

		// Graph legend
		JTextField leg_echo = new JTextField("Echo Nr.");
		leg_echo.setEditable(false);
		leg_echo.setBackground(null);
		leg_echo.setBorder(null);
		GUI.setfinalSize(leg_echo, new Dimension(100, 50));

		// Graph legend
		leg_gray = new RotatePanel("Grayscale");
		leg_gray.setBorder(null);
		leg_gray.setBackground(null);
		GUI.setfinalSize(leg_gray, new Dimension(20, 100));
		leg_gray.setVisible(false);

		// log Checkbox + text
		JLabel legWrapper = new JLabel();
		legWrapper.setLayout(new BoxLayout(legWrapper, BoxLayout.LINE_AXIS));
		legWrapper.add(Box.createRigidArea(new Dimension(120, 0)));
		legWrapper.add(leg_echo);
		GUI.setfinalSize(legWrapper, new Dimension(300, 100));

		JTextField text_dim = new JTextField("Fitting: ");
		text_dim.setEditable(false);
		text_dim.setBackground(null);
		text_dim.setBorder(null);
		GUI.setfinalSize(text_dim, new Dimension(70, 50));

		dimension = new JComboBox<String>();
		String help[] = { "", "st", "nd", "rd", "th", "th" };
		for (int i = 0; i < 5; i++) {
			dimension.addItem("Polynomial (" + i + help[i] + " order)");
		}
		dimension.addItem("Polynomial (n order)");
		dimension.addItem("Exponential");
		dimension.setSelectedIndex(3);
		dimension.addActionListener(this);
		GUI.setfinalSize(dimension, new Dimension(200, 50));

		JLabel dimselection = new JLabel();
		dimselection
				.setLayout(new BoxLayout(dimselection, BoxLayout.LINE_AXIS));
		dimselection.add(text_dim);
		dimselection.add(dimension);
		GUI.setfinalSize(dimselection, new Dimension(270, 50));

		// image
		roiimage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
		// The ImageIcon is kinda a wrapper for the image
		roiimgicon = new ImageIcon(roiimage);
		// imagepanel wrapps the ImageIcon
		roilabel = new JLabel(roiimgicon);

		// Checkbox for showing the log evaluation
		alsolog = new JCheckBox();
		alsolog.addChangeListener(this);

		// Log Checkbox text
		JTextField logtext = new JTextField("log");
		logtext.setEditable(false);
		logtext.setBackground(null);
		logtext.setBorder(null);
		GUI.setfinalSize(logtext, new Dimension(200, 100));

//		// Calc zero echo
//		JButton zero_echo = new JButton("calc Zero Echo");
//		zero_echo.addActionListener(this);
//		GUI.setfinalSize(zero_echo, new Dimension(200, 50));

		// log Checkbox + text
		JLabel loglabel = new JLabel();
		loglabel.setLayout(new BoxLayout(loglabel, BoxLayout.LINE_AXIS));
		loglabel.add(alsolog);
		loglabel.add(logtext);
		loglabel.add(shape);
//		loglabel.add(zero_echo);
		GUI.setfinalSize(loglabel, new Dimension(300, 100));

		// Putting the roi Panel together
		roiPanel = new JPanel();
		roiPanel.setLayout(new BoxLayout(roiPanel, BoxLayout.PAGE_AXIS));
		GUI.setfinalSize(roiPanel, new Dimension(400, 1100));
		Component[] roistuff = { Box.createRigidArea(new Dimension(0, 5)),
				dimselection, Box.createRigidArea(new Dimension(0, 5)),
				roilabel, /* legWrapper, */
				Box.createRigidArea(new Dimension(0, 50)), loglabel };
		GUI.addComponents(roiPanel, roistuff);
		roiPanel.setVisible(false);

		// Putting everything together now
		toppanel = new JPanel();
		toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
		GUI.setfinalSize(toppanel, new Dimension(1100, 450));
		toppanel.add(leftSidePanel);
		toppanel.add(imagelabel);
		// toppanel.add(Box.createRigidArea(new Dimension(35, 0)));
		// toppanel.add(leg_gray);
		toppanel.add(Box.createRigidArea(new Dimension(10, 0)));
		toppanel.add(roiPanel);

		// Some this stuff
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		GUI.setfinalSize(this, new Dimension(1100, 450));
		this.add(toppanel);

		this.setVisible(true);
	}

	/**
	 * Method to try, to create a Volume to the given path.
	 */
	public void createVolume() {
		creatingVolume = true;
		try {
			// speciall Constructur which throws an Exception if new Volume
			// fails, instead of calling System.exit(1)
			creatingText();
			volume = new Volume(path.getText(), this);
		} catch (RuntimeException ert) {
			// thrown by new Volume() if it didit worked.
			index_slice.setEditable(false);
			index_slice.setText("0");
			max_slice.setText("/0");

			index_echo.setEditable(false);
			index_echo.setText("0");
			max_echo.setText("/0");
		}
		creatingVolume = false;
	}

	private void creatingText() {
		new java.util.Timer().schedule(new java.util.TimerTask() {
			public void run() {
				switch (creatingTextStatus) {
				case 0:
					outputArea.setText("Creating");
					break;
				case 1:
					outputArea.setText("Creating.");
					break;
				case 2:
					outputArea.setText("Creating..");
					break;
				case 3:
					outputArea.setText("Creating...");
					break;
				}
				creatingTextStatus += 1;
				creatingTextStatus %= 4;
				repaint();
				if (creatingVolume) {
					creatingText();
				} else if (volume != null) {
					// Default Index
					index_slice.setText("1");
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
					showROI(false);
					displayAttributes();
					while (!displayImage()) {
						System.out.println("not worked");
					}
				}else{
					outputArea
					.setText("Creating Volume didnt work. Please check the path. (Maybe the Selected Folder is empty)");
				}
			}
		}, 500);
	}

	public void setPath(String path) {
		this.path.setText(path);
	}

	public boolean isCreatingVolume() {
		return creatingVolume;
	}

	/**
	 * Method for displaying all Attriubtes or display some choosen Attributes.
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
					String header = volume.getSlice(actualSliceIndex())
							.getHeader();
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
		} catch (NumberFormatException | NullPointerException e) {

		}
	}

	public void sleep(int milisec) {
		try {
			Thread.sleep(milisec);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
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
		case "calc Zero Echo":
			actionCalculateZeroEcho();
			break;
		case "open in External":
			actionOpenInExternal();
			break;
		case "browse": // searching for a volume
			actionBrowse();
			break;
		case "Display all Attributes":
			actionDisplayAttributes();
			break;
		default:
			if (e.getSource() == dimension) {
				if (relativroi != null) {
					showROI(true);
				}
			}
			break;
		}
	}

	public void actionCalculateZeroEcho() {
		ZeroEcho ze = new ZeroEcho(volume);
		new Thread(ze).start();
		while (ze.isrunning) {
			sleep(1000);
		}
		drawIntoImage(image, ze.echo0.get(0));
		repaint();
	}

	public void actionBrowse() {
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
				&& chooser.getSelectedFile() != null) {
			if (chooser.getSelectedFile().isDirectory()) {
				path.setText(chooser.getSelectedFile().toString());
			} else if (chooser.getSelectedFile().isFile()) {
				if (!chooser.getSelectedFile().getAbsolutePath()
						.endsWith("nii")) {
					System.out.println(chooser.getSelectedFile().getAbsolutePath());
					path.setText(chooser.getSelectedFile().getParent()
							.toString());
				}
			}
			new Thread(this).start();
		}
	}

	public void actionDisplayAttributes() {
		displayAll = true;
		displayAttributes();
	}

	public void actionOpenInExternal() {
		if (volume != null) {
			parent.imec = new ImageExtractorConfig();
			String customExternal = parent.imec.getCustomExternal();
			ProcessBuilder pb;

			String commands[] = splittCommand(customExternal.replace("$FILE", path.getText()));
			pb = new ProcessBuilder(commands);

			System.out.println("Try to execute:\n");
			for (String command : commands) {
				System.out.print(command + " ");
			}
			System.out.println();

			try {
				pb.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public String[] splittCommand(String command){
		ArrayList<String> commands = new ArrayList<String>();
		String[] forreturn = new String[0];
		
		boolean ignore = false;
		
		StringBuilder next = new StringBuilder();
		for (char c : command.toCharArray()){
			if (c == '\''){
				ignore = !ignore;
			}else if (c == ' ' && !ignore){
				commands.add(next.toString());
				next.delete(0, next.length());
			}else{
				next.append(c);
			}
		}
		
		if (next.length() != 0){
			commands.add(next.toString());
		}
		
		return commands.toArray(forreturn);
	}

	private void checkSlice() {
		Runnable handlechange = new Runnable() {
			public void run() {
				int act = 0;
				try {
					act = Integer.parseInt(index_slice.getText());
				} catch (NumberFormatException e) {
					String newtext = index_slice.getText().replaceAll("[^\\d]",
							"");
					if (newtext.length() == 0) {
						act = 0;
					} else {
						act = Integer.parseInt(newtext);
					}
				}
				if (act > perEcho) {
					act = perEcho;
				} else if (act < 1 && volume != null) {
					act = 1;
				}
				CaretListener[] listener = index_slice.getCaretListeners();
				if (listener.length != 0) {
					for (CaretListener l: listener){
						index_slice.removeCaretListener(l);
					}
					index_slice.setText(act + "");
					for (CaretListener l: listener){
						index_slice.addCaretListener(l);
					}
				}
			}
		};

		SwingUtilities.invokeLater(handlechange);
	}

	private void checkEcho() {
		Runnable handlechange = new Runnable() {
			public void run() {
				int act = 0;
				try {
					act = Integer.parseInt(index_echo.getText());
				} catch (NumberFormatException e) {
					String newtext = index_echo.getText().replaceAll("[^\\d]",
							"");
					if (newtext.length() == 0) {
						act = 0;
					} else {
						act = Integer.parseInt(newtext);
					}
				}
				if (act > echoNumbers) {
					act = echoNumbers;
				} else if (act < 1 && volume != null) {
					act = 1;
				}
				CaretListener[] listener = index_echo.getCaretListeners();
				if (listener.length != 0) {
					for (CaretListener l: listener){
						index_echo.removeCaretListener(l);
					}
					index_echo.setText(act + "");
					for (CaretListener l: listener){
						index_echo.addCaretListener(l);
					}
				}
			}
		};

		SwingUtilities.invokeLater(handlechange);
	}

	public int getActualSlice() {
		return Integer.parseInt(index_slice.getText());
	}

	public int getActualEcho() {
		return Integer.parseInt(index_echo.getText());
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton b = (JButton) e.getSource();
			String name = b.getText();
			if (name.contains("arrow")) {
				whilePressed();
			}
		} else if (e.getSource() == alsolog) {
			showROI(true);
		}
	}

	public void whilePressed() {
		boolean slice = true;
		boolean pressed = true;
		if (arrow_up_slice.getModel().isPressed()) {
			index_slice.setText("" + (getActualSlice() + 1));
		} else if (arrow_down_slice.getModel().isPressed()) {
			index_slice.setText("" + (getActualSlice() - 1));
		} else if (arrow_up_echo.getModel().isPressed()) {
			slice = false;
			index_echo.setText("" + (getActualEcho() + 1));
		} else if (arrow_down_echo.getModel().isPressed()) {
			slice = false;
			index_echo.setText("" + (getActualEcho() - 1));
		} else {
			pressed = false;
		}

		if (slice) {
			checkSlice();
		} else {
			checkEcho();
		}

		if (pressed) {
			new java.util.Timer().schedule(new java.util.TimerTask() {
				public void run() {
					whilePressed();
				}
			}, 100);
			displayImage();
		}
	}

	/**
	 * This method is used by the GUI to find easily out, which Tab the user is
	 * using right now.
	 */
	public String getClassName() {
		return "VolumeTab";
	}

	@Override
	public void lifeUpdate() {
	}

	private int actualSliceIndex() {
		return getActualSlice() - 1 + perEcho * (getActualEcho() - 1);
	}

	private boolean displayImage() {
		if (volume == null || actualSliceIndex() < 0
				|| actualSliceIndex() >= volume.size()) {
			return false;
		}

		Image curimg = this.volume.getSlice(actualSliceIndex());
		drawIntoImage(image, this.volume.getSlice(actualSliceIndex()).getData()
				.getBufferedImage());

		if (curimg.getRoi() != null) {
			relativroi.draw(image.getGraphics());
			showROI(true);
		}
		repaint();
		return true;
	}

	private void drawIntoImage(BufferedImage target, BufferedImage source) {
		java.awt.Graphics gr = target.getGraphics();
		gr.drawImage(source.getScaledInstance(target.getWidth(),
				target.getHeight(), BufferedImage.SCALE_FAST), 0, 0, null);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		Object obj = e.getSource();
		int change = -1 * e.getWheelRotation();

		if (obj instanceof JTextField) {
			JTextField index = (JTextField) obj;
			if (index.equals(index_slice) || index.equals(max_slice)
					|| index.getText().equals("Slice:")) {
				index_slice.setText(""
						+ (Integer.parseInt(index_slice.getText()) + change));
				checkSlice();
			} else {
				index_echo.setText(""
						+ (Integer.parseInt(index_echo.getText()) + change));
				checkEcho();
			}
		} else if (obj instanceof JLabel) {
			JLabel img = (JLabel) obj;
			if (img.equals(imagelabel)) {
				if (index_echo.hasFocus()) {
					index_echo
							.setText(""
									+ (Integer.parseInt(index_echo.getText()) + change));
					checkEcho();
				} else {
					index_slice
							.setText(""
									+ (Integer.parseInt(index_slice.getText()) + change));
					checkSlice();
				}
			}
		} else if (obj instanceof JPanel) {
			JPanel arrow = (JPanel) obj;
			if (!arrow.equals(arrows_slice)) {
				index_echo.setText(""
						+ (Integer.parseInt(index_echo.getText()) + change));
				checkEcho();
			} else {
				index_slice.setText(""
						+ (Integer.parseInt(index_slice.getText()) + change));
				checkSlice();
			}

		}
		displayImage();
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

	public void showROI(boolean visible) {
		if (visible) {
			if (vf == null) {
				vf = new VolumeFitter();
			}
			int degree = -1;
			if (((String) dimension.getSelectedItem()).contains("Exponential")) {
				degree = -2;
			} else {
				for (int i = 0; i < 5; i++) {
					if (((String) dimension.getSelectedItem()).contains("" + i)) {
						degree = i;
					}
				}
			}
			try {
				drawIntoImage(roiimage, vf.getPlot(this.volume, this.volume
						.getSlice(actualSliceIndex()).getRoi(),
						getActualSlice() - 1, degree, alsolog.isSelected()));
				roiPanel.setVisible(true);
				if (!ownExtended) {
					parent.setExtendedWindow(true);
					ownExtended = true;
					leg_gray.setVisible(true);
					GUI.setfinalSize(toppanel, new Dimension(1400, 450));
					GUI.setfinalSize(parent, new Dimension(1450, 550));
				}
			} catch (SingularMatrixException | ArrayIndexOutOfBoundsException e) {
			}
		} else {
			relativroi = null;
			roiPanel.setVisible(false);
			if (ownExtended) {
				parent.setExtendedWindow(false);
				ownExtended = false;
				leg_gray.setVisible(false);
				GUI.setfinalSize(toppanel, new Dimension(1100, 450));
				GUI.setfinalSize(parent, new Dimension(1100, 550));
			}
		}

		this.repaint();
	}

	public void setRoiPosition(int x, int y) {
		BufferedImage orig = this.volume.getSlice(actualSliceIndex()).getData()
				.getBufferedImage();
		Roi realroi = null;
		if (shape.getSelectedItem().equals("Point")){
		relativroi = new PointRoi(x, y);

		realroi = new PointRoi(((double) y) / this.image.getWidth()
				* orig.getWidth(), ((double) x) / this.image.getHeight()
				* orig.getHeight());
		}else{
			relativroi = new OvalRoi(x, y, 10, 10);
			realroi = new OvalRoi(((double) y) / this.image.getWidth()
					* orig.getWidth(), ((double) x) / this.image.getHeight()
					* orig.getHeight(),10,10);
		}
		volume.setRoi(realroi);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this.imagelabel && volume != null) {
			setRoiPosition(e.getX(), e.getY());
			displayImage();
			showROI(true);
		}
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void caretUpdate(CaretEvent e) {
		Object source = e.getSource();
		if (source == index_slice) {
			checkSlice();
		} else if (source == index_echo) {
			checkEcho();
		} else if (source == filter) {
			if (!filter.getText().equals(lastfilter)) {
				lastfilter = filter.getText();
				displayAttributes();
			}
		}
	}

}