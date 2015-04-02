package imagehandling;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Gui extends JFrame implements ActionListener{

	Color[][] pixels;

	private static final long serialVersionUID = 1L;
	
	JPanel panel;

	JLabel label;

	Volume vol;
	
	JTextField path;
	
	public Gui(){
		path = new JTextField();
		path.setText("/path/to/volume");
		JButton apply_path = new JButton();
		apply_path.setText("search");
		apply_path.addActionListener(this);
		
		label = new JLabel();
		label.setBorder(new LineBorder(Color.DARK_GRAY));

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.DARK_GRAY);
		panel.add(label, BorderLayout.CENTER);
		panel.add(path, BorderLayout.NORTH);
		panel.add(apply_path, BorderLayout.SOUTH);
		
		getContentPane().add(panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ImageExtractor");
		setVisible(true);
	}
	
	public static void main(String [] agrs){
		new Gui();
	}

	public void actionPerformed(ActionEvent e) {
	   switch (e.getActionCommand()){
	   case "search": vol = new Volume(path.getText()); break;
		   default:break;
	   }
	}
}
