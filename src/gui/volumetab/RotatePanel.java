package gui.volumetab;

import gui.GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class RotatePanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RotatePanel(String s) {
        JLabel l = new JLabel(s);
        l.setFont(new JTextField().getFont());
        GUI.setfinalSize(l, new Dimension(10,100));
        this.add(l);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int w2 = getWidth() / 2;
        int h2 = getHeight() / 2;
        g2d.rotate(-Math.PI / 2, w2, h2);
        g2d.translate(0, 35);
        super.paintComponent(g);
    }
    
}