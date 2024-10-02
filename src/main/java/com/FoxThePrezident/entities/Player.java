package com.FoxThePrezident.entities;

import com.FoxThePrezident.Debug;
import com.FoxThePrezident.map.Collisions;
import com.FoxThePrezident.Data;
import com.FoxThePrezident.map.Icons;
import com.FoxThePrezident.map.Graphics;
import com.FoxThePrezident.listeners.RefreshListener;
import com.FoxThePrezident.utils.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Player class.<br>
 * Controlling movement and actions from and to player.
 */
public class Player implements Runnable, RefreshListener {
	public static int health = 15;

	private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};
	public static int actionIndex = 0;
	private static int[] nextPosition;
	private static JSONArray actionSets;
	private static JSONArray currentActionSet;
	private static JSONObject currentAction;

	private static long lastMoveTime = System.currentTimeMillis();

	private static final Collisions collisions = new Collisions();
	private static final Graphics graphics = new Graphics();

	public Player() {
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
	 * Main thread for caning directions and arrows
	 */
	public void run() {
		if (Debug.entities.Player) System.out.println(">>> [Player.run]");

		if (!Data.running) {
			return;
		}
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		nextPosition = getNextPosition();

		if (Debug.entities.Player) System.out.println("--- [Player.run] Starting main loop for actions");
		while (Data.running) {
			long elapsedTime = System.currentTimeMillis() - lastMoveTime;
			long timeToWait = Data.Player.controlDelay - elapsedTime;
			if (timeToWait > 0) {
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				continue;
			}

			actionIndex += 1;
			if (actionIndex >= currentActionSet.length()) actionIndex = 0;
			currentAction = currentActionSet.getJSONObject(actionIndex);

			graphics.clearLayer(graphics.ARROW_LAYER);

			String iconName = currentAction.getString("icon");
			if (Objects.equals(iconName, "out")) {
				drawOutwardArrows();
			} else {
				nextPosition = getNextPosition();
				ImageIcon icon = getIcon(currentAction.getString("icon"));
				graphics.drawTile(nextPosition, icon, graphics.ARROW_LAYER);
			}

			lastMoveTime = System.currentTimeMillis();
		}

		if (Debug.entities.Player) System.out.println("<<< [Player.run]");
	}

	private void drawOutwardArrows() {
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
	}



	/**
	 * Function, for dealing damage for player.
	 *
	 * @param damage which is dealt
	 */
	public static void getDamage(int damage) {
		if (Debug.entities.Player) System.out.println(">>> [Player.getDamage]");

		if (damage <= 0) return;

		health -= damage;
		graphics.clearLayer(graphics.TEXT_LAYER);

		// Checking, if player is still alive
		if (health <= 0) {
			Data.running = false;
			graphics.clearLayer(graphics.ARROW_LAYER);
		} else {
			graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		}

		if (Debug.entities.Player) System.out.println("<<< [Player.getDamage]");
	}

	/**
	 * Function, for adding health to a player.
	 *
	 * @param heal which is added to a player
	 */
	public static void getHeal(int heal) {
		if (Debug.entities.Player) System.out.println("--- [Player.getHeal]");

		if (heal <= 0) return;
		health += heal;
		graphics.clearLayer(graphics.TEXT_LAYER);
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
	}

	/**
	 * Method for handling movement of the player
	 */
	public static void action() {
		if (Debug.entities.Player) System.out.println(">>> [Player.move]");

		if (!Data.running) {
			graphics.refreshScreen();
			return;
		}

		lastMoveTime = System.currentTimeMillis();

		switch (currentAction.getString("action")) {
			case "move": {
				// Getting next position
				nextPosition = getNextPosition();
				// Checking if player could move
				ImageIcon nextTile = graphics.getTile(nextPosition);
				int couldMove = collisions.checkForCollision(nextTile);
				if (couldMove == collisions.immovable) return;

				// Updating player position and refreshing screen
				Data.Player.position = nextPosition;
				graphics.refreshScreen();
				break;
			}
			case "changeSet": {
				for (int i = 0; i < actionSets.length(); i++) {
					JSONObject actionObject = actionSets.getJSONObject(i);
					if (Objects.equals(actionObject.getString("name"), currentAction.getString("setName"))) {
						currentActionSet = actionObject.getJSONArray("items");
					}
				}
				break;
			}
		}
	}

	/**
	 * Get icon for current action
	 *
	 * @param icon name
	 * @return ImageIcon of current action
	 */
	private ImageIcon getIcon(String icon) {
		if (Debug.entities.Player) System.out.println("--- [Player.getIcon]");

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


	/**
	 * Getting the next position based on a direction
	 *
	 * @return int[] of next position
	 */
	private static int[] getNextPosition() {
		if (Debug.entities.Player) System.out.println("--- [Player.getNextPosition]");

		int y = Data.Player.position[0] + DIRECTIONS[actionIndex][0];
		int x = Data.Player.position[1] + DIRECTIONS[actionIndex][1];
		return new int[]{y, x};
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.Player) System.out.println("--- [Enemy.getPosition]");
		if (Data.LevelEditor.levelEdit) {
			return Data.LevelEditor.holdPosition;
		}
		return Data.Player.position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.Player) System.out.println(">>> [Player.onRefresh]");

		graphics.drawTile(Data.Player.position, Icons.Player.player, graphics.PLAYER_LAYER);

		// Show outward arrows if the action icon is "menu"
		if (Data.running) {
			if (currentAction.getString("icon").equals("menu")) {
				drawOutwardArrows();
			} else {
				nextPosition = getNextPosition();
				ImageIcon icon = getIcon(currentAction.getString("icon"));
				graphics.drawTile(nextPosition, icon, graphics.ARROW_LAYER);
			}

			graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		}
	}

}
