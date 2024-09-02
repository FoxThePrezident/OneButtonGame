package com.FoxThePrezident;

import com.FoxThePrezident.entities.Sign;
import com.FoxThePrezident.entities.potions.HP;
import com.FoxThePrezident.entities.Player;
import com.FoxThePrezident.entities.enemies.Zombie;
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
		if (Data.debug) System.out.println(">>> [Main.main]");

		// Initializing main components
		graphics = new Graphics();
		fileHandle = new FileHandle();
		Main main = new Main();
		main.init();

		if (Data.debug) System.out.println("<<< [Main.main]");
	}

	/**
	 * Initializing game
	 */
	public void init() {
		if (Data.debug) System.out.println(">>> [Main.init]");

		// Initializing
		Player _player = new Player();
		fileHandle.initFiles();
		Data.loadSettings();
		Data.loadMap();
		graphics.initMap();

		// Checking, if it should be run in level edit mode
		if (Data.LevelEditor.levelEdit) {
			if (Data.debug) System.out.println("--- [Main.init] Running game in level editor mode");

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

			if (Data.debug) System.out.println("--- [Main.init] Returning from level editor mode");
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
					HP hp = new HP(position);
					graphics.addListener(hp);
					break;
				}
				case "sign": {
					String signText = inter.getString("text");
					Sign sign = new Sign(position, signText);
					graphics.addListener(sign);
					break;
				}
			}
		}

		// Refreshing screen and adding player to it
		graphics.refreshScreen();
		// Starting thread for changing player actions
		Thread player = new Thread(_player);
		player.start();

		if (Data.debug) System.out.println("<<< [Main.init]");
	}
}