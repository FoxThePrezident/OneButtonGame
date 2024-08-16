package com.FoxThePrezident.utils;

import com.FoxThePrezident.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

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
	 * @return JSONArray of constructed map
	 */
	public JSONArray constructMap() {
		if (Data.debug) System.out.println(">>> [MapUtils.constructMap]");

		JSONArray map = new JSONArray();

		// Putting walls to map
		JSONArray walls = Data.Map.walls;
		if (Data.debug) System.out.println("--- [MapUtils.constructMap] Putting walls to map");
		if (walls != null) {
			for (int i = 0; i < walls.length(); i++) {
				JSONArray wall = walls.getJSONArray(i);
				int y = wall.getInt(0);
				int x = wall.getInt(1);
				JSONArray row = getRow(map, y);
				row.put(x, "W");
			}
		}

		// Putting ground to map
		if (Data.debug) System.out.println("--- [MapUtils.constructMap] Putting ground to map");
		JSONArray grounds = Data.Map.ground;
		if (grounds != null) {
			for (int i = 0; i < grounds.length(); i++) {
				JSONArray ground = grounds.getJSONArray(i);
				int y = ground.getInt(0);
				int x = ground.getInt(1);
				JSONArray row = getRow(map, y);
				row.put(x, " ");
			}
		}

		if (Data.debug) System.out.println("<<< [MapUtils.constructMap]");

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
	public void deconstructMap() throws IOException {
		if (Data.debug) System.out.println(">>> [MapUtils.deconstructMap]");

		// Create a JSONObject to store the map data
		JSONArray walls = new JSONArray();
		JSONArray ground = new JSONArray();
		for (int y = 0; y < Data.map.length(); y++) {
			JSONArray row = Data.map.getJSONArray(y);
			for (int x = 0; x < Data.map.getJSONArray(y).length(); x++) {
				String element = row.isNull(x) ? "" : row.getString(x);
				// Process the element
				JSONArray tile = new JSONArray();
				tile.put(y);
				tile.put(x);
				// Checking, which element is on a map
				switch (element) {
					case "W" -> walls.put(tile);
					case " " -> ground.put(tile);
				}
			}
		}

		// Storing data
		Data.Map.walls = walls;
		Data.Map.ground = ground;

		if (Data.debug) System.out.println("<<< [MapUtils.deconstructMap]");
	}

	/**
	 * Creating new rows for a map if needed.
	 *
	 * @param map      from which we want a row
	 * @param rowIndex which we want
	 * @return JSONArray of the row we want
	 */
	private JSONArray getRow(JSONArray map, int rowIndex) {
		if (Data.debug) System.out.println("--- [MapUtils.getRow]");

		while (map.length() <= rowIndex) {
			map.put(new JSONArray()); // Add a new row if it doesn't exist
		}
		return map.getJSONArray(rowIndex);
	}

	/**
	 * Shifting map to be able to place things on "negative" indexes of a map.
	 *
	 * @param toShift formated like {@code [y, x]}, where y, x >= 0.
	 */
	public void shiftMap(int[] toShift) {
		if (Data.debug) System.out.println(">>> [MapUtils.shiftMap]");

		int maxRowNum = 0;
		// Looping over rows in a y direction
		if (Data.debug) System.out.println("--- [MapUtils.shiftMap] Shifting map");
		for (int y = Data.map.length() - 1; y >= toShift[0]; y--) {
			JSONArray row;
			// Checking, if we have something to move
			if (y < 0) {
				// If now, then we create an empty row of a void
				row = new JSONArray();
				for (int x = 0; x <= maxRowNum; x++) {
					row.put(x, "");
				}
			} else {
				// Shifting x direction in a row
				row = Data.map.getJSONArray(y);
				int rowLength = row.length() - 1;
				// Checking if we need to shift columns
				if (toShift[1] < 0) {
					// Looping over columns
					for (int x = rowLength; x >= toShift[1]; x--) {
						// Checking, if there is something to shift
						if (x < 0 || row.isNull(x)) {
							row.put(x - toShift[1], "");
						} else {
							row.put(x - toShift[1], row.getString(x));
						}
					}
					rowLength = row.length() - 1;
					if (rowLength > maxRowNum) maxRowNum = rowLength;
				}
			}
			// Storing shifted row back to map
			Data.map.put(y - toShift[0], row);
		}
		// Cursor
		Data.Player.position[0] -= toShift[0];
		Data.Player.position[1] -= toShift[1];

		// Player
		Data.LevelEditor.holdPosition[0] -= toShift[0];
		Data.LevelEditor.holdPosition[1] -= toShift[1];

		// Shifting interactive things. Enemies, potions, ...
		if (Data.debug) System.out.println("--- [MapUtils.shiftMap] Shifting interactive things on the map");
		for (int i = 0; i < Data.Map.interactive.length(); i++) {
			// Getting interactive position
			JSONObject interactive = Data.Map.interactive.getJSONObject(i);
			JSONArray position = interactive.getJSONArray("position");
			// Shifting it
			position.put(0, position.getInt(0) - toShift[0]);
			position.put(1, position.getInt(1) - toShift[1]);
			// Storing it back
			interactive.put("position", position);
			Data.Map.interactive.put(i, interactive);
		}

		if (Data.debug) System.out.println("<<< [MapUtils.shiftMap]");
	}
}
