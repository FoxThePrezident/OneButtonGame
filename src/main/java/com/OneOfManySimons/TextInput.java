package com.OneOfManySimons;

import com.OneOfManySimons.listeners.TextInputListener;

import javax.swing.*;

/**
 * Window for text inputs. For things like signs.
 */
public class TextInput {
	private static JFrame frame;
	private static JTextField textField;

	// default constructor
	public TextInput() {
		if (Debug.TextInput) System.out.println(">>> [TextInput.constructor]");

		// Create a new frame to store text field and button
		frame = new JFrame("Text input");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

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

	public static void disposeFrame() {
		if (frame != null) {
			frame.dispose();
		}
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
