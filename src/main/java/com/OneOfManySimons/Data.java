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

import static com.OneOfManySimons.Data.libraries.*;

/**
 * Class for holding global game data
 */
public class Data {
	/**
	 * Global variables to reduce memory usage by creating multiple instances of these classes
	 */
	public static class libraries {
		public static Gson gson;
		public static Menu menu;
		public static Graphics graphics;
		public static MapUtils mapUtils;
		public static Listeners listeners;
		public static FileHandle fileHandle;
		public static Collisions collisions;
		public static PlayerActions playerActions;

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
		public static String currentMap = "first_level";
		/**
		 * Point array for storing location of walls.<br>
		 * Formated like {@code {Point(x, y), Point(x, y), ...}}
		 */
		public static ArrayList<Point> walls = new ArrayList<>();
		/**
		 * Point array for storing location ground tiles.<br>
		 * Formated like {@code {Point(x, y), Point(x, y), ...}}
		 */
		public static ArrayList<Point> ground = new ArrayList<>();
		/**
		 * Array list of Interactive for storing interactive things like potions, enemies and signs.
		 */
		public static ArrayList<Interactive> interactive = new ArrayList<>();
		/**
		 * Enemy count in level<br>
		 * Used for determining winning condition.
		 */
		public static int enemyCount = 0;
	}

	/**
	 * Things related to a level editor
	 */
	public static class LevelEditor {
		/**
		 * Unlocks ability to place things onto map.
		 */
		public static boolean levelEdit = false;
		/**
		 * Hold position of player character.<br>
		 * Formated like {@code Point(x, y)}
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

	/**
	 * Initializing global libraries
	 */
	public static void init() {
		if (Debug.Data) System.out.println(">>> [Data.init]");

		gson = new Gson();
		menu = new Menu();
		graphics = new Graphics();
		mapUtils = new MapUtils();
		listeners = new Listeners();
		fileHandle = new FileHandle();
		collisions = new Collisions();
		playerActions = new PlayerActions();

		if (Debug.Data) System.out.println("<<< [Data.init]");
	}

	/**
	 * Loading interactive things from variable and creating new instances
	 */
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
					libraries.listeners.addRefreshListener(zombie);
					Map.enemyCount++;
					break;
				}
				case "hp": {
					HP hp = new HP(position);
					libraries.listeners.addRefreshListener(hp);
					break;
				}
				case "sign": {
					String signText = inter.text;
					Sign sign = new Sign(position, signText);
					libraries.listeners.addRefreshListener(sign);
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
			Player.controlDelay = player.controlDelay;
		} catch (IOException e) {
			if (Debug.Data) System.out.println("<<< [Data.loadSettings] Exception");
			e.printStackTrace();
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
			String mapName = "/maps/" + Map.currentMap + ".json";
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
			e.printStackTrace();
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
		player.controlDelay = Player.controlDelay;
		data.player = player;

		// Saving data
		fileHandle.saveText("/settings.json", gson.toJson(data));

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
		String mapName = "maps/" + Map.currentMap + ".json";
		fileHandle.saveText(mapName, gson.toJson(levelData));

		if (Debug.Data) System.out.println("<<< [Data.saveMap]");
	}
}
