package com.FoxThePrezident.map;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.Debug;
import com.FoxThePrezident.TextInput;
import com.FoxThePrezident.listeners.Listeners;
import com.FoxThePrezident.listeners.PlayerMoveListener;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.border.Border;
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
	protected final int imageSize = 16 * Data.imageScale;

	/**
	 * listeners
	 */
	protected static final Listeners listeners = new Listeners();

	/**
	 * Method for initialization screen.
	 */
	public void initMap() {
		if (Debug.map.Graphics) System.out.println(">>> [Graphics.initMap]");

		frame = new JFrame();
		new TextInput();

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

		if (Debug.map.Graphics) System.out.println("<<< [Graphics.initMap]");
	}

	/**
	 * Resizing screen and centering it.
	 */
	public void resizeScreen() {
		if (Debug.map.Graphics) System.out.println("--- [Graphics.resizeScreen]");

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
		if (Debug.map.Graphics) System.out.println(">>> [Graphics.refreshScreen]");

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

		if (Debug.map.Graphics) System.out.println("<<< [Graphics.refreshScreen]");
	}

	/**
	 * Clearing everything inside given layer
	 *
	 * @param layer that we want to remove
	 */
	public void clearLayer(int layer) {
		if (Debug.map.Graphics) System.out.println("--- [Graphics.removeLayer]");

		// Removing content from panels
		panels.get(layer).removeAll();
		panels.get(layer).revalidate();
		panels.get(layer).repaint();

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
		if (Debug.map.Graphics) System.out.println("--- [Graphics.getTile]");

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
		if (Debug.map.Graphics) System.out.println(">>> [Graphics.drawTile]");

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

		if (Debug.map.Graphics) System.out.println("<<< [Graphics.drawTile]");
	}

	/**
	 * Drawing text on the screen.
	 *
	 * @param position of the text, needs to be absolute pixel position
	 * @param text     which will be displayed
	 * @param size     of the text
	 */
	public void drawText(int[] position, String text, int size) {
		drawText(position, text, size, false, Color.BLACK, null);
	}

	/**
	 * Drawing text on the screen.
	 *
	 * @param position of the text, needs to be absolute pixel position
	 * @param text     which will be displayed
	 * @param size     of the text
	 * @param centered stretch textbox to the width of the screen and centers it
	 */
	public void drawText(int[] position, String text, int size, boolean centered) {
		drawText(position, text, size, centered, Color.BLACK, null);
	}

	/**
	 * Drawing text on the screen.
	 *
	 * @param position        of the text, needs to be absolute pixel position
	 * @param text            which will be displayed
	 * @param size            of the text
	 * @param backgroundColor of the background
	 * @param border          of the text
	 */
	public void drawText(int[] position, String text, int size, Color backgroundColor, Border border) {
		drawText(position, text, size, false, backgroundColor, border);
	}

	/**
	 * Drawing text on the screen.
	 *
	 * @param position        of the text, needs to be absolute pixel position
	 * @param text            which will be displayed
	 * @param size            of the text
	 * @param centered        if the text needs to be centered on screen
	 * @param backgroundColor of the background
	 * @param border          of the text
	 */
	public void drawText(int[] position, String text, int size, boolean centered, Color backgroundColor, Border border) {
		if (Debug.map.Graphics) System.out.println(">>> [Graphics.drawText]");

		JLabel label = new JLabel();

		// Setting ability for text to be wrapped and also centered.
		label.setText("<html><p style=text-align: 'center';>" + text + "</p></html>");
		label.setFont(new Font("Serif", Font.PLAIN, size));
		label.setForeground(Color.WHITE);

		// Set background color
		if (backgroundColor != null) {
			label.setOpaque(true);
			label.setBackground(backgroundColor);
		} else {
			label.setOpaque(false);
		}

		// Set border
		if (border != null) {
			label.setBorder(border);
		}

		// Get preferred size to accommodate text and border
		Dimension textSize = label.getPreferredSize();

		// Calculate label width considering the border thickness
		int labelWidth = textSize.width + 8;
		int labelHeight = textSize.height;
		// For case of long text
		int maxTextLength = (int) (Math.pow(2, Data.Player.radius)*2 - 1);
		if (text.length() > maxTextLength) {
			labelHeight*=2;
		}

		// Centering or positioning text
		if (centered) {
			label.setBounds(0, position[0], frame.getWidth(), labelHeight);
		} else {
			label.setBounds(position[1], position[0], labelWidth, labelHeight);
		}

		label.setHorizontalAlignment(SwingConstants.CENTER);
		// Add label to the layered pane and refresh
		panels.get(TEXT_LAYER).add(label);
		panels.get(TEXT_LAYER).repaint();

		if (Debug.map.Graphics) System.out.println("<<< [Graphics.drawText]");
	}

	/**
	 * Showing text input for things like signs.
	 */
	public void showTextInput() {
		if (Debug.map.Graphics) System.out.println("--- [Graphics.showTextInput]");

		if (!TextInput.getVisibility()) {
			TextInput.setVisibility(true);
		}
	}

	/**
	 * Clearing the whole screen.
	 */
	private static void clearScreen() {
		if (Debug.map.Graphics) System.out.println("--- [Graphics.clearScreen]");
		for (JPanel panel: panels){
			panel.removeAll();
			panel.revalidate();
			panel.repaint();
		}
		layeredPane.revalidate();
		layeredPane.repaint();
	}
}
