package com.OneOfManySimons.listeners;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.TextInput;
import com.OneOfManySimons.entities.templates.Sign;
import com.OneOfManySimons.graphics.Graphics;
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

		String text = TextInput.getText();

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
		sign.put("text", text);
		Data.Map.interactive.put(sign);

		// Hiding text input
		TextInput.disposeFrame();

		Listeners listeners = new Listeners();
		listeners.addRefreshListener(new Sign(new int[]{y, x}, text));

		// Updating screen
		Graphics graphics = new Graphics();
		graphics.refreshScreen();

		if (Debug.listeners.TextInputListener) System.out.println("<<< [TextInputListener.actionPerformed]");
	}
}
