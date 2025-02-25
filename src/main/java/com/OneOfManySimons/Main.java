package com.OneOfManySimons;

import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.menu.Menu;

import static com.OneOfManySimons.Data.libraries.*;

/**
 * Main class
 */
public class Main {
	public static Player player;

	public static void main(String[] args) {
		if (Debug.Main) System.out.println(">>> [Main.main]");

		// Initializing main components
		Main main = new Main();
		main.init();

		if (Debug.Main) System.out.println("<<< [Main.main]");
	}

	/**
	 * Creating new player instance
	 */
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
		Data.init();
		fileHandle.initFiles();
		playerActions.init();
		Data.loadSettings();
		Data.loadMap();
		graphics.initMap();

		// Loading interactive thing to a map
		Data.loadInteractive();
		createPlayer();

		// Creating menu
		Menu menu = new Menu();
		menu.init();
		Thread menuThread = new Thread(menu);
		menuThread.start();

		if (Debug.Main) System.out.println("<<< [Main.init]");
	}
}