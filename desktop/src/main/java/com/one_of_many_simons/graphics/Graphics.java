package com.one_of_many_simons.graphics;

import com.common.Data;
import com.common.DataClasses.Colour;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.DataClasses.TextData;
import com.common.graphics.TextInput;
import com.one_of_many_simons.listeners.PlayerMoveListener;
import com.one_of_many_simons.listeners.TextInputListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

import static com.common.Debug.Flags.Graphics.GRAPHICS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;


/**
 * Handling all graphics related stuff, like initializing, painting tiles and text.
 */
public class Graphics extends com.common.graphics.Graphics {
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
	 * Clearing the whole screen.
	 */
	public void clearScreen() {
		debug(GRAPHICS, CORE, "--- [Graphics.clearScreen]");

		for (JPanel panel : panels) {
			panel.removeAll();
			panel.revalidate();
			panel.repaint();
		}
		layeredPane.revalidate();
		layeredPane.repaint();
	}

	/**
	 * Method for initialization screen.
	 */
	@Override
	public void initMap() {
		debug(GRAPHICS, CORE, ">>> [Graphics.initMap]");

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

		debug(GRAPHICS, CORE, "<<< [Graphics.initMap]");
	}

	/**
	 * Resizing screen and centering it.
	 */
	@Override
	public void resizeScreen() {
		debug(GRAPHICS, CORE, "--- [Graphics.resizeScreen]");

		// Dimensions of the window
		int gridSize = Data.Player.radius * 2 + 1;
		int windowWidth = gridSize * imageSize;
		int windowHeight = gridSize * imageSize;
		int halfTile = imageSize / Data.IMAGE_SCALE;
		frame.setSize(windowWidth + halfTile - 1, windowHeight + halfTile * 2 + 5);
		frame.setLocationRelativeTo(null);
		layeredPane.setPreferredSize(new Dimension(windowWidth, windowHeight));

		// Setting maximum size for panels
		for (JPanel panel : panels) {
			panel.setBounds(0, 0, windowWidth, windowHeight);
		}
	}

	/**
	 * Clearing everything inside given layer
	 *
	 * @param layer that we want to remove
	 */
	@Override
	public void clearLayer(int layer) {
		debug(GRAPHICS, CORE, "--- [Graphics.removeLayer]");

		// Removing content from panels
		panels.get(layer).removeAll();
		revalidate();
	}

	/**
	 * Revalidating panels on screen
	 */
	@Override
	public void revalidate() {
		debug(GRAPHICS, CORE, "--- [Graphics.revalidate]");

		for (int i = 0; i < layersCount; i++) {
			panels.get(i).revalidate();
			panels.get(i).repaint();
		}

		// Redrawing layeredPane
		layeredPane.revalidate();
		layeredPane.repaint();
	}

	/**
	 * Drawing tile on the screen.
	 *
	 * @param position of tile, we want to place
	 * @param tile     that will be drawn
	 * @param layer    which layer we want to draw on
	 */
	@Override
	public void drawTile(Position position, ImageWrapper tile, int layer) {
		debug(GRAPHICS, CORE, ">>> [Graphics.drawTile]");

		// Player position
		int playerY = Data.Player.position.y;
		int playerX = Data.Player.position.x;

		// Starting position
		int startY = playerY - Data.Player.radius;
		int startX = playerX - Data.Player.radius;

		// Adjusting coordinate based on player position
		int pixelY = (position.y - startY) * imageSize;
		int pixelX = (position.x - startX) * imageSize;

		// Drawing tile
		JLabel label = new JLabel((ImageIcon) tile.getIcon());
		label.setBounds(pixelX, pixelY, imageSize, imageSize);
		panels.get(layer).add(label);

		debug(GRAPHICS, CORE, "<<< [Graphics.drawTile]");
	}

	/**
	 * Drawing text field on screen
	 *
	 * @param textField that will be drawn
	 */
	@Override
	public void drawText(TextData textField) {
		debug(GRAPHICS, CORE, "--- [Graphics.drawText]");

		JLabel label = new JLabel();

		label.setText("<html><p style=text-align: 'center';>" + textField.text + "</p></html>");

		label.setFont(new Font("Serif", Font.PLAIN, textField.size));
		label.setForeground(convertColor(textField.foregroundColor));

		// Set background color
		if (textField.backgroundColor != null) {
			label.setOpaque(true);
			label.setBackground(convertColor(textField.backgroundColor));
		} else {
			label.setOpaque(false);
		}

		// Set border
		if (textField.borderWidth != 0) {
			label.setBorder(new LineBorder(convertColor(textField.borderColor), textField.borderWidth));
		}

		// Get preferred size to accommodate text and border
		Dimension textSize = label.getPreferredSize();

		// Calculate label width considering the border thickness
		int labelWidth = textSize.width + 8;
		int labelHeight = textSize.height;
		// For case of long text
		int maxTextLength = (int) (Math.pow(2, Data.Player.radius) * 2 - 1);
		if (textField.text.length() > maxTextLength) {
			labelHeight *= 2;
		}

		// Centering or positioning text
		if (textField.centered) {
			label.setBounds(0, textField.position.y, frame.getWidth(), labelHeight);
		} else {
			label.setBounds(textField.position.x, textField.position.y, labelWidth, labelHeight);
		}

		label.setHorizontalAlignment(SwingConstants.CENTER);

		panels.get(TEXT_LAYER).add(label);
		panels.get(TEXT_LAYER).repaint();
	}

	private Color convertColor(Colour color) {
		return new Color(color.r, color.g, color.b);
	}

	/**
	 * Showing text input for things like signs.
	 */
	@Override
	public void showTextInput() {
		debug(GRAPHICS, INFORMATION, "--- [Graphics.showTextInput]");

		TextInput.open(new TextInputListener());
	}
}
