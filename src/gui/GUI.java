package gui;

import gui.sortertab.SorterTab;
import gui.volumetab.VolumeTab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.ImageExtractorConfig;

/**
 * This GUI is used, to look the Header and Images of Dicoms, to Search and Sort
 * Dicoms and to convert Dicoms to Niftis.
 * 
 * @author Dominik Ridder
 *
 */
public class GUI extends JFrame implements ActionListener, ChangeListener,
		Runnable, WindowListener {

	/**
	 * Boolean Value that can be used for Test's. Note that conditions like if
	 * (DEBUG) {...} can be optimised by the compiler in that way, that there is
	 * no if condition at this place.
	 */
	public static final boolean DEBUG = false;

	/**
	 * Main method, to start the GUI.
	 * 
	 * @param agrs
	 *            This value is currently ignored.
	 */
	public static void main(String[] args) {
		if (args.length >=1) {
			new GUI(true, true, args[0]);
		} else {
			new GUI(true, true);
		}
	}

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Int value that represents the right click Button on the Mouse.
	 */
	public static final int RIGHT_CLICK = MouseEvent.BUTTON3;
	
	/**
	 * Int value that represents the left click Button on the Mouse.
	 */
	public static final int LEFT_CLICK = MouseEvent.BUTTON1;

	/**
	 * The tabber managing the Tabs.
	 */
	private JTabbedPane tabber;

	/**
	 * If there is no Window anymore, the forceEnd boolean can call
	 * System.exit(1) to really force an end to all remaining Threads. If this
	 * GUI is just a part of another programm, than you should not force an end,
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
	 * This boolean contains the Information, whether the window have a extra
	 * width or not.
	 */
	boolean extendedWindow = false;

	/**
	 * This class is used, to load and save the config of the ImageExtractor.
	 */
	public ImageExtractorConfig imec;

	/**
	 * Width is the width of the GUI.
	 */
	public int width;

	/**
	 * Height is the height of the GUI.
	 */
	public int height;

	/**
	 * MenuBar, that contains the MenueOptions of the Gui displayed in the head
	 * of the Gui. The MenuBar containing options like creating a new Tab or a
	 * new Window.
	 */
	private JMenuBar menuBar;

	/**
	 * The Tab in the Tabbar, that is currently displayed in the Gui.
	 */
	private MyTab currentTab;

	/**
	 * The Panel of the Gui, that contains a Label for displaying Text and a
	 * Progressbar, that can show a Progress of a prozess as a bar.
	 */
	private JPanel statusPanel;

	/**
	 * This Label can be used, to displaying the Text in the Statusbar.
	 */
	private JLabel statusLabel;

	/**
	 * This Progressbar can be used to visualize the progress of a task at the
	 * Buttom of the Gui.
	 */
	private JProgressBar progressBar;

	/**
	 * This boolean is true, if the underlying System is a linux based OS.
	 */
	public static boolean islinux = System.getProperty("os.name").toLowerCase()
			.contains("linux");
	
	public GUI(boolean forceProgrammEndIfThereIsNoWindow, boolean visible, String filename) {
		this(forceProgrammEndIfThereIsNoWindow, visible);
//		filechooser.setCurrentDirectory(new File(fileChooserDirectory));
		if (tabber.getSelectedComponent() instanceof VolumeTab) {
			VolumeTab currentTab = (VolumeTab) tabber.getSelectedComponent();
			currentTab.open(filename);
		}
	}
	
	/**
	 * Constructs a new GUI. The GUI can be controlled with a Mouse or even
	 * based on Java code.
	 * 
	 * @param forceProgrammEndIfThereIsNoWindow
	 *            determines, if System.exit(1) is called, if all windows closed
	 * @param visible
	 *            Is used to control the visibility of the GUI. This can be
	 *            helpfull for testcases.
	 */
	public GUI(boolean forceProgrammEndIfThereIsNoWindow, boolean visible) {
		imec = new ImageExtractorConfig(System.getProperty("user.home")+".ImageExtractorConfig.cfg");
		this.addWindowListener(this);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		
		width = (int) (((double) d.width) / 2.5);
		height = d.height / 2;

		width = width < 1100 ? width : 1100;
		height = height < 550 ? height : 550;

		width = (islinux ? width : (int) (width * 1.5));
		// System.out.println(System.getProperty("os.name"));

		setfinalSize(this, new Dimension(width, height));

		filechooser = new JFileChooser();
		String startbrowse = imec.getOption("StartBrowse");
		if (startbrowse == null) {
			startbrowse = imec.getOption("LastBrowse");
		}
		if (startbrowse == null) {
			startbrowse = new java.io.File("$HOME").getAbsolutePath();
		}
		filechooser.setCurrentDirectory(new File(startbrowse));
		filechooser.setDialogTitle("Search Directory");
		filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		forceEnd = forceProgrammEndIfThereIsNoWindow;
		JMenu menu;
		JMenuItem newGuiWindow, newVolumeTab, newSortTab, imageSearch;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build menu in the menu bar.
		newVolumeTab = new JMenuItem("new Volume Tab");
		newVolumeTab.addActionListener(this);
		newSortTab = new JMenuItem("new Sort Tab");
		newSortTab.addActionListener(this);
		newGuiWindow = new JMenuItem("new Window");
		newGuiWindow.addActionListener(this);
		imageSearch = new JMenuItem("Search Images");
		imageSearch.addActionListener(this);
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.add(newGuiWindow);
		menu.add(newVolumeTab);
		menu.add(newSortTab);
		menu.addSeparator();
		menu.add(imageSearch);
		menuBar.add(menu);

		this.setJMenuBar(menuBar);

		// Menu stuff ends here

		tabber = new JTabbedPane();
		newTab(new VolumeTab(filechooser, this));
		newTab(new SorterTab(filechooser, this));
		tabber.setSelectedIndex(0);

		tabber.addChangeListener(this);
		add(tabber);
		setLocationRelativeTo(null);
		setTitle("ImageExtractor");
		setResizable(true);

		statusPanel = new JPanel();
		// statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 20));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel("");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		Rectangle bounds = progressBar.getBounds();
		bounds.height = 5;
		progressBar.setBounds(bounds);
		statusPanel.add(progressBar);

		statusPanel.setVisible(true);

		setVisible(visible);

		height -= statusPanel.getHeight();

		currentTab = (MyTab) tabber.getSelectedComponent();
		currentTab.onFocus();
	}

	/**
	 * Method, which is called by new Threads, to make life updates in the tabs.
	 * Each ImageExtractor Window has his own Thread this way.
	 */
	public void run() {
		lifeupdate();
	}

	/**
	 * Getter for the StatusLabel. The StatusLabel can be used to print any text
	 * information at the Bottom of the Gui into the Statusbar.
	 * 
	 * @return The StatusLabel of the Gui.
	 */
	public JLabel getStatusLabel() {
		return statusLabel;
	}

	/**
	 * Getter for the ProgressBar. The Progressbar can be use to display the
	 * progress of a Task in the Statusbar.
	 * 
	 * @return The ProgressBar of the Gui.
	 */
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * Method, to find, if the GUI is running.
	 * 
	 * @return true, if there are more than 0 window open; else false
	 */
	public boolean isAlive() {
		return windows != 0;
	}

	/**
	 * Setting the current displayed Tab of the GUI.
	 * 
	 * @param i
	 *            The Position of the tab in the Tabbar.
	 */
	public void setCurrentTab(int i) {
		this.tabber.setSelectedIndex(i);
	}

	/**
	 * This Method is called by the JMenuBar and the Buttons inside of the
	 * VolumeTab.
	 */
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "new Volume Tab":
			newTab(new VolumeTab(filechooser, this));
			break;
		case "new Sort Tab":
			newTab(new SorterTab(filechooser, this));
			break;
		case "new Window":
			new GUI(forceEnd, this.isVisible());
			break;
		case "Search Images":
			new ImageSearch();
			break;
		default:
			break;
		}
	}

	/**
	 * Creates a new Tab if there are less than 10 Tabs open.
	 * 
	 * @param comp
	 *            The Tab Component, that should be attached to the Tabbar
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
		if (comp instanceof VolumeTab) {
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

		// changing the Layout of the Tabs
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
	 * This Method was used, to change the Size of the GUI. This was used by the
	 * Sorter and VolumeTab, since the ROI Panel took additional space.
	 * 
	 * @param bool
	 *            if true, expanding the window; else making it smaller
	 */
	@Deprecated
	public void setExtendedWindow(boolean bool) {
		if (extendedWindow == bool) {
			return;
		}
		extendedWindow = bool;
	}

	/**
	 * Method, which is always running, to handle the lifeupdate of the tabs.
	 */
	@Deprecated
	private void lifeupdate() {
		while (this.isVisible()) {
			if (tabber.getTabCount() == 0) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
		}
	}

	/**
	 * Requesting a new Width Size for the GUI. This Method only works, if the
	 * Tab, that calls this Method, is the current focused Tab.
	 * 
	 * @param width
	 *            The new Width for the GUI.
	 * @param requester
	 *            The 'MyTab' that calls this method.
	 */
	public void requestWidth(int width, MyTab requester) {
		if (tabber.getComponentAt(tabber.getSelectedIndex()).equals(requester)) {
			if (width != this.getWidth()) {
				setfinalSize(this,
						new Dimension(width, height + statusPanel.getHeight()));
			}
		}
	}

	/**
	 * Secures, that a Component should stay in his Shape.
	 * 
	 * @param p
	 *            The Component, that gets modified
	 * @param d
	 *            The Dimension, the Component should fit
	 */
	public static void setfinalSize(Component p, Dimension d) {
		p.setMinimumSize(d);
		p.setMaximumSize(d);
		p.setPreferredSize(d);
		p.setSize(d);
	}

	/**
	 * Method to add a list of Components to a JPanel. This Method can be used,
	 * to create shorter code.
	 * 
	 * @param here
	 *            The JPanel, that should contain the Components at the end
	 * @param toadd
	 *            The Components, that are added to the JPanel
	 */
	public static void addComponents(JPanel here, Component[] toadd) {
		for (int i = 0; i < toadd.length; i++) {
			here.add(toadd[i]);
		}
	}

	/**
	 * Class for the Close Buttons in the Tab.
	 */
	class MyCloseActionHandler implements ActionListener {

		/**
		 * Important to track the Tab, that should be Observed.
		 */
		private String tabName;

		/**
		 * Construkutur, that initialize a new MyCloseActionHandler Object to
		 * the given Tab Name.
		 * 
		 * @param tabName
		 *            The Name of the Tab, that uses this CloseActionHandler.
		 */
		public MyCloseActionHandler(String tabName) {
			this.tabName = tabName;
		}

		/**
		 * Getter for the tabName.
		 * 
		 * @return The Name of the Tab, that is observed by this object.
		 */
		public String getTabName() {
			return tabName;
		}

		/**
		 * Simple ActionListener Method for close actions.
		 */
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
	 * This Method detects changes in the Tabbar. The Method onExit() gets
	 * called on the old Tab, if there was a old tab. Also the Method onFocus()
	 * is called at the new Tab.
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == tabber) {
			if (currentTab != null) {
				currentTab.onExit();
			}

			if (tabber.getSelectedIndex() != -1) {
				if (tabber.getComponentAt(tabber.getSelectedIndex()) instanceof MyTab) {
					currentTab = ((MyTab) tabber.getComponentAt(tabber
							.getSelectedIndex()));
					currentTab.onFocus();
				}
			}
		}
	}

	/**
	 * Getter for the JMenuBar of this Gui.
	 * 
	 * @return The JMenuBar at the bottom of the Gui.
	 */
	public JMenuBar getJMenueBar() {
		return menuBar;
	}

	/**
	 * This Method returns the current Tab.
	 * 
	 * @return The Current Tab if there is a Tab; null otherwiese.
	 */
	public MyTab getCurrentTab() {
		Component current = tabber.getSelectedComponent();
		if (current != null) {
			return (MyTab) current;
		} else {
			return null;
		}
	}

	/**
	 * This Method is used, to keep the number of Windows up to date.
	 */
	@Override
	public void windowOpened(WindowEvent e) {
		windows++;
	}

	/**
	 * This Method is used, to keep the number of Windows up to date. If all
	 * windows are closed and GUI constructur was called with GUI(true, x), than
	 * System.exit(1) is executed to make sure that this thread terminates.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		windows--;
		if (!this.isAlive() && forceEnd) {
			System.exit(1);
		}
	}

	/**
	 * Unused.
	 */
	@Override
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void windowIconified(WindowEvent e) {

	}

	/**
	 * Unused.
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	/**
	 * Unused.
	 */
	@Override
	public void windowActivated(WindowEvent e) {

	}

	/**
	 * Unused.
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
