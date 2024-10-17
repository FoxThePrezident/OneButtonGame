package com.FoxThePrezident;

/**
 * For debugging application
 * Prints open and close statements in a function, like:
 * <pre>{@code
 *   >>> main function
 *   <<< main function
 * }</pre>
 * <p>
 * Useful, if you need to know what and when is called, without using ides debugger.
 * Format: indicator [class.function name] additional information.<br>
 * Example: {@code >>> [Main.start] Hello world!}
 * <p>
 * Indicators:
 * <ul>
 *   <li> {@code >>>} for entering a function, placed at the start</li>
 *   <li> {@code <<<} for exiting a function, placed at the end</li>
 *   <li> {@code ---} information inside a function, or where first two indicators will be useless, like getters and setters</li>
 * </ul>
 */
public class Debug {
	public static class entities {
		public static class enemies {
			public static final boolean Zombie = false;
		}

		public static class player {
			public static final boolean Player = false;
			public static final boolean PlayerAction = false;
		}

		public static class potions {
			public static final boolean HP = false;
		}

		public static class templates {
			public static final boolean Enemy = false;
			public static final boolean Potion = false;
			public static final boolean Sign = false;
		}
	}

	public static class graphics {
		public static final boolean Graphics = false;
	}

	public static class listeners {
		public static final boolean Listeners = false;
		public static final boolean TextInputListener = false;

	}

	public static class map {
		public static final boolean Collisions = false;
		public static final boolean LevelEditor = false;
	}

	public static class Menu {
		public static final boolean Menu = false;
		public static final boolean MenuCommands = false;
	}

	public static class utils {
		public static final boolean FileHandle = false;
		public static final boolean Json = false;
		public static final boolean MapUtils = false;
	}

	public static final boolean Data = false;
	public static final boolean Main = false;
	public static final boolean TextInput = false;
}
