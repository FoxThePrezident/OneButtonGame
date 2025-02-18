package com.OneOfManySimons;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Window for text inputs. For things like signs.
 */
public class TextInput {
	private static JFrame frame;
	private static JTextField textField;

	public static void open(ActionListener listener) {
		if (Debug.TextInput) System.out.println(">>> [TextInput.open]");

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

		if (Debug.TextInput) System.out.println("<<< [TextInput.open]");
	}

	public static void dispose() {
		if (Debug.TextInput) System.out.println("--- [TextInput.dispose]");
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
