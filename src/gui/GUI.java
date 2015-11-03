package gui;

import gui.sortertab.SorterTab;
import gui.volumetab.VolumeTab;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.ImageExtractorConfig;

/**
 * This GUI is used, to look the Header and Images of Dicoms and to Search and
 * Sort Dicoms.
 * 
 * @author dridder_local
 *
 */
public class GUI extends JFrame implements ActionListener, ChangeListener,
		Runnable, WindowListener {

	/**
	 * Main method, to start the GUI.
	 * 
	 * @param agrs
	 */
	public static void main(String[] agrs) {
		new GUI(true);
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
	 * 
	 */
	public ImageExtractorConfig imec;
	
	/**
	 * One and only Constructor.
	 */
	public GUI(boolean forceProgrammEndIfThereIsNoWindow) {
		imec = new ImageExtractorConfig();
		this.addWindowListener(this);

		filechooser = new ContextMenuFileChooser();
		filechooser.setCurrentDirectory(new java.io.File("$HOME"));
		filechooser.setDialogTitle("Search Directory");
		filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		filechooser.setAcceptAllFileFilterUsed(false);
		
		forceEnd = forceProgrammEndIfThereIsNoWindow;
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem newGuiWindow, newVolumeTab, newSortTab;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build menu in the menu bar.
		newVolumeTab = new JMenuItem("new Volume Tab");
		newVolumeTab.addActionListener(this);
		newSortTab = new JMenuItem("new Sort Tab");
		newSortTab.addActionListener(this);
		newGuiWindow = new JMenuItem("new Window");
		newGuiWindow.addActionListener(this);
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.add(newGuiWindow);
		menu.add(newVolumeTab);
		menu.add(newSortTab);
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
		lifeupdate();
	}

	public boolean isAlive() {
		return windows != 0;
	}

	public void setCurrentTab(int i){
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

	public void setExtendedWindow(boolean bool) {
		if (extendedWindow == bool) {
			return;
		}
		extendedWindow = bool;

		if (extendedWindow) {
			setfinalSize(this, new Dimension(1400, 550));
		} else {
			setfinalSize(this, new Dimension(1100, 550));
		}
	}

	/**
	 * Method, which is always running, to handle the lifeupdate of the tabs.
	 */
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
			if (tabber.getComponentAt(tabber.getSelectedIndex()) instanceof SorterTab){
			((MyTab) tabber.getComponentAt(tabber.getSelectedIndex()))
					.lifeUpdate();
			}
		}
	}

	/**
	 * Method, which should force a choosen Size for a Component.
	 */
	public static void setfinalSize(Component p, Dimension d) {
		p.setMinimumSize(d);
		p.setMaximumSize(d);
		p.setSize(d);
	}

	/**
	 * Method to add a list of Components to a JPanel.
	 */
	public static void addComponents(JPanel here, Component[] toadd) {
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

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == tabber) {
			this.setExtendedWindow(false);
		}
	}
	
	public MyTab getCurrentTab(){
		Component current = tabber.getSelectedComponent();
		if (current!= null){
			return (MyTab) current;
		}else{
			return null;
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		windows++;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		windows--;
		if (!this.isAlive() && forceEnd) {
			System.exit(1);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
