package com.FoxThePrezident.listeners;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.TextInput;
import com.FoxThePrezident.map.Graphics;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextInputListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		// Getting coordinates
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];
		JSONArray position = new JSONArray();
		position.put(y);
		position.put(x);

		JSONObject sign = new JSONObject();
		sign.put("type", "sign");
		sign.put("position", position);
		sign.put("text", TextInput.getText());
		Data.Map.interactive.put(sign);

		TextInput.clearInput();
		TextInput.setVisibility(false);

		Graphics graphics = new Graphics();
		graphics.refreshScreen();
	}
}
