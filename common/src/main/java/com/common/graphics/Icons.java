package com.common.graphics;

import com.common.DataClasses.ImageWrapper;

import static com.common.Data.fileHandle;

/**
 * Icons for graphics.
 */
@SuppressWarnings("unused")
public class Icons {
	/**
	 * Player related icons.
	 */
	public static class Player {
		/**
		 * Player icon.
		 */
		public static final ImageWrapper player = fileHandle.loadIcon("/image/player/player.png");

		// Arrows
		public static final ImageWrapper up = fileHandle.loadIcon("/image/player/up.png");
		public static final ImageWrapper down = fileHandle.loadIcon("/image/player/down.png");
		public static final ImageWrapper left = fileHandle.loadIcon("/image/player/left.png");
		public static final ImageWrapper right = fileHandle.loadIcon("/image/player/right.png");
	}

	/**
	 * Enemies related icons.
	 */
	public static class Enemies {
		public static final ImageWrapper zombie = fileHandle.loadIcon("/image/entities/zombie.png");
		public static final ImageWrapper skeleton = fileHandle.loadIcon("/image/entities/skeleton.png");
	}

	/**
	 * Interactive related icons.
	 */
	public static class Interactive {
		public static final ImageWrapper hp_potion = fileHandle.loadIcon("/image/interactive/HP_potion.png");
		public static final ImageWrapper sign = fileHandle.loadIcon("/image/interactive/sign.png");
		public static final ImageWrapper armor = fileHandle.loadIcon("/image/interactive/armor.png");
		public static final ImageWrapper projectile = fileHandle.loadIcon("/image/interactive/projectile.png");
	}

	/**
	 * Environment. Like ground, void, walls...
	 */
	public static class Environment {
		public static final ImageWrapper blank = fileHandle.loadIcon("/image/environment/void.png");
		public static final ImageWrapper wall = fileHandle.loadIcon("/image/environment/wall.png");
		public static final ImageWrapper floor = fileHandle.loadIcon("/image/environment/floor.png");
	}

	/**
	 * Icons for level editor.
	 */
	public static class LevelEditor {
		public static final ImageWrapper cursor = fileHandle.loadIcon("/image/cursor.png");
	}

	/**
	 * Menu and general icons
	 */

	public static class General {
		public static final ImageWrapper inventory = fileHandle.loadIcon("/image/icons/inventory.png");
		public static final ImageWrapper menu = fileHandle.loadIcon("/image/icons/menu.png");
		public static final ImageWrapper move = fileHandle.loadIcon("/image/icons/move.png");
		public static final ImageWrapper attention = fileHandle.loadIcon("/image/attention.png");
	}
}
