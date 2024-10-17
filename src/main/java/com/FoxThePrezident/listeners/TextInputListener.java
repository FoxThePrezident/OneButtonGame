package com.FoxThePrezident.listeners;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.Debug;
import com.FoxThePrezident.TextInput;
import com.FoxThePrezident.graphics.Graphics;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for submitting text to a sign.
 */
public class TextInputListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		if (Debug.listeners.TextInputListener) System.out.println(">>> [TextInputListener.actionPerformed]");

		// Getting coordinates
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];
		JSONArray position = new JSONArray();
		position.put(y);
		position.put(x);

		// Creating JSON object for a sign
		JSONObject sign = new JSONObject();
		sign.put("type", "sign");
		sign.put("position", position);
		sign.put("text", TextInput.getText());
		Data.Map.interactive.put(sign);

		// Hiding text input
		TextInput.setVisibility(false);

		// Updating screen
		Graphics graphics = new Graphics();
		graphics.refreshScreen();

		if (Debug.listeners.TextInputListener) System.out.println("<<< [TextInputListener.actionPerformed]");
	}
}
