package com.OneOfManySimons.graphics;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.TextInput;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.listeners.PlayerMoveListener;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
public class Graphics {
	// Constants
	public final int GROUND_LAYER = 0;
	public final int ENTITIES_LAYER = 1;
	public final int DECOR_LAYER = 2;
	public final int PLAYER_LAYER = 3;
	public final int TEXT_LAYER = 4;
	public final int ARROW_LAYER = 5;

	public final int layersCount = 6;

	/**
	 * Main window
	 */
	private static JFrame frame;
	/**
	 * Window for graphics
	 */
	private static JLayeredPane layeredPane;
	/**
	 * Panels for displaying things
	 */
	private static ArrayList<JPanel> panels;

	/**
	 * Size of an image after scaling
	 */
	protected final int imageSize = Data.imageSize * Data.imageScale;

	/**
	 * listeners
	 */
	protected static final Listeners listeners = new Listeners();

	/**
	 * Method for initialization screen.
	 */
	public void initMap() {
		if (Debug.graphics.Graphics) System.out.println(">>> [Graphics.initMap]");

		if (frame != null) {
			frame.dispose();
		}
		frame = new JFrame();

		// General window settings
		frame.setTitle("One button game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);

		// Panel used for storing drawing panels
		layeredPane = new JLayeredPane();
		frame.getContentPane().add(layeredPane);

		// Initializing panels for drawing
		panels = new ArrayList<>(layersCount);
		for (int i = 0; i < layersCount; i++) {
			JPanel panel = new JPanel();
			// Setting transparency
			panel.setOpaque(false);
			panel.setLayout(null);

			// Add each panel to the JLayeredPane with its layer index
			layeredPane.add(panel, Integer.valueOf(i));

			panels.add(panel);
		}

		resizeScreen();

		// Listeners
		frame.addKeyListener(new PlayerMoveListener());
		frame.addMouseListener(new PlayerMoveListener());

		if (Debug.graphics.Graphics) System.out.println("<<< [Graphics.initMap]");
	}

	/**
	 * Resizing screen and centering it.
	 */
	public void resizeScreen() {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.resizeScreen]");

		// Dimensions of the window
		int gridSize = Data.Player.radius * 2 + 1;
		int windowWidth = gridSize * imageSize;
		int windowHeight = gridSize * imageSize;
		int halfTile = imageSize / Data.imageScale;
		frame.setSize(windowWidth + halfTile - 1, windowHeight + halfTile * 2 + 5);
		frame.setLocationRelativeTo(null);
		layeredPane.setPreferredSize(new Dimension(windowWidth, windowHeight));

		// Setting maximum size for panels
		for (JPanel panel : panels) {
			panel.setBounds(0, 0, windowWidth, windowHeight);
		}
	}

	/**
	 * Method for refreshing screen.
	 */
	public void refreshScreen() {
		if (Debug.graphics.Graphics) System.out.println(">>> [Graphics.refreshScreen]");

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

		// Notifying entities that screen got refreshed
		listeners.callRefreshListeners();

		if (Debug.graphics.Graphics) System.out.println("<<< [Graphics.refreshScreen]");
	}

	/**
	 * Clearing everything inside given layer
	 *
	 * @param layer that we want to remove
	 */
	public void clearLayer(int layer) {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.removeLayer]");

		// Removing content from panels
		panels.get(layer).removeAll();
		revalidate();
	}

	/**
	 * Revalidating panels on screen
	 */
	public void revalidate() {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.revalidate]");

		for (int i = 0; i < layersCount; i++) {
			panels.get(i).revalidate();
			panels.get(i).repaint();
		}

		// Redrawing layeredPane
		layeredPane.revalidate();
		layeredPane.repaint();
	}

	/**
	 * Getting tile from a map on certain position.
	 *
	 * @param position of tile, we want to get
	 * @return IMageIcon on specified position
	 */
	public ImageIcon getTile(int[] position) {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.getTile]");

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
		if (Debug.graphics.Graphics) System.out.println(">>> [Graphics.drawTile]");

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
		panels.get(layer).add(label);

		if (Debug.graphics.Graphics) System.out.println("<<< [Graphics.drawTile]");
	}

	/**
	 * Drawing text field on screen
	 *
	 * @param textField that will be drawn
	 */
	public void drawText(Text textField) {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.drawText]");

		panels.get(TEXT_LAYER).add(textField.getText(frame.getWidth()));
		panels.get(TEXT_LAYER).repaint();
	}

	/**
	 * Showing text input for things like signs.
	 */
	public void showTextInput() {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.showTextInput]");

		new TextInput();
	}

	/**
	 * Clearing the whole screen.
	 */
	private static void clearScreen() {
		if (Debug.graphics.Graphics) System.out.println("--- [Graphics.clearScreen]");

		for (JPanel panel : panels) {
			panel.removeAll();
			panel.revalidate();
			panel.repaint();
		}
		layeredPane.revalidate();
		layeredPane.repaint();
	}
}
