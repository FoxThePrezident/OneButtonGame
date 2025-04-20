package com.common.map;

import com.common.Data;
import com.common.DataClasses.*;
import com.common.entities.enemies.Skeleton;
import com.common.entities.enemies.Zombie;
import com.common.entities.potions.HP;
import com.common.graphics.Icons;
import com.common.listeners.Listeners;
import com.common.listeners.RefreshListener;
import com.common.menu.Menu;
import com.common.utils.MapUtils;

import java.util.*;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Map.LEVEL_EDITOR;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.ACTIONS_LAYER;

/**
 * Handling level editing.
 */
public class LevelEditor implements RefreshListener {
	/**
	 * Top bar for easier keybindings
	 */
	private final LinkedHashMap<String, ImageWrapper> toolTable = new LinkedHashMap<String, ImageWrapper>() {{
		put("Void: 0", Icons.Environment.blank);
		put("Wall: 1", Icons.Environment.wall);
		put("Floor: 2", Icons.Environment.floor);
		put("Player: 3", Icons.Player.player);
		put("Zombie: 4", Icons.Enemies.zombie);
		put("Skeleton: 5", Icons.Enemies.skeleton);
		put("HP: 6", Icons.Interactive.hp_potion);
		put("Sign: 7", Icons.Interactive.sign);
	}};

	/**
	 * Initializing viewport radius and other variables.
	 */
	public static void init() {
		debug(LEVEL_EDITOR, CORE, "--- [LevelEditor.constructor]");

		Data.Player.radius = 10;
	}

	/**
	 * Handling movement of a camera.
	 *
	 * @param keyChar of an input
	 */
	public static void move(char keyChar) {
		debug(LEVEL_EDITOR, CORE, ">>> [LevelEditor.move]");

		// Enter key, for not refreshing whole game to be able to see that game was saved
		if (keyChar == '\n') {
			save();
			debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.move]");
			return;
		}

		// Checking, which key was pressed
		// Do not forget to update this in README file
		switch (Character.toLowerCase(keyChar)) {
			// Movement
			case 'w':
				Data.Player.position.y -= 1;
				break;
			case 'd':
				Data.Player.position.x += 1;
				break;
			case 's':
				Data.Player.position.y += 1;
				break;
			case 'a':
				Data.Player.position.x -= 1;
				break;

			// Ground
			// Void
			case '0':
				changeTile("");
				break;
			// Wall
			case '1':
				changeTile("W");
				break;
			// Floor
			case '2':
				changeTile(" ");
				break;

			// Interactive/player
			case '3':
				movePlayer();
				break;
			case '4':
				addEntity("zombie");
				break;
			case '5':
				addEntity("skeleton");
				break;
			case '6':
				addEntity("hp");
				break;
			case '7':
				addSign();
				break;

			// Exit
			case 'q':
				showMenu();
				break;

			default:
				System.out.println(keyChar);
				break;
		}
		// Refreshing screen after each input
		graphics.refreshScreen();

		debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.move]");
	}

	private static void showMenu() {
		debug(LEVEL_EDITOR, CORE, "--- LevelEditor.menu");

		Data.running = false;
		Menu.setMenu("InGameMenu");
	}

	/**
	 * Placing player on a cursor position.
	 */
	private static void movePlayer() {
		debug(LEVEL_EDITOR, CORE, ">>> [LevelEditor.movePlayer]");

		// Checking, if we need to shift map
		checkForShift();
		if (removeEntity(false)) return;

		// Changing hold position
		Data.LevelEditor.holdPosition = new Position(Data.Player.position);

		debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.movePlayer]");
	}

	/**
	 * Adding entity or interactive thing to a map.
	 *
	 * @param entityName which we want to place at cursor position
	 */
	private static void addEntity(String entityName) {
		debug(LEVEL_EDITOR, CORE, ">>> [LevelEditor.addEntity]");

		checkForShift();
		if (removeEntity(false)) return;

		// Getting coordinates
		Position position = new Position(Data.Player.position);

		// Checking, which type of entity we want to add
		Interactive entity = new Interactive();
		entity.position = position;
		switch (entityName) {
			case "zombie":
				entity.entityType = "zombie";
				Zombie zombie = new Zombie(position);
				Listeners.addRefreshListener(zombie);
				break;

			case "skeleton":
				entity.entityType = "skeleton";
				Skeleton skeleton = new Skeleton(position);
				Listeners.addRefreshListener(skeleton);
				break;

			case "hp":
				entity.entityType = "hp";
				HP hp = new HP(position);
				Listeners.addRefreshListener(hp);
				break;

			default:
				debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.addEntity]");
				return;
		}
		Data.Map.interactive.add(entity);

		debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.addEntity]");
	}

	/**
	 * Adding sign to a map
	 */
	private static void addSign() {
		debug(LEVEL_EDITOR, CORE, ">>> [LevelEditor.addSign]");

		checkForShift();
		if (removeEntity(false)) return;

		graphics.showTextInput();

		debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.addSign]");
	}

	/**
	 * Changing tile in a map.
	 *
	 * @param tile which we want to place/override in a map
	 */
	private static void changeTile(String tile) {
		debug(LEVEL_EDITOR, CORE, ">>> [LevelEditor.changeTile]");

		if (removeEntity(true)) return;

		// Checking if we need to shift the map and getting the row to change
		ArrayList<String> rowArray = checkForShift();
		Position position = new Position(Data.Player.position);

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

		debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.changeTile]");
	}

	/**
	 * Method for removing entities that are under cursor when creating new entity on that space
	 *
	 * @param skipGround if is true, this method will skip checking for ground and player position
	 */
	private static boolean removeEntity(boolean skipGround) {
		debug(LEVEL_EDITOR, CORE, "--- [LevelEditor.removeEntity]");

		// Getting coordinates
		Position position = new Position(Data.Player.position);

		// Checking, if we could place it on the ground
		ImageWrapper tile = graphics.getTile(position);

		if (!skipGround) {
			// Ground
			if (Collisions.checkForCollision(tile) == Collisions.immovable) return true;
			// Player
			if (Data.LevelEditor.holdPosition.equals(position)) return true;
		}

		// Checking, if it collides with an interactive thing
		// If yes, then remove it
		ArrayList<Interactive> toRemove = new ArrayList<>();
		for (int i = 0; i < Data.Map.interactive.size(); i++) {
			Position interPosition = Data.Map.interactive.get(i).position;
			if (interPosition.equals(position)) toRemove.add(Data.Map.interactive.get(i));
		}
		for (Interactive inter : toRemove) {
			Data.Map.interactive.remove(inter);
		}

		// Removing old listener
		Listeners.removeRefreshListener(Data.Player.position);

		return false;
	}

	/**
	 * Checking, if we get error if we want to interact with nonexistent space in a map.
	 *
	 * @return JSONArray of row in which the cursor is
	 */
	private static ArrayList<String> checkForShift() {
		debug(LEVEL_EDITOR, CORE, ">>> [LevelEditor.checkForShift]");

		// Checking if the cursor is in the negative of the map
		Position shift = new Position();
		// Y
		if (Data.Player.position.y < 0) {
			shift.y = Data.Player.position.y;
		}
		// X
		if (Data.Player.position.x < 0) {
			shift.x = Data.Player.position.x;
		}
		// Shifting, if we need to
		if (shift.y != 0 || shift.x != 0) MapUtils.shiftMap(shift);

		// Retrieve the row array at index y
		ArrayList<String> rowArray;
		// Trying to get row, which cursor is in
		try {
			rowArray = Data.map.get(Data.Player.position.y);
		} catch (IndexOutOfBoundsException e) {
			// In case of failure, just create a new one
			rowArray = new ArrayList<>();
		}

		debug(LEVEL_EDITOR, CORE, "<<< [LevelEditor.checkForShift]");
		return rowArray;
	}

	/**
	 * Saving everything
	 */
	private static void save() {
		debug(LEVEL_EDITOR, CORE, "--- [LevelEditor.save]");

		Data.saveSettings();
		Data.saveMap();

		TextData text = new TextData();
		text.position = new Position((Data.Player.radius - 1) * Data.IMAGE_SCALE * Data.IMAGE_SIZE, 0);
		text.text = "The map was saved.";
		text.centered = true;

		graphics.drawText(text);
	}

	@Override
	public Position getPosition() {
		debug(LEVEL_EDITOR, CORE, "--- [LevelEditor.getPosition]");
		return null;
	}

	@Override
	public void onRefresh() {
		debug(LEVEL_EDITOR, CORE, "--- [LevelEditor.onRefresh]");
		// Drawing edit cursor
		graphics.drawTile(Data.Player.position, Icons.LevelEditor.cursor, ACTIONS_LAYER);

		int x = Data.Player.position.x - Data.Player.radius;
		int y = Data.Player.position.y - Data.Player.radius;
		int startPoint = Data.IMAGE_SIZE * Data.IMAGE_SCALE;

		// Void
		TextData text = new TextData();
		text.backgroundColor = null;
		text.foregroundColor = new Colour(0, 0, 0);

		// Drawing tool tip menu items
		List<Map.Entry<String, ImageWrapper>> entries = new ArrayList<>(toolTable.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<String, ImageWrapper> entry = entries.get(i);

			// Text
			text.text = entry.getKey();
			if (i % 2 == 0) {
				text.position = new Position(startPoint * i, startPoint);
			} else {
				text.position = new Position(startPoint * i, startPoint + 16);
			}
			graphics.drawText(text);

			// Icon
			graphics.drawTile(new Position(x + i, y), entry.getValue(), ACTIONS_LAYER);
		}

		// Background
		TextData box = new TextData();
		box.centered = true;
		box.backgroundColor = new Colour(100, 100, 100);
		box.text = String.join("", Collections.nCopies(Data.IMAGE_SCALE + 1, "<br>"));
		graphics.drawText(box);
	}

	@Override
	public void getEntityDamage(int damage) {
	}
}
