package com.common.graphics;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.common.Debug.Flags.TEXT_INPUT;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;

/**
 * Window for text inputs, allowing text input for certain aspects of the game, like creating signs or generating new level.
 */
public class TextInput {
	private static JFrame frame;
	private static JTextField textField;

	/**
	 * Open text input
	 *
	 * @param listener is what will be called when user entered text
	 */
	public static void open(ActionListener listener) {
		debug(TEXT_INPUT, CORE, ">>> [TextInput.open]");

		// Create a new frame to store text field and button
		frame = new JFrame("Text input");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		// Create a new button
		JButton submitBtn = new JButton("submit");
		submitBtn.addActionListener(listener);

		// Create an object of JTextField with 16 columns
		textField = new JTextField(16);
		textField.addActionListener(listener);

		// Create a panel to add buttons and text field
		JPanel panel = new JPanel();

		// Adding things to a screen
		panel.add(textField);
		panel.add(submitBtn);
		frame.add(panel);
		frame.pack();

		debug(TEXT_INPUT, CORE, "<<< [TextInput.open]");
	}

	/**
	 * Deleting text input window to be able to reuse
	 */
	public static void dispose() {
		debug(TEXT_INPUT, INFORMATION, "--- [TextInput.dispose]");
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
		debug(TEXT_INPUT, INFORMATION, "--- [TextInput.getText]");
		return textField.getText();
	}
}
