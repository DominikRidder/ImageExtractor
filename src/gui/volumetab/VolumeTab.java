package gui.volumetab;

import gui.GUI;
import gui.MyTab;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.frame.ContrastAdjuster;
import imagehandling.KeyMap;
import imagehandling.TextOptions;
import imagehandling.Volume;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

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
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import tools.ImageExtractorConfig;
import tools.VolumeFitter;
import tools.ZeroEcho;

/**
 * This class representing a Tab in the GUI window, where you can look up the
 * header and images of a Volume.
 * 
 * @author dridder_local
 *
 */
public class VolumeTab extends JPanel implements ActionListener, MyTab,
		ChangeListener, MouseWheelListener, MouseListener, KeyListener,
		Runnable, CaretListener, DropTargetListener {

	/**
	 * The parent is the actual window, that created this VolumeTab.
	 */
	private GUI parent;

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
	 * The JPanel dir is a row, which contains two JButtons (browse_path and
	 * apply_path).
	 */
	private JPanel volumePanel;

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
	 * This JPanel contains a JTextField search with the text "Search:" and a
	 * textfield to search for Attributes.
	 */
	private JPanel attributeConfig;

	private JPanel sizepanel;

	private JPanel roiPanel;

	private JScrollPane outputScroller;

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
	 * Arrow, for changing the index.
	 */
	private JButton arrow_up_slice, arrow_down_slice;

	/**
	 * Arrows, for changing the index.
	 */
	private JButton arrow_up_echo, arrow_down_echo;

	/**
	 * JFileChooser is used to search a Volume (dir).
	 */
	private JFileChooser chooser;

	/**
	 * This TextField is used to create a Volume.
	 */
	private JTextField path;

	/**
	 * This field shows the maximum echo.
	 */
	private JTextField max_echo;

	/**
	 * The index field shows the current selected Volume slice.
	 */
	private JTextField index_slice;

	/**
	 * This field shows the number of slices in the volume (minus one).
	 */
	private JTextField max_slice;

	/**
	 * This field is the current choosen echo.
	 */
	private JTextField index_echo;

	/**
	 * The filter is used to search for Attributes in the header of an image.
	 */
	private JTextField filter;

	/**
	 * Header and co. output stuff, which is wrapped in the scroll object.
	 */
	private JTextArea outputArea;

	private JCheckBox alsolog;

	private JComboBox<String> dimension;

	private JComboBox<String> shape;

	private JComboBox<String> unit;

	private JSlider radius;

	/**
	 * The imagepanel is the container, which contains the ImageIcon ic.
	 */
	private JLabel imagelabel;

	private JLabel radiustext;

	/**
	 * Image which is on the right side of the Tab.
	 */
	private BufferedImage image;

	private BufferedImage roiimage;

	private ImageIcon roiimgicon;

	/**
	 * The ImageIcon ic wrapping the image.
	 */
	private ImageIcon imgicon;

	private Roi relativroi;

	private VolumeFitter vf;

	private String lastfilter = "";

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private long lastpressed;

	/**
	 * Number of echos.
	 */
	private int echoNumbers;

	private double scaling;

	/**
	 * Number of slices per echo.
	 */
	private int perEcho;

	private int creatingTextStatus = 0;

	/**
	 * Value, which decides, whether the hole header of a dicom is shown or if
	 * only a searched part in shown.
	 */
	private boolean displayAll;

	/**
	 * True if a Thread creates a volume.
	 */
	private boolean creatingVolume;

	/**
	 * This boolean is important to know, if this tab was in an extended state
	 * or not.
	 */
	private boolean ownExtended = false;

	private double roitabwidth = System.getProperty("os.name").toLowerCase()
			.contains("mac") ? 0.7 : 0.35;

	private ArrayList<BufferedImage> zeroecho;

	private ContrastAdjuster contrast;

	/**
	 * Standard Constructur.
	 */
	public VolumeTab(JFileChooser filechooser, GUI gui) {
		parent = gui;
		
		new DropTarget(this, this);
		// ImageExtractorConfig iec = new ImageExtractorConfig();
		// customImagej.

		// The path for the Volume
		path = new JTextField("");
		path.addKeyListener(this);
		path.setEditable(false);
		path.setBackground(null);
		new DropTarget(path, this);
//		path.setDropTarget(dnd);
		GUI.setfinalSize(path, new Dimension((int) (parent.width / 2.5),
				(int) (parent.height / 17)));

		// The Attribute Filter
		filter = new JTextField("");
		filter.addCaretListener(this);
		GUI.setfinalSize(filter, new Dimension((int) (parent.width / 6),
				(int) (parent.height / 5.4)));

		String[] shapes = { "Point", "Circle", "Sphere" };
		shape = new JComboBox<String>(shapes);
		shape.setSelectedIndex(1);
		shape.addActionListener(this);
		GUI.setfinalSize(shape, new Dimension((int) (parent.width / 15.714),
				(int) (parent.height / 21.6)));

		String[] units = { "mm", "pixel" };
		unit = new JComboBox<String>(units);
		unit.addActionListener(this);
		GUI.setfinalSize(unit, new Dimension((int) (parent.width / 15.714),
				(int) (parent.height / 21.6)));

		// image
		image = new BufferedImage((int) (parent.width / 2.483),
				(int) (parent.height / 1.219), BufferedImage.TYPE_4BYTE_ABGR);
		// The ImageIcon is kinda a wrapper for the image
		imgicon = new ImageIcon(image);
		// imagepanel wrapps the ImageIcon
		imagelabel = new JLabel(imgicon);
		GUI.setfinalSize(imagelabel, new Dimension(
				(int) (parent.width / 2.483), (int) (parent.height / 1.219)));
		imagelabel.addMouseWheelListener(this);
		imagelabel.addKeyListener(this);
		imagelabel.addMouseListener(this);

		// initialize the Buttons
		open_imagej = new JButton("open in External");
		open_imagej.addActionListener(this);
		open_imagej.addKeyListener(this);
		GUI.setfinalSize(open_imagej, new Dimension((int) (parent.width / 2.5),
				(int) (parent.height / 15)));

		browse_path = new JButton("browse");
		browse_path.addActionListener(this);
		browse_path.addKeyListener(this);
		GUI.setfinalSize(browse_path, new Dimension((int) (parent.width / 2.5),
				(int) (parent.height / 15)));

		show_attributes = new JButton("Display all Attributes");
		show_attributes.addActionListener(this);
		show_attributes.addKeyListener(this);
		GUI.setfinalSize(show_attributes, new Dimension(
				(int) (parent.width / 4), (int) (parent.height / 5.4)));

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
		GUI.setfinalSize(max_slice, new Dimension((int) (parent.width / 14.67),
				(int) (parent.height / 5.4)));

		max_echo = new JTextField("/0");
		max_echo.setEditable(false);
		max_echo.setBackground(null);
		max_echo.setBorder(null);
		max_echo.addMouseWheelListener(this);
		GUI.setfinalSize(max_echo, new Dimension((int) (parent.width / 14.67),
				(int) (parent.height / 5.4)));

		index_slice = new JTextField("0");
		index_slice.setEditable(false);
		index_slice.addMouseWheelListener(this);
		index_slice.addKeyListener(this);
		index_slice.addCaretListener(this);
		GUI.setfinalSize(index_slice, new Dimension(
				(int) (parent.width / 14.67), (int) (parent.height / 5.4)));

		index_echo = new JTextField("0");
		index_echo.setEditable(false);
		index_echo.addMouseWheelListener(this);
		index_echo.addKeyListener(this);
		index_echo.addCaretListener(this);
		GUI.setfinalSize(index_echo, new Dimension(
				(int) (parent.width / 14.67), (int) (parent.height / 5.4)));

		JTextField slice = new JTextField("Slice:");
		slice.setEditable(false);
		slice.setBackground(null);
		GUI.setfinalSize(slice, new Dimension((int) (parent.width / 30),
				(int) (parent.height / 5.4)));
		slice.setBorder(null);
		slice.addMouseWheelListener(this);

		JTextField echo = new JTextField("Echo:");
		echo.setEditable(false);
		echo.setBackground(null);
		GUI.setfinalSize(echo, new Dimension((int) (parent.width / 30),
				(int) (parent.height / 5.4)));
		echo.setBorder(null);
		echo.addMouseWheelListener(this);

		JTextField search = new JTextField("Search:");
		search.setEditable(false);
		search.setBackground(null);
		GUI.setfinalSize(search, new Dimension(parent.width / 22,
				(int) (parent.height / 5.4)));
		search.setBorder(null);

		// creating 2 arrow panel
		arrows_slice = new JPanel();
		arrows_slice
				.setLayout(new BoxLayout(arrows_slice, BoxLayout.PAGE_AXIS));
		arrows_slice.add(arrow_up_slice);
		arrows_slice.add(arrow_down_slice);
		arrows_slice.addMouseWheelListener(this);
		GUI.setfinalSize(arrows_slice, new Dimension(parent.width / 50,
				(int) (parent.height / 13.5)));

		arrows_echo = new JPanel();
		arrows_echo.setLayout(new BoxLayout(arrows_echo, BoxLayout.PAGE_AXIS));
		arrows_echo.add(arrow_up_echo);
		arrows_echo.add(arrow_down_echo);
		arrows_echo.addMouseWheelListener(this);
		GUI.setfinalSize(arrows_echo, new Dimension(parent.width / 50,
				(int) (parent.height / 13.5)));

		// creating the output field
		outputArea = new JTextArea("status");
		outputArea.setEditable(false);
		outputScroller = new JScrollPane(outputArea);
		outputScroller.setPreferredSize(new Dimension(parent.width / 11,
				(int) (parent.height / 5.4)));
		outputScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outputScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		// creating the directory chooser
		chooser = filechooser;

		// dir contains two buttons
		volumePanel = new JPanel();
		volumePanel.setLayout(new GridLayout(1, 2, 20, 1));
		Component[] dirstuff = { browse_path, open_imagej };
		GUI.addComponents(volumePanel, dirstuff);
		GUI.setfinalSize(volumePanel, new Dimension((int) (parent.width / 2.5),
				(int) (parent.height / 17)));

		// "Slice:", actual slice and Max slice
		index_Panel = new JPanel();
		index_Panel.setLayout(new BoxLayout(index_Panel, BoxLayout.LINE_AXIS));
		Component[] imgstuff = { slice, arrows_slice, index_slice, max_slice,
				echo, arrows_echo, index_echo, max_echo };
		GUI.addComponents(index_Panel, imgstuff);
		GUI.setfinalSize(index_Panel, new Dimension((int) (parent.width / 2.5),
				(int) (parent.height / 15)));

		// Search option Panel
		attributeConfig = new JPanel();
		attributeConfig.setLayout(new BoxLayout(attributeConfig,
				BoxLayout.LINE_AXIS));
		Component[] attstuff = { show_attributes,
				Box.createRigidArea(new Dimension(10, 0)), search, filter };
		GUI.addComponents(attributeConfig, attstuff);
		GUI.setfinalSize(attributeConfig, new Dimension(
				(int) (parent.width / 2), (int) (parent.height / 15)));

		// Putting everything on the left side together
		leftSidePanel = new JPanel();
		leftSidePanel.setLayout(new BoxLayout(leftSidePanel,
				BoxLayout.PAGE_AXIS));
		Component[] panelstuff = { Box.createRigidArea(new Dimension(0, 10)),
				path, volumePanel, index_Panel, attributeConfig, outputScroller };
		GUI.addComponents(leftSidePanel, panelstuff);
		GUI.setfinalSize(leftSidePanel, new Dimension(
				(int) (parent.width / 1.6923), (int) (parent.height / 1.2)));

		JTextField text_dim = new JTextField("Fitting: ");
		text_dim.setEditable(false);
		text_dim.setBackground(null);
		text_dim.setBorder(null);
		GUI.setfinalSize(text_dim, new Dimension(
				(int) (parent.width / 15.7143), (int) (parent.height / 10.8)));

		dimension = new JComboBox<String>();
		String help[] = { "", "st", "nd", "rd", "th", "th" };
		for (int i = 0; i < 5; i++) {
			dimension.addItem("Polynomial (" + i + help[i] + " order)");
		}
		dimension.addItem("Polynomial (n order)");
		dimension.addItem("Exponential");
		dimension.setSelectedIndex(1);
		dimension.addActionListener(this);
		GUI.setfinalSize(dimension, new Dimension((int) (parent.width / 5.5),
				(int) (parent.height / 15)));

		// Calc zero echo
		JButton zero_echo = new JButton("Calculate Zero Echo");
		zero_echo.addActionListener(this);
		GUI.setfinalSize(zero_echo, new Dimension((int) (parent.width / 5.5),
				parent.height / 25));

		JPanel dimselection = new JPanel();
		dimselection
				.setLayout(new BoxLayout(dimselection, BoxLayout.LINE_AXIS));
		dimselection.add(text_dim);
		dimselection.add(dimension);
		// dimselection.add(zero_echo);
		GUI.setfinalSize(dimselection, new Dimension(
				(int) (parent.width * roitabwidth), (int) (parent.height / 20)));

		JPanel zeropanel = new JPanel();
		zeropanel.setLayout(new BoxLayout(zeropanel, BoxLayout.LINE_AXIS));
		zeropanel.add(Box.createRigidArea(new Dimension(
				(int) (parent.width / 15.7143), 1)));
		zeropanel.add(zero_echo);
		GUI.setfinalSize(zeropanel, new Dimension(
				(int) (parent.width * roitabwidth), (int) (parent.height / 25)));

		// image
		roiimage = new BufferedImage(
				(int) (parent.width * roitabwidth * 7. / 10),
				(int) (parent.height / 1.8), BufferedImage.TYPE_4BYTE_ABGR);
		// The ImageIcon is kinda a wrapper for the image
		roiimgicon = new ImageIcon(roiimage);
		// imagepanel wrapps the ImageIcon
		JLabel roilabel = new JLabel(roiimgicon);
		new DropTarget(roilabel, this);
//		roilabel.setDropTarget(dnd);
		GUI.setfinalSize(roilabel,
				new Dimension((int) (parent.width * roitabwidth),
						(int) (parent.height / 1.8)));
		JPanel roiimg = new JPanel();
		roiimg.add(roilabel);
		GUI.setfinalSize(roiimg,
				new Dimension((int) (parent.width * roitabwidth),
						(int) (parent.height / 1.8)));

		// Checkbox for showing the log evaluation
		alsolog = new JCheckBox();
		alsolog.addChangeListener(this);

		// Log Checkbox text
		JTextField logtext = new JTextField("log");
		logtext.setEditable(false);
		logtext.setBackground(null);
		logtext.setBorder(null);
		GUI.setfinalSize(logtext, new Dimension((int) (parent.width / 5.5),
				(int) (parent.height / 5.4)));

		radius = new JSlider();
		radius.setMaximum(10);
		radius.addChangeListener(this);
		GUI.setfinalSize(radius, new Dimension((int) (parent.width / 9),
				(int) (parent.height / 10.8)));

		radiustext = new JLabel("Radius: " + radius.getValue());
		GUI.setfinalSize(radiustext, new Dimension(parent.width / 11,
				(int) (parent.height / 18)));

		// log Checkbox + text
		JPanel logpanel = new JPanel();
		logpanel.setLayout(new BoxLayout(logpanel, BoxLayout.LINE_AXIS));
		logpanel.add(alsolog);
		logpanel.add(logtext);
		logpanel.add(shape);
		GUI.setfinalSize(logpanel, new Dimension(
				(int) (parent.width * roitabwidth), (int) (parent.height / 10)));

		sizepanel = new JPanel();
		sizepanel.setLayout(new BoxLayout(sizepanel, BoxLayout.LINE_AXIS));
		sizepanel.add(radius);
		sizepanel.add(radiustext);
		sizepanel.add(unit);
		GUI.setfinalSize(sizepanel, new Dimension(
				(int) (parent.width * roitabwidth), (int) (parent.height / 15)));

		// Putting the roi Panel together
		roiPanel = new JPanel();
		roiPanel.setLayout(new BoxLayout(roiPanel, BoxLayout.Y_AXIS));
		GUI.setfinalSize(roiPanel,
				new Dimension((int) (parent.width * roitabwidth),
						(int) (parent.height / 1.2)));
		Component[] roistuff = { Box.createRigidArea(new Dimension(0, 5)),
				dimselection, Box.createRigidArea(new Dimension(0, 3)),
				zeropanel, Box.createRigidArea(new Dimension(0, 5)), roiimg,
				logpanel, sizepanel };
		GUI.addComponents(roiPanel, roistuff);
		roiPanel.setVisible(false);

		// Putting everything together now
		toppanel = new JPanel();
		toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
		GUI.setfinalSize(toppanel, new Dimension(parent.width,
				(int) (parent.height / 1.2)));
		toppanel.add(leftSidePanel);
		toppanel.add(imagelabel);
		toppanel.add(Box.createRigidArea(new Dimension(parent.width / 110,
				(int) (parent.height / 1.2))));
		toppanel.add(roiPanel);

		// Some this stuff
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.add(toppanel);
		GUI.setfinalSize(this, new Dimension(parent.width,
				(int) (parent.height / 1.2)));

		this.setVisible(true);
	}

	/**
	 * Method to try, to create a Volume to the given path.
	 */
	public void createVolume() {
		creatingVolume = true;
		try {
			creatingText();
			volume = Volume.createVolume(path.getText());
			if (volume == null) {
				throw new RuntimeException();
			}
			WindowManager.setTempCurrentImage(volume.getData().get(0));
			contrast = new ContrastAdjuster();
			contrast.run("");
			WindowManager.setTempCurrentImage(contrast.thread, volume.getData()
					.get(0));

			path.setText(volume.getPath());

			volume.getTextOptions().setReturnExpression(
					TextOptions.ATTRIBUTE_VALUE + "");

			// Default Index
			index_slice.setText("1");
			index_echo.setText("1");
			// User can change fields again
			index_slice.setEditable(true);
			index_echo.setEditable(true);

			// Getting some values
			echoNumbers = Integer.parseInt(volume.getAttribute(
					KeyMap.KEY_ECHO_NUMBERS_S, volume.size() - 1));
			perEcho = volume.size() / echoNumbers;
			max_echo.setText("/" + echoNumbers);
			max_slice.setText("/" + perEcho);

			index_slice.requestFocus();
			showROI(false);
			while (!displayImage()) {
				System.out.println("not worked");
			}
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
				if (creatingVolume) {
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
					creatingText();
				} else if (volume != null) {
					displayAttributes();
				} else {
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
					StringReader reader = new StringReader(header);
					outputArea.read(reader, null);
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

		} catch (IOException e) {
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
		case "Calculate Zero Echo":
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
		case "comboBoxChanged":
			actionShape();
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

	public void actionShape() {
		if (shape.getSelectedItem().equals("Point")) {
			sizepanel.setVisible(false);
		} else {
			sizepanel.setVisible(true);
		}

		if (relativroi != null) {
			if (relativroi instanceof PointRoi) {
				setRoiPosition((int) relativroi.getXBase(),
						(int) relativroi.getYBase());
			} else {
				java.awt.Rectangle bounds = relativroi.getBounds();
				setRoiPosition(bounds.x + bounds.height / 2, bounds.y
						+ bounds.height / 2);
			}
		}
	}

	public void actionCalculateZeroEcho() {
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

		ZeroEcho ze = new ZeroEcho(volume, this, degree, alsolog.isSelected());
		new Thread(ze).start();
	}

	/***
	 * This Method checks if a ZeroEcho slice is calculated already to a given
	 * Fitting function. This Method also sets a litte "+ 0" to the max_echo
	 * field, so that the user get informed, about the additional slice.
	 * 
	 * @return if there is a ZeroEcho slice to show.
	 */
	public boolean zeroEchoCheck() {
		try {
			if (zeroecho.get(this.getActualSlice() + shape.getSelectedIndex()) != null) {
				max_echo.setText(max_echo.getText().replace("+ 0", "") + "+ 0");
				return true;
			}
		} catch (IOException e) {

		}
		max_echo.setText(max_echo.getText().replace("+ 0", ""));
		return false;
	}

	/***
	 * This Method gets called, whenever the ZeroEcho Class finished an ZeroEcho
	 * slice.
	 */
	public void addZeroEcho(ArrayList<BufferedImage> zeroecho,
			String fittingfunction) {
		this.zeroecho = zeroecho;
		max_echo.setText(max_echo.getText().replace(" + 0", "") + " + 0");
	}

	public void actionBrowse() {
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
				&& chooser.getSelectedFile() != null) {
			if (chooser.getSelectedFile().isDirectory()) {
				path.setText(chooser.getSelectedFile().toString());
			} else if (chooser.getSelectedFile().isFile()) {
				if (!chooser.getSelectedFile().getAbsolutePath()
						.endsWith("nii")) {
					path.setText(chooser.getSelectedFile().getParent()
							.toString());
				} else {
					path.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
			new Thread(this).start();
		}
		parent.imec.setOption("LastBrowse", chooser.getCurrentDirectory()
				.getAbsolutePath());
		parent.imec.save();
	}

	public void actionDisplayAttributes() {
		displayAll = true;
		displayAttributes();
	}

	public void actionOpenInExternal() {
		// if (volume != null) {
		parent.imec = new ImageExtractorConfig();
		String customExternal = null;
		if (path.getText().endsWith(".nii")) {
			customExternal = parent.imec.getOption("External_NITFI");
		} else {
			customExternal = parent.imec.getOption("External_DICOM");
		}
		ProcessBuilder pb;

		String commands[] = splittCommand(customExternal.replace("$FILE",
				path.getText()));
		pb = new ProcessBuilder(commands);

		System.out.println("\nTry to execute:");
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

	public String[] splittCommand(String command) {
		ArrayList<String> commands = new ArrayList<String>();
		String[] forreturn = new String[0];

		boolean ignore = false;

		StringBuilder next = new StringBuilder();
		for (char c : command.toCharArray()) {
			if (c == '\'') {
				ignore = !ignore;
			} else if (c == ' ' && !ignore) {
				commands.add(next.toString());
				next.delete(0, next.length());
			} else {
				next.append(c);
			}
		}

		if (next.length() != 0) {
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
					for (CaretListener l : listener) {
						index_slice.removeCaretListener(l);
					}
					index_slice.setText(act + "");
					for (CaretListener l : listener) {
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
				} else if (act <= 0 && zeroecho != null) {
					act = 0;
				} else if (act < 1 && volume != null) {
					act = 1;
				}
				CaretListener[] listener = index_echo.getCaretListeners();
				if (listener.length != 0) {
					for (CaretListener l : listener) {
						index_echo.removeCaretListener(l);
					}
					index_echo.setText(act + "");
					for (CaretListener l : listener) {
						index_echo.addCaretListener(l);
					}
				}
			}
		};

		SwingUtilities.invokeLater(handlechange);
	}

	public int getActualSlice() throws IOException {
		try {
			return Integer.parseInt(index_slice.getText());
		} catch (NumberFormatException e) {
			throw new IOException(e.getMessage()); // Forcing myself, to catch
													// Exceptions
		}
	}

	public int getActualEcho() throws IOException {
		try {
			return Integer.parseInt(index_echo.getText());
		} catch (NumberFormatException e) {
			throw new IOException(e.getMessage()); // Forcing myself, to catch
													// Exceptions
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton b = (JButton) e.getSource();
			String name = b.getText();
			if (name.contains("arrow")) {
				whilePressed(300);
			}
		} else if (e.getSource() == alsolog) {
			showROI(true);
		} else if (e.getSource() == radius) {
			radiustext.setText("Radius: " + radius.getValue());
			updateROI();
		} else if (e.getSource() == unit) {
			updateROI();
		}
	}

	public void whilePressed(final int time) {
		try {
			boolean slice = true;
			boolean pressed = true;
			if (System.currentTimeMillis() - lastpressed > 50) {
				lastpressed = System.currentTimeMillis();
				if (arrow_up_slice.getModel().isPressed()) {
					if (getActualSlice() != perEcho) {
						index_slice.setText("" + (getActualSlice() + 1));
					}
				} else if (arrow_down_slice.getModel().isPressed()) {
					if (getActualSlice() != 1) {
						index_slice.setText("" + (getActualSlice() - 1));
					}
				} else if (arrow_up_echo.getModel().isPressed()) {
					slice = false;
					if (getActualEcho() != echoNumbers) {
						index_echo.setText("" + (getActualEcho() + 1));
					}
				} else if (arrow_down_echo.getModel().isPressed()) {
					slice = false;
					if (getActualEcho() != 1 || getActualEcho() == 1
							&& zeroecho != null) {
						index_echo.setText("" + (getActualEcho() - 1));
					}
				} else {
					pressed = false;
					lastpressed = 0;
				}

				if (pressed) {
					displayImage();
					if (slice) {
						checkSlice();
					} else {
						checkEcho();
					}
				}

			}

			if (pressed) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					public void run() {
						whilePressed(50 + time / 2);
					}
				}, time);
			}
		} catch (IOException e) {
			// couldn't read actual slice/echo
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
	public void neededSize() {
		parent.requestWidth(preferedWidth(), this);
	}

	private int actualSliceIndex() throws IOException {
		return getActualSlice() - 1 + perEcho * (getActualEcho() - 1);
	}

	private boolean displayImage() {
		try {
			if (volume == null
					|| (actualSliceIndex() < 0 || actualSliceIndex() >= volume
							.size())
					&& !(getActualEcho() == 0 && zeroecho != null)) {
				return false;
			}

			BufferedImage buff = null;
			if (getActualEcho() != 0) {
				WindowManager.setTempCurrentImage(contrast.thread, volume
						.getSlice(actualSliceIndex()).getData());
				contrast.adjustmentValueChanged(new AdjustmentEvent(
						DummyAdjuster.dummy, 0, 0, 0));
				sleep(20); // waiting for the ContrastAdjuster
				buff = this.volume.getSlice(actualSliceIndex()).getData()
						.getBufferedImage();
			} else {
				buff = zeroecho.get(getActualSlice() - 1);
			}

			int max = buff.getHeight() > buff.getWidth() ? buff.getHeight()
					: buff.getWidth();
			double imgmax = image.getWidth() > image.getHeight() ? image
					.getWidth() : image.getHeight();
			scaling = imgmax / max;

			image = new BufferedImage((int) (buff.getWidth() * scaling),
					(int) (buff.getHeight() * scaling),
					BufferedImage.TYPE_3BYTE_BGR);
			drawIntoImage(image, buff);

			GUI.setfinalSize(imagelabel,
					new Dimension(image.getWidth(), image.getHeight()));
			imgicon.setImage(image);
			imagelabel.setIcon(imgicon);

			int neededwidth = image.getWidth() + (int) (parent.width / 1.6923);
			if (relativroi != null) {
				neededwidth += (int) (parent.width * roitabwidth);
			}

			if (parent.getWidth() != neededwidth) {
				parent.requestWidth(neededwidth, this);
				GUI.setfinalSize(toppanel, new Dimension(neededwidth,
						parent.height));
				System.out.println("Roi Panel width = " + parent.width
						* roitabwidth);
			}

			if (relativroi != null) {
				if (relativroi instanceof Roi3D) {
					((Roi3D) relativroi).draw(volume, image,
							getActualSlice() - 1, scaling);
				} else {
					relativroi.draw(image.getGraphics());
				}
				showROI(true);
			}
			repaint();
			return true;
		} catch (IOException e) {
			return false; // couldnt read the info from the gui elements (slice
							// or echo)
		}
	}

	private void drawIntoImage(BufferedImage target, BufferedImage source) {
		java.awt.Graphics gr = target.getGraphics();
		gr.drawImage(source.getScaledInstance(target.getWidth(),
				target.getHeight(), BufferedImage.SCALE_FAST), 0, 0, null);
	}

	public void setShape(String shape) {
		for (int i = 0; i < this.shape.getItemCount(); i++) {
			if (this.shape.getItemAt(i).equals(shape)) {
				this.shape.setSelectedIndex(i);
				break;
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (System.currentTimeMillis() - lastpressed > 50) {
			Object obj = e.getSource();
			int change = -1 * e.getWheelRotation();
			boolean makechange = false;
			boolean changeEcho = false;

			if (obj instanceof JTextField) {
				JTextField index = (JTextField) obj;
				if (index.equals(index_slice) || index.equals(max_slice)
						|| index.getText().equals("Slice:")) {
					makechange = true;
					changeEcho = false;
				} else {
					makechange = true;
					changeEcho = true;
				}
			} else if (obj instanceof JLabel) {
				JLabel img = (JLabel) obj;
				if (img.equals(imagelabel)) {
					if (index_echo.hasFocus()) {
						makechange = true;
						changeEcho = true;
					} else {
						makechange = true;
						changeEcho = false;
					}
				}
			} else if (obj instanceof JPanel) {
				JPanel arrow = (JPanel) obj;
				if (!arrow.equals(arrows_slice)) {
					makechange = true;
					changeEcho = true;
				} else {
					makechange = true;
					changeEcho = false;
				}

			}

			if (makechange) {
				if (changeEcho) {
					try {
						if (!(getActualEcho() + change < 1 || getActualEcho()
								+ change > echoNumbers))
							index_echo
									.setText(""
											+ (Integer.parseInt(index_echo
													.getText()) + change));
						checkEcho();
					} catch (NumberFormatException | IOException e1) {

					}
				} else {
					try {
						if (!(getActualSlice() + change < 1 || getActualSlice()
								+ change > perEcho)) {
							index_slice
									.setText(""
											+ (Integer.parseInt(index_slice
													.getText()) + change));
							checkSlice();
						}
					} catch (NumberFormatException | IOException e1) {

					}
				}
			}
			lastpressed = System.currentTimeMillis();
		}
	}

	public void keyPressed(KeyEvent e) {
		if (System.currentTimeMillis() - lastpressed > 50) {
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
				index_echo.setText(""
						+ (Integer.parseInt(index_echo.getText()) + change));
			} else {
				index_slice.setText(""
						+ (Integer.parseInt(index_slice.getText()) + change));
			}
			lastpressed = System.currentTimeMillis();
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
				drawIntoImage(roiimage, vf.getPlot(this.volume,
						this.relativroi, getActualSlice() - 1, degree,
						alsolog.isSelected(), roiimage.getWidth(),
						roiimage.getHeight()));
				if (!ownExtended) {
					ownExtended = true;
				}
				roiPanel.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
				// a lot of exception can ocure here
				// (IOException, SingularMatrixException,
				// ArrayIndexOutOfBoundsException, ...)
			}
		} else {
			relativroi = null;
			roiPanel.setVisible(false);
			if (ownExtended) {
				ownExtended = false;
			}
		}

		this.repaint();
	}

	public void setRoiPosition(int x, int y) {
		try {
			BufferedImage orig = this.volume.getSlice(actualSliceIndex())
					.getData().getBufferedImage();
			Roi realroi = null;

			switch ((String) shape.getSelectedItem()) {
			case "Point":
				relativroi = new PointRoi(x, y);

				realroi = new PointRoi(((double) x) / scaling, ((double) y)
						/ scaling);
				break;
			case "Circle":
				double radius = calculateRadius();
				double newr = (int) (radius * scaling);
				x -= newr;
				y -= newr;
				relativroi = new OvalRoi(x, y, newr * 2, newr * 2);
				realroi = new OvalRoi(((double) x) / scaling, ((double) y)
						/ scaling, radius * 2, radius * 2);
				break;
			case "Sphere":
				setRoiPosition(x, y, getActualSlice() - 1);
				return;
			}
			relativroi.setProperty("unit", (String) unit.getSelectedItem());
			realroi.setProperty("unit", (String) unit.getSelectedItem());
			volume.setRoi(realroi);
			this.displayImage();
		} catch (IOException e) {
			// couldnt read actual slice/echo
		}
	}

	public void setRoiPosition(int x, int y, int z) {
		try {
			BufferedImage orig = this.volume.getSlice(actualSliceIndex())
					.getData().getBufferedImage();
			Roi realroi = null;

			switch ((String) shape.getSelectedItem()) {
			case "Point":
			case "Circle":
				setRoiPosition(x, y);
				return;
			case "Sphere":
				double radius = calculateRadius();
				double newr = (int) (radius * scaling);
				x -= newr;
				y -= newr;
				relativroi = new SphereRoi(x, y, z, newr);
				realroi = new SphereRoi(((double) x) / scaling, ((double) y)
						/ scaling, z, radius);

				break;
			}
			relativroi.setProperty("unit", (String) unit.getSelectedItem());
			realroi.setProperty("unit", (String) unit.getSelectedItem());
			volume.setRoi(realroi);
			this.displayImage();
		} catch (IOException e) {
			// couldn't read actual slice/echo
		}
	}

	public void updateROI() {
		try {
			BufferedImage orig = this.volume.getSlice(actualSliceIndex())
					.getData().getBufferedImage();
			Roi realroi = null;
			if (shape.getSelectedItem().equals("Point")) {
			} else if (shape.getSelectedItem().equals("Circle")) {
				double x = relativroi.getXBase();
				double y = relativroi.getYBase();
				double calrad = relativroi.getBounds().getWidth() / 2;

				double radius = this.radius.getValue();

				if (unit.getSelectedItem().equals("pixel")) {
					radius /= volume.getSlice(0).getData().getFileInfo().pixelWidth;
				}

				double newr = (int) (radius * scaling);

				x += calrad - newr;
				y += calrad - newr;

				relativroi = new OvalRoi(x, y, newr * 2, newr * 2);
				realroi = new OvalRoi(((double) x) / scaling, ((double) y)
						/ scaling, radius * 2, radius * 2);
			} else if (shape.getSelectedItem().equals("Sphere")) {
				double x = relativroi.getXBase();
				double y = relativroi.getYBase();
				int z = ((Roi3D) relativroi).getZ();
				double calrad = relativroi.getBounds().getHeight() / 2;

				double radius = this.radius.getValue();

				if (unit.getSelectedItem().equals("pixel")) {
					radius /= volume.getSlice(0).getData().getFileInfo().pixelWidth;
				}
				double newr = (int) (radius * scaling);
				x += calrad - newr;
				y += calrad - newr;
				relativroi = new SphereRoi((int) x, (int) y, z, newr);
				realroi = new SphereRoi(((double) x) / scaling, ((double) y)
						/ scaling, z, radius);
			}
			relativroi.setProperty("unit", (String) unit.getSelectedItem());
			realroi.setProperty("unit", (String) unit.getSelectedItem());
			volume.setRoi(realroi);
			this.displayImage();
		} catch (IOException e) {
			// couldn't read the actual slice/echo
		}
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
			displayImage();
		} else if (source == index_echo) {
			checkEcho();
			displayImage();
		} else if (source == filter) {
			if (!filter.getText().equals(lastfilter)) {
				lastfilter = filter.getText();
				displayAttributes();
			}
		}
		if (!filter.getText().equals("")) {
			displayAttributes();
		}
	}

	public int preferedWidth() {
		if (volume == null) {
			return parent.width;
		} else {
			int neededwidth = image.getWidth() + (int) (parent.width / 1.6923);
			if (relativroi != null) {
				neededwidth += (int) (parent.width * roitabwidth);
			}
			return neededwidth;
		}
	}

	public int calculateRadius() {
		if (unit.getSelectedItem().equals("mm")) {
			return radius.getValue();
		} else {
			try {
				return (int) ((double) radius.getValue() / volume.getSlice(0)
						.getData().getFileInfo().pixelWidth);
			} catch (NumberFormatException | NullPointerException e) {
				return 1;
			}
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		dtde.acceptDrag(dtde.getDropAction());
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			dtde.acceptDrop(dtde.getDropAction());
			List<File> dropppedFiles = (List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			for (File file : dropppedFiles){
				if (file.isDirectory()){
					path.setText(file.getAbsolutePath());
				}else{
					if (file.getName().endsWith(".nii")){
						path.setText(file.getAbsolutePath());
					}else{
						path.setText(file.getParent());
					}
				}
				createVolume();
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}