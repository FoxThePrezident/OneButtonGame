package com.OneOfManySimons.menu;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.Main;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.utils.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Contains all methods that are used in menus
 */
@SuppressWarnings("unused")
public class MenuCommands {
	private static Menu menu;
	private static Listeners listeners;

	public MenuCommands(Menu menu) {
		MenuCommands.menu = menu;
		listeners = new Listeners();
	}

	public void main_menu() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.main_menu]");

		ArrayList<JSONObject> menuItems = new ArrayList<>();
		Menu.generateMenu("MainMenu", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.main_menu]");
	}

	/**
	 * Create new game with player
	 */
	public void newGame() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.newGame]");

		FileHandle fileHandle = new FileHandle();
		String[] maps = fileHandle.getContentOfDirectory("maps");
		ArrayList<JSONObject> menuItems = new ArrayList<>();

		for (String map : maps) {
			JSONObject mapObject = new JSONObject();

			String mapName = map.replace(".json", "");

			mapObject.put("label", mapName);
			mapObject.put("itemType", "command");
			mapObject.put("action", "generateNewGame");
			mapObject.put("parameters", mapName);

			JSONArray visible = new JSONArray();
			visible.put("NewGame");
			mapObject.put("visible", visible);

			menuItems.add(mapObject);
		}

		Menu.generateMenu("NewGame", menuItems);

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.newGame]");
	}

	/**
	 * Generate new game for player
	 * @param mapName that will be loaded
	 */
	public void generateNewGame(String mapName) {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.generateNewGame]");

		// Clearing old things.
		Listeners listeners = new Listeners();
		listeners.clearListeners();

		// Loading new ones.
		Data.Map.current = mapName;
		Data.loadMap();
		Data.loadInteractive();

		menu.running = false;
		Data.running = true;

		Main.createPlayer();

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.generateNewGame]");
	}

	/**
	 * Resuming game
	 */
	public void resumeGame() {
		if (Debug.menu.MenuCommands) System.out.println(">>> [MenuCommands.resumeGame]");

		listeners.removeRefreshListener(menu, true);
//		listeners.removeRefreshListener(Main.player);

		menu.running = false;

		Graphics graphics = new Graphics();
		graphics.refreshScreen();

		Data.running = true;
		Thread player = new Thread(Main.player);
		player.start();

		if (Debug.menu.MenuCommands) System.out.println("<<< [MenuCommands.resumeGame]");
	}

	/**
	 * Method for exiting game
	 */
	public void exitGame() {
		if (Debug.menu.MenuCommands) System.out.println("--- [MenuCommands.exitGame]");
		menu.running = false;
		System.exit(0);
	}
}
