package com.FoxThePrezident.map;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.listeners.RefreshListener;
import com.FoxThePrezident.utils.MapUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

/**
 * Handling level editing.
 */
public class LevelEditor implements RefreshListener {
	private static Graphics graphics;
	private static MapUtils mapUtils;
	private static Collisions collisions;

	/**
	 * Initializing viewport radius and other variables.
	 */
	public LevelEditor() {
		if (Data.debug) System.out.println("--- [LevelEditor.constructor]");
		Data.Player.radius = 5;
		graphics = new Graphics();
		mapUtils = new MapUtils();
		collisions = new Collisions();
	}

	/**
	 * Handling movement of a camera.
	 *
	 * @param keyChar of an input
	 */
	public static void move(char keyChar) {
		if (Data.debug) System.out.println(">>> [LevelEditor.move]");

		// Checking, which key was pressed
		switch (keyChar) {
			// Movement
			case 'w' -> Data.Player.position[0] -= 1;
			case 'd' -> Data.Player.position[1] += 1;
			case 's' -> Data.Player.position[0] += 1;
			case 'a' -> Data.Player.position[1] -= 1;

			// Ground
			case '0' -> changeTile("");
			case '1' -> changeTile("W");
			case '2' -> changeTile(" ");

			// Interactive/player
			case '3' -> movePlayer();
			case '4' -> addEntity("zombie");
			case '5' -> addEntity("hp");
			case '6' -> addSign();

			// Enter
			case '\n' -> save();
		}
		// Refreshing screen after each input
		graphics.refreshScreen();

		if (Data.debug) System.out.println("<<< [LevelEditor.move]");
	}

	/**
	 * Placing player on a cursor position.
	 */
	private static void movePlayer() {
		if (Data.debug) System.out.println(">>> [LevelEditor.movePlayer]");

		// Checking, if we need to shift map
		checkForShift();

		// Getting coordinates
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];

		// CChecking if is a valid place on the ground
		ImageIcon tile = graphics.getTile(new int[]{y, x});
		if (collisions.checkForCollision(tile) == collisions.immovable) return;

		// Checking, if it collides with an interactive thing
		// If yes, then remove it
		for (int i = 0; i < Data.Map.interactive.length(); i++) {
			JSONArray position = Data.Map.interactive.getJSONObject(i).getJSONArray("position");
			if ((position.getInt(0) == y) && (position.getInt(1) == x)) Data.Map.interactive.remove(i);
		}

		// Changing hold position
		Data.LevelEditor.holdPosition = new int[]{y, x};

		if (Data.debug) System.out.println("<<< [LevelEditor.movePlayer]");
	}

	/**
	 * Adding entity or interactive thing to a map.
	 *
	 * @param entity which we want to place at cursor position
	 */
	private static void addEntity(String entity) {
		if (Data.debug) System.out.println(">>> [LevelEditor.addEntity]");

		checkForShift();

		// Getting coordinates
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];
		JSONArray position = new JSONArray();
		position.put(y);
		position.put(x);

		// Checking, if we could place it on the ground
		ImageIcon tile = graphics.getTile(new int[]{y, x});
		// Ground
		if (collisions.checkForCollision(tile) == collisions.immovable) return;
		// Player
		if ((y == Data.LevelEditor.holdPosition[0]) && x == Data.LevelEditor.holdPosition[1]) return;

		// Checking for overlap
		for (int i = 0; i < Data.Map.interactive.length(); i++) {
			JSONArray _position = Data.Map.interactive.getJSONObject(i).getJSONArray("position");
			if ((_position.getInt(0) == y) && (_position.getInt(1) == x)) Data.Map.interactive.remove(i);
		}

		// Checking, which type of entity we want to add
		switch (entity) {
			case "zombie": {
				JSONObject enemy = new JSONObject();
				enemy.put("type", "zombie");
				enemy.put("position", position);
				Data.Map.interactive.put(enemy);
				break;
			}
			case "hp": {
				JSONObject hp = new JSONObject();
				hp.put("type", "hp");
				hp.put("position", position);
				Data.Map.interactive.put(hp);
				break;
			}
		}

		if (Data.debug) System.out.println("<<< [LevelEditor.addEntity]");
	}

	/**
	 * Adding sign to a map
	 */
	private static void addSign() {
		if (Data.debug) System.out.println(">>> [LevelEditor.addSign]");

		checkForShift();

		// Getting coordinates
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];
		JSONArray position = new JSONArray();
		position.put(y);
		position.put(x);

		// Checking, if we could place it on the ground
		ImageIcon tile = graphics.getTile(new int[]{y, x});
		// Ground
		if (collisions.checkForCollision(tile) == collisions.immovable) return;
		// Player
		if ((y == Data.LevelEditor.holdPosition[0]) && x == Data.LevelEditor.holdPosition[1]) return;

		// Checking for overlap
		for (int i = 0; i < Data.Map.interactive.length(); i++) {
			JSONArray _position = Data.Map.interactive.getJSONObject(i).getJSONArray("position");
			if ((_position.getInt(0) == y) && (_position.getInt(1) == x)) Data.Map.interactive.remove(i);
		}

		graphics.showTextInput();

		if (Data.debug) System.out.println("<<< [LevelEditor.addSign]");
	}

	/**
	 * Changing tile in a map.
	 *
	 * @param tile which we want to place/override in a map
	 */
	private static void changeTile(String tile) {
		if (Data.debug) System.out.println(">>> [LevelEditor.changeTile]");

		// Position
		int y = Data.Player.position[0];
		int x = Data.Player.position[1];

		// Deleting entity in that place, as we want to change tile
		for (int i = 0; i < Data.Map.interactive.length(); i++) {
			JSONArray position = Data.Map.interactive.getJSONObject(i).getJSONArray("position");
			if ((position.getInt(0) == y) && (position.getInt(1) == x)) Data.Map.interactive.remove(i);
		}

		// Checking, if we need to shift map and getting row, we want to change
		JSONArray rowArray = checkForShift();

		// Updating position, due to shift
		y = Data.Player.position[0];
		x = Data.Player.position[1];

		// Update the tile value at position x in the row array
		rowArray.put(x, tile);

		// Put the modified row array back into the map
		Data.map.put(y, rowArray);

		graphics.refreshScreen();

		if (Data.debug) System.out.println("<<< [LevelEditor.changeTile]");
	}

	/**
	 * Checking, if we get error if we want to interact with nonexistent space in a map.
	 *
	 * @return JSONArray of row in which the cursor is
	 */
	private static JSONArray checkForShift() {
		if (Data.debug) System.out.println(">>> [LevelEditor.checkForShift]");

		// Checking if the cursor is in the negative of the map
		int[] shift = new int[]{0, 0};
		// Y
		if (Data.Player.position[0] < 0) {
			shift[0] = Data.Player.position[0];
		}
		// X
		if (Data.Player.position[1] < 0) {
			shift[1] = Data.Player.position[1];
		}
		// Shifting, if we need to
		if (shift[0] != 0 || shift[1] != 0) mapUtils.shiftMap(shift);

		// Retrieve the row array at index y
		JSONArray rowArray;
		// Trying to get row, which cursor is in
		try {
			rowArray = Data.map.getJSONArray(Data.Player.position[0]);
		} catch (JSONException e) {
			// In case of failure, just create a new one
			rowArray = new JSONArray();
		}

		if (Data.debug) System.out.println("<<< [LevelEditor.checkForShift]");
		return rowArray;
	}

	/**
	 * Saving everything
	 */
	private static void save() {
		Data.saveSettings();
		Data.saveMap();
	}

	/**
	 * Special method for drawing sign onto a screen
	 *
	 * @param position of the sign, formated like [y, x]
	 * @param text     that will be displayed after cursor hovers on top of it.
	 */
	private void drawSign(int[] position, String text) {
		if (Data.debug) System.out.println("--- [LevelEditor.drawSign]");

		graphics.drawTile(position, Icons.Interactive.sign, graphics.ENTITIES_LAYER);

		if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
			graphics.drawText(new int[]{Data.Player.radius * (Data.imageScale - 1) * 16, Data.Player.radius * Data.imageScale * 16}, text, 16, true);
		}
	}

	@Override
	public void onRefresh() {
		if (Data.debug) System.out.println(">>> [LevelEditor.onRefresh]");
		// Drawing player
		graphics.drawTile(Data.LevelEditor.holdPosition, Icons.Player.player, graphics.ENTITIES_LAYER);

		// Looping over interactive things
		for (int i = 0; i < Data.Map.interactive.length(); i++) {
			// Getting position
			JSONObject interactive = Data.Map.interactive.getJSONObject(i);
			JSONArray position = interactive.getJSONArray("position");
			int y = position.getInt(0);
			int x = position.getInt(1);
			int[] _position = new int[]{y, x};

			String text;
			try {
				text = interactive.getString("text");
			} catch (JSONException e) {
				text = "";
			}

			// Checking, which ImageIcon we want to draw
			switch (interactive.getString("type")) {
				case "zombie" -> graphics.drawTile(_position, Icons.Enemies.zombie, graphics.ENTITIES_LAYER);
				case "hp" -> graphics.drawTile(_position, Icons.Interactive.hp_potion, graphics.ENTITIES_LAYER);
				case "sign" -> drawSign(_position, text);
			}
		}

		// Drawing edit cursor
		graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);
		if (Data.debug) System.out.println("<<< [LevelEditor.onRefresh]");
	}
}
