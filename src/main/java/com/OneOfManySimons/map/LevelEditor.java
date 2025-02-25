package com.OneOfManySimons.map;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.DataClasses.Interactive;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.enemies.Zombie;
import com.OneOfManySimons.entities.potions.HP;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.graphics.Text;
import com.OneOfManySimons.listeners.RefreshListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.OneOfManySimons.Data.libraries.*;

/**
 * Handling level editing.
 */
public class LevelEditor implements RefreshListener {
	/**
	 * Top bar for easier keybindings
	 */
	private final LinkedHashMap<String, ImageIcon> toolTable = new LinkedHashMap<>() {{
		put("Void: 0", Icons.Environment.blank);
		put("Wall: 1", Icons.Environment.wall);
		put("Floor: 2", Icons.Environment.floor);
		put("Player: 3", Icons.Player.player);
		put("Zombie: 4", Icons.Enemies.zombie);
		put("HP: 5", Icons.Interactive.hp_potion);
		put("Sign: 6", Icons.Interactive.sign);
	}};

	/**
	 * Initializing viewport radius and other variables.
	 */
	public LevelEditor() {
		if (Debug.map.LevelEditor) System.out.println("--- [LevelEditor.constructor]");
		Data.Player.radius = 10;
	}

	/**
	 * Handling movement of a camera.
	 *
	 * @param keyChar of an input
	 */
	public static void move(char keyChar) {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.move]");

		// Enter key, for not refreshing whole game to be able to see that game was saved
		if (keyChar == '\n') {
			save();
			if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.move]");
			return;
		}

		// Checking, which key was pressed
		switch (Character.toLowerCase(keyChar)) {
			// Movement
			case 'w' -> Data.Player.position.y -= 1;
			case 'd' -> Data.Player.position.x += 1;
			case 's' -> Data.Player.position.y += 1;
			case 'a' -> Data.Player.position.x -= 1;

			// Ground
			case '0' -> changeTile("");
			case '1' -> changeTile("W");
			case '2' -> changeTile(" ");

			// Interactive/player
			case '3' -> movePlayer();
			case '4' -> addEntity("zombie");
			case '5' -> addEntity("hp");
			case '6' -> addSign();

			// Exit
			case 'q' -> showMenu();

			default -> System.out.println(keyChar);
		}
		// Refreshing screen after each input
		graphics.refreshScreen();

		if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.move]");
	}

	private static void showMenu() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- LevelEditor.menu");

		Data.running = false;
		menu.setMenu("InGameMenu");
	}

	/**
	 * Placing player on a cursor position.
	 */
	private static void movePlayer() {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.movePlayer]");

		// Checking, if we need to shift map
		checkForShift();
		if (removeEntity(false)) return;

		// Changing hold position
		Data.LevelEditor.holdPosition = new Point(Data.Player.position);

		if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.movePlayer]");
	}

	/**
	 * Adding entity or interactive thing to a map.
	 *
	 * @param entityName which we want to place at cursor position
	 */
	private static void addEntity(String entityName) {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.addEntity]");

		checkForShift();
		if (removeEntity(false)) return;

		// Getting coordinates
		Point position = new Point(Data.Player.position);

		// Checking, which type of entity we want to add
		Interactive entity = new Interactive();
		entity.position = position;
		switch (entityName) {
			case "zombie": {
				entity.entityType = "zombie";
				Zombie zombie = new Zombie(position);
				listeners.addRefreshListener(zombie);
				break;
			}

			case "hp": {
				entity.entityType = "hp";
				HP hp = new HP(position);
				listeners.addRefreshListener(hp);
				break;
			}

			default: {
				if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.addEntity]");
				return;
			}
		}
		Data.Map.interactive.add(entity);

		if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.addEntity]");
	}

	/**
	 * Adding sign to a map
	 */
	private static void addSign() {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.addSign]");

		checkForShift();
		if (removeEntity(false)) return;

		graphics.showTextInput();

		if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.addSign]");
	}

	/**
	 * Changing tile in a map.
	 *
	 * @param tile which we want to place/override in a map
	 */
	private static void changeTile(String tile) {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.changeTile]");

		if (removeEntity(true)) return;

		// Checking if we need to shift the map and getting the row to change
		ArrayList<String> rowArray = checkForShift();
		Point position = new Point(Data.Player.position);

		// Ensure the row has enough length
		while (rowArray.size() <= position.x) {
			rowArray.add("");
		}

		// Replace the tile at the current cursor position
		rowArray.set(position.x, tile);

		// Update the row in the map
		if (position.y < Data.map.size()) {
			Data.map.set(position.y, rowArray);
		} else {
			// Add new rows if necessary
			while (Data.map.size() <= position.y) {
				Data.map.add(new ArrayList<>());
			}
			Data.map.set(position.y, rowArray);
		}

		graphics.refreshScreen();

		if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.changeTile]");
	}

	/**
	 * Method for removing entities that are under cursor when creating new entity on that space
	 *
	 * @param skipGround if is true, this method will skip checking for ground and player position
	 */
	private static boolean removeEntity(boolean skipGround) {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.removeEntity]");

		// Getting coordinates
		Point position = new Point(Data.Player.position);

		// Checking, if we could place it on the ground
		ImageIcon tile = graphics.getTile(position);

		if (!skipGround) {
			// Ground
			if (collisions.checkForCollision(tile) == collisions.immovable) return true;
			// Player
			if (Data.LevelEditor.holdPosition.equals(position)) return true;
		}

		// Checking, if it collides with an interactive thing
		// If yes, then remove it
		ArrayList<Interactive> toRemove = new ArrayList<>();
		for (int i = 0; i < Data.Map.interactive.size(); i++) {
			Point interPosition = Data.Map.interactive.get(i).position;
			if (interPosition.equals(position)) toRemove.add(Data.Map.interactive.get(i));
		}
		for (Interactive inter : toRemove) {
			Data.Map.interactive.remove(inter);
		}

		// Removing old listener
		listeners.removeRefreshListener(Data.Player.position);

		return false;
	}

	/**
	 * Checking, if we get error if we want to interact with nonexistent space in a map.
	 *
	 * @return JSONArray of row in which the cursor is
	 */
	private static ArrayList<String> checkForShift() {
		if (Debug.map.LevelEditor) System.out.println(">>> [LevelEditor.checkForShift]");

		// Checking if the cursor is in the negative of the map
		Point shift = new Point();
		// Y
		if (Data.Player.position.y < 0) {
			shift.y = Data.Player.position.y;
		}
		// X
		if (Data.Player.position.x < 0) {
			shift.x = Data.Player.position.x;
		}
		// Shifting, if we need to
		if (shift.y != 0 || shift.x != 0) mapUtils.shiftMap(shift);

		// Retrieve the row array at index y
		ArrayList<String> rowArray;
		// Trying to get row, which cursor is in
		try {
			rowArray = Data.map.get(Data.Player.position.y);
		} catch (IndexOutOfBoundsException e) {
			// In case of failure, just create a new one
			rowArray = new ArrayList<>();
		}

		if (Debug.map.LevelEditor) System.out.println("<<< [LevelEditor.checkForShift]");
		return rowArray;
	}

	/**
	 * Saving everything
	 */
	private static void save() {
		if (Debug.map.LevelEditor) System.out.println("--- [LevelEditor.save]");

		Data.saveSettings();
		Data.saveMap();

		Text text = new Text();
		text.setPosition(new Point((Data.Player.radius - 1) * Data.imageScale * Data.imageSize, 0));
		text.setText("The map was saved.");
		text.setCentered(true);

		graphics.drawText(text);
	}

	@Override
	public Point getPosition() {
		if (Debug.map.LevelEditor) System.out.println("--- [LevelEditor.getPosition]");
		return null;
	}

	@Override
	public void onRefresh() {
		if (Debug.map.LevelEditor) System.out.println("--- [LevelEditor.onRefresh]");
		// Drawing edit cursor
		graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);

		int x = Data.Player.position.x - Data.Player.radius;
		int y = Data.Player.position.y - Data.Player.radius;
		int startPoint = Data.imageSize * Data.imageScale;

		// Void
		Text text = new Text();
		text.setBackgroundColor(null);
		text.setForegroundColor(Color.BLACK);

		// Drawing tool tip menu items
		List<Map.Entry<String, ImageIcon>> entries = new ArrayList<>(toolTable.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<String, ImageIcon> entry = entries.get(i);

			// Text
			text.setText(entry.getKey());
			if (i%2 == 0) {
				text.setPosition(new Point(startPoint*i, startPoint));
			} else {
				text.setPosition(new Point(startPoint*i, startPoint + 16));
			}
			graphics.drawText(text);

			// Icon
			graphics.drawTile(new Point(x+i, y), entry.getValue(), graphics.ARROW_LAYER);
		}

		// Background
		Text box = new Text();
		box.setCentered(true);
		box.setBackgroundColor(Color.GRAY);
		box.setBorder(new LineBorder(Color.BLACK));
		box.setText("<br>".repeat(Data.imageScale + 1));
		graphics.drawText(box);
	}
}
