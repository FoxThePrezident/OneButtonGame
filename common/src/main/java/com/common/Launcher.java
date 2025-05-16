package com.common;

import com.common.entities.player.Player;
import com.common.entities.player.PlayerActions;
import com.common.listeners.Listeners;
import com.common.map.LevelEditor;
import com.common.menu.Menu;

import static com.common.Data.fileHandle;
import static com.common.Data.graphics;
import static com.common.Debug.Flags.MAIN;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

public class Launcher {
	public static Player player;
	public static LevelEditor levelEditor;

	/**
	 * Creating new player instance
	 */
	public static void createPlayer() {
		debug(MAIN, CORE, ">>> [Main.createPlayer]");

		player = new Player();
		Listeners.addRefreshListener(player);

		// Starting thread for changing player actions
		if (!Data.LevelEditor.levelEdit) {
			Thread _player = new Thread(player);
			_player.start();
		}

		graphics.refreshScreen();
		debug(MAIN, CORE, "<<< [Main.createPlayer]");
	}

	/**
	 * Initializing game
	 */
	public void init() {
		debug(MAIN, CORE, ">>> [Main.init]");

		// Initializing
		fileHandle.initFiles();
		PlayerActions.init();
		Data.loadSettings();
		Data.loadMap();
		graphics.initMap();

		// Loading interactive thing to a map
		Data.loadInteractive();
		createPlayer();

		// Creating menu
		Menu.init();
		Thread menuThread = new Thread(new Menu());
		menuThread.start();

		debug(MAIN, CORE, "<<< [Main.init]");
	}
}
