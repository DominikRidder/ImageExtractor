package gui;

import imagehandling.Image;

import java.awt.Dimension;
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

	JTextField searchin;

	JTextField filter;

	JTextArea outputArea;

	JScrollPane outputScroller;

	JButton startButton;

	JCheckBox searchAll;

	boolean stop = false;

	public ImageSearch() {
		this.setTitle("ImageSearch");

		searchin = new JTextField();
		searchin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
		searchin.setMinimumSize(new Dimension(300, 75));

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

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(addDescription(searchin, "Directory:"));
		panel.add(Box.createVerticalStrut(10));
		panel.add(addDescription(filter, "Filter:"));
		panel.add(Box.createVerticalStrut(10));
		panel.add(addDescription(searchAll, "Full Search"));
		panel.add(Box.createVerticalStrut(10));
		panel.add(startButton);
		panel.add(Box.createVerticalStrut(10));
		panel.add(outputScroller);

		// adding everythis to this (class)
		this.add(panel);
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public JPanel addDescription(JComponent textfield, String desc) {
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
		row.setMinimumSize(new Dimension(300, 75));

		JLabel label = new JLabel(desc);

		row.add(label);
		row.add(textfield);

		return row;
	}

	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Start Search":
			stop = false;
			Thread t = new Thread(this);
			t.start();
			break;
		case "Cancle":
			stop = true;
			startButton.setText("Start Search");
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
		String dir = searchin.getText();
		String filterStr = filter.getText();
		HashSet<String> alreadyOutput = new HashSet<String>();
		boolean fullsearch = searchAll.isSelected();

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

		if (dir.equals("")) {
			outputArea.setText("Directory not set");
			return;
		}

		Stack<File> nextFolders = new Stack<File>();
		String path;
		File file = new File(dir);

		if (!file.exists() || !file.isDirectory()) {
			outputArea.setText("Invalid Directory");
			return;
		}

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
				} else if (!potentialDicom.isDirectory()
						&& !alreadyOutput.contains(potentialDicom
								.getParentFile().getAbsolutePath())
						&& (path.endsWith(".dcm") || path.endsWith(".IMA") || Image
								.isDicom(potentialDicom.toPath()))) {
					foundAImage = true;
					try {
						Image img = new Image(potentialDicom.getAbsolutePath());

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
}
