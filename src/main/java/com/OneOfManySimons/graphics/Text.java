package com.OneOfManySimons.graphics;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Text {
	private Point position = new Point();
	private String text = "";
	private int size = 16;
	private boolean centered = false;
	private Color backgroundColor = Color.BLACK;
	private Border border = null;

	/**
	 * Set text field position, default is [0, 0]
	 *
	 * @param position int[y, x]
	 */
	public void setPosition(Point position) {
		this.position = new Point(position);
	}

	/**
	 * Set text field text, default is ""
	 *
	 * @param text which will be set to text field
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Set  text field text size, default is 16
	 *
	 * @param size int of text size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * If text field should be centered on screen, default is false
	 *
	 * @param centered boolean
	 */
	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	/**
	 * Background color of text field, default is false
	 *
	 * @param backgroundColor of text field
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Border of text field, default border is null
	 *
	 * @param border of text field
	 */
	public void setBorder(Border border) {
		this.border = border;
	}

	public JLabel getText(int width) {
		if (Debug.graphics.Text) System.out.println(">>> [Text.getText]");

		JLabel label = new JLabel();

		label.setText("<html><p style=text-align: 'center';>" + text + "</p></html>");

		label.setFont(new Font("Serif", Font.PLAIN, size));
		label.setForeground(Color.WHITE);

		// Set background color
		if (backgroundColor != null) {
			label.setOpaque(true);
			label.setBackground(backgroundColor);
		} else {
			label.setOpaque(false);
		}

		// Set border
		if (border != null) {
			label.setBorder(border);
		}

		// Get preferred size to accommodate text and border
		Dimension textSize = label.getPreferredSize();

		// Calculate label width considering the border thickness
		int labelWidth = textSize.width + 8;
		int labelHeight = textSize.height;
		// For case of long text
		int maxTextLength = (int) (Math.pow(2, Data.Player.radius) * 2 - 1);
		if (text.length() > maxTextLength) {
			labelHeight *= 2;
		}

		// Centering or positioning text
		if (centered) {
			label.setBounds(0, position.y, width, labelHeight);
		} else {
			label.setBounds(position.x, position.y, labelWidth, labelHeight);
		}

		label.setHorizontalAlignment(SwingConstants.CENTER);

		if (Debug.graphics.Text) System.out.println("<<< [Text.getText]");
		return label;
	}
}
