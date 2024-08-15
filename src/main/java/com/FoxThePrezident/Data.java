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
	 * Player related information
	 */
	public static class Player {
		/**
		 * Player current position
		 * Formated like y, x
		 */
		public static int[] position;
		/**
		 * Players viewing radius
		 */
		public static int radius;
		/**
		 * Delay in milliseconds between swapping action
		 */
		public static int controlDelay;
	}

	/**
	 * Map and interactive things related stuff
	 */
	public static class Map {
		/**
		 * JSON array for storing location of walls
		 */
		public static JSONArray walls;
		/**
		 * JSON array for storing location ground tiles
		 */
		public static JSONArray ground;
		/**
		 * JSON array for storing interactive things like potions, enemies
		 */
		public static JSONArray interactive;
	}

	/**
	 * Things related to a level editor
	 */
	public static class LevelEditor {
		/**
		 * If we want to boot it in level edit mode
		 */
		public static boolean levelEdit = false;
		/**
		 * Hold position of player character
		 * Formated like y, x
		 */
		public static int[] holdPosition;
	}

	/**
	 * Main map
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
	 * Loading data from a save file
	 */
	public static void loadSettings() {
		try {
			// Initializing libraries
			FileHandle fileHandle = new FileHandle();
			MapUtils mapUtils = new MapUtils();

			// Loading data
			String settingsRaw = fileHandle.loadText("data.json", false);
			if (settingsRaw == null) throw new RuntimeException("Cannot find data.json");
			Json settings = new Json(new JSONObject(settingsRaw));

			// Loading player related information
			Json player = new Json(settings.getJsonObject("Player"));
			Player.position = player.getInt2D("position", new int[]{10, 10});
			Player.radius = player.getInt("radius", 20);
			Player.controlDelay = player.getInt("controlDelay", 500);

			// Loading map related information
			Json _map = new Json(settings.getJsonObject("Map"));
			Map.walls = _map.getJsonArray("walls");
			Map.ground = _map.getJsonArray("ground");
			Map.interactive = _map.getJsonArray("interactive");
			map = mapUtils.constructMap();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveSettings() {
		// Initializing libraries
		JSONObject data = new JSONObject();
		MapUtils mapUtils = new MapUtils();
		FileHandle fileHandle = new FileHandle();

		// Trying to deconstruct a map to more manageable storing information
		try {
			mapUtils.deconstructMap();
		} catch (IOException ignored) {
		}

		// Storing player related information
		JSONObject player = new JSONObject();
		player.put("position", LevelEditor.holdPosition);
		player.put("radius", Player.radius);
		player.put("controlDelay", Player.controlDelay);
		data.put("Player", player);

		// Storing map related information
		JSONObject map = new JSONObject();
		map.put("walls", Map.walls);
		map.put("ground", Map.ground);
		map.put("interactive", Map.interactive);
		data.put("Map", map);

		// Saving data
		fileHandle.saveText("/data.json", String.valueOf(data));
	}
}
