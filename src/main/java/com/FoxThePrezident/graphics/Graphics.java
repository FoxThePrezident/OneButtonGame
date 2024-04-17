package com.FoxThePrezident.graphics;

import com.FoxThePrezident.common.Settings;
import com.FoxThePrezident.entities.Player;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


/**
 * Handling all graphics related stuff
 */
public class Graphics {
	// Constants
	public final int GROUND_LAYER = 4;
	public final int ENTITIES_LAYER = 3;
	public final int DECOR_LAYER = 2;
	public final int TEXT_LAYER = 1;
	public final int ARROW_LAYER = 0;

	private static JLayeredPane layeredPane;
	private final int imageSize = 16 * Settings.imageScale;
	private static final ArrayList<RefreshListener> listeners = new ArrayList<>();

	/**
	 * Method for initialization screen
	 */
	public void initMap() {
		JFrame frame = new JFrame();
		// General window settings
		frame.setTitle("One button game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);

		// Dimensions of the window
		int gridSize = Settings.playerRadius * 2 + 1;
		int windowWidth = gridSize * imageSize;
		int windowHeight = gridSize * imageSize;
		frame.setSize(windowWidth + 16, windowHeight + imageSize / (2 * Settings.imageScale) + frame.getInsets().top);

		// Panel used for drawing
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(windowWidth, windowHeight));
		frame.getContentPane().add(layeredPane);

		// Keyboard listener
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				Player.move();
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {}
		});

		// First time drawing on screen
		refreshScreen();
	}

	/**
	 * Adding listeners that will be called on screen refresh
	 * @param toAdd class that will be notified
	 */
	public void addListener(RefreshListener toAdd) {
		listeners.add(toAdd);
	}

	/**
	 * Method for refreshing screen
	 */
	public void refreshScreen() {
		// Clearing previous content of the screen
		clearScreen();

		// Creating variables
		int gridSize = Settings.playerRadius * 2 + 1;
		int playerY = Player.position[0];
		int playerX = Player.position[1];
		int startY = playerY - Settings.playerRadius;
		int startX = playerX - Settings.playerRadius;

		// Looping over each tile around the player
		for (int y = startY; y < startY + gridSize; y++) {
			for (int x = startX; x < startX + gridSize; x++) {
				ImageIcon tile = getTile(y, x);
				drawTile(y, x, tile, GROUND_LAYER);
			}
		}
		layeredPane.repaint();
		// Notifying entities that screen got refreshed
		callListeners();
	}

	/**
	 * Removing the whole layer
	 * @param layer that we want to remove
	 */
	public void removeLayer(int layer) {
		layeredPane.remove(layer);
		layeredPane.repaint();

	}

	/**
	 * Getting tile from a map on certain position
	 * @param y coordinate
	 * @param x coordinate
	 * @return IMageIcon on specified position
	 */
	public ImageIcon getTile(int y, int x) {
		try {
			String tileName = Settings.map.getJSONArray(y).getString(x);
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
	 * Drawing tile on the screen
	 * @param y coordinate
	 * @param x coordinate
	 * @param tile that will be drawn
	 * @param layer which layer we want to draw on
	 */
	public void drawTile(int y, int x, ImageIcon tile, int layer) {
		// Player position
		int playerY = Player.position[0];
		int playerX = Player.position[1];

		// Starting position
		int startY = playerY - Settings.playerRadius;
		int startX = playerX - Settings.playerRadius;

		// Adjusting coordinate based on player position
		int pixelX = (x - startX) * imageSize;
		int pixelY = (y - startY) * imageSize;

		// Drawing tile
		JLabel label = new JLabel(tile);
		label.setBounds(pixelX, pixelY, imageSize, imageSize);
		layeredPane.add(label, layer);
	}

	public void drawText(int y, int x, String text) {
		JLabel label = new JLabel();
		label.setText(text);
		label.setBounds(x, y, 128, 32);
		label.setForeground(Color.WHITE);
		label.setBackground(new Color(0, 0, 0, 0));
		label.setFont(new Font("Serif", Font.PLAIN, 32));
		layeredPane.add(label, TEXT_LAYER);
		layeredPane.repaint();
	}

	/**
	 * Clearing the whole screen
	 */
	private static void clearScreen() {
		layeredPane.removeAll();
	}

	/**
	 * Calling listener that screen got refreshed
	 */
	private void callListeners() {
		// Looping over each listener
		for (RefreshListener refreshListener : listeners) {
			refreshListener.onRefresh();
		}
	}
}
