package com.common.map;


import com.common.DataClasses.ImageWrapper;
import com.common.graphics.Icons;

import static com.common.Debug.Flags.Map.COLLISIONS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

/**
 * Collision related.<br>
 * Checking, if that tile have collisions or not.
 */
public class Collisions {
	// Collision types
	public static final int empty = 0;
	public static final int immovable = 1;

	/**
	 * Checking, if tile has a collision border.
	 *
	 * @param tile that we want to check
	 * @return integer
	 */
	public static int checkForCollision(ImageWrapper tile) {
		debug(COLLISIONS, CORE, "--- [Collisions.checkForCollision]");

		if (tile.equals(Icons.Environment.wall)) {
			return immovable;
		}
		if (tile.equals(Icons.Environment.blank)) {
			return immovable;
		}
		return empty;
	}
}
