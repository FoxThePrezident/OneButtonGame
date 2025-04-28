package com.common.entities.player;

import com.common.Data;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.PlayerActionData;
import com.common.DataClasses.PlayerActionItem;
import com.common.DataClasses.Position;
import com.common.graphics.Icons;
import com.common.map.Collisions;
import com.common.menu.Menu;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.common.Data.*;
import static com.common.Debug.Flags.Entities.Player.PLAYER_ACTIONS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.ACTIONS_LAYER;

/**
 * Managing player actions, like inventory, movement ...
 */
public class PlayerActions {
	public static int actionIndex = 0;
	private static Position nextPosition;
	private static ArrayList<PlayerActionData> actionSets;
	private static ArrayList<PlayerActionItem> currentActionSet;
	private static PlayerActionItem currentAction;
	private static PlayerActionItem changeSet;

	public static void init() {
		debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.init]");

		try {
			String actionsRaw = fileHandle.loadText("player_actions.json", false);
			actionSets = gson.fromJson(actionsRaw, new TypeToken<ArrayList<PlayerActionData>>() {
			}.getType());
			assert actionSets != null;
			currentActionSet = actionSets.get(0).items;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (int i = 0; i < currentActionSet.size(); i++) {
			if (Objects.equals(currentActionSet.get(i).action, "changeSet")) {
				changeSet = currentActionSet.get(i);
				currentActionSet.remove(currentActionSet.get(i));
			}
		}
	}

	/**
	 * Changing current shortAction
	 */
	static void nextAction() {
		debug(PLAYER_ACTIONS, INFORMATION, "--- [PlayerActions.nextAction]");

		actionIndex += 1;
		if (actionIndex >= currentActionSet.size()) actionIndex = 0;
		currentAction = currentActionSet.get(actionIndex);
		graphics.clearLayer(ACTIONS_LAYER);
		drawAction();
		Player.resetLatMoveTime();
	}

	/**
	 * Drawing current shortAction
	 */
	static void drawAction() {
		debug(PLAYER_ACTIONS, INFORMATION, ">>> [PlayerActions.drawAction]");

		if (currentAction == null) return;
		String iconName = currentAction.icon;

		if (iconName.equals("out")) {
			drawOutwardArrows();
			return;
		}

		nextPosition = Player.getNextPosition(null);
		ImageWrapper icon = getIcon(iconName);

		if (icon == null) {
			Player.getInventoryItem(actionIndex).draw(nextPosition);
		} else {
			graphics.drawTile(nextPosition, icon, ACTIONS_LAYER);
		}

		debug(PLAYER_ACTIONS, INFORMATION, "<<< [PlayerActions.drawAction]");
	}

	static void shortAction() {
		debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.shortAction]");

		if (!Data.running) {
			graphics.refreshScreen();
			return;
		}

		Player.resetLatMoveTime();

		switch (currentAction.action) {
			case "move":
				move();
				break;
			case "inventory":
				inventory();
				break;
			case "changeSet":
				changeSet();
				break;
			case "menu":
				menu();
				break;
		}

		debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.shortAction]");
	}

	static void longAction() {
		debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.longAction]");

		if (!Data.running) {
			graphics.refreshScreen();
			return;
		}

		Player.resetLatMoveTime();

		currentAction = changeSet;
		changeSet();

		debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.longAction]");

	}

	private static void move() {
		debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.move]");

		// Getting next position
		nextPosition = Player.getNextPosition(currentAction.vector);
		// Checking if player could move
		ImageWrapper nextTile = graphics.getTile(nextPosition);
		int couldMove = Collisions.checkForCollision(nextTile);
		if (couldMove == Collisions.immovable) return;

		// Updating player position and refreshing screen
		Data.Player.position = nextPosition;
		graphics.refreshScreen();

		debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.move]");
	}

	private static void inventory() {
		debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.inventory]");

		Player.getInventoryItem(actionIndex).applyEffects();
		Player.setInventoryItem(actionIndex, new Item(Icons.LevelEditor.cursor, true));
	}

	private static void changeSet() {
		debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.changeSet]");

		actionIndex = -1;
		for (PlayerActionData actionObject : actionSets) {
			if (Objects.equals(actionObject.name, currentAction.setName)) {
				currentActionSet = actionObject.items;
			}
		}
	}

	private static void menu() {
		debug(PLAYER_ACTIONS, CORE, "--- [PlayerActions.menu]");

		Data.running = false;
		Menu.setMenu(currentAction.menu);
	}

	/**
	 * Drawing arrows pointing outwards from player
	 */
	private static void drawOutwardArrows() {
		debug(PLAYER_ACTIONS, CORE, ">>> [PlayerActions.drawOutwardArrows]");

		Position[] directions = {new Position(0, -1), new Position(1, 0), new Position(0, 1), new Position(-1, 0)}; // Up, Right, Down, Left
		String[] arrowIcons = {"Player.up", "Player.right", "Player.down", "Player.left"}; // Corresponding arrow icons

		// Draw arrows around the player in all directions
		for (int i = 0; i < directions.length; i++) {
			Position direction = directions[i];
			Position arrowPosition = new Position(
					Data.Player.position.x + direction.x,
					Data.Player.position.y + direction.y
			);
			ImageWrapper arrowIcon = getIcon(arrowIcons[i]);
			graphics.drawTile(arrowPosition, arrowIcon, ACTIONS_LAYER);
		}
		graphics.revalidate();

		debug(PLAYER_ACTIONS, CORE, "<<< [PlayerActions.drawOutwardArrows]");
	}

	/**
	 * Get icon for current shortAction
	 *
	 * @param icon name
	 * @return ImageIcon of current shortAction
	 */
	private static ImageWrapper getIcon(String icon) {
		debug(PLAYER_ACTIONS, INFORMATION, "--- [PlayerActions.getIcon]");

		if (Objects.equals(icon, "null")) {
			return null;
		}

		String[] iconPath = icon.split("\\.");

		try {
			Class<?> iconClass = Arrays.stream(Icons.class.getDeclaredClasses())
					.filter(c -> c.getSimpleName().equals(iconPath[0]))
					.findFirst()
					.orElse(null);

			// Get the field by name from the static Icons.Player class
			ImageWrapper value;
			if (iconClass != null) {
				Field field = iconClass.getField(iconPath[1]);
				value = (ImageWrapper) field.get(null);
			} else {
				value = Icons.Environment.blank;
			}
			// Get the value of the field
			return value;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
