package gui;

import ij.ImageJ;
import ij.plugin.DragAndDrop;
import imagehandling.KeyMap;
import imagehandling.TextOptions;
import imagehandling.Volume;

import java.awt.Color;
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
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import polyfitter.Point2D;

/**
 * This class representing a Tab in the GUI window, where you can look up
 * the header and images of a Volume.
 * 
 * @author dridder_local
 *
 */
public class VolumeTab extends JPanel implements ActionListener, MyTab,
		ChangeListener, MouseWheelListener, MouseListener, KeyListener,
		Runnable {

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
	 * The Current ImageJ Frame
	 */
	private ImageJ imgj;

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

	private Point2D roi;

	private JCheckBox alsolog;

	private RotatePanel leg_gray;
	/**
	 * Standard Constructur.
	 */
	public VolumeTab(JFileChooser filechooser, GUI gui) {
		parent = gui;

		// The path for the Volume
		path = new JTextField("");
		path.addKeyListener(this);
		path.setEditable(false);
		path.setBackground(null);
		GUI.setfinalSize(path, new Dimension(500, 100));

		// The Attribute Filter
		filter = new JTextField("");
		filter.addKeyListener(this);
		GUI.setfinalSize(filter, new Dimension(500, 100));

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
		open_imagej = new JButton("open in Imagej");
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
		
		// image
		roiimage = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
		// The ImageIcon is kinda a wrapper for the image
		roiimgicon = new ImageIcon(roiimage);
		// imagepanel wrapps the ImageIcon
		roilabel = new JLabel(roiimgicon);

		// Checkbox for showing also the log evaluation
		alsolog = new JCheckBox();
		alsolog.addChangeListener(this);
		
		// Log Checkbox text
		JTextField logtext = new JTextField("also log (GREEN)");
		logtext.setEditable(false);
		logtext.setBackground(null);
		logtext.setBorder(null);
		GUI.setfinalSize(logtext, new Dimension(200, 100));
		
		// log Checkbox + text
		JLabel loglabel = new JLabel();
		loglabel.setLayout(new BoxLayout(loglabel, BoxLayout.LINE_AXIS));
		loglabel.add(alsolog);
		loglabel.add(logtext);
		GUI.setfinalSize(loglabel, new Dimension(300, 100));
		
		// Putting the roi Panel together
		roiPanel = new JPanel();
		roiPanel.setLayout(new BoxLayout(roiPanel, BoxLayout.PAGE_AXIS));
		GUI.setfinalSize(roiPanel, new Dimension(400, 1100));
		Component[] roistuff = { Box.createRigidArea(new Dimension(0, 50)),
				roilabel,legWrapper, Box.createRigidArea(new Dimension(0, 50)), loglabel };
		GUI.addComponents(roiPanel, roistuff);
		roiPanel.setVisible(false);

		// Putting everything together now
		toppanel = new JPanel();
		toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.LINE_AXIS));
		GUI.setfinalSize(toppanel, new Dimension(1100, 450));
		toppanel.add(leftSidePanel);
		toppanel.add(imagelabel);
//		toppanel.add(Box.createRigidArea(new Dimension(35, 0)));
		toppanel.add(leg_gray);
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
	private void createVolume() {
		creatingVolume = true;
		try {
			// speciall Constructur which throws an Exception if new Volume
			// fails, instead of calling System.exit(1)
			volume = new Volume(path.getText(), this);

			// Default Index
			actual_slice = 1;
			actual_echo = 1;
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
			ert.printStackTrace();
		}
		creatingVolume = false;
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
					String header = volume
							.getSlice(
									Integer.parseInt(index_slice.getText())
											- 1
											+ perEcho
											* (Integer.parseInt(index_echo
													.getText()) - 1))
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
		if (e.getSource() == alsolog){
			showROI(true);
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
		parent.setExtendedWindow(this.ownExtended);
		if (ownExtended) {
			GUI.setfinalSize(parent, new Dimension(1400, 550));
		}
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
					actual_slice = Integer.parseInt(this.index_slice.getText());
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
						// max 20 changes per second
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
					actual_echo = Integer.parseInt(this.index_echo.getText());
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
						// max 20 changes per second
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// one arrow still getting pressed?
					if (!(arrow_up_echo.getModel().isPressed() || arrow_down_echo
							.getModel().isPressed())
							| (change_echo < 0 && index_echo.getText().equals(
									"1"))) {
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
							|| !lasttime_echo.equals(this.index_echo.getText())) {
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
		BufferedImage orig = this.volume.getSlice(actualSliceIndex()).getData();
		java.awt.Graphics gr = this.image.getGraphics();
		gr.drawImage(
				this.volume
						.getSlice(actualSliceIndex())
						.getData()
						.getScaledInstance(this.image.getWidth(),
								this.image.getHeight(),
								BufferedImage.SCALE_FAST), 0, 0, null);
		if (this.roi != null) {
			gr.setColor(Color.RED);
			gr.drawRect((int) (this.roi.getX() * this.image.getHeight() / orig
					.getHeight()),
					(int) (this.roi.getY() * this.image.getWidth() / orig
							.getWidth()), 1, 1);
			showROI(true);
		}
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

	private void showROI(boolean visible){
		if (visible){
		VolumeFitter vf = new VolumeFitter();
		this.roiimage.getGraphics().drawImage(
				vf.getPlot(this.volume, roi, this.actual_slice - 1,
						alsolog.isSelected()).getScaledInstance(
						this.roiimage.getWidth(), this.roiimage.getHeight(),
						BufferedImage.SCALE_AREA_AVERAGING), 0, 0, null);
		roiPanel.setVisible(true);
		if (!ownExtended) {
			parent.setExtendedWindow(true);
			ownExtended = true;
			leg_gray.setVisible(true);
			GUI.setfinalSize(toppanel, new Dimension(1400, 450));
			GUI.setfinalSize(parent, new Dimension(1450, 550));
		}
		}else{
			roi = null;
			roiPanel.setVisible(false);
			if (ownExtended){
				parent.setExtendedWindow(false);
				ownExtended = false;
				leg_gray.setVisible(false);
				GUI.setfinalSize(toppanel, new Dimension(1100, 450));
				GUI.setfinalSize(parent, new Dimension(1100, 550));
			}
		}
		this.repaint();
	}
	
	public void mouseClicked(MouseEvent e) {
		BufferedImage orig = this.volume.getSlice(actualSliceIndex()).getData();

		roi = new Point2D(((double) e.getY()) / this.image.getWidth()
				* orig.getWidth(), ((double) e.getX()) / this.image.getHeight()
				* orig.getHeight());
		roi = new Point2D(roi.getY(), roi.getX());
		
		displayImage();
		showROI(true);
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

}