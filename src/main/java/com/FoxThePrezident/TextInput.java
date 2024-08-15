package com.FoxThePrezident;

import com.FoxThePrezident.listeners.TextInputListener;

import javax.swing.*;

public class TextInput extends JFrame {
	private static JFrame frame;
	private static JTextField textField;

	// default constructor
	public TextInput() {
		// create a new frame to store text field and button
		frame = new JFrame("Text input");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);

		// create a new button
		JButton submitBtn = new JButton("submit");
		submitBtn.addActionListener(new TextInputListener());

		// create an object of JTextField with 16 columns
		textField = new JTextField(16);
		textField.addActionListener(new TextInputListener());

		// create a panel to add buttons and textfield
		JPanel p = new JPanel();

		// add buttons and textfield to panel
		p.add(textField);
		p.add(submitBtn);

		// add panel to frame
		frame.add(p);
		frame.pack();
	}

	public static boolean getVisibility() {
		return frame.isVisible();
	}

	public static void setVisibility(boolean visibility) {
		frame.setVisible(visibility);
	}

	public static String getText() {
		return textField.getText();
	}

	public static void clearInput() {
		textField.setText(" ");
	}
}
