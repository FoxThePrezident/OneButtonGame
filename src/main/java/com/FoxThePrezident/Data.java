package com.FoxThePrezident;

import com.FoxThePrezident.entities.Sign;
import com.FoxThePrezident.entities.enemies.Zombie;
import com.FoxThePrezident.entities.potions.HP;
import com.FoxThePrezident.listeners.Listeners;
import com.FoxThePrezident.utils.MapUtils;
import com.FoxThePrezident.utils.FileHandle;
import com.FoxThePrezident.utils.Json;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class for holding global game data
 */
public class Data {
	/**
	 * Player related information.
	 */
	public static class Player {
		/**
		 * Player current position.<br>
		 * Formated like {@code y, x}
		 */
		public static int[] position;
		/**
		 * Players viewing radius.<br>
		 * Tells, how many tiles around player are rendered.
		 */
		public static int radius;
		/**
		 * Delay in milliseconds between swapping action.
		 */
		public static int controlDelay;
	}

	/**
	 * Map and interactive things related stuff.
	 */
	public static class Map {
		/**
		 * Defining current map that is loaded.
		 */
		public static String current = "mainMenu";
		/**
		 * JSON array for storing location of walls.<br>
		 * Formated like {@code {[y, x], [y, x], ...}}
		 */
		public static JSONArray walls;
		/**
		 * JSON array for storing location ground tiles.<br>
		 * Formated like {@code {[y, x], [y, x], ...}}
		 */
		public static JSONArray ground;
		/**
		 * JSON array for storing interactive things like potions, enemies and signs.<br>
		 * Formatted like
		 * <pre>{@code
		 * {
		 *   {"position":[y, x],"entityType":"hp", ...},
		 * 	...
		 * }
		 * }</pre>
		 */
		public static JSONArray interactive;
	}

	/**
	 * Things related to a level editor
	 */
	public static class LevelEditor {
		/**
		 * If we want to boot it in level edit mode.<br>
		 * Unlocks ability to place things onto map.
		 */
		public static final boolean levelEdit = false;
		/**
		 * Hold position of player character.<br>
		 * Formated like {@code [y, x]}
		 */
		public static int[] holdPosition;
	}

	/**
	 * JSON array storing current map.<br>
	 * Storing each tile and its type.<br>
	 * Formatted like 2D array, where each cell in nested one represents letter of that tile.<br>
	 * Example:
	 * <pre>{@code [
	 * 	["W", "W", "W"],
	 * 	["W", " ", "W"],
	 * 	["W", "W", "W"]
	 * ]}</pre>
	 */
	public static JSONArray map;
	/**
	 * Main loop for player
	 */
	public static boolean running = false;
	/**
	 * Scale, which images need to be resized
	 */
	public static final int imageScale = 3;

	public static void loadInteractive() {
		if (Debug.Data) System.out.println(">>> [Data.loadInteractive]");

		Listeners listeners = new Listeners();

		JSONArray interactive = Data.Map.interactive;
		for (int i = 0; i < interactive.length(); i++) {
			JSONObject inter = interactive.getJSONObject(i);
			// Getting position of interactive thing
			int y = inter.getJSONArray("position").getInt(0);
			int x = inter.getJSONArray("position").getInt(1);
			int[] position = new int[]{y, x};

			// Checking, which type it is
			switch (inter.getString("entityType")) {
				case "zombie": {
					Zombie zombie = new Zombie(position);
					listeners.addRefreshListener(zombie);
					break;
				}
				case "hp": {
					HP hp = new HP(position);
					listeners.addRefreshListener(hp);
					break;
				}
				case "sign": {
					String signText = inter.getString("text");
					Sign sign = new Sign(position, signText);
					listeners.addRefreshListener(sign);
					break;
				}
			}
		}

		if (Debug.Data) System.out.println("<<< [Data.loadInteractive]");
	}

	/**
	 * Loading settings from a save file
	 */
	public static void loadSettings() {
		if (Debug.Data) System.out.println(">>> [Data.loadSettings]");

		try {
			// Initializing libraries
			FileHandle fileHandle = new FileHandle();

			// Loading data for settings
			String settingsRaw = fileHandle.loadText("settings.json", false);
			if (settingsRaw == null) throw new RuntimeException("Cannot find settings.json");
			Json settings = new Json(settingsRaw);

			// Loading player related information
			Json player = new Json(settings.getJsonObject("Player"));
			Player.radius = player.getInt("radius", 20);
			Player.controlDelay = player.getInt("controlDelay", 500);
		} catch (IOException e) {
			if (Debug.Data) System.out.println("<<< [Data.loadSettings] Exception");
			throw new RuntimeException(e);
		}
		if (Debug.Data) System.out.println("<<< [Data.loadSettings]");
	}

	/**
	 * Loading map from a save file
	 */
	public static void loadMap() {
		if (Debug.Data) System.out.println(">>> [Data.loadMap]");

		try {
			// Initializing libraries
			FileHandle fileHandle = new FileHandle();
			MapUtils mapUtils = new MapUtils();

			// Loading data for map
			String mapName = "/maps/" + Map.current + ".json";
			String mapRaw = fileHandle.loadText(mapName, false);
			if (mapRaw == null) throw new RuntimeException("Cannot find " + mapName);
			Json mapData = new Json(new JSONObject(mapRaw));

			// Loading map related information
			Json _map = new Json(mapData.getJsonObject("Map"));
			Map.walls = _map.getJsonArray("walls");
			Map.ground = _map.getJsonArray("ground");
			Map.interactive = _map.getJsonArray("interactive");
			map = mapUtils.constructMap();

			// Loading player related information
			Json player = new Json(mapData.getJsonObject("Player"));
			Player.position = player.getInt2D("position", new int[]{0, 0});
		} catch (IOException e) {
			if (Debug.Data) System.out.println("<<< [Data.loadMap] Exception");
			throw new RuntimeException(e);
		}
		if (Debug.Data) System.out.println("<<< [Data.loadMap]");
	}

	/**
	 * Saving settings to a file
	 */
	public static void saveSettings() {
		if (Debug.Data) System.out.println(">>> [Data.saveSettings]");

		// Initializing libraries
		JSONObject data = new JSONObject();
		FileHandle fileHandle = new FileHandle();

		// Storing player related information
		JSONObject player = new JSONObject();
		player.put("radius", Player.radius);
		player.put("controlDelay", Player.controlDelay);
		data.put("Player", player);

		// Saving data
		fileHandle.saveText("/settings.json", String.valueOf(data));

		if (Debug.Data) System.out.println("<<< [Data.saveSettings]");
	}

	/**
	 * Saving map to a file
	 */
	public static void saveMap() {
		if (Debug.Data) System.out.println(">>> [Data.saveMap]");

		// Initializing libraries
		JSONObject data = new JSONObject();
		MapUtils mapUtils = new MapUtils();
		FileHandle fileHandle = new FileHandle();

		// Trying to deconstruct a map to more manageable storing information
		mapUtils.deconstructMap();

		// Storing map related information
		JSONObject map = new JSONObject();
		map.put("walls", Map.walls);
		map.put("ground", Map.ground);
		map.put("interactive", Map.interactive);
		data.put("Map", map);

		// Storing player related information
		JSONObject player = new JSONObject();
		player.put("position", Player.position);

		// Saving data
		fileHandle.saveText("/map.json", String.valueOf(data));

		if (Debug.Data) System.out.println("<<< [Data.saveMap]");
	}
}
