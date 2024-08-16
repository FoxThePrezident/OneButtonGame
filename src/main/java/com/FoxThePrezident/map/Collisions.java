package com.FoxThePrezident.map;


import com.FoxThePrezident.Data;

import javax.swing.*;

/**
 * Collision related.<br>
 * Checking, if that tile have collisions or not.
 */
public class Collisions {
	// Collision types
	public final int empty = 0;
	public final int immovable = 1;

	/**
	 * Checking, if tile has a collision border.
	 *
	 * @param tile that we want to check
	 * @return integer
	 */
	public int checkForCollision(ImageIcon tile) {
		if (Data.debug) System.out.println("--- [Collisions.checkForCollision]");

		if (tile.equals(Icons.Environment.wall)) {
			return immovable;
		}
		if (tile.equals(Icons.Environment.blank)) {
			return immovable;
		}
		return empty;
	}
}
