package com.OneOfManySimons;

import com.OneOfManySimons.menu.Menu;
import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.map.LevelEditor;
import com.OneOfManySimons.utils.FileHandle;

/**
 * Main class
 */
public class Main {
	public static Player player;
	private static Graphics graphics;
	private static Listeners listeners;
	private static FileHandle fileHandle;

	public static void main(String[] args) {
		if (Debug.Main) System.out.println(">>> [Main.main]");

		// Initializing main components
		graphics = new Graphics();
		listeners = new Listeners();
		fileHandle = new FileHandle();
		Main main = new Main();
		main.init();

		if (Debug.Main) System.out.println("<<< [Main.main]");
	}

	/**
	 * Initializing game
	 */
	public void init() {
		if (Debug.Main) System.out.println(">>> [Main.init]");

		// Initializing
		fileHandle.initFiles();
		Data.loadSettings();
		Data.loadMap();
		graphics.initMap();

		// Checking, if it should be run in level edit mode
		if (Data.LevelEditor.levelEdit) {
			if (Debug.Main) System.out.println("--- [Main.init] Running game in level editor mode");

			// Saving meanwhile position for player
			int y = Data.Player.position[0];
			int x = Data.Player.position[1];
			Data.LevelEditor.holdPosition = new int[]{y, x};

			// Initializing level editor
			LevelEditor editor = new LevelEditor();
			graphics.resizeScreen();
			listeners.addRefreshListener(editor);
			graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);
			graphics.refreshScreen();

			if (Debug.Main) System.out.println("--- [Main.init] Returning from level editor mode");
		}

		// Loading interactive thing to a map
		Data.loadInteractive();
		createPlayer();

		if (!Data.LevelEditor.levelEdit) {
			Menu menu = new Menu();
			menu.init();
			Thread menuThread = new Thread(menu);
			menuThread.start();
		}

		if (Debug.Main) System.out.println("<<< [Main.init]");
	}

	public static void createPlayer() {
		if (Debug.Main) System.out.println(">>> [Main.createPlayer]");

		player = new Player();
		listeners.addRefreshListener(player);

		// Starting thread for changing player actions
		if (!Data.LevelEditor.levelEdit) {
			Thread _player = new Thread(player);
			_player.start();
		}

		graphics.refreshScreen();
		if (Debug.Main) System.out.println("<<< [Main.createPlayer]");
	}
}