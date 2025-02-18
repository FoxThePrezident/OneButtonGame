package com.OneOfManySimons.entities.player;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.DataClasses.PlayerActionData;
import com.OneOfManySimons.DataClasses.PlayerActionItem;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.Item;
import com.OneOfManySimons.graphics.Icons;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.OneOfManySimons.Data.libaries.*;

/**
 * Managing player actions, like inventory, movement ...
 */
public class PlayerActions {
	public static int actionIndex = 0;
	private static Point nextPosition;
	private static ArrayList<PlayerActionData> actionSets;
	private static ArrayList<PlayerActionItem> currentActionSet;
	private static PlayerActionItem currentAction;

	public PlayerActions() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.constructor");

		try {
			String actionsRaw = fileHandle.loadText("player_actions.json", false);
			actionSets = gson.fromJson(actionsRaw, new TypeToken<ArrayList<PlayerActionData>>(){}.getType());
			currentActionSet = actionSets.get(0).items;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Changing current action
	 */
	public void nextAction() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.nextAction");

		actionIndex += 1;
		if (actionIndex >= currentActionSet.size()) actionIndex = 0;
		currentAction = currentActionSet.get(actionIndex);
		graphics.clearLayer(graphics.ARROW_LAYER);
		drawAction();
		Player.resetLatMoveTime();
	}

	/**
	 * Drawing current action
	 */
	public void drawAction() {
		if (Debug.entities.player.PlayerAction) System.out.println(">>> PlayerActions.drawAction");

		String iconName = currentAction.icon;

		if (iconName.equals("out")) {
			drawOutwardArrows();
			return;
		}

		nextPosition = Player.getNextPosition();
		ImageIcon icon = getIcon(iconName);

		if (icon == null) {
			Player.getInventoryItem(actionIndex).draw(nextPosition);
		} else {
			graphics.drawTile(nextPosition, icon, graphics.ARROW_LAYER);
		}

		if (Debug.entities.player.PlayerAction) System.out.println("<<< PlayerActions.drawAction");
	}

	public void action() {
		if (Debug.entities.player.PlayerAction) System.out.println(">>> PlayerActions.action");

		if (!Data.running) {
			graphics.refreshScreen();
			return;
		}

		Player.resetLatMoveTime();

		switch (currentAction.action) {
			case "move" -> move();
			case "inventory" -> inventory();
			case "changeSet" -> changeSet();
			case "menu" -> menu();
		}

		if (Debug.entities.player.PlayerAction) System.out.println("<<< PlayerActions.action");
	}

	private void move() {
		if (Debug.entities.player.PlayerAction) System.out.println(">>> PlayerActions.move");

		// Getting next position
		nextPosition = Player.getNextPosition(currentAction.vector);
		// Checking if player could move
		ImageIcon nextTile = graphics.getTile(nextPosition);
		int couldMove = collisions.checkForCollision(nextTile);
		if (couldMove == collisions.immovable) return;

		// Updating player position and refreshing screen
		Data.Player.position = nextPosition;
		graphics.refreshScreen();

		if (Debug.entities.player.PlayerAction) System.out.println("<<< PlayerActions.move");
	}

	private void inventory() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.inventory");

		Player.getInventoryItem(actionIndex).applyEffects();
		Player.setInventoryItem(actionIndex, new Item(Icons.LevelEditor.cursor, true));
	}

	private void changeSet() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.changeSet");

		actionIndex = -1;
		for (PlayerActionData actionObject : actionSets) {
			if (Objects.equals(actionObject.name, currentAction.setName)) {
				currentActionSet = actionObject.items;
			}
		}
	}

	private void menu() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.menu");

		Data.running = false;
		menu.setMenu(currentAction.menu);
	}

	/**
	 * Drawing arrows pointing outwards from player
	 */
	private void drawOutwardArrows() {
		if (Debug.entities.player.PlayerAction) System.out.println(">>> [PlayerActions.drawOutwardArrows]");

		Point[] directions = {new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)}; // Up, Right, Down, Left
		String[] arrowIcons = {"Player.up", "Player.right", "Player.down", "Player.left"}; // Corresponding arrow icons

		// Draw arrows around the player in all directions
		for (int i = 0; i < directions.length; i++) {
			Point direction = directions[i];
			Point arrowPosition = new Point(
					Data.Player.position.x + direction.x,
					Data.Player.position.y + direction.y
			);
			ImageIcon arrowIcon = getIcon(arrowIcons[i]);
			graphics.drawTile(arrowPosition, arrowIcon, graphics.ARROW_LAYER);
		}
		graphics.revalidate();

		if (Debug.entities.player.PlayerAction) System.out.println("<<< [PlayerActions.drawOutwardArrows]");
	}

	/**
	 * Get icon for current action
	 *
	 * @param icon name
	 * @return ImageIcon of current action
	 */
	private ImageIcon getIcon(String icon) {
		if (Debug.entities.player.PlayerAction) System.out.println("--- [PlayerActions.getIcon]");

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
			ImageIcon value;
			if (iconClass != null) {
				Field field = iconClass.getField(iconPath[1]);
				value = (ImageIcon) field.get(null);
			} else {
				value = Icons.Environment.blank;
			}
			// Get the value of the field
			return value;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public int getActionIndex() {
		return actionIndex;
	}
}
