package com.FoxThePrezident;

import com.FoxThePrezident.listeners.TextInputListener;

import javax.swing.*;

/**
 * Window for text inputs. For things like signs.
 */
public class TextInput extends JFrame {
	private static JFrame frame;
	private static JTextField textField;

	// default constructor
	public TextInput() {
		if (Debug.TextInput) System.out.println(">>> [TextInput.constructor]");

		// Create a new frame to store text field and button
		frame = new JFrame("Text input");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);

		// Create a new button
		JButton submitBtn = new JButton("submit");
		submitBtn.addActionListener(new TextInputListener());

		// Create an object of JTextField with 16 columns
		textField = new JTextField(16);
		textField.addActionListener(new TextInputListener());

		// Create a panel to add buttons and text field
		JPanel panel = new JPanel();

		// Adding things to a screen
		panel.add(textField);
		panel.add(submitBtn);
		frame.add(panel);
		frame.pack();

		if (Debug.TextInput) System.out.println("<<< [TextInput.constructor]");
	}

	/**
	 * Getter for frame visibility.
	 *
	 * @return frame visibility
	 */
	public static boolean getVisibility() {
		if (Debug.TextInput) System.out.println("--- [TextInput.getVisibility]");
		return frame.isVisible();
	}

	/**
	 * Setter for frame visibility.
	 *
	 * @param visibility that screen will be set.
	 */
	public static void setVisibility(boolean visibility) {
		if (Debug.TextInput) System.out.println("--- [TextInput.setVisibility]");
		frame.setVisible(visibility);
		textField.setText(" ");
	}

	/**
	 * Getter for currently inputted text.
	 *
	 * @return text field text
	 */
	public static String getText() {
		if (Debug.TextInput) System.out.println("--- [TextInput.getText]");
		return textField.getText();
	}
}
