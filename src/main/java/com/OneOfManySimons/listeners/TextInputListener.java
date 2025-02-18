package com.OneOfManySimons.listeners;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.DataClasses.Interactive;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.TextInput;
import com.OneOfManySimons.entities.templates.Sign;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.OneOfManySimons.Data.libaries.graphics;
import static com.OneOfManySimons.Data.libaries.listeners;

/**
 * Listener for submitting text to a sign.
 */
public class TextInputListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		if (Debug.listeners.TextInputListener) System.out.println(">>> [TextInputListener.actionPerformed]");

		String text = TextInput.getText();

		// Getting coordinates
		Point position = new Point(Data.Player.position);

		// Creating JSON object for a sign
		Interactive sign = new Interactive();
		sign.entityType = "sign";
		sign.position = new Point(position);
		sign.text = text;
		Data.Map.interactive.add(sign);

		// Hiding text input
		TextInput.dispose();

		listeners.addRefreshListener(new Sign(new Point(position), text));

		// Updating screen
		graphics.refreshScreen();

		if (Debug.listeners.TextInputListener) System.out.println("<<< [TextInputListener.actionPerformed]");
	}
}
