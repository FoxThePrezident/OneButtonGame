package com.common.utils;

import com.common.Data;
import com.common.DataClasses.Interactive;
import com.common.DataClasses.Position;
import com.common.Launcher;
import com.common.listeners.Listeners;

import java.util.ArrayList;

import static com.common.Debug.Flags.Utils.MAP_UTILS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;

/**
 * Managing map related stuff.<br>
 * Like constructing map, deconstructing and map shifting.
 */
public class MapUtils {
	/**
	 * Constructing map from position of individual things.<br>
	 * From:
	 * <pre>{@code {
	 * "walls":[
	 * 	[0, 0],
	 * 	[0, 1],
	 * 	...
	 * ]}
	 * }</pre>
	 * To:
	 * <pre>{@code [
	 * 	["W", "W", "W"],
	 * 	["W", " ", "W"],
	 * 	["W", "W", "W"]
	 * ]}</pre>
	 *
	 * @return ArrayList of constructed map
	 */
	public static ArrayList<ArrayList<String>> constructMap() {
		debug(MAP_UTILS, CORE, ">>> [MapUtils.constructMap]");

		ArrayList<ArrayList<String>> map = new ArrayList<>();

		// Putting walls to map
		ArrayList<Position> walls = Data.Map.walls;
		debug(MAP_UTILS, INFORMATION, "--- [MapUtils.constructMap] Putting walls to map");
		if (walls != null) {
			for (Position wall : walls) {
				ArrayList<String> row = getRow(map, wall.y);

				// Ensure the row has enough size
				while (row.size() <= wall.x) {
					row.add("");  // Add empty space until the row is large enough
				}

				row.set(wall.x, "W");  // Safely set the value at the desired index
			}
		}

		// Putting ground to map
		debug(MAP_UTILS, INFORMATION, "--- [MapUtils.constructMap] Putting ground to map");
		ArrayList<Position> grounds = Data.Map.ground;
		if (grounds != null) {
			for (Position ground : grounds) {
				ArrayList<String> row = getRow(map, ground.y);

				// Ensure the row has enough size
				while (row.size() <= ground.x) {
					row.add("");  // Add empty space until the row is large enough
				}

				row.set(ground.x, " ");  // Safely set the value at the desired index
			}
		}

		debug(MAP_UTILS, CORE, "<<< [MapUtils.constructMap]");

		return map;
	}

	/**
	 * Destructuring map to better formation to be stored in a file.<br>
	 * From:
	 * <pre>{@code [
	 * 	["W", "W", "W"],
	 * 	["W", " ", "W"],
	 * 	["W", "W", "W"]
	 * ]}</pre>
	 * To:
	 * <pre>{@code {
	 * "walls":[
	 * 	[0, 0],
	 * 	[0, 1],
	 * 	...
	 * ]}
	 * }</pre>
	 */
	public static void deconstructMap() {
		debug(MAP_UTILS, CORE, ">>> [MapUtils.deconstructMap]");

		// Create a JSONObject to store the map data
		ArrayList<Position> walls = new ArrayList<>();
		ArrayList<Position> ground = new ArrayList<>();
		for (int y = 0; y < Data.map.size(); y++) {
			ArrayList<String> row = Data.map.get(y);
			for (int x = 0; x < Data.map.get(y).size(); x++) {
				String element = row.get(x) == null ? "" : row.get(x);
				// Process the element
				Position tile = new Position(x, y);
				// Checking, which element is on a map
				switch (element) {
					case "W":
						walls.add(tile);
						break;
					case " ":
						ground.add(tile);
						break;
				}
			}
		}

		// Storing data
		Data.Map.walls = walls;
		Data.Map.ground = ground;

		debug(MAP_UTILS, CORE, "<<< [MapUtils.deconstructMap]");
	}

	/**
	 * Creating new rows for a map if needed.
	 *
	 * @param map      from which we want a row
	 * @param rowIndex which we want
	 * @return JSONArray of the row we want
	 */
	private static ArrayList<String> getRow(ArrayList<ArrayList<String>> map, int rowIndex) {
		debug(MAP_UTILS, INFORMATION, "--- [MapUtils.getRow]");

		while (map.size() <= rowIndex) {
			map.add(new ArrayList<>()); // Add a new row if it doesn't exist
		}
		return map.get(rowIndex);
	}

	/**
	 * Shifting map to be able to place things on "negative" indexes of a map.
	 *
	 * @param toShift formated like {@code [y, x]}, where y, x >= 0.
	 */
	public static void shiftMap(Position toShift) {
		debug(MAP_UTILS, CORE, ">>> [MapUtils.shiftMap]");

		ArrayList<ArrayList<String>> newMap = new ArrayList<>();

		int maxRowNum = 0;
		// Row shifting
		for (int y = Data.map.size() - 1; y >= toShift.y; y--) {
			ArrayList<String> row;
			if (y < 0) {
				row = new ArrayList<>();
				for (int x = 0; x <= maxRowNum; x++) {
					row.add("");
				}
			} else {
				// Column shifting
				row = new ArrayList<>(Data.map.get(y)); // Create a copy
				if (toShift.x < 0) {
					for (int x = row.size() - 1; x >= toShift.x; x--) {
						if (x < 0 || row.get(x) == null) {
							row.add(x - toShift.x, "");
						} else {
							row.add(x - toShift.x, row.get(x));
						}
					}
					if (row.size() - 1 > maxRowNum) maxRowNum = row.size() - 1;
				}
			}
			newMap.add(0, row); // Add to new map
		}

		// Replace old map with shifted map
		Data.map = newMap;

		// Update player and editor positions
		Data.Player.position.x -= toShift.x;
		Data.Player.position.y -= toShift.y;
		Data.LevelEditor.holdPosition.x -= toShift.x;
		Data.LevelEditor.holdPosition.y -= toShift.y;

		// Shift interactive objects
		debug(MAP_UTILS, INFORMATION, "--- [MapUtils.shiftMap] Shifting interactive things");
		for (Interactive interactive : Data.Map.interactive) {
			interactive.position.x -= toShift.x;
			interactive.position.y -= toShift.y;
		}

		// Refresh entities
		Listeners.clearListeners();
		Listeners.addRefreshListener(Launcher.player);
		Data.loadInteractive();

		debug(MAP_UTILS, CORE, "<<< [MapUtils.shiftMap]");
	}
}
