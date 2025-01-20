package com.OneOfManySimons.utils;

import com.OneOfManySimons.Debug;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handling JSON related things, like getting array, int, ...<br>
 * Double downs as like fail check for cases like missing key or exceptions.
 */
public class Json {
	/**
	 * JSON data, that we are working with.
	 */
	private final JSONObject data;

	/**
	 * Constructor
	 *
	 * @param Data JSON object, that this class will work with
	 */
	public Json(JSONObject Data) {
		if (Debug.utils.Json) System.out.println("--- [Json.constructor]");
		data = Data;
	}

	/**
	 * Constructor
	 *
	 * @param Data String, that this class will work with
	 */
	public Json(String Data) {
		if (Debug.utils.Json) System.out.println("--- [Json.constructor]");
		data = new JSONObject(Data);
	}

	/**
	 * Getting integer from JSON document.
	 *
	 * @param key          which is used, to get integer
	 * @param defaultValue in case that integer is not existing in a provided JSON document
	 * @return Integer, default or founded
	 */
	public int getInt(String key, int defaultValue) {
		if (Debug.utils.Json) System.out.println("--- [Json.getInt]");
		try {
			return data.getInt(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	/**
	 * Getting JSONArray from JSON document.
	 *
	 * @param key which is used, to get JSONArray
	 * @return JSONArray, default or founded
	 */
	public JSONArray getJsonArray(String key) {
		if (Debug.utils.Json) System.out.println("--- [Json.getJsonArray]");
		return getJsonArray(key, new JSONArray());
	}

	/**
	 * Getting JSONArray from JSON document.
	 *
	 * @param key          which is used, to get JSONArray
	 * @param defaultValue in case that JSONArray is not existing in a provided JSON document
	 * @return JSONArray, default or founded
	 */
	public JSONArray getJsonArray(String key, JSONArray defaultValue) {
		if (Debug.utils.Json) System.out.println("--- [Json.getJsonArray]");
		try {
			return data.getJSONArray(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	/**
	 * Getting JSONObject from JSON document.
	 *
	 * @param key which is used, to get JSONObject
	 * @return JSONObject, default or founded
	 */
	public JSONObject getJsonObject(String key) {
		if (Debug.utils.Json) System.out.println("--- [Json.getJsonObject]");
		return getJsonObject(key, new JSONObject());
	}

	/**
	 * Getting JSONObject from JSON document.
	 *
	 * @param key          which is used, to get JSONObject
	 * @param defaultValue in case that JSONObject is not existing in a provided JSON document
	 * @return JSONObject, default or founded
	 */
	public JSONObject getJsonObject(String key, JSONObject defaultValue) {
		if (Debug.utils.Json) System.out.println("--- [Json.getJsonObject]");
		try {
			return data.getJSONObject(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	/**
	 * Getting int[] from JSON document.
	 *
	 * @param key          which is used, to get int[]
	 * @param defaultValue in case that int[] is not existing in a provided JSON document
	 * @return int[], default or founded
	 */
	public int[] getInt2D(String key, int[] defaultValue) {
		if (Debug.utils.Json) System.out.println("--- [Json.getInt2D]");
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
