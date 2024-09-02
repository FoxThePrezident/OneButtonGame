package com.FoxThePrezident;

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
	 * For debugging application
	 * Prints open and close statements in a function, like:
	 * <pre>{@code
	 *   >>> main function
	 *   <<< main function
	 * }</pre>
	 * <p>
	 * Useful, if you need to know what and when is called, without using ides debugger.
	 * Format: indicator [class.function name] additional information.<br>
	 * Example: {@code >>> [Main.start] Hello world!}
	 * <p>
	 * Indicators:
	 * <ul>
	 *   <li> {@code >>>} for entering a function, placed at the start</li>
	 *   <li> {@code <<<} for exiting a function, placed at the end</li>
	 *   <li> {@code ---} information inside a function, or where first two indicators will be useless, like getters and setters</li>
	 * </ul>
	 */
	public static boolean debug = false;

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
		 *   {"position":[y, x],"type":"hp", ...},
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
		public static boolean levelEdit = false;
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
	 * Main loop
	 */
	public static boolean running = true;
	/**
	 * Scale, which images need to be resized
	 */
	public static int imageScale = 3;

	/**
	 * Loading settings from a save file
	 */
	public static void loadSettings() {
		if (debug) System.out.println(">>> [Data.loadSettings]");

		try {
			// Initializing libraries
			FileHandle fileHandle = new FileHandle();

			// Loading data for settings
			String settingsRaw = fileHandle.loadText("settings.json", false);
			if (settingsRaw == null) throw new RuntimeException("Cannot find settings.json");
			Json settings = new Json(new JSONObject(settingsRaw));

			// Loading player related information
			Json player = new Json(settings.getJsonObject("Player"));
			Player.position = player.getInt2D("position", new int[]{10, 10});
			Player.radius = player.getInt("radius", 20);
			Player.controlDelay = player.getInt("controlDelay", 500);
		} catch (IOException e) {
			if (debug) System.out.println("<<< [Data.loadSettings]");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loading map from a save file
	 */
	public static void loadMap() {
		if (debug) System.out.println(">>> [Data.loadMap]");

		try {
			// Initializing libraries
			FileHandle fileHandle = new FileHandle();
			MapUtils mapUtils = new MapUtils();

			// Loading data for map
			String mapRaw = fileHandle.loadText("json/maps/tutorial.json", true);
			if (mapRaw == null) throw new RuntimeException("Cannot find tutorial.json");
			Json mapData = new Json(new JSONObject(mapRaw));

			// Loading map related information
			Json _map = new Json(mapData.getJsonObject("Map"));
			Map.walls = _map.getJsonArray("walls");
			Map.ground = _map.getJsonArray("ground");
			Map.interactive = _map.getJsonArray("interactive");
			map = mapUtils.constructMap();
		} catch (IOException e) {
			if (debug) System.out.println("<<< [Data.loadMap]");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saving settings to a file
	 */
	public static void saveSettings() {
		if (debug) System.out.println(">>> [Data.saveSettings]");

		// Initializing libraries
		JSONObject data = new JSONObject();
		FileHandle fileHandle = new FileHandle();

		// Storing player related information
		JSONObject player = new JSONObject();
		player.put("position", LevelEditor.holdPosition);
		player.put("radius", Player.radius);
		player.put("controlDelay", Player.controlDelay);
		data.put("Player", player);

		// Saving data
		fileHandle.saveText("/settings.json", String.valueOf(data));

		if (debug) System.out.println("<<< [Data.saveSettings]");
	}

	/**
	 * Saving map to a file
	 */
	public static void saveMap() {
		if (debug) System.out.println(">>> [Data.saveMap]");

		// Initializing libraries
		JSONObject data = new JSONObject();
		MapUtils mapUtils = new MapUtils();
		FileHandle fileHandle = new FileHandle();

		// Trying to deconstruct a map to more manageable storing information
		try {
			mapUtils.deconstructMap();
		} catch (IOException ignored) {
		}

		// Storing map related information
		JSONObject map = new JSONObject();
		map.put("walls", Map.walls);
		map.put("ground", Map.ground);
		map.put("interactive", Map.interactive);
		data.put("Map", map);

		// Saving data
		fileHandle.saveText("/map.json", String.valueOf(data));

		if (debug) System.out.println("<<< [Data.saveMap]");
	}
}
