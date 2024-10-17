package com.FoxThePrezident.entities.player;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.Debug;
import com.FoxThePrezident.Menu.Menu;
import com.FoxThePrezident.entities.Item;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.graphics.Icons;
import com.FoxThePrezident.map.Collisions;
import com.FoxThePrezident.utils.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Managing player actions, like inventory, movement ...
 */
public class PlayerActions {
	public static int actionIndex = 0;
	private static int[] nextPosition;

	private static JSONArray actionSets;
	private static JSONArray currentActionSet;
	private static JSONObject currentAction;

	private static final Collisions collisions = new Collisions();
	private static final Graphics graphics = new Graphics();

	public PlayerActions() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.constructor");

		try {
			FileHandle fileHandle = new FileHandle();
			String actionsRaw = fileHandle.loadText("player_actions.json", false);
			actionSets = new JSONArray(actionsRaw);
			currentActionSet = actionSets.getJSONObject(0).getJSONArray("items");
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
		if (actionIndex >= currentActionSet.length()) actionIndex = 0;
		currentAction = currentActionSet.getJSONObject(actionIndex);
		graphics.clearLayer(graphics.ARROW_LAYER);
		drawAction();
		Player.resetLatMoveTime();
	}

	/**
	 * Drawing current action
	 */
	public void drawAction() {
		if (Debug.entities.player.PlayerAction) System.out.println(">>> PlayerActions.drawAction");

		String iconName = currentAction.getString("icon");

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

		switch (currentAction.getString("action")) {
			case "move" -> move();
			case "inventory" -> inventory();
			case "changeSet" -> changeSet();
			case "menu" -> menu();
		}

		if (Debug.entities.player.PlayerAction) System.out.println("<<< PlayerActions.action");
	}

	private void move() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.move");

		// Getting next position
		nextPosition = Player.getNextPosition(currentAction.getJSONArray("vector"));
		// Checking if player could move
		ImageIcon nextTile = graphics.getTile(nextPosition);
		int couldMove = collisions.checkForCollision(nextTile);
		if (couldMove == collisions.immovable) return;

		// Updating player position and refreshing screen
		Data.Player.position = nextPosition;
		graphics.refreshScreen();
	}

	private void inventory() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.inventory");

		Player.getInventoryItem(actionIndex).applyEffects();
		Player.setInventoryItem(actionIndex, new Item(Icons.LevelEditor.cursor, true));
	}

	private void changeSet() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.changeSet");

		for (int i = 0; i < actionSets.length(); i++) {
			JSONObject actionObject = actionSets.getJSONObject(i);
			if (Objects.equals(actionObject.getString("name"), currentAction.getString("setName"))) {
				currentActionSet = actionObject.getJSONArray("items");
			}
		}
	}

	private void menu() {
		if (Debug.entities.player.PlayerAction) System.out.println("--- PlayerActions.menu");

		Menu menu = new Menu();
		menu.setMenu(currentAction.getString("menu"));
	}

	/**
	 * Drawing arrows pointing outwards from player
	 */
	private void drawOutwardArrows() {
		if (Debug.entities.player.Player) System.out.println(">>> [PlayerActions.drawOutwardArrows]");

		int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // Up, Right, Down, Left
		String[] arrowIcons = {"up", "right", "down", "left"}; // Corresponding arrow icons

		// Draw arrows around the player in all directions
		for (int i = 0; i < directions.length; i++) {
			int[] direction = directions[i];
			int[] arrowPosition = {
					  Data.Player.position[0] + direction[0],
					  Data.Player.position[1] + direction[1]
			};
			ImageIcon arrowIcon = getIcon(arrowIcons[i]);
			graphics.drawTile(arrowPosition, arrowIcon, graphics.ARROW_LAYER);
		}
		graphics.revalidate();

		if (Debug.entities.player.Player) System.out.println("<<< [PlayerActions.drawOutwardArrows]");
	}

	/**
	 * Get icon for current action
	 *
	 * @param icon name
	 * @return ImageIcon of current action
	 */
	private ImageIcon getIcon(String icon) {
		if (Debug.entities.player.Player) System.out.println("--- [PlayerActions.getIcon]");

		if (Objects.equals(icon, "null")) {
			return null;
		}

		try {
			// Get the field by name from the static Icons.Player class
			Field field = Icons.Player.class.getField(icon);
			// Get the value of the field
			Object value = field.get(null);  // null because it's static
			return (ImageIcon) value;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public int getActionIndex() {
		return actionIndex;
	}
}
