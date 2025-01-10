package com.FoxThePrezident.entities.player;

import com.FoxThePrezident.Debug;
import com.FoxThePrezident.entities.Item;
import com.FoxThePrezident.Data;
import com.FoxThePrezident.graphics.Icons;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.listeners.RefreshListener;
import org.json.JSONArray;

/**
 * Player class.<br>
 * Controlling movement and actions from and to player.
 */
public class Player implements Runnable, RefreshListener {
	public static int health = 15;

	private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};

	private static final Item[] inventory = new Item[]{
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true),
			new Item(Icons.LevelEditor.cursor, true)
	};

	private static long lastMoveTime = System.currentTimeMillis();

	private static final Graphics graphics = new Graphics();
	private static final PlayerActions playerActions = new PlayerActions();

	/**
	 * Main thread for caning directions and arrows
	 */
	public void run() {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.run]");

		if (!Data.running) {
			return;
		}
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);

		if (Debug.entities.player.Player) System.out.println("--- [Player.run] Starting main loop for actions");
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

			playerActions.nextAction();
		}

		if (Debug.entities.player.Player) System.out.println("<<< [Player.run]");
	}

	/**
	 * Function, for dealing damage for player.
	 *
	 * @param damage which is dealt
	 */
	public static void getDamage(int damage) {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.getDamage]");

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

		if (Debug.entities.player.Player) System.out.println("<<< [Player.getDamage]");
	}

	/**
	 * Function, for adding health to a player.
	 *
	 * @param heal which is added to a player
	 */
	public static void getHeal(int heal) {
		if (Debug.entities.player.Player) System.out.println("--- [Player.getHeal]");

		if (heal <= 0) return;
		health += heal;
		graphics.clearLayer(graphics.TEXT_LAYER);
		graphics.drawText(new int[]{8, 8}, health + " HP", 25);
	}

	/**
	 * Adding item to a player inventory
	 *
	 * @param item to add
	 */
	public static void addItem(Item item) {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.addItem]");
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i].getIsNull()) {
				inventory[i] = item;
				return;
			}
		}
	}

	/**
	 * Method for handling movement of the player
	 */
	public static void action() {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.action]");

		playerActions.action();

		if (Debug.entities.player.Player) System.out.println("<<< [Player.action]");
	}

	/**
	 * Getting the next position based on a direction
	 *
	 * @param direction custom direction of next position
	 * @return int[] of next position
	 */
	public static int[] getNextPosition(JSONArray direction) {
		if (Debug.entities.player.Player) System.out.println("--- [Player.getNextPosition]");

		int y = Data.Player.position[0];
		int x = Data.Player.position[1];

		if (direction == null) {
			y += DIRECTIONS[playerActions.getActionIndex()][0];
			x += DIRECTIONS[playerActions.getActionIndex()][1];
		} else {
			y += (int) Math.signum(direction.getInt(0));
			x += (int) Math.signum(direction.getInt(1));
		}
		return new int[]{y, x};
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.player.Player) System.out.println("--- [Enemy.getPosition]");
		if (Data.LevelEditor.levelEdit) {
			return Data.LevelEditor.holdPosition;
		}
		return Data.Player.position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.player.Player) System.out.println(">>> [Player.onRefresh]");

		if (Data.LevelEditor.levelEdit) {
			graphics.drawTile(Data.LevelEditor.holdPosition, Icons.Player.player, graphics.PLAYER_LAYER);
		} else {
			graphics.drawTile(Data.Player.position, Icons.Player.player, graphics.PLAYER_LAYER);
		}

		// Show outward arrows if the action icon is "menu"
		if (Data.running) {
			playerActions.drawAction();

			graphics.drawText(new int[]{8, 8}, health + " HP", 25);
		}

		if (Debug.entities.player.Player) System.out.println("<<< [Player.onRefresh]");
	}

	public static int[] getNextPosition() {
		return getNextPosition(null);
	}

	public static void setInventoryItem(int slot, Item item) {
		inventory[slot] = item;
	}

	public static Item getInventoryItem(int slot) {
		return inventory[slot];
	}

	public static void resetLatMoveTime() {
		lastMoveTime = System.currentTimeMillis();
	}
}
