package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;


/**
 * 
 */
public class ToolbarButton extends JButton {

	private static final long serialVersionUID = 1L;

	private String altText;

	private Color color = null;

	/**
	 * @param toolbarImage
	 * @param actionListener
	 * @param toolTipText
	 * @param altText
	 */
	public ToolbarButton(ToolbarImage toolbarImage, ActionListener actionListener, String toolTipText, String altText) {

		this.altText = altText;

		setImage(toolbarImage);

		addActionListener(actionListener);
		setToolTipText(toolTipText);

		int sizeInt = 30;

		Dimension size = new Dimension(sizeInt, sizeInt);
		setMaximumSize(size);
		setSize(size);
		setPreferredSize(size);

	}

	@Override
	protected void paintComponent(Graphics g) {
		if (color != null) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(color);
			g.fillRoundRect(0, 0, 30, 30, 20, 20);
		}
		super.paintComponent(g);
	}

	/**
	 * 
	 * @param toolbarImage
	 */
	public void setImage(ToolbarImage toolbarImage) {

		String imagePath = System.getProperty("user.dir");

		String imgLocation = toolbarImage.imagePath();

		ImageIcon icon = new ImageIcon(imagePath + imgLocation, altText);

		if (imagePath != null) {
			setIcon(icon);
		} else {
			setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

	}

	/**
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		LookAndFeel laf = UIManager.getLookAndFeel();
		this.color = color;
		if (laf.getName().equals("Nimbus"))
			repaint();
		else
			setBackground(color);
	}

	/**
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}



}