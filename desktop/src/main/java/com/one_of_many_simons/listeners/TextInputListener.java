package com.one_of_many_simons.listeners;

import com.common.Data;
import com.common.DataClasses.Interactive;
import com.common.DataClasses.Position;
import com.common.entities.templates.Sign;
import com.common.graphics.TextInput;
import com.common.listeners.Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Listeners.TEXT_INPUT_LISTENER;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

/**
 * Listener for submitting text to a sign.
 */
public class TextInputListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		debug(TEXT_INPUT_LISTENER, CORE, ">>> [TextInputListener.actionPerformed]");

		String text = TextInput.getText();

		// Getting coordinates
		Position position = new Position(Data.Player.position);

		// Creating JSON object for a sign
		Interactive sign = new Interactive();
		sign.entityType = "sign";
		sign.position = position;
		sign.text = text;
		Data.Map.interactive.add(sign);

		// Hiding text input
		TextInput.dispose();

		Listeners.addRefreshListener(new Sign(position, text));

		// Updating screen
		graphics.refreshScreen();

		debug(TEXT_INPUT_LISTENER, CORE, "<<< [TextInputListener.actionPerformed]");
	}
}
