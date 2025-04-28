package com.common;

import com.common.DataClasses.*;
import com.common.entities.enemies.Skeleton;
import com.common.entities.enemies.Zombie;
import com.common.entities.player.Armor;
import com.common.entities.potions.HP;
import com.common.entities.templates.Sign;
import com.common.graphics.Graphics;
import com.common.listeners.Listeners;
import com.common.utils.FileHandle;
import com.common.utils.MapUtils;
import com.common.utils.SystemUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import static com.common.Debug.Flags.DATA;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.EXCEPTION;
import static com.common.Debug.debug;

/**
 * Class for holding global game data
 */
public class Data {
	public static Graphics graphics;
	public static FileHandle fileHandle;
	public static SystemUtils systemUtils;
	public static Gson gson = new Gson();

	/**
	 * Player related information.
	 */
	public static class Player {
		/**
		 * Player current position.<br>
		 * Formated like {@code y, x}
		 */
		public static Position position = new Position();
		/**
		 * Players viewing radius.<br>
		 * Tells, how many tiles around player are rendered.
		 */
		public static int radius = 5;
		/**
		 * Delay in milliseconds between swapping shortAction.
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
		public static ArrayList<Position> walls = new ArrayList<>();
		/**
		 * Point array for storing location ground tiles.<br>
		 * Formated like {@code {Point(x, y), Point(x, y), ...}}
		 */
		public static ArrayList<Position> ground = new ArrayList<>();
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
		public static Position holdPosition = new Position();
	}

	/**
	 * Scale, which images need to be resized
	 */
	public static final int IMAGE_SCALE = 3;
	/**
	 * Size of images in pixels
	 */
	public static final int IMAGE_SIZE = 16;
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
	 * Loading interactive things from variable and creating new instances
	 */
	public static void loadInteractive() {
		debug(DATA, CORE, ">>> [Data.loadInteractive]");

		Map.enemyCount = 0;

		ArrayList<Interactive> interactive = Map.interactive;
		for (Interactive inter : interactive) {
			// Getting position of interactive thing
			Position position = inter.position;

			// Checking, which type it is
			switch (inter.entityType) {
				// Enemies
				case "zombie":
					Zombie zombie = new Zombie(position);
					Listeners.addRefreshListener(zombie);
					Map.enemyCount++;
					break;
				case "skeleton":
					Skeleton skeleton = new Skeleton(position);
					Listeners.addRefreshListener(skeleton);
					Map.enemyCount++;
					break;

				// For player
				case "armor":
					Armor armor = new Armor(position);
					Listeners.addRefreshListener(armor);
					break;
				case "hp":
					HP hp = new HP(position);
					Listeners.addRefreshListener(hp);
					break;

				// Other interactive
				case "sign":
					String signText = inter.text;
					Sign sign = new Sign(position, signText);
					Listeners.addRefreshListener(sign);
					break;
				default:
					System.out.println("Entity:" + inter.entityType + " was not yet implemented in Data.loadInteractive");
			}
		}

		debug(DATA, CORE, "<<< [Data.loadInteractive]");
	}

	/**
	 * Loading settings from a save file
	 */
	public static void loadSettings() {
		debug(DATA, CORE, ">>> [Data.loadSettings]");

		try {
			// Loading data for settings
			String settingsRaw = fileHandle.loadText("settings.json", false);
			SettingsData settings = gson.fromJson(settingsRaw, SettingsData.class);

			// Loading player related information
			PlayerSettingsData player = settings.player;
			Player.controlDelay = player.controlDelay;
		} catch (IOException e) {
			debug(DATA, EXCEPTION, "<<< [Data.loadSettings] IOException: " + e.getMessage());
		}

		debug(DATA, CORE, "<<< [Data.loadSettings]");
	}

	/**
	 * Loading map from a save file
	 */
	public static void loadMap() {
		debug(DATA, CORE, ">>> [Data.loadMap]");

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
			map = MapUtils.constructMap();

			// Loading player related information
			Player.position = levelData.player.position;
		} catch (IOException e) {
			debug(DATA, EXCEPTION, "<<< [Data.loadMap] IOException: " + e.getMessage());
		}

		debug(DATA, CORE, "<<< [Data.loadMap]");
	}

	/**
	 * Saving settings to a file
	 */
	public static void saveSettings() {
		debug(DATA, CORE, ">>> [Data.saveSettings]");

		// Storing player related information
		SettingsData data = new SettingsData();
		PlayerSettingsData player = new PlayerSettingsData();
		player.controlDelay = Player.controlDelay;
		data.player = player;

		// Saving data
		fileHandle.saveText("/settings.json", gson.toJson(data));

		debug(DATA, CORE, "<<< [Data.saveSettings]");
	}

	/**
	 * Saving map to a file
	 */
	public static void saveMap() {
		debug(DATA, CORE, ">>> [Data.saveMap]");

		// Trying to deconstruct a map to more manageable storing information
		MapUtils.deconstructMap();

		// Storing map related information
		LevelData levelData = new LevelData();
		MapData mapData = new MapData();
		mapData.walls = Map.walls;
		mapData.ground = Map.ground;
		mapData.interactive = Map.interactive;
		levelData.map = mapData;

		// Storing player related information
		PlayerMapData player = new PlayerMapData();
		player.position = new Position(LevelEditor.holdPosition);
		levelData.player = player;

		// Saving data
		String mapName = "maps/" + Map.currentMap + ".json";
		fileHandle.saveText(mapName, gson.toJson(levelData));

		debug(DATA, CORE, "<<< [Data.saveMap]");
	}
}
