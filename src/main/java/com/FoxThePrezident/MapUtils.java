package com.FoxThePrezident;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapUtils {
	public JSONArray constructMap(JSONObject mapData) {
		JSONArray dimension = mapData.getJSONArray("dimension");
		int rows = dimension.getInt(0);
		int cols = dimension.getInt(1);

		JSONArray map = new JSONArray();
		for (int i = 0; i < rows; i++) {
			JSONArray row = new JSONArray();
			for (int j = 0; j < cols; j++) {
				row.put(" "); // Fill with empty spaces initially
			}
			map.put(row);
		}

		JSONArray walls = mapData.getJSONArray("walls");
		for (int i = 0; i < walls.length(); i++) {
			JSONArray wall = walls.getJSONArray(i);
			int x = wall.getInt(0);
			int y = wall.getInt(1);
			JSONArray row = map.getJSONArray(y);
			row.put(x, "W"); // Update the position with "W" for walls
		}

		return map;
	}
}
