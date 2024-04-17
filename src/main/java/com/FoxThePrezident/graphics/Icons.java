package com.FoxThePrezident.graphics;

import com.FoxThePrezident.handlers.FileHandle;

import javax.swing.*;

public class Icons {
	private static final FileHandle fileHandle = new FileHandle();

	public static class Player {
		public static final ImageIcon player = fileHandle.loadIcon("/image/player/player.png");

		public static final ImageIcon up = fileHandle.loadIcon("/image/player/up.png");
		public static final ImageIcon down = fileHandle.loadIcon("/image/player/down.png");
		public static final ImageIcon left = fileHandle.loadIcon("/image/player/left.png");
		public static final ImageIcon right = fileHandle.loadIcon("/image/player/right.png");
	}

	public static class Enemies {
		public static final ImageIcon zombie = fileHandle.loadIcon("/image/entities/zombie.png");
	}

	public static class Interactive {
		public static final ImageIcon hp_potion = fileHandle.loadIcon("/image/interactive/HP_potion.png");
	}

	public static class Environment {
		public static final ImageIcon blank = fileHandle.loadIcon("/image/environment/void.png");
		public static final ImageIcon wall = fileHandle.loadIcon("/image/environment/wall.png");
		public static final ImageIcon floor = fileHandle.loadIcon("/image/environment/floor.png");
	}
}
