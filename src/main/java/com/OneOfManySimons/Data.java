package com.OneOfManySimons;

import com.OneOfManySimons.DataClasses.*;
import com.OneOfManySimons.entities.enemies.Zombie;
import com.OneOfManySimons.entities.player.PlayerActions;
import com.OneOfManySimons.entities.potions.HP;
import com.OneOfManySimons.entities.templates.Sign;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.map.Collisions;
import com.OneOfManySimons.menu.Menu;
import com.OneOfManySimons.utils.FileHandle;
import com.OneOfManySimons.utils.MapUtils;
import com.google.gson.Gson;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.OneOfManySimons.Data.libaries.*;

/**
 * Class for holding global game data
 */
public class Data {
	public static class libaries {
		public static Gson gson = new Gson();
		public static Menu menu = new Menu();
		public static Graphics graphics = new Graphics();
		public static MapUtils mapUtils = new MapUtils();
		public static Listeners listeners = new Listeners();
		public static FileHandle fileHandle = new FileHandle();
		public static Collisions collisions = new Collisions();
		public static PlayerActions playerActions = new PlayerActions();

	}
	/**
	 * Player related information.
	 */
	public static class Player {
		/**
		 * Player current position.<br>
		 * Formated like {@code y, x}
		 */
		public static Point position = new Point();
		/**
		 * Players viewing radius.<br>
		 * Tells, how many tiles around player are rendered.
		 */
		public static int radius = 5;
		/**
		 * Delay in milliseconds between swapping action.
		 */
		public static int controlDelay = 500;
	}

	/**
	 * Map and interactive things related stuff.
	 */
	public static class Map {
		/**
		 * Defining current map that is loaded.
		 */
		public static String current = "first_level";
		/**
		 * JSON array for storing location of walls.<br>
		 * Formated like {@code {[y, x], [y, x], ...}}
		 */
		public static ArrayList<Point> walls = new ArrayList<>();
		/**
		 * JSON array for storing location ground tiles.<br>
		 * Formated like {@code {[y, x], [y, x], ...}}
		 */
		public static ArrayList<Point> ground = new ArrayList<>();
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
		public static ArrayList<Interactive> interactive = new ArrayList<>();
		/**
		 * Enemy count in level
		 */
		public static int enemyCount = 0;
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
		public static Point holdPosition = new Point();
	}

	/**
	 * Scale, which images need to be resized
	 */
	public static final int imageScale = 3;
	/**
	 * Size of images in pixels
	 */
	public static final int imageSize = 16;
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
	public static ArrayList<ArrayList<String>> map;
	/**
	 * Main loop for player
	 */
	public static boolean running = false;

	public static void loadInteractive() {
		if (Debug.Data) System.out.println(">>> [Data.loadInteractive]");

		Map.enemyCount = 0;

		ArrayList<Interactive> interactive = Map.interactive;
		for (Interactive inter : interactive) {
			// Getting position of interactive thing
			Point position = inter.position;

			// Checking, which type it is
			switch (inter.entityType) {
				case "zombie": {
					Zombie zombie = new Zombie(position);
					libaries.listeners.addRefreshListener(zombie);
					Map.enemyCount++;
					break;
				}
				case "hp": {
					HP hp = new HP(position);
					libaries.listeners.addRefreshListener(hp);
					break;
				}
				case "sign": {
					String signText = inter.text;
					Sign sign = new Sign(position, signText);
					libaries.listeners.addRefreshListener(sign);
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
			// Loading data for settings
			String settingsRaw = fileHandle.loadText("settings.json", false);
			SettingsData settings = gson.fromJson(settingsRaw, SettingsData.class);

			// Loading player related information
			PlayerSettingsData player = settings.player;
			Player.radius = player.radius;
			Player.controlDelay = player.controlDelay;
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
			// Loading data for map
			String mapName = "/maps/" + Map.current + ".json";
			String mapRaw = fileHandle.loadText(mapName, false);
			if (mapRaw == null) throw new RuntimeException("Cannot find " + mapName);
			LevelData levelData = gson.fromJson(mapRaw, LevelData.class);

			// Loading map related information
			MapData _map = levelData.map;
			Map.walls = _map.walls;
			Map.ground = _map.ground;
			Map.interactive = _map.interactive;
			map = mapUtils.constructMap();

			// Loading player related information
			Player.position = levelData.player.position;
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

		// Storing player related information
		SettingsData data = new SettingsData();
		PlayerSettingsData player = new PlayerSettingsData();
		player.radius = Player.radius;
		player.controlDelay = Player.controlDelay;
		data.player = player;

		// Saving data
		fileHandle.saveText("/settings.json", String.valueOf(data));

		if (Debug.Data) System.out.println("<<< [Data.saveSettings]");
	}

	/**
	 * Saving map to a file
	 */
	public static void saveMap() {
		if (Debug.Data) System.out.println(">>> [Data.saveMap]");

		// Trying to deconstruct a map to more manageable storing information
		mapUtils.deconstructMap();

		// Storing map related information
		LevelData levelData = new LevelData();
		MapData mapData = new MapData();
		mapData.walls = Map.walls;
		mapData.ground = Map.ground;
		mapData.interactive = Map.interactive;
		levelData.map = mapData;

		// Storing player related information
		PlayerMapData player = new PlayerMapData();
		player.position = new Point(LevelEditor.holdPosition);
		levelData.player = player;

		// Saving data
		fileHandle.saveText("/map.json", String.valueOf(levelData));

		if (Debug.Data) System.out.println("<<< [Data.saveMap]");
	}
}
