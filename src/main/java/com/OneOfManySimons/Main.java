package com.OneOfManySimons;

import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.menu.Menu;
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
}