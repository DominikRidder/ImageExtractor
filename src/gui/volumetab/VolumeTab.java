package gui.volumetab;

import gui.GUI;
import gui.MyTab;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.Nifti_Writer;
import ij.plugin.frame.ContrastAdjuster;
import imagehandling.Volume;
import imagehandling.headerhandling.KeyMap;
import imagehandling.headerhandling.TextOptions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.DefaultCaret;

import util.ImageExtractorConfig;
import util.VolumeFitter;
import util.ZeroEcho;

/**
 * This class representing a Tab in the GUI window, where you can look up the
 * header and images of a Volume.
 * 
 * @author Dominik Ridder
 *
 */
public class VolumeTab extends JPanel implements ActionListener, MyTab,
		ChangeListener, MouseWheelListener, MouseListener, KeyListener,
		Runnable, CaretListener, DropTargetListener, MouseMotionListener,
		PropertyChangeListener {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parent is the actual window, that created this VolumeTab.
	 */
	private GUI parent;

	/**
	 * Volume, which is used to get the header informations and the image.
	 */
	private Volume volume;

	/**
	 * Highest Panel is this Tab, which contains the leftSideStuff and the
	 * Imagepanel.
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

	/**
	 * Panel that is a part of the roiPanel. The sizePanel contains the slider
	 * and info about the current Circle/Sphere radius.NOTE: This Panel is not
	 * visible if there is no Roi on the Image.
	 */
	private JPanel sizepanel;

	/**
	 * Panel, that contains the options, that can be used with by the roi.
	 */
	private JPanel roiPanel;

	/**
	 * Field that contains the Information/Header about the current
	 * Slice/Volume.
	 */
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
	 * JFileChooser is used to search a Volume (directory/file).
	 */
	private JFileChooser chooser;

	/**
	 * This TextField is used to create a Volume.
	 */
	private JTextField path;

	/**
	 * This field is the current chosen echo.
	 */
	private JTextField echo_index;

	/**
	 * This field shows the maximum echo.
	 */
	private JTextField max_echo;

	/**
	 * Arrow, for changing the index.
	 */
	private ArrowButton echo_arrow_up, echo_arrow_down;

	/**
	 * The index field shows the current selected Volume slice.
	 */
	private JTextField slice_index;

	/**
	 * This field shows the number of slices in the volume (minus one).
	 */
	private JTextField slice_max;

	/**
	 * Arrow, for changing the index.
	 */
	private ArrowButton slice_arrow_up, slice_arrow_down;

	/**
	 * The filter is used to search for Attributes in the header of an image.
	 */
	private JTextField filter;

	/**
	 * Header and co. output stuff, which is wrapped in the scroll object.
	 */
	private JTextArea outputArea;

	/**
	 * CheckBox, that provides the Option, to fit throught the log(val+1) values
	 * instead of the values itself.
	 */
	private JCheckBox alsolog;

	/**
	 * The function, that should be used, to fit through the Echo's.
	 */
	private JComboBox<String> fittingfunction;

	/**
	 * The selected roi shape.
	 */
	private JComboBox<String> shape;

	/**
	 * The unit of the roi.
	 */
	private JComboBox<String> unit;

	/**
	 * Radius slider, that changes the radius of a Circle/Sphere roi.
	 */
	private JSlider radius;

	/**
	 * The imagepanel is the container, which contains the ImageIcon ic.
	 */
	private JLabel imagelabel;

	/**
	 * Label that displays the Text "Radius: <i>X</i>" where <i>X</i> is the
	 * actual radius.
	 */
	private JLabel radiustext;

	/**
	 * Image which is on the right side of the Tab.
	 */
	private BufferedImage image;

	/**
	 * The ImageIcon that wrapes the image.
	 */
	private ImageIcon imgicon;

	/**
	 * The BufferedImage, that saves the Graph of the fit.
	 */
	private BufferedImage fittingImage;

	/**
	 * The ImageIcon, that wrappes the fittingImage, so it can be displayed in a
	 * Label of the Gui.
	 */
	private ImageIcon fittingImageWrapper;

	/**
	 * This Roi is a scaled roi of the internal Roi. It is used, for a faster
	 * and easier solution, to find out, whether a roi is set or not and what
	 * kind of roi is used.
	 */
	private Roi relativroi;

	/**
	 * This Object creates an BufferedImage (if requested) that contains a Graph
	 * of a wished fit. This is used, to validate the roi.
	 */
	private VolumeFitter vf;

	/**
	 * String that was in the Attribute Filter Textfield since the last time the
	 * filter got checked.
	 */
	private String lastfilter = "";

	/**
	 * The LastTime in milliseconds, that the current Slice/Echo is changed.
	 * This gives the User a better feeling i. e. sliding through 64 slices with
	 * 1 millisecond each slice would be to fast for the User.
	 */
	private long lastpressed;

	/**
	 * Number of echos.
	 */
	private int echoNumbers;

	/**
	 * Scaling factor, from the small (original) Image sizes to the Gui Image
	 * sizes.
	 */
	private double scaling;

	/**
	 * Number of slices per echo.
	 */
	private int perEcho;

	/**
	 * Status of the creation Text in the StatusPanel, that indicates, that the
	 * createVolume() method is executed.
	 */
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

	/**
	 * This size is used to adjust a better size of some Gui Components.
	 */
	private double roitabwidth = 0.35;

	/**
	 * This field saves the ZeroEcho data.
	 */
	private ArrayList<ImagePlus> zeroecho;

	/**
	 * The ContrastAdjuster is used to change the min/max/brightness/contrast of
	 * the Images.
	 */
	private ContrastAdjuster contrast;

	/**
	 * Menu, that is used, to call the ContrastAdjuster or to save a ZeroEcho.
	 */
	private JMenu imagemenu = new JMenu("Image");

	/**
	 * Button, that is used, to start or cancel the calculation of the ZeroEcho.
	 */
	private JButton zero_echo;

	/**
	 * This Object is used to calculate the ZeroEcho.
	 */
	private ZeroEcho ze;

	/**
	 * This Timer is used to update the StatusPanel, while the createVolume()
	 * method is executed.
	 */
	private final Timer timer = new Timer(500, this);

	/**
	 * This boolean indicates, if a actionPerformed() is called by the
	 * ContrastAdjuster. Without this boolean the Program could stuck in an
	 * Update Cycle.
	 */
	private boolean contrastupdate;

	/**
	 * MenueItem, that contains the options to save the calculated ZeroEcho. In
	 * case, that the user didn't calculated the ZeroEcho, this Menu item will
	 * be disabled.
	 */
	private JMenuItem save;

	/**
	 * Panel, that contains the Image and the slider to change the actual
	 * Position/Image.
	 */
	private JPanel imagepanel;

	/**
	 * Slider, that changes the first dimension.
	 */
	private JSlider index_slider;

	/**
	 * Slider, that changes the second dimension.
	 */
	private JSlider echo_slider;

	/**
	 * Standard Constructor.
	 * 
	 * @param filechooser
	 *            The Filechooser, that should be used
	 * @param gui
	 *            The Gui, that contains this VolumeTab
	 */
	public VolumeTab(JFileChooser filechooser, GUI gui) {
		parent = gui;

		JMenuItem contrtmen = new JMenuItem("Adjust Brightness/Contrast");
		contrtmen.addActionListener(this);
		imagemenu.add(contrtmen);
		save = new JMenuItem("Save ZeroEcho");
		save.addActionListener(this);
		save.setEnabled(false);
		imagemenu.add(save);

		new DropTarget(this, this);

		// The path for the Volume
		path = new JTextField("");
		path.addKeyListener(this);
		path.setEditable(false);
		path.setBackground(null);
		new DropTarget(path, this);
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
		GUI.setfinalSize(shape, new Dimension((int) (parent.width / 12),
				(int) (parent.height / 21.6)));

		String[] units = { "mm", "pixel" };
		unit = new JComboBox<String>(units);
		unit.addActionListener(this);
		GUI.setfinalSize(unit, new Dimension((int) (parent.width / 12),
				(int) (parent.height / 21.6)));

		index_slider = new JSlider();
		index_slider.setName("SliceSlider");
		index_slider.setMinimum(1);
		index_slider.setMaximum(10);
		index_slider.addChangeListener(this);
		GUI.setfinalSize(index_slider, new Dimension(
				(int) (parent.width / 2.483), (int) (parent.height / 20)));

		echo_slider = new JSlider();
		echo_slider.setName("EchoSlider");
		echo_slider.setMinimum(1);
		echo_slider.setMaximum(10);
		echo_slider.addChangeListener(this);
		GUI.setfinalSize(echo_slider, new Dimension(
				(int) (parent.width / 2.483), (int) (parent.height / 20)));

		// image
		image = new BufferedImage((int) (parent.width / 2.483),
				(int) (parent.height / 1.3) - index_slider.getHeight()
						- echo_slider.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		// The ImageIcon is kinda a wrapper for the image
		imgicon = new ImageIcon(image);
		// imagepanel wrapps the ImageIcon
		imagelabel = new JLabel(imgicon);
		imagelabel.addMouseWheelListener(this);
		imagelabel.addKeyListener(this);
		imagelabel.addMouseListener(this);
		imagelabel.addMouseMotionListener(this);
		GUI.setfinalSize(imagelabel,
				new Dimension(image.getWidth(), image.getHeight()));

		GridBagConstraints c = new GridBagConstraints();
		// natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;

		imagepanel = new JPanel();
		imagepanel.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		imagepanel.add(imagelabel, c);
		c.gridy = 1;
		imagepanel.add(index_slider, c);
		c.gridy = 2;
		imagepanel.add(echo_slider, c);
		GUI.setfinalSize(imagepanel, new Dimension(
				(int) (parent.width / 2.483), (int) (parent.height / 1.3)));

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

		slice_arrow_up = new ArrowButton(BasicArrowButton.NORTH);
		slice_arrow_up.setChange(1);
		slice_arrow_up.setType(ArrowButton.SLICE_ARROW);
		slice_arrow_up.setToCall(this);

		slice_arrow_down = new ArrowButton(BasicArrowButton.SOUTH);
		slice_arrow_down.setChange(-1);
		slice_arrow_down.setType(ArrowButton.SLICE_ARROW);
		slice_arrow_down.setToCall(this);

		echo_arrow_up = new ArrowButton(BasicArrowButton.NORTH);
		echo_arrow_up.setChange(1);
		echo_arrow_up.setType(ArrowButton.ECHO_ARROW);
		echo_arrow_up.setToCall(this);

		echo_arrow_down = new ArrowButton(BasicArrowButton.SOUTH);
		echo_arrow_down.setChange(-1);
		echo_arrow_down.setType(ArrowButton.ECHO_ARROW);
		echo_arrow_down.setToCall(this);

		// Next some not editable TextFields
		slice_max = new JTextField("/0");
		slice_max.setEditable(false);
		slice_max.setBackground(null);
		slice_max.setBorder(null);
		slice_max.addMouseWheelListener(this);
		GUI.setfinalSize(slice_max, new Dimension((int) (parent.width / 14.67),
				(int) (parent.height / 5.4)));

		max_echo = new JTextField("/0");
		max_echo.setEditable(false);
		max_echo.setBackground(null);
		max_echo.setBorder(null);
		max_echo.addMouseWheelListener(this);
		GUI.setfinalSize(max_echo, new Dimension((int) (parent.width / 14.67),
				(int) (parent.height / 5.4)));

		slice_index = new JTextField("0");
		slice_index.setName("SliceIndex");
		slice_index.setEditable(false);
		slice_index.addMouseWheelListener(this);
		slice_index.addKeyListener(this);
		slice_index.addCaretListener(this);
		GUI.setfinalSize(slice_index, new Dimension(
				(int) (parent.width / 14.67), (int) (parent.height / 5.4)));

		echo_index = new JTextField("0");
		echo_index.setName("EchoIndex");
		echo_index.setEditable(false);
		echo_index.addMouseWheelListener(this);
		echo_index.addKeyListener(this);
		echo_index.addCaretListener(this);
		GUI.setfinalSize(echo_index, new Dimension(
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
		arrows_slice.add(slice_arrow_up);
		arrows_slice.add(slice_arrow_down);
		arrows_slice.addMouseWheelListener(this);
		GUI.setfinalSize(arrows_slice, new Dimension(parent.width / 50,
				(int) (parent.height / 13.5)));

		arrows_echo = new JPanel();
		arrows_echo.setLayout(new BoxLayout(arrows_echo, BoxLayout.PAGE_AXIS));
		arrows_echo.add(echo_arrow_up);
		arrows_echo.add(echo_arrow_down);
		arrows_echo.addMouseWheelListener(this);
		GUI.setfinalSize(arrows_echo, new Dimension(parent.width / 50,
				(int) (parent.height / 13.5)));

		// creating the output field
		outputArea = new JTextArea("");
		outputArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		outputScroller = new JScrollPane(outputArea);
		outputScroller.setPreferredSize(new Dimension(parent.width / 11,
				(int) (parent.height / 5.4)));
		outputScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outputScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		outputScroller.setAutoscrolls(false);

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
		Component[] imgstuff = { slice, arrows_slice, slice_index, slice_max,
				echo, arrows_echo, echo_index, max_echo };
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

		fittingfunction = new JComboBox<String>();
		String help[] = { "", "st", "nd", "rd", "th", "th" };
		for (int i = 0; i < 5; i++) {
			fittingfunction.addItem("Polynomial (" + i + help[i] + " order)");
		}
		fittingfunction.addItem("Polynomial (n order)");
		fittingfunction.addItem("Exponential");
		fittingfunction.setSelectedIndex(1);
		fittingfunction.addActionListener(this);
		GUI.setfinalSize(fittingfunction, new Dimension(
				(int) (parent.width / 5.5), (int) (parent.height / 15)));

		// Calc zero echo
		zero_echo = new JButton("Calculate Zero Echo");
		zero_echo.addActionListener(this);
		GUI.setfinalSize(zero_echo, new Dimension((int) (parent.width / 5.5),
				parent.height / 25));

		JPanel dimselection = new JPanel();
		dimselection
				.setLayout(new BoxLayout(dimselection, BoxLayout.LINE_AXIS));
		dimselection.add(text_dim);
		dimselection.add(fittingfunction);
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
		fittingImage = new BufferedImage(
				(int) (parent.width * roitabwidth * 7. / 10),
				(int) (parent.height / 2), BufferedImage.TYPE_4BYTE_ABGR);
		// The ImageIcon is kinda a wrapper for the image
		fittingImageWrapper = new ImageIcon(fittingImage);
		// imagepanel wrapps the ImageIcon
		JLabel roilabel = new JLabel(fittingImageWrapper);
		new DropTarget(roilabel, this);
		// roilabel.setDropTarget(dnd);
		GUI.setfinalSize(roilabel,
				new Dimension((int) (parent.width * roitabwidth),
						(int) (fittingImage.getHeight())));
		JPanel roiimg = new JPanel();
		roiimg.add(roilabel);
		GUI.setfinalSize(roiimg,
				new Dimension((int) (parent.width * roitabwidth),
						(int) (fittingImage.getHeight())));

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
		radius.setMinimum(1);
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
				(int) (parent.width * roitabwidth), (int) (parent.height / 15)));

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
		toppanel.add(imagepanel);
		toppanel.add(Box.createRigidArea(new Dimension(parent.width / 110,
				(int) (parent.height / 1.2))));
		toppanel.add(roiPanel);

		// Some this stuff
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.add(toppanel);
		GUI.setfinalSize(this, new Dimension(parent.width,
				(int) (parent.height / 1.2)));

		JPopupMenu popup = new JPopupMenu();
		popup.add(new JMenuItem("Hallo"));

		roilabel.addMouseListener(new PopupListener(popup));

		this.setVisible(true);
	}

	/**
	 * Action Command, that is called, when the actual shape is changed through
	 * the JComboBox in the Gui.
	 */
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

	/**
	 * This method is called by the 5 buttons of VolumeTab.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
			switch (creatingTextStatus) {
			case 0:
				parent.getStatusLabel().setText("Creating");
				break;
			case 1:
				parent.getStatusLabel().setText("Creating.");
				break;
			case 2:
				parent.getStatusLabel().setText("Creating..");
				break;
			case 3:
				parent.getStatusLabel().setText("Creating...");
				break;
			}
			creatingTextStatus += 1;
			creatingTextStatus %= 4;
		}
		if (creatingVolume) {
			return;
		}
		switch (e.getActionCommand()) {
		case "Calculate Zero Echo":
			if (ze == null) {
				actionCalculateZeroEcho();
			}
			break;
		case "Cancle":
			if (ze != null) {
				ze.Cancle();
				ze = null;
			}
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
		case "Image Changed":
			contrastupdate = true;
			displayImage();
			break;
		case "Save ZeroEcho":
			saveZeroEcho();
			break;
		case "Adjust Brightness/Contrast":
			if (volume == null) {
				return;
			}
			contrast = new ContrastAdjuster();
			contrast.setImageUpdater(this);
			contrast.run("");
			try {
				ImagePlus imp = null;
				if (getActualEcho() != 0) {
					imp = this.volume.getSlice(actualSliceIndex()).getData();
				} else {
					imp = zeroecho.get(getActualSlice() - 1);
				}

				if (contrast != null && contrast.thread != null) {
					WindowManager.setTempCurrentImage(volume.getData().get(0));
					WindowManager.setTempCurrentImage(contrast.thread, imp);
					contrast.adjustmentValueChanged(new AdjustmentEvent(
							DummyAdjuster.dummy, 0, 0, 0));
					sleep(20); // waiting for the ContrastAdjuster
				}
			} catch (IOException | NullPointerException e1) {
			}
			break;
		default:
			if (e.getSource() == fittingfunction) {
				if (relativroi != null) {
					showROI(true);
				}
			}
			break;
		}
	}

	/**
	 * Action command to calculate the ZeroEcho. This Method is called by the
	 * "CalculateZeroEcho Button".
	 */
	public void actionCalculateZeroEcho() {
		int degree = -1;
		if (((String) fittingfunction.getSelectedItem())
				.contains("Exponential")) {
			degree = -2;
		} else {
			for (int i = 0; i < 5; i++) {
				if (((String) fittingfunction.getSelectedItem()).contains(""
						+ i)) {
					degree = i;
				}
			}
		}

		zero_echo.setText("Cancle");

		parent.getStatusLabel().setText("Calculating ZeroEcho: ");
		parent.getProgressBar().setValue(0);
		parent.getProgressBar().setMaximum(perEcho);
		parent.getProgressBar().setVisible(true);

		ze = new ZeroEcho(volume, this, degree, alsolog.isSelected(), this);
		new Thread(ze).start();
	}

	/**
	 * ActionEvent, that is called, when the browse Button is pressed.
	 */
	public void actionBrowse() {
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
				&& chooser.getSelectedFile() != null) {
			if (chooser.getSelectedFile().isDirectory()) {
				path.setText(chooser.getSelectedFile().toString());
			} else if (chooser.getSelectedFile().isFile()) {
				if (!chooser.getSelectedFile().getAbsolutePath()
						.endsWith("nii")) {
					open(chooser.getSelectedFile().getParent()
							.toString());
				} else {
					open(chooser.getSelectedFile().getAbsolutePath());
				}
			}
//			new Thread(this).start();
		}
		parent.imec.setOption("LastBrowse", chooser.getCurrentDirectory()
				.getAbsolutePath());
		parent.imec.save();
	}
	
	public void open(String filename) {
		path.setText(filename);
		new Thread(this).start();
	}

	/**
	 * Method, that displays the Attributes. That method is called, when the
	 * "Display all" Button is pressed.
	 */
	public void actionDisplayAttributes() {
		displayAll = true;
		displayAttributes();
	}

	/**
	 * This method is called, when the Button "Open in External" is pressed.
	 * This Method executes a command, that is specified in the
	 * ImageExtractor.config.
	 */
	public void actionOpenInExternal() {
		// if (volume != null) {
		//parent.imec = new ImageExtractorConfig();
		String customExternal = null;
		if (path.getText().endsWith(".nii")) {
			customExternal = parent.imec.getOption("External_NIFTI");
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

	/**
	 * With this Method you can change the actual echo by i.
	 * 
	 * @param change
	 *            the number that is added to the actual echo.
	 */
	public void addtoEcho(int change) {
		try {
			int actecho = getActualEcho();
			int lowest = zeroecho == null ? 1 : 0;
			if (actecho + change > lowest - 1
					&& actecho + change <= echoNumbers) {
				echo_index.setText(""
						+ (Integer.parseInt(echo_index.getText()) + change));
				checkEcho();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * With this Method you can change the actual slice by i.
	 * 
	 * @param change
	 *            the number that is added to the actual slice.
	 */
	public void addtoSlice(int change) {
		try {
			if (!(getActualSlice() + change < 1 || getActualSlice() + change > perEcho)) {
				slice_index.setText(""
						+ (Integer.parseInt(slice_index.getText()) + change));
				checkSlice();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method calculates the radius of the roi. This value depends on the
	 * given radius and the given unit.
	 * 
	 * @return the radius of the roi in pixels
	 */
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

	public void caretUpdate(CaretEvent e) {
		Object source = e.getSource();
		if (source == slice_index) {
			checkSlice();
			displayImage();
		} else if (source == echo_index) {
			checkEcho();
			displayImage();
		} else if (source == filter) {
			if (!filter.getText().equals(lastfilter)) {
				lastfilter = filter.getText();
				displayAll = false;
				displayAttributes();
			}
		}
		displayAttributes();
	}

	/**
	 * Method that tries to create a Volume to the given path.
	 */
	public void createVolume() {
		creatingVolume = true;
		if (path.getText() == "") {
			return;
		}
		try {
			zeroecho = null;

			timer.start();
			volume = Volume.createVolume(path.getText());
			timer.stop();

			if (volume == null) {
				throw new RuntimeException();
			}
			path.setText(volume.getPath());

			volume.getTextOptions().setReturnExpression(
					TextOptions.ATTRIBUTE_VALUE + "");

			// Default Index
			slice_index.setText("1");
			echo_index.setText("1");
			displayAttributes();
			// User can change fields again
			slice_index.setEditable(true);
			echo_index.setEditable(true);

			// Getting some values
			echoNumbers = Integer.parseInt(volume.getAttribute(
					KeyMap.KEY_ECHO_NUMBERS_S, volume.size() - 1));
			perEcho = volume.size() / echoNumbers;
			max_echo.setText("/" + echoNumbers);
			slice_max.setText("/" + perEcho);

			index_slider.setMinimum(1);
			index_slider.setMaximum(perEcho);
			index_slider.setValue(1);
			echo_slider.setMinimum(1);
			echo_slider.setMaximum(echoNumbers);
			echo_slider.setValue(1);

			slice_index.requestFocus();
			showROI(false);
			displayImage();

			// A Way, how the combobox could be filled
			// TextOptions oldopt = volume.getTextOptions();
			// TextOptions newopt = new TextOptions();
			//
			// int[] searchopt = { TextOptions.ATTRIBUTE_NAME };
			// newopt.setSearchOptions(searchopt);
			// newopt.setReturnExpression(TextOptions.ATTRIBUTE_NAME + "");
			//
			// volume.setTextOptions(newopt);
			// for (KeyMap att : KeyMap.values()) {
			// String name = volume.getAttribute(att, false).replace("\n", "");
			// if (!name.replace(" ", "").equals("")) {
			// System.out.println(name);
			// }
			// }
			// volume.setTextOptions(oldopt);
		} catch (RuntimeException ert) {
			// thrown by createVolume() if it didn't worked.
			slice_index.setEditable(false);
			slice_index.setText("0");
			slice_max.setText("/0");

			echo_index.setEditable(false);
			echo_index.setText("0");
			max_echo.setText("/0");

			if (image != null) {
				image = new BufferedImage((int) (image.getWidth()),
						(int) (image.getHeight()), BufferedImage.TYPE_3BYTE_BGR);
				imgicon.setImage(image);
				imagelabel.setIcon(imgicon);
				showROI(false);
			}
			repaint();
		} finally {
			creatingVolume = false;
			if (timer.isRunning()) {
				timer.stop();
			}
			parent.getStatusLabel().setText("");
			save.setEnabled(false);
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

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			dtde.acceptDrop(dtde.getDropAction());
			List<File> dropppedFiles = (List<File>) dtde.getTransferable()
					.getTransferData(DataFlavor.javaFileListFlavor);
			for (File file : dropppedFiles) {
				if (file.isDirectory()) {
					path.setText(file.getAbsolutePath());
				} else {
					if (file.getName().endsWith(".nii")) {
						path.setText(file.getAbsolutePath());
					} else {
						path.setText(file.getParent());
					}
				}
				new Thread(this).start();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to read out the slice Textfield. This Method is specially used to
	 * have a checked Exception, in case the User give us an unqualifiet input.
	 * 
	 * @return The Number, that is written in the slice textfield.
	 * @throws IOException
	 *             when the slice field contains a character, that is not a
	 *             Digit or if there is no character.
	 */
	public int getActualSlice() throws IOException {
		try {
			return Integer.parseInt(slice_index.getText());
		} catch (NumberFormatException e) {
			throw new IOException(e.getMessage()); // Forcing myself, to catch
													// Exceptions
		}
	}

	/**
	 * Method to read out the echo Textfield. This Method is specially used to
	 * have a checked Exception, in case the User give us an unqualified input.
	 * 
	 * @return The Number, that is written in the slice textfield.
	 * @throws IOException
	 *             when the slice field contains a character, that is not a
	 *             Digit or if there is no character.
	 */
	public int getActualEcho() throws IOException {
		try {
			return Integer.parseInt(echo_index.getText());
		} catch (NumberFormatException e) {
			throw new IOException(e.getMessage()); // Forcing myself, to catch
													// Exceptions
		}
	}

	/**
	 * Returns the current Image.
	 * 
	 * @return The current Image.
	 */
	public ImagePlus getImage() {
		try {
			if (getActualEcho() != 0) {
				return this.volume.getSlice(actualSliceIndex()).getData();
			} else {
				return zeroecho.get(getActualSlice() - 1);
			}
		} catch (IOException | NullPointerException e) {
		}
		return null;
	}

	/**
	 * The Scale, that had been used, to scale the Image.
	 * 
	 * @return The scale for the width and height of the Image.
	 */
	public double getScale() {
		return scaling;
	}

	/**
	 * This method is used by the GUI to find easily out, which Tab the user is
	 * using right now.
	 * 
	 * @return The Name, that can be used, to represent this Tab.
	 */
	public String getClassName() {
		return "VolumeTab";
	}

	/**
	 * This Method returns an intern boolean, that contains the Information, if
	 * a volume is in creation or not.
	 * 
	 * @return true if volume is in creation; false otherwise
	 */
	public boolean isCreatingVolume() {
		return creatingVolume;
	}

	/**
	 * Returns, if the outputarea is empty.
	 * 
	 * @return True if the outputarea is empty; false otherwise
	 */
	public boolean isOutputAreaEmpty() {
		return outputArea.getText().equals("");
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

			if (echo_index.hasFocus()) {
				addtoEcho(change);
			} else {
				addtoSlice(change);
			}
			lastpressed = System.currentTimeMillis();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	}

	/**
	 * This Method is used, to request the needed size by the GUI.
	 */
	public void neededSize() {
		parent.requestWidth(preferedWidth(), this);
	}

	/**
	 * Method that handles mouseWheel input.
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (System.currentTimeMillis() - lastpressed > 50) {
			Object obj = e.getSource();
			int change = -1 * e.getWheelRotation();
			boolean makechange = false;
			boolean changeEcho = false;

			if (obj instanceof JTextField) {
				JTextField index = (JTextField) obj;
				if (index.equals(slice_index) || index.equals(slice_max)
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
					if (echo_index.hasFocus()) {
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
					addtoEcho(change);
				} else {
					addtoSlice(change);
				}
			}
			lastpressed = System.currentTimeMillis();
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this.imagelabel && volume != null) {
			setRoiPosition(e.getX(), e.getY());
		}
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {
		if (volume != null && !creatingVolume && ze == null) {
			parent.getStatusLabel().setText("");
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getSource().equals(this.imagelabel) && volume != null) {
			setRoiPosition(e.getX(), e.getY());
		}
		if (volume != null && !creatingVolume && ze == null
				&& e.getSource().equals(imagelabel)) {
			float value = getImage().getProcessor().getf(
					(int) (e.getX() / scaling), (int) (e.getY() / scaling));

			String val = "value = " + value;
			String x = "x = " + e.getX();
			String y = "y = " + e.getY();
			String seperator = ", ";

			parent.getStatusLabel()
					.setText(x + seperator + y + seperator + val);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (volume != null && !creatingVolume && ze == null
				&& e.getSource().equals(imagelabel)) {
			float value = getImage().getProcessor().getf(
					(int) (e.getX() / scaling), (int) (e.getY() / scaling));
			String val = "value = " + value;
			String x = "x = " + e.getX();
			String y = "y = " + e.getY();
			String seperator = ", ";
			parent.getStatusLabel()
					.setText(x + seperator + y + seperator + val);
		}
	}

	@Override
	public void onFocus() {
		parent.requestWidth(preferedWidth(), this);
		parent.getJMenuBar().add(imagemenu);
		parent.getJMenuBar().repaint();
	}

	@Override
	public void onExit() {
		parent.getJMenuBar().remove(imagemenu);
		parent.getJMenuBar().repaint();
	}

	/**
	 * This method returns the preferedWidth of this Tab.
	 * 
	 * @return the preferedWidth of this Tab
	 */
	public int preferedWidth() {
		if (volume == null) {
			return parent.width;
		} else {
			int neededwidth = image.getWidth() + (int) (parent.width / 1.6923)
					+ 20;
			if (relativroi != null) {
				neededwidth += (int) (parent.width * roitabwidth);
			}
			return neededwidth;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof ZeroEcho) {
			parent.getProgressBar().setValue((int) evt.getNewValue());
		}
	}

	/**
	 * Creating Volumes this way, so the window wont freeze.
	 */
	public void run() {
		createVolume();
	}

	/**
	 * This method resizing the roi.
	 */
	public void resizeROI() {
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
	}

	/**
	 * Updates the RoiTab.
	 * 
	 * @param visible
	 *            wish, that the roi tab should be visible or not.
	 */
	public void showROI(boolean visible) {
		if (visible && !max_echo.getText().equals("/1") && relativroi != null) {
			parent.requestWidth(preferedWidth(), this);
			GUI.setfinalSize(toppanel, new Dimension(preferedWidth(),
					parent.height));

			if (vf == null) {
				vf = new VolumeFitter();
			}
			int degree = -1;
			if (((String) fittingfunction.getSelectedItem())
					.contains("Exponential")) {
				degree = -2;
			} else {
				for (int i = 0; i < 5; i++) {
					if (((String) fittingfunction.getSelectedItem())
							.contains("" + i)) {
						degree = i;
					}
				}
			}
			try {
				drawIntoImage(fittingImage, vf.getPlot(this.volume,
						this.relativroi, getActualSlice() - 1, degree,
						alsolog.isSelected(), fittingImage.getWidth(),
						fittingImage.getHeight(), getActualEcho()));
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
			if (volume != null) {
				volume.setRoi(null);
			}
			relativroi = null;
			roiPanel.setVisible(false);
			ownExtended = false;
			parent.requestWidth(preferedWidth(), this);
		}

		this.repaint();
	}

	/**
	 * This Method sets the actual roi Shape. Actual valid shape names are:
	 * Point, Circle and Sphere.
	 * 
	 * @param shape
	 *            The String name of the Shape.
	 */
	public void setShape(String shape) {
		for (int i = 0; i < this.shape.getItemCount(); i++) {
			if (this.shape.getItemAt(i).equals(shape)) {
				this.shape.setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * Method, that calls a JFileChooser and saves the ZeroEcho + the other
	 * Echos as a Nifty, as the selected File. If the filename don't ends with
	 * ".nii", than the extension ".nii" is added.
	 */
	public void saveZeroEcho() {
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION
				&& chooser.getSelectedFile() != null) {
			Nifti_Writer writer = new Nifti_Writer();

			ImageStack is = null;
			ImagePlus ip = new ImagePlus();

			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;

			ArrayList<ImagePlus> concat = new ArrayList<ImagePlus>();
			concat.addAll(zeroecho);
			for (int i = 0; i < volume.size(); i++) {
				concat.add(volume.getSlice(i).getData());
			}

			is = new ImageStack(volume.getSlice(volume.size() - 1).getData()
					.getWidth(), volume.getSlice(volume.size() - 1).getData()
					.getHeight());
			ip.setCalibration(volume.getSlice(volume.size() - 1).getData()
					.getCalibration());

			for (ImagePlus nextimp : concat) {

				min = Math.min(nextimp.getProcessor().getMin(), min);
				max = Math.max(nextimp.getProcessor().getMax(), max);

				try {
					is.addSlice(nextimp.getProcessor());
				} catch (IllegalArgumentException e) {
					continue;
				}
			}

			ip.setStack(is);
			ip.getProcessor().setMinAndMax(min, max);
			if (echoNumbers != 1) {
				ip.setDimensions(1, is.getSize() / (echoNumbers + 1),
						echoNumbers + 1);
				ip.setOpenAsHyperStack(true);
			}
			WindowManager.setTempCurrentImage(ip);

			writer.dicom_to_nifti = false;
			String name = chooser.getSelectedFile().getName();
			if (!name.endsWith(".nii")) {
				name += ".nii";
			}
			writer.save(ip, chooser.getSelectedFile().getParent(), name);
		}
		parent.imec.setOption("LastBrowse", chooser.getCurrentDirectory()
				.getAbsolutePath());
		parent.imec.save();
	}

	/**
	 * Listener Method, that handles changes of some Gui Components.
	 */
	public void stateChanged(ChangeEvent e) {
		if (volume == null) { // This field shouldn't do anything if there is no
								// data.
			return;
		}

		if (e.getSource() == alsolog) {
			// Checkbox for logarithmic fitting
			showROI(true);

		} else if (e.getSource() == radius) {
			// Slider, that changes the roi radius
			radiustext.setText("Radius: " + radius.getValue());
			resizeROI();

		} else if (e.getSource() == unit) {
			// JComboBox, that decides the Unit of the roi radius
			resizeROI();

		} else if (e.getSource() == index_slider) {
			// Slider below the Image
			slice_index.setText("" + index_slider.getValue());
			displayImage();

		} else if (e.getSource() == echo_slider) {
			// Slider below the Image
			echo_index.setText("" + echo_slider.getValue());
			displayImage();

		}
	}

	/**
	 * This method set's the Path in the Textfield, that indicates the Path.
	 * 
	 * @param path
	 *            The path to a file or directory of a volume
	 */
	public void setPath(String path) {
		this.path.setText(path);
	}

	/***
	 * This Method gets called, whenever the ZeroEcho Class finished an ZeroEcho
	 * slice.
	 * 
	 * @param zeroecho
	 *            The computed ZeroEcho of the of the volume
	 * @param fittingfunction
	 *            The Function, that was used
	 */
	public void setZeroEcho(ArrayList<ImagePlus> zeroecho,
			String fittingfunction) {
		this.zeroecho = zeroecho;
		if (zeroecho == null) {
			max_echo.setText(max_echo.getText().replace(" + 0", ""));
		} else {
			max_echo.setText(max_echo.getText().replace(" + 0", "") + " + 0");

			int i = 0;
			for (ImagePlus imp : zeroecho) {
				imp.setCalibration(volume.getSlice(i).getData()
						.getCalibration());
			}

			save.setEnabled(true);
		}
		parent.getStatusLabel().setText("");
		parent.getProgressBar().setVisible(false);
		zero_echo.setText("Calculate Zero Echo");
		ze = null;
	}

	/**
	 * This method is used, to splitt a command, before executing it.
	 * 
	 * @param command
	 *            The Command, that should be executed
	 * @return The array, that can be executed than
	 */
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

	/**
	 * Sets the Roi position to the specified position or creates a Roi
	 * 
	 * @param x
	 *            The x coordinate of the roi
	 * @param y
	 *            The y coordinate of the roi
	 */
	public void setRoiPosition(int x, int y) {
		try {
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

	/**
	 * Sets the Roi position to the specified position or creates a Roi
	 * 
	 * @param x
	 *            The x coordinate of the roi
	 * @param y
	 *            The y coordinate of the roi
	 * @param z
	 *            The z coordinate of the roi
	 */
	public void setRoiPosition(int x, int y, int z) {
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
	}

	/**
	 * Sleeps for <i>int</i> milliseconds. The InterruptedException can cancel
	 * this Method. This Method is useful to write less code.
	 * 
	 * @param millisec
	 *            The amount of time, that the current Thread should sleep.
	 */
	public void sleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e1) {
		}
	}

	/***
	 * This Method checks if a ZeroEcho slice is calculated already to a given
	 * Fitting function. This Method also sets a little "+ 0" to the max_echo
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

	/**
	 * This Method calculates the actual ImageNumber with the formula: slice - 1
	 * + perEcho * (echo - 1).
	 * 
	 * @return The actual ImageNumber.
	 * @throws IOException
	 *             If the actual Textfield information (slice, echo) is not
	 *             valid.
	 */
	private int actualSliceIndex() throws IOException {
		return getActualSlice() - 1 + perEcho * (getActualEcho() - 1);
	}

	/**
	 * This Method checks, if the EchoTextfield contains a valid String. If The
	 * String is not valid, than the actual String is set to 0. If the actual
	 * String is a valid Number and this Number is Higher than the number of
	 * echos, than the String is set to the number of echos. If the Number
	 * Number is to small, than it's set to 0 or 1, whether there is a ZeroEcho
	 * or not.
	 * 
	 * If the Number in bounds, than this method won't have any effekt.
	 */
	private void checkEcho() {
		Runnable handlechange = new Runnable() {
			public void run() {
				int act = 0;
				try {
					act = Integer.parseInt(echo_index.getText());
				} catch (NumberFormatException e) {
					String newtext = echo_index.getText().replaceAll("[^\\d]",
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
				CaretListener[] listener = echo_index.getCaretListeners();
				if (listener.length != 0) {
					for (CaretListener l : listener) {
						echo_index.removeCaretListener(l);
					}
					echo_index.setText(act + "");
					for (CaretListener l : listener) {
						echo_index.addCaretListener(l);
					}
				}
			}
		};

		SwingUtilities.invokeLater(handlechange);
	}

	/**
	 * This Method checks, if the SliceTextfield contains a valid String. If The
	 * String is not valid, than the actual String is set to 0. If the actual
	 * String is a valid Number and this Number is Higher than the number of
	 * echos, than the String is set to the number of echos. If the Number
	 * Number is to small, than it's set to 0 or 1, whether there is a ZeroEcho
	 * or not.
	 * 
	 * If the Number in bounds, than this method won't have any effect.
	 */
	private void checkSlice() {
		Runnable handlechange = new Runnable() {
			public void run() {
				int act = 0;
				try {
					act = Integer.parseInt(slice_index.getText());
				} catch (NumberFormatException e) {
					String newtext = slice_index.getText().replaceAll("[^\\d]",
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
				CaretListener[] listener = slice_index.getCaretListeners();
				if (listener.length != 0) {
					for (CaretListener l : listener) {
						slice_index.removeCaretListener(l);
					}
					slice_index.setText(act + "");
					for (CaretListener l : listener) {
						slice_index.addCaretListener(l);
					}
				}
			}
		};

		SwingUtilities.invokeLater(handlechange);
	}

	/**
	 * This Method updates/displays the Image in the Gui.
	 * 
	 * @return true if the Image could be displayed correctly; false otherwise.
	 */
	private boolean displayImage() {
		try {
			if (volume == null
					|| (actualSliceIndex() < 0 || actualSliceIndex() >= volume
							.size())
					&& !(getActualEcho() == 0 && zeroecho != null)) {
				return false;
			}

			ImagePlus imp = getImage();

			imp.setPosition(1, getActualSlice(), getActualEcho());

			if (contrast != null && contrast.thread != null && !contrastupdate
					&& contrast.isVisible()) {
				WindowManager.setTempCurrentImage(contrast.thread, imp); // dirty
																			// solution
				contrast.apply();
				sleep(20); // waiting for the ContrastAdjuster
				return true;
			}

			contrastupdate = false;

			BufferedImage buff = imp.getBufferedImage();

			scaling = ((double) image.getHeight()) / buff.getHeight();

			image = new BufferedImage((int) (buff.getWidth() * scaling),
					(int) (buff.getHeight() * scaling), buff.getType());
			drawIntoImage(image, buff);

			GUI.setfinalSize(imagelabel,
					new Dimension(image.getWidth(), image.getHeight()));
			GUI.setfinalSize(imagepanel, new Dimension(image.getWidth(),
					imagepanel.getHeight()));
			imgicon.setImage(image);
			imagelabel.setIcon(imgicon);
			GUI.setfinalSize(index_slider, new Dimension(image.getWidth(),
					index_slider.getHeight()));
			GUI.setfinalSize(echo_slider, new Dimension(image.getWidth(),
					echo_slider.getHeight()));

			showROI(true);
			if (relativroi != null) {
				if (relativroi instanceof Roi3D) {
					((Roi3D) relativroi).draw(volume, image,
							getActualSlice() - 1, scaling);
				} else {
					relativroi.draw(image.getGraphics());
				}
			}
			repaint();
			return true;
		} catch (IOException e) {
			return false; // couldnt read the info from the gui elements (slice
							// or echo)
		}
	}

	/**
	 * Method for displaying all Attriubtes or display some choosen Attributes.
	 */
	private void displayAttributes() {
		try {
			if (volume != null && !volume.isEmpty()) {
				if (actualSliceIndex() < 0) {
					return;
				}

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
					for (String str : volume.getSlice(actualSliceIndex())
							.getHeader().split("\n")) {
						if (!filter.getText().equals("")) {
							for (String part : filter.getText().split("\\|")){
								if (str.toLowerCase().contains(
										part.trim().toLowerCase())) {
									outputstring.append(str + "\n");
									break;
								}
							}
						} else {
							if (!str.contains("--:")) {
								outputstring.append(str + "\n");
							}
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

	/**
	 * This Method draws the source BufferedImage into the target BufferedImage.
	 * The source is always scaled to the needed size, using the BufferedImage
	 * Method "getScaledInstance", with the BufferedImage.SCALE_FAST hint.
	 * 
	 * @param target
	 *            The BufferedImage, that should be copied/scaled into another.
	 * @param source
	 *            The BufferedImage, that contains the scaled target
	 *            BufferedImage.
	 */
	private static void drawIntoImage(BufferedImage target, BufferedImage source) {
		java.awt.Graphics gr = target.getGraphics();
		gr.drawImage(source.getScaledInstance(target.getWidth(),
				target.getHeight(), BufferedImage.SCALE_FAST), 0, 0, null);
	}

}