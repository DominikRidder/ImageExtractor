package gui.sortertab;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class GradientTextField extends JTextField {

	private Color ulColor = null;
	private Color lrColor = null;

	public GradientTextField(String text) {
		super(text);
	}

	public void setGradientColors(Color ul, Color lr) {
		this.ulColor = ul;
		this.lrColor = lr;
	}

	protected void paintComponent(Graphics g) {
		if (ulColor != null && lrColor != null) {
			int x = getWidth();
			int y = getHeight();
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(new GradientPaint(new Point(0, 0), ulColor, new Point(
					x, y), lrColor, false));
			g2.fillRect(0, 0, x, y);
		}
		super.paintComponent(g);
	}
}
