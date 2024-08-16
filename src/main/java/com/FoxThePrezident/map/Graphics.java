package com.FoxThePrezident.map;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.TextInput;
import com.FoxThePrezident.entities.Player;
import com.FoxThePrezident.listeners.RefreshListener;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
public class Graphics {
	// Constants
	public final int GROUND_LAYER = 4;
	public final int ENTITIES_LAYER = 3;
	public final int DECOR_LAYER = 2;
	public final int TEXT_LAYER = 1;
	public final int ARROW_LAYER = 0;

	private static JFrame frame;
	private static JLayeredPane layeredPane;

	private final int imageSize = 16 * Data.imageScale;
	private static final ArrayList<RefreshListener> listeners = new ArrayList<>();

	/**
	 * Method for initialization screen.
	 */
	public void initMap() {
		if (Data.debug) System.out.println(">>> [Graphics.initMap]");

		frame = new JFrame();
		new TextInput();

		// General window settings
		frame.setTitle("One button game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);

		// Panel used for drawing
		layeredPane = new JLayeredPane();
		frame.getContentPane().add(layeredPane);

		resizeScreen();

		// Keyboard listener
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (Data.LevelEditor.levelEdit) {
					LevelEditor.move(keyEvent.getKeyChar());
				} else {
					Player.move();
				}
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
			}
		});

		// First time drawing on screen
		refreshScreen();

		if (Data.debug) System.out.println("<<< [Graphics.initMap]");
	}

	/**
	 * Resizing screen and centering it.
	 */
	public void resizeScreen() {
		if (Data.debug) System.out.println("--- [Graphics.resizeScreen]");

		// Dimensions of the window
		int gridSize = Data.Player.radius * 2 + 1;
		int windowWidth = gridSize * imageSize;
		int windowHeight = gridSize * imageSize;
		int halfTile = imageSize / Data.imageScale;
		frame.setSize(windowWidth + halfTile - 1, windowHeight + halfTile * 2 + 5);
		frame.setLocationRelativeTo(null);
		layeredPane.setPreferredSize(new Dimension(windowWidth, windowHeight));
	}

	/**
	 * Adding listeners that will be called on screen refresh.
	 *
	 * @param toAdd class that will be notified
	 */
	public void addListener(RefreshListener toAdd) {
		if (Data.debug) System.out.println("--- [Graphics.addListener]");
		listeners.add(toAdd);
	}

	/**
	 * Removing listener and preventing it from screen refresh calling.
	 *
	 * @param toRemove class that will be romed from notification
	 */
	public void removeListener(RefreshListener toRemove) {
		if (Data.debug) System.out.println("--- [Graphics.removeListener]");
		listeners.remove(toRemove);
	}

	/**
	 * Method for refreshing screen.
	 */
	public void refreshScreen() {
		if (Data.debug) System.out.println(">>> [Graphics.refreshScreen]");

		// Clearing previous content of the screen
		clearScreen();

		// Creating variables
		int gridSize = Data.Player.radius * 2 + 1;
		int playerY = Data.Player.position[0];
		int playerX = Data.Player.position[1];
		int startY = playerY - Data.Player.radius;
		int startX = playerX - Data.Player.radius;

		// Looping over each tile around the player
		for (int y = startY; y < startY + gridSize; y++) {
			for (int x = startX; x < startX + gridSize; x++) {
				ImageIcon tile = getTile(new int[]{y, x});
				drawTile(new int[]{y, x}, tile, GROUND_LAYER);
			}
		}
		layeredPane.repaint();
		// Notifying entities that screen got refreshed
		callListeners();

		if (Data.debug) System.out.println("<<< [Graphics.refreshScreen]");
	}

	/**
	 * Removing the whole layer.
	 *
	 * @param layer that we want to remove
	 */
	public void removeLayer(int layer) {
		if (Data.debug) System.out.println("--- [Graphics.removeLayer]");

		layeredPane.remove(layer);
		layeredPane.repaint();
	}

	/**
	 * Getting tile from a map on certain position.
	 *
	 * @param position of tile, we want to get
	 * @return IMageIcon on specified position
	 */
	public ImageIcon getTile(int[] position) {
		if (Data.debug) System.out.println("--- [Graphics.getTile]");

		try {
			String tileName = Data.map.getJSONArray(position[0]).getString(position[1]);
			return switch (tileName) {
				case "W" -> Icons.Environment.wall;
				case " " -> Icons.Environment.floor;
				default -> Icons.Environment.blank;
			};
		} catch (JSONException e) {
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
	public void drawTile(int[] position, ImageIcon tile, int layer) {
		if (Data.debug) System.out.println(">>> [Graphics.drawTile]");

		// Player position
		int playerY = Data.Player.position[0];
		int playerX = Data.Player.position[1];

		// Starting position
		int startY = playerY - Data.Player.radius;
		int startX = playerX - Data.Player.radius;

		// Adjusting coordinate based on player position
		int pixelY = (position[0] - startY) * imageSize;
		int pixelX = (position[1] - startX) * imageSize;

		// Drawing tile
		JLabel label = new JLabel(tile);
		label.setBounds(pixelX, pixelY, imageSize, imageSize);
		layeredPane.add(label, layer);

		if (Data.debug) System.out.println("<<< [Graphics.drawTile]");
	}

	/**
	 * Drawing text on the screen.
	 *
	 * @param position of the text, needs to be absolute pixel position
	 * @param text which will be displayed
	 */
	public void drawText(int[] position, String text, int size) {
		if (Data.debug) System.out.println(">>> [Graphics.drawText]");

		JLabel label = new JLabel();

		int offset = text.length();

		label.setText(text);
		label.setBounds(position[1] - offset, position[0], 255, size);
		label.setForeground(Color.WHITE);
		label.setBackground(new Color(0, 0, 0, 0));
		label.setFont(new Font("Serif", Font.PLAIN, size));

		layeredPane.add(label, TEXT_LAYER);
		layeredPane.repaint();

		if (Data.debug) System.out.println("<<< [Graphics.drawText]");
	}

	/**
	 * Showing text input for things like signs.
	 */
	public void showTextInput() {
		if (Data.debug) System.out.println("--- [Graphics.showTextInput]");

		if (!TextInput.getVisibility()) {
			TextInput.setVisibility(true);
		}
	}

	/**
	 * Clearing the whole screen.
	 */
	private static void clearScreen() {
		if (Data.debug) System.out.println("--- [Graphics.clearScreen]");
		layeredPane.removeAll();
	}

	/**
	 * Calling listener that screen got refreshed.
	 */
	private void callListeners() {
		if (Data.debug) System.out.println("--- [Graphics.callListeners]");

		// Looping over each listener
		for (int i = 0; i < listeners.toArray().length; i++) {
			listeners.get(i).onRefresh();
		}
	}
}
