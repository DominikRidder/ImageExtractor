package gui;

import imagehandling.Image;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ImageSearch extends JFrame implements ActionListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6770567548026413228L;

	JFileChooser filechoose = new JFileChooser();

	JTextField searchin[];

	JTextField filter;

	JTextArea outputArea;

	JScrollPane outputScroller;

	JButton startButton;

	JButton browse[];

	JCheckBox searchAll;

	JFileChooser chooser;

	int num_directorys = 0;
	int default_num_directorys = 1;
	int max_directorys = 9;

	JPanel directorysL, directorysM, directorysR;
	
	JPanel panel;
	
	JMenuItem addRow, removeRow;
	
	boolean stop = false;

	public ImageSearch() {
		this.setTitle("ImageSearch");

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		
		addRow = new JMenuItem("add directory");
		addRow.addActionListener(this);
		removeRow = new JMenuItem("remove directory");
		removeRow.addActionListener(this);
		
		menu.add(addRow);
		menu.add(removeRow);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		chooser = new JFileChooser(new File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		searchin = new JTextField[max_directorys];
		browse = new JButton[max_directorys];

		for (int i = 0; i < max_directorys; i++) {
			searchin[i] = new JTextField();
			searchin[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
			searchin[i].setMinimumSize(new Dimension(300, 75));

			browse[i] = new JButton("...");
			browse[i].addActionListener(this);
		}

		// upperleft rectangle
		filter = new JTextField("");
		filter.setEditable(true);
		filter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
		filter.setMinimumSize(new Dimension(300, 75));

		startButton = new JButton("Start Search");
		startButton.addActionListener(this);

		searchAll = new JCheckBox();

		// // creating the output area
		outputArea = new JTextArea();
		outputArea.setEditable(false);
		outputArea.setMargin(new Insets(0, 0, 0, 0));
		outputScroller = new JScrollPane(outputArea);
		outputScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		directorysR = new JPanel();		
		directorysM = new JPanel();
		directorysL = new JPanel();
		
		for (int i = 0; i < default_num_directorys; i++) {
			addDirectoryRow();
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		//panel.add(createRow(new Component[]{new JLabel("Directorys:"), directorys}));
		panel.add(createRow(new Component[]{directorysL, directorysM, directorysR}));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JComponent[] { new JLabel("Filter:"), filter }));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JComponent[] { new JLabel("Full Search:"),
				searchAll }));
		panel.add(Box.createVerticalStrut(10));
		panel.add(startButton);
		panel.add(Box.createVerticalStrut(10));
		panel.add(outputScroller);
		
		// adding everythis to this (class)
		this.add(panel);
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public JPanel createRow(Component components[]) {
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
		row.setMinimumSize(new Dimension(300, 75));

		for (Component component : components) {
			row.add(component);
		}

		return row;
	}

	public void addDirectoryRow() {
		if (num_directorys >= max_directorys) {
			return;
		}
		
		num_directorys++;
		
		directorysL.setLayout(new GridLayout(num_directorys, 1));
		directorysM.setLayout(new GridLayout(num_directorys, 1));
		directorysR.setLayout(new GridLayout(num_directorys, 1));
		
		directorysL.setMaximumSize(new Dimension(75, 75*num_directorys));
		directorysM.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75*num_directorys));
		directorysR.setMaximumSize(new Dimension(75, 75*num_directorys));
		
		if (num_directorys == 1) {
			directorysL.add(new JLabel("Directory:"));
		} else {
			Component b = Box.createHorizontalGlue();
			b.setSize(0, 0);
			directorysL.add(b);
		}
			
		directorysM.add(searchin[num_directorys-1], num_directorys-1);
		directorysR.add(browse[num_directorys-1], num_directorys-1);
		
		updateMenuOptions();
		
		validate();
	}
	
	public void removeDirectoryRow() {
		if (num_directorys <= 1) {
			return;
		}
		
		num_directorys--;
		
		searchin[num_directorys].setText("");
		
		directorysL.remove(num_directorys);
		directorysM.remove(num_directorys);
		directorysR.remove(num_directorys);
		
		directorysL.setLayout(new GridLayout(num_directorys, 1));
		directorysM.setLayout(new GridLayout(num_directorys, 1));
		directorysR.setLayout(new GridLayout(num_directorys, 1));
		
		directorysL.setMaximumSize(new Dimension(75, 75*num_directorys));
		directorysM.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75*num_directorys));
		directorysR.setMaximumSize(new Dimension(75, 75*num_directorys));
		
		updateMenuOptions();
		
		validate();
	}
	
	void updateMenuOptions() {
		if (num_directorys == max_directorys) {
			addRow.setEnabled(false);
		} else {
			addRow.setEnabled(true);
		}
		
		if (num_directorys == 1) {
			removeRow.setEnabled(false);
		} else {
			removeRow.setEnabled(true);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "...":
			for (int i = 0; i < num_directorys; i++) {
				if (e.getSource().equals(browse[i])) {
					if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
							&& chooser.getSelectedFile() != null) {
						searchin[i].setText(chooser.getSelectedFile()
								.getAbsolutePath());
					}
					break;
				}
			}
			break;
		case "Start Search":
			stop = false;
			Thread t = new Thread(this);
			t.start();
			break;
		case "Cancle":
			stop = true;
			startButton.setText("Start Search");
			break;
		case "add directory":
			addDirectoryRow();
			break;
		case "remove directory":
			removeDirectoryRow();
			break;
		}
	}

	public void run() {
		startButton.setText("Cancle");
		search();
		startButton.setText("Start Search");
	}

	public void search() {
		outputArea.setText("");
		String filterStr = filter.getText();
		String dirs[] = new String[num_directorys];
		HashSet<String> alreadyOutput = new HashSet<String>();
		boolean fullsearch = searchAll.isSelected();
		boolean foundADir = false;

		for (String test : filterStr.split("&")) {
			String parts[] = test.split("=");

			if (parts.length != 2) {
				outputArea
						.setText("Invalid filter component. Please use: <Keyword> = <Value>");
				outputArea
						.setText(outputArea.getText()
								+ "\nor multiple: <Keyword> = <Value> & <Keyword> = <Value> & ...");
				outputArea
						.setText(outputArea.getText()
								+ "\nor Check for diffent Values: <Keyword> = <Value> | <Value> & <Keyword> = ...");
				return;
			}

			String key = parts[0].trim();

			if (key.equals("")) {
				outputArea.setText("No key is given before '='");
				return;
			}

			for (String posval : parts[1].split("\\|")) {
				String val = posval.trim().toLowerCase();

				if (val.equals("")) {
					outputArea.setText("Value error behind '='");
					return;
				}
			}
		}

		for (int i = 0; i < dirs.length; i++) {
			String dir = searchin[i].getText();
			File dirFile = new File(dir);
			if (!dir.equals("") && (!dirFile.exists() || !dirFile.isDirectory())) {
				dirs[i] = "";
				outputArea.setText("Invalid Directory: "+dir+"\n"+outputArea.getText());
				return;
			} else {
				dirs[i] = dir;
			}
		}
		
		for (int i = 0; i < dirs.length; i++) {
			String dir = dirs[i];
			Stack<File> nextFolders = new Stack<File>();
			String path;
			File file = new File(dir);

			if (dir.equals("")) {
				continue;
			}
			
			foundADir = true;

			nextFolders.push(file);
			File[] list;
			boolean warnedKey = false;

			while (!nextFolders.isEmpty()) {
				boolean foundAImage = false;
				file = nextFolders.pop();
				list = file.listFiles();
				if (list == null) {
					// Maybe we dont have a Directory than
					continue;
				}
				if (stop) {
					return;
				}
				outer: for (File potentialDicom : list) {

					path = potentialDicom.getAbsolutePath();

					if (!fullsearch && potentialDicom.getName().startsWith(".")) {
						continue;
					}

					if (!fullsearch && foundAImage) {
						break;
					} else if (potentialDicom.isDirectory()) {
						if (!Files.isSymbolicLink(potentialDicom.toPath())) {
							nextFolders.push(potentialDicom);
						}
					} else if (!alreadyOutput.contains(potentialDicom
							.getParentFile().getAbsolutePath())
							&& (path.endsWith(".dcm") || path.endsWith(".IMA") || Image
									.isDicom(potentialDicom.toPath()))) {
						foundAImage = true;
						try {
							Image img = new Image(
									potentialDicom.getAbsolutePath());

							for (String test : filterStr.split("&")) {
								boolean foundOneVal = false;
								String parts[] = test.split("=");
								String key = parts[0].trim();
								for (String posVal : parts[1].split("\\|")) {
									String val = posVal.trim().toLowerCase();

									String extracted = img.getAttribute(key)
											.toLowerCase().trim();

									if (!warnedKey && extracted.equals("")) {
										outputArea
												.setText("WARNING: Key "
														+ key
														+ " is maybe not correct spelled!\n"
														+ outputArea.getText());
										warnedKey = true;
									}

									if (extracted.contains(val)) {
										foundOneVal = true;
										break;
									}
								}

								if (!foundOneVal) {
									continue outer;
								}
							}
							outputArea.setText(outputArea.getText()
									+ "\n"
									+ potentialDicom.getParentFile()
											.getAbsolutePath());
							repaint();
							alreadyOutput.add(potentialDicom.getParentFile()
									.getAbsolutePath());
						} catch (Exception err) {
							continue;
						}
					}
				}
			}
		}
		
		if (!foundADir) {
			outputArea.setText("Directory not set");
		}
	}
}
