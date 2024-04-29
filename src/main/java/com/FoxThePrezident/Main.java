package com.FoxThePrezident;

import com.FoxThePrezident.entities.Hp_potion;
import com.FoxThePrezident.entities.Player;
import com.FoxThePrezident.entities.Zombie;
import com.FoxThePrezident.map.Graphics;
import com.FoxThePrezident.map.Icons;
import com.FoxThePrezident.utils.FileHandle;
import com.FoxThePrezident.map.LevelEditor;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Main class
 */
public class Main {
	private static Graphics graphics;
	private static FileHandle fileHandle;

	public static void main(String[] args) {
		graphics = new Graphics();
		fileHandle = new FileHandle();
		Main main = new Main();
		main.init();
	}

	/**
	 * Initializing game
	 */
	public void init() {
		// Initializing
		Player _player = new Player();
		fileHandle.initFiles();
		Data.loadSettings();
		graphics.initMap();

		// Checking, if it should be run in level edit mode
		if (Data.LevelEditor.levelEdit) {
			// Saving meanwhile position for player
			int y = Data.Player.position[0];
			int x = Data.Player.position[1];
			Data.LevelEditor.holdPosition = new int[]{y, x};

			// Initializing level editor
			LevelEditor editor = new LevelEditor();
			graphics.resizeScreen();
			graphics.addListener(editor);
			graphics.refreshScreen();
			graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);
			return;
		}

		graphics.addListener(_player);

		// Loading interactive thing to a map
		JSONArray interactive = Data.Map.interactive;
		for (int i = 0; i < interactive.length(); i++) {
			JSONObject inter = interactive.getJSONObject(i);
			// Getting position of interactive thing
			int y = inter.getJSONArray("position").getInt(0);
			int x = inter.getJSONArray("position").getInt(1);
			int[] position = new int[]{y, x};

			// Checking, which type it is
			switch (inter.getString("type")) {
				case "zombie": {
					Zombie zombie = new Zombie(position);
					graphics.addListener(zombie);
					break;
				}
				case "hp": {
					Hp_potion hp = new Hp_potion(position);
					graphics.addListener(hp);
					break;
				}
			}
		}

		// Refreshing screen and adding player to it
		graphics.refreshScreen();
		Thread player = new Thread(_player);
		player.start();
	}
}