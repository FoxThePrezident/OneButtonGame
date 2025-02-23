package com.OneOfManySimons.graphics;

import com.OneOfManySimons.utils.FileHandle;

import javax.swing.*;

/**
 * Icons for graphics.
 */
@SuppressWarnings("unused")
public class Icons {
	private static final FileHandle fileHandle = new FileHandle();

	/**
	 * Player related icons.
	 */
	public static class Player {
		/**
		 * Player icon.
		 */
		public static final ImageIcon player = fileHandle.loadIcon("/image/player/player.png");

		// Arrows
		public static final ImageIcon up = fileHandle.loadIcon("/image/player/up.png");
		public static final ImageIcon down = fileHandle.loadIcon("/image/player/down.png");
		public static final ImageIcon left = fileHandle.loadIcon("/image/player/left.png");
		public static final ImageIcon right = fileHandle.loadIcon("/image/player/right.png");
	}

	/**
	 * Enemies related icons.
	 */
	public static class Enemies {
		public static final ImageIcon zombie = fileHandle.loadIcon("/image/entities/zombie.png");
	}

	/**
	 * Interactive related icons.
	 */
	public static class Interactive {
		public static final ImageIcon hp_potion = fileHandle.loadIcon("/image/interactive/HP_potion.png");
		public static final ImageIcon sign = fileHandle.loadIcon("/image/interactive/sign.png");
	}

	/**
	 * Environment. Like ground, void, walls...
	 */
	public static class Environment {
		public static final ImageIcon blank = fileHandle.loadIcon("/image/environment/void.png");
		public static final ImageIcon wall = fileHandle.loadIcon("/image/environment/wall.png");
		public static final ImageIcon floor = fileHandle.loadIcon("/image/environment/floor.png");
	}

	/**
	 * Icons for level editor.
	 */
	public static class LevelEditor {
		public static final ImageIcon cursor = fileHandle.loadIcon("/image/cursor.png");
	}

	/**
	 * Menu and general icons
	 */

	public static class General {
		public static final ImageIcon inventory = fileHandle.loadIcon("/image/icons/inventory.png");
		public static final ImageIcon menu = fileHandle.loadIcon("/image/icons/menu.png");
		public static final ImageIcon move = fileHandle.loadIcon("/image/icons/move.png");
	}
}
