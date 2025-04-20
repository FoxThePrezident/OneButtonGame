package com.common;

/**
 * For debugging application and getting quickly what is when called.<br>
 * Prints open and close statements in a function, like:
 * <p>
 * `>>> [Launcher.init]`
 * <p>
 * `<<< [Launcher.init]`
 * <p>
 * Useful, if you need to know what and when is called, without using ides debugger.
 */
public class Debug {
	/**
	 * Flags for functions. Their order is same as in file system for easier navigation.
	 */
	public static class Flags {
		public static class Entities {
			public static class Algorithms {
				public static final int PATH_FINDING = 0;
			}

			public static class Enemies {
				public static final int SKELETON = 0;
				public static final int ZOMBIE = 0;
			}

			public static class Player {
				public static final int ARMOR = 0;
				public static final int ITEM = 0;
				public static final int PLAYER = 0;
				public static final int PLAYER_ACTIONS = 0;
			}

			public static class Potions {
				public static final int HP = 0;
			}

			public static class Templates {
				public static final int ENEMY = 0;
				public static final int POTION = 0;
				public static final int PROJECTILE = 0;
				public static final int SIGN = 0;
			}
		}

		public static class Graphics {
			public static final int GRAPHICS = 0;
		}

		public static class Listeners {
			public static final int LISTENERS = 0;
			public static final int TEXT_INPUT_LISTENER = 0;
		}

		public static class Map {
			public static final int COLLISIONS = 0;
			public static final int LEVEL_EDITOR = 0;
		}

		public static class Menu {
			public static final int MENU = 0;
			public static final int MENU_COMMANDS = 0;
		}

		public static class Utils {
			public static final int FILE_UTILS = 0;
			public static final int MAP_UTILS = 0;
			public static final int SYSTEM_UTILS = 0;
		}

		public static final int DATA = 0;
		public static final int MAIN = 0;
		public static final int TEXT_INPUT = 0;
	}

	/**
	 * Debug levels
	 * lower level means more critical the message is
	 */
	public static class Levels {
		/**
		 * Used for critical bugs that occurs in code
		 */
		public static final int EXCEPTION = 0;

		/**
		 * Critical things like initialization, refresh screen
		 */
		public static final int CORE = 1;

		/**
		 * Not relevant, but useful information like setters and getters
		 */
		public static final int INFORMATION = 2;
	}

	/**
	 * Debug function for handling debug messages.
	 * <p>
	 * Message format: `prefix [class.function name] additional information`<br>
	 * Example: `>>> [Launcher.init] Hello world!`<br>
	 * Prefixes for messages:
	 * *  `>>>` for entering a function, placed at the start
	 * *  `<<<` for exiting a function, placed at the end
	 * *  `---` information inside a function, or where first two indicators are not needed, like getters and setters or short functions
	 *
	 * @param flag    is from `Debug.Flags` object
	 * @param level   that message is, defined in `Debug.Levels` object
	 * @param message that will be printed in case that debug level us sufficient
	 */
	public static void debug(int flag, int level, String message) {
		if (flag >= level) {
			System.out.println(message);
		}
	}
}
