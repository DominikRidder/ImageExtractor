package gui.volumetab;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageLabel extends JLabel implements MouseMotionListener, MouseListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int posX, posY;
	private int mouseX, mouseY;
	private boolean shiftdown;
	private boolean inbounds;
	private VolumeTab parent;

	public ImageLabel(ImageIcon icon) {
		super(icon);

		addMouseMotionListener(this);

		addMouseListener(this);
		
		addKeyListener(this);
	}

	public void setParent(VolumeTab volume) {
		parent = volume;
	}

	protected void paintComponent(Graphics g) {
		Graphics scratchGraphics = (g == null) ? null : g.create();
		try {
			ui.update(scratchGraphics, this);

			if (shiftdown && inbounds &&  parent.getImage() != null) {
				g.setColor(Color.YELLOW);
				String todraw = "GrayScale: "
						+ parent.getImage().getProcessor().getPixel(posX, posY);
				int stringwidth = g.getFontMetrics().stringWidth(todraw);
				int stringheight = g.getFontMetrics().getHeight();
				int changeY = 20;
				int changeX = 0;

				if (stringwidth + mouseX + changeX > this.getWidth()) {
					changeX = this.getWidth() - stringwidth - mouseX;
				}
				if (stringheight + mouseY + changeY > this.getHeight()) {
					changeY = this.getHeight() - stringheight - mouseY;
				}
				g.drawString(todraw, mouseX + changeX, mouseY + changeY);
			}
		} finally {
			scratchGraphics.dispose();
		}
	}

	public void mouseMoved(MouseEvent e) {
		mouseActions(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		mouseActions(e);
	}
	
	public void mouseActions(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		posX = (int) ((double) e.getX() / parent.getScale());
		posY = (int) ((double) e.getY() / parent.getScale());
		shiftdown = e.isControlDown();
		inbounds = this.contains(e.getPoint());
		repaint();
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		mouseActions(e);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseActions(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseActions(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		shiftdown = e.isControlDown();
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		shiftdown = e.isControlDown();
		repaint();
	}
}
