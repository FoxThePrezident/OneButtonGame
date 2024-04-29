package com.FoxThePrezident.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handling JSON related things
 */
public class Json {
	// JSON data
	private final JSONObject data;

	// Storing data from a constructor
	public Json(JSONObject Data) {
		data = Data;
	}

	/**
	 * Getting integer from JSON document
	 *
	 * @param key          which is used, to get integer
	 * @param defaultValue in case that integer is not existing in a provided JSON document
	 * @return Integer, default or founded
	 */
	public int getInt(String key, int defaultValue) {
		try {
			return data.getInt(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	/**
	 * Getting JSONArray from JSON document
	 *
	 * @param key which is used, to get JSONArray
	 * @return JSONArray, default or founded
	 */
	public JSONArray getJsonArray(String key) {
		return getJsonArray(key, new JSONArray());
	}

	/**
	 * Getting JSONArray from JSON document
	 *
	 * @param key          which is used, to get JSONArray
	 * @param defaultValue in case that JSONArray is not existing in a provided JSON document
	 * @return JSONArray, default or founded
	 */
	public JSONArray getJsonArray(String key, JSONArray defaultValue) {
		try {
			return data.getJSONArray(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	/**
	 * Getting JSONObject from JSON document
	 *
	 * @param key which is used, to get JSONObject
	 * @return JSONObject, default or founded
	 */
	public JSONObject getJsonObject(String key) {
		return getJsonObject(key, new JSONObject());
	}

	/**
	 * Getting JSONObject from JSON document
	 *
	 * @param key          which is used, to get JSONObject
	 * @param defaultValue in case that JSONObject is not existing in a provided JSON document
	 * @return JSONObject, default or founded
	 */
	public JSONObject getJsonObject(String key, JSONObject defaultValue) {
		try {
			return data.getJSONObject(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	/**
	 * Getting int[] from JSON document
	 *
	 * @param key          which is used, to get int[]
	 * @param defaultValue in case that int[] is not existing in a provided JSON document
	 * @return int[], default or founded
	 */
	public int[] getInt2D(String key, int[] defaultValue) {
		try {
			JSONArray jsonArray = data.getJSONArray(key);
			int y = jsonArray.getInt(0);
			int x = jsonArray.getInt(1);
			return new int[]{y, x};
		} catch (JSONException e) {
			return defaultValue;
		}
	}
}
