package com.common.graphics;

import com.common.Data;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.DataClasses.TextData;
import com.common.listeners.Listeners;

import static com.common.Debug.Flags.Graphics.GRAPHICS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;


/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
public abstract class Graphics {
	// Constants
	public static final int GROUND_LAYER = 0;
	public static final int ENTITIES_LAYER = 1;
	public static final int DECOR_LAYER = 2;
	public static final int PLAYER_LAYER = 3;
	public static final int TEXT_LAYER = 4;
	public static final int ACTIONS_LAYER = 5;
	protected static final int layersCount = 6;
	/**
	 * Size of an image after scaling
	 */
	protected static final int imageSize = Data.IMAGE_SIZE * Data.IMAGE_SCALE;

	/**
	 * Clearing the whole screen.
	 */
	public abstract void clearScreen();

	/**
	 * Method for initialization screen.
	 */
	public abstract void initMap();

	/**
	 * Resizing screen and centering it.
	 */
	public abstract void resizeScreen();

	/**
	 * Method for refreshing screen.
	 */
	public void refreshScreen() {
		debug(GRAPHICS, CORE, ">>> [Graphics.refreshScreen]");

		// Clearing previous content of the screen
		clearScreen();

		// Creating variables
		int gridSize = Data.Player.radius * 2 + 1;
		int playerY = Data.Player.position.y;
		int playerX = Data.Player.position.x;
		int startY = playerY - Data.Player.radius;
		int startX = playerX - Data.Player.radius;

		// Looping over each tile around the player
		for (int y = startY; y < startY + gridSize; y++) {
			for (int x = startX; x < startX + gridSize; x++) {
				ImageWrapper tile = getTile(new Position(x, y));
				drawTile(new Position(x, y), tile, GROUND_LAYER);
			}
		}

		// Notifying entities that screen got refreshed
		Listeners.callRefreshListeners();

		debug(GRAPHICS, CORE, "<<< [Graphics.refreshScreen]");
	}

	/**
	 * Clearing everything inside given layer
	 *
	 * @param layer that we want to remove
	 */
	public abstract void clearLayer(int layer);

	/**
	 * Revalidating panels on screen
	 */
	public abstract void revalidate();

	/**
	 * Getting tile from a map on certain position.
	 *
	 * @param position of tile, we want to get
	 * @return IMageIcon on specified position
	 */
	public ImageWrapper getTile(Position position) {
		debug(GRAPHICS, INFORMATION, "--- [Graphics.getTile]");

		try {
			String tileName = Data.map.get(position.y).get(position.x);
			switch (tileName) {
				case "W":
					return Icons.Environment.wall;
				case " ":
					return Icons.Environment.floor;
				default:
					return Icons.Environment.blank;
			}
		} catch (IndexOutOfBoundsException e) {
			debug(GRAPHICS, INFORMATION, "--- [Graphics.getTile] Failed to get tile at position x: " + position.x + ",y: " + position.y);
			return Icons.Environment.blank;
		}
	}

	/**
	 * Drawing tile on the screen.
	 *
	 * @param position of tile, we want to place
	 * @param tile     that will be drawn
	 * @param layer    which layer we want to draw on
	 */
	public abstract void drawTile(Position position, ImageWrapper tile, int layer);

	/**
	 * Drawing text field on screen
	 *
	 * @param textField that will be drawn
	 */
	public abstract void drawText(TextData textField);

	/**
	 * Showing text input for things like signs.
	 */
	public abstract void showTextInput();
}
