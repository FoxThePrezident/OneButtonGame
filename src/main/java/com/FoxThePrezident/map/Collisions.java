package com.FoxThePrezident.map;


import javax.swing.*;

/**
 * Collision related
 */
public class Collisions {
	// Collision types
	public final int empty = 0;
	public final int immovable = 1;

	/**
	 * Checking, if tile has a collision border
	 *
	 * @param tile that we want to check
	 * @return integer
	 */
	public int checkForCollision(ImageIcon tile) {
		if (tile.equals(Icons.Environment.wall)) {
			return immovable;
		}
		if (tile.equals(Icons.Environment.blank)) {
			return immovable;
		}
		return empty;
	}
}
