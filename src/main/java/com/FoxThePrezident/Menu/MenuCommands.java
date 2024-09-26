package com.FoxThePrezident.Menu;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.Debug;
import com.FoxThePrezident.Main;
import com.FoxThePrezident.listeners.Listeners;
import com.FoxThePrezident.utils.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Contains all methods that are used in menus
 */
@SuppressWarnings("unused")
public class MenuCommands {
	/**
	 * Create new game with player
	 */
	public void newGame() {
		if (Debug.Menu.MenuCommands) System.out.println(">>> [MenuCommands.newGame]");

		FileHandle fileHandle = new FileHandle();
		String[] maps = fileHandle.getContentOfDirectory("maps");
		ArrayList<JSONObject> menuItems = new ArrayList<>();

		for (String map: maps) {
			JSONObject mapObject = new JSONObject();

			String mapName = map.replace(".json", "");

			mapObject.put("label", mapName);
			mapObject.put("itemType", "command");
			mapObject.put("action", "generateNewGame");

			JSONArray visible = new JSONArray();
			visible.put("NewGame");
			mapObject.put("visible", visible);

			menuItems.add(mapObject);
		}

		Menu.generateMenu("generateNewGame", menuItems);

		if (Debug.Menu.MenuCommands) System.out.println("<<< [MenuCommands.newGame]");
	}

	public void generateNewGame(String mapName) {
		if (Debug.Menu.MenuCommands) System.out.println(">>> [MenuCommands.generateNewGame]");

		// Clearing old things.
		Listeners listeners = new Listeners();
		listeners.clearListeners();

		// Loading new ones.
		Data.Map.current = mapName;
		Data.loadMap();
		Data.loadInteractive();

		Menu.running = false;
		Data.running = true;

		Main.createPlayer();

		if (Debug.Menu.MenuCommands) System.out.println("<<< [MenuCommands.generateNewGame]");
	}

	/**
	 * Method for exiting game
	 */
	public void exitGame() {
		if (Debug.Menu.MenuCommands) System.out.println("--- [MenuCommands.exitGame]");
		System.exit(0);
	}
}
